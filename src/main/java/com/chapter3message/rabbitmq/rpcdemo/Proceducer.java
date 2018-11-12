package com.chapter3message.rabbitmq.rpcdemo;

import javax.annotation.Resource;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class Proceducer {

	@Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Person person){
    	System.out.println("============sync=============");
        Person obj = (Person) rabbitTemplate.convertSendAndReceive(rabbitTemplate.getExchange(), AmqpConfig.SYNC_ROUTINGKEY, person, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
            	//MessagePostProcessor可以为队列配置一些参数，本例中配置了jobName
                message.getMessageProperties().setHeader("jobName", "我的测试rpc");
                return message;
            }
        });
        System.out.println("new person==" + obj);
    }

    public void sendMessage2(Person person){
    	System.out.println("============async=============");
    	rabbitTemplate.convertAndSend(rabbitTemplate.getExchange(), AmqpConfig.ASYNC_ROUTINGKEY, person, new MessagePostProcessor() {
    		@Override
    		public Message postProcessMessage(Message message) throws AmqpException {
    			//MessagePostProcessor可以为队列配置一些参数，本例中配置了jobName
    			message.getMessageProperties().setHeader("jobName", "我的测试rpc");
    			return message;
    		}
    	});
    	System.out.println("异步立刻返回");
    }

    @RabbitListener(queues = {AmqpConfig.R_ASYNC_QUEUE})
    public void asyncReply(Person person) {
    	System.out.println("============async reply=============");
        System.out.println("接收到异步回复：" + person);
    }

}
