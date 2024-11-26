package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountPageQueryDTO implements Serializable {

    private Integer auth;//账号权限

    private String input;//查询条件

    private Integer currentPage;//当前页码

    private Integer pageSize;//每页展示数据条数

    private Long schoolId;//校管理员有schoolId
}
