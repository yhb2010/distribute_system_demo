package com.chapter6zookeeper.barrier;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * 继承watcher，实现分布式环境中不同任务之间的同步处理（利用了Watcher机制的反向推送）。
 * 针对事件的触发使线程做出相应的处理，从而避免无谓的while(true)，导致cpu空转。
 */
public class Barrier implements Watcher {

    private static final String addr = "127.0.0.145:2181";
    private ZooKeeper zk = null;
    private Integer mutex;
    private int size = 0;
    private String root;

    public Barrier(String root, int size) {
        this.root = root;
        this.size = size;

        try {
            zk = new ZooKeeper(addr, 10 * 1000, this);
            mutex = new Integer(-1);
            Stat s = zk.exists(root, false);
            if (s == null) {
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 当触发事件后，唤醒在mutex上等待的线程
     * 只要是zk服务器上节点的数据发生改变（不管哪个zk client改变了数据），
     * 这里都会接收到相应的事件，从而唤醒相应的线程，做出相应的处理
     *
     * @param event
     */
    public synchronized void process(WatchedEvent event) {
    	System.out.println("触发事件");
        synchronized (mutex) {
            mutex.notify();
        }
    }

    /**
     * 当新建znode时，首先持有mutex监视器才能进入同步代码块。
     * 当znode发生事件后，会触发process，从而唤醒在mutex上等待的线程。
     * 通过while循环判断创建的节点个数，当节点个数大于设定的值时，这个enter方法才执行完成。
     *
     * @param name
     * @return
     * @throws Exception
     */
    public boolean enter(String name) throws Exception {
        zk.create(root + "/" + name, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("创建" + name);
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(root, true);
                if (list.size() < size) {
                	System.out.println("等待" + name);
                    mutex.wait();
                } else {
                	System.out.println("释放" + name);
                    return true;
                }
            }
        }
    }

    /**
     * 同理。对于leave方法，当delete znode时，触发事件，从而唤醒mutex上等待的线程，通过while循环
     * 判断节点的个数，当节点全部删除后，leave方法结束。
     * 从而使整个添加删除znode的线程结束
     *
     * @param name
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public boolean leave(String name) throws KeeperException, InterruptedException {
        zk.delete(root + "/" + name, 0);
        System.out.println("删除" + name);
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(root, true);
                if (list.size() > 0) {
                    mutex.wait();
                } else {
                    return true;
                }
            }
        }
    }
}