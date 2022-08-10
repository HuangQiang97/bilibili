package com.imooc.bilibili.service.config;

import com.imooc.bilibili.domain.UserMoment;
import com.imooc.bilibili.domain.constant.UserMQConstant;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发送消息
 *
 * @author huangqiang
 * @date 2022/4/12 9:39
 * @see
 * @since
 */
@Component
public class RabbitMQProducerConfig {

    @Autowired
    private AmqpTemplate amqpTemplate;

    // 发送用户动态
    public void sendMessage(UserMoment userMoment) {
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(UserMQConstant.Exchange_MOMENTS, "", userMoment);
        System.out.println("send user-moment:" + userMoment.toString());
    }

    // 发送弹幕消息
    public void sendMessage(Map<String, Object> map) {
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(UserMQConstant.Exchange_DANMU, "", map);
        System.out.println("send user: " + map.get("sessionId") + map.get("danmu"));
    }
}