package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.HomeWorkPageDTO;
import com.sjxm.springbootinit.model.dto.HomeworkAddDTO;
import com.sjxm.springbootinit.model.entity.Homework;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【homework】的数据库操作Service
* @createDate 2024-11-26 11:12:46
*/
public interface HomeworkService extends IService<Homework> {


    List<Homework> listByTorSID(HomeWorkPageDTO homeWorkPageDTO);

    Homework info(Long homeworkId);

    void add(HomeworkAddDTO homeworkAddDTO);
}
