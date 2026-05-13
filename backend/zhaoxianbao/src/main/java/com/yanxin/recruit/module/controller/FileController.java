package com.yanxin.recruit.module.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.yanxin.recruit.common.api.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileController {
    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public ApiResult<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String ext = FileUtil.extName(file.getOriginalFilename());
        String day = DateUtil.today();
        String name = IdUtil.simpleUUID() + "." + ext;
        
        File dir = new File(System.getProperty("user.dir"), uploadPath + "/" + day);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir, name);
        file.transferTo(dest);
        
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String fileUrl = baseUrl + "/uploads/" + day + "/" + name;
        return ApiResult.ok(fileUrl);
    }
}
