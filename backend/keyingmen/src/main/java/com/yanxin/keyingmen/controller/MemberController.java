package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.Member;
import com.yanxin.keyingmen.service.IMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final IMemberService service;

    @GetMapping("/page")
    public Result<Page<Member>> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "memberName", required = false) String memberName,
                                     @RequestParam(value = "phone", required = false) String phone,
                                     @RequestParam(value = "memberLevel", required = false) Integer memberLevel) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Member> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.like(memberName != null && !memberName.isEmpty(), Member::getMemberName, memberName)
                    .like(phone != null && !phone.isEmpty(), Member::getPhone, phone)
                    .eq(memberLevel != null, Member::getMemberLevel, memberLevel)
                    .orderByDesc(Member::getCreateTime);
        return Result.success(service.page(new Page<>(page, pageSize), queryWrapper));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Member entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Member entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) {
        return Result.success(service.removeById(id));
    }
}
