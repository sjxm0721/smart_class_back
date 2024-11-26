package com.sjxm.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.model.entity.Chapter;
import com.sjxm.springbootinit.mapper.ChapterMapper;
import com.sjxm.springbootinit.service.ChapterService;
import org.springframework.stereotype.Service;

/**
* @author sijixiamu
* @description 针对表【chapter】的数据库操作Service实现
* @createDate 2024-11-26 18:07:37
*/
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter>
    implements ChapterService{

}




