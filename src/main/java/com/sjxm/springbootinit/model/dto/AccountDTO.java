package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountDTO implements Serializable {
    private String userId;
    private String password;
}
