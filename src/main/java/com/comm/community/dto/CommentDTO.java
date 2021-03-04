package com.comm.community.dto;

import com.comm.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;//评论所属的问题ID
    private Integer type;//父类类型，比如1表示这是一级回复，2是二级回复
    private Long commentor;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private Integer commentCount;//二级评论数量
    private String content;
    private User user;
}
