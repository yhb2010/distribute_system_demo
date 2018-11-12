package com.chapter6zookeeper.barrier;

import java.util.Random;

public class BarrierTest {

    /**
     * 启动三个线程，也就对应着三个zk客户端
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        for (int i = 0; i < 3; i++) {
            Process p = new Process("Thread-" + i, new Barrier("/test_node", 3));
            Thread.sleep(2000 + new Random().nextInt(2000));
            p.start();
        }
    }
}

class Process extends Thread {

    private String name;
    private Barrier barrier;

    public Process(String name, Barrier barrier) {
        this.name = name;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            barrier.enter(name);
            System.out.println(name + " enter at " + System.currentTimeMillis());
            Thread.sleep(1000 + new Random().nextInt(2000));
            barrier.leave(name);
            System.out.println(name + " leave at " + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}