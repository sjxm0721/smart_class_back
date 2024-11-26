package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.MyClassDTO;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.vo.MyClassVO;
import com.sjxm.springbootinit.result.PageResult;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【class】的数据库操作Service
* @createDate 2024-11-18 19:40:39
*/
public interface ClassService extends IService<Class> {

    List<MyClassVO> info(Long classId);

    List<MyClassVO> myList(Long schoolId);

    PageResult myPage(Long schoolId, String input, Integer currentPage, Integer pageSize);

    void add(MyClassDTO myClassDTO);

    void myUpdate(MyClassDTO myClassDTO);

    void delete(Long schoolId, Long classId);

    void clearTeacher(Long classId);
}
