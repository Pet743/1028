package com.ruoyi.uni.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.service.IAlseProductService;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.uni.converter.ProductConverter;
import com.ruoyi.uni.model.DTO.request.userPublish.ProductPriceUpdateDTO;
import com.ruoyi.uni.model.DTO.request.userPublish.ProductStatusUpdateDTO;
import com.ruoyi.uni.model.DTO.request.userPublish.UserPublishProductVO;
import com.ruoyi.uni.model.DTO.request.userPublish.UserPublishQueryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/user/publish")
@Api(tags = "用户管理发布商品信息接口")
public class UniUserPublishController {


    @Autowired
    private IAlseUserService alseUserService;

    @Autowired
    private IAlseProductService productService;

    @Autowired
    private ProductConverter productConverter;

    /**
     * 查询用户发布的商品列表
     */
    @GetMapping("/products")
    @ApiOperation("查询用户发布的商品列表")
    public TableDataInfo listUserProducts(@Validated UserPublishQueryDTO queryDTO) {
        // 验证用户是否存在
        AlseUser user = alseUserService.selectAlseUserByUserId(queryDTO.getUserId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        Integer pageNum = ServletUtils.getParameterToInt("pageNum");
        Integer pageSize = ServletUtils.getParameterToInt("pageSize");

        // 确保分页参数有效
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;  // 默认每页10条
        }

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        AlseProduct queryParam = new AlseProduct();
        queryParam.setPublisherId(queryDTO.getUserId());
        queryParam.setProductStatus(queryDTO.getProductStatus());

        // 查询列表
        List<AlseProduct> list = productService.selectAlseProductList(queryParam);

        // 创建PageInfo对象，用于获取总记录数和页数信息
        PageInfo<AlseProduct> pageInfo = new PageInfo<>(list);

        // 如果当前页码超出总页数且总页数不为0，则返回空列表
        if (pageNum > pageInfo.getPages() && pageInfo.getPages() > 0) {
            return getDataTable(new ArrayList<>(), pageInfo.getTotal());
        }

        // 转换为VO
        List<UserPublishProductVO> voList = list.stream()
                .map(productConverter::toUserPublishProductVO)
                .collect(Collectors.toList());

        return getDataTable(voList, pageInfo.getTotal());
    }

    /**
     * 修改商品价格
     */
    @PutMapping("/price")
    @ApiOperation("修改商品价格")
    public AjaxResult updatePrice(@RequestBody @Validated ProductPriceUpdateDTO updateDTO) {
        // 验证商品是否存在且属于该用户
        AlseProduct product = productService.selectAlseProductByProductId(updateDTO.getProductId());
        if (product == null) {
            return AjaxResult.error("商品不存在");
        }
        if (!product.getPublisherId().equals(updateDTO.getUserId())) {
            return AjaxResult.error("无权操作此商品");
        }

        // 更新价格
        product.setProductPrice(updateDTO.getNewPrice());
        product.setUpdateTime(DateUtils.getNowDate());
        product.setUpdateBy(updateDTO.getUserId().toString());

        int rows = productService.updateAlseProduct(product);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 下架商品
     */
    @PutMapping("/offline")
    @ApiOperation("下架商品")
    public AjaxResult offlineProduct(@RequestBody @Validated ProductStatusUpdateDTO updateDTO) {
        // 验证商品是否存在且属于该用户
        AlseProduct product = productService.selectAlseProductByProductId(updateDTO.getProductId());
        if (product == null) {
            return AjaxResult.error("商品不存在");
        }
        if (!product.getPublisherId().equals(updateDTO.getUserId())) {
            return AjaxResult.error("无权操作此商品");
        }
        if ("1".equals(product.getProductStatus())) {
            return AjaxResult.error("商品已经是下架状态");
        }

        // 更新状态为下架
        product.setProductStatus("1");
        product.setUpdateTime(DateUtils.getNowDate());
        product.setUpdateBy(updateDTO.getUserId().toString());

        int rows = productService.updateAlseProduct(product);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 下架商品
     */
    @PutMapping("/listed")
    @ApiOperation("上架商品")
    public AjaxResult listedProduct(@RequestBody @Validated ProductStatusUpdateDTO updateDTO) {
        // 验证商品是否存在且属于该用户
        AlseProduct product = productService.selectAlseProductByProductId(updateDTO.getProductId());
        if (product == null) {
            return AjaxResult.error("商品不存在");
        }
        if (!product.getPublisherId().equals(updateDTO.getUserId())) {
            return AjaxResult.error("无权操作此商品");
        }
        if ("0".equals(product.getProductStatus())) {
            return AjaxResult.error("商品已经是下架状态");
        }

        // 更新状态为上架
        product.setProductStatus("0");
        product.setUpdateTime(DateUtils.getNowDate());
        product.setUpdateBy(updateDTO.getUserId().toString());

        int rows = productService.updateAlseProduct(product);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
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