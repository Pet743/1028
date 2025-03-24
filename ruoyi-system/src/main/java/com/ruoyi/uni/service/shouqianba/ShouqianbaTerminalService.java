package com.ruoyi.uni.service.shouqianba;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收钱吧终端管理服务
 */
@Service
@Slf4j
public class ShouqianbaTerminalService {

    @Autowired
    private ShouqianbaService shouqianbaService;

    /**
     * 初始化终端
     * 检查是否已激活，如果没有则激活，否则签到
     */
    public boolean initTerminal() {
        // 尝试获取终端号，如果不为空表示已经激活过
        if (StringUtils.isNotEmpty(shouqianbaService.getTerminalSn())) {
            log.info("终端已激活，执行签到操作");
            return shouqianbaService.checkin();
        } else {
            log.info("终端未激活，执行激活操作");
            return shouqianbaService.activate();
        }
    }

    /**
     * 终端签到
     */
    public boolean checkinTerminal() {
        boolean checkedIn = shouqianbaService.checkin();
        if (checkedIn) {
            log.info("终端签到成功");
            return true;
        } else {
            log.warn("终端签到失败，尝试重新激活");
            return shouqianbaService.activate();
        }
    }
}