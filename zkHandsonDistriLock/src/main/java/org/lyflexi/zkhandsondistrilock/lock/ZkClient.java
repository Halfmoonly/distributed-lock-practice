package org.lyflexi.zkhandsondistrilock.lock;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.zookeeper.*;
import org.springframework.stereotype.Component;

/**
 * @Author: ly
 * @Date: 2024/1/24 15:09
 */
@Component
public class ZkClient {

    private static final String connectString = "192.168.18.100:2181";

    private static final String ROOT_PATH = "/distributed";

    private ZooKeeper zooKeeper;

    @PostConstruct
    public void init(){
        try {
            // 连接zookeeper服务器
            this.zooKeeper = new ZooKeeper(connectString, 60000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("获取链接成功！！");
                }
            });

            // 创建分布式锁根节点
            if (this.zooKeeper.exists(ROOT_PATH, false) == null){
                this.zooKeeper.create(ROOT_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            System.out.println("获取链接失败！");
            e.printStackTrace();
        }
    }

    //Bean预销毁，当前Bean销毁之前，执行@PreDestroy
    @PreDestroy
    public void destroy(){
        try {
            if (zooKeeper != null){
                zooKeeper.close();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化zk分布式锁对象方法
     * @param lockName
     * @return
     */
    public ZkDistributedLock getZkDistributedLock(String lockName){
        return new ZkDistributedLock(zooKeeper, lockName);
    }
}