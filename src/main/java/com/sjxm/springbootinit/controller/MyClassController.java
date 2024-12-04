package com.sjxm.springbootinit.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.model.dto.MyClassDTO;
import com.sjxm.springbootinit.model.dto.TeacherBindClassDTO;
import com.sjxm.springbootinit.model.entity.Clstearelation;
import com.sjxm.springbootinit.model.vo.MyClassVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.ClstearelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sjxm.springbootinit.model.entity.Class;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/myclass")
@Api(tags = "班级管理相关接口")
public class MyClassController {

    @Autowired
    private ClassService classService;

    @Resource
    private ClstearelationService clstearelationService;

    @GetMapping("/info")
    @ApiOperation("根据id获取班级信息具体数据")
    //todo 方法好像存在问题
    public Result<List<MyClassVO>> info(Long classId){

        List<MyClassVO> list = classService.info(classId);

        return Result.success(list);
    }

    @GetMapping("/list")
    @ApiOperation("获取班级信息列表")
    public Result<List<MyClassVO>> list(Long schoolId){
        List<MyClassVO> list = classService.myList(schoolId);
        return Result.success(list);
    }


    @GetMapping("/page")
    @ApiOperation("班级信息分页查询")
    public Result<PageResult> page(Long schoolId, String input, Integer currentPage, Integer pageSize){

        PageResult page = classService.myPage(schoolId,input,currentPage,pageSize);

        return Result.success(page);
    }


    @PostMapping("/add")
    @ApiOperation("新增班级信息")
    public Result add(@RequestBody MyClassDTO myClassDTO){

        classService.add(myClassDTO);

        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改班级信息")
    public Result update(@RequestBody MyClassDTO myClassDTO){
        classService.myUpdate(myClassDTO);

        return Result.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除班级信息")
    public Result delete(Long classId){
        classService.delete(classId);

        return Result.success();
    }

    @PutMapping("/clearTeacher")
    @ApiOperation("班级解绑教师")
    public Result clearTeacher(Long classId){
        classService.clearTeacher(classId);

        return Result.success();
    }

    @GetMapping("/all-class")
    @ApiOperation("获取该教师下的所有班级")
    public Result<List<Class>> allClass(Long accountId){
        LambdaQueryWrapper<Class> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Clstearelation> clstearelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clstearelationLambdaQueryWrapper.eq(Clstearelation::getTeacherId,accountId);
        List<Clstearelation> clstearelationList = clstearelationService.list(clstearelationLambdaQueryWrapper);
        List<Long> classIds = clstearelationList.stream().map(Clstearelation::getClassId).collect(Collectors.toList());
        lambdaQueryWrapper.in(!CollUtil.isEmpty(classIds),Class::getClassId,classIds);
        List<Class> list = classService.list(lambdaQueryWrapper);
        return Result.success(list);
    }


    @PostMapping("/bind-teachers")
    @ApiOperation("关联教师")
    public Result bindTeachers(@RequestBody TeacherBindClassDTO teacherBindClassDTO){
        Long classId = teacherBindClassDTO.getClassId();
        List<Long> teacherIds = teacherBindClassDTO.getTeacherIds();
        if(CollUtil.isEmpty(teacherIds)){
            return Result.success();
        }
        LambdaQueryWrapper<Clstearelation> clstearelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clstearelationLambdaQueryWrapper.eq(Clstearelation::getClassId,classId);
        clstearelationService.remove(clstearelationLambdaQueryWrapper);
        List<Clstearelation> list = new ArrayList<>();
        for(Long teacherId:teacherIds){
            Clstearelation clstearelation = new Clstearelation();
            clstearelation.setTeacherId(teacherId);
            clstearelation.setClassId(classId);
            list.add(clstearelation);
        }
        clstearelationService.saveBatch(list);
        return Result.success();
    }
}
