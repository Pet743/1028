package com.ruoyi.uni.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.mapper.AlseUserMapper;
import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.alse.service.IAlseProductService;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.annotation.NoToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.converter.UserConverter;
import com.ruoyi.uni.model.DTO.request.product.BrowsingHistoryItem;
import com.ruoyi.uni.model.DTO.request.user.*;
import com.ruoyi.uni.model.DTO.request.user.browsing.BrowsingHistoryQueryDTO;
import com.ruoyi.uni.model.DTO.request.user.browsing.BrowsingHistoryRecord;
import com.ruoyi.uni.model.DTO.request.user.browsing.DeleteBrowsingHistoryDTO;
import com.ruoyi.uni.model.DTO.request.user.browsing.UserIdRequestDTO;
import com.ruoyi.uni.model.DTO.request.user.follow.BatchUnfollowRequestDTO;
import com.ruoyi.uni.model.DTO.request.user.follow.FollowUserRequestDTO;
import com.ruoyi.uni.model.DTO.request.user.follow.UserFollowItemDTO;
import com.ruoyi.uni.model.DTO.request.user.follow.UserFollowQueryDTO;
import com.ruoyi.uni.model.DTO.respone.user.BrowsingHistoryResponseDTO;
import com.ruoyi.uni.model.DTO.respone.user.UserInfoResponseDTO;
import com.ruoyi.uni.model.DTO.respone.user.UserStatisticsDTO;
import com.ruoyi.uni.util.FinanceUtils;
import com.ruoyi.uni.util.JwtUtil;
import com.ruoyi.uni.util.SecurityUtils;
import com.ruoyi.uni.util.ValidateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/user")
@Api(tags = "用户信息接口")
public class UniUserController {

    @Autowired
    private IAlseUserService alseUserService;

    @Autowired
    private IAlseProductService alseProductService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlseUserMapper alseUserMapper;

    @Autowired
    private IAlseOrderService alseOrderService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    @NoToken
    @ApiOperation("用户注册")
    public AjaxResult register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        // 验证用户名格式
        String username = registerRequestDTO.getUsername();
        if (StringUtils.isEmpty(username)) {
            return AjaxResult.error("用户名不能为空");
        }

        // 判断用户名类型并验证格式
        boolean isPhone = ValidateUtil.isPhone(username);
        boolean isEmail = ValidateUtil.isEmail(username);
        if (!isPhone && !isEmail) {
            return AjaxResult.error("请输入正确的手机号或邮箱格式");
        }

        // 查询用户名是否已存在
        AlseUser existingUser = new AlseUser();
        if (isPhone) {
            existingUser.setPhone(username);
        } else {
            existingUser.setEmail(username);
        }

        List<AlseUser> existingUsers = alseUserService.selectAlseUserList(existingUser);
        if (!existingUsers.isEmpty()) {
            return AjaxResult.error(isPhone ? "手机号已被注册" : "邮箱已被注册");
        }

        // 密码和确认密码校验
        if (!registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            return AjaxResult.error("密码和确认密码不一致");
        }

        // 创建新用户并设置相关信息
        AlseUser newUser = new AlseUser();
        // 根据类型设置对应字段
        if (isPhone) {
            newUser.setPhone(username);
            newUser.setUsername("user_" + username.substring(username.length() - 4)); // 生成默认用户名
        } else {
            newUser.setEmail(username);
            newUser.setUsername("user_" + username.substring(0, username.indexOf("@"))); // 生成默认用户名
        }

        newUser.setPassword(SecurityUtils.encryptPassword(registerRequestDTO.getPassword())); // 密码加密
        newUser.setStatus("0"); // 默认状态为正常
        newUser.setCreateBy(newUser.getUsername());
        newUser.setCreateTime(new Date());
        newUser.setUpdateBy(newUser.getUsername());
        newUser.setUpdateTime(new Date());

