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

    private Long masterId;

    private String phone;

    private String masterName;

    private Integer deviceNum;

    private String pic;

    private Integer classNum;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;
}
