package com.sjxm.springbootinit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountVO implements Serializable {
    private Long accountId;

    private String userId;

    private String name;

    private Integer auth;//权限

    private  Long schoolId;

    private Long classId;

    private String phone;

    private String email;

    private String avatar;

    private String token;
}
