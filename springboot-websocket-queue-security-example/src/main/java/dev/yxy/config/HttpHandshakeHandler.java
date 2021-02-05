package dev.yxy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 创建用于与正在建立会话过程中的 WebSocket 的用户相关联的方法，可以在此处配置用于关联用户相关的信息。
 * -----
 * todo 这个方法可以从request中获取Principal
 * <p>
 * Created by Nuclear on 2021/1/6
 */
public class HttpHandshakeHandler extends DefaultHandshakeHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpHandshakeHandler.class);

    /**
     * Principal的信息就是在这里注入WebSocket的
     * 用于与正在建立会话过程中的 WebSocket 的用户相关联的方法，可以在此处配置进行关联的用户信息。
     *
     * @param request    握手请求对象
     * @param wsHandler  WebSocket 处理器
     * @param attributes 从 HTTP 握手到与 WebSocket 会话关联的属性
     * @return WebSocket会话的用户 或者 null
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        logger.info("determineUser");
        return request.getPrincipal();
    }
}
