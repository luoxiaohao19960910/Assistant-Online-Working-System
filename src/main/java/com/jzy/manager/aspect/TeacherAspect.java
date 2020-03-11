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
 * @ClassName TeacherAspect
 * @Author JinZhiyun
 * @Description 教师业务方法切面
 * @Date 2020/3/1 10:44
 * @Version 1.0
 **/
@Aspect
@Component
public class TeacherAspect {
    @Autowired
    private RedisOperation redisOperation;

    @Pointcut("execution(* com.jzy.service.TeacherService.insert*(..)) " +
            "|| execution(* com.jzy.service.TeacherService.update*(..))" +
            "|| execution(* com.jzy.service.TeacherService.delete*(..))")
    public void updateTeacherPoints() {
    }

    /**
     * 在助教信息被更新后，清空redis缓存
     *
     * @param jp
     */
    @AfterReturning("updateTeacherPoints()")
    public void clearRedisAfterUpdateTeacher(JoinPoint jp){
        //清缓存
        String key = RedisConstants.TEACHERS_LIKE_TEACHER_NAME_KEY;
        redisOperation.expireKey(key);
    }
}
