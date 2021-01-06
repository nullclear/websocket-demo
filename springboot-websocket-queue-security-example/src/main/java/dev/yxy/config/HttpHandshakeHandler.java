package dev.yxy.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 创建用于与正在建立会话过程中的 WebSocket 的用户相关联的方法，可以在此处配置用于关联用户相关的信息。
 * Created by Nuclear on 2021/1/6
 */
public class HttpHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * Principal的信息就是在这里注入WebSocket的
     * 用于与正在建立会话过程中的 WebSocket 的用户相关联的方法，可以在此处配置进行关联的用户信息。
     *
     * @param request    握手请求对象
     * @param wsHandler  WebSocket 处理器
     * @param attributes 从 HTTP 握手到与 WebSocket 会话关联的属性
     * @return 对于 WebSocket 的会话的用户或者 null
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return request.getPrincipal();
    }
}
