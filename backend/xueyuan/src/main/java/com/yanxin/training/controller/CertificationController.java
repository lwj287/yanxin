package com.yanxin.training.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.training.common.Result;
import com.yanxin.training.entity.Certification;
import com.yanxin.training.service.CertificationService;
import com.yanxin.training.mapper.CertificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cert")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/page")
    public Result<Page<Certification>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String certName,
            @RequestParam(required = false) String certStatus) {
        
        Page<Certification> pageInfo = ((CertificationMapper) certificationService.getBaseMapper()).selectCertPage(
                new Page<>(current, size), certName, certStatus);
        
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<Certification> getById(@PathVariable String id) {
        return Result.success(certificationService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Certification certification) {
        return Result.success(certificationService.save(certification));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Certification certification) {
        return Result.success(certificationService.updateById(certification));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return Result.success(certificationService.removeById(id));
    }
}