        // 插入新用户
        try {
            int result = alseUserService.insertAlseUser(newUser);
            if (result > 0) {
                // 注册成功后生成并返回 token
                String token = JwtUtil.createToken(newUser.getUserId(), newUser.getUsername());
                Long userId = newUser.getUserId();
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("userId", userId);

                return AjaxResult.success("注册成功", data);
            } else {
                return AjaxResult.error("注册失败");
            }
        } catch (Exception e) {
            log.error("用户注册失败：", e);
            return AjaxResult.error("注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    @NoToken
    @ApiOperation("用户登录")
    public AjaxResult login(@RequestBody LoginDTO loginDTO) {
        // 根据用户名查找用户
        AlseUser existingUser = new AlseUser();
        String username = loginDTO.getUsername();
        boolean isPhone = ValidateUtil.isPhone(username);
        boolean isEmail = ValidateUtil.isEmail(username);
        if (isPhone) {
            existingUser.setPhone(username);
        } else if (isEmail) {
            existingUser.setEmail(username);
        } else {
            return AjaxResult.error("用户名不规范");
        }
        AlseUser user = alseUserService.selectAlseUserList(existingUser).stream().findFirst().orElse(null); // 根据用户名查询
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }

        // 验证密码是否正确
        if (!SecurityUtils.matchesPassword(loginDTO.getPassword(), user.getPassword())) {
            return AjaxResult.error("密码错误");
        }

        // 生成并返回 token
        String token = JwtUtil.createToken(user.getUserId(), user.getUsername());
        Long userId = user.getUserId();
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", userId);

        return AjaxResult.success("登录成功", data);
    }


    /**
     * 用户信息编辑接口
     *
     * @param requestDTO 包含用户ID和需要编辑的用户信息
     * @return 编辑结果
     */
    @PostMapping("/edit")
    @CheckToken
    @ApiOperation("编辑用户信息")
    public AjaxResult editUser(@RequestBody UserEditRequestDTO requestDTO) {
        // 先查询是否存在该用户
        AlseUser existingUser = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
        if (existingUser == null) {
            return AjaxResult.error("用户不存在");
        }

        // 从DTO中获取用户信息
        AlseUser alseUser = requestDTO.getUserInfo();
        if (Objects.nonNull(requestDTO.getUserInfo()) && StringUtils.isNotBlank(requestDTO.getUserInfo().getPassword())) {
            alseUser.setPassword(SecurityUtils.encryptPassword(requestDTO.getUserInfo().getPassword()));
        } else {
            alseUser.setPassword(null);
        }
        // 只更新传入值不为 null 的字段
        BeanUtils.copyProperties(alseUser, existingUser, getNullPropertyNames(alseUser));

        // 调用 service 层进行更新
        int result = alseUserService.updateAlseUser(existingUser);
        if (result > 0) {
            return AjaxResult.success("用户信息编辑成功");
        } else {
            return AjaxResult.error("用户信息编辑失败");
        }
    }

    /**
     * 忘记密码接口
     */
    @PostMapping("/forget-password")
    @NoToken
    @ApiOperation("忘记密码")
    public AjaxResult forgetPassword(@Validated @RequestBody ForgetPasswordRequestDTO requestDTO) {
        // 验证用户名格式
        String username = requestDTO.getUsername();
        boolean isPhone = ValidateUtil.isPhone(username);
        boolean isEmail = ValidateUtil.isEmail(username);
        if (!isPhone && !isEmail) {
            return AjaxResult.error("请输入正确的手机号或邮箱格式");
        }

        // 查询用户是否存在
        AlseUser queryUser = new AlseUser();
        if (isPhone) {
            queryUser.setPhone(username);
        } else {
            queryUser.setEmail(username);
        }
        AlseUser user = alseUserService.selectAlseUserList(queryUser).stream().findFirst().orElse(null);
        if (user == null) {
            return AjaxResult.error(isPhone ? "手机号未注册" : "邮箱未注册");
        }

        // 验证新密码格式
        if (requestDTO.getNewPassword().length() < 6) {
            return AjaxResult.error("密码长度不能小于6位");
        }

        // 验证两次密码是否一致
        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmNewPassword())) {
            return AjaxResult.error("两次输入的密码不一致");
        }

