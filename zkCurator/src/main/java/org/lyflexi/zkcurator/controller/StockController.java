package org.lyflexi.zkcurator.controller;


import org.lyflexi.zkcurator.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("stock/deduct")
    public String deduct(){
        stockService.checkAndLock();
        return "hello stock deduct！！";
    }

    /*测试不可重入锁deductInterProcessSemaphoreMutex*/
    @GetMapping("stock/deductInterProcessSemaphoreMutex")
    public String deductInterProcessSemaphoreMutex(){
        stockService.deductInterProcessSemaphoreMutex();
        return "hello stock deduct！！";
    }

    /*测试可重入读写锁InterProcessReadWriteLock*/
    @GetMapping("stock/testZkReadLock")
    public String testZkReadLock(){
        stockService.testZkReadLock();
        return "hello stock deduct！！";
    }
    @GetMapping("stock/testZkWriteLock")
    public String testZkWriteLock(){
        stockService.testZkWriteLock();
        return "hello stock deduct！！";
    }

    /*测试信号量*/
    @GetMapping("test/semaphore")
    public String testSemaphore(){
        this.stockService.testSemaphore();
        return "hello Semaphore";
    }
    /*测试集群共享计数器*/
    @GetMapping("test/zk/share/count")
    public String testZkShareCount(){
        this.stockService.testZkShareCount();
        return "hello shareData";
    }


}
