package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.*;
import com.sjxm.springbootinit.model.dto.MyClassDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.mapper.ClassMapper;
import com.sjxm.springbootinit.model.entity.School;
import com.sjxm.springbootinit.model.entity.Student;
import com.sjxm.springbootinit.model.vo.MyClassVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.SchoolService;
import com.sjxm.springbootinit.service.StudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
* @author sijixiamu
* @description 针对表【class】的数据库操作Service实现
* @createDate 2024-11-18 19:40:39
*/
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class>
    implements ClassService{

    @Resource
    @Lazy
    private SchoolService schoolService;

    @Resource
    private AccountService accountService;

    public MyClassVO obj2VO(Class myClass){
        MyClassVO myClassVO = new MyClassVO();
        BeanUtils.copyProperties(myClass,myClassVO);
        Long schoolId = myClass.getSchoolId();
        School school = schoolService.getById(schoolId);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        myClassVO.setSchoolName(school.getSchoolName());
        Long teacherId = myClass.getTeacherId();
        if(teacherId!=null){
            Account account = accountService.getById(teacherId);
            if(account==null){
                throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
            }
            myClassVO.setTeacherName(account.getName());
            myClassVO.setPhone(account.getPhone());
        }
        return myClassVO;
    }

    @Override
    public List<MyClassVO> info(Long classId) {
        Class myClass = this.getById(classId);
        if(myClass==null)
        {
            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
        }
        List<MyClassVO> list = new ArrayList<>();
        MyClassVO myClassVO = obj2VO(myClass);
        list.add(myClassVO);
        return list;
    }

    @Override
    public List<MyClassVO> myList(Long schoolId) {
        LambdaQueryWrapper<Class> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Class::getSchoolId,schoolId);
        List<Class> list = this.list(lambdaQueryWrapper);
        if(list==null||list.size()==0){
            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
        }
        List<MyClassVO> result = new ArrayList<>();
        for (Class myClass : list) {
            result.add(obj2VO(myClass));
        }
        return result;
    }

    @Override
    public PageResult myPage(Long schoolId, String input, Integer currentPage, Integer pageSize) {
        Page<Class> page = new Page<>(currentPage,pageSize);
        LambdaQueryWrapper<Class> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(schoolId!=null,Class::getSchoolId,schoolId)
                .like(!StrUtil.isBlankIfStr(input),Class::getClassName,input)
                .orderBy(true,false,Class::getUpdateTime);
        Page<Class> newPage = this.page(page,lambdaQueryWrapper);

        long total = page.getTotal();
        List<Class> result = page.getRecords();

        List<MyClassVO> list = new ArrayList<>();

        for (Class myClass : result) {
            list.add(obj2VO(myClass));
        }
        return new PageResult(total,list);
    }

    @Override
    @Transactional
    public void add(MyClassDTO myClassDTO) {
        Long schoolId=myClassDTO.getSchoolId();
        String className=myClassDTO.getClassName();
        School school = schoolService.getById(schoolId);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        school.setClassNum(school.getClassNum()+1);
        schoolService.updateById(school);

        Class myClass = Class.builder()
                .className(className)
                .schoolId(schoolId)
                .deviceNum(0)
                .studentNum(0)
                .build();
        this.save(myClass);
    }

    @Override
    @Transactional
    public void myUpdate(MyClassDTO myClassDTO) {
        Long classId = myClassDTO.getClassId();

        Class oldClass = this.getById(classId);
        if(oldClass==null){
            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
        }
        Long newSchoolId = myClassDTO.getSchoolId();
        Long oldSchoolId = oldClass.getSchoolId();
        if(oldClass.getTeacherId()!=null&& !Objects.equals(newSchoolId, oldSchoolId)){
            throw new BaseException(MessageConstant.CLASS_WITH_TEACHER_EDIT_ERROR);
        }
        School oldSchool = schoolService.getById(oldSchoolId);
        if(oldSchool==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        oldSchool.setClassNum(oldSchool.getClassNum()-1);
        schoolService.updateById(oldSchool);
        School newSchool = schoolService.getById(newSchoolId);
        if(newSchool==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        newSchool.setClassNum(newSchool.getClassNum()+1);
        schoolService.updateById(newSchool);

        Class myClass = new Class();

        BeanUtils.copyProperties(myClassDTO,myClass);

        this.updateById(myClass);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long schoolId, Long classId) {
        Class myClass = this.getById(classId);
        Integer deviceNum = myClass.getDeviceNum();
        if(deviceNum>0){
            throw new ClassWithDeviceDeleteErrorException(MessageConstant.CLASS_WITH_DEVICE_DELETE_ERROR);
        }

        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Account::getClassId,classId);
        List<Account> list = accountService.list(lambdaQueryWrapper);

        if(list!=null&&list.size()>0){
            throw new ClassWithAccountDeleteException(MessageConstant.CLASS_WITH_ACCOUNT_DELETE_FAILED);
        }

        School school = schoolService.getById(schoolId);

        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }

        school.setClassNum(school.getClassNum()-1);

        schoolService.updateById(school);

        LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountLambdaQueryWrapper.eq(Account::getClassId,classId).eq(Account::getAuth,0);
        accountService.remove(accountLambdaQueryWrapper);

        this.removeById(classId);
    }

    @Override
    public void clearTeacher(Long classId) {
        Class myClass = this.getById(classId);
        if(myClass==null){
            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
        }

        Long teacherId = myClass.getTeacherId();
        Account account = accountService.getById(teacherId);

        if(account==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if(Objects.equals(account.getClassId(), classId)){
            LambdaUpdateWrapper<Account> accountLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            accountLambdaUpdateWrapper.eq(Account::getAccountId,account.getAccountId()).set(Account::getClassId,null);
            accountService.update(accountLambdaUpdateWrapper);
        }

        myClass.setTeacherId(null);

        this.updateById(myClass);
    }


}




