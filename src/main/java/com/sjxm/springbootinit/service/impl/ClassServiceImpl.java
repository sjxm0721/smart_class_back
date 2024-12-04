package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.*;
import com.sjxm.springbootinit.model.dto.MyClassDTO;
import com.sjxm.springbootinit.model.entity.*;
import com.sjxm.springbootinit.mapper.ClassMapper;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.vo.MyClassVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Resource
    private ClstearelationService clstearelationService;

    @Resource
    @Lazy
    private DeviceService deviceService;

    public MyClassVO obj2VO(Class myClass){
        MyClassVO myClassVO = new MyClassVO();
        BeanUtils.copyProperties(myClass,myClassVO);
        Long schoolId = myClass.getSchoolId();
        School school = schoolService.getById(schoolId);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        myClassVO.setSchoolName(school.getSchoolName());

        Long classId = myClass.getClassId();
        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        deviceLambdaQueryWrapper.eq(Device::getClassId,classId);
        myClassVO.setDeviceNum(deviceService.count(deviceLambdaQueryWrapper));
        LambdaQueryWrapper<Account> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentLambdaQueryWrapper.eq(Account::getClassId,classId).eq(Account::getAuth,0);
        myClassVO.setStudentNum(accountService.count(studentLambdaQueryWrapper));

        LambdaQueryWrapper<Clstearelation> clstearelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clstearelationLambdaQueryWrapper.eq(Clstearelation::getClassId,classId);
        List<Clstearelation> clstearelationList = clstearelationService.list(clstearelationLambdaQueryWrapper);
        List<Long> teacherIds = clstearelationList.stream().map(Clstearelation::getTeacherId).collect(Collectors.toList());
        if(!CollUtil.isEmpty(teacherIds)){
            List<Account> teacherList = accountService.listByIds(teacherIds);
            myClassVO.setTeacherNum((long) teacherList.size());
            myClassVO.setTeacherList(teacherList);
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
    public void add(MyClassDTO myClassDTO) {
        Long schoolId=myClassDTO.getSchoolId();
        String className=myClassDTO.getClassName();

        Class myClass = Class.builder()
                .className(className)
                .schoolId(schoolId)
                .build();
        this.save(myClass);
    }

    @Override
    public void myUpdate(MyClassDTO myClassDTO) {
        Long classId = myClassDTO.getClassId();
        Long schoolId = myClassDTO.getSchoolId();
        String className = myClassDTO.getClassName();

        LambdaUpdateWrapper<Class> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(schoolId!=null,Class::getSchoolId,schoolId)
                .set(!StrUtil.isBlankIfStr(className),Class::getClassName,className)
                .eq(classId!=null,Class::getClassId,classId);

        this.update(lambdaUpdateWrapper);
    }

    @Override
    public void delete(Long classId) {
        this.removeById(classId);
    }

    @Override
    public void clearTeacher(Long classId) {

    }


}




