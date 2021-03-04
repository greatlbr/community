package com.comm.community.controller;

import com.comm.community.dto.NotificationDTO;
import com.comm.community.dto.PaginationDTO;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.Notification;
import com.comm.community.model.User;
import com.comm.community.service.NotificationService;
import com.comm.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;//service是因为需要在模型放问题和回复者，所以需要构建一个DTO同时构建一个service
    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size){

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)return "redirect:/";

        //本来要传一个对象到前端，现在用字符串代替一下，后面的时候再封装?
        if ("questions".equals(action)){
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO paginationDTO = questionService.list(user.getId(),page,size);
            model.addAttribute("pagination", paginationDTO);
        }else if ("replies".equals(action)){

            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);

            //Long unreadCount = notificationService.unreadCount(user.getId());
            model.addAttribute("section", "replies");
            model.addAttribute("pagination", paginationDTO);
            //model.addAttribute("unreadCount", unreadCount);
            model.addAttribute("sectionName", "最新回复");
        }

        return "profile";
    }
}
