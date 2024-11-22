package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sjxm.springbootinit.mapper.HomeworkMapper;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.entity.Homework;
import com.sjxm.springbootinit.model.entity.Student;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.HomeworkService;
import com.sjxm.springbootinit.service.StudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author sijixiamu
* @description 针对表【homework】的数据库操作Service实现
* @createDate 2024-11-20 17:44:02
*/
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework>
    implements HomeworkService {

    @Resource
    private ClassService classService;

    @Resource
    private StudentService studentService;

    @Override
    public Homework getHomeworkTarget(Homework homework) {
        int type = homework.getType();
        String[] strings = null;
        List<Student> list = null;
        if(type==0){
            //布置给全班
            String target = "";
            Long classId = homework.getClassId();
            Class aClass = classService.getById(classId);
            target=aClass.getClassName();
            homework.setTarget(target);
            LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentLambdaQueryWrapper.eq(Student::getClassId,classId);
            list = studentService.list(studentLambdaQueryWrapper);
        }
        else{
            //布置给个人
            StringBuilder sb = new StringBuilder();
            String studentIdList = homework.getStudentIdList();
            String[] allStudentStr = new String[0];
            if(!StrUtil.isBlankIfStr(studentIdList)){
                allStudentStr = studentIdList.split(",");
            }
            LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentLambdaQueryWrapper.in(allStudentStr.length>0,Student::getStudentId, allStudentStr);
            list = studentService.list(studentLambdaQueryWrapper);
            for(int i =0;i<list.size();i++){
                sb.append(list.get(i).getStudentName()).append("、");
            }
            StringBuilder target = sb.deleteCharAt(sb.length() - 1);
            homework.setTarget(target.toString());
        }
        String completedStudentId = homework.getCompletedStudentId();
        if(!StrUtil.isBlankIfStr(completedStudentId)){
            strings = completedStudentId.split(",");
        }
        if(!CollUtil.isEmpty(list)){
            Set<Long> sets = new HashSet<>();
            if (strings != null) {
                sets = Arrays.stream(strings).map(Long::parseLong).collect(Collectors.toSet());
            }
            StringBuilder studentCompleted = new StringBuilder();
            StringBuilder studentNotCompleted = new StringBuilder();
            for (Student student : list) {
                if(sets.contains(student.getStudentId())){
                    studentCompleted.append(student.getStudentName()).append(",");
                }
                else{
                    studentNotCompleted.append(student.getStudentName()).append(",");
                }
            }
            if(studentCompleted.length()>0){
                studentCompleted.deleteCharAt(studentCompleted.length()-1);
            }
            if(studentNotCompleted.length()>0){
                studentNotCompleted.deleteCharAt(studentNotCompleted.length()-1);
            }
            homework.setStudentCompleted(studentCompleted.toString());
            homework.setStudentNotCompleted(studentNotCompleted.toString());
        }
        return homework;
    }
}



