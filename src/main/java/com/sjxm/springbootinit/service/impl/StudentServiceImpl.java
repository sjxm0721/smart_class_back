package com.sjxm.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.*;
import com.sjxm.springbootinit.mapper.ResultMapper;
import com.sjxm.springbootinit.model.dto.StudentAddOrUpdateDTO;
import com.sjxm.springbootinit.model.entity.*;
import com.sjxm.springbootinit.mapper.StudentMapper;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.vo.StudentNumberAndSightVO;
import com.sjxm.springbootinit.model.vo.StudentVO;
import com.sjxm.springbootinit.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @author sijixiamu
* @description 针对表【student】的数据库操作Service实现
* @createDate 2024-11-18 19:41:01
*/
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService{
    @Resource
    private SchoolService schoolService;

    @Resource
    @Lazy
    private ClassService classService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private ResultMapper resultMapper;

    @Resource
    private ResultService resultService;


    public Student dto2Obj(StudentAddOrUpdateDTO dto){
        Integer schoolId= dto.getSchoolId();
        Integer classId = dto.getClassId();
        String studentIdNumber = dto.getStudentIdNumber();

        School school = schoolService.getById(schoolId);
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }

        Class myClass = classService.getById(classId);
        if(myClass==null){
            throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
        }
        myClass.setStudentNum(myClass.getStudentNum()+1);
        classService.updateById(myClass);

        LambdaQueryWrapper<Student> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Student::getClassId,classId).eq(Student::getStudentIdNumber,studentIdNumber);
        Student ifHasStudent =  this.getOne(lambdaQueryWrapper);
        if(ifHasStudent!=null){
            throw new IdNumberHasBeenUsedException(MessageConstant.ID_NUMBER_HAS_BEEN_USED);
        }
        Student student=new Student();
        BeanUtils.copyProperties(dto,student);
        student.setTestNum(0);
        if(dto.getShortSighted()==0){
            student.setSsValue((double) 0);
        }
        return student;
    }

    @Override
    public StudentVO info(Integer studentId) {
        Student student = this.getById(studentId);
        if(student==null){
            throw new StudentNotExistException(MessageConstant.STUDENT_NOT_EXIST);
        }
        StudentVO studentVO = new StudentVO();
        BeanUtils.copyProperties(student,studentVO);
        return studentVO;
    }

    @Override
    public void add(StudentAddOrUpdateDTO studentAddOrUpdateDTO) {
        this.save(dto2Obj(studentAddOrUpdateDTO));
    }

    @Override
    public void myUpdate(StudentAddOrUpdateDTO studentAddOrUpdateDTO) {
        this.updateById(dto2Obj(studentAddOrUpdateDTO));
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        Integer classId= null;
        for (Integer id : ids) {
            Student student = this.getById(id);
            if(student==null){
                throw new StudentNotExistException(MessageConstant.STUDENT_NOT_EXIST);
            }
            classId = student.getClassId();
            List<DeviceTestNumber> deviceUsageNumber = resultMapper.getStudentUsedDevice(student);
            if(deviceUsageNumber.size()!=0)
            {
                for (DeviceTestNumber deviceTestNumber : deviceUsageNumber) {
                    Integer deviceId = deviceTestNumber.getDeviceId();
                    Integer deviceUsedNum = deviceTestNumber.getDeviceUsedNum();
                    Device device = deviceService.getById(deviceId);
                    if(device==null){
                        throw new DeviceNotExistException(MessageConstant.DEVICE_NOT_EXIST);
                    }
                    device.setTestNum(device.getTestNum()-deviceUsedNum);
                    deviceService.updateById(device);
                }
            }
            LambdaQueryWrapper<Result> resultLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resultLambdaQueryWrapper.eq(Result::getStudentId,id);
            resultService.remove(resultLambdaQueryWrapper);
            this.removeById(id);
        }
        Integer size = ids.size();
        if(classId!=null){
            Class myClass = classService.getById(classId);
            if(myClass==null){
                throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
            }
            myClass.setStudentNum(myClass.getStudentNum()-size);
            classService.updateById(myClass);
        }
    }

    @Override
    public StudentNumberAndSightVO studentNumberAndSight(Integer schoolId, Integer classId) {
        LambdaQueryWrapper<Student> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(schoolId!=null,Student::getSchoolId,schoolId)
                .eq(classId!=null,Student::getClassId,classId);
        List<Student> list = this.list(lambdaQueryWrapper);
        StudentNumberAndSightVO studentNumberAndSightVO = new StudentNumberAndSightVO();
        List<Integer> studentSightStatus = new ArrayList<>();
        Integer size=0;
        if(list!=null){
            size = list.size();
            Integer[] array = new Integer[4];
            Arrays.fill(array,0);
            for (Student student : list) {
                Double value = student.getSsValue();
                if(value==0){
                    array[0]++;
                }
                else if(value>0&&value<=200){
                    array[1]++;
                }
                else if(value>200&&value<=400){
                    array[2]++;
                }
                else if(value>400){
                    array[3]++;
                }
            }
            studentSightStatus=Arrays.asList(array);
        }
        studentNumberAndSightVO.setStudentNumber(size);
        studentNumberAndSightVO.setStudentSightStatus(studentSightStatus);

        return studentNumberAndSightVO;
    }
}




