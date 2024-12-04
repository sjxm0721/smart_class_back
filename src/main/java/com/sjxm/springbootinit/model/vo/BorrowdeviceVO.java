package com.sjxm.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/4
 * @Description:
 */

@Data
public class BorrowdeviceVO implements Serializable {

    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long deviceId;

    private String deviceName;

    /**
     *
     */
    private Long studentId;

    private String studentName;

    /**
     *
     */
    private Date startTime;

    /**
     *
     */
    private Date endTime;

    /**
     *
     */
    private String ddescribe;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private String createUser;

    /**
     *
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

    private Integer status;

    private String statusValue;

}
