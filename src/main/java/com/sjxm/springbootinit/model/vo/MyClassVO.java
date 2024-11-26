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
    private Long classId;

    private String className;

    private Long schoolId;

    private String schoolName;

    private Long teacherId;

    private String teacherName;

    private String phone;

    private Integer deviceNum;

    private Integer studentNum;

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;
}
