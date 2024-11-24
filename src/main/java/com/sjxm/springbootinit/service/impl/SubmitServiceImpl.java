package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.common.ErrorCode;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.BaseException;
import com.sjxm.springbootinit.exception.BusinessException;
import com.sjxm.springbootinit.exception.SubmitNotFoundException;
import com.sjxm.springbootinit.mapper.SubmitMapper;
import com.sjxm.springbootinit.model.dto.SubmitQueryDTO;
import com.sjxm.springbootinit.model.entity.Homework;
import com.sjxm.springbootinit.model.entity.Submit;
import com.sjxm.springbootinit.service.HomeworkService;
import com.sjxm.springbootinit.service.SubmitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author sijixiamu
* @description 针对表【submit】的数据库操作Service实现
* @createDate 2024-11-21 18:44:22
*/
@Service
public class SubmitServiceImpl extends ServiceImpl<SubmitMapper, Submit>
    implements SubmitService {

    @Resource
    private HomeworkService homeworkService;

    Submit obj2VO(Submit submit){
        Long homeworkId = submit.getHomeworkId();
        Homework homework = homeworkService.getById(homeworkId);
        submit.setHomeworkTitle(homework.getTitle());
        return submit;
    }

    @Resource
    private SubmitMapper submitMapper;

    @Override
    public List<Submit> myList(SubmitQueryDTO submitQueryDTO) {
        return submitMapper.myList(submitQueryDTO.getInput(),submitQueryDTO.getStatus(),submitQueryDTO.getStudentId(),submitQueryDTO.getTeacherId());
    }

    @Override
    public Submit info(Long submitId) {
        Submit submit = this.getById(submitId);
        if(submit==null){
            throw new SubmitNotFoundException(MessageConstant.SUBMIT_NOT_FOUND_ERROR);
        }
        return obj2VO(submit);
    }
}




