package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.SchoolPageQueryDTO;
import com.sjxm.springbootinit.model.entity.School;
import com.sjxm.springbootinit.model.vo.SchoolVO;
import com.sjxm.springbootinit.result.PageResult;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【school】的数据库操作Service
* @createDate 2024-11-18 19:40:56
*/
public interface SchoolService extends IService<School> {

    PageResult pageQuery(SchoolPageQueryDTO schoolPageQueryDTO);

    void delete(Long schoolId);

    SchoolVO searchBySchoolId(Long schoolId);

    List<SchoolVO> myList();
}
