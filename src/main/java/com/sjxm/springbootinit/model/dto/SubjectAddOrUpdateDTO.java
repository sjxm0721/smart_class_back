package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/27
 * @Description:
 */

@Data
public class SubjectAddOrUpdateDTO implements Serializable {

    private Long id;

    private Long teacherId;

    private String title;

    private String brief;

    private String pic;

    private String classId;

    private String endTime;

}
