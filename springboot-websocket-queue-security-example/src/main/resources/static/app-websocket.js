// 设置 STOMP 客户端
let stompClient = null;
// 设置 WebSocket 进入端点
const SOCKET_ENDPOINT = "/point";
// 设置服务器端点，访问服务器中哪个接口
const SEND_ENDPOINT = "/app/test";
// 向服务器问好的接口
let welcome = "/app/hello";
// 异常处理地址
let error = "/user/queue/error";

// 原来的监听函数
let origin;
// 订阅地址
let subscribe = "/user/queue";
// 推送地址
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
        welcomeHandler();
        errorHandler();
    });
}

//注意: 可以同时订阅多个地址，意思就是不需要的地址需要解除订阅

//与服务器的单向交互
function welcomeHandler() {
    stompClient.subscribe(welcome, function (responseBody) {
        let msg = responseBody.body;
        $("#name").text(msg);
    });
}

//异常处理
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
            if (fromUser) {
                //输出到网页上
                $("#information").append("<tr><td>" + fromUser + ": " + receiveMessage.content + "</td></tr>");
            } else {
                //输出到网页上
                $("#information").append("<tr><td>群发消息: " + receiveMessage.content + "</td></tr>");
            }
        });
    } else {
        alert("未连接");
    }
}

//推送信息
function sendMessage() {
    if (stompClient != null) {
        const targetUser = $("#targetUser").val();
        // 设置推送地址
        destination = $("#destination").val();
        const sendContent = $("#content").val();
        // 设置待发送的消息内容
        let message = {"targetUser": targetUser, "destination": destination, "content": sendContent};
        // 支持事务
        let tx = stompClient.begin();
        // 推送消息
        stompClient.send(SEND_ENDPOINT, {}, JSON.stringify(message));
        tx.commit();
    } else {
        alert("未连接");
    }
}

//关闭网页时释放连接
window.onbeforeunload = function () {
    disconnect();
};