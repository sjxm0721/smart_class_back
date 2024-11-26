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

    private Long testId;

    private Long studentId;

    private String studentName;

    private Long classId;

    private String className;

    private Long schoolId;

    private String schoolName;

    private String phone;

    private Long deviceId;

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
