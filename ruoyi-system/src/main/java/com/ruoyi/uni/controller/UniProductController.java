package com.ruoyi.uni.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.service.IAlseProductService;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.converter.ProductConverter;
import com.ruoyi.uni.model.DTO.request.product.FollowProductRequestDTO;
import com.ruoyi.uni.model.DTO.request.product.ProductBatchOperationDTO;
import com.ruoyi.uni.model.DTO.request.product.ProductPublishDTO;
import com.ruoyi.uni.model.DTO.request.product.ProductQueryDTO;
import com.ruoyi.uni.model.DTO.request.user.browsing.BrowsingHistoryRecord;
import com.ruoyi.uni.model.DTO.respone.product.ProductDetailDTO;
import com.ruoyi.uni.model.DTO.respone.product.ProductListResponseDTO;
import com.ruoyi.uni.model.Enum.ProductCategoryEnum;
import com.ruoyi.uni.model.Enum.ProductOperationTypeEnum;
import com.ruoyi.uni.model.Enum.ShippingMethodEnum;
import com.ruoyi.uni.util.FinanceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/product")
@Api(tags = "商品接口")
public class UniProductController {


    @Autowired
    private IAlseProductService alseProductService;

    @Autowired
    private IAlseUserService alseUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductConverter productConverter;

