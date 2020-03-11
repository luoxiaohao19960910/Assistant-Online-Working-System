package com.jzy.manager.aspect;

import com.jzy.manager.constant.RedisConstants;
import com.jzy.service.RedisOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName AssistantAspect
 * @Author JinZhiyun
 * @Description 助教业务方法切面
 * @Date 2020/3/1 10:40
 * @Version 1.0
 **/
@Aspect
@Component
public class AssistantAspect {
    @Autowired
    private RedisOperation redisOperation;

    @Pointcut("execution(* com.jzy.service.AssistantService.insert*(..)) " +
            "|| execution(* com.jzy.service.AssistantService.update*(..))" +
            "|| execution(* com.jzy.service.AssistantService.delete*(..))")
    public void updateAssistantPoints() {
    }

    /**
     * 在助教信息被更新后，清空redis缓存
     *
     * @param jp
     */
    @AfterReturning("updateAssistantPoints()")
    public void clearRedisAfterUpdateAssistant(JoinPoint jp){
        //清缓存
        String key = RedisConstants.ASSISTANTS_LIKE_ASSISTANT_NAME_KEY;
        redisOperation.expireKey(key);
    }
}
