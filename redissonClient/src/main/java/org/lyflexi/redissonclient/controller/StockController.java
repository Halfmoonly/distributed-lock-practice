package org.lyflexi.redissonclient.controller;


import org.lyflexi.redissonclient.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("stock/deduct")
    public String deduct(){
        stockService.deduct();
        return "hello stock deduct！！";
    }

    /*测试分布式读写锁*/
    @GetMapping("test/read")
    public String testRead(){
        String msg = stockService.testRead();

        return "测试读";
    }

    @GetMapping("test/write")
    public String testWrite(){
        String msg = stockService.testWrite();

        return "测试写";
    }


    /*测试semaphore*/
    @GetMapping("test/semaphore")
    public String testSemaphore(){
        this.stockService.testSemaphore();

        return "测试信号量";
    }
}
