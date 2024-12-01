package com.sjxm.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.mapper.MyResourceMapper;
import com.sjxm.springbootinit.model.entity.MyResource;
import com.sjxm.springbootinit.service.MyResourceService;
import org.springframework.stereotype.Service;

/**
* @author sijixiamu
* @description 针对表【resource】的数据库操作Service实现
* @createDate 2024-11-30 15:12:48
*/
@Service
public class MyResourceServiceImpl extends ServiceImpl<MyResourceMapper, MyResource>
    implements MyResourceService {

}




