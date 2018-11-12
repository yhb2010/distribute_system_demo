package com.chapter6zookeeper.queue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.zookeeper.KeeperException;

import com.ByteToObjectUtil;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        String queueName = "test";
        final ZooKeeperQueue queue = new ZooKeeperQueue(queueName);

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        queue.consume();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {

                	User user = new User();
                    user.setId("massive" + i);
                    user.setName("xiao wang" + i);

                    try {
                        queue.produce(ByteToObjectUtil.ObjectToByte(user));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"Produce-thread").start();


    }

}
