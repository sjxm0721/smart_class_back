package com.sjxm.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjxm.springbootinit.model.entity.*;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【result】的数据库操作Mapper
* @createDate 2024-11-18 19:40:50
* @Entity com.sjxm.springbootinit.model.entity.Result
*/
public interface ResultMapper extends BaseMapper<Result> {

    @Select("select student_id,count(student_id) as student_used_num from result where device_id=#{deviceId} group by student_id")
    List<StudentTestNumber> getStudentUserDevice2(Device device);

    @Select("select device_id,count(device_id) as device_used_num from result where student_id=#{studentId} group by device_id")
    List<DeviceTestNumber> getStudentUsedDevice(Student student);
}




