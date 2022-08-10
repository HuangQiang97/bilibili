package com.imooc.bilibili.domain.constant;

/**
 * 动态消息MQ常量
 *
 * @author huangqiang
 * @date 2022/4/11 17:16
 * @see
 * @since
 */
public interface UserMQConstant {

    // 消息发送时使用的exchange
    public static final String Exchange_MOMENTS = "user-moment";

    // 接收时使用的队列
    public static final String Queue_MOMENTS = "q-user-moment";

    // 消息发送时使用的exchange
    public static final String Exchange_DANMU = "user-danmu";

    // 接收时使用的队列
    public static final String Queue_DANMU = "q-user-danmu";
}
