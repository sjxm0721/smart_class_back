package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.factory.ScoreStatusFactory;
import com.sjxm.springbootinit.model.dto.SubjectAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.SubjectQueryDTO;
import com.sjxm.springbootinit.model.entity.*;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.vo.SubjectGradeExportVO;
import com.sjxm.springbootinit.service.*;
import com.sjxm.springbootinit.mapper.SubjectMapper;
import com.sjxm.springbootinit.utils.DateTransferUtil;
import com.sjxm.springbootinit.utils.ExcelExportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author sijixiamu
* @description 针对表【subject】的数据库操作Service实现
* @createDate 2024-11-25 17:20:03
*/
@Service
@Slf4j
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject>
    implements SubjectService{

//    @Resource
//    private StudentService studentService;

    @Resource
    @Lazy
    private HomeworkService homeworkService;

    @Resource
    private ClassService classService;

    @Resource
    @Lazy
    private SubmitService submitService;

    @Resource
    private AccountService accountService;

    private Subject obj2VO1(Subject subject){
        Long classId = subject.getClassId();
        Long teacherId = subject.getTeacherId();
        Class aClass = classService.getById(classId);
        Account account = accountService.getById(teacherId);
        subject.setClassName(aClass.getClassName());
        subject.setTeacherName(account.getName());
        return subject;
    }

    @Override
    public List<Subject> myList(SubjectQueryDTO subjectQueryDTO) {
        Long studentId = subjectQueryDTO.getStudentId();
        Long teacherId = subjectQueryDTO.getTeacherId();
        String input = subjectQueryDTO.getInput();


        LambdaQueryWrapper<Subject> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(studentId!=null){
            Account student = accountService.getById(studentId);
            Long classId = student.getClassId();
            lambdaQueryWrapper.eq(Subject::getClassId,classId);
        }
        else{
            lambdaQueryWrapper.eq(Subject::getTeacherId,teacherId);
        }

        lambdaQueryWrapper.like(!StrUtil.isBlankIfStr(input),Subject::getTitle,input);
        List<Subject> list = this.list(lambdaQueryWrapper);

        return list.stream().map(this::obj2VO1).collect(Collectors.toList());

    }

    /**
     * 添加或修改课程
     * @param subjectAddOrUpdateDTO
     */
    @Override
    public void addOrUpdate(SubjectAddOrUpdateDTO subjectAddOrUpdateDTO) {

        String endTime = subjectAddOrUpdateDTO.getEndTime();
        Date startDate = new Date();
        Date endDate = DateTransferUtil.transfer(LocalDateTime.parse(endTime.substring(0,endTime.length()-1)));
        Subject subject = new Subject();
        BeanUtil.copyProperties(subjectAddOrUpdateDTO,subject);
        subject.setStartTime(startDate);
        subject.setEndTime(endDate);
        this.saveOrUpdate(subject);
    }

    @Override
    public Subject info(Long subjectId) {
        log.info(subjectId.toString());
        Subject subject = this.getById(subjectId);
        return obj2VO1(subject);
    }

    @Override
    public void export(Long subjectId, HttpServletResponse response) {
        try {
            List<SubjectGradeExportVO> exportVOS = new ArrayList<>();
            Subject subject = this.getById(subjectId);
            LambdaQueryWrapper<Homework> homeworkLambdaQueryWrapper = new LambdaQueryWrapper<>();
            homeworkLambdaQueryWrapper.eq(Homework::getSubjectId,subjectId);
            List<Homework> list = homeworkService.list(homeworkLambdaQueryWrapper);
            Set<Long> homeworkIds = list.stream().map(Homework::getId).collect(Collectors.toSet());
            if(!CollUtil.isEmpty(homeworkIds)){
                LambdaQueryWrapper<Submit> submitLambdaQueryWrapper = new LambdaQueryWrapper<>();
                submitLambdaQueryWrapper.in(Submit::getHomeworkId,homeworkIds).eq(Submit::getIsCorrected,1);
                List<Submit> submitList = submitService.list(submitLambdaQueryWrapper);
                for (Submit submit : submitList) {
                    SubjectGradeExportVO subjectGradeExportVO = new SubjectGradeExportVO();
                    Homework homework = homeworkService.getById(submit.getHomeworkId());
                    subjectGradeExportVO.setHomeworkName(homework.getTitle());
                    subjectGradeExportVO.setSubjectName(subject.getTitle());
                    Account account = accountService.getById(submit.getStudentId());
                    subjectGradeExportVO.setStudentName(account.getName());
                    subjectGradeExportVO.setScore(submit.getScore());
                    subjectGradeExportVO.setStatus(ScoreStatusFactory.getStatus(submit.getScore()));
                    subjectGradeExportVO.setComment(submit.getComment());
                    exportVOS.add(subjectGradeExportVO);
                }
            }
            String fileName = subject.getTitle()+"学生成绩表";
            ExcelExportUtil.exportExcel(response, fileName, exportVOS, SubjectGradeExportVO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




