package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.Orders;
import com.yanxin.keyingmen.entity.Member;
import com.yanxin.keyingmen.service.IOrdersService;
import com.yanxin.keyingmen.service.IMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final IOrdersService service;
    private final IMemberService memberService;

    @GetMapping("/page")
    public Result<Page<Orders>> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "orderId", required = false) Long orderId,
                                     @RequestParam(value = "memberName", required = false) String memberName,
                                     @RequestParam(value = "payStatus", required = false) Integer payStatus) {
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Orders> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(orderId != null, Orders::getOrderId, orderId)
                    .eq(payStatus != null, Orders::getPayStatus, payStatus)
                    .orderByDesc(Orders::getCreateTime);
                    
        // 如果有按会员姓名搜索的条件
        if (memberName != null && !memberName.isEmpty()) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Member> memberQw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            memberQw.like(Member::getMemberName, memberName);
            List<Member> matchingMembers = memberService.list(memberQw);
            if (matchingMembers.isEmpty()) {
                return Result.success(new Page<>(page, pageSize)); // 没找到对应会员，直接返回空分页
            }
            List<Long> memberIds = matchingMembers.stream().map(Member::getMemberId).collect(Collectors.toList());
            queryWrapper.in(Orders::getMemberId, memberIds);
        }

        Page<Orders> pageResult = service.page(new Page<>(page, pageSize), queryWrapper);
        
        // 组装会员姓名到返回结果中
        List<Orders> records = pageResult.getRecords();
        if (!records.isEmpty()) {
            List<Long> memberIds = records.stream().map(Orders::getMemberId).distinct().collect(Collectors.toList());
            if (!memberIds.isEmpty()) {
                List<Member> members = memberService.listByIds(memberIds);
                Map<Long, String> memberNameMap = members.stream().collect(Collectors.toMap(Member::getMemberId, Member::getMemberName));
                records.forEach(order -> order.setMemberName(memberNameMap.get(order.getMemberId())));
            }
        }
        
        return Result.success(pageResult);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Orders entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Orders entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) {
        return Result.success(service.removeById(id));
    }
}
