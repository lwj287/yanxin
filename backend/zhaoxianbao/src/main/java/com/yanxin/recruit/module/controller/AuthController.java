package com.yanxin.recruit.module.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.common.enums.RoleEnum;
import com.yanxin.recruit.common.exception.BizException;
import com.yanxin.recruit.common.security.JwtTokenProvider;
import com.yanxin.recruit.module.dto.LoginReq;
import com.yanxin.recruit.module.dto.RegisterCandidateReq;
import com.yanxin.recruit.module.entity.CandidateUser;
import com.yanxin.recruit.module.entity.SysUser;
import com.yanxin.recruit.module.mapper.CandidateUserMapper;
import com.yanxin.recruit.module.mapper.SysUserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SysUserMapper sysUserMapper;
    private final CandidateUserMapper candidateUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register/candidate")
    public ApiResult<?> registerCandidate(@RequestBody @Valid RegisterCandidateReq req) {
        CandidateUser phoneExists = candidateUserMapper.selectOne(new LambdaQueryWrapper<CandidateUser>()
                .eq(CandidateUser::getPhone, req.getPhone()));
        if (phoneExists != null) {
            throw new BizException("手机号已注册");
        }
        CandidateUser exists = candidateUserMapper.selectOne(new LambdaQueryWrapper<CandidateUser>()
                .eq(CandidateUser::getUsername, req.getUsername()));
        if (exists != null) {
            throw new BizException("用户名已存在");
        }
        CandidateUser user = new CandidateUser();
        // 统一手机号登录，username 与 phone 保持一致以兼容历史逻辑
        user.setUsername(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setResumeDeliveryStatus(0);
        user.setStatus(1);
        user.setDeleted(0);
        candidateUserMapper.insert(user);
        return ApiResult.ok();
    }

    @PostMapping("/login")
    public ApiResult<?> login(@RequestBody @Valid LoginReq req) {
        String role = req.getRole().toUpperCase();
        Long uid;
        String realName;
        String encodedPwd;
        if (RoleEnum.CANDIDATE.name().equals(role)) {
            CandidateUser user = candidateUserMapper.selectOne(new LambdaQueryWrapper<CandidateUser>()
                    .eq(CandidateUser::getPhone, req.getUsername()));
            if (user == null) {
                // 兼容历史数据：允许旧的 username 登录
                user = candidateUserMapper.selectOne(new LambdaQueryWrapper<CandidateUser>()
                        .eq(CandidateUser::getUsername, req.getUsername()));
            }
            if (user == null) throw new BizException("手机号不存在");
            uid = user.getId();
            realName = user.getRealName();
            encodedPwd = user.getPassword();
            req.setUsername(user.getPhone() != null ? user.getPhone() : user.getUsername());
        } else {
            SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, req.getUsername())
                    .eq(SysUser::getRole, role));
            if (user == null) throw new BizException("账号不存在");
            uid = user.getId();
            realName = user.getRealName();
            encodedPwd = user.getPassword();
        }

        boolean pass = passwordEncoder.matches(req.getPassword(), encodedPwd) || req.getPassword().equals(encodedPwd);
        if (!pass) {
            throw new BizException("密码错误");
        }
        String token = jwtTokenProvider.createToken(uid, req.getUsername(), role);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", role);
        data.put("realName", realName);
        data.put("userId", uid);
        return ApiResult.ok(data);
    }
}
