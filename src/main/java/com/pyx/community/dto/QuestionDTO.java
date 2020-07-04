package com.pyx.community.dto;

import com.pyx.community.model.User;
import lombok.Data;

//新建一个传输层类，加上user对象。
//不在Question中直接添加，因为Question是和数据库一一对应的
@Data
public class QuestionDTO {
    private Integer id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private String tag;
    private User user;
}
