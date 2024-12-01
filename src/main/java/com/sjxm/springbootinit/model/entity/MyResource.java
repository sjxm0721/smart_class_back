package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName resource
 */
@TableName(value ="resource")
@Data
public class MyResource implements Serializable {
    /**
     * 资源id，主键
     */
    @TableId
    private Long id;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 具体的资源内容，如果有多个资源，资源与资源之间用“,“分隔开
     */
    private String resources;

    /**
     * 
     */
    private Long subjectId;

    /**
     * 测试时间
     */
    private Date createTime;

    /**
     * 
     */
    private String createUser;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 
     */
    private String updateUser;

    /**
     * 
     */
    @TableLogic
    private Integer deleteFlag;

    /**
     * 资源介绍
     */
    private String brief;

    private BigDecimal size;

    @TableField(exist = false)
    private String teacherName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}