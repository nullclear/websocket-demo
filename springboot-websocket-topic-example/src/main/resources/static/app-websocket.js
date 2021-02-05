// 设置 STOMP 客户端
let stompClient = null;
// 设置 WebSocket 进入端点，仅用于初始化连接
const SOCKET_ENDPOINT = "/point";
// 设置服务器端点，访问服务器中哪个@MessageMapping接口
const SEND_ENDPOINT = "/app/test";

// 原来的监听函数
let origin;
// 订阅地址
let subscribe = "/topic";
// 推送地址
let destination = "/topic";

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
            //输出到网页上
            $("#information").append("<tr><td>" + receiveMessage.content + "</td></tr>");
        });
    } else {
        alert("未连接");
    }
}

//推送信息
function sendMessage() {
    if (stompClient != null) {
        // 设置推送地址
        destination = $("#destination").val();
        const sendContent = $("#content").val();
        // 设置待发送的消息内容
        let message = {"destination": destination, "content": sendContent};
        // 推送消息
        stompClient.send(SEND_ENDPOINT, {}, JSON.stringify(message));
    } else {
        alert("未连接");
    }
}

//关闭网页时释放连接
window.onbeforeunload = function () {
    disconnect();
};