package com.sjxm.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.model.dto.HomeWorkPageDTO;
import com.sjxm.springbootinit.model.dto.HomeworkAddDTO;
import com.sjxm.springbootinit.model.entity.Homework;
import com.sjxm.springbootinit.model.entity.Submit;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.HomeworkService;
import com.sjxm.springbootinit.service.SubmitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/20
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/admin/homework")
@Api(tags = "作业相关接口")
public class HomeWorkController {


    @Resource
    private HomeworkService homeworkService;

    @Resource
    private SubmitService submitService;

    @GetMapping("/list-ts")
    @ApiOperation("获取作业列表信息")
    public Result<List<Homework>> listByTorSID(HomeWorkPageDTO homeWorkPageDTO){
        List<Homework> list = homeworkService.listByTorSID(homeWorkPageDTO);
        return Result.success(list);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除作业")
    public Result delete(Long homeworkId){
        homeworkService.removeById(homeworkId);
        return Result.success();
    }



    @PostMapping("/add")
    @ApiOperation("布置作业")
    public Result add(@RequestBody HomeworkAddDTO homeworkAddDTO){

        homeworkService.add(homeworkAddDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("获取某课程作业列表信息")
    public Result<List<Homework>> list(Long subjectId,Long studentId){
        LambdaQueryWrapper<Homework> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Homework::getSubjectId,subjectId).orderBy(true,false,Homework::getCreateTime);
        List<Homework> homeworkList = homeworkService.list(lambdaQueryWrapper);
        if(studentId!=null){
            homeworkList.forEach(homework -> buildSstatusInfo(homework,studentId));
        }
        return Result.success(homeworkList);
    }

    @GetMapping("/info")
    @ApiOperation("获取具体作业信息")
    public Result<Homework> info(Long homeworkId){
        Homework homework = homeworkService.info(homeworkId);
        return Result.success(homework);
    }

    void buildSstatusInfo(Homework homework,Long studentId){
        Long homeworkId = homework.getId();
        LambdaQueryWrapper<Submit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Submit::getHomeworkId,homeworkId).eq(Submit::getStudentId,studentId);
        Submit submit = submitService.getOne(lambdaQueryWrapper);
        if(submit!=null){
            homework.setSstatus(1);
        }
    }

}
