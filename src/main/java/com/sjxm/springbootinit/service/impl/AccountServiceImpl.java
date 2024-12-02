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
    implements AccountService{

    @Resource
    @Lazy
    private SchoolService schoolService;

    @Resource
    @Lazy
    private ClassService classService;

    public Account getByUserId(String userId){
        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Account::getUserId,userId);
        return this.getOne(lambdaQueryWrapper);
    }

    private AccountPageVO obj2PageVO(Account account){
        Long schoolId = account.getSchoolId();
        Long classId = account.getClassId();
        Integer auth = account.getAuth();
        Integer status = account.getStatus();
        AccountPageVO accountPageVO = new AccountPageVO();
        BeanUtils.copyProperties(account,accountPageVO);
        if(auth==1){
            accountPageVO.setAuthValue("总管理员");
        }
        else if(auth==2){
            accountPageVO.setAuthValue("校管理员");
        }
        else{
            accountPageVO.setAuthValue("教师");
        }
        if(status==1){
            accountPageVO.setStatusValue("启用");
        }
        else{
            accountPageVO.setStatusValue("禁用");
        }
        if(schoolId!=null){
            School school = schoolService.getById(schoolId);
            if(school==null){
                throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
            }
            accountPageVO.setSchoolName(school.getSchoolName());
        }
        if(classId!=null){

            Class myClass = classService.getById(classId);
            if(myClass == null){
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

        if(account == null){
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }


        if(!password.equals(account.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if(account.getStatus() == 0){
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return account;
    }

    @Override
    public Account tokenLogin(String userId) {
        Account account = this.getById(userId);
        if(account == null){
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if(account.getStatus() == 0){
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return account;
    }

    @Override
    public void editPassword(PasswordChangeDTO passwordMsg) {
        Integer accountId= passwordMsg.getAccountId();;
        String newPassword= passwordMsg.getNewPassword();
        String oldPassword=passwordMsg.getOldPassword();

        Account account = this.getById(accountId);
        if(account==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        String truePassword=account.getPassword();
        if(!truePassword.equals(oldPassword)){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        account.setPassword(newPassword);
        this.updateById(account);
    }

    @Override
    public AccountPageVO info(String userId) {
        Account account = this.getByUserId(userId);

        if(account==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return this.obj2PageVO(account);
    }

    @Override
    public PageResult myPage(AccountPageQueryDTO accountPageQueryDTO) {
        Page<Account> page = new Page<>(accountPageQueryDTO.getCurrentPage(), accountPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(accountPageQueryDTO.getAuth() != null, Account::getAuth, accountPageQueryDTO.getAuth())
                .eq(!StrUtil.isBlankIfStr(accountPageQueryDTO.getInput()), Account::getName, accountPageQueryDTO.getInput())
                .eq(accountPageQueryDTO.getSchoolId() != null, Account::getSchoolId, accountPageQueryDTO.getSchoolId())
                .orderBy(true, false, Account::getUpdateTime);
        Page<Account> newPage = this.page(page, lambdaQueryWrapper);
        long total = page.getTotal();
        List<Account> result = page.getRecords();
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

        if(account==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        else if(account.getAuth()==1){
            throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
        }
        LambdaUpdateWrapper<Account> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Account::getUserId,account.getUserId())
                        .set(Account::getStatus,account.getStatus()^1)
                                .set(Account::getUpdateUser,account.getUpdateUser());
        this.update(lambdaUpdateWrapper);
    }

    @Override
    @Transactional
    public void add(AccountAddOrUpdateDTO accountInfo) {
        Integer auth=accountInfo.getAuth();
        Long schoolId= accountInfo.getSchoolId();
        Long classId=accountInfo.getClassId();

        Account account=new Account();
        BeanUtils.copyProperties(accountInfo,account);
        account.setPassword(PasswordConstant.DEFAULT_PASSWORD);
        account.setStatus(1);
        this.save(account);

        if(auth==2){
            //校管理员
            School school=new School();
            if(schoolId!=null) {
                School oldSchool = schoolService.getById(schoolId);
                if (oldSchool == null) {
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                } else if (oldSchool.getMasterId() != null) {
                    throw new SchoolWithOtherManagerException(MessageConstant.SCHOOL_WITH_OTHER_MANAGER);
                }
                school.setSchoolId(schoolId);
                school.setMasterId(account.getAccountId());
                schoolService.updateById(school);
            }
        }
        else if(auth==3){
            //教师
            Class myClass = new Class();
            if(classId!=null)
            {
                Class oldClass = classService.getById(classId);
                if(oldClass==null) {
                    throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
                }
                else if(oldClass.getTeacherId()!=null){
                    throw new ClassWithOtherTeacherException(MessageConstant.CLASS_WITH_OTHER_TEACHER);
                }
                myClass.setClassId(classId);
                myClass.setTeacherId(account.getAccountId());
                classService.updateById(myClass);
            }
        }

    }

    @Override
    @Transactional
    public void myUpdate(AccountAddOrUpdateDTO accountInfo) {
        Long accountId=accountInfo.getAccountId();

        Account account = this.getById(accountId);


        if(account==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if(accountInfo.getAuth()!=null)
        {
            Integer authOld=account.getAuth();
            Integer authNew=accountInfo.getAuth();

            Long schoolIdOld=account.getSchoolId();
            Long schoolIdNew=accountInfo.getSchoolId();


            Long classIdOld=account.getClassId();
            Long classIdNew=accountInfo.getClassId();


            if(authOld==2&&schoolIdOld!=null){//原来是校管理员 去掉原来与学校的联系
                School schoolOld = schoolService.getById(schoolIdOld);
                if(schoolOld==null){
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                }
                schoolOld.setMasterId(null);
                schoolService.updateById(schoolOld);
                //todo schoolMapper.clearMasterId(schoolId);
            }

            if(authOld==3&&classIdOld!=null){//原来是教师，去掉原来与班级的联系
                Class classOld = classService.getById(classIdOld);
                if(classOld==null){
                    throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
                }
                classOld.setTeacherId(null);
                classService.updateById(classOld);
                //todo myClassMapper.clearTeacherId(classOld);

            }

            if(authNew==2&&schoolIdNew!=null){//现在为校管理员与学校建立新联系

                School schoolNew = schoolService.getById(schoolIdNew);

                if(schoolNew==null){
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                }
                else if(schoolNew.getMasterId()!=null){
                    throw new SchoolWithOtherManagerException(MessageConstant.SCHOOL_WITH_OTHER_MANAGER);
                }

                schoolNew.setMasterId(accountInfo.getAccountId());
                schoolService.updateById(schoolNew);
            }

            if(authNew==3&&classIdNew!=null){//现在是教师与班级建立新联系
                Class classNew = classService.getById(classIdNew);

                if(classNew==null){
                    throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
                }

                else if(classNew.getTeacherId()!=null){
                    throw new ClassWithOtherTeacherException(MessageConstant.CLASS_WITH_OTHER_TEACHER);
                }

                classNew.setTeacherId(accountInfo.getAccountId());
                classService.updateById(classNew);
            }
        }
        BeanUtils.copyProperties(accountInfo,account);
        //更改账号数据
        this.updateById(account);
    }

    @Override
    @Transactional
    public void delete(Long accountId) {
        Account account = this.getById(accountId);
        Integer auth = account.getAuth();
        if(auth==1){
            throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
        }
        if(auth==2){
            Long schoolId = account.getSchoolId();
            //校管理员
            if(schoolId!=null)
            {
                School school = schoolService.getById(schoolId);
                if(school==null){
                    throw new SchoolNotExistException(MessageConstant.SCHOOL_NOT_EXIST);
                }
                school.setMasterId(null);
                schoolService.updateById(school);
            }
        }
        if(auth==3){
            Long classId = account.getClassId();
            //教师
            if(classId!=null)
            {
                Class myClass = classService.getById(classId);
                if(myClass==null){
                    throw new ClassNotExistException(MessageConstant.CLASS_NOT_EXIST);
                }
                myClass.setTeacherId(null);
                classService.updateById(myClass);
            }
        }
        this.delete(accountId);
    }

    @Override
    public long teacherNumber(Long schoolId, Long classId) {

        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(schoolId!=null,Account::getSchoolId,schoolId)
                .eq(classId!=null,Account::getClassId,classId)
                .eq(Account::getAuth,3);

        return this.count(lambdaQueryWrapper);
    }

    @Override
    public Account getLoginUser() {
        String currentId = BaseContext.getCurrentId();
        return this.getById(Long.parseLong(currentId));
    }


    @Override
    public void importStudents(InputStream inputStream,Long classId){
        ExcelImportUtil.importExcel(inputStream, StudentImportExcelDTO.class,classId,this::excelAddStudents);
    }

    @Transactional(rollbackFor = Exception.class)
    public void excelAddStudents(List<StudentImportExcelDTO> list,Long classId){
        List<String> errorMsgs = new ArrayList<>();
        for(StudentImportExcelDTO studentImportExcelDTO:list){
            String errorMsg = validateStudentExcel(studentImportExcelDTO);
            if(errorMsg!=null){
                errorMsgs.add(String.format("第%s行：%s", studentImportExcelDTO.getUserId(), errorMsg));
            }
        }

        // 如果存在校验错误，抛出异常
        if (!errorMsgs.isEmpty()) {
            log.info("Excel数据校验失败：\n{}",String.join("\n", errorMsgs));
            throw new ExcelImportException(MessageConstant.EXCEL_IMPORT_ERROR);
        }

        // 2. 数据转换
        List<Account> accountList = list.stream()
                .map(studentImportExcelDTO -> convertToStudentAccount(studentImportExcelDTO,classId))
                .collect(Collectors.toList());

        // 3. 数据去重（假设根据用户名去重）
        List<String> userIds = accountList.stream()
                .map(Account::getUserId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountLambdaQueryWrapper.in(Account::getUserId,userIds);
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

        //todo 班级学生数修改
        Class aClass = classService.getById(classId);
        aClass.setStudentNum(aClass.getStudentNum()+toInsert.size());
        classService.updateById(aClass);

    }

    private String validateStudentExcel(StudentImportExcelDTO studentImportExcelDTO) {
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
    private Account convertToStudentAccount(StudentImportExcelDTO studentImportExcelDTO,Long classId) {
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


}




