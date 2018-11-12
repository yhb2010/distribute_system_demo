package com.chapter3message.rabbitmq.queueroute2.sender;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chapter3message.rabbitmq.queueroute2.consumer.AmqpConfig;

/**
 * 消息生产者
 *
 * @author liaokailin
 * @version $Id: Send.java, v 0.1 2015年11月01日 下午4:22:25 liaokailin Exp $
 */
@RestController
public class Send implements RabbitTemplate.ConfirmCallback {

	private RabbitTemplate rabbitTemplate;

    /**
     * 构造方法注入
     */
	public Send(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        //设置消费回调
        this.rabbitTemplate.setConfirmCallback(this);
    }

    @RequestMapping("send1")
    //http://localhost:8080/send1?content=aaaa
    public String sendMsg(String content) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTINGKEY1, content, correlationId);
        return null;
    }

    @RequestMapping("send2")
    //http://localhost:8080/send2?content=bbb
    public String sendMsg2(String content) {
    	CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
    	rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTINGKEY2, content, correlationId);
    	return null;
    }

    /**
     * 消息的回调，主要是实现RabbitTemplate.ConfirmCallback接口
     * 注意，消息回调只能代表成功消息发送到RabbitMQ服务器，不能代表消息被成功处理和接受
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("回调id:" + correlationData);
        if (ack) {
            System.out.println("消息成功消费");
        } else {
            System.out.println("消息消费失败:" + cause);
        }
    }

}
