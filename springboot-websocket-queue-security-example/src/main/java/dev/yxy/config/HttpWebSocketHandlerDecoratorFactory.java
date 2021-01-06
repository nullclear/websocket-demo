package dev.yxy.config;

import org.apache.tomcat.websocket.WsSession;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

/**
 * 装饰器模式增强对象
 * Created by Nuclear on 2021/1/6
 */
public class HttpWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    /**
     * 配置 webSocket 处理器
     *
     * @param webSocketHandler webSocket 处理器
     * @return webSocket 处理器
     */
    @Override
    public WebSocketHandler decorate(WebSocketHandler webSocketHandler) {
        return new WebSocketHandlerDecorator(webSocketHandler) {
            /**
             * websocket 连接时执行的动作
             * @param session    websocket session 对象
             * @throws Exception 异常对象
             * @see HttpHandshakeInterceptor WebSocketHandler 和 Map 都和这里的有关
             */
            @Override
            public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                // 握手拦截器添加的属性可以在这里调用
                System.out.printf("X-ID [ %s ]\n", session.getAttributes().get("X-ID"));

                // 不需要额外添加属性 其实也能获取SessionID
                if (session instanceof WebSocketServerSockJsSession) {
                    WsSession wsSession = ((WebSocketServerSockJsSession) session).getNativeSession(WsSession.class);
                    if (wsSession != null) {
                        System.out.printf("SESSION ID is [ %s ] ", wsSession.getHttpSessionId());
                    }
                }

                // 输出进行 websocket 连接的用户信息
                if (session.getPrincipal() != null) {
                    String username = session.getPrincipal().getName();
                    System.out.printf("用户[ %s ]上线\n", username);
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
                if (session instanceof WebSocketServerSockJsSession) {
                    WsSession wsSession = ((WebSocketServerSockJsSession) session).getNativeSession(WsSession.class);
                    if (wsSession != null) {
                        System.out.printf("SESSION ID is [ %s ] ", wsSession.getHttpSessionId());
                    }
                }

                // 输出关闭 websocket 连接的用户信息
                if (session.getPrincipal() != null) {
                    String username = session.getPrincipal().getName();
                    System.out.printf("用户[ %s ]下线 ", username);
                    System.out.printf("关闭状态 %s\n", closeStatus.toString());
                }

                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}
