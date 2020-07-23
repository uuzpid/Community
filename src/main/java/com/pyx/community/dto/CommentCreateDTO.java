package com.pyx.community.dto;

import lombok.Data;

/**
 * 接收json
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
