package com.sjxm.springbootinit.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/2
 * @Description:
 */

@Data
public class DeviceImportExcelDTO {

    @ExcelProperty("设备编号")
    private String deviceName;

}
