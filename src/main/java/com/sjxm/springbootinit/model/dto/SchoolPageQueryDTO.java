package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SchoolPageQueryDTO implements Serializable {

    private Integer currentPage;//当前页码

    private Integer pageSize;//每页展示条数

    private String input;//用户查询关键字
}
