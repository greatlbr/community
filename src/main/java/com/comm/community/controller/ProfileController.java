package com.comm.community.controller;

import com.comm.community.dto.PaginationDTO;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.User;
import com.comm.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size){

        User user = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            //-->深入理解机制以后可以用redis
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    //在数据库查找是否有token记录，若有就把user放到session里,这样前端就能通过页面级的数据判断是否展示”我“还是”登陆“
                    user = userMapper.findByToken(token);//User user = userMapper.findByToken(token);//可以使用拦截器？，拦截器意思好像是没登陆就不能访问其他的？必须登陆？
                    if (user != null) {
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
            //<--可以用redis
        }

        if (user == null)return "redirect:/";

        //本来要传一个对象到前端，现在用字符串代替一下，后面的时候再封装?
        if ("questions".equals(action)){
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
        }else if ("replies".equals(action)){
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
        }
        PaginationDTO paginationDTO = questionService.list(user.getId(),page,size);
        model.addAttribute("pagination", paginationDTO);
        return "profile";
    }
}
