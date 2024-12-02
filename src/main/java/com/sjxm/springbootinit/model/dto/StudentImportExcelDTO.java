package com.sjxm.springbootinit.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/2
 * @Description:
 */

@Data
public class StudentImportExcelDTO {

    @ExcelProperty("学号")
    private String userId;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("联系电话")
    private String phone;
}
