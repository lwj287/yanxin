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

    // 模拟 Redis 或内存缓存存放验证码，MVP阶段简单使用静态 Map
    private static final java.util.Map<String, String> SMS_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/sendSms")
    public Result<String> sendSms(@RequestBody java.util.Map<String, String> params) {
        String phone = params.get("phone");
        if (phone == null || phone.isEmpty()) {
            return Result.error(400, "手机号不能为空");
        }
        // 模拟生成 6 位验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        SMS_CACHE.put(phone, code);
        // 实际开发中这里会调用阿里云/腾讯云短信服务发送，现在直接返回给前端展示（方便测试）
        return Result.success(code);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody java.util.Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        
        if (phone == null || phone.isEmpty()) {
            return Result.error(400, "手机号不能为空");
        }
        if (code == null || code.isEmpty()) {
            return Result.error(400, "验证码不能为空");
        }
        
        // 校验验证码
        String cachedCode = SMS_CACHE.get(phone);
        if (cachedCode == null || !cachedCode.equals(code)) {
            // MVP为了方便演示，留个后门万能验证码 123456
            if (!"123456".equals(code)) {
                return Result.error(400, "验证码错误或已过期");
            }
        }
        // 校验成功后移除
        SMS_CACHE.remove(phone);

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Member> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getPhone, phone);
        Member member = service.getOne(queryWrapper);
        if (member == null) {
            // MVP阶段：如果是新手机号，自动注册为普通会员
            member = new Member();
            member.setPhone(phone);
            member.setMemberName("微信用户" + phone.substring(phone.length() >= 4 ? phone.length() - 4 : 0));
            member.setMemberLevel(1);
            service.save(member);
        }
        // 返回模拟 token
        return Result.success("token_" + member.getMemberId());
    }

    @GetMapping("/info")
    public Result<Member> info(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("token_")) {
            return Result.error(401, "未登录");
        }
        try {
            Long memberId = Long.parseLong(token.substring(6));
            Member member = service.getById(memberId);
            return Result.success(member);
        } catch (Exception e) {
            return Result.error(401, "Token无效");
        }
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
