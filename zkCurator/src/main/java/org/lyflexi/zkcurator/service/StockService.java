package org.lyflexi.zkcurator.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.*;

import org.apache.curator.framework.recipes.shared.SharedCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: ly
 * @Date: 2024/1/24 15:11
 */
@Service
public class StockService {
    @Autowired
    private CuratorFramework curatorFramework;
    @Autowired
    StringRedisTemplate redisTemplate;

    /*测试可重入锁InterProcessMutex*/
    public void checkAndLock() {
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, "/curator/lock");
        try {
            // 加锁
            mutex.acquire();

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

             this.testSub(mutex);

            // 释放锁
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSub(InterProcessMutex mutex) {

        try {
            mutex.acquire();
            System.out.println("测试可重入锁。。。。");
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*测试不可重入锁InterProcessSemaphoreMutex*/
    public void deductInterProcessSemaphoreMutex() {

        InterProcessSemaphoreMutex mutex = new InterProcessSemaphoreMutex(curatorFramework, "/curator/lock");
        try {
            mutex.acquire();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*测试可重入读写锁InterProcessReadWriteLock*/
    public void testZkReadLock() {
        try {
            InterProcessReadWriteLock rwlock = new InterProcessReadWriteLock(curatorFramework, "/curator/rwlock");
            rwlock.readLock().acquire(10, TimeUnit.SECONDS);
            // TODO：一顿读的操作。。。。
            //rwlock.readLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testZkWriteLock() {
        try {
            InterProcessReadWriteLock rwlock = new InterProcessReadWriteLock(curatorFramework, "/curator/rwlock");
            rwlock.writeLock().acquire(10, TimeUnit.SECONDS);
            // TODO：一顿写的操作。。。。
            //rwlock.writeLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*测试信号量InterProcessSemaphoreV2*/
    public void testSemaphore() {
        // 设置资源量 限流的线程数
        InterProcessSemaphoreV2 semaphoreV2 = new InterProcessSemaphoreV2(curatorFramework, "/locks/semaphore", 5);
        try {
            Lease acquire = semaphoreV2.acquire();// 获取资源，获取资源成功的线程可以继续处理业务操作。否则会被阻塞住
            this.redisTemplate.opsForList().rightPush("log", "10010获取了资源，开始处理业务逻辑。" + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(10 + new Random().nextInt(10));
            this.redisTemplate.opsForList().rightPush("log", "10010处理完业务逻辑，释放资源=====================" + Thread.currentThread().getName());
            semaphoreV2.returnLease(acquire); // 手动释放资源，后续请求线程就可以获取该资源
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*测试集群共享的计数器，这个功能可以实现ConuntDownLanch*/
    public void testZkShareCount() {
        try {
            // 第三个参数是共享计数的初始值
            SharedCount sharedCount = new SharedCount(curatorFramework, "/curator/count", 0);
            // 启动共享计数器
            sharedCount.start();
            // 获取共享计数的值
            int count = sharedCount.getCount();
            // 修改共享计数的值
            int random = new Random().nextInt(1000);
            sharedCount.setCount(random);
            System.out.println("我获取了共享计数的初始值：" + count + "，并把计数器的值改为：" + random);
            sharedCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
