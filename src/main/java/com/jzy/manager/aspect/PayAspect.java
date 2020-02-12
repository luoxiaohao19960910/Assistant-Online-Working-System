package com.jzy.manager.aspect;

import com.jzy.manager.constant.ModelConstants;
import com.jzy.manager.constant.RedisConstants;
import com.jzy.model.RoleEnum;
import com.jzy.model.entity.User;
import com.jzy.model.vo.PayAnnouncement;
import com.jzy.model.vo.PayStatus;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PayAspect
 * @Author JinZhiyun
 * @Description 处理是否推送用户支付公告的切面
 * @Date 2020/2/11 14:51
 * @Version 1.0
 **/
@Aspect
@Component
public class PayAspect extends AbstractLogger {
    /**
     * 判断是否推送用户支付公告的切面
     */
    @Pointcut("execution(* com.jzy.web.controller.HomeController.console(..))" +
            "|| com.jzy.manager.aspect.VisitorStatisticsAspect.infoManagementPagePoint()" +
            "|| com.jzy.manager.aspect.VisitorStatisticsAspect.toolboxPagePoint()")
    public void judgeIfPushPayAnnouncementPoint() {
    }

    @Before(value = "judgeIfPushPayAnnouncementPoint()")
    public void judgeIfPushPayAnnouncement(JoinPoint joinPoint) {
        User user = userService.getSessionUserInfo();
        Object[] args = joinPoint.getArgs();
        //获得model以添加必要的公告信息
        for (Object arg : args) {
            if (arg instanceof Model) {
                Model model = (Model) arg;
                PayAnnouncement announcement = new PayAnnouncement();
                PayStatus payStatus = new PayStatus();
                if (needToPushPayAnnouncement(user)) {
                    payStatus = (PayStatus) valueOps.get(RedisConstants.getPayAnnouncementUserStatusKey(user.getId()));
                    //计时起始时间为当前服务器时间
                    payStatus.setStartDate(new Date());
                    announcement = (PayAnnouncement) valueOps.get(RedisConstants.PAY_ANNOUNCEMENT_KEY);
                    announcement.parse();
                }
                model.addAttribute(ModelConstants.PAY_ANNOUNCEMENT_MODEL_KEY, announcement);
                model.addAttribute(ModelConstants.PAY_STATUS_MODEL_KEY, payStatus);
            }
        }
    }

    /**
     * 判断输入的用户是否有必要推送支付公告
     *
     * @param user 要判断的用户
     * @return
     */
    private boolean needToPushPayAnnouncement(User user) {
        boolean hasPayAnnouncement = redisTemplate.hasKey(RedisConstants.PAY_ANNOUNCEMENT_KEY);
        if (!hasPayAnnouncement) {
            return false;
        }
        boolean userRoleNeedToJudge = RoleEnum.ASSISTANT.equals(user.getUserRole()) || RoleEnum.ASSISTANT_MASTER.equals(user.getUserRole());
        if (!userRoleNeedToJudge) {
            return false;
        }
        String userPayStatusKey = RedisConstants.getPayAnnouncementUserStatusKey(user.getId());
        if (!redisTemplate.hasKey(userPayStatusKey)) {
            //没有缓存，即第一次被推支付公告
            PayAnnouncement announcement = (PayAnnouncement) valueOps.get(RedisConstants.PAY_ANNOUNCEMENT_KEY);
            valueOps.set(userPayStatusKey, new PayStatus(announcement.getExpireTimeValueInSecondUnit()));
            redisTemplate.expire(userPayStatusKey, announcement.getExpireTimeValueInSecondUnit(), TimeUnit.SECONDS);
            return true;
        } else {
            //有缓存，判断是否已支付
            PayStatus payStatus = (PayStatus) valueOps.get(userPayStatusKey);
            if (payStatus.isNeedToPay()) {
                //未支付
                return true;
            }
            return false;
        }

    }
}
