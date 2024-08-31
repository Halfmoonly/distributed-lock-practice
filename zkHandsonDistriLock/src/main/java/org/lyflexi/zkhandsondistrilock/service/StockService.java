package org.lyflexi.zkhandsondistrilock.service;

import org.lyflexi.zkhandsondistrilock.lock.ZkClient;
import org.lyflexi.zkhandsondistrilock.lock.ZkDistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: ly
 * @Date: 2024/1/24 15:11
 */
@Service
public class StockService {
    @Autowired
    private ZkClient client;
    @Autowired
    StringRedisTemplate redisTemplate;

    public void checkAndLock() {
        // 加锁，获取锁失败重试
        ZkDistributedLock lock = this.client.getZkDistributedLock("lock");
        lock.lock();

        try {
            // 1. 查询库存信息
            String stock = redisTemplate.opsForValue().get("stock").toString();
            // 2. 判断库存是否充足
            if (stock != null && stock.length() != 0) {
                Integer st = Integer.valueOf(stock);
                if (st > 0) {
                    // 3.扣减库存
                    redisTemplate.opsForValue().set("stock", String.valueOf(--st));
                }
            }
            this.test();
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            lock.unlock();
        }




    }


    public void test() {
        ZkDistributedLock lock = this.client.getZkDistributedLock("lock");
        lock.lock();
        System.out.println("测试可重入锁");
        lock.unlock();
    }
}
