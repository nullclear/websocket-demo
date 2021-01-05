package dev.yxy.model;

/**
 * Created by Nuclear on 2021/1/5
 */
public class MessageBody {
    /**
     * 消息内容
     */
    private String content;
    /**
     * 广播转发的目标地址（告知 STOMP 代理转发到哪个地方）
     */
    private String destination;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
