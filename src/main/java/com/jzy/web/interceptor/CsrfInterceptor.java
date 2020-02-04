package com.jzy.web.interceptor;

import com.jzy.manager.constant.Constants;
import com.jzy.manager.util.ShiroUtils;
import com.jzy.model.LogLevelEnum;
import com.jzy.service.ImportantLogService;
import com.jzy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName CsrfInterceptor
 * @description CSRFToken拦截器验证，拦截所有涉及修改操作的请求，防止CSRF攻击。
 * 拦截的基本方法是检查请求的参数中是否有csrftoken ,并检查这个值，是否合法有效（不为空，
 * 并且得到的参数等于cookies 中保存的值，而且还要等于session 中的值，那么就是合法的）
 * @date 2019/10/11 8:42
 **/
public class CsrfInterceptor implements HandlerInterceptor {
    private final static Logger logger = LogManager.getLogger(CsrfInterceptor.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ImportantLogService importantLogService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String keyFromRequestParam = request.getParameter(Constants.CSRF_NUMBER);
        String keyFromCookies = "";
        boolean result = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                String name = cookies[i].getName();
                if (Constants.CSRF_NUMBER.equals(name)) {
                    keyFromCookies = cookies[i].getValue();
                }
            }
        }

        if ((keyFromRequestParam != null && keyFromRequestParam.length() > 0 &&
                keyFromRequestParam.equals(keyFromCookies) &&
                keyFromRequestParam.equals((String) request.getSession().getAttribute(Constants.CSRF_NUMBER)))) {
            result = true;
        } else {
            String ip = ShiroUtils.getClientIpAddress(request);
            String msg = "可疑CSRF请求！" + "from ip: " + ip;
            logger.error(msg);
            importantLogService.saveImportantLog(msg, LogLevelEnum.ERROR, userService.getSessionUserInfo(), ip);
            request.getRequestDispatcher("/400").forward(request, response);
        }

        return result;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

