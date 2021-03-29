package com.comm.community.mapper;

import com.comm.community.dto.QuestionQueryDTO;
import com.comm.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {//通过QuestionExtMapper使用
    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);
    Integer countBySearch(QuestionQueryDTO questionQueryDTO);//搜索话题功能
    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);//直接在前端和controller判断字符串可能比较好，在mapper可能太靠后，并发量大的话浪费系统资源
}
