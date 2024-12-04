package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/4
 * @Description:
 */
@Data
public class BorrowApproveDTO implements Serializable {

    private Long id;

    private Boolean approved;

}
