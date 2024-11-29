package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/29
 * @Description:
 */
@Data
public class ChapterAddOrUpdateDTO implements Serializable {

    private Long id;

    private String title;

    private List<ChildrenChapter> childrenList;

    private Long subjectId;

    private String chapterNum;

    @Data
    public static class ChildrenChapter implements Serializable{
        private Long id;
        private String chapterNum;
        private String title;
        private String url;
        private Long subjectId;
    }

}
