package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SchoolDTO implements Serializable {

    private Long schoolId;

    private String schoolName;

    private String address;

    private String pic;

}
