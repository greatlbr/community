package com.comm.community.controller;

import com.comm.community.dto.PaginationDTO;
import com.comm.community.dto.QuestionDTO;
import com.comm.community.mapper.QuestionMapper;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.Question;
import com.comm.community.model.User;
import com.comm.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class indexController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size
                        ){
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            //-->深入理解机制以后可以用redis
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    //在数据库查找是否有token记录，若有就把user放到session里,这样前端就能通过页面级的数据判断是否展示”我“还是”登陆“
                    User user = userMapper.findByToken(token);//可以使用拦截器？，拦截器意思好像是没登陆就不能访问其他的？必须登陆？
                    if (user != null) {
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
            //<--可以用redis
        }
        //通过model可以把数据写到前端
        PaginationDTO pagination = questionService.list(page, size);//questionMapper其实是针对question这张表的，并不是依赖user表的，所以不能返回user的questionDTO，所以需要新的模型service
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
