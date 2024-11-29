package com.sjxm.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.model.entity.Chapter;
import com.sjxm.springbootinit.mapper.ChapterMapper;
import com.sjxm.springbootinit.service.ChapterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author sijixiamu
 * @description 针对表【chapter】的数据库操作Service实现
 * @createDate 2024-11-26 18:07:37
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter>
        implements ChapterService {

    @Override
    public List<Chapter> myList(Long subjectId) {
        LambdaQueryWrapper<Chapter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Chapter::getSubjectId, subjectId);
        List<Chapter> list = this.list(lambdaQueryWrapper);

        List<Chapter> fatherList = list.stream().filter(item -> item.getIsFather() == 1
        ).collect(Collectors.toList());

        List<Chapter> childrenList = list.stream().filter(item -> item.getIsFather() == 0).collect(Collectors.toList());

        fatherList.forEach(father->{
            List<Chapter> collect = childrenList.stream().filter(item -> Objects.equals(item.getFatherChapterId(), father.getId())).collect(Collectors.toList());
            father.setChildrenList(collect);
        });

        return fatherList;
    }
}




