package com.qqkj.inspection.common.task;

import com.qqkj.inspection.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 主要用于定时删除 Redis中 key为 qqkj.user.active 中
 * 已经过期的 score
 */
@Slf4j
//@Component
public class CacheTask {

    @Autowired
    private RedisService redisService;

//    @Scheduled(fixedRate = 3600000)
    public void run() {
        try {
        } catch (Exception ignore) {
        }
    }
}
