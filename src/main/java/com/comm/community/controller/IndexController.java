package com.comm.community.controller;

import com.comm.community.cache.HotTagCache;
import com.comm.community.dto.PaginationDTO;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.User;
import com.comm.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTagCache hotTagCache;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "tag", required = false) String tag
                        ){
        //通过model可以把数据写到前端
        PaginationDTO pagination = questionService.list(search,tag, page, size);//questionMapper其实是针对question这张表的，并不是依赖user表的，所以不能返回user的questionDTO，所以需要新的模型service
        List<String> tags = hotTagCache.getHots();
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        model.addAttribute("tags", tags);//回显
        model.addAttribute("tag", tag);
        return "index";
    }
}
