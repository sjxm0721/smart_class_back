package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName school
 */
@TableName(value ="school")
@Data
public class School implements Serializable {
    /**
     * 学校id，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer schoolId;

    /**
     * 
     */
    private String schoolName;

    /**
     * 学校的详细地址

     */
    private String address;

    /**
     * 学校负责人的id

     */
    private Integer masterId;

    /**
     * 学校设备数
     */
    private Integer deviceNum;

    /**
     * 学校图片
     */
    private String pic;

    /**
     * 学校班级数

     */
    private Integer classNum;

    /**
     * 
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private String updateUser;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}