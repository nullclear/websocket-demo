package dev.yxy.model;

/**
 * Created by Nuclear on 2021/1/6
 */
public class MessageBody {
    /**
     * 发送消息的用户
     */
    private String from;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 目标用户（告知 STOMP 代理转发到哪个用户）
     */
    private String targetUser;
    /**
     * 广播转发的目标地址（告知 STOMP 代理转发到哪个地方）
     */
    private String destination;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
