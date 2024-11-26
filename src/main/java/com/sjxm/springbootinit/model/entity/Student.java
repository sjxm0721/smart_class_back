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
 * @TableName student
 */
@TableName(value ="student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {
    /**
     * 学生id，主键
     */
    @TableId(type = IdType.AUTO)
    private Long studentId;

    /**
     * 学号
     */
    private String studentIdNumber;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生年龄
     */
    private Integer studentAge;

    /**
     * 学生性别：0男 1女
     */
    private Integer studentSex;

    /**
     * 学生或其家长联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 
     */
    private Long classId;

    /**
     * 
     */
    private Long schoolId;

    /**
     * 是否近视，0不近视，1近视
     */
    private Integer shortSighted;

    /**
     * 近视度数
     */
    private Double ssValue;

    /**
     * 参与测试次数

     */
    private Integer testNum;

    /**
     * 创建时间
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
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}