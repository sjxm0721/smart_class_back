package com.sjxm.springbootinit.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/19
 * @Description:
 */
public class DateTransferUtil {
    public static Date transfer(LocalDateTime localDateTime){
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
}
