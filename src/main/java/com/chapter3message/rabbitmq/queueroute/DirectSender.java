package com.chapter3message.rabbitmq.queueroute;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//消息发送。增加类DirectSender
//发送器，发送send1会匹配到第一个Receiver收到消息，发送send2、send3都匹配到Receiver2收到消息。
//发送器在发送消息时，使用的方法是需要传入一个特定的交换机的，以及路由规则。
@Component
public class DirectSender implements RabbitTemplate.ConfirmCallback {

	@Autowired
    private RabbitTemplate rabbitTemplate;

	/**
     * 构造方法注入
     */
	public DirectSender(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        //设置消费回调
        this.rabbitTemplate.setConfirmCallback(this);
    }

	public void sendOrange() {
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
	    String context = "hi, i am message orange";
	    System.out.println("Sender : " + context);
	    this.rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE, DirectRabbitConfig.ROUTINGKEY1, context, correlationId);
	}

	public void sendBlack() {
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
	    String context = "hi, i am messages black";
	    System.out.println("Sender : " + context);
	    this.rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE, DirectRabbitConfig.ROUTINGKEY2, context, correlationId);
	}

	public void sendGreen() {
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
	    String context = "hi, i am messages green";
	    System.out.println("Sender : " + context);
	    this.rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE, DirectRabbitConfig.ROUTINGKEY3, context, correlationId);
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
