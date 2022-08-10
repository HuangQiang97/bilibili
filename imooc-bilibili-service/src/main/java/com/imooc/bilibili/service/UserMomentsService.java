package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.imooc.bilibili.dao.UserMomentsDao;
import com.imooc.bilibili.domain.UserMoment;
import com.imooc.bilibili.service.config.RabbitMQProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户动态service
 *
 * @author huangqiang
 * @date 2022/4/11 20:51
 * @see
 * @since
 */
@Service
public class UserMomentsService {

    @Autowired
    private UserMomentsDao userMomentsDao;

    @Autowired
    private RabbitMQProducerConfig producer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 用户发布动态
    public void addUserMoments(UserMoment userMoment) throws Exception {
        userMoment.setCreateTime(new Date());
        // 保存到数据库
        userMomentsDao.addUserMoments(userMoment);
        producer.sendMessage(userMoment);
    }

    // 从redis中获取关注用户发布的动态
    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        // 从redis中获取str形式的动态集合
        String key = "subscribed-" + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        // redisTemplate.delete(key);
        // str->list(obj)
        return JSONArray.parseArray(listStr, UserMoment.class);
    }
}
