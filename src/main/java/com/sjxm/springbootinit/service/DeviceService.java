package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.DeviceAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.DevicePageQueryDTO;
import com.sjxm.springbootinit.model.entity.Device;
import com.sjxm.springbootinit.result.PageResult;

import java.io.InputStream;

/**
* @author sijixiamu
* @description 针对表【device】的数据库操作Service
* @createDate 2024-11-18 19:40:44
*/
public interface DeviceService extends IService<Device> {

    PageResult myPage(DevicePageQueryDTO devicePageQueryDTO);

    void add(DeviceAddOrUpdateDTO deviceAddOrUpdateDTO);

    void myUpdate(DeviceAddOrUpdateDTO deviceAddOrUpdateDTO);

    void clearBindWithClass(Long deviceId);

    void delete(Long deviceId);

    void importDevices(InputStream inputStream, Long schoolId);
}
