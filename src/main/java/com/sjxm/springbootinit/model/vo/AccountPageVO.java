package com.sjxm.springbootinit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountPageVO implements Serializable {

    private Long accountId;

    private String userId;

    private String name;

    private String phone;

    private String avatar;

    private Integer auth;

    private String authValue;

    private Long schoolId;

    private String schoolName;

    private Long classId;

    private String className;

    private String email;

    private Integer status;

    private String statusValue;

    private LocalDateTime createTime;

    private Integer createUser;

    private LocalDateTime updateTime;

    private Integer updateUser;


}
