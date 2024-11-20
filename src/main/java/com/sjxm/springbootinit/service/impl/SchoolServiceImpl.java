package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.AccountNotFoundException;
import com.sjxm.springbootinit.exception.SchoolNotExistException;
import com.sjxm.springbootinit.exception.SchoolWithDeviceDeleteFailedException;
import com.sjxm.springbootinit.model.dto.SchoolPageQueryDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.entity.School;
import com.sjxm.springbootinit.mapper.SchoolMapper;
import com.sjxm.springbootinit.model.vo.SchoolVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.SchoolService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sijixiamu
 * @description 针对表【school】的数据库操作Service实现
 * @createDate 2024-11-18 19:40:56
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School>
        implements SchoolService {

    @Resource
    private AccountService accountService;

    @Resource
    @Lazy
    private ClassService classService;

    public SchoolVO obj2VO(School school){
        SchoolVO schoolVO = new SchoolVO();
        BeanUtils.copyProperties(school, schoolVO);
        Integer masterId = school.getMasterId();
        if (masterId != null) {
            Account account = accountService.getById(masterId);
            schoolVO.setMasterName(account.getName());
            schoolVO.setPhone(account.getPhone());
        }
        return schoolVO;
    }

    @Override
    public PageResult pageQuery(SchoolPageQueryDTO schoolPageQueryDTO) {
        String input = schoolPageQueryDTO.getInput();
        Page<School> page = new Page<>(schoolPageQueryDTO.getCurrentPage(), schoolPageQueryDTO.getPageSize());
        LambdaQueryWrapper<School> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(!StrUtil.isBlankIfStr(input), School::getSchoolName, input).orderBy(true, false, School::getUpdateTime);
        Page<School> newPage = this.page(page,lambdaQueryWrapper);
        Long total = newPage.getTotal();
        List<School> result = newPage.getRecords();

        List<SchoolVO> list = new ArrayList<>();

        for (School school : result) {
            list.add(obj2VO(school));
        }
        return new PageResult(total, list);
    }

    @Override
    @Transactional
    public void delete(Integer schoolId) {
        School school = this.getById(schoolId);
        if(school==null)
        {
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        Integer deviceNum = school.getDeviceNum();
        if(deviceNum>0){
            throw new SchoolWithDeviceDeleteFailedException(MessageConstant.SCHOOL_WITH_DEVICE_DELETE_FAILED);
        }
        LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountLambdaQueryWrapper.eq(Account::getSchoolId,schoolId);
        List<Account> list = accountService.list(accountLambdaQueryWrapper);
        if(list!=null&&list.size()>0){
            for (Account account : list) {
                Integer accountId = account.getAccountId();
                accountService.delete(accountId);
            }
        }
        //可以删除
        LambdaQueryWrapper<Class> classLambdaQueryWrapper = new LambdaQueryWrapper<>();
        classLambdaQueryWrapper.eq(Class::getSchoolId,schoolId);
        List<Class> classListBySchoolId = classService.list(classLambdaQueryWrapper);
        if(classListBySchoolId.size()!=0)
        {
            for (Class myClass : classListBySchoolId) {
                Integer classId = myClass.getClassId();
                classService.delete(schoolId,classId);
            }
        }
        this.removeById(schoolId);
    }

    @Override
    public SchoolVO searchBySchoolId(Integer schoolId) {
        School school = this.getById(schoolId);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        SchoolVO schoolVO= new SchoolVO();
        BeanUtils.copyProperties(school,schoolVO);
        Integer masterId = school.getMasterId();
        Account account = accountService.getById(masterId);
        if(account==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        schoolVO.setMasterName(account.getName());
        schoolVO.setPhone(account.getPhone());
        return schoolVO;
    }

    @Override
    public List<SchoolVO> myList() {
        List<School> list = this.list();
        List<SchoolVO> newList=new ArrayList<>();
        for (School school : list) {
            newList.add(obj2VO(school));
        }
        return newList;
    }
}




