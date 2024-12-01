package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Homework implements Serializable {
    /**
     * Id 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 可见时间
     */
    private Date sightedTime;

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
    @TableLogic
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
     * 课程id
     */
    private Long subjectId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Integer totalNum;

    @TableField(exist = false)
    private Integer curNum;

    @TableField(exist = false)
    private String unCompleteStudentList;

    @TableField(exist = false)
    private Long classId;

    @TableField(exist = false)
    private String className;

    @TableField(exist = false)
    private Long teacherId;

    @TableField(exist = false)
    private String teacherName;

    @TableField(exist = false)
    private String subjectName;

    /**
     * 是否已提交
     */
    @TableField(exist = false)
    private Integer sstatus = 0;


}