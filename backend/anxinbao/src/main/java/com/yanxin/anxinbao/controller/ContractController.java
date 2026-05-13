package com.yanxin.anxinbao.controller;

import com.yanxin.anxinbao.common.Result;
import com.yanxin.anxinbao.entity.Contract;
import com.yanxin.anxinbao.service.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    @Autowired
    private IContractService contractService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(contractService.list());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(contractService.getById(id));
    }

    @PutMapping("/{id}/void")
    public Result<?> voidContract(@PathVariable Long id) {
        Contract contract = contractService.getById(id);
        if (contract != null) {
            contract.setContractStatus(3);
            contractService.updateById(contract);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        contractService.removeById(id);
        return Result.success();
    }
}
