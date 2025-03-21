package com.ruoyi.uni.converter;

import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.uni.model.DTO.request.userPublish.UserPublishProductVO;
import com.ruoyi.uni.model.DTO.respone.product.ProductDetailDTO;
import com.ruoyi.uni.model.DTO.respone.product.ProductListResponseDTO;
import com.ruoyi.uni.model.Enum.ProductCategoryEnum;
import com.ruoyi.uni.model.Enum.ShippingMethodEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductConverter {

    public UserPublishProductVO toUserPublishProductVO(AlseProduct product) {
        if (product == null) {
            return null;
        }

        UserPublishProductVO vo = new UserPublishProductVO();
        vo.setProductId(product.getProductId());
        vo.setProductCoverImg(product.getProductCoverImg());
        vo.setProductTitle(product.getProductTitle());
        vo.setCreateTime(product.getCreateTime());
        vo.setProductStatus(product.getProductStatus());
        vo.setProductPrice(product.getProductPrice());
        return vo;
    }

    /**
     * 将商品实体和发布者信息转换为商品详情DTO
     */
    public ProductDetailDTO toProductDetailDTO(AlseProduct product, AlseUser publisher, List<String> detailImgList) {
        if (product == null) {
            return null;
        }

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(product.getProductId());
        dto.setProductTitle(product.getProductTitle());
        dto.setProductCategory(product.getProductCategory());
        dto.setProductCoverImg(product.getProductCoverImg());
        dto.setProductDetailImgs(product.getProductDetailImgs());
        dto.setDetailImgList(detailImgList);
        dto.setProductDescription(product.getProductDescription());
        dto.setProductPrice(product.getProductPrice());

        if (publisher != null) {
            dto.setPublisherImg(publisher.getAvatar());
        }

        // 处理枚举转换
        if (product.getShippingMethod() != null) {
            dto.setShippingMethod(ShippingMethodEnum.getByCode(product.getShippingMethod()).getDesc());
        }

        dto.setSalesCount(product.getSalesCount());
        dto.setProductStatus(product.getProductStatus());
        dto.setPublisherId(product.getPublisherId());
        dto.setPublisherName(product.getPublisherName());
        dto.setPublisherPhone(product.getPublisherPhone());
        dto.setProductRating(product.getProductRating());
        dto.setShopName(product.getShopName());
        dto.setStatus(product.getStatus());
        dto.setCreateTime(product.getCreateTime());
        dto.setUpdateTime(product.getUpdateTime());

        // 默认设置为未关注、未收藏
        dto.setIsStar(false);
        dto.setIsShopStar(false);

        return dto;
    }

    /**
     * 批量转换商品列表为响应DTO列表
     */
    public List<ProductListResponseDTO> toProductListResponseDTOList(List<AlseProduct> productList) {
        if (productList == null) {
            return new ArrayList<>();
        }

        return productList.stream()
                .map(this::toProductListResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将商品实体转换为列表响应DTO
     */
    public ProductListResponseDTO toProductListResponseDTO(AlseProduct product) {
        if (product == null) {
            return null;
        }

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

        // 类别描述转换
        if (product.getProductCategory() != null) {
            responseDTO.setCategoryDesc(ProductCategoryEnum.getByCode(product.getProductCategory()).getDesc());
        }

        responseDTO.setShippingMethod(product.getShippingMethod());

        // 发货方式描述转换
        if (product.getShippingMethod() != null) {
            responseDTO.setShippingMethodDesc(ShippingMethodEnum.getByCode(product.getShippingMethod()).getDesc());
        }

        responseDTO.setProductStatus(product.getProductStatus());
        responseDTO.setCreateTime(product.getCreateTime());

        return responseDTO;
    }

}
