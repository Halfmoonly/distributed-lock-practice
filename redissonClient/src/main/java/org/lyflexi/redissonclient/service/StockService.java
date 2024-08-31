package org.lyflexi.redissonclient.service;


import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class StockService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    public void deduct() {
        RLock lock = redissonClient.getLock("lock");
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
        } finally {
            lock.unlock();
        }
    }
    public void test() {
        RLock redisLock = this.redissonClient.getLock("lock");
        redisLock.lock();
        System.out.println("测试可重入锁");
        redisLock.unlock();
    }

    /*测试读写锁*/
    public String testRead() {
        RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("rwLock");
        rwLock.readLock().lock(10, TimeUnit.SECONDS);

        System.out.println("测试读锁。。。。");
        // rwLock.readLock().unlock();

        return null;
    }

    public String testWrite() {
        RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("rwLock");
        rwLock.writeLock().lock(10, TimeUnit.SECONDS);

        System.out.println("测试写锁。。。。");
        // rwLock.writeLock().unlock();

        return null;
    }

    /*测试semaphore*/
    public void testSemaphore() {
        RSemaphore semaphore = this.redissonClient.getSemaphore("semaphore");
        semaphore.trySetPermits(3);
        try {
            semaphore.acquire();

            TimeUnit.SECONDS.sleep(5);
            System.out.println(System.currentTimeMillis());

            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
