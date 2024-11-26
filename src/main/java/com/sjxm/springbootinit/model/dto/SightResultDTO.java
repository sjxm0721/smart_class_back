package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SightResultDTO implements Serializable {

    private Long testId;

    private Double result;

    private Integer level;

    private String advice;

    private Long deviceId;

    private Long studentId;
}
