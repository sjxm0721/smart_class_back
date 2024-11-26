package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceAddOrUpdateDTO implements Serializable {

    private Long deviceId;

    private String deviceName;

    private Long schoolId;

    private Long classId;

    private String lastRepairTime;

    private Integer isFault;
}
