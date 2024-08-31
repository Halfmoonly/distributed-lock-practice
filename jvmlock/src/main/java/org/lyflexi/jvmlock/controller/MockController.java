package org.lyflexi.jvmlock.controller;

import org.lyflexi.jvmlock.service.impl.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class MockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stock/deductJvm")
    public String deductJvm(@RequestParam("productId") Long productId){
        stockService.deductJvm(productId);
        return "hello stock deduct！！";
    }

}

