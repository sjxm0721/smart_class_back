package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.entity.Homework;

/**
* @author sijixiamu
* @description 针对表【homework】的数据库操作Service
* @createDate 2024-11-20 17:44:02
*/
public interface HomeworkService extends IService<Homework> {

    Homework getHomeworkTarget(Homework homework);

}
