package com.sjxm.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjxm.springbootinit.model.entity.Submit;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【submit】的数据库操作Mapper
* @createDate 2024-11-21 18:44:22
* @Entity generator.domain.Submit
*/
public interface SubmitMapper extends BaseMapper<Submit> {

    List<Submit> myList(String input, Integer status, Long studentId, Long teacherId);
}




