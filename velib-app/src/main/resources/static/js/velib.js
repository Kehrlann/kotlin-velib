const websockets = [];

const WIDTH = 1000;
const HEIGHT = 400;
const MARGIN_LEFT = 50;
const MARGIN_BOTTOM = 50;
const TRANSITION_DURATION_MS = 1000;
const BUFFER_SIZE = 1;
let total_received = 0;
let last_received = new Date();

const velib_data = {};
const messageBuffer = [];

/*****************************************
 * d3 extensions setup
 *****************************************/
d3.selection.prototype.setPosition = function (xScale, yFunc, heightFunc) {
    return this
        .attr("x", d => xScale(d.id))
        .attr("y", yFunc)
        .attr("height", heightFunc);
};

d3.selection.prototype.setPositionFree = function (xScale, yScale, maxDomain) {
    return this.setPosition(xScale, d => yScale(maxDomain - d.free), d => yScale(d.free));
};


d3.selection.prototype.setPositionAvailable = function (xScale, yScale, maxDomain) {
    return this.setPosition(xScale, d => yScale(maxDomain - (d.free + d.available)), d => yScale(d.available));
};


d3.selection.prototype.transitionToFullWidthBar = function (xScale) {
    this
        .attr("width", 0)
        .transition()
        .duration(TRANSITION_DURATION_MS)
        .attr("width", (d, i) => xScale.bandwidth());
};


d3.transition.prototype.setPosition = d3.selection.prototype.setPosition;
d3.transition.prototype.setPositionFree = d3.selection.prototype.setPositionFree;
d3.transition.prototype.setPositionAvailable = d3.selection.prototype.setPositionAvailable;

/*****************************************
 * Websocketty stuff
 *****************************************/
function startConnection(onmessageCallback) {
    const socket = new WebSocket("ws://localhost:8080/ws/status");
    socket.onmessage = onmessageCallback;
    websockets.push(socket);
}

function startPrintingConnection() {
    const printingCallback = function (event) {
        const msg = event.data;

        const existingMessages = document.getElementById("messages").innerHTML;
        const newMessage = "<div>" + msg + "</div>";
        document.getElementById("messages").innerHTML = newMessage + existingMessages;
    };
    startConnection(printingCallback);
}

function d3WebsocketCallback(event) {

    total_received++;
    if(total_received % 10 === 0) {
        const tempDate = new Date();
        console.log("It took", (tempDate - last_received), "ms to receive 10 data points.");
        last_received = tempDate;
    }

    const data = JSON.parse(event.data);
    if (data.hasProblem) {
        // console.error("Problem with station", data);
        return;
    }

    if (messageBuffer.length < BUFFER_SIZE) {
        messageBuffer.push(data);
    } else {
        messageBuffer.splice(0, BUFFER_SIZE).forEach(
            d => {
                velib_data[d.id] = d;
                updateSvgData(Object.values(velib_data).sort(sortStation));
            }
        )
    }
}

function startd3Connection() {
    if (websockets.length === 0) {
        startConnection(d3WebsocketCallback);
    }
}

function sendMessage() {
    websockets.forEach(function (ws, i) {
        ws.send("Hi ! From " + i + "th connection. Date is : " + new Date())
    });
}

function closeConnections() {
    websockets.forEach(function (ws) {
        ws.close();
    });

    // Empty array.
    websockets.splice(0, websockets.length);
}

/*****************************************
 * dee-three-y stuff
 *****************************************/
function sortStation(a, b) {
    const totalDiff = (b.available + b.free) - (a.available + a.free);
    if (totalDiff !== 0) return totalDiff;

    return b.free - a.free;
}

function updateSvgData(data) {
    // TODO : add axes ?
    // console.log("UPDATE", data[0]);
    const maxTotal = d3.max(data, d => d.available + d.free);

    const yScale = d3
        .scaleLinear()
        .domain([0, maxTotal])
        .range([0, HEIGHT]);

    const xScale = d3
        .scaleBand()
        .domain(data.map(d => d.id))
        .range([0, WIDTH])
        .paddingInner(0.1)
        .paddingOuter(0.1);


    const allRects = d3.select("svg").selectAll("g.rectangle")
        .data(data, d => d.id);

    allRects.exit().remove();

    const newGroup = allRects
        .enter()
        .append("g")
        .attr("class", "rectangle");

    allRects.select("rect.free")
        .transition()
        .duration(TRANSITION_DURATION_MS)
        .attr("width", (d, i) => xScale.bandwidth())
        .setPositionFree(xScale, yScale, maxTotal);


    allRects.select("rect.available")
        .transition()
        .duration(TRANSITION_DURATION_MS)
        .attr("width", (d, i) => xScale.bandwidth())
        .setPositionAvailable(xScale, yScale, maxTotal);


    newGroup
        .append("rect")
        .style("fill", "MEDIUMORCHID")
        .attr("class", "free")
        .setPositionFree(xScale, yScale, maxTotal)
        .transitionToFullWidthBar(xScale);

    newGroup
        .append("rect")
        .style("fill", "DARKTURQUOISE")
        .attr("class", "available")
        .setPositionAvailable(xScale, yScale, maxTotal)
        .transitionToFullWidthBar(xScale);
}

function cleanMessages() {
    document.getElementById("messages").innerHTML = "";
}
