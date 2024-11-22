package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.SubmitQueryDTO;
import com.sjxm.springbootinit.model.entity.Submit;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【submit】的数据库操作Service
* @createDate 2024-11-21 18:44:22
*/
public interface SubmitService extends IService<Submit> {

    List<Submit> myList(SubmitQueryDTO submitQueryDTO);

    Submit info(Long submitId);
}
