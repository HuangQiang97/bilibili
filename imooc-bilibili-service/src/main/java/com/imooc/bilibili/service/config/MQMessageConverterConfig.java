package com.imooc.bilibili.service.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用于将Object转换为string，再往MQ发送
 *
 * @author huangqiang
 * @date 2022/4/12 9:49
 * @see
 * @since
 */
@Configuration
public class MQMessageConverterConfig {
    @Bean
    MessageConverter createMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}