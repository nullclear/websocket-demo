package dev.yxy.service;

import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本质上类似一个观察者模式，每一个Session应该都是与前端建立连接的实例 {@link WsSession}
 * Created by Nuclear on 2021/1/4
 */
@Service
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    //WsSession的池
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     * -----
     * 将新用户加入Map，并发送成功消息
     */
    @OnOpen
    public void onOpen(@PathParam("sid") String sid, Session session) {
        sessionPools.put(sid, session);
        sendMessage(session, String.format("用户[%s]连接成功", sid));
        logger.info("新用户[{}]加入, 当前在线人数为: {}", sid, sessionPools.size());
    }

    /**
     * 连接关闭调用的方法
     * -----
     * 将用户从Map中删除
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        sessionPools.remove(sid);
        logger.info("用户[{}]退出, 当前在线人数为: {}", sid, sessionPools.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * -----
     * 群发消息
     */
    @OnMessage
    public void onMessage(@PathParam("sid") String sid, String message) {
        logger.info("收到用户[{}]的信息: {}", sid, message);
        sessionPools.forEachValue(10, session -> sendMessage(session, String.format("用户[%s]: %s", sid, message)));
    }

    /**
     * 错误回调
     */
    @OnError
    public void onError(@PathParam("sid") String sid, Throwable error) {
        logger.error(String.format("用户[%s]请求时发生错误", sid), error);
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendMessage(Session session, String message) {
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.error(String.format("信息[%s]推送失败", message), e);
            }
        }
    }

    /**
     * 服务端推送消息到个人或者全体用户
     */
    public static void sendInfo(String sid, String message) {
        if (sid == null) {
            sessionPools.forEachValue(10, session -> sendMessage(session, message));
            logger.info("群发消息: {}", message);
        } else {
            sendMessage(sessionPools.get(sid), message);
            logger.info("推送消息到用户[{}], 推送内容: {}", sid, message);
        }
    }
}
