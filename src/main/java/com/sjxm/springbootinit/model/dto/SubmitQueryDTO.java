package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/22
 * @Description:
 */
@Data
public class SubmitQueryDTO implements Serializable {

    private Long teacherId;

    private Long studentId;

    private Integer status;

    private String input;

}
