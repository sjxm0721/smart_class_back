package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName subject
 */
@TableName(value ="subject")
@Data
public class Subject implements Serializable {
    /**
     * 课程id
     */
    @TableId
    private Long id;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 简介
     */
    private String brief;

    /**
     * 教师id
     */
    private Long teacherId;

    /**
     * 课程开始时间
     */
    private Date startTime;

    /**
     * 课程结束时间
     */
    private Date endTime;

    /**
     * 测试时间
     */
    private Date createTime;

    /**
     * 最后修改时间
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
     * 开设班级id
     */
    private Long classId;

    @TableField(exist = false)
    private String className;

    @TableField(exist = false)
    private String teacherName;

    /**
     * 封面资源
     */
    private String pic;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}