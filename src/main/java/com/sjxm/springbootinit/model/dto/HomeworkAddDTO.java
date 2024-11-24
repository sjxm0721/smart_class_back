package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/24
 * @Description:
 */
@Data
public class HomeworkAddDTO implements Serializable {

    private Long id;

    private String content;

    private String completeTime;

    private List<String> resources;

    private List<Long> studentIdList;

    private String sightedTime;

    private String title;

    private Integer type;

}
