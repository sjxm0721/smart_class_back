package com.sjxm.springbootinit.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备类型枚举
 *
 * @author <a href="https://github.com/sjxm0721">四季夏目</a>

 */
@Getter
public enum DeviceTypeEnum {

    //'笔记本电脑', '平板电脑', '投影仪', '其他'
    LAPTOP("笔记本电脑", 0),
    TABLET("平板电脑", 1),
    PROJECTOR("投影仪", 2),
    OTHER("其他",3);

    private final String text;

    private final Integer value;

    DeviceTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static DeviceTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (DeviceTypeEnum anEnum : DeviceTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
