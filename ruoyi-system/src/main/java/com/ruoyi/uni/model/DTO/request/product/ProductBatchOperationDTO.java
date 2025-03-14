package com.ruoyi.uni.model.DTO.request.product;


import com.ruoyi.uni.model.Enum.ProductOperationTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("商品批量操作请求DTO")
public class ProductBatchOperationDTO {

    @NotEmpty(message = "商品ID列表不能为空")
    @ApiModelProperty("商品ID列表")
    private List<Long> productIds;

    @NotNull(message = "操作类型不能为空")
    @ApiModelProperty("操作类型(0:上架 1:下架 2:删除)")
    private String operationType;

    /**
     * 获取操作类型枚举
     */
    public ProductOperationTypeEnum getOperationTypeEnum() {
        return ProductOperationTypeEnum.getByCode(operationType);
    }
}