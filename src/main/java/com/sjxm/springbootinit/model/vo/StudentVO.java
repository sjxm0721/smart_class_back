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
public class StudentVO implements Serializable {

    private Integer studentId;

    private String studentIdNumber;

    private String studentName;

    private Integer studentAge;

    private Integer studentSex;

    private String phone;

    private String email;

    private Integer classId;

    private Integer schoolId;

    private Integer shortSighted;

    private Double ssValue;

    private Integer testNum;

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;
}
