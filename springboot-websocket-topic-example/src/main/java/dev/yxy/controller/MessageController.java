package dev.yxy.controller;

import dev.yxy.model.MessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by Nuclear on 2021/1/5
 */
@Controller
public class MessageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 消息发送工具对象
     */
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 广播发送消息，将消息发送到指定的目标地址
     */
    @MessageMapping("/test")
    public void sendTopicMessage(MessageBody messageBody) {
        // 将消息发送到 WebSocket 配置类中配置的代理中（/topic）进行消息转发
        template.convertAndSend(messageBody.getDestination(), messageBody);
    }
}
