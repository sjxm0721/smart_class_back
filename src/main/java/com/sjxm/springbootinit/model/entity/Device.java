package com.sjxm.springbootinit.model.entity;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.sjxm.springbootinit.model.enums.DeviceTypeEnum;
import com.sjxm.springbootinit.model.vo.DeviceVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName device
 */
@TableName(value ="device")
@Data
public class Device implements Serializable {
    /**
     * 设备id

     */
    @TableId(type = IdType.AUTO)
    private Long deviceId;

    /**
     * 设备名
     */
    private String deviceName;

    /**
     * 
     */
    private Long schoolId;


    /**
     * 
     */
    private Date lastRepairTime;

    /**
     * 0表示正常 1表示故障
     */
    private Integer isFault;

    /**
     * 0表示未使用，1表示使用中
     */
    private Integer inUse;

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
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Integer type;

    public static DeviceVO obj2VO(Device device){
        DeviceVO deviceVO = new DeviceVO();
        BeanUtil.copyProperties(device,deviceVO);
        Integer deviceType = device.getType();
        DeviceTypeEnum enumByValue = DeviceTypeEnum.getEnumByValue(deviceType);
        if (enumByValue != null) {
            deviceVO.setTypeValue(enumByValue.getText());
        }
        return deviceVO;
    }
}