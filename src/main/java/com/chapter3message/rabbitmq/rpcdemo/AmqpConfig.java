package com.chapter3message.rabbitmq.rpcdemo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
//@EnableRabbit和@Configuration一起使用，可以加在类或者方法上，这个注解开启了容器对注册的bean的@RabbitListener检查
@EnableRabbit
public class AmqpConfig {

	/**
	 * 每个项目配置自己的exchange，格式为项目名称简写+exchange，如： rabbitmq.direct.exchange=ccs.direct.exchange，可以防止队列重名。
	 * routeKey和queue的名称也要加上项目名称简写防止重名。
	 * 队列分为同步和异步模式，异步模式支持异步回复功能。ccs.queue.sync队列为同步，ccs.queue.async和ccs.queue.async.reply组合为异步。
	 * */
	public static final String EXCHANGE   = "spring-boot-exchange";
    public static final String SYNC_ROUTINGKEY = "ccs.binding.sync";
    public static final String ASYNC_ROUTINGKEY = "ccs.binding.async";
    public static final String R_ASYNC_ROUTINGKEY = "ccs.binding.async.reply";
    public static final String SYNC_QUEUE = "ccs.queue.sync";
    public static final String ASYNC_QUEUE = "ccs.queue.async";
    //定义回复queue
    public static final String R_ASYNC_QUEUE = "ccs.queue.async.reply";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("localhost:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true); //必须要设置，才能进行消息的回调
        return connectionFactory;
    }

    //队列配置需要在生产者消费者两边同时配置。
    //如果只在生产者方配置，rabbitmq服务器中无队列时，会导致消费者服务器启动时找不到监听队列报错。
    //direct方式：根据routingKey将消息发送到所有绑定的queue中
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue squeue() {
        return new Queue(SYNC_QUEUE, true); //队列持久
    }

    @Bean
    public Queue asqueue() {
    	return new Queue(ASYNC_QUEUE, true); //队列持久
    }

    @Bean
    public Queue rasqueue() {
    	return new Queue(R_ASYNC_QUEUE, true); //队列持久
    }

    @Bean
    public Binding sbinding() {
    	return BindingBuilder.bind(squeue()).to(defaultExchange()).with(SYNC_ROUTINGKEY);
    }

    @Bean
    public Binding abinding() {
    	return BindingBuilder.bind(asqueue()).to(defaultExchange()).with(ASYNC_ROUTINGKEY);
    }

    @Bean
    public Binding rabinding() {
    	return BindingBuilder.bind(rasqueue()).to(defaultExchange()).with(R_ASYNC_ROUTINGKEY);
    }

    //json转换器，消息可以自动根据转换器转换格式，不配置时默认为java序列化，可以自行配置
    @Bean
    public MessageConverter messageConverter(){
    	return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
    	RabbitTemplate template = new RabbitTemplate(connectionFactory);
    	template.setExchange(EXCHANGE);
        template.setReplyTimeout(6000);
        template.setMessageConverter(messageConverter());
        template.setRetryTemplate(retryTemplate());
        return template;
    }

    //retryTemplate为连接失败时的重发队列所用的templete
    @Bean
    public RetryTemplate retryTemplate(){
    	RetryTemplate retryTemplate = new RetryTemplate();
    	ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    	backOffPolicy.setInitialInterval(500);
    	backOffPolicy.setMultiplier(10.0);
    	backOffPolicy.setMaxInterval(10000);
    	retryTemplate.setBackOffPolicy(backOffPolicy);
    	return retryTemplate;
    }

}
