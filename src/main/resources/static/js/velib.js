const websockets = [];

const WIDTH = 1000, HEIGHT = 400, MARGIN_LEFT = 50, MARGIN_BOTTOM = 50;

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
        var msg = event.data;

        var existingMessages = document.getElementById("messages").innerHTML;
        var newMessage = "<div>" + msg + "</div>";
        document.getElementById("messages").innerHTML = newMessage + existingMessages;
    };
    startConnection(printingCallback);
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

function cleanMessages() {
    document.getElementById("messages").innerHTML = "";
}

/*****************************************
 * dee-three-y stuff
 *****************************************/

function oneShot() {
    // 1. Create scales (scaleband ?)
    // 2. Create axes ... ?
    // 3. Add data

    const maxTotal = d3.max(data, d => d.available);

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


    // Red stuff
    // newGroup
    //     .append("rect")
    //     // .filter(d => d.hasProblem)
    //     .attr("class", "red")
    //     .attr("x", d => xScale(d.id))
    //     .attr("width", (d, i) => xScale.bandwidth())
    //     .attr("y", 0)
    //     .attr("height", HEIGHT)
    //     .style("fill", "LIGHTCORAL");

    allRects.select("rect.free")
        .transition(1000)
        .attr("x", d => xScale(d.id))
        .attr("width", (d, i) => xScale.bandwidth())
        .attr("y", d => yScale(maxTotal - d.free))
        .attr("height", d => yScale(d.free));

    allRects.select("rect.available")
        .transition(1000)
        .attr("x", d => xScale(d.id))
        .attr("width", (d, i) => xScale.bandwidth())
        .attr("y", d => yScale(maxTotal - (d.free + d.available)))
        .attr("height", d => yScale(d.available));

    newGroup
        .append("rect")
        .style("fill", "MEDIUMORCHID")
        .attr("class", "free")
        // .filter(d => !d.hasProblem)
        .attr("x", d => xScale(d.id))
        .attr("y", d => yScale(maxTotal - d.free))
        .attr("height", d => yScale(d.free))
        .attr("width", 0)
        .transition(1000)
        .attr("width", (d, i) => xScale.bandwidth());

    newGroup
        .append("rect")
        .style("fill", "DARKTURQUOISE")
        .attr("class", "available")
        // .filter(d => !d.hasProblem)
        .attr("x", d => xScale(d.id))
        .attr("y", d => yScale(maxTotal - (d.free + d.available)))
        .attr("height", d => yScale(d.available))
        .attr("width", 0)
        .transition(1000)
        .attr("width", (d, i) => xScale.bandwidth())
}