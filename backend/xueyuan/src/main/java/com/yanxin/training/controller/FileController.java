package com.yanxin.training.controller;

import com.yanxin.training.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/file")
public class FileController {

    private final String uploadPath = "uploads";

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        
        File dir = new File(System.getProperty("user.dir"), uploadPath + "/" + day);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir, name);
        file.transferTo(dest);
        
        // Since we are using Vite proxy or accessing directly, return a relative URL
        // or a full URL if required. Let's return relative URL /api/uploads/... so the frontend can display it correctly.
        // Wait, if it returns /api/uploads/..., the frontend will use it as an absolute path from current domain.
        // But the backend serves it under /api/uploads/... because context-path is /api!
        // The WebMvcConfig maps /uploads/** to file system. So the actual URL is /api/uploads/...
        String baseUrl = request.getContextPath();
        String fileUrl = baseUrl + "/uploads/" + day + "/" + name;
        return Result.success(fileUrl);
    }
}
