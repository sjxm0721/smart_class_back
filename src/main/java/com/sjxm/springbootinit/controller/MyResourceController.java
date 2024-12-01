package com.sjxm.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sjxm.springbootinit.model.dto.ResourceAddDTO;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.entity.MyResource;
import com.sjxm.springbootinit.model.entity.Subject;
import com.sjxm.springbootinit.result.Result;
import com.sjxm.springbootinit.service.AccountService;
import com.sjxm.springbootinit.service.MyResourceService;
import com.sjxm.springbootinit.service.SubjectService;
import com.sjxm.springbootinit.utils.ResourceSizeCalculatorUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/30
 * @Description:
 */
@RestController
@RequestMapping("/admin/resource")
@Api(tags = "资源控制器")
public class MyResourceController {

    @Resource
    private MyResourceService resourceService;


    @Resource
    private SubjectService subjectService;

    @Resource
    private AccountService accountService;

    MyResource obj2VO(MyResource myResource){
        Long subjectId = myResource.getSubjectId();
        Subject subject = subjectService.getById(subjectId);

        Long teacherId = subject.getTeacherId();
        Account account = accountService.getById(teacherId);
        myResource.setTeacherName(account.getName());
        return myResource;
    }

    @GetMapping("/list")
    @ApiOperation("获取课程资源信息列表")
    public Result<List<MyResource>> list(Long subjectId,String input){
        LambdaQueryWrapper<MyResource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MyResource::getSubjectId,subjectId).like(!StrUtil.isBlankIfStr(input),MyResource::getName,input);
        List<MyResource> list = resourceService.list(lambdaQueryWrapper);
        List<MyResource> collect = list.stream().map(this::obj2VO).collect(Collectors.toList());
        return Result.success(collect);
    }

    @GetMapping("/downloadByRscName/{fileName}")
    public void downloadByRscName(@PathVariable String fileName,
                                  @RequestParam String url,
                                  HttpServletResponse response) {
        try {
            // 创建URL连接
            URL fileUrl = new URL(url);
            URLConnection conn = fileUrl.openConnection();

            // 获取输入流
            try (InputStream inputStream = conn.getInputStream();
                 ServletOutputStream outputStream = response.getOutputStream()) {

                // 设置响应头
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" +
                        URLEncoder.encode(fileName, "UTF-8"));

                // 使用缓冲区复制数据
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{resourceId}")
    @ApiOperation("资源下载")
    public void download(@PathVariable Long resourceId, HttpServletResponse response) {
        File tempDir = null;
        try {
            MyResource myResource = resourceService.getById(resourceId);
            if (myResource == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 创建临时目录
            tempDir = Files.createTempDirectory("download_").toFile();

            String[] urls = myResource.getResources().split(",");
            List<File> downloadedFiles = new ArrayList<>();

            // 下载所有文件到临时目录
            for (String url : urls) {
                url = url.trim();
                if (!url.startsWith("http")) continue;

                String fileName = url.substring(url.lastIndexOf('/') + 1);
                File tempFile = new File(tempDir, fileName);

                try {
                    // 下载文件
                    URL fileUrl = new URL(url);
                    try (BufferedInputStream bis = new BufferedInputStream(fileUrl.openStream());
                         BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile))) {

                        byte[] buffer = new byte[1024 * 1024];
                        int bytesRead;
                        while ((bytesRead = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                    downloadedFiles.add(tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 设置响应头
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(myResource.getName() + ".zip", "UTF-8"));

            // 创建zip文件
            try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
                for (File file : downloadedFiles) {
                    if (!file.exists()) continue;

                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOut.putNextEntry(zipEntry);

                    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                        byte[] buffer = new byte[1024 * 1024];
                        int bytesRead;
                        while ((bytesRead = bis.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, bytesRead);
                        }
                    }
                    zipOut.closeEntry();
                }
                zipOut.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // 清理临时文件
            if (tempDir != null) {
                try {
                    FileUtils.deleteDirectory(tempDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @PostMapping("/add")
    @ApiOperation("新增资源")
    public Result addResource(@RequestBody ResourceAddDTO resourceAddDTO){
        MyResource myResource = new MyResource();
        BeanUtil.copyProperties(resourceAddDTO,myResource);
        long totalSize = ResourceSizeCalculatorUtil.calculateTotalSize(myResource.getResources());
        double size = (double) totalSize /1024;
        myResource.setSize(BigDecimal.valueOf(size));
        resourceService.save(myResource);
        return Result.success();
    }

}
