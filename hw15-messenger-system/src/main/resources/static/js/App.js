let stompClient = null;

const setConnected = (connected) => {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#chatLine").show();
    } else {
        $("#chatLine").hide();
    }
    $("#message").html("");
}

const connect = () => {
    stompClient = Stomp.over(new SockJS('/gs-guide-websocket'));
    stompClient.connect({}, (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        sendRequestForUsers();
        stompClient.subscribe('/topic/users', (user) => showUsers(JSON.parse(user.body)))
        stompClient.subscribe('/topic/create', (user) => {
            updateTable(JSON.parse(user.body))
        })
    });
}


const updateTable = (userT) => {
    $("#list").append("<tr><td>" + userT.id + "</td><td>" + userT.name + "</td><td>" + userT.age + "</td><td>" + userT.login +
        "</td><td>" + userT.password + "</td></tr>")
}


const disconnect = () => {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

const showUsers = (users) => {
    users.forEach(user => {
        $("#list").append("<tr><td>" + user.id + "</td><td>" + user.name +
            "</td><td>" + user.age + "</td><td>" + user.login + "</td><td>" + user.password + "</td></tr>");
    });
};

const sendRequestForUsers = () => stompClient.send("/app/users", {}, {});

const saveUser = () => {
    const user = {
        'name': document.getElementById("nameUser").value,
        'age': document.getElementById("ageUser").value,
        'login': document.getElementById("loginUser").value,
        'password': document.getElementById("passwordUser").value
    }
    stompClient.send("/app/create", {}, JSON.stringify(user))
    document.getElementById("create-form").reset();
}

$(function () {
    $("form").on('submit', (event) => {
        event.preventDefault();
    });
    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
});
