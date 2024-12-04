package com.sjxm.springbootinit.model.vo;

import com.sjxm.springbootinit.model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyClassVO {
    private Long classId;

    private String className;

    private Long schoolId;

    private String schoolName;


    private String phone;

    private Long deviceNum;

    private Long studentNum;

    private Long teacherNum = 0l;

    private List<Account> teacherList = new ArrayList<>();

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;
}
