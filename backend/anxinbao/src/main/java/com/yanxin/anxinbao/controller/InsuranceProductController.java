package com.yanxin.anxinbao.controller;

import com.yanxin.anxinbao.common.Result;
import com.yanxin.anxinbao.entity.InsuranceProduct;
import com.yanxin.anxinbao.service.IInsuranceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/insurance-product")
public class InsuranceProductController {

    @Autowired
    private IInsuranceProductService insuranceProductService;

    @GetMapping("/list")
    public Result<List<InsuranceProduct>> list() {
        return Result.success(insuranceProductService.list());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody InsuranceProduct product) {
        insuranceProductService.save(product);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody InsuranceProduct product) {
        insuranceProductService.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        insuranceProductService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        InsuranceProduct product = insuranceProductService.getById(id);
        if (product != null) {
            product.setStatus(status);
            insuranceProductService.updateById(product);
        }
        return Result.success();
    }
}
