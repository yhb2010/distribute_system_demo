package com.chapter3message.rabbitmq.queueroute;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//消息接收2.增加类DirectReceiver
//接收器，依然各自监控自己的队列
@Component
@RabbitListener(queues = DirectRabbitConfig.QUEUE2)
public class DirectReceiver2 {

	//@RabbitListener 和 @RabbitHandler结合使用，不同类型的消息使用不同的方法来处理。
	@RabbitHandler
    public void process(String message) {
        System.out.println(DirectRabbitConfig.QUEUE2 + " Receiver  : " + message);
    }

}
