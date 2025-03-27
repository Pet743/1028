package com.ruoyi.uni.model.DTO.request.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("WAP支付请求DTO")
public class PayRequestDTO {

    @ApiModelProperty(value = "交易总金额(元，字符串格式)", required = true)
    @NotBlank(message = "交易金额不能为空")
    private String totalAmount;

    @ApiModelProperty(value = "支付方式(zfb:支付宝 vx:微信 yl:翼支付)", example = "zfb", required = false)
    private String fangshi = "zfb"; // 默认支付宝

    @ApiModelProperty(value = "备注", required = false)
    private String remark;
}
