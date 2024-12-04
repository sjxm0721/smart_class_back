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
import com.sjxm.springbootinit.model.entity.Device;
import com.sjxm.springbootinit.model.entity.School;
import com.sjxm.springbootinit.mapper.SchoolMapper;
import com.sjxm.springbootinit.model.vo.SchoolVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.DeviceService;
import com.sjxm.springbootinit.service.SchoolService;
import me.chanjar.weixin.mp.bean.device.WxDeviceBindDeviceResult;
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

    @Resource
    @Lazy
    private DeviceService deviceService;

    public SchoolVO obj2VO(School school){
        Long schoolId = school.getSchoolId();
        SchoolVO schoolVO = new SchoolVO();
        BeanUtils.copyProperties(school, schoolVO);
        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.eq(Device::getSchoolId, schoolId);
        schoolVO.setDeviceNum(deviceService.count(deviceLambdaQueryWrapper));
        LambdaQueryWrapper<Class> classLambdaQueryWrapper = new LambdaQueryWrapper<>();
        classLambdaQueryWrapper.eq(Class::getSchoolId, schoolId);
        schoolVO.setClassNum(classService.count(classLambdaQueryWrapper));
        LambdaQueryWrapper<Account> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(Account::getSchoolId, schoolId).eq(Account::getAuth,2);
        schoolVO.setTeacherNum(accountService.count(teacherLambdaQueryWrapper));
        LambdaQueryWrapper<Account> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentLambdaQueryWrapper.eq(Account::getSchoolId, schoolId).eq(Account::getAuth,0);
        schoolVO.setStudentNum(accountService.count(studentLambdaQueryWrapper));
        return schoolVO;
    }

    @Override
    public PageResult pageQuery(SchoolPageQueryDTO schoolPageQueryDTO) {
        String input = schoolPageQueryDTO.getInput();
        Page<School> page = new Page<>(schoolPageQueryDTO.getCurrentPage(), schoolPageQueryDTO.getPageSize());
        LambdaQueryWrapper<School> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!StrUtil.isBlankIfStr(input), School::getSchoolName, input).orderBy(true, false, School::getUpdateTime);
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
    public void delete(Long schoolId) {
        this.removeById(schoolId);
    }

    @Override
    public SchoolVO searchBySchoolId(Long schoolId) {
        School school = this.getById(schoolId);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        return obj2VO(school);
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




