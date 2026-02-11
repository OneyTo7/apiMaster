package com.apimaster.service;

public interface RabbitMQService {

    /**
     * 发送消息
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息内容
     */
    void sendMessage(String exchange, String routingKey, Object message);

    /**
     * 发送API任务消息
     * @param message 任务消息
     */
    void sendApiTaskMessage(Object message);

    /**
     * 发送API结果消息
     * @param message 结果消息
     */
    void sendApiResultMessage(Object message);
}