    /**
     * 发布商品
     */
    @PostMapping("/publish")
    @CheckToken
    @ApiOperation("发布商品")
    public AjaxResult publish(@RequestBody ProductPublishDTO publishDTO) {
        try {
            // 校验参数
            if (publishDTO.getProductTitle() == null || publishDTO.getProductTitle().isEmpty()) {
                return AjaxResult.error("商品标题不能为空");
            }
            if (publishDTO.getProductImages() == null || publishDTO.getProductImages().isEmpty()) {
                return AjaxResult.error("商品图片不能为空");
            }
            // 使用decimal4j校验价格
            if (publishDTO.getProductPrice() == null) {
                return AjaxResult.error("商品价格不能为空");
            }
            // 创建商品实体
            AlseProduct product = new AlseProduct();

            // 价格校验与转换
            try {
                // 将字符串价格转为BigDecimal并校验
                BigDecimal price = FinanceUtils.toBigDecimal(publishDTO.getProductPrice());

                // 校验价格是否在有效范围内
                if (!FinanceUtils.isValidAmount(price, FinanceUtils.MIN_AMOUNT, FinanceUtils.MAX_AMOUNT)) {
                    if (price.compareTo(FinanceUtils.ZERO) <= 0) {
                        return AjaxResult.error("商品价格必须大于0");
                    } else if (price.compareTo(FinanceUtils.MAX_AMOUNT) > 0) {
                        return AjaxResult.error("商品价格不能超过" + FinanceUtils.formatNumber(FinanceUtils.MAX_AMOUNT));
                    } else {
                        return AjaxResult.error("商品价格无效");
                    }
                }

                // 标准化价格
                price = FinanceUtils.normalizeAmount(price);

                product.setProductPrice(price);
            } catch (IllegalArgumentException e) {
                return AjaxResult.error("商品价格格式不正确: " + e.getMessage());
            }
            // 获取发布人信息
            AlseUser user = alseUserService.selectAlseUserByUserId(publishDTO.getPublisherId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }
            if (StringUtils.isBlank(user.getIdCardNo())){
                return AjaxResult.error("用户未完成实名认证");
            }

            product.setProductTitle(publishDTO.getProductTitle());
            product.setProductDescription(publishDTO.getProductDescription());

            // 设置商品分类
            String category = publishDTO.getProductCategory();
            ProductCategoryEnum categoryEnum = ProductCategoryEnum.getByCode(category);
            product.setProductCategory(categoryEnum.getCode());

            // 设置发货方式
            String shippingMethod = publishDTO.getShippingMethod();
            ShippingMethodEnum methodEnum = ShippingMethodEnum.getByCode(shippingMethod);
            product.setShippingMethod(methodEnum.getCode());

            // 处理图片，第一张为封面，其余为详情图
            List<String> images = publishDTO.getProductImages();
            if (!images.isEmpty()) {
                product.setProductCoverImg(images.get(0));

                // 将其余图片转为JSON存储
                List<String> detailImgs = images.size() > 1 ? images.subList(1, images.size()) : new ArrayList<>();
                product.setProductDetailImgs(objectMapper.writeValueAsString(detailImgs));
            }

            // 设置发布人信息
            product.setPublisherId(publishDTO.getPublisherId());
            product.setPublisherName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            product.setPublisherPhone(user.getPhone()); // 可能为空
            product.setShopName(user.getNickname() != null ? user.getNickname() + "的店铺" : user.getUsername() + "的店铺");

            // 设置状态
            product.setProductStatus("0"); // 默认上架
            product.setStatus("0"); // 正常状态
            product.setCreateBy(user.getUsername());
            product.setCreateTime(new Date());
            product.setUpdateBy(user.getUsername());
            product.setUpdateTime(new Date());

            // 保存商品
            int result = alseProductService.insertAlseProduct(product);
            if (result > 0) {
                return AjaxResult.success("发布成功", product.getProductId());
            } else {
                return AjaxResult.error("发布失败");
            }
        } catch (Exception e) {
            log.error("发布商品异常", e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 首页商品查询
     */
    @GetMapping("/list")
    @ApiOperation("首页商品查询")
    public TableDataInfo list(ProductQueryDTO queryDTO) {
        Integer pageNum = ServletUtils.getParameterToInt("pageNum");
        Integer pageSize = ServletUtils.getParameterToInt("pageSize");
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        AlseProduct query = new AlseProduct();
        query.setProductStatus("0");
        query.setStatus("0");
        if (queryDTO != null && queryDTO.getCategory() != null && !queryDTO.getCategory().isEmpty()) {
            ProductCategoryEnum categoryEnum = ProductCategoryEnum.getByCode(queryDTO.getCategory());
            query.setProductCategory(categoryEnum.getCode());
        }
        List<AlseProduct> productList = alseProductService.selectAlseProductList(query);
        // 对当前分页数据进行随机排序，给用户每次访问产生数据不一样的错觉
        Collections.shuffle(productList);
        PageInfo<AlseProduct> pageInfo = new PageInfo<>(productList);
        if (pageNum > pageInfo.getPages() && pageInfo.getPages() > 0) {
            return getDataTable(new ArrayList<>(), pageInfo.getTotal());
        }
        List<ProductListResponseDTO> responseList = productConverter.toProductListResponseDTOList(productList);
        return getDataTable(responseList, pageInfo.getTotal());
    }


    /**
     * 商品详情查询
     */
    @GetMapping("/detail/{productId}")
    @ApiOperation("商品详情查询")
    public AjaxResult detail(@PathVariable Long productId, @RequestParam(required = false) Long userId) {
        if (productId == null) {
            return AjaxResult.error("商品ID不能为空");
        }

        // 查询商品详情
        AlseProduct product = alseProductService.selectAlseProductByProductId(productId);

        if (product == null) {
            return AjaxResult.error("商品不存在");
        }

        AlseUser user = null;
        if (product.getPublisherId() != null) {
            user = alseUserService.selectAlseUserByUserId(product.getPublisherId());
        }
        List<String> detailImgList = null;
        try {
            if (StringUtils.isNotEmpty(product.getProductDetailImgs())) {
                detailImgList = objectMapper.readValue(product.getProductDetailImgs(), List.class);
            }
        } catch (Exception e) {
            log.error("解析商品详情图片异常", e);
        }

        // 转换为DTO
        ProductDetailDTO productDetailDTO = productConverter.toProductDetailDTO(product, user, detailImgList);

        // 处理用户相关逻辑
        if (Objects.nonNull(userId)) {
            try {
                AlseUser alseUser = alseUserService.selectAlseUserByUserId(userId);
                if (alseUser == null) {
                    return AjaxResult.error("用户不存在");
                }

                // 设置交互状态
                setUserInteractionStatus(productDetailDTO, product, alseUser);

                // 记录浏览历史
                recordBrowsingHistory(alseUser, product);
            } catch (Exception e) {
                log.error("处理用户相关数据失败", e);
                // 继续执行，不影响主流程
            }
        }

        return AjaxResult.success(productDetailDTO);
    }

    /**
     * 记录用户浏览历史
     */
    private void recordBrowsingHistory(AlseUser user, AlseProduct product) {
        if (user == null) {
            return;
        }
        // 配置日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);

        // 创建新的浏览记录
        BrowsingHistoryRecord newRecord = new BrowsingHistoryRecord();
        newRecord.setProductId(product.getProductId());
        newRecord.setViewTime(new Date());

        // 获取当前浏览历史
        String browsingHistoryJson = user.getBrowsingHistory();
        List<BrowsingHistoryRecord> historyList = new ArrayList<>();

        if (StringUtils.isNotEmpty(browsingHistoryJson)) {
            try {
                historyList = objectMapper.readValue(browsingHistoryJson,
                        new TypeReference<List<BrowsingHistoryRecord>>() {});

                // 检查是否已存在该商品，如果存在则更新浏览时间
                Optional<BrowsingHistoryRecord> existingRecord = historyList.stream()
                        .filter(record -> record.getProductId().equals(product.getProductId()))
                        .findFirst();

                if (existingRecord.isPresent()) {
                    existingRecord.get().setViewTime(newRecord.getViewTime());
                    // 将更新的记录移到列表开头
                    historyList.remove(existingRecord.get());
                    historyList.add(0, existingRecord.get());
                } else {
                    // 添加新记录到列表开头
                    historyList.add(0, newRecord);
                }
            } catch (Exception e) {
                log.error("解析浏览历史异常", e);
                historyList = new ArrayList<>();
                historyList.add(newRecord);
            }
        } else {
            historyList.add(newRecord);
        }

        // 限制最多保存100条记录
        if (historyList.size() > 100) {
            historyList = historyList.subList(0, 100);
        }

        // 保存更新后的浏览历史
        try {
            user.setBrowsingHistory(objectMapper.writeValueAsString(historyList));
            alseUserService.updateAlseUser(user);
        } catch (Exception e) {
            log.error("保存浏览历史异常", e);
        }
    }

    /**
     * 判断当前用户是否关注了商品发布者、是否收藏了该商品
     */
    private void setUserInteractionStatus(ProductDetailDTO productDetailDTO, AlseProduct product, AlseUser currentUser ) {
        if (Objects.isNull(currentUser)) {
            return;
        }
        // 默认设置为未关注、未收藏
        productDetailDTO.setIsStar(false);
        productDetailDTO.setIsShopStar(false);

        // 获取商品发布者ID
        Long publisherId = product.getPublisherId();
        // 获取商品ID
        Long productId = product.getProductId();

        // 判断是否关注了该用户
        String followedUserIdsJson = currentUser.getFollowedUserIds();
        if (StringUtils.isNotEmpty(followedUserIdsJson)) {
            try {
                // 使用 ObjectMapper 解析JSON数组
                List<Long> followedUserIds = objectMapper.readValue(followedUserIdsJson,
                        new TypeReference<List<Long>>() {});

                // 检查发布者ID是否在关注列表中
                if (followedUserIds.contains(publisherId)) {
                    productDetailDTO.setIsStar(true);
                }
            } catch (Exception e) {
                log.error("解析用户关注列表失败: " + followedUserIdsJson, e);
            }
        }

        // 判断是否收藏了该商品
        String favoriteProductsJson = currentUser.getFavoriteProducts();
        if (StringUtils.isNotEmpty(favoriteProductsJson)) {
            try {
                // 使用 ObjectMapper 解析JSON数组
                List<Long> favoriteProducts = objectMapper.readValue(favoriteProductsJson,
                        new TypeReference<List<Long>>() {});

                // 检查商品ID是否在收藏列表中
                if (favoriteProducts.contains(productId)) {
                    productDetailDTO.setIsShopStar(true);
                }
            } catch (Exception e) {
                log.error("解析用户收藏商品列表失败: " + favoriteProductsJson, e);
            }
        }
    }

    /**
     * 批量操作商品状态
     */
    @PostMapping("/batchOperation")
    @CheckToken
    @ApiOperation("批量操作商品状态(上架/下架/删除)")
    public AjaxResult batchOperation(@RequestBody ProductBatchOperationDTO operationDTO) {
        if (operationDTO.getProductIds() == null || operationDTO.getProductIds().isEmpty()) {
            return AjaxResult.error("商品ID列表不能为空");
        }

        if (operationDTO.getOperationType() == null) {
            return AjaxResult.error("操作类型不能为空");
        }

        try {
            // 获取操作类型枚举
            ProductOperationTypeEnum operationType = operationDTO.getOperationTypeEnum();
            if (operationType == null) {
                return AjaxResult.error("不支持的操作类型");
            }

            // 批量处理商品
            int result = operationDTO.getProductIds().stream()
                    .map(productId -> {
                        AlseProduct product = new AlseProduct();
                        product.setProductId(productId);
                        // 应用操作
                        operationType.getOperation().accept(product);
                        // 更新并返回结果
                        return alseProductService.updateAlseProduct(product);
                    })
                    .reduce(0, Integer::sum);

            if (result > 0) {
                return AjaxResult.success("批量" + operationType.getDesc() + "成功");
            } else {
                return AjaxResult.error("批量" + operationType.getDesc() + "失败");
            }
        } catch (Exception e) {
            log.error("批量操作商品状态异常", e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 收藏/取消收藏商品接口
     */
    @PostMapping("/favorite-product")
    @CheckToken
    @ApiOperation("收藏/取消收藏商品")
    public AjaxResult favoriteProduct(@RequestBody FollowProductRequestDTO requestDTO) {
        try {
            // 参数校验
            if (requestDTO.getUserId() == null || requestDTO.getTargetProductId() == null) {
                return AjaxResult.error("参数不完整");
            }
            // 获取当前用户信息
            AlseUser currentUser = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
            if (currentUser == null) {
                return AjaxResult.error("用户不存在");
            }
            // 验证目标商品是否存在
            AlseProduct product = alseProductService.selectAlseProductByProductId(requestDTO.getTargetProductId());
            if (product == null) {
                return AjaxResult.error("商品不存在");
            }
            // 验证不能收藏自己发布的商品
            if (product.getPublisherId().equals(requestDTO.getUserId())) {
                return AjaxResult.error("不能收藏自己发布的商品");
            }
            // 处理收藏列表
            String favoriteProductsJson = currentUser.getFavoriteProducts();
            List<Long> favoriteProductIds = new ArrayList<>();
            if (StringUtils.isNotEmpty(favoriteProductsJson)) {
                try {
                    // 使用ObjectMapper解析JSON
                    favoriteProductIds = objectMapper.readValue(favoriteProductsJson,
                            new TypeReference<List<Long>>() {
                            });
                } catch (Exception e) {
                    log.error("解析收藏商品列表异常", e);
                    // 如果解析失败，初始化为空列表
                    favoriteProductIds = new ArrayList<>();
                }
            }
            boolean isFavorited = favoriteProductIds.contains(requestDTO.getTargetProductId());
            // 如果是收藏操作且未收藏，则添加
            if (requestDTO.isFollow() && !isFavorited) {
                favoriteProductIds.add(requestDTO.getTargetProductId());
            }
            // 如果是取消收藏操作且已收藏，则移除
            else if (!requestDTO.isFollow() && isFavorited) {
                favoriteProductIds.remove(requestDTO.getTargetProductId());
            } else {
                // 状态已经是目标状态，直接返回成功
                return AjaxResult.success(requestDTO.isFollow() ? "商品已在收藏列表中" : "商品已不在收藏列表中");
            }
            // 更新用户的收藏列表
            try {
                String newFavoriteProductsJson = objectMapper.writeValueAsString(favoriteProductIds);
                currentUser.setFavoriteProducts(newFavoriteProductsJson);
                currentUser.setUpdateTime(new Date());
                currentUser.setUpdateBy(currentUser.getUsername());
                int result = alseUserService.updateAlseUser(currentUser);
                if (result > 0) {
                    return AjaxResult.success(requestDTO.isFollow() ? "收藏成功" : "取消收藏成功");
                } else {
                    return AjaxResult.error(requestDTO.isFollow() ? "收藏失败" : "取消收藏失败");
                }
            } catch (Exception e) {
                log.error("更新收藏列表失败", e);
                return AjaxResult.error("操作失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("收藏/取消收藏操作失败：", e);
            return AjaxResult.error("操作失败，请稍后重试");
        }
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