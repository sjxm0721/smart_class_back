package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName homework
 */
@TableName(value ="homework")
@Data
@Builder
public class Homework implements Serializable {
    /**
     * Id 主键
     */
    @TableId
    private Long id;

    /**
     * 作业布置老师id
     */
    private Long teacherId;

    /**
     * 是否可见

     */
    private Integer isSighted;

    /**
     * 可见时间
     */
    private Date sightedTime;

    /**
     * 
     */
    private Long classId;

    /**
     * 
     */
    private String studentIdList;

    /**
     * 0-布置给全班
1-布置给个人
     */
    private Integer type;

    /**
     * 需要提交总数
     */
    private Integer totalNum;

    /**
     * 当前提交数
     */
    private Integer curNum;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private String createUser;

    /**
     * 
     */
    private String updateUser;

    /**
     * 
     */
    private Integer deleteFlag;

    /**
     * 具体内容
     */
    private String content;

    /**
     * 资源url，可供下载
     */
    private String resources;

    private Date completeTime;

    private String title;

    @TableField(exist = false)
    private String target;

    private String completedStudentId;

    @TableField(exist = false)
    private String studentCompleted;

    @TableField(exist = false)
    private String studentNotCompleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}