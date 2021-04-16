package com.qqkj.inspection.common.aspect;


import com.qqkj.inspection.common.properties.OilProperties;
import com.qqkj.inspection.common.utils.HttpContextUtil;
import com.qqkj.inspection.common.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * AOP 记录用户操作日志
 *
 * @author qqkj
 */
@Slf4j
@Aspect
@Component
public class LogAspect
{
    @Autowired
    private OilProperties oilProperties;

    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        // 执行方法
        result = point.proceed();
        // 获取 request
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        // 设置 IP 地址
        String ip = IPUtil.getIpAddr(request);
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        if(oilProperties.isOpenAopLog())
        {
//            // 保存日志
//            String token = (String) SecurityUtils.getSubject().getPrincipal();
//            String username = JWTUtil.getUsername(token);
//
//            Log log = new Log();
//            log.setUsername(username);
//            log.setIp(ip);
//            log.setTime(time);
//            logService.saveLog(point, log);
        }
        return result;
    }
}
