package com.sjxm.springbootinit.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sjxm.springbootinit.model.dto.CommentDTO;
import com.sjxm.springbootinit.model.dto.SubmitAddDTO;
import com.sjxm.springbootinit.model.dto.SubmitQueryDTO;
import com.sjxm.springbootinit.model.entity.Submit;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.SubmitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/22
 * @Description:
 */
@Api(tags = "作业提交记录控制器")
@RestController
@RequestMapping("/admin/submit")
@Slf4j
public class SubmitController {


    @Resource
    private SubmitService submitService;



    @GetMapping("/list")
    public Result<List<Submit>> list(SubmitQueryDTO submitQueryDTO){

        List<Submit> list = submitService.myList(submitQueryDTO);
        return Result.success(list);
    }
    @GetMapping("/info")
    public Result<Submit> info(Long submitId){

        return Result.success(submitService.info(submitId));
    }


    @PostMapping("/add")
    @ApiOperation("作答")
    public Result add(@RequestBody SubmitAddDTO submitAddDTO){
        Long homeworkId = submitAddDTO.getHomeworkId();
        Long studentId = submitAddDTO.getStudentId();
        String content = submitAddDTO.getContent();
        List<String> resources = submitAddDTO.getResources();

        Submit submit = Submit.builder()
                .homeworkId(homeworkId)
                .studentId(studentId)
                .submitTime(new Date())
                .content(content).build();
        StringBuilder sb = new StringBuilder();
        if(!CollUtil.isEmpty(resources)){
            resources.forEach((resource)->{
                sb.append(resource).append(",");
            });
            sb.deleteCharAt(sb.length()-1);
        }

        submit.setResources(sb.toString());
        submitService.save(submit);
        return Result.success();
    }


    @PostMapping("/give-comment")
    public Result giveComment(@RequestBody CommentDTO commentDTO){
        Long id = commentDTO.getId();
        Long score = commentDTO.getScore();
        String comment = commentDTO.getComment();

        LambdaUpdateWrapper<Submit> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(score!=null,Submit::getScore, BigDecimal.valueOf(score))
                .set(!StrUtil.isBlankIfStr(comment),Submit::getComment,comment)
                .set(Submit::getIsCorrected,1)
                .set(Submit::getCorrectTime,new Date())
                .eq(Submit::getId,id);
        submitService.update(lambdaUpdateWrapper);
        return Result.success();
    }
}
