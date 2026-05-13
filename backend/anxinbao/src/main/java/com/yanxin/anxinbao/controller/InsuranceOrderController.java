package com.yanxin.anxinbao.controller;

import com.yanxin.anxinbao.common.Result;
import com.yanxin.anxinbao.entity.InsuranceOrder;
import com.yanxin.anxinbao.service.IInsuranceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insurance-order")
public class InsuranceOrderController {

    @Autowired
    private IInsuranceOrderService insuranceOrderService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(insuranceOrderService.list());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(insuranceOrderService.getById(id));
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody InsuranceOrder order) {
        insuranceOrderService.updateById(order);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        insuranceOrderService.removeById(id);
        return Result.success();
    }
}
