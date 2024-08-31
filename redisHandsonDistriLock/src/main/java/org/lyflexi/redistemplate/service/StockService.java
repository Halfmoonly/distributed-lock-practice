package org.lyflexi.redistemplate.service;

import org.lyflexi.redistemplate.lock.DistributedLockClient;
import org.lyflexi.redistemplate.lock.DistributedRedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StockService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    DistributedLockClient distributedLockClient;
    public void deduct() {
        DistributedRedisLock redisLock = this.distributedLockClient.getRedisLock("lock");
        redisLock.lock();

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
            try {
                TimeUnit.SECONDS.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            redisLock.unlock();
        }
    }
    public void test() {
        DistributedRedisLock redisLock = this.distributedLockClient.getRedisLock("lock");
        redisLock.lock();
        System.out.println("测试可重入锁");
        redisLock.unlock();
    }
}
