package com.ruoyi.uni.controller;

import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.annotation.NoToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.converter.UserConverter;
import com.ruoyi.uni.model.DTO.request.user.*;
import com.ruoyi.uni.model.DTO.respone.user.UserInfoResponseDTO;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/user")
@Api(tags = "用户信息接口")
public class UniUserController {

    @Autowired
    private IAlseUserService alseUserService;

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
}
