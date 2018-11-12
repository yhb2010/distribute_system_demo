package com.chapter3message.rabbitmq.rpcdemo;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class AsyncQueueListener {

	/**
     * 同步队列
     * @param id 任务ID
     * @param type 任务名称
     */
    @RabbitListener(queues = AmqpConfig.SYNC_QUEUE)
    public Person hello(Person person, @Header("jobName") String jobName) {
    	System.out.println("Received jobName for " + jobName);
        System.out.println("Received request for " + person);
        person.setAge(person.getAge() + 10);
        try {
        	System.out.println("sleep two second");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        return person;
    }

    /**
     * 异步队列，SendTo为回复的队列名称
     * @param id 任务ID
     * @param type 任务名称
     * @return
     */
    @RabbitListener(queues = AmqpConfig.ASYNC_QUEUE)
    @SendTo(AmqpConfig.R_ASYNC_QUEUE)
    public Person hello2(Person person, @Header("jobName") String jobName) {
    	System.out.println("Received jobName for " + jobName);
        System.out.println("Received request for " + person);
        try {
        	System.out.println("sleep three second");
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        return person;
    }

}
