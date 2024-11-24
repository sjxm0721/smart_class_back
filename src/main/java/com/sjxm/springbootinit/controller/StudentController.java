package com.sjxm.springbootinit.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.NoEnoughAuthException;
import com.sjxm.springbootinit.model.dto.StudentAddOrUpdateDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.entity.Student;
import com.sjxm.springbootinit.model.vo.StudentNumberAndSightVO;
import com.sjxm.springbootinit.model.vo.StudentVO;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "学生管理相关接口")
@RestController
@RequestMapping("/admin/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Resource
    private AccountService accountService;

    @Resource
    private ClassService classService;

    @GetMapping("/list")
    @ApiOperation("获取学生信息列表")
    public Result<List<StudentVO>> list(Integer classId,String studentName,String studentIdNumber){

        LambdaQueryWrapper<Student> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(classId!=null,Student::getClassId,classId)
                .like(!StrUtil.isBlankIfStr(studentName),Student::getStudentName,studentName)
                .eq(!StrUtil.isBlankIfStr(studentIdNumber),Student::getStudentIdNumber,studentIdNumber)
                .orderBy(true,false,Student::getUpdateTime);
        List<Student> list =  studentService.list(lambdaQueryWrapper);
        List<StudentVO> result = new ArrayList<>();
        for (Student student : list) {
            StudentVO studentVO = new StudentVO();
            BeanUtils.copyProperties(student,studentVO);
            result.add(studentVO);
        }
        return Result.success(result);
    }


    @GetMapping("/info")
    @ApiOperation("根据id获取学生信息")
    public Result<StudentVO> info(Integer studentId){
        StudentVO studentVO = studentService.info(studentId);
        return Result.success(studentVO);
    }

    @PostMapping("/add")
    @ApiOperation("添加学生")
    public Result add(@RequestBody StudentAddOrUpdateDTO studentAddOrUpdateDTO){

        studentService.add(studentAddOrUpdateDTO);

        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改学生")
    public Result update(@RequestBody StudentAddOrUpdateDTO studentAddOrUpdateDTO){

        studentService.myUpdate(studentAddOrUpdateDTO);

        return Result.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除学生")
    public Result delete(@RequestParam List<Integer> ids){

        studentService.delete(ids);
        return Result.success();
    }


    @GetMapping("/studentNumberAndSight")
    @ApiOperation("获取学生数量")
    public Result<StudentNumberAndSightVO> studentNumberAndSight(Integer schoolId, Integer classId){
        StudentNumberAndSightVO studentNumberAndSightVO = studentService.studentNumberAndSight(schoolId,classId);

        return Result.success(studentNumberAndSightVO);
    }

    @GetMapping("/all-stu")
    @ApiOperation("获取此教师所有的学生")
    public Result<List<Student>> allStu(Integer accountId){
        Account account = accountService.getById(accountId);
        if(account.getAuth()==0){
            throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
        }
        LambdaQueryWrapper<Class> classLambdaQueryWrapper = new LambdaQueryWrapper();
        classLambdaQueryWrapper.eq(Class::getTeacherId,accountId);
        List<Class> list = classService.list(classLambdaQueryWrapper);
        Set<Long> set = list.stream().map(Class::getClassId).collect(Collectors.toSet());
        LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentLambdaQueryWrapper.in(!CollUtil.isEmpty(set),Student::getClassId,set);
        List<Student> studentList = studentService.list(studentLambdaQueryWrapper);
        return Result.success(studentList);
    }


}
