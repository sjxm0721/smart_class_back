package com.sjxm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjxm.springbootinit.model.dto.AccountAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.AccountDTO;
import com.sjxm.springbootinit.model.dto.AccountPageQueryDTO;
import com.sjxm.springbootinit.model.dto.PasswordChangeDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.vo.AccountPageVO;
import com.sjxm.springbootinit.result.PageResult;

/**
* @author sijixiamu
* @description 针对表【account】的数据库操作Service
* @createDate 2024-11-18 19:40:07
*/
public interface AccountService extends IService<Account> {

    Account login(AccountDTO accountDTO);

    Account tokenLogin(String userId);

    void editPassword(PasswordChangeDTO passwordMsg);

    AccountPageVO info(String userId);

    PageResult myPage(AccountPageQueryDTO accountPageQueryDTO);

    void status(String userId);

    void add(AccountAddOrUpdateDTO accountInfo);

    void myUpdate(AccountAddOrUpdateDTO accountInfo);

    void delete(Integer accountId);

    long teacherNumber(Integer schoolId, Integer classId);
}
