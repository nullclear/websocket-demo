package dev.yxy.controller;

import dev.yxy.model.MessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Objects;

/**
 * Created by Nuclear on 2021/1/6
 */
@Controller
public class MessageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 这个方法有三个功能
     * -----
     * 抛出异常
     * 群发消息
     * 点对点发送消息，将消息发送到指定用户
     */
    @MessageMapping("/test")
    public void sendMessageToUser(Principal principal, MessageBody messageBody) {
        // todo 推送地址为/error时，抛出测试异常，见下面的方法 handleException(Exception e)
        if (Objects.equals(messageBody.getDestination(), "/error")) {
            throw new RuntimeException("测试异常处理方法");
        } else {
            // 设置发送消息的用户
            messageBody.setFrom(principal.getName());
            // 调用 STOMP 代理进行消息转发
            String targetUser = messageBody.getTargetUser();
            if (targetUser == null || targetUser.isBlank()) {
                // todo 群发消息
                template.convertAndSend(messageBody.getDestination(), messageBody);
            } else {
                // todo 点对点发送
                template.convertAndSendToUser(targetUser, messageBody.getDestination(), messageBody);
            }
        }
    }


    /**
     * 见js中的welcomeHandler()，初始化的时候已经订阅
     * -----
     * 一次性响应
     * 服务端直接返回消息给客户端
     * 与Http模型不同，这种请求是异步的
     */
    @SubscribeMapping("/hello")
    public String welcome(Principal principal) {
        return "你好, " + principal.getName();
    }

    /**
     * 见js中的errorHandler()，初始化的时候已经订阅
     * -----
     * 消息发生异常时的处理
     * 信息直接推送给消息发送者
     * 默认broadcast是true
     * 即不同Session的相同username的用户都会收到此推送
     */
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/error")
    public String handleException(Exception e) {
        return e.getMessage();
    }
}

