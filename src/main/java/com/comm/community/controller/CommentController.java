package com.comm.community.controller;

import com.comm.community.dto.CommentCreateDTO;
import com.comm.community.dto.CommentDTO;
import com.comm.community.dto.ResultDTO;
import com.comm.community.enums.CommentTypeEnum;
import com.comm.community.exception.CustomizeErrorCode;
import com.comm.community.model.Comment;
import com.comm.community.model.User;
import com.comm.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        //验证
        //if (commentCreateDTO == null || commentCreateDTO.getContent()==null || commentCreateDTO.getContent() == ""){
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentor(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment,user);
        return ResultDTO.okOf();//ResultDTO.okOf()进一步集成了封装,因为是评论，所以不需要返回值，只返回OK
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List> comments(@PathVariable(name = "id") Long id){//拿到子评论的id
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);//但拿到的是questionId，但questionID只有type不一样，所以只要改变type就可以了
        return ResultDTO.okOf(commentDTOS);
    }
}
