package com.sjxm.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.constant.PasswordConstant;
import com.sjxm.springbootinit.context.BaseContext;
import com.sjxm.springbootinit.exception.*;
import com.sjxm.springbootinit.model.dto.*;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.mapper.AccountMapper;
import com.sjxm.springbootinit.model.entity.Class;
import com.sjxm.springbootinit.model.entity.School;
import com.sjxm.springbootinit.model.vo.AccountPageVO;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.ClassService;
import com.sjxm.springbootinit.service.SchoolService;
import com.sjxm.springbootinit.utils.ExcelImportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sijixiamu
 * @description 针对表【account】的数据库操作Service实现
 * @createDate 2024-11-18 19:40:07
 */
@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
        implements AccountService {

    @Resource
    @Lazy
    private SchoolService schoolService;

    @Resource
    @Lazy
    private ClassService classService;

    public Account getByUserId(String userId) {
        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Account::getUserId, userId);
        return this.getOne(lambdaQueryWrapper);
    }

    private AccountPageVO obj2PageVO(Account account) {
        Long schoolId = account.getSchoolId();
        Long classId = account.getClassId();
        Integer auth = account.getAuth();
        Integer status = account.getStatus();
        AccountPageVO accountPageVO = new AccountPageVO();
        BeanUtils.copyProperties(account, accountPageVO);
        if (auth == 0) {
            accountPageVO.setAuthValue("学生");
        } else if (auth == 1) {
            accountPageVO.setAuthValue("管理员");
        } else {
            accountPageVO.setAuthValue("教师");
        }
        if (status == 1) {
            accountPageVO.setStatusValue("启用");
        } else {
            accountPageVO.setStatusValue("禁用");
        }
        if (schoolId != null) {
            School school = schoolService.getById(schoolId);
            if (school == null) {
                throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
            }
            accountPageVO.setSchoolName(school.getSchoolName());
        }
        if (classId != null) {

            Class myClass = classService.getById(classId);
            if (myClass == null) {
                throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
            }
            accountPageVO.setClassName(myClass.getClassName());
        }
        return accountPageVO;
    }


    @Override
    public Account login(AccountDTO accountDTO) {
        String userId = accountDTO.getUserId();
        String password = accountDTO.getPassword();

        Account account = this.getByUserId(userId);

        if (account == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }


        if (!password.equals(account.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (account.getStatus() == 0) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return account;
    }

    @Override
    public Account tokenLogin(String userId) {
        Account account = this.getById(userId);
        if (account == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if (account.getStatus() == 0) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return account;
    }

    @Override
    public void editPassword(PasswordChangeDTO passwordMsg) {
        Integer accountId = passwordMsg.getAccountId();
        ;
        String newPassword = passwordMsg.getNewPassword();
        String oldPassword = passwordMsg.getOldPassword();

        Account account = this.getById(accountId);
        if (account == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        String truePassword = account.getPassword();
        if (!truePassword.equals(oldPassword)) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        account.setPassword(newPassword);
        this.updateById(account);
    }

    @Override
    public AccountPageVO info(String userId) {
        Account account = this.getByUserId(userId);

        if (account == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return this.obj2PageVO(account);
    }

    @Override
    public PageResult myPage(AccountPageQueryDTO accountPageQueryDTO) {
        Page<Account> page = new Page<>(accountPageQueryDTO.getCurrentPage(), accountPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(accountPageQueryDTO.getAuth() != null, Account::getAuth, accountPageQueryDTO.getAuth())
                .like(!StrUtil.isBlankIfStr(accountPageQueryDTO.getInput()), Account::getName, accountPageQueryDTO.getInput())
                .eq(accountPageQueryDTO.getSchoolId() != null, Account::getSchoolId, accountPageQueryDTO.getSchoolId())
                .orderBy(true, false, Account::getUpdateTime);
        Page<Account> newPage = this.page(page, lambdaQueryWrapper);
        long total = newPage.getTotal();
        List<Account> result = newPage.getRecords();
        List<AccountPageVO> list = new ArrayList<>();
        for (Account account : result) {
            list.add(this.obj2PageVO(account));
        }
        return new PageResult(total, list);
    }

    @Override
    public void status(String userId) {
        //根据accountId获取对象
        Account account = this.getByUserId(userId);

        if (account == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        } else if (account.getAuth() == 1) {
            throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
        }
        LambdaUpdateWrapper<Account> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Account::getUserId, account.getUserId())
                .set(Account::getStatus, account.getStatus() ^ 1)
                .set(Account::getUpdateUser, account.getUpdateUser());
        this.update(lambdaUpdateWrapper);
    }

    @Override
    @Transactional
    public void add(AccountAddOrUpdateDTO accountInfo) {
        Integer auth = accountInfo.getAuth();
        Long schoolId = accountInfo.getSchoolId();
        Long classId = accountInfo.getClassId();

        Account account = new Account();
        BeanUtils.copyProperties(accountInfo, account);
        account.setPassword(PasswordConstant.DEFAULT_PASSWORD);
        account.setStatus(1);
        this.save(account);

    }

    @Override
    @Transactional
    public void myUpdate(AccountAddOrUpdateDTO accountInfo) {
        Long accountId = accountInfo.getAccountId();
        String userId = accountInfo.getUserId();
        String name = accountInfo.getName();
        String phone = accountInfo.getPhone();
        String email = accountInfo.getEmail();
        String avatar = accountInfo.getAvatar();
        Long schoolId = accountInfo.getSchoolId();
        Long classId = accountInfo.getClassId();
        Integer auth = accountInfo.getAuth();

        LambdaUpdateWrapper<Account> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(!StrUtil.isBlankIfStr(userId),Account::getUserId,userId)
                .set(!StrUtil.isBlankIfStr(name),Account::getName,name)
                .set(!StrUtil.isBlankIfStr(phone),Account::getPhone,phone)
                .set(!StrUtil.isBlankIfStr(email),Account::getEmail,email)
                .set(!StrUtil.isBlankIfStr(avatar),Account::getAvatar,avatar)
                .set(schoolId!=null,Account::getSchoolId,schoolId)
                .set(classId!=null,Account::getClassId,classId)
                .set(auth!=null,Account::getAuth,auth)
                .eq(Account::getAccountId,accountId);
        this.update(lambdaUpdateWrapper);
    }

        @Override
        @Transactional
        public void delete (Long accountId){
            this.removeById(accountId);
        }


        @Override
        public Account getLoginUser () {
            String currentId = BaseContext.getCurrentId();
            return this.getById(Long.parseLong(currentId));
        }


        @Override
        public void importStudents (InputStream inputStream, Long classId){
            ExcelImportUtil.importExcel(inputStream, StudentImportExcelDTO.class, classId, this::excelAddStudents);
        }

        @Override
        public void importTeachers (InputStream inputStream, Long schoolId){
            ExcelImportUtil.importExcel(inputStream, TeacherImportExcelDTO.class, schoolId, this::excelAddTeachers);
        }

        @Transactional(rollbackFor = Exception.class)
        public void excelAddStudents (List < StudentImportExcelDTO > list, Long classId){
            List<String> errorMsgs = new ArrayList<>();
            for (StudentImportExcelDTO studentImportExcelDTO : list) {
                String errorMsg = validateStudentExcel(studentImportExcelDTO);
                if (errorMsg != null) {
                    errorMsgs.add(String.format("第%s行：%s", studentImportExcelDTO.getUserId(), errorMsg));
                }
            }

            // 如果存在校验错误，抛出异常
            if (!errorMsgs.isEmpty()) {
                log.info("Excel数据校验失败：\n{}", String.join("\n", errorMsgs));
                throw new ExcelImportException(MessageConstant.EXCEL_IMPORT_ERROR);
            }

            // 2. 数据转换
            List<Account> accountList = list.stream()
                    .map(studentImportExcelDTO -> convertToStudentAccount(studentImportExcelDTO, classId))
                    .collect(Collectors.toList());

            // 3. 数据去重（假设根据用户名去重）
            List<String> userIds = accountList.stream()
                    .map(Account::getUserId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
            accountLambdaQueryWrapper.in(Account::getUserId, userIds);
            List<Account> existingStudents = this.list(accountLambdaQueryWrapper);
            Map<String, Account> existingStudentMap = existingStudents.stream()
                    .collect(Collectors.toMap(Account::getUserId, user -> user));

            // 4. 分离新增和更新的数据
            List<Account> toInsert = new ArrayList<>();
            List<Account> toUpdate = new ArrayList<>();

            for (Account account : accountList) {
                if (existingStudentMap.containsKey(account.getUserId())) {
                    // 设置ID等必要字段
                    Account existAccount = existingStudentMap.get(account.getUserId());
                    account.setAccountId(existAccount.getAccountId());
                    // 设置不应被更新的字段
                    account.setCreateTime(existAccount.getCreateTime());
                    account.setCreateUser(existAccount.getCreateUser());
                    toUpdate.add(account);
                } else {
                    // 设置新增记录的默认值
                    account.setCreateTime(new Date());
                    account.setCreateUser(getLoginUser().getName());
                    toInsert.add(account);
                }
            }

            // 5. 批量保存数据
            if (!toInsert.isEmpty()) {
                this.saveBatch(toInsert);
                log.info("批量插入{}条数据", toInsert.size());
            }

            if (!toUpdate.isEmpty()) {
                this.updateBatchById(toUpdate);
                log.info("批量更新{}条数据", toUpdate.size());
            }


        }

        private String validateStudentExcel (StudentImportExcelDTO studentImportExcelDTO){
            StringBuilder errorMsg = new StringBuilder();

            // 校验必填字段
            if (StrUtil.isBlank(studentImportExcelDTO.getName())) {
                errorMsg.append("姓名不能为空；");
            } else if (studentImportExcelDTO.getName().length() > 50) {
                errorMsg.append("姓名长度不能超过50个字符；");
            }


//        // 校验邮箱格式（如果有）
//        if (StrUtil.isNotBlank(studentImportExcelDTO.getEmail())) {
//            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
//            if (!userExcel.getEmail().matches(emailRegex)) {
//                errorMsg.append("邮箱格式不正确；");
//            }
//        }

            // 校验手机号格式（如果有）
            if (StrUtil.isNotBlank(studentImportExcelDTO.getPhone())) {
                String phoneRegex = "^1[3-9]\\d{9}$";
                if (!studentImportExcelDTO.getPhone().matches(phoneRegex)) {
                    errorMsg.append("手机号格式不正确；");
                }
            }

            return errorMsg.length() > 0 ? errorMsg.toString() : null;
        }

        /**
         * 转换Excel数据为实体类
         */
        private Account convertToStudentAccount (StudentImportExcelDTO studentImportExcelDTO, Long classId){
            Account account = new Account();
            // 基本信息转换
            account.setUserId(studentImportExcelDTO.getUserId());
            account.setName(studentImportExcelDTO.getName());
            account.setPhone(studentImportExcelDTO.getPhone());

            Class aClass = classService.getById(classId);
            Long schoolId = aClass.getSchoolId();
            account.setClassId(classId);
            account.setSchoolId(schoolId);

            account.setPassword("123456");
            account.setAuth(0);
            account.setCreateUser(getLoginUser().getName());
            account.setUpdateUser(getLoginUser().getName());

            return account;
        }

        @Transactional(rollbackFor = Exception.class)
        public void excelAddTeachers (List < TeacherImportExcelDTO > list, Long schoolId){
            List<String> errorMsgs = new ArrayList<>();
            for (TeacherImportExcelDTO teacherImportExcelDTO : list) {
                String errorMsg = validateTeacherExcel(teacherImportExcelDTO);
                if (errorMsg != null) {
                    errorMsgs.add(String.format("第%s行：%s", teacherImportExcelDTO.getUserId(), errorMsg));
                }
            }

            // 如果存在校验错误，抛出异常
            if (!errorMsgs.isEmpty()) {
                log.info("Excel数据校验失败：\n{}", String.join("\n", errorMsgs));
                throw new ExcelImportException(MessageConstant.EXCEL_IMPORT_ERROR);
            }

            // 2. 数据转换
            List<Account> accountList = list.stream()
                    .map(teacherImportExcelDTO -> convertToTeacherAccount(teacherImportExcelDTO, schoolId))
                    .collect(Collectors.toList());

            // 3. 数据去重（假设根据用户名去重）
            List<String> userIds = accountList.stream()
                    .map(Account::getUserId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
            accountLambdaQueryWrapper.in(Account::getUserId, userIds);
            List<Account> existingStudents = this.list(accountLambdaQueryWrapper);
            Map<String, Account> existingStudentMap = existingStudents.stream()
                    .collect(Collectors.toMap(Account::getUserId, user -> user));

            // 4. 分离新增和更新的数据
            List<Account> toInsert = new ArrayList<>();
            List<Account> toUpdate = new ArrayList<>();

            for (Account account : accountList) {
                if (existingStudentMap.containsKey(account.getUserId())) {
                    // 设置ID等必要字段
                    Account existAccount = existingStudentMap.get(account.getUserId());
                    account.setAccountId(existAccount.getAccountId());
                    // 设置不应被更新的字段
                    account.setCreateTime(existAccount.getCreateTime());
                    account.setCreateUser(existAccount.getCreateUser());
                    toUpdate.add(account);
                } else {
                    // 设置新增记录的默认值
                    account.setCreateTime(new Date());
                    account.setCreateUser(getLoginUser().getName());
                    toInsert.add(account);
                }
            }

            // 5. 批量保存数据
            if (!toInsert.isEmpty()) {
                this.saveBatch(toInsert);
                log.info("批量插入{}条数据", toInsert.size());
            }

            if (!toUpdate.isEmpty()) {
                this.updateBatchById(toUpdate);
                log.info("批量更新{}条数据", toUpdate.size());
            }

        }

        private String validateTeacherExcel (TeacherImportExcelDTO teacherImportExcelDTO){
            StringBuilder errorMsg = new StringBuilder();

            // 校验必填字段
            if (StrUtil.isBlank(teacherImportExcelDTO.getName())) {
                errorMsg.append("姓名不能为空；");
            } else if (teacherImportExcelDTO.getName().length() > 50) {
                errorMsg.append("姓名长度不能超过50个字符；");
            }


//        // 校验邮箱格式（如果有）
//        if (StrUtil.isNotBlank(studentImportExcelDTO.getEmail())) {
//            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
//            if (!userExcel.getEmail().matches(emailRegex)) {
//                errorMsg.append("邮箱格式不正确；");
//            }
//        }

            // 校验手机号格式（如果有）
            if (StrUtil.isNotBlank(teacherImportExcelDTO.getPhone())) {
                String phoneRegex = "^1[3-9]\\d{9}$";
                if (!teacherImportExcelDTO.getPhone().matches(phoneRegex)) {
                    errorMsg.append("手机号格式不正确；");
                }
            }

            return errorMsg.length() > 0 ? errorMsg.toString() : null;
        }

        /**
         * 转换Excel数据为实体类
         */
        private Account convertToTeacherAccount (TeacherImportExcelDTO studentImportExcelDTO, Long schoolId){
            Account account = new Account();
            // 基本信息转换
            account.setUserId(studentImportExcelDTO.getUserId());
            account.setName(studentImportExcelDTO.getName());
            account.setPhone(studentImportExcelDTO.getPhone());
            account.setSchoolId(schoolId);

            account.setPassword("123456");
            account.setAuth(2);
            account.setCreateUser(getLoginUser().getName());
            account.setUpdateUser(getLoginUser().getName());

            return account;
        }


    }




