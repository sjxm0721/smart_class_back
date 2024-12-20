package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.entity.Chapter;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【chapter】的数据库操作Service
* @createDate 2024-11-26 18:07:37
*/
public interface ChapterService extends IService<Chapter> {

    List<Chapter> myList(Long subjectId);
}
