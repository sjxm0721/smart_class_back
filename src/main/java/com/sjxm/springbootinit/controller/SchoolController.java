package com.sjxm.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.SchoolNotExistException;
import com.sjxm.springbootinit.model.dto.SchoolDTO;
import com.sjxm.springbootinit.model.dto.SchoolPageQueryDTO;
import com.sjxm.springbootinit.model.entity.School;
import com.sjxm.springbootinit.model.vo.SchoolVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.SchoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/school")
@Slf4j
@Api(tags = "学校相关接口")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/info")
    @ApiOperation("获取学校分页信息")
    public Result<PageResult> info(SchoolPageQueryDTO schoolPageQueryDTO){

        PageResult page = schoolService.pageQuery(schoolPageQueryDTO);

        return Result.success(page);
    }

    @PostMapping("/add")
    @ApiOperation("添加学校")
    public Result add(@RequestBody SchoolDTO schoolDTO){
        School school = new School();
        BeanUtil.copyProperties(schoolDTO,school);
        school.setClassNum(0);
        school.setDeviceNum(0);
        schoolService.save(school);
        return Result.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除学校")
    public Result delete(Long schoolId){

        schoolService.delete(schoolId);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改学校信息")
    public Result update(@RequestBody SchoolDTO schoolDTO){
        School school = schoolService.getById(schoolDTO.getSchoolId());
        if(school==null){
            throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
        }
        BeanUtil.copyProperties(schoolDTO,school);
        schoolService.updateById(school);
        return Result.success();
    }


    @GetMapping("/search/{schoolId}")
    @ApiOperation("根据id查询学校信息")
    public Result<SchoolVO> searchBySchoolId(@PathVariable Long schoolId){


        SchoolVO schoolVO = schoolService.searchBySchoolId(schoolId);

        return Result.success(schoolVO);
    }

    @GetMapping("/list")
    @ApiOperation("获取所有学校信息的列表")
    public Result<List<SchoolVO>> list(){
        List<SchoolVO> list = schoolService.myList();

        return Result.success(list);
    }
}
