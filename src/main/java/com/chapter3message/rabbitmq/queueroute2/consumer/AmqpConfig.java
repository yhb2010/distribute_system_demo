package com.chapter3message.rabbitmq.queueroute2.consumer;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.rabbitmq.client.Channel;

@Configuration
public class AmqpConfig {

	/** 消息交换机的名字
	 * producer并不直接发送消息到queue，而是发送到了exchange中，exchange一边接收来自producer的消息，一边将消息插入queue*/
	public static final String EXCHANGE   = "spring-boot-exchange";
	/** 队列key
	 * 路由意味着在消息订阅中，可选择性的只订阅部分消息。*/
    public static final String ROUTINGKEY1 = "spring-boot-routingKey1";
    public static final String ROUTINGKEY2 = "spring-boot-routingKey2";
    public static final String QUEUE1 = "spring-boot-queue1";
    public static final String QUEUE2 = "spring-boot-queue2";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("127.0.0.1:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true); //必须要设置，才能进行消息的回调
        return connectionFactory;
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     *
     *
        FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
        HeadersExchange ：通过添加属性key-value匹配
        DirectExchange:按照routingkey分发到指定队列
        TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE);
    }

    /**
     * 配置消息队列
     * 工作队列又叫做任务队列，主要思想是避免立即处理一个资源密集型任务造成的长时间等待，相反我们可以计划着让任务后续执行。
     * 我们将任务封装成消息发送到队列中。一个工作者进程在后台运行，获取任务并最终执行任务。当运行多个工作者时，所有的任务
     * 将会被它们所共享。
     * 针对消费者配置
     * @return
     */
    @Bean
    public Queue queue1() {
        return new Queue(QUEUE1, true); //队列持久
    }

    /**
     * 将消息队列与交换机绑定
     * 针对消费者配置
     * @return
     */
    @Bean
    public Binding binding1() {
    	return BindingBuilder.bind(queue1()).to(defaultExchange()).with(AmqpConfig.ROUTINGKEY1);
    }

    @Bean
    public Queue queue2() {
    	return new Queue(QUEUE2, true); //队列持久
    }
    @Bean
    public Binding binding2() {
    	return BindingBuilder.bind(queue2()).to(defaultExchange()).with(AmqpConfig.ROUTINGKEY2);
    }

    /**
     * 接受消息的监听，这个监听会接受消息队列的消息
     * 针对消费者配置
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer messageContainer1() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(queue1());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认，不需要自动询问
        container.setMessageListener(new ChannelAwareMessageListener() {

            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                byte[] body = message.getBody();
                System.out.println("queue1 receive msg : " + new String(body));
                //将告知rabbitMQ不要同时给一个工作者超过一个任务，也就是说，在一个工作者完成处理，发送确认之前不要给它分发一个新的消息，取而代之的是把消息发给下一个不忙的工作者。
                channel.basicQos(1);
                //rabbitMQ通过消息确认机制来保证消息已经被送达。一个消息确认是由消费者发出的，告诉rabbitMQ这个消息已经被接收，处理完成后，rabbitMQ就可以删除它。如果一个消费者没有
                //发送确认信号，rabbitMQ就认为这个消息没有完全处理成功，会把它传递给另一个消费者。通过这种方式，即使工作者死掉，依旧可以保证没有消息丢失。这里不存在超时的情况，
                //rabbitMQ只会在worker连接死掉后才重新传递这个消息。即使一个消息要被处理很长时间，也不是问题。消息确认机制默认是开着的。
                //不需要自动询问，显示执行下面代码来提交询问。
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费
            }
        });
        return container;
    }
    @Bean
    public SimpleMessageListenerContainer messageContainer2() {
    	SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
    	container.setQueues(queue2());
    	container.setExposeListenerChannel(true);
    	container.setMaxConcurrentConsumers(1);
    	container.setConcurrentConsumers(1);
    	container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认，不需要自动询问
    	container.setMessageListener(new ChannelAwareMessageListener() {

    		@Override
    		public void onMessage(Message message, Channel channel) throws Exception {
    			byte[] body = message.getBody();
    			System.out.println("queue2 receive msg : " + new String(body));
    			//将告知rabbitMQ不要同时给一个工作者超过一个任务，也就是说，在一个工作者完成处理，发送确认之前不要给它分发一个新的消息，取而代之的是把消息发给下一个不忙的工作者。
    			channel.basicQos(1);
    			//rabbitMQ通过消息确认机制来保证消息已经被送达。一个消息确认是由消费者发出的，告诉rabbitMQ这个消息已经被接收，处理完成后，rabbitMQ就可以删除它。如果一个消费者没有
    			//发送确认信号，rabbitMQ就认为这个消息没有完全处理成功，会把它传递给另一个消费者。通过这种方式，即使工作者死掉，依旧可以保证没有消息丢失。这里不存在超时的情况，
    			//rabbitMQ只会在worker连接死掉后才重新传递这个消息。即使一个消息要被处理很长时间，也不是问题。消息确认机制默认是开着的。
    			//不需要自动询问，显示执行下面代码来提交询问。
    			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费
    		}
    	});
    	return container;
    }

}
