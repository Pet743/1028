package com.ruoyi.uni.service.shouqianba;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 收钱吧终端管理服务
 */
@Service
public class ShouqianbaTerminalService {

    private static final Logger log = LoggerFactory.getLogger(ShouqianbaTerminalService.class);

    private static final String TERMINAL_INFO_KEY = "shouqianba:terminal:info";


    @Autowired
    private ShouqianbaService shouqianbaService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 初始化终端
     * 检查是否有终端信息，如果没有则激活，如果有则恢复并签到
     */
    public boolean initTerminal() {
        // 从Redis获取终端信息
        JSONObject terminalInfo = redisCache.getCacheObject(TERMINAL_INFO_KEY);

        if (terminalInfo != null && terminalInfo.containsKey("terminalSn") && terminalInfo.containsKey("terminalKey")) {
            // 如果Redis中有终端信息，则设置到Service中并进行签到
            shouqianbaService.setTerminalInfo(
                    terminalInfo.getString("terminalSn"),
                    terminalInfo.getString("terminalKey")
            );
            return shouqianbaService.checkin();
        } else {
            // 如果Redis中没有终端信息，则激活终端
            boolean activated = shouqianbaService.activate();
            if (activated) {
                // 保存终端信息到Redis
                saveTerminalInfoToRedis();
                return true;
            }
            return false;
        }
    }

    /**
     * 终端签到
     */
    public boolean checkinTerminal() {
        boolean checkedIn = shouqianbaService.checkin();
        if (checkedIn) {
            // 更新Redis中的终端信息
            saveTerminalInfoToRedis();
            return true;
        }
        return false;
    }


    /**
     * 将终端信息保存到Redis
     */
    private void saveTerminalInfoToRedis() {
        JSONObject terminalInfo = new JSONObject();
        terminalInfo.put("terminalSn", shouqianbaService.getTerminalSn());
        terminalInfo.put("terminalKey", shouqianbaService.getTerminalKey());

        // 保存到Redis，可以设置合适的过期时间，例如24小时
        redisCache.setCacheObject(TERMINAL_INFO_KEY, terminalInfo, 72, TimeUnit.HOURS);
        log.info("终端信息已保存到Redis，SN：{}", shouqianbaService.getTerminalSn());
    }

    /**
     * 从Redis获取终端信息
     * @return 终端信息，如果不存在则返回null
     */
    public JSONObject getTerminalInfoFromRedis() {
        return redisCache.getCacheObject(TERMINAL_INFO_KEY);
    }

    /**
     * 检查终端信息是否存在
     * @return 如果终端信息存在则返回true
     */
    public boolean hasTerminalInfo() {
        JSONObject terminalInfo = getTerminalInfoFromRedis();
        return terminalInfo != null &&
                terminalInfo.containsKey("terminalSn") &&
                terminalInfo.containsKey("terminalKey");
    }

    /**
     * 清除终端信息
     */
    public void clearTerminalInfo() {
        redisCache.deleteObject(TERMINAL_INFO_KEY);
        log.info("已清除Redis中的终端信息");
    }
}