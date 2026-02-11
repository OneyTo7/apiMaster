package com.apimaster.service.impl;

import com.apimaster.config.RabbitMQConfig;
import com.apimaster.service.RabbitMQService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQServiceImpl implements RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    @Override
    public void sendApiTaskMessage(Object message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.API_EXCHANGE,
                RabbitMQConfig.API_TASK_ROUTING_KEY,
                message
        );
    }

    @Override
    public void sendApiResultMessage(Object message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.API_EXCHANGE,
                RabbitMQConfig.API_RESULT_ROUTING_KEY,
                message
        );
    }
}
