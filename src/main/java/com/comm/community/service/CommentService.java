package com.comm.community.service;

import com.comm.community.dto.CommentDTO;
import com.comm.community.enums.CommentTypeEnum;
import com.comm.community.enums.NotificationEnum;
import com.comm.community.enums.NotificationStatusEnum;
import com.comm.community.exception.CustomizeErrorCode;
import com.comm.community.exception.CustomizeException;
import com.comm.community.mapper.*;
import com.comm.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired(required = false)
    private CommentMapper commentMapper;
    @Autowired(required = false)
    private QuestionMapper questionMapper;
    @Autowired(required = false)
    private QuestionExtMapper questionExtMapper;
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private CommentExtMapper commentExtMapper;
    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    @Transactional//事务
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if(comment.getType() == null || CommentTypeEnum.isExist(comment.getType())){
            //throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType().equals(CommentTypeEnum.COMMENT.getType())){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            //回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());//parentId  P49,回顾回复评论的逻辑，会比较清晰
            if (question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);

            //二级评论数量加1
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);//评论的二级评论数量加1
            commentExtMapper.incCommentCount(parentComment);

            //评论通知//创建通知
            createNotify(comment, dbComment.getCommentor(), commentator.getName(), question.getTitle(), NotificationEnum.REPLY_COMMENT, question.getId());
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0);//commentCount=null的原因：insert的时候没有传commentCount导致mommentCount被覆盖成null
            //使用事务
            commentMapper.insert(comment);
            question.setCommentCount(1);//评论数加1
            questionExtMapper.incCommentCount(question);

            //问题通知//创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationEnum.REPLY_QUESTION, question.getId());
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationEnum notificationType, Long outerId) {//传outerId的原因：无论什么时候都需要跳转到question //评论的时候parentId可能指向一级评论的id，所以需要抽出来
        if (receiver == comment.getCommentor()){//接收通知的人和触发通知的人同一个，就不应该通知
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentor());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        //首先在questionMapper中查找
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());//这一行是因为只用上一行有一个问题：此时parentID是不对的，因为存在一个状态，当type=不同情况的时候才是问题，question下面的评论
        commentExample.setOrderByClause("gmt_create desc");//按时间先后排序提出的问题
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        //然后根据这个list查询评论的信息，如果评论很多的话会很慢，所以使用Java8流
        if (comments.size() == 0){
            return new ArrayList<>();
        }
        //使用lambda获取去重的评论者
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentor()).collect(Collectors.toSet());//P42

        List<Long> userIDs = new ArrayList();
        userIDs.addAll(commentators);

        //获取评论人并转换为Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIDs);//此时拿到了所有的user
        List<User> users = userMapper.selectByExample(userExample);

        Map<Long,User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));//因为在遍历了comment中再遍历user以匹配userID时间复杂度是n平方，所以使用Java流

        //转换comment为commentDTO
        //匹配userID
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {//复习一下容器类的用法
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentor()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
