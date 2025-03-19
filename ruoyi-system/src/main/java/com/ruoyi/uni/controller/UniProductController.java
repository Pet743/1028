package com.ruoyi.uni.controller;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.service.IAlseProductService;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.model.DTO.request.product.ProductBatchOperationDTO;
import com.ruoyi.uni.model.DTO.request.product.ProductPublishDTO;
import com.ruoyi.uni.model.DTO.request.product.ProductQueryDTO;
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
import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/product")
@Api(tags = "商品接口")
public class UniProductController {

    // 创建固定精度的金额处理工厂（2位小数）
    private static final BigDecimal ZERO_PRICE = BigDecimal.ZERO;
    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000"); // 最大价格限制

    @Autowired
    private IAlseProductService productService;

    @Autowired
    private IAlseUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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
            AlseUser user = userService.selectAlseUserByUserId(publishDTO.getPublisherId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
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
            int result = productService.insertAlseProduct(product);
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
        // 分页查询
        PageUtils.startPage();

        // 构建查询条件
        AlseProduct query = new AlseProduct();
        query.setProductStatus("0"); // 只查询已上架商品
        query.setStatus("0"); // 只查询正常状态商品

        // 设置分类查询
        if (queryDTO.getCategory() != null && !queryDTO.getCategory().isEmpty()) {
            ProductCategoryEnum categoryEnum = ProductCategoryEnum.getByCode(queryDTO.getCategory());
            query.setProductCategory(categoryEnum.getCode());
        }

        // 查询商品列表
        List<AlseProduct> productList = productService.selectAlseProductList(query);

        // 转换为响应DTO
        List<ProductListResponseDTO> responseList = productList.stream().map(product -> {
            ProductListResponseDTO responseDTO = new ProductListResponseDTO();
            responseDTO.setProductId(product.getProductId());
            responseDTO.setProductTitle(product.getProductTitle());
            responseDTO.setProductCoverImg(product.getProductCoverImg());
            responseDTO.setProductPrice(product.getProductPrice());
            responseDTO.setSalesCount(product.getSalesCount());
            responseDTO.setProductRating(product.getProductRating());
            responseDTO.setShopName(product.getShopName());
            responseDTO.setPublisherId(product.getPublisherId());
            responseDTO.setPublisherName(product.getPublisherName());
            responseDTO.setProductCategory(product.getProductCategory());
            responseDTO.setCategoryDesc(ProductCategoryEnum.getByCode(product.getProductCategory()).getDesc());
            responseDTO.setShippingMethod(product.getShippingMethod());
            responseDTO.setShippingMethodDesc(ShippingMethodEnum.getByCode(product.getShippingMethod()).getDesc());
            responseDTO.setProductStatus(product.getProductStatus());
            responseDTO.setCreateTime(product.getCreateTime());
            return responseDTO;
        }).collect(Collectors.toList());

        // 返回分页结果
        return getDataTable(responseList);
    }

    /**
     * 商品详情查询
     */
    @GetMapping("/detail/{productId}")
    @ApiOperation("商品详情查询")
    public AjaxResult detail(@PathVariable Long productId) {
        if (productId == null) {
            return AjaxResult.error("商品ID不能为空");
        }

        // 查询商品详情
        AlseProduct product = productService.selectAlseProductByProductId(productId);

        if (product == null) {
            return AjaxResult.error("商品不存在");
        }
        AlseUser user = null;
        if (product.getPublisherId() != null) {
            user = userService.selectAlseUserByUserId(product.getPublisherId());
        }

        ProductDetailDTO productDetailDTO = new ProductDetailDTO();
        // 设置用户互动状态（关注和收藏）
        setUserInteractionStatus(productDetailDTO, product, user);

        productDetailDTO.setProductId(product.getProductId());
        productDetailDTO.setProductTitle(product.getProductTitle());
        productDetailDTO.setProductCategory(product.getProductCategory());
        productDetailDTO.setProductCoverImg(product.getProductCoverImg());
        // 转换详情图片从JSON到List
        try {
            if (product.getProductDetailImgs() != null && !product.getProductDetailImgs().isEmpty()) {
                List<String> detailImgList = objectMapper.readValue(product.getProductDetailImgs(), List.class);
                productDetailDTO.setDetailImgList(detailImgList);
            }
        } catch (Exception e) {
            log.error("解析商品详情图片异常", e);
        }

        productDetailDTO.setProductDetailImgs(product.getProductDetailImgs());
        productDetailDTO.setProductDescription(product.getProductDescription());
        productDetailDTO.setProductPrice(product.getProductPrice());
        if (user != null) {
            productDetailDTO.setPublisherImg(user.getAvatar());
        }
        productDetailDTO.setShippingMethod(ShippingMethodEnum.getByCode(product.getShippingMethod()).getDesc());
        productDetailDTO.setSalesCount(product.getSalesCount());
        productDetailDTO.setProductStatus(product.getProductStatus());
        productDetailDTO.setPublisherName(product.getPublisherName());
        productDetailDTO.setPublisherPhone(product.getPublisherPhone());
        productDetailDTO.setProductRating(product.getProductRating());
        productDetailDTO.setShopName(product.getShopName());
        productDetailDTO.setStatus(product.getStatus());
        productDetailDTO.setCreateTime(product.getCreateTime());
        productDetailDTO.setUpdateTime(product.getUpdateTime());
        return AjaxResult.success(productDetailDTO);
    }


    /**
     * 判断当前用户是否关注了商品发布者、是否收藏了该商品
     */
    private void setUserInteractionStatus(ProductDetailDTO productDetailDTO, AlseProduct product, AlseUser currentUser) {
        // 默认设置为未关注、未收藏
        productDetailDTO.setIsStar(false);
        productDetailDTO.setIsShopStar(false);

        // 如果当前用户未登录，直接返回
        if (currentUser == null) {
            return;
        }

        // 获取商品发布者ID
        Long publisherId = product.getPublisherId();
        // 获取商品ID
        Long productId = product.getProductId();

        // 判断是否关注了该用户
        String followedUserIdsJson = currentUser.getFollowedUserIds();
        if (!StringUtils.isEmpty(followedUserIdsJson)) {
            try {
                JSONArray followedUserIds = JSON.parseArray(followedUserIdsJson);
                // 检查发布者ID是否在关注列表中
                for (int i = 0; i < followedUserIds.size(); i++) {
                    if (publisherId.equals(followedUserIds.getLong(i))) {
                        productDetailDTO.setIsStar(true);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("解析用户关注列表失败", e);
            }
        }

        // 判断是否收藏了该商品
        String favoriteProductsJson = currentUser.getFavoriteProducts();
        if (!StringUtils.isEmpty(favoriteProductsJson)) {
            try {
                JSONArray favoriteProducts = JSON.parseArray(favoriteProductsJson);
                // 检查商品ID是否在收藏列表中
                for (int i = 0; i < favoriteProducts.size(); i++) {
                    if (productId.equals(favoriteProducts.getLong(i))) {
                        productDetailDTO.setIsShopStar(true);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("解析用户收藏商品列表失败", e);
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
                        return productService.updateAlseProduct(product);
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
     * 封装分页数据
     */
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(new com.github.pagehelper.PageInfo(list).getTotal());
        return rspData;
    }
}