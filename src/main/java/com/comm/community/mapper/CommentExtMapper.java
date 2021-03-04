package com.comm.community.mapper;

import com.comm.community.model.Comment;
import com.comm.community.model.CommentExample;
import com.comm.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
//p45
public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}