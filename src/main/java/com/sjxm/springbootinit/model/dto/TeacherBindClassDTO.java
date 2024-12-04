package com.sjxm.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/3
 * @Description:
 */
@Data
public class TeacherBindClassDTO implements Serializable {

    private Long classId;

    private List<Long> teacherIds;

}
