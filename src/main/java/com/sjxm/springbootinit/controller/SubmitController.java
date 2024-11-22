package com.sjxm.springbootinit.controller;

import com.sjxm.springbootinit.model.dto.SubmitQueryDTO;
import com.sjxm.springbootinit.model.entity.Submit;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.SubmitService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
}
