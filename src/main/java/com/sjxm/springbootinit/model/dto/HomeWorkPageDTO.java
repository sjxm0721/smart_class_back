package com.sjxm.springbootinit.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/20
 * @Description:
 */
@Data
@Builder
public class HomeWorkPageDTO implements Serializable {


    private Long teacherId;//教师id

    private String input;//查询关键字


}
