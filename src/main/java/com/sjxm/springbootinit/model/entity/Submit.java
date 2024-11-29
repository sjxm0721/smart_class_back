package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName submit
 */
@TableName(value ="submit")
@Data
public class Submit implements Serializable {
    /**
     * 作业提交表主键
     */
    @TableId
    private Long id;

    /**
     * 作业id
     */
    private Long homeworkId;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 批改时间
     */
    private Date correctTime;

    /**
     * 是否批改
0-未批改
1-已批改
     */
    private Integer isCorrected;

    /**
     * 提交作业的学生
     */
    private Long studentId;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 评价
     */
    private String comment;

    /**
     * 
     */
    private String createUser;

    /**
     * 
     */
    private Date updateUser;

    /**
     * 测试时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 作业内容
     */
    private String content;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    @TableField(exist = false)
    private String homeworkTitle;

    @TableField
    private Integer deleteFlag;
}