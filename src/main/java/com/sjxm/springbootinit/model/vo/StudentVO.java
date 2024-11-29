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

    private Long accountId;

    private String userId;

    private String name;

    private String phone;

    private String email;

    private Long classId;

    private String className;

    private Long schoolId;

    private String schoolName;

}
