package com.jzy.web.interceptor;

import com.jzy.manager.constant.SessionConstants;
import com.jzy.manager.util.ShiroUtils;
import com.jzy.model.RoleEnum;
import com.jzy.model.entity.User;
import com.jzy.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName RememberMeSessionInterceptor
 * @Author JinZhiyun
 * @Description 解决"shiro记住我"会话丢失问题的拦截器
 * @Date 2020/3/11 18:46
 * @Version 1.0
 **/
public class RememberMeSessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() && subject.isRemembered() && userService.getSessionUserInfo() == null) {
            System.out.println("===============");
            //记住我且session中用户信息为空（关闭过浏览器），重新添加用户信息到当前session
            User principal = (User) subject.getPrincipal();
            if (principal != null) {
                //实体信息不为空，根据用户id更新下用户对象，防止中间该用户在其他客户端更新过用户资料信息导致当前principal是过时的错误的信息
                if (!RoleEnum.GUEST.equals(principal.getUserRole())) {
                    principal = userService.getUserById(principal.getId());
                }
                ShiroUtils.setSessionAttribute(SessionConstants.USER_INFO_SESSION_KEY, principal);
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
