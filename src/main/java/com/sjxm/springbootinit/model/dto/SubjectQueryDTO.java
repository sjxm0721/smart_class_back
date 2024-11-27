package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/27
 * @Description:
 */
@Data
public class SubjectQueryDTO implements Serializable {
    private Long studentId;

    private Long teacherId;

    private String input;

}
