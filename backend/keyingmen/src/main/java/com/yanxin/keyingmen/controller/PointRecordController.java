package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.PointRecord;
import com.yanxin.keyingmen.service.IPointRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point-record")
@RequiredArgsConstructor
public class PointRecordController {
    private final IPointRecordService service;

    @GetMapping("/page")
    public Result<Page<PointRecord>> page(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.page(new Page<>(page, pageSize)));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody PointRecord entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody PointRecord entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(service.removeById(id));
    }
}
