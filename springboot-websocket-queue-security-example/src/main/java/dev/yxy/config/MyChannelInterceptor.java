package dev.yxy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;

/**
 * 通道拦截器, 可以对通道进行配置
 * -----
 * todo 每次操作都会经过这个类
 * todo 接口里面还有一堆方法，看着用
 * Created by Nuclear on 2021/1/6
 */
public class MyChannelInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MyChannelInterceptor.class);

    /**
     * getUser()的Principal 来自 {@link HttpHandshakeHandler}
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        logger.info("preSend");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //stomp可以获取spring security的认证信息
        if (accessor != null) {
            Principal principal = accessor.getUser();
            if (principal != null) {
                logger.info("spring security的认证信息：{}", principal.getName());
            }
        }
        return message;
    }
}
