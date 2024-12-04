package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/4
 * @Description:
 */
@Data
public class BorrowSubmitDTO implements Serializable {

    private Long accountId;

    private Long deviceId;

    private String startTime;

    private String endTime;

    private String description;

}
