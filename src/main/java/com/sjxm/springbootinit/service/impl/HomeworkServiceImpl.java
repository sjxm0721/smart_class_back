package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.NoEnoughAuthException;
import com.sjxm.springbootinit.mapper.HomeworkMapper;
import com.sjxm.springbootinit.model.dto.HomeworkAddDTO;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.entity.Homework;
import com.sjxm.springbootinit.model.entity.Student;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.HomeworkService;
import com.sjxm.springbootinit.service.StudentService;
import com.sjxm.springbootinit.utils.DateTransferUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
        List<Student> listStudent = null;
        List<Class> listClass = null;
        if(type==0){
            //布置给全班
            String classIdList = homework.getClassIdList();
            String[] allClassStr = new String[0];
            if(!StrUtil.isBlankIfStr(classIdList)){
                allClassStr = classIdList.split(",");
            }
            StringBuilder sb = new StringBuilder();
            LambdaQueryWrapper<Class> classLambdaQueryWrapper = new LambdaQueryWrapper<>();
            classLambdaQueryWrapper.in(allClassStr.length>0,Class::getClassId, allClassStr);
            listClass = classService.list(classLambdaQueryWrapper);
            for(int i =0;i<listClass.size();i++){
                sb.append(listClass.get(i).getClassName()).append("、");
            }
            StringBuilder target = sb.deleteCharAt(sb.length() - 1);
            homework.setTarget(target.toString());

            LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentLambdaQueryWrapper.in(!CollUtil.isEmpty(listClass),Student::getClassId,listClass);
            listStudent = studentService.list(studentLambdaQueryWrapper);
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
            listStudent = studentService.list(studentLambdaQueryWrapper);
            for(int i =0;i<listStudent.size();i++){
                sb.append(listStudent.get(i).getStudentName()).append("、");
            }
            StringBuilder target = sb.deleteCharAt(sb.length() - 1);
            homework.setTarget(target.toString());
        }
        String completedStudentId = homework.getCompletedStudentId();
        if(!StrUtil.isBlankIfStr(completedStudentId)){
            strings = completedStudentId.split(",");
        }
        if(!CollUtil.isEmpty(listStudent)){
            Set<Long> sets = new HashSet<>();
            if (strings != null) {
                sets = Arrays.stream(strings).map(Long::parseLong).collect(Collectors.toSet());
            }
            StringBuilder studentCompleted = new StringBuilder();
            StringBuilder studentNotCompleted = new StringBuilder();
            for (Student student : listStudent) {
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

    @Override
    public void add(HomeworkAddDTO homeworkAddDTO,Long accountId) {
        if(accountId==null){
            throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
        }
        String content = homeworkAddDTO.getContent();
        String completeTime = homeworkAddDTO.getCompleteTime();
        List<String> resources = homeworkAddDTO.getResources();
        List<Long> studentIdList = homeworkAddDTO.getStudentIdList();
        String sightedTime = homeworkAddDTO.getSightedTime();
        String title = homeworkAddDTO.getTitle();
        Integer type = homeworkAddDTO.getType();
        List<String> classIdList = homeworkAddDTO.getClassIdList();

        Date completeDate = DateTransferUtil.transfer(LocalDateTime.parse(completeTime.replace(" ", "T")));
        Date sightedDate = DateTransferUtil.transfer(LocalDateTime.parse(sightedTime.replace(" ", "T")));
        StringBuilder sb = new StringBuilder();
        resources.forEach(resource->{
            sb.append(resource).append(" ");
        });
        sb.deleteCharAt(sb.length()-1);

        Integer isSight = sightedDate.before(new Date())?1:0;

        Homework homework = Homework.builder()
                .teacherId(accountId)
                .isSighted(isSight)
                .sightedTime(sightedDate)
                .type(type)
                .content(content)
                .resources(sb.toString())
                .completeTime(completeDate)
                .title(title).build();

        if(type==0){
            //布置给班级
            StringBuilder sb2 = new StringBuilder();
            classIdList.forEach(classId->{
                sb2.append(classId).append(",");
            });
            sb2.deleteCharAt(sb2.length()-1);
            homework.setClassIdList(sb2.toString());
        }
        else{
            //布置给个人
            StringBuilder sb2 = new StringBuilder();
            studentIdList.forEach(studentId->{
                sb2.append(studentId).append(",");
            });
            sb2.deleteCharAt(sb2.length()-1);
            homework.setStudentIdList(sb2.toString());
        }

        this.save(homework);


    }
}




