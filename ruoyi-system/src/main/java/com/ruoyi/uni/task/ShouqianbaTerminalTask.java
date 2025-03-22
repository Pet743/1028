package com.ruoyi.uni.task;

import com.ruoyi.uni.service.shouqianba.ShouqianbaTerminalService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 收钱吧终端管理定时任务
 */
@Component("shouqianbaTerminalTask")
@Slf4j
public class ShouqianbaTerminalTask {

    private static final Logger log = LoggerFactory.getLogger(ShouqianbaTerminalTask.class);

    @Autowired
    private ShouqianbaTerminalService terminalService;

    /**
     * 应用启动时初始化终端
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void initTerminalOnStartup() {
        log.info("应用启动，初始化收钱吧终端");
        boolean success = terminalService.initTerminal();
        if (success) {
            log.info("终端初始化成功");
        } else {
            log.error("终端初始化失败，将在下次定时任务中重试");
        }
    }

    /**
     * 每天凌晨2点进行终端签到
     * 商户每日第一笔交易前必须完成签到,保证终端密钥正确
     */
    public void dailyCheckin() {
        log.info("执行收钱吧终端签到任务");
        boolean success = terminalService.checkinTerminal();
        if (success) {
            log.info("终端签到成功");
        } else {
            log.error("终端签到失败，将尝试重新激活");
            boolean activated = terminalService.initTerminal();
            if (activated) {
                log.info("终端重新激活成功");
            } else {
                log.error("终端重新激活失败，请检查配置或联系收钱吧客服");
            }
        }
    }
}
