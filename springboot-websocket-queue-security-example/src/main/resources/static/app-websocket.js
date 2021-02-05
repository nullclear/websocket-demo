// 设置 STOMP 客户端
let stompClient = null;
// 设置 WebSocket 进入端点，仅用于初始化连接
const SOCKET_ENDPOINT = "/point";
// 设置服务器端点，访问服务器中哪个接口
const SEND_ENDPOINT = "/app/test";

// 单向订阅地址 todo 注意这个地址不一样
let welcome = "/app/hello";
// 广播地址
let topic = "/topic";
// 广播地址
let queue = "/queue";
// 异常处理地址
let error = "/user/queue/error";

// 原来的监听函数
let origin;
// 订阅地址，加前缀就是单点用户通道，不加就是广播通道
let subscribe = "/user/queue";
// 推送地址，沿用广播地址就行，群发或者单发是服务器控制的
let destination = "/queue";


//进行连接
function connect() {
    // 设置 SOCKET
    const socket = new SockJS(SOCKET_ENDPOINT);
    // 配置 STOMP 客户端
    stompClient = Stomp.over(socket);
    // STOMP 客户端连接
    stompClient.connect({}, function (frame) {
        console.log("=====连接成功=====");
        console.log(frame);
        console.log("=====连接成功=====");
        $("#connect_btn").addClass("disabled");
        $("#disconnect_btn").removeClass("disabled");
        welcomeHandler();
        topicHandler();
        queueHandler();
        errorHandler();
        subscribeMessage();
    });
}

//todo 注意: 可以同时订阅多个地址，意思就是不需要的地址需要解除订阅

//与服务器的单向交互，见 MessageController 的 @SubscribeMapping("/hello")
function welcomeHandler() {
    stompClient.subscribe(welcome, function (responseBody) {
        let msg = responseBody.body;
        $("#name").text(msg);
    });
}

//接收/topic的群发消息
function topicHandler() {
    stompClient.subscribe(topic, function (responseBody) {
        const receiveMessage = JSON.parse(responseBody.body);
        let fromUser = receiveMessage.from;
        //输出到网页上
        $("#information").append("<tr><td>[Topic]群发消息来自-" + fromUser + ": " + receiveMessage.content + "</td></tr>");
    });
}

//接收/queue群发消息
function queueHandler() {
    stompClient.subscribe(queue, function (responseBody) {
        const receiveMessage = JSON.parse(responseBody.body);
        let fromUser = receiveMessage.from;
        //输出到网页上
        $("#information").append("<tr><td>[Queue]群发消息来自-" + fromUser + ": " + receiveMessage.content + "</td></tr>");
    });
}

//接收异常处理，见 MessageController 的 handleException(Exception e)
function errorHandler() {
    stompClient.subscribe(error, function (responseBody) {
        let msg = JSON.stringify(responseBody.body);
        alert(msg);
    });
}

//断开连接
function disconnect() {
    stompClient.disconnect(function () {
        console.log("=====断开连接=====");
        stompClient = null;
        $("#connect_btn").removeClass("disabled");
        $("#disconnect_btn").addClass("disabled");
    });
}

//订阅信息
function subscribeMessage() {
    if (stompClient != null) {
        // 设置订阅地址
        subscribe = $("#subscribe").val();
        // 退定原来的监听函数
        if (origin) {
            origin.unsubscribe();
        }
        // 执行订阅消息监听函数
        origin = stompClient.subscribe(subscribe, function (responseBody) {
            const receiveMessage = JSON.parse(responseBody.body);
            let fromUser = receiveMessage.from;
            //输出到网页上
            $("#information").append("<tr><td>" + fromUser + ": " + receiveMessage.content + "</td></tr>");
        });
        $("#subscribe_btn").addClass("disabled");
        $("#unsubscribe_btn").removeClass("disabled");
    } else {
        alert("未连接");
    }
}

//解除订阅
function unsubscribeMessage() {
    if (origin) {
        origin.unsubscribe();
        $("#subscribe_btn").removeClass("disabled");
        $("#unsubscribe_btn").addClass("disabled");
    }
}

//推送信息
function sendMessage() {
    if (stompClient != null) {
        const targetUser = $("#targetUser").val();
        // 设置推送地址
        destination = $("#destination").val();
        let sendContent = $("#content").val();
        if (!sendContent) {
            sendContent = new Date().getTime();
        }
        // 设置待发送的消息内容
        let message = {"targetUser": targetUser, "destination": destination, "content": sendContent};
        // todo 支持事务
        let tx = stompClient.begin();
        // 推送消息
        stompClient.send(SEND_ENDPOINT, {}, JSON.stringify(message));
        // todo 事务提交
        tx.commit();
    } else {
        alert("未连接");
    }
}

//关闭网页时释放连接
window.onbeforeunload = function () {
    disconnect();
};