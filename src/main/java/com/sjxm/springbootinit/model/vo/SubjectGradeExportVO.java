package com.sjxm.springbootinit.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/5
 * @Description:
 */
@Data
public class SubjectGradeExportVO {

    @ExcelProperty(value = "作业名")
    @ColumnWidth(20)
    private String homeworkName;

    @ExcelProperty(value = "课程名")
    @ColumnWidth(20)
    private String subjectName;

    @ExcelProperty(value = "学生姓名")
    @ColumnWidth(15)
    private String studentName;

    @ExcelProperty(value = "分数")
    @ColumnWidth(15)
    private BigDecimal score;

    @ExcelProperty(value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(value = "评价")
    @ColumnWidth(30)
    private String comment;
}


