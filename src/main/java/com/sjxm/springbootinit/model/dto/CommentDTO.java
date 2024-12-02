package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/2
 * @Description:
 */

@Data
public class CommentDTO implements Serializable{
    private Long id;
    private Long score;
    private String comment;
}
