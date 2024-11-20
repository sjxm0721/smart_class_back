package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceAddOrUpdateDTO implements Serializable {

    private Integer deviceId;

    private String deviceName;

    private Integer schoolId;

    private Integer classId;

    private String lastRepairTime;

    private Integer isFault;
}
