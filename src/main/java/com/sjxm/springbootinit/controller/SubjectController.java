package com.sjxm.springbootinit.controller;

import com.sjxm.springbootinit.model.dto.SubjectAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.SubjectQueryDTO;
import com.sjxm.springbootinit.model.entity.Subject;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/27
 * @Description:
 */
@RestController
@RequestMapping("/admin/subject")
@Api(tags="课程相关接口")
@Slf4j
public class SubjectController {


    @Resource
    private SubjectService subjectService;

    @GetMapping("/list")
    @ApiOperation("获取课程信息列表")
    public Result<List<Subject>> list(SubjectQueryDTO subjectQueryDTO){

        List<Subject> list = subjectService.myList(subjectQueryDTO);
        return Result.success(list);
    }

    @PostMapping("/add-or-update")
    @ApiOperation("添加或修改课程")
    public Result addOrUpdate(@RequestBody SubjectAddOrUpdateDTO subjectAddOrUpdateDTO){

        subjectService.addOrUpdate(subjectAddOrUpdateDTO);
        return Result.success();
    }

    @GetMapping("/info")
    @ApiOperation("获取课程详情信息")
    public Result<Subject> info(Long subjectId){
        return Result.success(subjectService.info(subjectId));
    }


    @GetMapping("/export")
    @PostMapping("导出学生课程数据")
    public void export(Long subjectId){
        
    }
}
