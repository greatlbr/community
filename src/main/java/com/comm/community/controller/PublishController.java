package com.comm.community.controller;

import com.comm.community.cache.TagCache;
import com.comm.community.dto.QuestionDTO;
import com.comm.community.model.Question;
import com.comm.community.model.User;
import com.comm.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model){
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());//相当于是修改的时候   //第一次创建的时候,修改的时候,创建的时候返回错误信息的时候，都需要有这个tag的属性.

        return "publish";
    }

    //@GetMapping("/publish}")
    @RequestMapping(value = "/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());//相当于第一次创建的时候
        return "publish";
    }//统一使用Get请求来渲染页面，使用Post处理请求

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,

            HttpServletRequest request,
            Model model
    ){
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());//点击提交以后如果有空的就跳转回了publish，所以也要加上这个tags(即相当于创建的时候返回错误信息的时候)

        //一般验证是由页面处理，这里为了练习服务端就写在了服务端处理,但前端可能绕过验证，所以前后端都需要验证
        if (title == null || title == ""){
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (description == null || description == ""){
            model.addAttribute("error", "问题描述不能为空");
            return "publish";
        }
        if (title == null || title == ""){
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        String invalid = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(invalid)){
            model.addAttribute("error","输入非法标签");
            return "publish";
        }


        User user = (User) request.getSession().getAttribute("user");

        if (user == null){
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());

        question.setId(id);

        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}
