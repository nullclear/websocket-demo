package dev.yxy.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * WebSocket 握手请求拦截器，可用于拦截握手请求和响应，以及将 HTTP 属性传递到目标 WebSocketHandler，
 * 这里主要是设置握手时候，从 HTTP Session 中获取用户信息，传入 WebSocket 信息中。
 * Created by Nuclear on 2021/1/6
 */
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 握手前拦截，从 HTTP 中参数传入 WebSocket Attributes 方便后续取出相关参数
     *
     * @param request    请求对象
     * @param response   响应对象
     * @param wsHandler  WebSocket 处理器
     * @param attributes 从 HTTP 握手到与 WebSocket 会话关联的属性
     * @return 如果返回 true 则继续握手，返回 false 则终止握手
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 将 request 对象转换为 ServletServerHttpRequest 对象
        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        // 获取 HTTP Session 对象
        HttpSession session = serverRequest.getServletRequest().getSession();
        if (session != null) {
            // 从 HTTP Session 中获取用户信息
            String sessionId = session.getId();
            if (sessionId != null) {
                // 将从 HTTP Session 中获取的用户信息存入 WebSocket 的 Attributes 对象中
                attributes.put("X-ID", sessionId);
                // 继续握手
                return true;
            }
        }
        // 终止握手
        return false;
    }

    /**
     * 握手完成后调用
     *
     * @param request   请求对象
     * @param response  响应对象
     * @param wsHandler WebSocket 处理器
     * @param ex        异常信息
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
    }
}
