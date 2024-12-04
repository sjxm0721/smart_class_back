package com.sjxm.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.NoEnoughAuthException;
import com.sjxm.springbootinit.model.dto.StudentAddOrUpdateDTO;
import com.sjxm.springbootinit.model.entity.*;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.vo.StudentNumberAndSightVO;
import com.sjxm.springbootinit.model.vo.StudentVO;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "学生管理相关接口")
@RestController
@RequestMapping("/admin/student")
public class StudentController {


    @Resource
    private AccountService accountService;

    @Resource
    private ClassService classService;

    @Resource
    private SchoolService schoolService;

    @Resource
    private ClstearelationService clstearelationService;

    @GetMapping("/list")
    @ApiOperation("获取学生信息列表")
    public Result<List<StudentVO>> list(Integer classId,String name,String userId){

        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(classId!=null,Account::getClassId,classId)
                .like(!StrUtil.isBlankIfStr(name),Account::getName,name)
                .eq(!StrUtil.isBlankIfStr(userId),Account::getUserId,userId)
                .eq(Account::getAuth,0)
                .orderBy(true,false,Account::getUpdateTime);
        List<Account> list =  accountService.list(lambdaQueryWrapper);
        List<StudentVO> result = new ArrayList<>();
        for (Account account : list) {
            StudentVO studentVO = account2StudentVO(account);
            result.add(studentVO);
        }
        return Result.success(result);
    }

    private StudentVO account2StudentVO(Account account) {
        Long classId = account.getClassId();
        Long schoolId = account.getSchoolId();
        StudentVO studentVO = new StudentVO();
        BeanUtil.copyProperties(account,studentVO);
        if(classId!=null){
            Class aClass = classService.getById(classId);
            studentVO.setClassName(aClass.getClassName());
        }
        if(schoolId!=null){
            School school = schoolService.getById(schoolId);
            studentVO.setSchoolName(school.getSchoolName());
        }
        return studentVO;
    }


    @GetMapping("/info")
    @ApiOperation("根据id获取学生信息")
    public Result<StudentVO> info(Long accountId){
        Account account = accountService.getById(accountId);
        StudentVO studentVO = account2StudentVO(account);
        return Result.success(studentVO);
    }

    @PostMapping("/add")
    @ApiOperation("添加学生")
    public Result add(@RequestBody StudentAddOrUpdateDTO studentAddOrUpdateDTO){

        Account account = new Account();
        BeanUtil.copyProperties(studentAddOrUpdateDTO,account);
        account.setPassword("123456");
        account.setAuth(0);
        account.setCreateUser(accountService.getLoginUser().getName());
        account.setUpdateUser(accountService.getLoginUser().getName());
        accountService.save(account);

        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改学生")
    public Result update(@RequestBody StudentAddOrUpdateDTO studentAddOrUpdateDTO){

        Account account = new Account();
        BeanUtil.copyProperties(studentAddOrUpdateDTO,account);
        accountService.updateById( account);

        return Result.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除学生")
    public Result delete(@RequestParam List<Long> ids){

        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Account::getAccountId,ids);
        accountService.remove(lambdaQueryWrapper);
        return Result.success();
    }


    @GetMapping("/studentNumberAndSight")
    @ApiOperation("获取学生数量")
    public Result<Long> studentNumberAndSight(Long schoolId, Long classId){
        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Account::getSchoolId,schoolId).eq(Account::getClassId,classId).eq(Account::getAuth,0);
        long count = accountService.count(lambdaQueryWrapper);
        return Result.success(count);
    }

    @GetMapping("/all-stu")
    @ApiOperation("获取此教师所有的学生")
    public Result<List<StudentVO>> allStu(Long accountId){
        Account account = accountService.getById(accountId);
        if(account.getAuth()==0){
            throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
        }
        LambdaQueryWrapper<Class> classLambdaQueryWrapper = new LambdaQueryWrapper();
        LambdaQueryWrapper<Clstearelation> clstearelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clstearelationLambdaQueryWrapper.eq(Clstearelation::getTeacherId,accountId);
        List<Clstearelation> clstearelationList = clstearelationService.list(clstearelationLambdaQueryWrapper);
        List<Long> classIds = clstearelationList.stream().map(Clstearelation::getClassId).collect(Collectors.toList());
        classLambdaQueryWrapper.in(!CollUtil.isEmpty(classIds),Class::getClassId,classIds);
        List<Class> list = classService.list(classLambdaQueryWrapper);
        Set<Long> set = list.stream().map(Class::getClassId).collect(Collectors.toSet());
        LambdaQueryWrapper<Account> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentLambdaQueryWrapper.in(!CollUtil.isEmpty(set),Account::getClassId,set);
        List<Account> studentList = accountService.list(studentLambdaQueryWrapper);
        List<StudentVO> collect = studentList.stream().map(this::account2StudentVO).collect(Collectors.toList());
        return Result.success(collect);
    }


    @PostMapping("/import")
    public Result importUsers(@RequestParam("file") MultipartFile file,Long classId) {
        try {
            accountService.importStudents(file.getInputStream(),classId);
            return Result.success("导入成功");
        } catch (IOException e) {
            return Result.error(MessageConstant.EXCEL_IMPORT_ERROR);
        }
    }


}
