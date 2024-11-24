package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountAddOrUpdateDTO implements Serializable {

    private Integer accountId;

    private String userId;

    private String name;

    private String phone;

    private String email;

    private String avatar;

    private Integer schoolId;

    private String schoolName;

    private Long classId;

    private String className;

    private Integer auth;
}
