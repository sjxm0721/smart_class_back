package com.sjxm.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.model.entity.Subject;
import com.sjxm.springbootinit.service.SubjectService;
import com.sjxm.springbootinit.mapper.SubjectMapper;
import org.springframework.stereotype.Service;

/**
* @author sijixiamu
* @description 针对表【subject】的数据库操作Service实现
* @createDate 2024-11-25 17:20:03
*/
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject>
    implements SubjectService{

}




