package com.pyx.community.interceptor;

import com.pyx.community.mapper.UserMapper;
import com.pyx.community.model.User;
import com.pyx.community.model.UserExample;
import com.pyx.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) { //需要先判断是否有cookie，如果为null遍历时会报错
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {//判断cookie中是否有token名字的cookie
                    String token = cookie.getValue();//如果有，则取出token对应的值
                    /**
                     * mybatis generate
                     */
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {//如果数据库中有该用户
                        //则把这个用户放入session中
                        request.getSession().setAttribute("user", users.get(0));

                        //
                        Long unreadCount = notificationService.unreadCount(users.get(0).getId());
                        request.getSession().setAttribute("unreadMessage",unreadCount);                    }
                    break;
                }
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
