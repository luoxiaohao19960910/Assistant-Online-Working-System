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
 * @ClassName ClassAspect
 * @Author JinZhiyun
 * @Description 班级业务方法切面
 * @Date 2020/3/1 10:06
 * @Version 1.0
 **/
@Aspect
@Component
public class ClassAspect {
    @Autowired
    private RedisOperation redisOperation;

    @Pointcut("(execution(* com.jzy.service.ClassService.insert*(..)) " +
            "|| execution(* com.jzy.service.ClassService.update*(..))" +
            "|| execution(* com.jzy.service.ClassService.delete*(..)))" +
            "&& !execution(* com.jzy.service.ClassService.deleteCurrentClassSeason(..))"+
            "&& !execution(* com.jzy.service.ClassService.updateCurrentClassSeason(..))")
    public void updateClassPoints() {
    }

    /**
     * 在班级信息被更新后，清空redis缓存
     *
     * @param jp
     */
    @AfterReturning("updateClassPoints()")
    public void clearRedisAfterUpdateClass(JoinPoint jp){
        //清缓存
        String key = RedisConstants.CLASSES_LIKE_CLASS_ID_KEY;
        redisOperation.expireKey(key);
    }
}
