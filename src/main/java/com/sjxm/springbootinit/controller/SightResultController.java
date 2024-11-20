package com.sjxm.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.model.dto.SightResultDTO;
import com.sjxm.springbootinit.model.vo.SightResultVO;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.ResultService;
import com.sjxm.springbootinit.utils.DateTransferUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/sightResult")
@Api(tags = "测试结果接口")
@Slf4j
public class SightResultController {

    @Autowired
    private ResultService sightResultService;

    @GetMapping("/info")
    @ApiOperation("获取结果信息列表")
    public Result<List<SightResultVO>> info(Integer classId, Integer schoolId, String select, String input){

        List<SightResultVO> list = sightResultService.info(classId,schoolId,select,input);

        return Result.success(list);
    }

    @PutMapping("/update")
    @ApiOperation("修改结果信息")
    public Result update(@RequestBody SightResultDTO sightResultDTO){
        com.sjxm.springbootinit.model.entity.Result sightResult = new com.sjxm.springbootinit.model.entity.Result();
        BeanUtil.copyProperties(sightResultDTO,sightResult);
        sightResultService.updateById(sightResult);
        return Result.success();
    }


    @GetMapping("/yesResultTime")
    @ApiOperation("获取昨日测试次数")
    public Result<Long> yesResultTime(Integer schoolId,Integer classId,String timeStart,String timeEnd){

        Date startTime = DateTransferUtil.transfer(LocalDateTime.parse(timeStart.replace(" ","T")));
        Date endTime = DateTransferUtil.transfer(LocalDateTime.parse(timeEnd.replace(" ","T")));
        LambdaQueryWrapper<com.sjxm.springbootinit.model.entity.Result> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(schoolId!=null, com.sjxm.springbootinit.model.entity.Result::getSchoolId,schoolId)
                .eq(classId!=null, com.sjxm.springbootinit.model.entity.Result::getClassId,classId)
                .between(startTime!=null&&endTime!=null, com.sjxm.springbootinit.model.entity.Result::getTestTime,startTime,endTime);
        long times = sightResultService.count(lambdaQueryWrapper);

        return Result.success(times);
    }


    @GetMapping("/resultStatus")
    @ApiOperation("获取测试结果分布")
    public Result<List<Integer>> resultStatus(Integer schoolId,Integer classId,String timeStart,String timeEnd){

        List<Integer> list = sightResultService.resultStatus(schoolId,classId,timeStart,timeEnd);

        return Result.success(list);
    }

    @PostMapping("/add")
    @ApiOperation("增加测试结果")
    public Result<Integer> addResult(@RequestBody SightResultDTO sightResultDTO){
        Integer testId = sightResultService.addResult(sightResultDTO);
        return Result.success(testId);
    }
}
