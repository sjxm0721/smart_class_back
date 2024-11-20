package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyClassDTO implements Serializable {

    private Integer classId;

    private Integer schoolId;

    private String className;
}
