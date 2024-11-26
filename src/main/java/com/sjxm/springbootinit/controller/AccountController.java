package com.sjxm.springbootinit.controller;

import com.sjxm.springbootinit.constant.JwtClaimsConstant;
import com.sjxm.springbootinit.model.dto.AccountAddOrUpdateDTO;
import com.sjxm.springbootinit.model.dto.AccountDTO;
import com.sjxm.springbootinit.model.dto.AccountPageQueryDTO;
import com.sjxm.springbootinit.model.dto.PasswordChangeDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.vo.AccountPageVO;
import com.sjxm.springbootinit.model.vo.AccountVO;
import com.sjxm.springbootinit.properties.JwtProperties;
import com.sjxm.springbootinit.result.PageResult;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/admin/user")
@RestController
@Slf4j
@Api(tags = "用户相关接口")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtProperties jwtProperties;



    @PostMapping("/login")
    @ApiOperation("用户登陆")
    public Result<AccountVO> login(@RequestBody AccountDTO accountDTO){

        Account account = accountService.login(accountDTO);

        //登陆成功，生产JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,account.getAccountId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),//密钥
                jwtProperties.getAdminTtl(),//有效时间
                claims//用户身份
        );

        AccountVO accountVO = AccountVO.builder()
                .accountId(account.getAccountId())
                .userId(account.getUserId())
                .name(account.getName())
                .avatar(account.getAvatar())
                .auth(account.getAuth())
                .schoolId(account.getSchoolId())
                .email(account.getEmail())
                .phone(account.getPhone())
                .classId(account.getClassId())
                .token(token)
                .build();
        return Result.success(accountVO);
    }

    @GetMapping("/tokenLogin")
    @ApiOperation("token获取用户信息")
    public Result<AccountVO> tokenLogin(String token){
        log.info("收到的token为:{}",token);

        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        String userId = String.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        log.info("userId为:{}",userId);
        Account account = accountService.tokenLogin(userId);

        AccountVO accountVO = Account.obj2VO(account);

        return Result.success(accountVO);
    }

    @PutMapping("/editPassword")
    @ApiOperation("修改密码")
    public Result editPassword(@RequestBody PasswordChangeDTO passwordMsg){

        accountService.editPassword(passwordMsg);

        return Result.success();
    }


    @GetMapping("/info")
    @ApiOperation("根据用户ID来获取账号")
    public Result<AccountPageVO> info(String userId){
        AccountPageVO accountPageVO = accountService.info(userId);
        return Result.success(accountPageVO);
    }

    @GetMapping("/page")
    @ApiOperation("账号信息的分页查询")
    public Result<PageResult> page(AccountPageQueryDTO accountPageQueryDTO){


        PageResult page = accountService.myPage(accountPageQueryDTO);

        return Result.success(page);
    }

    @PutMapping("/status")
    @ApiOperation("更新账号状态")
    public Result status(String userId){
        accountService.status(userId);
        return Result.success();
    }

    @PostMapping("/add")
    @ApiOperation("新增账号")
    public Result add(@RequestBody AccountAddOrUpdateDTO accountInfo){

        accountService.add(accountInfo);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改账号")
    public Result update(@RequestBody AccountAddOrUpdateDTO accountInfo){

        accountService.myUpdate(accountInfo);
        return Result.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除账号")
    public Result delete(Long accountId){

        accountService.delete(accountId);
        return Result.success();
    }

    @GetMapping("/teacherNumber")
    @ApiOperation("查看教师数量")
    public Result<Long> teacherNumber(Long schoolId,Long classId){
        long number = accountService.teacherNumber(schoolId,classId);

        return Result.success(number);
    }
}
