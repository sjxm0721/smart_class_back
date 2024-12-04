package com.sjxm.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.BorrowNotFoundException;
import com.sjxm.springbootinit.model.dto.BorrowApproveDTO;
import com.sjxm.springbootinit.model.dto.BorrowSubmitDTO;
import com.sjxm.springbootinit.model.dto.DeviceBorrowPageQueryDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.entity.Borrowdevice;
import com.sjxm.springbootinit.model.entity.Device;
import com.sjxm.springbootinit.model.enums.StatusTypeEnum;
import com.sjxm.springbootinit.model.vo.BorrowdeviceVO;
import com.sjxm.springbootinit.model.vo.DeviceVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.BorrowdeviceService;
import com.sjxm.springbootinit.service.DeviceService;
import com.sjxm.springbootinit.utils.DateTransferUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/4
 * @Description:
 */
@RestController
@RequestMapping("/admin/device-borrow")
@Api(tags = "设备租借控制器")
@Slf4j
public class DeviceBorrowController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private BorrowdeviceService borrowdeviceService;

    @Resource
    private AccountService accountService;


    @ApiOperation("租借信息分页查询")
    @PostMapping("/available")
    public Result<PageResult> availableDevices(@RequestBody DeviceBorrowPageQueryDTO dto) {
        String searchKey = dto.getSearchKey();
        Integer currentPage = dto.getCurrentPage();
        Integer pageSize = dto.getPageSize();
        Integer type = dto.getType();
        Long schoolId = dto.getSchoolId();

        Page<Device> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.eq(Device::getSchoolId, schoolId)
                .like(!StrUtil.isBlankIfStr(searchKey), Device::getDeviceName, searchKey)
                .eq(type != null, Device::getType, type);
        Page<Device> devicePage = deviceService.page(page, deviceLambdaQueryWrapper);
        long total = devicePage.getTotal();
        List<Device> result = devicePage.getRecords();
        List<DeviceVO> deviceVOList = result.stream().map(Device::obj2VO).collect(Collectors.toList());
        List<DeviceVO> newDeviceVOList = deviceVOList.stream().filter(deviceVO -> {
            Long deviceId = deviceVO.getDeviceId();
            LambdaQueryWrapper<Borrowdevice> borrowdeviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            borrowdeviceLambdaQueryWrapper.eq(Borrowdevice::getDeviceId, deviceId);
            List<Borrowdevice> borrowdeviceList = borrowdeviceService.list(borrowdeviceLambdaQueryWrapper);
            for (Borrowdevice borrowdevice : borrowdeviceList) {
                if (borrowdevice.getStatus() == 0 || borrowdevice.getStatus() == 1) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        return Result.success(new PageResult(total, newDeviceVOList));
    }

    @GetMapping("/my-borrows")
    @ApiOperation("获取自己的租借情况")
    public Result<List<BorrowdeviceVO>> myBorrows(Long accountId) {
        LambdaQueryWrapper<Borrowdevice> borrowdeviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        borrowdeviceLambdaQueryWrapper.eq(Borrowdevice::getStudentId, accountId);
        List<Borrowdevice> borrowdeviceList = borrowdeviceService.list(borrowdeviceLambdaQueryWrapper);
        List<BorrowdeviceVO> borrowdeviceVOList = new ArrayList<>();
        for (Borrowdevice borrowdevice : borrowdeviceList) {
            BorrowdeviceVO borrowdeviceVO = new BorrowdeviceVO();
            BeanUtil.copyProperties(borrowdevice, borrowdeviceVO);
            Long deviceId = borrowdevice.getDeviceId();
            Long studentId = borrowdevice.getStudentId();
            Integer status = borrowdevice.getStatus();
            Device device = deviceService.getById(deviceId);
            Account account = accountService.getById(studentId);
            if (device != null) {
                borrowdeviceVO.setDeviceName(device.getDeviceName());
            }
            if (account != null) {
                borrowdeviceVO.setStudentName(account.getName());
            }
            borrowdeviceVO.setStatusValue(StatusTypeEnum.getEnumByValue(status).getText());
            borrowdeviceVOList.add(borrowdeviceVO);
        }
        return Result.success(borrowdeviceVOList);
    }


    @PostMapping("/borrow")
    @ApiOperation("提交租借申请")
    public Result submitBorrow(@RequestBody BorrowSubmitDTO borrowSubmitDTO){

        Date startDate = DateTransferUtil.transfer(LocalDateTime.parse(borrowSubmitDTO.getStartTime().replace(" ","T")));
        Date endDate = DateTransferUtil.transfer(LocalDateTime.parse(borrowSubmitDTO.getEndTime().replace(" ","T")));
        Long accountId = borrowSubmitDTO.getAccountId();
        Long deviceId = borrowSubmitDTO.getDeviceId();
        String description = borrowSubmitDTO.getDescription();

        LambdaQueryWrapper<Borrowdevice> borrowdeviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        borrowdeviceLambdaQueryWrapper.eq(Borrowdevice::getDeviceId,deviceId);
        List<Borrowdevice> borrowdeviceList = borrowdeviceService.list(borrowdeviceLambdaQueryWrapper);
        for (Borrowdevice borrowdevice : borrowdeviceList) {
            Integer status = borrowdevice.getStatus();
            if(status==0||status==1){
                return Result.error("该设备正在租借流程中");
            }
        }
        Borrowdevice borrowdevice = new Borrowdevice();
        borrowdevice.setDeviceId(deviceId);
        borrowdevice.setStudentId(accountId);
        borrowdevice.setStartTime(startDate);
        borrowdevice.setEndTime(endDate);
        borrowdevice.setDdescribe(description);
        borrowdevice.setStatus(0);
        borrowdeviceService.save(borrowdevice);
        return Result.success();
    }


    @DeleteMapping("/delete-borrow/{borrowId}")
    @ApiOperation("取消设备申请")
    public Result deleteBorrow(@PathVariable Long borrowId){
        Borrowdevice borrowdevice = borrowdeviceService.getById(borrowId);
        Integer status = borrowdevice.getStatus();
        if(status!=0){
            return Result.error("只能取消正在申请中的设备");
        }
        borrowdeviceService.removeById(borrowId);
        return Result.success();
    }


    @GetMapping("/approvals")
    @ApiOperation("获取待审批列表")
    public Result<List<BorrowdeviceVO>> approvals(Long schoolId){
        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.eq(Device::getSchoolId,schoolId);
        List<Device> list = deviceService.list(deviceLambdaQueryWrapper);
        Set<Long> deviceIdSet = list.stream().map(Device::getDeviceId).collect(Collectors.toSet());
        if(CollUtil.isEmpty(deviceIdSet)){
            return Result.success();
        }
        LambdaQueryWrapper<Borrowdevice> borrowdeviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        borrowdeviceLambdaQueryWrapper.in(Borrowdevice::getDeviceId,deviceIdSet).eq(Borrowdevice::getStatus,0);
        List<Borrowdevice> borrowdeviceList = borrowdeviceService.list(borrowdeviceLambdaQueryWrapper);
        List<BorrowdeviceVO> borrowdeviceVOList = new ArrayList<>();
        for (Borrowdevice borrowdevice : borrowdeviceList) {
            BorrowdeviceVO borrowdeviceVO = new BorrowdeviceVO();
            BeanUtil.copyProperties(borrowdevice, borrowdeviceVO);
            Long deviceId = borrowdevice.getDeviceId();
            Long studentId = borrowdevice.getStudentId();
            Integer status = borrowdevice.getStatus();
            Device device = deviceService.getById(deviceId);
            Account account = accountService.getById(studentId);
            if (device != null) {
                borrowdeviceVO.setDeviceName(device.getDeviceName());
            }
            if (account != null) {
                borrowdeviceVO.setStudentName(account.getName());
            }
            borrowdeviceVO.setStatusValue(StatusTypeEnum.getEnumByValue(status).getText());
            borrowdeviceVOList.add(borrowdeviceVO);
        }
        return Result.success(borrowdeviceVOList);
    }


    @PostMapping("/approve")
    @ApiOperation("处理审批")
    @Transactional
    public Result approve(@RequestBody BorrowApproveDTO borrowApproveDTO){
        Long id = borrowApproveDTO.getId();
        Boolean approved = borrowApproveDTO.getApproved();

        Borrowdevice borrowdevice = borrowdeviceService.getById(id);
        if(borrowdevice==null){
            throw new BorrowNotFoundException(MessageConstant.BORROW_NOT_FOUND_ERROR);
        }

        if(approved){
            borrowdevice.setStatus(1);
            Long deviceId = borrowdevice.getDeviceId();
            Device device = deviceService.getById(deviceId);
            device.setInUse(1);
            deviceService.updateById(device);
        }
        else{
            borrowdevice.setStatus(2);
        }

        borrowdeviceService.updateById(borrowdevice);
        return Result.success();
    }




}
