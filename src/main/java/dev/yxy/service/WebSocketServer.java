package dev.yxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Nuclear on 2021/1/4
 */
@Service
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //每个用户的id
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        this.sid = sid;
        webSocketSet.add(this);
        try {
            sendMessage("用户[" + sid + "]连接成功");
            logger.info("新用户[" + sid + "]加入, 当前在线人数为: " + webSocketSet.size());
        } catch (IOException e) {
            logger.error("websocket IO Exception");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        logger.info("用户[" + sid + "]退出, 当前在线人数为: " + webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("收到用户[" + sid + "]的信息: " + message);
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage("[" + sid + "]: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 错误回调
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message, String sid) {
        //这里可以设定只推送给这个sid的，null则全部推送
        for (WebSocketServer item : webSocketSet) {
            try {
                if (sid == null) {
                    item.sendMessage(message);
                    logger.info("群发消息: " + message);
                } else if (Objects.equals(item.sid, sid)) {
                    item.sendMessage(message);
                    logger.info("推送消息到用户[" + sid + "], 推送内容: " + message);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
