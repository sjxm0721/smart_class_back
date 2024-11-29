package com.sjxm.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.ClassNotExistException;
import com.sjxm.springbootinit.exception.DeviceNotExistException;
import com.sjxm.springbootinit.exception.SchoolNotExistException;
import com.sjxm.springbootinit.exception.StudentNotExistException;
import com.sjxm.springbootinit.model.dto.SightResultDTO;
import com.sjxm.springbootinit.model.entity.*;
import com.sjxm.springbootinit.mapper.ResultMapper;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.vo.SightResultVO;
import com.sjxm.springbootinit.model.vo.StudentVO;
import com.sjxm.springbootinit.service.*;
import com.sjxm.springbootinit.utils.DateTransferUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author sijixiamu
* @description 针对表【result】的数据库操作Service实现
* @createDate 2024-11-18 19:40:50
*/
@Service
@Slf4j
public class ResultServiceImpl extends ServiceImpl<ResultMapper, Result>
    implements ResultService{

    @Autowired
    private ClassService classService;

    @Autowired
    private SchoolService schoolService;

    @Resource
    private AccountService accountService;

//    @Autowired
//    @Lazy
//    private StudentService studentService;

    @Autowired
    private DeviceService deviceService;

    public SightResultVO obj2VO(Result sightResult){
        SightResultVO sightResultVO = new SightResultVO();
        BeanUtils.copyProperties(sightResult,sightResultVO);
        Long classIdTmp = sightResult.getClassId();
        Long schoolIdTmp = sightResult.getSchoolId();
        Long studentId = sightResult.getStudentId();
        Long deviceId = sightResult.getDeviceId();
        Class myClass = classService.getById(classIdTmp);
        if(myClass==null){
            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
        }
        sightResultVO.setClassName(myClass.getClassName());
        School school = schoolService.getById(schoolIdTmp);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        sightResultVO.setSchoolName(school.getSchoolName());
        Account student = accountService.getById(studentId);
        if(student==null){
            throw new StudentNotExistException(MessageConstant.STUDENT_NOT_EXIST);
        }
        sightResultVO.setStudentName(student.getName());
        sightResultVO.setPhone(student.getPhone());
        Device device = deviceService.getById(deviceId);
        if(device==null){
            throw new DeviceNotExistException(MessageConstant.DEVICE_NOT_EXIST);
        }
        sightResultVO.setDeviceName(device.getDeviceName());
        return sightResultVO;
    }

    @Override
    public List<SightResultVO> info(Long classId, Long schoolId, String select, String input) {
        Integer testId=null;
        if(!Objects.equals(input, "")) {
            if (Objects.equals(select, "1")) {
                //结果ID
                testId = Integer.parseInt(input);
            }
        }

        LambdaQueryWrapper<Result> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(schoolId!=null,Result::getSchoolId,schoolId)
                .eq(classId!=null,Result::getClassId,classId)
                .eq(testId!=null,Result::getTestId,testId);
        List<Result> list = this.list(lambdaQueryWrapper);

        List<SightResultVO> result = new ArrayList<>();

        for (Result sightResult : list) {
            result.add(obj2VO(sightResult));
        }
        if(!Objects.equals(input, "")){
            Stream<SightResultVO> filterStream = result.stream();
            if(Objects.equals(select,"2")){
                //学生姓名
                String studentName=input;
                filterStream = filterStream.filter(sightResultVO -> sightResultVO.getStudentName().contains(studentName));
            }
            else if(Objects.equals(select,"3")){
                //设备名
                String deviceName=input;
                filterStream = filterStream.filter(sightResultVO -> sightResultVO.getDeviceName().contains(deviceName));
            }
            return filterStream.collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public List<Integer> resultStatus(Long schoolId, Long classId, String timeStart, String timeEnd) {
        Date startTime = DateTransferUtil.transfer(LocalDateTime.parse(timeStart.replace(" ","T")));
        Date endTime = DateTransferUtil.transfer(LocalDateTime.parse(timeEnd.replace(" ","T")));

        LambdaQueryWrapper<Result> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(schoolId!=null, Result::getSchoolId,schoolId)
                .eq(classId!=null, Result::getClassId,classId)
                .between(startTime!=null&&endTime!=null, Result::getTestTime,startTime,endTime);
        List<Result> list = this.list(lambdaQueryWrapper);

        List<Integer> rs = new ArrayList<>();

        if(list!=null){
            Integer[] array = new Integer[4];
            Arrays.fill(array,0);
            for (Result sightResult : list) {
                Double result = sightResult.getResult();
                if(result>=5.0){
                    array[3]++;
                }
                else if(result>=4.7){
                    array[2]++;
                }
                else if(result>=4.4){
                    array[1]++;
                }
                else if(result>=4.0){
                    array[0]++;
                }
                rs=Arrays.asList(array);
            }
        }

        return rs;
    }

    @Override
    public Long addResult(SightResultDTO sightResultDTO) {
        log.info("接受到了esp32数据:{}",sightResultDTO);
        Long studentId = sightResultDTO.getStudentId();
        Result sightResult = new Result();
        BeanUtils.copyProperties(sightResultDTO,sightResult);
        Account account = accountService.getById(studentId);
        sightResult.setClassId(account.getClassId());
        sightResult.setSchoolId(account.getSchoolId());
        sightResult.setAdvice("多喝热水");
        sightResult.setTestTime(DateTransferUtil.transfer(LocalDateTime.now()));
        Double result = sightResultDTO.getResult();
        if(result>=5.0){
            sightResult.setLevel(0);
        }
        else if(result>=4.0){
            sightResult.setLevel(1);
        }
        else if(result>=3.0){
            sightResult.setLevel(2);
        }
        else{
            sightResult.setLevel(3);
        }
        this.save(sightResult);
        return sightResult.getTestId();
    }
}




