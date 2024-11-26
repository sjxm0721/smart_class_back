package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DevicePageQueryDTO implements Serializable {
    private Long schoolId;

    private Integer inUse;

    private String input;

    private Integer currentPage;

    private Integer pageSize;
}
