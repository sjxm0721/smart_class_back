package com.sjxm.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.ChapterNotFoundException;
import com.sjxm.springbootinit.model.dto.ChapterAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.ChapterChildDTO;
import com.sjxm.springbootinit.model.entity.Chapter;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.ChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/admin/chapter")
@Api(tags = "章节相关接口")
public class ChapterController {

    @Resource
    private ChapterService chapterService;

    @GetMapping("/list")
    @ApiOperation("目录列表")
    public Result<List<Chapter>> list(Long subjectId) {
        List<Chapter> list = chapterService.myList(subjectId);
        return Result.success(list);
    }


    @GetMapping("/info")
    @ApiOperation("章节详情")
    public Result<Chapter> info(Long chapterId) {
        //todo 安全校验
        Chapter chapter = chapterService.getById(chapterId);
        return Result.success(chapter);
    }

    @PostMapping("delete/{chapterId}")
    @ApiOperation("删除章节")
    @Transactional
    public Result delete(@PathVariable Long chapterId) {
        LambdaQueryWrapper<Chapter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Chapter chapter = chapterService.getById(chapterId);
        if(chapter==null){
            throw new ChapterNotFoundException(MessageConstant.CHAPTER_NOT_FOUND_ERROR);
        }
        Integer isFather = chapter.getIsFather();
        if(isFather==1){
            //父
            lambdaQueryWrapper.eq(Chapter::getFatherChapterId, chapterId);
            chapterService.remove(lambdaQueryWrapper);
            chapterService.removeById(chapterId);
        }
        else{
            //子
            chapterService.removeById(chapterId);
        }

        return Result.success();
    }

    @PostMapping("/add-or-update")
    @ApiOperation("新增或修改章节")
    @Transactional
    public Result addOrUpdate(@RequestBody ChapterAddOrUpdateDTO chapterAddOrUpdateDTO) {
        Chapter chapter = new Chapter();
        BeanUtil.copyProperties(chapterAddOrUpdateDTO, chapter);
        chapter.setIsFather(1);
        chapterService.saveOrUpdate(chapter);
        List<ChapterAddOrUpdateDTO.ChildrenChapter> childrenChapters = chapterAddOrUpdateDTO.getChildrenList();
        if(!CollUtil.isEmpty(childrenChapters)){
            List<Chapter> chapterList = childrenChapters.stream().map(item -> {
                Chapter c = new Chapter();
                BeanUtil.copyProperties(item, c);
                c.setIsFather(0);
                c.setFatherChapterId(chapter.getId());
                return c;
            }).collect(Collectors.toList());
            chapterService.saveOrUpdateBatch(chapterList);
        }
        return Result.success();
    }

    @PostMapping("/update-child")
    @ApiOperation("修改子章节内容")
    public Result updateChildren(@RequestBody ChapterChildDTO chapterChildDTO){
        Long id = chapterChildDTO.getId();
        String chapterNum = chapterChildDTO.getChapterNum();
        String title = chapterChildDTO.getTitle();
        String url = chapterChildDTO.getUrl();

        LambdaUpdateWrapper<Chapter> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(!StrUtil.isBlankIfStr(chapterNum),Chapter::getChapterNum,chapterNum)
                .set(!StrUtil.isBlankIfStr(title),Chapter::getTitle,title)
                .set(!StrUtil.isBlankIfStr(url),Chapter::getUrl,url)
                .eq(Chapter::getId,id);
        chapterService.update(lambdaUpdateWrapper);
        return Result.success();
    }

}
