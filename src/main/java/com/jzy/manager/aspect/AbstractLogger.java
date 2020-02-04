package com.jzy.manager.aspect;

import com.jzy.manager.util.ShiroUtils;
import com.jzy.service.ImportantLogService;
import com.jzy.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName AbstractLogger
 * @Author JinZhiyun
 * @Description 抽象的日志管理器
 * @Date 2020/1/31 12:35
 * @Version 1.0
 **/
@Aspect
@Component
public abstract class AbstractLogger {
    @Autowired
    protected UserService userService;

    @Autowired
    protected ImportantLogService importantLogService;

    /**
     * 获取增强方法的request，以获取调用该方法的客户端ip
     *
     * @param jp 连接点
     * @return 客户端ip地址
     */
    protected String getIpAddress(JoinPoint jp){
        String ip = null;

        Object[] args = jp.getArgs();
        //获得request参数以获得请求的客户端ip
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) arg;
                ip = ShiroUtils.getClientIpAddress(request);
            }
        }
        return ip;
    }
}
