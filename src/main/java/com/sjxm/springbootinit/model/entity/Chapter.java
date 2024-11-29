package com.sjxm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName chapter
 */
@TableName(value ="chapter")
@Data
public class Chapter implements Serializable {
    /**
     * 章节id，主键
     */
    @TableId
    private Long id;

    /**
     * 所属课程id，逻辑id
     */
    private Long subjectId;

    /**
     * 课程号
     */
    private String chapterNum;

    /**
     * 是否是父？1-父、0-子。父表示具体的一个大章节，子表示大章节中具体的一小章节
     */
    private Integer isFather;

    /**
     * 当is_father为0时，需要father_chapter_id标识它属于哪一大章节
     */
    private Long fatherChapterId;

    /**
     * 标题
     */
    private String title;

    /**
     * 当is_father为0，需要url，表示这章的视频资源url
     */
    private String url;

    @TableField(exist = false)
    private List<Chapter> childrenList;

    /**
     * 测试时间
     */
    private Date createTime;

    /**
     * 
     */
    private String createUser;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 
     */
    private String updateUser;

    /**
     * 
     */
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}