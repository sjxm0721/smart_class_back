package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudentAddOrUpdateDTO implements Serializable {

    private Integer studentId;

    private String studentName;

    private String studentIdNumber;

    private Integer schoolId;

    private Integer classId;

    private Integer studentAge;

    private Integer studentSex;

    private String phone;

    private String email;

    private Integer shortSighted;

    private Double ssValue;
}
