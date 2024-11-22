package com.sjxm.springbootinit.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.model.dto.HomeWorkPageDTO;
import com.sjxm.springbootinit.model.entity.Homework;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.HomeworkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/list")
    @ApiOperation("获取作业列表信息")
    public Result<List<Homework>> page(HomeWorkPageDTO homeWorkPageDTO){
        LambdaQueryWrapper<Homework> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Homework::getTeacherId,homeWorkPageDTO.getTeacherId())
                .like(!StrUtil.isBlankIfStr(homeWorkPageDTO.getInput()),Homework::getTitle,homeWorkPageDTO.getInput());
        List<Homework> list = homeworkService.list(lambdaQueryWrapper);
        List<Homework> collect = list.stream().map(homework -> homeworkService.getHomeworkTarget(homework)).collect(Collectors.toList());
        return Result.success(collect);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除作业")
    public Result delete(Long homeworkId){
        homeworkService.removeById(homeworkId);
        return Result.success();
    }

}
