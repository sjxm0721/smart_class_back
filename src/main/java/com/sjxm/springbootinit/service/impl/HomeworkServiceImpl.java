package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.NoEnoughAuthException;
import com.sjxm.springbootinit.mapper.HomeworkMapper;
import com.sjxm.springbootinit.model.dto.HomeWorkPageDTO;
import com.sjxm.springbootinit.model.dto.HomeworkAddDTO;
import com.sjxm.springbootinit.model.entity.*;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.service.*;
import com.sjxm.springbootinit.utils.DateTransferUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author sijixiamu
* @description 针对表【homework】的数据库操作Service实现
* @createDate 2024-11-26 11:12:46
*/
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework>
    implements HomeworkService{


    @Resource
    private StudentService studentService;

    @Resource
    private ClassService classService;

    @Resource
    private SubjectService subjectService;

    @Resource
    private AccountService accountService;

    @Resource
    @Lazy
    private SubmitService submitService;

    private Homework obj2VO(Homework homework){
        Long subjectId = homework.getSubjectId();
        LambdaQueryWrapper<Subject> subjectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        subjectLambdaQueryWrapper.eq(Subject::getId,subjectId);
        Subject subject = subjectService.getOne(subjectLambdaQueryWrapper);
        Long classId = subject.getClassId();
        Long teacherId = subject.getTeacherId();
        Class aClass = classService.getById(classId);
        Account account = accountService.getById(teacherId);
        homework.setClassId(classId);
        homework.setClassName(aClass.getClassName());
        homework.setTeacherId(teacherId);
        homework.setTeacherName(account.getName());

        LambdaQueryWrapper<Submit> submitLambdaQueryWrapper = new LambdaQueryWrapper<>();
        submitLambdaQueryWrapper.eq(Submit::getHomeworkId,homework.getId());
        List<Submit> list = submitService.list(submitLambdaQueryWrapper);
        homework.setCurNum(list.size());
        homework.setTotalNum(aClass.getStudentNum());

        Set<Long> set = list.stream().map(Submit::getStudentId).collect(Collectors.toSet());
        LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentLambdaQueryWrapper.notIn(!CollUtil.isEmpty(set),Student::getStudentId,set);
        List<Student> studentList = studentService.list(studentLambdaQueryWrapper);
        StringBuilder sb = new StringBuilder();
        studentList.forEach(student -> {
            sb.append(student.getStudentName()).append(",");
        });
        sb.deleteCharAt(sb.length()-1);
        homework.setUnCompleteStudentList(sb.toString());
        return homework;
    }


    /**
     * 教师或学生获取作业列表
     * @param homeWorkPageDTO
     * @return
     */
    @Override
    public List<Homework> listByTorSID(HomeWorkPageDTO homeWorkPageDTO) {
        Long teacherId = homeWorkPageDTO.getTeacherId();
        Long studentId = homeWorkPageDTO.getStudentId();
        String input = homeWorkPageDTO.getInput();
        if(teacherId==null&&studentId==null){
            throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
        }
        List<Subject> list = null;
        if(teacherId==null){
            //学生查询
            LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentLambdaQueryWrapper.eq(Student::getStudentId,studentId);
            Student student = studentService.getOne(studentLambdaQueryWrapper);
            Long classId = student.getClassId();
            LambdaQueryWrapper<Subject> subjectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            subjectLambdaQueryWrapper.eq(Subject::getClassId,classId);
            list = subjectService.list(subjectLambdaQueryWrapper);
        }
        else{
            //教师查询
            LambdaQueryWrapper<Subject> subjectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            subjectLambdaQueryWrapper.eq(Subject::getTeacherId,teacherId);
            list = subjectService.list(subjectLambdaQueryWrapper);

        }
        LambdaQueryWrapper<Homework> homeworkLambdaQueryWrapper = new LambdaQueryWrapper<>();
        homeworkLambdaQueryWrapper.in(!CollUtil.isEmpty(list),Homework::getSubjectId,list).like(!StrUtil.isBlankIfStr(input),Homework::getTitle,input);
        List<Homework> homeworkList = this.list(homeworkLambdaQueryWrapper);
        return homeworkList.stream().map(this::obj2VO).collect(Collectors.toList());
    }

    @Override
    public Homework info(Long homeworkId) {
        Homework homework = this.getById(homeworkId);
        return obj2VO(homework);
    }

    @Override
    public void add(HomeworkAddDTO homeworkAddDTO) {
        String content = homeworkAddDTO.getContent();
        String completeTime = homeworkAddDTO.getCompleteTime();
        List<String> resources = homeworkAddDTO.getResources();
        String sightedTime = homeworkAddDTO.getSightedTime();
        String title = homeworkAddDTO.getTitle();
        Long subjectId = homeworkAddDTO.getSubjectId();

        Date sightedDate = DateTransferUtil.transfer(LocalDateTime.parse(sightedTime.replace(" ","T")));
        Date completeDate = DateTransferUtil.transfer(LocalDateTime.parse(completeTime.replace(" ","T")));

        Homework homework = Homework.builder()
                .title(title)
                .content(content)
                .subjectId(subjectId)
                .sightedTime(sightedDate)
                .completeTime(completeDate).build();

        StringBuilder sb = new StringBuilder();
        resources.forEach(resource->{
            sb.append(resource).append(",");
        });
        sb.deleteCharAt(sb.length()-1);
        homework.setResources(sb.toString());
        this.save(homework);
    }
}




