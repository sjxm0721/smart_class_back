package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.*;
import com.sjxm.springbootinit.mapper.ResultMapper;
import com.sjxm.springbootinit.model.dto.DeviceAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.DeviceImportExcelDTO;
import com.sjxm.springbootinit.model.dto.DevicePageQueryDTO;
import com.sjxm.springbootinit.model.dto.StudentImportExcelDTO;
import com.sjxm.springbootinit.model.entity.*;
import com.sjxm.springbootinit.mapper.DeviceMapper;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.vo.DeviceVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.service.*;
import com.sjxm.springbootinit.utils.DateTransferUtil;
import com.sjxm.springbootinit.utils.ExcelImportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author sijixiamu
* @description 针对表【device】的数据库操作Service实现
* @createDate 2024-11-18 19:40:44
*/
@Service
@Slf4j
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
    implements DeviceService{

    @Resource
    @Lazy
    private ClassService classService;

    @Resource
    @Lazy
    private SchoolService schoolService;


    @Resource
    @Lazy
    private ResultService resultService;

    @Resource
    private AccountService accountService;

    @Override
    public PageResult myPage(DevicePageQueryDTO devicePageQueryDTO) {
        Page<Device> page = new Page<>(devicePageQueryDTO.getCurrentPage(),devicePageQueryDTO.getPageSize());
        LambdaQueryWrapper<Device> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(devicePageQueryDTO.getSchoolId()!=null,Device::getSchoolId,devicePageQueryDTO.getSchoolId())
                .eq(devicePageQueryDTO.getInUse()!=null,Device::getInUse,devicePageQueryDTO.getInUse())
                .like(!StrUtil.isBlankIfStr(devicePageQueryDTO.getInput()),Device::getDeviceName,devicePageQueryDTO.getInput())
                .orderBy(true,false,Device::getUpdateTime);
        Page<Device> newPage = this.page(page, lambdaQueryWrapper);
        long total = newPage.getTotal();
        List<Device> result = newPage.getRecords();
        List<DeviceVO> list = new ArrayList<>();

        for (Device device : result) {
            DeviceVO deviceVO = new DeviceVO();
            BeanUtils.copyProperties(device,deviceVO);
            Long classId = device.getClassId();
            if(classId!=null){
                Class myClass = classService.getById(classId);
                if(myClass==null){
                    throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
                }
                deviceVO.setClassName(myClass.getClassName());
            }
            Long schoolId = device.getSchoolId();
            if(schoolId!=null){
                School school = schoolService.getById(schoolId);
                if(school==null){
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                }
                deviceVO.setSchoolName(school.getSchoolName());
            }
            list.add(deviceVO);
        }

        return new PageResult(total,list);
    }

    @Override
    @Transactional
    public void add(DeviceAddOrUpdateDTO deviceAddOrUpdateDTO) {
        Long schoolId=deviceAddOrUpdateDTO.getSchoolId();

        School school = schoolService.getById(schoolId);

        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }

        school.setDeviceNum(school.getDeviceNum()+1);
        schoolService.updateById(school);

        Device device=new Device();
        BeanUtils.copyProperties(deviceAddOrUpdateDTO,device);
        device.setLastRepairTime(DateTransferUtil.transfer(LocalDateTime.parse(deviceAddOrUpdateDTO.getLastRepairTime().replace(" ","T"))));
        device.setTestNum(0);
        device.setInUse(0);
        this.save(device);
    }

    @Override
    @Transactional
    public void myUpdate(DeviceAddOrUpdateDTO deviceAddOrUpdateDTO) {
        Long deviceId = deviceAddOrUpdateDTO.getDeviceId();
        if(deviceAddOrUpdateDTO.getSchoolId()!=null){
            Long newSchoolId = deviceAddOrUpdateDTO.getSchoolId();
            Device oldDevice = this.getById(deviceId);
            if(oldDevice==null){
                throw new DeviceNotExistException(MessageConstant.DEVICE_NOT_EXIST);
            }
            Long oldSchoolId = oldDevice.getSchoolId();
            if(!newSchoolId.equals(oldSchoolId)){
                //进行了学校更改
                if(oldDevice.getClassId()!=null){
                    throw new DeviceWithClassEditErrorException(MessageConstant.DEVICE_WITH_CLASS_EDIT_ERROR);
                }
                School oldSchool = schoolService.getById(oldSchoolId);
                if(oldSchool==null){
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                }

                oldSchool.setDeviceNum(oldSchool.getDeviceNum()-1);
                schoolService.updateById(oldSchool);

                School newSchool = schoolService.getById(newSchoolId);
                if(newSchool==null){
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                }
                newSchool.setDeviceNum(newSchool.getDeviceNum()+1);
                schoolService.updateById(newSchool);
            }

        }

        Device device=new Device();
        BeanUtils.copyProperties(deviceAddOrUpdateDTO,device);
        if(!StrUtil.isBlankIfStr(deviceAddOrUpdateDTO.getLastRepairTime()))
        {
            device.setLastRepairTime(DateTransferUtil.transfer(LocalDateTime.parse(deviceAddOrUpdateDTO.getLastRepairTime().replace(" ","T"))));
        }
        if(deviceAddOrUpdateDTO.getClassId()!=null){
            Long classId=deviceAddOrUpdateDTO.getClassId();
            Class myClass = classService.getById(classId);
            if(myClass==null){
                throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
            }
            myClass.setDeviceNum(myClass.getDeviceNum()+1);
            device.setInUse(1);
            classService.updateById(myClass);
        }
        this.updateById(device);
    }

    @Override
    @Transactional
    public void clearBindWithClass(Long deviceId) {
        Device device = this.getById(deviceId);

        if(device==null){
            throw new DeviceNotExistException(MessageConstant.DEVICE_NOT_EXIST);
        }

        Long classId = device.getClassId();
        Class myClass = classService.getById(classId);
        if(classId==null||myClass==null){
            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
        }
        myClass.setDeviceNum(myClass.getDeviceNum()-1);
        classService.updateById(myClass);

//        List<StudentTestNumber> studentTestNumbers = resultMapper.getStudentUserDevice2(device);
//
//        if(studentTestNumbers.size()!=0)
//        {
//            for (StudentTestNumber studentTestNumber : studentTestNumbers) {
//                Long studentId = studentTestNumber.getStudentId();
//                Integer studentUsedNum = studentTestNumber.getStudentUsedNum();
//                Student student = studentService.getById(studentId);
//                if(student==null){
//                    throw new StudentNotExistException(MessageConstant.STUDENT_NOT_EXIST);
//                }
//                student.setTestNum(student.getTestNum()-studentUsedNum);
//                studentService.updateById(student);
//            }
//        }

        LambdaQueryWrapper<Result> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Result::getDeviceId,device.getDeviceId());
        resultService.remove(lambdaQueryWrapper);
        device.setTestNum(0);
        device.setClassId(null);
        device.setInUse(0);
        this.updateById(device);
    }

    @Override
    @Transactional
    public void delete(Long deviceId) {
        Device device = this.getById(deviceId);
        if(device==null){
            throw new DeviceNotExistException(MessageConstant.DEVICE_NOT_EXIST);
        }

        if(device.getClassId()!=null){
            throw new DeviceWithClassDeleteError(MessageConstant.DEVICE_WITH_CLASS_DELETE_ERROR);
        }

        Long schoolId = device.getSchoolId();
        School school = schoolService.getById(schoolId);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        school.setDeviceNum(school.getDeviceNum()-1);
        schoolService.updateById(school);

        this.removeById(deviceId);
    }

    @Override
    public void importDevices(InputStream inputStream, Long schoolId) {
        ExcelImportUtil.importExcel(inputStream, DeviceImportExcelDTO.class,schoolId,this::excelAddDevices);
    }

    @Transactional(rollbackFor = Exception.class)
    public void excelAddDevices(List<DeviceImportExcelDTO> list,Long schoolId) {

        List<Device> deviceList = list.stream()
                .map(deviceImportExcelDTO -> convertToStudentAccount(deviceImportExcelDTO,schoolId))
                .collect(Collectors.toList());

        // 3. 数据去重（假设根据用户名去重）
        List<String> deviceNames = deviceList.stream()
                .map(Device::getDeviceName)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.in(Device::getDeviceName,deviceNames);
        List<Device> existingDevices = this.list(deviceLambdaQueryWrapper);
        Map<String, Device> existingDeviceMap = existingDevices.stream()
                .collect(Collectors.toMap(Device::getDeviceName, device -> device));

        // 4. 分离新增和更新的数据
        List<Device> toInsert = new ArrayList<>();
        List<Device> toUpdate = new ArrayList<>();

        for (Device device : deviceList) {
            if (existingDeviceMap.containsKey(device.getDeviceName())) {
                // 设置ID等必要字段
                Device existDevice = existingDeviceMap.get(device.getDeviceName());
                device.setDeviceId(existDevice.getDeviceId());
                // 设置不应被更新的字段
                device.setCreateTime(existDevice.getCreateTime());
                device.setCreateUser(existDevice.getCreateUser());
                toUpdate.add(device);
            } else {
                // 设置新增记录的默认值
                device.setCreateTime(new Date());
                device.setCreateUser(accountService.getLoginUser().getName());
                toInsert.add(device);
            }
        }

        // 5. 批量保存数据
        if (!toInsert.isEmpty()) {
            this.saveBatch(toInsert);
            log.info("批量插入{}条数据", toInsert.size());
        }

        if (!toUpdate.isEmpty()) {
            this.updateBatchById(toUpdate);
            log.info("批量更新{}条数据", toUpdate.size());
        }

        //todo 学校设备数修改
        School school = schoolService.getById(schoolId);
        school.setDeviceNum(school.getDeviceNum()+toInsert.size());
        schoolService.updateById(school);

    }

    public Device convertToStudentAccount(DeviceImportExcelDTO deviceImportExcelDTO,Long schoolId){
        Device device = new Device();
        device.setDeviceName(deviceImportExcelDTO.getDeviceName());
        device.setSchoolId(schoolId);
        device.setTestNum(0);
        device.setIsFault(0);
        device.setInUse(0);
        Account loginUser = accountService.getLoginUser();
        device.setCreateUser(loginUser.getName());
        device.setUpdateUser(loginUser.getName());
        return device;
    }


    }




