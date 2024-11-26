package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudentAddOrUpdateDTO implements Serializable {

    private Long studentId;

    private String studentName;

    private String studentIdNumber;

    private Long schoolId;

    private Long classId;

    private Integer studentAge;

    private Integer studentSex;

    private String phone;

    private String email;

    private Integer shortSighted;

    private Double ssValue;
}
