package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.SightResultDTO;
import com.sjxm.springbootinit.model.entity.Result;
import com.sjxm.springbootinit.model.vo.SightResultVO;

import java.util.List;

/**
* @author sijixiamu
* @description 针对表【result】的数据库操作Service
* @createDate 2024-11-18 19:40:50
*/
public interface ResultService extends IService<Result> {

    List<SightResultVO> info(Long classId, Long schoolId, String select, String input);

    List<Integer> resultStatus(Long schoolId, Long classId, String timeStart, String timeEnd);

    Long addResult(SightResultDTO sightResultDTO);
}
