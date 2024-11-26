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
 * @TableName class
 */
@TableName(value ="class")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Class implements Serializable {
    /**
     * 主键，班级号
     */
    @TableId(type = IdType.AUTO)
    private Long classId;

    /**
     * 班级名
     */
    private String className;

    /**
     * 所属学校
     */
    private Long schoolId;

    /**
     * 班主任id
     */
    private Long teacherId;

    /**
     * 设备数量
     */
    private Integer deviceNum;

    /**
     * 学生数量
     */
    private Integer studentNum;

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