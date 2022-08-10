package com.imooc.bilibili.service.config;


import com.imooc.bilibili.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听q-user-danmu上的消息，收到消息后根据用户session发送消息
 *
 * @author huangqiang
 * @date 2022/4/12 9:51
 * @see
 * @since
 */
@Component
@RabbitListener(queues = "q-user-danmu")
public class RabbitMQDanmuConsumerConfig {

    @RabbitHandler
    public void handle(Map<String, Object> map) {

        String sessionId = (String) (map.get("sessionId"));
        String message = (String) map.get("danmu");
        // 将消息并发送给指定用户
        WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);
        if (webSocketService.getSession().isOpen()) {
            webSocketService.sendMessage(message);
        }
    }
}