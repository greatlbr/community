package com.comm.community.interceptor;

import com.comm.community.mapper.UserMapper;
import com.comm.community.model.User;
import com.comm.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service//让spring管理这个UserMapper
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired(required=false)
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            //-->深入理解机制以后可以用redis
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    //在数据库查找是否有token记录，若有就把user放到session里,这样前端就能通过页面级的数据判断是否展示”我“还是”登陆“
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()//拼接各种sql
                            .andTokenEqualTo(token);//不用每次写修改底层的Mapper文件
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        request.getSession().setAttribute("user", users.get(0));
                    }
                    break;
                }
            }
            //<--可以用redis
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
