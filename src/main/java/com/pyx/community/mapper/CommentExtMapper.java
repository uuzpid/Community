package com.pyx.community.mapper;

import com.pyx.community.model.Comment;
import com.pyx.community.model.CommentExample;
import com.pyx.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}