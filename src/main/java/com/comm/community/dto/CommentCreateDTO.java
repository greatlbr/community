package com.comm.community.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {
    private  Long parentId;//评论所属的问题ID
    private String content;
    private Integer type;//父类类型，比如1表示这是一级回复，2是二级回复
}
