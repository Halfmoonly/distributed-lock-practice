package org.lyflexi.zkhandsondistrilock.lock;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @Author: ly
 * @Date: 2024/1/24 15:10
 */
public class ZkDistributedLock {

    private static final String ROOT_PATH = "/distributed";

    private String path;

    private ZooKeeper zooKeeper;
    private String lockName;

    private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

    public ZkDistributedLock(ZooKeeper zooKeeper, String lockName){
        try {
            this.zooKeeper = zooKeeper;
            this.lockName = lockName;
            //每次请求来都来创建序列号锁，lockName还是相同，但是序列号不同
//            this.path = zooKeeper.create(ROOT_PATH + "/" + lockName + "-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            //如果我这次是重入的，那就不要再重新创建锁了
            if (THREAD_LOCAL.get() == null || THREAD_LOCAL.get() == 0){
                this.path = zooKeeper.create(ROOT_PATH + "/" + lockName + "-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void lock(){
        Integer flag = THREAD_LOCAL.get();
        if (flag != null && flag > 0) {
            THREAD_LOCAL.set(flag + 1);
            return;
        }
        try {
            String preNode = getPreNode(path);

            if (!StringUtils.isEmpty(preNode)){
                CountDownLatch countDownLatch = new CountDownLatch(1);
                //二次校验前驱节点，如果在此期间前驱节点持有的锁释放，那么当前节点获取到锁
                if (this.zooKeeper.exists(ROOT_PATH + "/" + preNode, new Watcher(){
                    @Override
                    public void process(WatchedEvent event) {
                        countDownLatch.countDown();
                    }
                }) == null) {
                    //前驱节点结束，当前节点获取到锁,放行执行业务逻辑
                    //首次获取
                    THREAD_LOCAL.set(1);
                    return;
                }
                //如果监听到前驱节点，则当前线程阻塞，阻塞锁
                countDownLatch.await();
                THREAD_LOCAL.set(1);
                return;
            }

            // 如果该节点没有前一个节点，说明该节点是最小节点，获取到锁,放行执行业务逻辑
            THREAD_LOCAL.set(1);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            // 重新检查。是否获取到锁
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
//            lock();
        }
    }

    public void unlock(){
        try {
            THREAD_LOCAL.set(THREAD_LOCAL.get() - 1);
            if (THREAD_LOCAL.get() == 0) {
                this.zooKeeper.delete(path, 0);
                THREAD_LOCAL.remove();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定节点的前节点：指的是同一目录下的序列化节点中的前驱节点，序列化节点会从小到大排序
     * @param path
     * @return
     */
    private String getPreNode(String path){

        try {
            // 获取当前节点的序列化号43423525256
            Long curSerial = Long.valueOf(StringUtils.substringAfterLast(path, "-"));
            // 获取根路径distributed下的所有序列化子节点名称，如lock-43423525256，但是有可能是别的资源的锁如lockxx-111,lockyy-111
            List<String> children = this.zooKeeper.getChildren(ROOT_PATH, false);

            // 判空
            if (CollectionUtils.isEmpty(children)){
                throw new IllegalMonitorStateException("非法操作");
            }
            //对children去重，让当前资源和当前锁匹配
            List<String> nodes = children.stream().filter(node -> StringUtils.startsWith(node, lockName + '-')).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(nodes)){
                throw new IllegalMonitorStateException("非法操作");
            }
            //拍好序
            Collections.sort(nodes);
            //获取当前序列节点的索引
            String currNodeName = StringUtils.substringAfterLast(path, "/");
            int index = Collections.binarySearch(nodes, currNodeName);
            if (index<0){
                throw new IllegalMonitorStateException("非法操作");
            }else if (index>0){
                return nodes.get(index-1);
            }
            //index=0，没有前置节点，说明自己就是前置节点，返回null，放行
            return null;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}