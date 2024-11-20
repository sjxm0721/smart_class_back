package com.sjxm.springbootinit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceVO {
    private Integer deviceId;

    private String deviceName;

    private Integer schoolId;

    private String schoolName;

    private Integer classId;

    private String className;

    private Integer testNum;

    private LocalDateTime lastRepairTime;

    private  Integer isFault;

    private Integer inUse;

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;
}
