package com.comm.community.controller;

import com.comm.community.dto.CommentDTO;
import com.comm.community.dto.QuestionDTO;
import com.comm.community.enums.CommentTypeEnum;
import com.comm.community.model.Question;
import com.comm.community.service.CommentService;
import com.comm.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model//用于传到页面上
    ){
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestion = questionService.selectRelated(questionDTO);//如果传id，还需要再次获取，de相当于再次获取id，通过id拿到user的tag
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestion", relatedQuestion);//相关话题
        return "question";
    }
}
