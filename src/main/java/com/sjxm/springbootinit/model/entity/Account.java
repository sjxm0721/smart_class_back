package com.sjxm.springbootinit.model.entity;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sjxm.springbootinit.model.vo.AccountVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName account
 */
@TableName(value ="account")
@Data
public class Account implements Serializable {
    /**
     * 账号id,用于标识一个账号
     */
    @TableId(type = IdType.AUTO)
    private Integer accountId;

    /**
     * 用户名，登陆账号时使用

     */
    private String userId;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 存放头像地址
     */
    private String avatar;

    /**
     * 1表示总管理员，2表示校管理员，3表示班主任
     */
    private Integer auth;

    /**
     * 所属学校

     */
    private Integer schoolId;

    /**
     * 所属班级
     */
    private Integer classId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 默认1 为启用 0为禁用
     */
    private Integer status;

    /**
     * 创建时间
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
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static AccountVO obj2VO(Account account){
        AccountVO accountVO = new AccountVO();
        BeanUtil.copyProperties(account,accountVO);
        return accountVO;
    }
}