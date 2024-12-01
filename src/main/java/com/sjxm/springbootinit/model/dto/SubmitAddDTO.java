package com.sjxm.springbootinit.model.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/1
 * @Description:
 */
@Data
public class SubmitAddDTO implements Serializable {

    private Long homeworkId;

    private Long studentId;

    private String content;

    private List<String> resources;


}
