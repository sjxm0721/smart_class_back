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
public enum StatusTypeEnum {

//    0-申请中
//1-申请成功
//2-拒绝

    LAPTOP("申请中", 0),
    TABLET("申请成功", 1),
    PROJECTOR("拒绝", 2);

    private final String text;

    private final Integer value;

    StatusTypeEnum(String text, Integer value) {
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
    public static StatusTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (StatusTypeEnum anEnum : StatusTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
