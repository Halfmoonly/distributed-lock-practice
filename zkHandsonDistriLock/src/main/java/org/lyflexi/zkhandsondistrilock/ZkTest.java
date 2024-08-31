package org.lyflexi.zkhandsondistrilock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: ly
 * @Date: 2024/1/24 15:06
 */
public class ZkTest {

    public static void main(String[] args) throws KeeperException, InterruptedException {

        // 获取zookeeper链接
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper("192.168.18.100:2181", 30000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected.equals(event.getState())
                            && Event.EventType.None.equals(event.getType())) {
                        System.out.println("获取链接成功。。。。。。" + event);
                        countDownLatch.countDown();
                    }
                }
            });

            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建一个节点，1-节点路径 2-节点内容 3-节点的访问权限 4-节点类型
        // OPEN_ACL_UNSAFE：任何人可以操作该节点
        // CREATOR_ALL_ACL：创建者拥有所有访问权限
        // READ_ACL_UNSAFE: 任何人都可以读取该节点
        // zooKeeper.create("/atguigu/aa", "haha~~".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zooKeeper.create("/test", "haha~~".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        // zooKeeper.create("/atguigu/cc", "haha~~".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        // zooKeeper.create("/atguigu/dd", "haha~~".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        // zooKeeper.create("/atguigu/dd", "haha~~".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        // zooKeeper.create("/atguigu/dd", "haha~~".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // 判断节点是否存在
        Stat stat = zooKeeper.exists("/test", true);
        if (stat != null){
            System.out.println("当前节点存在！" + stat.getVersion());
        } else {
            System.out.println("当前节点不存在！");
        }

        // 判断节点是否存在，同时添加监听
        zooKeeper.exists("/test", event -> {
        });

        // 获取一个节点的数据
//        byte[] data = zooKeeper.getData("/atguigu/ss0000000001", false, null);
//        System.out.println(new String(data));

        // 查询一个节点的所有子节点
        List<String> children = zooKeeper.getChildren("/test", false);
        System.out.println(children);

        // 更新
        zooKeeper.setData("/test", "wawa...".getBytes(), stat.getVersion());

        // 删除一个节点
        //zooKeeper.delete("/test", -1);

        if (zooKeeper != null){
            zooKeeper.close();
        }
    }
}