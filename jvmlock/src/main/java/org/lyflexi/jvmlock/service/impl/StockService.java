package org.lyflexi.jvmlock.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lyflexi.jvmlock.mapper.StockMapper;
import org.lyflexi.jvmlock.pojo.Stock;
import org.lyflexi.jvmlock.service.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;

@Service
public class StockService extends ServiceImpl<StockMapper, Stock>  implements IStockService {

    private ReentrantLock lock = new ReentrantLock();


    /*
    * 锁要加载事务外面:
    *
    * 否则线程1锁释放之后，事务还未提交
    * 此时线程2获得锁，进行数据库修改操作。依然会造成线程不安全
    * */
    @Override
    public void deductJvm(Long productId){
        lock.lock();
        try {
            extracted(productId);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void extracted(Long productId) {
        Stock stock = null;
        try {
            stock = this.getOne(Wrappers.<Stock>lambdaQuery()
            .eq(Stock::getProductId, productId));
        } catch (Exception e) {
            throw new RuntimeException("getOne异常");
        }
        stock.setCount(stock.getCount()-1);
        this.baseMapper.updateStock(stock);
    }
}
