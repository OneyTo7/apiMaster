package com.apimaster.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String API_TASK_QUEUE = "api_task_queue";
    public static final String API_RESULT_QUEUE = "api_result_queue";
    
    // 交换机名称
    public static final String API_EXCHANGE = "api_exchange";
    
    // 路由键
    public static final String API_TASK_ROUTING_KEY = "api.task";
    public static final String API_RESULT_ROUTING_KEY = "api.result";

    // 创建队列
    @Bean
    public Queue apiTaskQueue() {
        return new Queue(API_TASK_QUEUE, true);
    }

    @Bean
    public Queue apiResultQueue() {
        return new Queue(API_RESULT_QUEUE, true);
    }

    // 创建交换机
    @Bean
    public DirectExchange apiExchange() {
        return new DirectExchange(API_EXCHANGE);
    }

    // 绑定队列到交换机
    @Bean
    public Binding apiTaskBinding(Queue apiTaskQueue, DirectExchange apiExchange) {
        return BindingBuilder.bind(apiTaskQueue).to(apiExchange).with(API_TASK_ROUTING_KEY);
    }

    @Bean
    public Binding apiResultBinding(Queue apiResultQueue, DirectExchange apiExchange) {
        return BindingBuilder.bind(apiResultQueue).to(apiExchange).with(API_RESULT_ROUTING_KEY);
    }

    // 消息转换器
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate配置
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }
}
