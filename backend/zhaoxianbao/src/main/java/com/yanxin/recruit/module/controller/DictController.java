package com.yanxin.recruit.module.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.module.entity.SysDict;
import com.yanxin.recruit.module.mapper.SysDictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dict")
@RequiredArgsConstructor
public class DictController {
    private final SysDictMapper sysDictMapper;

    @GetMapping("/list")
    public ApiResult<?> list(@RequestParam(required = false) String dictType) {
        return ApiResult.ok(sysDictMapper.selectList(new LambdaQueryWrapper<SysDict>()
                .eq(dictType != null && !dictType.isBlank(), SysDict::getDictType, dictType)
                .orderByAsc(SysDict::getSortNo)));
    }

    @PostMapping
    public ApiResult<?> add(@RequestBody SysDict dict) {
        sysDictMapper.insert(dict);
        return ApiResult.ok();
    }
}
