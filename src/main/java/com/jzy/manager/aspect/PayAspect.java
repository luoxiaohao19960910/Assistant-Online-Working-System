package com.jzy.manager.aspect;

import com.jzy.manager.constant.ModelConstants;
import com.jzy.manager.constant.RedisConstants;
import com.jzy.model.RoleEnum;
import com.jzy.model.entity.User;
import com.jzy.model.vo.PayAnnouncement;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

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
                PayAnnouncement announcement =new PayAnnouncement();
                if (needToPushPayAnnouncement(user)) {
                    announcement = (PayAnnouncement) valueOps.get(RedisConstants.PAY_ANNOUNCEMENT_KEY);
                    announcement.setNeedToPay(true);
                    announcement.parse();
                }
                model.addAttribute(ModelConstants.PAY_ANNOUNCEMENT_MODEL_KEY, announcement);
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
        boolean userRoleNeedToJudge = RoleEnum.ASSISTANT.equals(user.getUserRole()) || RoleEnum.ASSISTANT_MASTER.equals(user.getUserRole());
        boolean hasPaid = !hashOps.hasKey(RedisConstants.PAY_ANNOUNCEMENT_USER_STATUS_KEY, user.getId().toString());
        return hasPayAnnouncement && userRoleNeedToJudge && hasPaid;
    }
}
