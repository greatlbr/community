package com.comm.community.dto;

import com.comm.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {//丰富了一个对象，有user的模型
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
    //真正编程实战中也是这样用的，即使用DTO
}
