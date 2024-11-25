package com.sjxm.springbootinit.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 *
 * @author <a href="https://github.com/sjxm0721">四季夏目</a>
 
 */
@Getter
public enum AccountRoleEnum {

    STUDENT("学生", 0),
    ADMIN("教师管理员", 1),
    TEACHER("教师", 2);

    private final String text;

    private final Integer value;

    AccountRoleEnum(String text, Integer value) {
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
    public static AccountRoleEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (AccountRoleEnum anEnum : AccountRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
