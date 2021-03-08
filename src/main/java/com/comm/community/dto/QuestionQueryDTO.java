package com.comm.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private String tag;
    private String search;
    private Integer page;
    private Integer size;
}
