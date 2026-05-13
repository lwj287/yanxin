package com.yanxin.anxinbao.controller;

import com.yanxin.anxinbao.common.Result;
import com.yanxin.anxinbao.entity.Claim;
import com.yanxin.anxinbao.service.IClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claim")
public class ClaimController {

    @Autowired
    private IClaimService claimService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(claimService.list());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(claimService.getById(id));
    }

    @PutMapping("/{id}/accept")
    public Result<?> accept(@PathVariable Long id) {
        Claim claim = claimService.getById(id);
        if (claim != null) {
            claim.setClaimStatus(1);
            claim.setClaimTime(java.time.LocalDateTime.now());
            claimService.updateById(claim);
        }
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id, @RequestBody(required = false) String reason) {
        Claim claim = claimService.getById(id);
        if (claim != null) {
            claim.setClaimStatus(3);
            claim.setClaimTime(java.time.LocalDateTime.now());
            claimService.updateById(claim);
        }
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    public Result<?> complete(@PathVariable Long id) {
        Claim claim = claimService.getById(id);
        if (claim != null) {
            claim.setClaimStatus(2);
            claim.setClaimTime(java.time.LocalDateTime.now());
            claimService.updateById(claim);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        claimService.removeById(id);
        return Result.success();
    }
}
