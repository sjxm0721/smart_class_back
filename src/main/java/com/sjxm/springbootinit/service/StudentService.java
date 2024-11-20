package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.StudentAddOrUpdateDTO;
import com.sjxm.springbootinit.model.entity.Student;
import com.sjxm.springbootinit.model.vo.StudentNumberAndSightVO;
import com.sjxm.springbootinit.model.vo.StudentVO;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【student】的数据库操作Service
* @createDate 2024-11-18 19:41:01
*/
public interface StudentService extends IService<Student> {

    StudentVO info(Integer studentId);

    void add(StudentAddOrUpdateDTO studentAddOrUpdateDTO);

    void myUpdate(StudentAddOrUpdateDTO studentAddOrUpdateDTO);

    void delete(List<Integer> ids);

    StudentNumberAndSightVO studentNumberAndSight(Integer schoolId, Integer classId);
}
