package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
        implements DeviceService {

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

    @Resource
    private BorrowdeviceService borrowdeviceService;

    @Override
    public PageResult myPage(DevicePageQueryDTO devicePageQueryDTO) {
        Page<Device> page = new Page<>(devicePageQueryDTO.getCurrentPage(), devicePageQueryDTO.getPageSize());
        LambdaQueryWrapper<Device> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(devicePageQueryDTO.getSchoolId() != null, Device::getSchoolId, devicePageQueryDTO.getSchoolId())
                .eq(devicePageQueryDTO.getInUse() != null, Device::getInUse, devicePageQueryDTO.getInUse())
                .eq(devicePageQueryDTO.getType()!=null,Device::getType,devicePageQueryDTO.getType())
                .like(!StrUtil.isBlankIfStr(devicePageQueryDTO.getInput()), Device::getDeviceName, devicePageQueryDTO.getInput())
                .orderBy(true, false, Device::getUpdateTime);
        Page<Device> newPage = this.page(page, lambdaQueryWrapper);
        long total = newPage.getTotal();
        List<Device> result = newPage.getRecords();
        List<DeviceVO> list = new ArrayList<>();

        for (Device device : result) {
            DeviceVO deviceVO = Device.obj2VO(device);
            Long deviceId = device.getDeviceId();
            LambdaQueryWrapper<Borrowdevice> borrowdeviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            borrowdeviceLambdaQueryWrapper.eq(Borrowdevice::getDeviceId,deviceId).eq(Borrowdevice::getStatus,1);
            Borrowdevice borrowdevice = borrowdeviceService.getOne(borrowdeviceLambdaQueryWrapper);
            if(borrowdevice!=null){
                Long studentId = borrowdevice.getStudentId();
                Account account = accountService.getById(studentId);
                deviceVO.setStudentId(studentId);
                deviceVO.setStudentName(account.getName());
                Long classId = account.getClassId();
                Class aClass = classService.getById(classId);
                deviceVO.setClassId(classId);
                deviceVO.setClassName(aClass.getClassName());
            }
            Long schoolId = device.getSchoolId();
            if (schoolId != null) {
                School school = schoolService.getById(schoolId);
                if (school == null) {
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                }
                deviceVO.setSchoolName(school.getSchoolName());
            }
            list.add(deviceVO);
        }

        return new PageResult(total, list);
    }

    @Override
    @Transactional
    public void add(DeviceAddOrUpdateDTO deviceAddOrUpdateDTO) {
        Device device = new Device();
        BeanUtils.copyProperties(deviceAddOrUpdateDTO, device);
        device.setLastRepairTime(DateTransferUtil.transfer(LocalDateTime.parse(deviceAddOrUpdateDTO.getLastRepairTime().replace(" ", "T"))));
        device.setInUse(0);
        this.save(device);
    }

    @Override
    @Transactional
    public void myUpdate(DeviceAddOrUpdateDTO deviceAddOrUpdateDTO) {
        Long deviceId = deviceAddOrUpdateDTO.getDeviceId();
        String deviceName = deviceAddOrUpdateDTO.getDeviceName();
        Long schoolId = deviceAddOrUpdateDTO.getSchoolId();
        Long classId = deviceAddOrUpdateDTO.getClassId();
        String lastRepairTime = deviceAddOrUpdateDTO.getLastRepairTime();
        Integer isFault = deviceAddOrUpdateDTO.getIsFault();

        LambdaUpdateWrapper<Device> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(!StrUtil.isBlankIfStr(deviceName), Device::getDeviceName, deviceName)
                .set(schoolId != null, Device::getSchoolId, schoolId)
//                .set(classId != null, Device::getClassId, classId)
                .set(!StrUtil.isBlankIfStr(lastRepairTime), Device::getLastRepairTime, DateTransferUtil.transfer(LocalDateTime.parse(deviceAddOrUpdateDTO.getLastRepairTime().replace(" ", "T"))))
                .set(isFault != null, Device::getIsFault, isFault)
                .eq(Device::getDeviceId,deviceId);
        this.update(lambdaUpdateWrapper);

    }


    @Override
    @Transactional
    public void clearBindWithClass(Long deviceId) {
//        Device device = this.getById(deviceId);
//
//        if (device == null) {
//            throw new DeviceNotExistException(MessageConstant.DEVICE_NOT_EXIST);
//        }
//
//        Long classId = device.getClassId();
//        Class myClass = classService.getById(classId);
//        if (classId == null || myClass == null) {
//            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
//        }
//        myClass.setDeviceNum(myClass.getDeviceNum() - 1);
//        classService.updateById(myClass);

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

//        LambdaQueryWrapper<Result> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Result::getDeviceId, device.getDeviceId());
//        resultService.remove(lambdaQueryWrapper);
//        device.setTestNum(0);
//        device.setClassId(null);
//        device.setInUse(0);
//        this.updateById(device);
    }

    @Override
    @Transactional
    public void delete(Long deviceId) {
        this.removeById(deviceId);
    }

    @Override
    public void importDevices(InputStream inputStream, Long schoolId) {
        ExcelImportUtil.importExcel(inputStream, DeviceImportExcelDTO.class, schoolId, this::excelAddDevices);
    }

    @Transactional(rollbackFor = Exception.class)
    public void excelAddDevices(List<DeviceImportExcelDTO> list, Long schoolId) {

        List<Device> deviceList = list.stream()
                .map(deviceImportExcelDTO -> convertToStudentAccount(deviceImportExcelDTO, schoolId))
                .collect(Collectors.toList());

        // 3. 数据去重（假设根据用户名去重）
        List<String> deviceNames = deviceList.stream()
                .map(Device::getDeviceName)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.in(Device::getDeviceName, deviceNames);
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

    }

    public Device convertToStudentAccount(DeviceImportExcelDTO deviceImportExcelDTO, Long schoolId) {
        Device device = new Device();
        device.setDeviceName(deviceImportExcelDTO.getDeviceName());
        device.setSchoolId(schoolId);
        device.setIsFault(0);
        device.setInUse(0);
        Account loginUser = accountService.getLoginUser();
        device.setCreateUser(loginUser.getName());
        device.setUpdateUser(loginUser.getName());
        return device;
    }


}




