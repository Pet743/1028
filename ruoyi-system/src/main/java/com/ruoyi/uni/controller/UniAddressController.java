package com.ruoyi.uni.controller;

import com.ruoyi.alse.domain.AlseUserAddress;
import com.ruoyi.alse.service.IAlseUserAddressService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户地址接口
 */
@RestController
@RequestMapping("/api/address")
@Api(tags = "用户地址接口")
public class UniAddressController extends BaseController {

    @Autowired
    private IAlseUserAddressService addressService;

    /**
     * 根据用户ID查询地址列表
     */
    @GetMapping("/list/{userId}")
    @CheckToken
    @ApiOperation("查询用户地址列表")
    public AjaxResult getAddressList(@PathVariable("userId") Long userId) {
        AlseUserAddress queryParam = new AlseUserAddress();
        queryParam.setUserId(userId);
        queryParam.setStatus("0"); // 只查询状态正常的地址
        List<AlseUserAddress> addressList = addressService.selectAlseUserAddressList(queryParam);
        return AjaxResult.success(addressList);
    }

    /**
     * 查询用户默认地址
     */
    @GetMapping("/default/{userId}")
    @CheckToken
    @ApiOperation("查询用户默认地址")
    public AjaxResult getDefaultAddress(@PathVariable("userId") Long userId) {
        AlseUserAddress queryParam = new AlseUserAddress();
        queryParam.setUserId(userId);
        queryParam.setIsDefault("1"); // 查询默认地址
        queryParam.setStatus("0"); // 只查询状态正常的地址
        List<AlseUserAddress> addressList = addressService.selectAlseUserAddressList(queryParam);

        if (addressList != null && !addressList.isEmpty()) {
            return AjaxResult.success(addressList.get(0));
        } else {
            return AjaxResult.success(null);
        }
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    @CheckToken
    @ApiOperation("设置默认地址")
    public AjaxResult setDefaultAddress(@RequestBody SetDefaultAddressDTO requestDTO) {
        // 参数校验
        if (requestDTO.getUserId() == null || requestDTO.getAddressId() == null) {
            return AjaxResult.error("参数不完整");
        }

        try {
            // 先将该用户所有地址设为非默认
            AlseUserAddress queryParam = new AlseUserAddress();
            queryParam.setUserId(requestDTO.getUserId());
            queryParam.setIsDefault("1");
            List<AlseUserAddress> defaultAddresses = addressService.selectAlseUserAddressList(queryParam);

            for (AlseUserAddress address : defaultAddresses) {
                address.setIsDefault("0");
                addressService.updateAlseUserAddress(address);
            }

            // 将指定地址设置为默认
            AlseUserAddress address = addressService.selectAlseUserAddressByAddressId(requestDTO.getAddressId());
            if (address == null) {
                return AjaxResult.error("地址不存在");
            }

            // 确认地址属于该用户
            if (!address.getUserId().equals(requestDTO.getUserId())) {
                return AjaxResult.error("无权操作此地址");
            }

            address.setIsDefault("1");
            int rows = addressService.updateAlseUserAddress(address);
            if (rows > 0) {
                return AjaxResult.success("设置成功");
            } else {
                return AjaxResult.error("设置失败");
            }
        } catch (Exception e) {
            logger.error("设置默认地址失败：", e);
            return AjaxResult.error("设置失败");
        }
    }

    /**
     * 新增地址
     */
    @PostMapping
    @CheckToken
    @ApiOperation("新增地址")
    public AjaxResult addAddress(@RequestBody AlseUserAddress address) {
        // 参数校验
        if (address.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (address.getContactName() == null || address.getContactName().trim().isEmpty()) {
            return AjaxResult.error("联系人姓名不能为空");
        }
        if (address.getContactPhone() == null || address.getContactPhone().trim().isEmpty()) {
            return AjaxResult.error("联系人手机号不能为空");
        }
        if (address.getProvince() == null || address.getProvince().trim().isEmpty() ||
                address.getCity() == null || address.getCity().trim().isEmpty() ||
                address.getDistrict() == null || address.getDistrict().trim().isEmpty() ||
                address.getDetailAddress() == null || address.getDetailAddress().trim().isEmpty()) {
            return AjaxResult.error("地址信息不完整");
        }

        try {
            // 设置其他默认值
            address.setStatus("0"); // 默认状态为正常
            address.setCreateBy(String.valueOf(address.getUserId()));

            // 如果要设置为默认地址
            if ("1".equals(address.getIsDefault())) {
                // 先将该用户所有地址设为非默认
                AlseUserAddress queryParam = new AlseUserAddress();
                queryParam.setUserId(address.getUserId());
                queryParam.setIsDefault("1");
                List<AlseUserAddress> defaultAddresses = addressService.selectAlseUserAddressList(queryParam);

                for (AlseUserAddress defaultAddress : defaultAddresses) {
                    defaultAddress.setIsDefault("0");
                    addressService.updateAlseUserAddress(defaultAddress);
                }
            } else {
                // 如果未指定是否默认，则默认为非默认地址
                address.setIsDefault("0");
            }

            int rows = addressService.insertAlseUserAddress(address);
            if (rows > 0) {
                return AjaxResult.success("新增成功", address);
            } else {
                return AjaxResult.error("新增失败");
            }
        } catch (Exception e) {
            logger.error("新增地址失败：", e);
            return AjaxResult.error("新增失败");
        }
    }

    /**
     * 编辑地址
     */
    @PutMapping
    @CheckToken
    @ApiOperation("编辑地址")
    public AjaxResult updateAddress(@RequestBody AlseUserAddress address) {
        // 参数校验
        if (address.getAddressId() == null) {
            return AjaxResult.error("地址ID不能为空");
        }

        try {
            // 查询原地址信息
            AlseUserAddress existingAddress = addressService.selectAlseUserAddressByAddressId(address.getAddressId());
            if (existingAddress == null) {
                return AjaxResult.error("地址不存在");
            }

            // 确认地址属于当前用户
            if (address.getUserId() != null && !existingAddress.getUserId().equals(address.getUserId())) {
                return AjaxResult.error("无权操作此地址");
            }

            // 设置更新者
            address.setUpdateBy(String.valueOf(existingAddress.getUserId()));

            // 如果要设置为默认地址
            if ("1".equals(address.getIsDefault()) && !"1".equals(existingAddress.getIsDefault())) {
                // 先将该用户所有地址设为非默认
                AlseUserAddress queryParam = new AlseUserAddress();
                queryParam.setUserId(existingAddress.getUserId());
                queryParam.setIsDefault("1");
                List<AlseUserAddress> defaultAddresses = addressService.selectAlseUserAddressList(queryParam);

                for (AlseUserAddress defaultAddress : defaultAddresses) {
                    defaultAddress.setIsDefault("0");
                    addressService.updateAlseUserAddress(defaultAddress);
                }
            }

            int rows = addressService.updateAlseUserAddress(address);
            if (rows > 0) {
                return AjaxResult.success("更新成功");
            } else {
                return AjaxResult.error("更新失败");
            }
        } catch (Exception e) {
            logger.error("编辑地址失败：", e);
            return AjaxResult.error("编辑失败");
        }
    }

    /**
     * 删除地址（软删除）
     */
    @DeleteMapping("/{addressId}")
    @CheckToken
    @ApiOperation("删除地址")
    public AjaxResult deleteAddress(@PathVariable("addressId") Long addressId, @RequestParam("userId") Long userId) {
        try {
            // 查询原地址信息
            AlseUserAddress existingAddress = addressService.selectAlseUserAddressByAddressId(addressId);
            if (existingAddress == null) {
                return AjaxResult.error("地址不存在");
            }

            // 确认地址属于当前用户
            if (!existingAddress.getUserId().equals(userId)) {
                return AjaxResult.error("无权操作此地址");
            }

            // 如果是默认地址，需要处理默认地址的转移
            if ("1".equals(existingAddress.getIsDefault())) {
                // 查询该用户其他状态正常的地址
                AlseUserAddress queryParam = new AlseUserAddress();
                queryParam.setUserId(userId);
                queryParam.setStatus("0"); // 状态正常
                List<AlseUserAddress> otherAddresses = addressService.selectAlseUserAddressList(queryParam);

                // 过滤掉当前要删除的地址
                otherAddresses = otherAddresses.stream()
                        .filter(addr -> !addr.getAddressId().equals(addressId))
                        .collect(Collectors.toList());

                // 如果还有其他地址，将第一个设为默认
                if (!otherAddresses.isEmpty()) {
                    AlseUserAddress newDefaultAddress = otherAddresses.get(0);
                    newDefaultAddress.setIsDefault("1");
                    addressService.updateAlseUserAddress(newDefaultAddress);
                }
            }

            // 执行软删除操作
            AlseUserAddress updateParam = new AlseUserAddress();
            updateParam.setAddressId(addressId);
            updateParam.setStatus("1"); // 设置为停用状态
            updateParam.setUpdateBy(userId.toString());

            int rows = addressService.updateAlseUserAddress(updateParam);
            if (rows > 0) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            logger.error("删除地址失败：", e);
            return AjaxResult.error("删除失败");
        }
    }
}

/**
 * 设置默认地址请求DTO
 */
class SetDefaultAddressDTO {
    private Long userId;
    private Long addressId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}