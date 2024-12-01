package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/1
 * @Description:
 */
@Data
public class ResourceAddDTO implements Serializable {
    private Long subjectId;

    private String name;

    private String resources;

    private String brief;
}
