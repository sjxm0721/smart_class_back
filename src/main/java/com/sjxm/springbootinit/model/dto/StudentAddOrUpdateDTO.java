package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudentAddOrUpdateDTO implements Serializable {

    private Long accountId;

    private String name;

    private String userId;

    private Long schoolId;

    private Long classId;

    private String phone;

    private String email;

}
