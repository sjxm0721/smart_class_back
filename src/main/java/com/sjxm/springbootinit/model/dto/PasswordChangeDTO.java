package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordChangeDTO implements Serializable {

    private Integer accountId;

    private String newPassword;

    private String oldPassword;
}
