package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName homework
 */
@TableName(value ="homework")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Homework implements Serializable {


    /**
     * Id 主键
     */
    @TableId(type = IdType.AUTO)
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
    private String classIdList;

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
     * 创建时间
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

    /**
     * 截止时间
     */
    private Date completeTime;

    /**
     * 名称
     */
    private String title;

    /**
     * 已完成学生id
     */
    private String completedStudentId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    @TableField(exist = false)
    private String target;


    @TableField(exist = false)
    private String studentCompleted;

    @TableField(exist = false)
    private String studentNotCompleted;

}