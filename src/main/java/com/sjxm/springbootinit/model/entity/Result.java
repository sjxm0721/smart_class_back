package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName result
 */
@TableName(value ="result")
@Data
public class Result implements Serializable {
    /**
     * 测试id

     */
    @TableId(type = IdType.AUTO)
    private Long testId;

    /**
     * 
     */
    private Long studentId;

    /**
     * 
     */
    private Long deviceId;

    /**
     * 
     */
    private Long classId;

    /**
     * 
     */
    private Long schoolId;

    /**
     * 
     */
    private Double result;

    /**
     * 0 正常，1轻度近视，2中度近视，3重度近视
     */
    private Integer level;

    /**
     * 测试时间
     */
    private Date testTime;

    /**
     * 
     */
    private String advice;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private String createUser;

    /**
     * 
     */
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}