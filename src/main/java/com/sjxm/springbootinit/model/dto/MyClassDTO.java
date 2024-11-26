package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyClassDTO implements Serializable {

    private Long classId;

    private Long schoolId;

    private String className;
}
