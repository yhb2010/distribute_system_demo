package com.chapter3message.rabbitmq.queueroute;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**A、定于两个队列
B、定义一个Direct交换机
C、三个绑定策略
 * @author dell
 *
 */
@Configuration
public class DirectRabbitConfig {

	/** 消息交换机的名字
	 * producer并不直接发送消息到queue，而是发送到了exchange中，exchange一边接收来自producer的消息，一边将消息插入queue*/
	public static final String EXCHANGE   = "directExchange";

	public static final String QUEUE1 = "direct.A";
    public static final String QUEUE2 = "direct.B";

    /** 队列key
	 * 路由意味着在消息订阅中，可选择性的只订阅部分消息。*/
    public static final String ROUTINGKEY1 = "orange";
    public static final String ROUTINGKEY2 = "black";
    public static final String ROUTINGKEY3 = "green";

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
     * 配置消息队列
     * 工作队列又叫做任务队列，主要思想是避免立即处理一个资源密集型任务造成的长时间等待，相反我们可以计划着让任务后续执行。
     * 我们将任务封装成消息发送到队列中。一个工作者进程在后台运行，获取任务并最终执行任务。当运行多个工作者时，所有的任务
     * 将会被它们所共享。
     * 针对消费者配置
     * @return
     */
	@Bean
	public Queue AMessage() {
		return new Queue(QUEUE1, true);//队列持久
	}

	@Bean
	public Queue BMessage() {
		return new Queue(QUEUE2, true);
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
	DirectExchange directExchange() {
		return new DirectExchange(EXCHANGE);
	}

	/**
     * 将消息队列与交换机绑定
     * 针对消费者配置
     * @return
     */
	@Bean
	Binding bindingExchangeMessage(Queue AMessage, DirectExchange exchange) {
		return BindingBuilder.bind(AMessage).to(exchange).with(ROUTINGKEY1);
	}

	@Bean
	Binding bindingExchangeMessageBOfBlack(Queue BMessage, DirectExchange exchange) {
		return BindingBuilder.bind(BMessage).to(exchange).with(ROUTINGKEY2);
	}

	@Bean
	Binding bindingExchangeMessageBOfGreen(Queue BMessage, DirectExchange exchange) {
		return BindingBuilder.bind(BMessage).to(exchange).with(ROUTINGKEY3);
	}

}
