package com.sjxm.springbootinit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyClassVO {
    private Integer classId;

    private String className;

    private Integer schoolId;

    private String schoolName;

    private Integer teacherId;

    private String teacherName;

    private String phone;

    private Integer deviceNum;

    private Integer studentNum;

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;
}
