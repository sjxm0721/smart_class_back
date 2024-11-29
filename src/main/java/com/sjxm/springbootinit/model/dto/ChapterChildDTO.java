package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/29
 * @Description:
 */
@Data
public class ChapterChildDTO implements Serializable {

    private Long id;
    private String chapterNum;
    private String title;
    private String url;


}
