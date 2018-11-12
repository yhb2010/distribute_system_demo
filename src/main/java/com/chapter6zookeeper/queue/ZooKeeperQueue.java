package com.chapter6zookeeper.queue;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import com.ByteToObjectUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
实现原理
先进先出队列是最常用的队列，使用Zookeeper实现先进先出队列就是在特定的目录下创建PERSISTENT_EQUENTIAL节点，创建成功时Watcher通知等待的队列，队列删除序列号最小的节点用以消费。
此场景下Zookeeper的znode用于消息存储，znode存储的数据就是消息队列中的消息内容，SEQUENTIAL序列号就是消息的编号，按序取出即可。
由于创建的节点是持久化的，所以不必担心队列消息的丢失问题。

应用场景
Zookeeper队列不太适合要求高性能的场合，但可以在数据量不大的情况下考虑使用。比如已在项目中使用Zookeeper又需要小规模的队列应用，这时可以使用Zookeeper实现的队列；
毕竟引进一个消息中间件会增加系统的复杂性和运维的压力。
 * */
public class ZooKeeperQueue {

	private ZooKeeper zk;

    private static byte[] ROOT_QUEUE_DATA = {0x12,0x34};
    private static String QUEUE_ROOT = "/QUEUE";
    private String queuePath;
    private Object mutex = new Object();

    public ZooKeeperQueue(String queueName) throws IOException, KeeperException, InterruptedException {
        this.queuePath = QUEUE_ROOT + "/" + queueName;
        this.zk = ZooKeeperClient.getInstance();
        //----------------------------------------------------
        // 确保队列根目录/QUEUE和当前队列的目录的存在
        //----------------------------------------------------
        ensureExists(QUEUE_ROOT);
        ensureExists(queuePath);
    }

    public void consume() throws InterruptedException, KeeperException, UnsupportedEncodingException {
        List<String> nodes = null;
        byte[] returnVal = null;
        Stat stat = null;
        do {
            synchronized (mutex) {

                nodes = zk.getChildren(queuePath, new ProduceWatcher());

                //----------------------------------------------------
                // 如果没有消息节点，等待生产者的通知
                //----------------------------------------------------
                if (nodes == null || nodes.size() == 0) {
                    mutex.wait();
                } else {

                    SortedSet<String> sortedNode = new TreeSet<String>();
                    for (String node : nodes) {
                        sortedNode.add(queuePath + "/" + node);
                    }

                    //----------------------------------------------------
                    // 消费队列里序列号最小的消息
                    //----------------------------------------------------
                    String first = sortedNode.first();
                    returnVal = zk.getData(first, false, stat);
                    zk.delete(first, -1);

                    System.out.print(Thread.currentThread().getName() + " ");
                    System.out.print("consume a message from queue：" + first);
                    System.out.println(", message data is: " + ByteToObjectUtil.ByteToObject(returnVal));
                    System.out.println("--------------------------------------------------------");
                    System.out.println();
                    return;
                }
            }
        } while (true);
    }

    class ProduceWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            //----------------------------------------------------
            // 生产一条消息成功后通知一个等待线程
            //----------------------------------------------------
            synchronized (mutex) {
                mutex.notify();
            }
        }
    }

    public void produce(byte[] data) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        //----------------------------------------------------
        // 确保当前队列目录存在
        // example: /QUEUE/queueName
        //----------------------------------------------------
        ensureExists(queuePath);

        String node = zk.create(queuePath + "/", data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL);

        System.out.print(Thread.currentThread().getName() + " ");
        System.out.print("produce a message to queue：" + node);
        System.out.println(" , message data is: " + new String(data,"UTF-8"));
        System.out.println("--------------------------------------------------------");
        System.out.println();
    }


    public void ensureExists(String path) {
        try {
            Stat stat = zk.exists(path, false);
            if (stat == null) {
                zk.create(path, ROOT_QUEUE_DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
