package com.sjxm.springbootinit.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.context.BaseContext;
import com.sjxm.springbootinit.model.dto.HomeWorkPageDTO;
import com.sjxm.springbootinit.model.dto.HomeworkAddDTO;
import com.sjxm.springbootinit.model.entity.Homework;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.HomeworkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/info")
    @ApiOperation("获取具体作业信息")
    public Result<Homework> info(Long homeworkId){
        Homework homework = homeworkService.info(homeworkId);
        return Result.success(homework);
    }

    @PostMapping("/add")
    @ApiOperation("布置作业")
    public Result add(@RequestBody HomeworkAddDTO homeworkAddDTO){

        homeworkService.add(homeworkAddDTO);
        return Result.success();
    }

}
