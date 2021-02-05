package dev.yxy.config;

import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

/**
 * 装饰器模式增强对象
 * -----
 * 对WebSocketHandler进行增强
 * Created by Nuclear on 2021/1/6
 */
public class HttpWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {
    private static final Logger logger = LoggerFactory.getLogger(HttpWebSocketHandlerDecoratorFactory.class);

    /**
     * 配置 webSocket 处理器
     *
     * @param webSocketHandler webSocket 处理器
     * @return webSocket 处理器
     */
    @Override
    public WebSocketHandler decorate(WebSocketHandler webSocketHandler) {
        //看起来有点像simple-example中的WebSocketServer里四个注解
        return new WebSocketHandlerDecorator(webSocketHandler) {
            /**
             * websocket 连接时执行的动作
             * @param session    websocket session 对象
             * @throws Exception 异常对象
             * @see HttpHandshakeInterceptor 里的 WebSocketHandler 和 Map 都会传到这里
             */
            @Override
            public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                logger.info("afterConnectionEstablished");
                // 握手拦截器添加的属性可以在这里调用 {@link HttpHandshakeInterceptor}
                logger.info("X-ID [ {} ]", session.getAttributes().get("X-ID"));

                // 不需要额外添加属性 其实也能获取SessionID
                if (session instanceof WebSocketServerSockJsSession) {
                    WsSession wsSession = ((WebSocketServerSockJsSession) session).getNativeSession(WsSession.class);
                    if (wsSession != null) {
                        logger.info("SESSION ID is [ {} ]", wsSession.getHttpSessionId());
                    }
                }

                // 输出进行 websocket 连接的用户信息
                if (session.getPrincipal() != null) {
                    String username = session.getPrincipal().getName();
                    logger.info("用户[ {} ]上线", username);
                }

                super.afterConnectionEstablished(session);
            }

            /**
             * websocket 关闭连接时执行的动作
             * @param session websocket session 对象
             * @param closeStatus 关闭状态对象
             * @throws Exception 异常对象
             */
            @Override
            public void afterConnectionClosed(final WebSocketSession session, CloseStatus closeStatus) throws Exception {
                logger.info("afterConnectionClosed");

                // 不需要额外添加属性 其实也能获取SessionID
                if (session instanceof WebSocketServerSockJsSession) {
                    WsSession wsSession = ((WebSocketServerSockJsSession) session).getNativeSession(WsSession.class);
                    if (wsSession != null) {
                        logger.info("SESSION ID is [ {} ]", wsSession.getHttpSessionId());
                    }
                }

                // 输出关闭 websocket 连接的用户信息
                if (session.getPrincipal() != null) {
                    String username = session.getPrincipal().getName();
                    logger.info("用户[ {} ]下线，关闭状态：{}", username, closeStatus.toString());
                }

                super.afterConnectionClosed(session, closeStatus);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                super.handleMessage(session, message);
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                super.handleTransportError(session, exception);
            }
        };
    }
}
