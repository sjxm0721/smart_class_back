package com.sjxm.springbootinit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SightResultVO implements Serializable {

    private Integer testId;

    private Integer studentId;

    private String studentName;

    private Integer classId;

    private String className;

    private Integer schoolId;

    private String schoolName;

    private String phone;

    private Integer deviceId;

    private String deviceName;

    private  Double result;

    private Integer level;

    private LocalDateTime testTime;

    private String advice;

    private LocalDateTime updateTime;

    private String updateUser;

    private LocalDateTime createTime;

    private String createUser;
}
