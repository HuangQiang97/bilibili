package com.imooc.bilibili.service;


import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.domain.Danmu;
import com.imooc.bilibili.service.config.RabbitMQProducerConfig;
import com.imooc.bilibili.service.util.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * websocket service，用于从websocket连接中接收、发送消息
 *
 * @author huangqiang
 * @date 2022/4/20 21:11
 * @see
 * @since
 */
@Component
@ServerEndpoint("/danmu/{token}")
public class WebSocketService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 用于统计当前视频在线观看人数
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    // 用于保持<sessionId,WebSocketService>，保存用户连接信息
    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    // 与某个用户会话
    private Session session;

    private String sessionId;

    // websocket连接的用户Id
    private Long userId;

    // 上下文：由于WebSocketService非单列，spring只会对第一个WebSocketService注入所需依赖，
    // 后续WebSocketService无法获得需要的依赖
    // 需要手动从ApplicationContext获取需要的依赖
    private static ApplicationContext APPLICATION_CONTEXT;

    // 在启动类的main方法中调用，传入ApplicationContext
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 当连接建立时触发
     *
     * @param session，双方会话信息
     * @param token，用户token用于保存弹幕时设置弹幕具体信息
     */
    @OnOpen
    public void openConnection(Session session, @PathParam("token") String token) {
        try {
            // 认证
            this.userId = TokenUtil.verifyToken(token);
        } catch (Exception e) {
            logger.error("登录后才能发送弹幕");
            sendMessage("登录后才能发送弹幕");
            return;
        }
        // 保存当前连接会话信息
        this.sessionId = session.getId();
        this.session = session;

        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            // 更新会话信息
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        } else {
            // 添加新会话信息
            WEBSOCKET_MAP.put(sessionId, this);
            // 在线用户加一
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户连接成功：" + this.userId + "，在线人数：" + ONLINE_COUNT.get());
        // 连接建立完成
        this.sendMessage("0");

    }

    /**
     * 断开连接时触发
     */
    @OnClose
    public void closeConnection() {
        // 删除保存的会话信息，在线用户数减一
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户退出：" + userId + "在线人数为：" + ONLINE_COUNT.get());
    }

    /**
     * 用户发送消息时触发，接收到用户发送弹幕后，向当前在线用户推送新弹幕消息
     *
     * @param message:string 消息
     */
    @OnMessage
    public void onMessage(String message) {
        logger.info("用户：" + userId + "，报文：" + message);
        if (!StringUtil.isNullOrEmpty(message)) {
            try {
                Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                danmu.setUserId(userId);
                //向当前在线用户推送新弹幕消息
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    String sessionId = entry.getValue().getSessionId();
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("sessionId", sessionId);
                    map.put("danmu", JSONObject.toJSONString(danmu));
                    RabbitMQProducerConfig producer = (RabbitMQProducerConfig) APPLICATION_CONTEXT.getBean("rabbitMQProducerConfig");
                    producer.sendMessage(map);
                }
                if (this.userId != null) {
                    //保存弹幕到数据库
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");
                    danmuService.asyncAddDanmu(danmu);
                    //保存弹幕到redis
                    danmuService.addDanmusToRedis(danmu);
                }
            } catch (Exception e) {
                logger.error("弹幕接收出现问题");
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Throwable error) {
    }

    // 向客户端发送消息
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //指定时间向在线客户发送在线人数
    @Scheduled(fixedRate = 5000)
    private void noticeOnlineCount() throws IOException {
        // 轮询在线用户
        for (Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.session.isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为" + ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }
}
