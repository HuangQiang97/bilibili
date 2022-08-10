package com.imooc.bilibili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.domain.UserFollowing;
import com.imooc.bilibili.domain.UserMoment;
import com.imooc.bilibili.service.UserFollowingService;
import io.netty.util.internal.StringUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听q-user-moment上的消息，收到消息后以<userId,moment>形式主动保存到redis中
 *
 * @author huangqiang
 * @date 2022/4/12 9:51
 * @see
 * @since
 */
@Component
@RabbitListener(queues = "q-user-moment")
public class RabbitMQMomentConsumerConfig {

    @Autowired
    UserFollowingService userFollowingService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @RabbitHandler
    public void handle(UserMoment userMoment) {
        // 动态发布者
        Long userId = userMoment.getUserId();
        // 该用户的粉丝都会收到该通知
        List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
        for (UserFollowing fan : fanList) {
            // 接收到通知后将用户动态放入redis等待用户来取
            String key = "subscribed-" + fan.getUserId();
            // 该用户可能关注多个用户，收到多个用户的动态通知
            String subscribedListStr = redisTemplate.opsForValue().get(key);
            List<UserMoment> subscribedList;
            if (StringUtil.isNullOrEmpty(subscribedListStr)) {
                subscribedList = new ArrayList<>();
            } else {
                subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
            }
            // 键入该用户的待消费动态通知集合
            subscribedList.add(userMoment);
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
        }

    }
}