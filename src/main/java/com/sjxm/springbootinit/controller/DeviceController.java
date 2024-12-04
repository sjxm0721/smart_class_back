package com.sjxm.springbootinit.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.model.dto.DeviceAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.DevicePageQueryDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.entity.Borrowdevice;
import com.sjxm.springbootinit.model.entity.Device;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.BorrowdeviceService;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.DeviceService;
import com.sjxm.springbootinit.model.vo.DeviceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/admin/device")
@Api(tags = "设备相关接口")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Resource
    private AccountService accountService;

    @Resource
    private BorrowdeviceService borrowdeviceService;

    @GetMapping("/page")
    @ApiOperation("获取设备分页查询信息")
    public Result<PageResult> page(DevicePageQueryDTO devicePageQueryDTO){
        PageResult page = deviceService.myPage(devicePageQueryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("新增设备")
    public Result add(@RequestBody DeviceAddOrUpdateDTO deviceAddOrUpdateDTO){

        deviceService.add(deviceAddOrUpdateDTO);

        return Result.success();
    }


    @PutMapping("/update")
    @ApiOperation("修改设备")
    public Result update(@RequestBody DeviceAddOrUpdateDTO deviceAddOrUpdateDTO){

        deviceService.myUpdate(deviceAddOrUpdateDTO);

        return Result.success();
    }

    @PutMapping("/clearBind")
    @ApiOperation("解除设备与班级的绑定")
    public Result clearBindWithClass(Long deviceId){
        deviceService.clearBindWithClass(deviceId);

        return Result.success();
    }


    @DeleteMapping("/delete")
    @ApiOperation("删除设备")
    public Result delete(Long deviceId){
        deviceService.delete(deviceId);

        return Result.success();
    }

    @GetMapping("/listByClassId")
    @ApiOperation("根据班级id获取设备信息列表")
    public Result<List<DeviceVO>> listByClassId(Integer classId){
        List<DeviceVO> deviceVOList = new ArrayList<>();
        LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountLambdaQueryWrapper.eq(Account::getClassId,classId).eq(Account::getAuth,0);
        List<Account> list = accountService.list(accountLambdaQueryWrapper);
        if(CollUtil.isEmpty(list)){
            return Result.success(deviceVOList);
        }
        Set<Long> set = list.stream().map(Account::getAccountId).collect(Collectors.toSet());
        LambdaQueryWrapper<Borrowdevice> borrowdeviceLambdaQueryWrapper= new LambdaQueryWrapper<>();
        borrowdeviceLambdaQueryWrapper.in(Borrowdevice::getStudentId,set).eq(Borrowdevice::getStatus,1);
        List<Borrowdevice> borrowdeviceList = borrowdeviceService.list(borrowdeviceLambdaQueryWrapper);
        if(CollUtil.isEmpty(borrowdeviceList)){
            return Result.success(deviceVOList);
        }
        Set<Long> set1 = borrowdeviceList.stream().map(Borrowdevice::getDeviceId).collect(Collectors.toSet());
        LambdaQueryWrapper<Device> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Device::getDeviceId,set1);
        List<Device> deviceList = deviceService.list(lambdaQueryWrapper);
        deviceVOList = deviceList.stream().map(Device::obj2VO).collect(Collectors.toList());
        return Result.success(deviceVOList);
    }

    @GetMapping("/deviceNumber")
    @ApiOperation("获取设备数量")
    public Result<Long> deviceNumber(Integer schoolId,Integer classId){
        LambdaQueryWrapper<Device> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(schoolId!=null,Device::getSchoolId,schoolId);
//                .eq(classId!=null,Device::getClassId,classId);
        Long deviceNumber = deviceService.count(lambdaQueryWrapper);

        return Result.success(deviceNumber);
    }

    @PostMapping("/import")
    public Result importDevices(@RequestParam("file") MultipartFile file, Long schoolId) {
        try {
            deviceService.importDevices(file.getInputStream(),schoolId);
            return Result.success("导入成功");
        } catch (IOException e) {
            return Result.error(MessageConstant.EXCEL_IMPORT_ERROR);
        }
    }

}
