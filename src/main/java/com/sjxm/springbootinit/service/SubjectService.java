package com.sjxm.springbootinit.service;

import com.sjxm.springbootinit.model.dto.SubjectAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.SubjectQueryDTO;
import com.sjxm.springbootinit.model.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author sijixiamu
* @description 针对表【subject】的数据库操作Service
* @createDate 2024-11-25 17:20:03
*/
public interface SubjectService extends IService<Subject> {

    List<Subject> myList(SubjectQueryDTO subjectQueryDTO);

    void addOrUpdate(SubjectAddOrUpdateDTO subjectAddOrUpdateDTO);

    Subject info(Long subjectId);

    void export(Long subjectId, HttpServletResponse response);
}
