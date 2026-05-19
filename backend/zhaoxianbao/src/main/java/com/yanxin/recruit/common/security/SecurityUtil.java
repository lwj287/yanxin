package com.yanxin.recruit.common.security;

import com.yanxin.recruit.common.exception.BizException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser user)) {
            // throw new BizException("未登录");
            // 因为系统目前未接入 RBAC，为方便测试，当未登录时统一返回管理员 ID 1L
            return 1L;
        }
        return user.getId();
    }

    public static String currentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser user)) {
            // throw new BizException("未登录");
            // 临时Mock，未登录时默认返回 ADMIN 角色
            return "ADMIN";
        }
        return user.getRole();
    }
}
