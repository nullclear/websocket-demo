package dev.yxy.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

/**
 * 通道拦截器, 可以对通道进行配置
 * Created by Nuclear on 2021/1/6
 */
public class MyChannelInterceptor implements ChannelInterceptor {

    /**
     * @see HttpHandshakeHandler getUser()的来源
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //stomp可以获取spring security的认证信息
        if (accessor != null) {
            accessor.getUser();
        }
        return message;
    }
}
