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
public class SchoolVO {
    private Long schoolId;

    private String schoolName;

    private String address;

    private String pic;

    private Long deviceNum;

    private Long classNum;

    private Long teacherNum;

    private Long studentNum;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;
}
