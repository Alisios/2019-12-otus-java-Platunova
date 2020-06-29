let stompClient = null;
//
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
       stompClient.subscribe('/topic/create' , (user) => {updateTable(JSON.parse(user.body))})
      // stompClient.subscribe('/topic/create' , () =>  sendRequestForUsers())
  });
}

const updateTable = (userT) =>{
    $("#list").append("<tr><td>"+ userT.id+ "</td><td>" + userT.chatId + "</td><td>"+ userT.concert.artist + "</td><td>"+ userT.concert.place +"</td><td>"+ userT.concert.date +"</td><td>"+ userT.concert.concertUrl +"</td><td>"+ userT.concert.shouldBeMonitored +"</td><td>"+ userT.isMonitoringSuccessful+"</td><td>"+ userT.isDateExpired +"</td><td>"+ userT.dateOfMonitorFinish +
        "</td><td>"+ userT.messageText + "</td></tr>") }

const disconnect = () => {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

const showUsers = (users) =>{
 users.forEach(user => {$("#list").append(
"<tr><td>" + user.id +
"</td><td>"+ user.chatId +
"</td><td>"+ user.concert.artist +
"</td><td>"+ user.concert.place +
"</td><td>"+ user.concert.date +
"</td><td>"+ user.concert.concertUrl +
"</td><td>"+ user.concert.shouldBeMonitored +
"</td><td>"+ user.isMonitoringSuccessful +
"</td><td>"+user.isDateExpired +
"</td><td>"+ user.dateOfMonitorFinish +
"</td><td>"+ user.messageText + "</td></tr>"
);
}
);
};

const sendRequestForUsers = () => stompClient.send("/app/users" , {}, {});


const deleteUser=() => {
const userId = document.getElementById("deleteId").value;
stompClient.send("/app/delete", {}, JSON.stringify(userId))
document.getElementById("delete-form").reset();

var table = document.getElementById('list');
deleteRows(table, userId)
}

const deleteRows =(table,userId) =>{
    var allRows = table.getElementsByTagName("tr");
    var i = allRows.length;
    while (i--) {
        if (table.rows[i].cells[0].innerHTML==userId) {
            table.deleteRow(i);
        }
    }
}


const saveUser = () => {

    const concert = {
        'artist' : document.getElementById("artist").value,
        'date' : document.getElementById("date").value,
        'place' : document.getElementById("place").value,
        'concertUrl' : document.getElementById("concertUrl").value,
    }

    const user = {
        'chatId' : document.getElementById("chatId").value,
        'concert': concert,
        'dateOfMonitorFinish' : document.getElementById("dateOfMonitorFinish").value
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
//  $("#send").click(sendName);
});