        try {
            // 更新密码
            user.setPassword(SecurityUtils.encryptPassword(requestDTO.getNewPassword()));
            user.setUpdateTime(new Date());
            user.setUpdateBy(user.getUsername());

            int result = alseUserService.updateAlseUser(user);
            if (result > 0) {
                return AjaxResult.success("密码重置成功");
            } else {
                return AjaxResult.error("密码重置失败");
            }
        } catch (Exception e) {
            log.error("重置密码失败：", e);
            return AjaxResult.error("密码重置失败，请稍后重试");
        }
    }

    /**
     * 获取用户信息接口
     */
    @GetMapping("/info/{userId}")
    @CheckToken
    @ApiOperation("获取用户信息")
    public AjaxResult getUserInfo(@PathVariable("userId") Long userId) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 转换为DTO
            UserInfoResponseDTO userInfoDTO = UserConverter.convertToUserInfoDTO(user);

            return AjaxResult.success(userInfoDTO);
        } catch (Exception e) {
            log.error("获取用户信息失败：", e);
            return AjaxResult.error("获取用户信息失败");
        }
    }


    /**
     * 关注/取消关注用户接口
     */
    @PostMapping("/follow")
    @CheckToken
    @ApiOperation("关注/取消关注用户")
    public AjaxResult followUser(@RequestBody FollowUserRequestDTO requestDTO) {
        // 参数校验
        if (requestDTO.getUserId() == null || requestDTO.getTargetUserId() == null) {
            return AjaxResult.error("参数不完整");
        }

        // 不能关注自己
        if (requestDTO.getUserId().equals(requestDTO.getTargetUserId())) {
            return AjaxResult.error("不能关注自己");
        }

        try {
            // 获取当前用户信息
            AlseUser currentUser = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
            if (currentUser == null) {
                return AjaxResult.error("用户不存在");
            }

            // 验证目标用户是否存在
            AlseUser targetUser = alseUserService.selectAlseUserByUserId(requestDTO.getTargetUserId());
            if (targetUser == null) {
                return AjaxResult.error("关注的用户不存在");
            }

            // 处理关注列表
            String followedUserIdsJson = currentUser.getFollowedUserIds();
            List<Long> followedUserIds;

            if (StringUtils.isEmpty(followedUserIdsJson)) {
                followedUserIds = new ArrayList<>();
            } else {
                // 简单处理JSON字符串，假设格式为 [1,2,3,4]
                String content = followedUserIdsJson.replace("[", "").replace("]", "").trim();
                if (content.isEmpty()) {
                    followedUserIds = new ArrayList<>();
                } else {
                    followedUserIds = Arrays.stream(content.split(","))
                            .map(String::trim)
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                }
            }

            boolean isFollowed = followedUserIds.contains(requestDTO.getTargetUserId());

            // 如果是关注操作且未关注，则添加
            if (requestDTO.isFollow() && !isFollowed) {
                followedUserIds.add(requestDTO.getTargetUserId());
            }
            // 如果是取消关注操作且已关注，则移除
            else if (!requestDTO.isFollow() && isFollowed) {
                followedUserIds.remove(requestDTO.getTargetUserId());
            }

            // 更新用户的关注列表
            String newFollowedUserIdsJson = "[" + followedUserIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")) + "]";


            // 更新用户的关注列表
            currentUser.setFollowedUserIds(newFollowedUserIdsJson);
            currentUser.setUpdateTime(new Date());
            currentUser.setUpdateBy(currentUser.getUsername());

            int result = alseUserService.updateAlseUser(currentUser);
            if (result > 0) {
                return AjaxResult.success(requestDTO.isFollow() ? "关注成功" : "取消关注成功");
            } else {
                return AjaxResult.error(requestDTO.isFollow() ? "关注失败" : "取消关注失败");
            }
        } catch (Exception e) {
            log.error("关注/取消关注操作失败：", e);
            return AjaxResult.error("操作失败，请稍后重试");
        }
    }

    /**
     * 实名认证接口
     */
    @PostMapping("/verify-identity")
    @CheckToken
    @ApiOperation("实名认证")
    public AjaxResult verifyIdentity(@RequestBody VerifyIdentityRequestDTO requestDTO) {
        // 参数校验
        if (requestDTO.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (StringUtils.isEmpty(requestDTO.getRealName())) {
            return AjaxResult.error("真实姓名不能为空");
        }
        if (StringUtils.isEmpty(requestDTO.getIdCardNo())) {
            return AjaxResult.error("身份证号码不能为空");
        }
        if (StringUtils.isEmpty(requestDTO.getIdCardFrontImg())) {
            return AjaxResult.error("身份证正面照片不能为空");
        }
        if (StringUtils.isEmpty(requestDTO.getIdCardBackImg())) {
            return AjaxResult.error("身份证反面照片不能为空");
        }

        // 验证身份证号格式
        if (!ValidateUtil.isIdCard(requestDTO.getIdCardNo())) {
            return AjaxResult.error("身份证号码格式不正确");
        }

        try {
            // 获取用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 检查是否已实名认证
            if (!StringUtils.isEmpty(user.getIdCardNo())) {
                return AjaxResult.error("该用户已完成实名认证，不能重复认证");
            }

            // 更新实名认证信息
            user.setRealName(requestDTO.getRealName());
            user.setIdCardNo(requestDTO.getIdCardNo());
            user.setIdCardFrontImg(requestDTO.getIdCardFrontImg());
            user.setIdCardBackImg(requestDTO.getIdCardBackImg());
            user.setUpdateTime(new Date());
            user.setUpdateBy(user.getUsername());

            int result = alseUserService.updateAlseUser(user);
            if (result > 0) {
                return AjaxResult.success("实名认证成功");
            } else {
                return AjaxResult.error("实名认证失败");
            }
        } catch (Exception e) {
            log.error("实名认证失败：", e);
            return AjaxResult.error("实名认证失败，请稍后重试");
        }
    }

    /**
     * 查询用户浏览记录
     */
    @PostMapping("/browsing-history/list")
    @CheckToken
    @ApiOperation("查询用户浏览记录")
    public AjaxResult getBrowsingHistory(@RequestBody BrowsingHistoryQueryDTO queryDTO) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(queryDTO.getUserId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 获取浏览历史
            String browsingHistoryJson = user.getBrowsingHistory();
            List<BrowsingHistoryRecord> historyRecords = new ArrayList<>();
            List<BrowsingHistoryItem> resultList = new ArrayList<>();

            if (StringUtils.isNotEmpty(browsingHistoryJson)) {
                try {
                    // 在方法内创建一个临时的 ObjectMapper 来解析特定格式的日期
                    ObjectMapper tempMapper = new ObjectMapper();
                    // 设置日期格式
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tempMapper.setDateFormat(dateFormat);

                    // 使用这个临时的 mapper 解析 JSON
                    historyRecords = tempMapper.readValue(browsingHistoryJson,
                            new TypeReference<List<BrowsingHistoryRecord>>() {});

                    // 如果指定了天数范围，过滤记录
                    if (queryDTO.getDays() != null && queryDTO.getDays() > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, -queryDTO.getDays());
                        Date startDate = calendar.getTime();

                        historyRecords = historyRecords.stream()
                                .filter(record -> record.getViewTime().after(startDate))
                                .collect(Collectors.toList());
                    }

                    // 批量查询商品信息
                    if (!historyRecords.isEmpty()) {
                        List<Long> productIds = historyRecords.stream()
                                .map(BrowsingHistoryRecord::getProductId)
                                .collect(Collectors.toList());

                        // 批量查询商品的方法
                        List<AlseProduct> products = alseProductService.selectAlseProductByIds(productIds);
                        Map<Long, AlseProduct> productMap = products.stream()
                                .collect(Collectors.toMap(AlseProduct::getProductId, p -> p));

                        // 组装完整的浏览记录
                        for (BrowsingHistoryRecord record : historyRecords) {
                            AlseProduct product = productMap.get(record.getProductId());
                            if (product != null) {
                                BrowsingHistoryItem item = new BrowsingHistoryItem();
                                item.setProductId(product.getProductId());
                                item.setProductTitle(product.getProductTitle());
                                item.setProductCoverImg(product.getProductCoverImg());
                                item.setProductPrice(product.getProductPrice());
                                item.setViewTime(record.getViewTime());
                                resultList.add(item);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("解析浏览历史异常", e);
                    return AjaxResult.error("解析浏览历史异常");
                }
            }

            // 分页处理
            int total = resultList.size();
            int fromIndex = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            int toIndex = Math.min(fromIndex + queryDTO.getPageSize(), resultList.size());

            if (fromIndex >= resultList.size()) {
                resultList = new ArrayList<>();
            } else {
                resultList = resultList.subList(fromIndex, toIndex);
            }

            BrowsingHistoryResponseDTO response = new BrowsingHistoryResponseDTO();
            response.setTotal(total);
            response.setRecords(resultList);

            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("查询浏览历史失败", e);
            return AjaxResult.error("查询浏览历史失败");
        }
    }

    /**
     * 删除指定浏览记录
     */
    @PostMapping("/browsing-history/delete")
    @CheckToken
    @ApiOperation("删除指定浏览记录")
    public AjaxResult deleteBrowsingHistory(@RequestBody @Validated DeleteBrowsingHistoryDTO deleteDTO) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(deleteDTO.getUserId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 获取浏览历史
            String browsingHistoryJson = user.getBrowsingHistory();
            List<BrowsingHistoryRecord> historyList = new ArrayList<>();

            if (StringUtils.isNotEmpty(browsingHistoryJson)) {
                try {
                    // 在方法内创建一个临时的 ObjectMapper 来解析特定格式的日期
                    ObjectMapper tempMapper = new ObjectMapper();
                    // 设置日期格式
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tempMapper.setDateFormat(dateFormat);

                    // 解析JSON到历史列表 - 这一行是关键
                    historyList = tempMapper.readValue(browsingHistoryJson,
                            new TypeReference<List<BrowsingHistoryRecord>>() {});

                    // 移除指定商品记录
                    boolean removed = historyList.removeIf(record ->
                            deleteDTO.getProductId().equals(record.getProductId()));

                    if (!removed) {
                        return AjaxResult.error("未找到指定浏览记录");
                    }

                    // 保存更新后的浏览历史 - 使用同一个临时mapper确保日期格式一致
                    user.setBrowsingHistory(tempMapper.writeValueAsString(historyList));
                    alseUserService.updateAlseUser(user);

                    return AjaxResult.success("删除浏览记录成功");
                } catch (Exception e) {
                    log.error("处理浏览历史异常", e);
                    return AjaxResult.error("处理浏览历史异常");
                }
            }
            return AjaxResult.error("用户没有浏览记录");
        } catch (Exception e) {
            log.error("删除浏览记录失败", e);
            return AjaxResult.error("删除浏览记录失败");
        }
    }

    /**
     * 清空浏览记录
     */
    @PostMapping("/browsing-history/clear")
    @CheckToken
    @ApiOperation("清空浏览记录")
    public AjaxResult clearBrowsingHistory(@RequestBody @Validated UserIdRequestDTO requestDTO) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 清空浏览历史
            user.setBrowsingHistory("[]");
            alseUserService.updateAlseUser(user);

            return AjaxResult.success("清空浏览记录成功");
        } catch (Exception e) {
            log.error("清空浏览记录失败", e);
            return AjaxResult.error("清空浏览记录失败");
        }
    }


    /**
     * 获取源对象中值为 null 的属性名
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 查询用户关注列表
     */
    @PostMapping("/follows/list")
    @CheckToken
    @ApiOperation("查询用户关注列表")
    public TableDataInfo getUserFollows(@RequestBody @Validated UserFollowQueryDTO queryDTO) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(queryDTO.getUserId());
            if (user == null) {
                throw new ServiceException("用户不存在");
            }

            // 获取关注用户ID列表
            String followedUserIdsJson = user.getFollowedUserIds();
            List<Long> followedUserIds = new ArrayList<>();

            if (StringUtils.isNotEmpty(followedUserIdsJson)) {
                try {
                    followedUserIds = objectMapper.readValue(followedUserIdsJson,
                            new TypeReference<List<Long>>() {});
                } catch (Exception e) {
                    log.error("解析关注用户列表异常", e);
                    throw new ServiceException("解析关注用户列表异常");
                }
            }

            if (followedUserIds.isEmpty()) {
                return getDataTable(new ArrayList<>(), 0);
            }

            // 分页处理
            int start = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            int end = Math.min(start + queryDTO.getPageSize(), followedUserIds.size());

            // 处理页码超出范围情况
            if (start >= followedUserIds.size()) {
                return getDataTable(new ArrayList<>(), followedUserIds.size());
            }

            // 获取当前页的用户ID
            List<Long> pageUserIds = followedUserIds.subList(start, end);

            // 批量查询用户信息
            AlseUser queryParam = new AlseUser();
            Map<String, Object> params = new HashMap<>();
            params.put("userIds", StringUtils.join(pageUserIds, ","));
            queryParam.setParams(params);
            List<AlseUser> userList = alseUserMapper.selectAlseUserByUserIds(pageUserIds);

            // 转换为响应DTO
            List<UserFollowItemDTO> resultList = new ArrayList<>();
            for (AlseUser followedUser : userList) {
                UserFollowItemDTO item = new UserFollowItemDTO();
                item.setUserId(followedUser.getUserId());
                item.setAvatar(followedUser.getAvatar());
                item.setUserName(followedUser.getNickname() != null ?
                        followedUser.getNickname() : followedUser.getUsername());

                // 脱敏处理联系方式
                if (StringUtils.isNotEmpty(followedUser.getPhone())) {
                    item.setContactInfo(maskPhoneNumber(followedUser.getPhone()));
                } else if (StringUtils.isNotEmpty(followedUser.getEmail())) {
                    item.setContactInfo(maskEmail(followedUser.getEmail()));
                } else {
                    item.setContactInfo("");
                }

                // 判断是否认证(有身份证号为已认证)
                item.setIsVerified(StringUtils.isNotEmpty(followedUser.getIdCardNo()));

                resultList.add(item);
            }

            return getDataTable(resultList, followedUserIds.size());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询用户关注列表失败", e);
            throw new ServiceException("查询用户关注列表失败");
        }
    }

    /**
     * 批量取消关注
     */
    @PostMapping("/follows/batch-unfollow")
    @CheckToken
    @ApiOperation("批量取消关注")
    public AjaxResult batchUnfollow(@RequestBody @Validated BatchUnfollowRequestDTO requestDTO) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 获取当前关注列表
            String followedUserIdsJson = user.getFollowedUserIds();
            List<Long> followedUserIds = new ArrayList<>();

            if (StringUtils.isNotEmpty(followedUserIdsJson)) {
                try {
                    followedUserIds = objectMapper.readValue(followedUserIdsJson,
                            new TypeReference<List<Long>>() {});
                } catch (Exception e) {
                    log.error("解析关注用户列表异常", e);
                    return AjaxResult.error("解析关注用户列表异常");
                }
            }

            // 如果关注列表为空，直接返回
            if (followedUserIds.isEmpty()) {
                return AjaxResult.success("操作成功，但关注列表为空");
            }

            // 记录原始大小
            int originalSize = followedUserIds.size();

            // 移除要取消关注的用户
            followedUserIds.removeAll(requestDTO.getUnfollowUserIds());

            // 如果大小没变化，说明没有取消任何关注
            if (originalSize == followedUserIds.size()) {
                return AjaxResult.success("操作成功，但未找到要取消关注的用户");
            }

            // 更新关注列表
            try {
                user.setFollowedUserIds(objectMapper.writeValueAsString(followedUserIds));
                user.setUpdateTime(new Date());
                user.setUpdateBy(user.getUsername());
                alseUserService.updateAlseUser(user);

                return AjaxResult.success("成功取消关注 " + (originalSize - followedUserIds.size()) + " 个用户");
            } catch (Exception e) {
                log.error("更新关注列表失败", e);
                return AjaxResult.error("更新关注列表失败");
            }
        } catch (Exception e) {
            log.error("批量取消关注失败", e);
            return AjaxResult.error("批量取消关注失败");
        }
    }

    /**
     * 查询用户统计数据
     */
    @GetMapping("/statistics")
    @CheckToken
    @ApiOperation("查询用户统计数据")
    public AjaxResult getUserStatistics(@RequestParam Long userId) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 创建响应对象
            UserStatisticsDTO statistics = new UserStatisticsDTO();
            statistics.setUserId(userId);

            // 设置钱包余额
            if (user.getWalletBalance() != null) {
                statistics.setWalletBalance(FinanceUtils.formatNumber(user.getWalletBalance()));
            } else {
                statistics.setWalletBalance("0.00");
            }

            // 1. 查询发布的商品总数（只查询计数，不查询详细内容）
            int publishedCount = alseProductService.countProductsByPublisher(userId);
            statistics.setPublishedProductCount(publishedCount);

            // 2. 查询卖出的商品总数（已完成的订单中的商品数量）
            int soldCount = alseOrderService.countSoldProductsByUser(userId);
            statistics.setSoldProductCount(soldCount);

            // 3. 查询购买的商品总数（已完成的订单中作为买家的订单数量）
            int boughtCount = alseOrderService.countBoughtProductsByUser(userId);
            statistics.setBoughtProductCount(boughtCount);

            // 4. 查询收藏的商品总数
            int favoriteCount = 0;
            if (StringUtils.isNotEmpty(user.getFavoriteProducts())) {
                try {
                    List<Long> favoriteIds = objectMapper.readValue(user.getFavoriteProducts(),
                            new TypeReference<List<Long>>() {});
                    favoriteCount = favoriteIds.size();
                } catch (Exception e) {
                    log.error("解析收藏商品列表异常", e);
                }
            }
            statistics.setFavoriteProductCount(favoriteCount);

            // 5. 查询浏览历史总数
            int browsingCount = 0;
            if (StringUtils.isNotEmpty(user.getBrowsingHistory())) {
                try {
                    // 在方法内创建一个临时的 ObjectMapper 来解析特定格式的日期
                    ObjectMapper tempMapper = new ObjectMapper();
                    // 设置日期格式
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tempMapper.setDateFormat(dateFormat);

                    List<BrowsingHistoryRecord> records = tempMapper.readValue(user.getBrowsingHistory(),
                            new TypeReference<List<BrowsingHistoryRecord>>() {});
                    browsingCount = records.size();
                } catch (Exception e) {
                    log.error("解析浏览历史异常", e);
                    // 尝试使用特定日期格式的解析方式
                    try {
                        List<BrowsingHistoryRecord> records = objectMapper.readValue(user.getBrowsingHistory(),
                                new TypeReference<List<BrowsingHistoryRecord>>() {});
                        browsingCount = records.size();
                    } catch (Exception e2) {
                        log.error("使用特定日期格式解析浏览历史仍然异常", e2);
                    }
                }
            }
            statistics.setBrowsingHistoryCount(browsingCount);

            // 6. 查询关注的用户总数
            int followCount = 0;
            if (StringUtils.isNotEmpty(user.getFollowedUserIds())) {
                try {
                    List<Long> followIds = objectMapper.readValue(user.getFollowedUserIds(),
                            new TypeReference<List<Long>>() {});
                    followCount = followIds.size();
                } catch (Exception e) {
                    log.error("解析关注用户列表异常", e);
                }
            }
            statistics.setFollowedUserCount(followCount);

            return AjaxResult.success(statistics);
        } catch (Exception e) {
            log.error("查询用户统计数据失败", e);
            return AjaxResult.error("查询用户统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 手机号脱敏
     */
    private String maskPhoneNumber(String phone) {
        if (StringUtils.isEmpty(phone) || phone.length() < 8) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 邮箱脱敏
     */
    private String maskEmail(String email) {
        if (StringUtils.isEmpty(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return username + "****" + domain;
        }

        return username.substring(0, 2) + "****" + domain;
    }

    /**
     * 封装分页数据
     */
    protected TableDataInfo getDataTable(List<?> list, long total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(total);
        return rspData;
    }
}
