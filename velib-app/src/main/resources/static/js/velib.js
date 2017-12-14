const websockets = [];

let total_received = 0;
let first_received = new Date();

/*****************************************
 * Websocketty stuff
 *****************************************/
function startConnection(url, onmessageCallback) {
    const socket = new WebSocket(url);
    socket.onmessage = onmessageCallback;
    websockets.push(socket);
}

function startMonitoringConnection(target) {
    stopMonitoring();

    if(websockets.length > 0)
    {
        return;
    }

    total_received = 0;
    first_received = new Date();
    clearMonitoringData();
    const url = `ws://localhost:8080/ws/status/${target}`;
    startConnection(url, monitoringCallback(`.${target}`));
}


function monitoringCallback(target) {
    return function(event) {
        total_received++;
        const msgRate = 1000 * total_received / (new Date() - first_received);

        const monitoringStart = "<div> Started monitoring : " + first_received + "</div>";
        const currentRate = "<div>Current message rate : " + msgRate + " messages / second</div>";
        document.querySelector(target).innerHTML = monitoringStart + currentRate;
    };
}

function clearMonitoringData() {
    document.querySelector(".sync").innerHTML = "<div>Not measuring</div>";
    document.querySelector(".async").innerHTML = "<div>Not measuring</div>";
}

function stopMonitoring() {
    closeConnections();
    clearMonitoringData();
}

function closeConnections() {
    websockets.forEach(function (ws) {
        ws.close();
    });

    // Empty array.
    websockets.splice(0, websockets.length);
}