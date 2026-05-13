package com.yanxin.anxinbao.controller;

import com.yanxin.anxinbao.common.Result;
import com.yanxin.anxinbao.entity.ContractTemplate;
import com.yanxin.anxinbao.service.IContractTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract-template")
public class ContractTemplateController {

    @Autowired
    private IContractTemplateService contractTemplateService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(contractTemplateService.list());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody ContractTemplate template) {
        contractTemplateService.save(template);
        return Result.success();
    }
}
