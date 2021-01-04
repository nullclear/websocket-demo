package dev.yxy.controller;

import dev.yxy.service.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nuclear on 2021/1/4
 */
@Controller
public class SystemController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    //推送数据接口
    @ResponseBody
    @RequestMapping(value = {"/push/{sid}", "/push"})
    public Map<String, Object> pushToWeb(@PathVariable(name = "sid", required = false) String sid) {
        String message = "[天气预报]: 今天天气晴";
        Map<String, Object> result = new HashMap<>();
        WebSocketServer.sendInfo(message, sid);
        result.put("id", sid);
        result.put("msg", message);
        return result;
    }
}
