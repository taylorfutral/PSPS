function showSubscriptions(res, statusText, xhr) {
    console.log("chagning subscriptions DOM");
    if(xhr == "Bad Request") {
        console.log("bad request");
    }
    else {
        let subscribeDOM = $("#subscribeDOM");

        subscribeDOM.empty();
        html = `You are subscribed to `;
        if(res.topics.length == 0) {
            html += ` nothing yet. Please subscribe!`
            subscribeDOM.append(html);
            return
        }
        for(let i = 0; i < res.topics.length; i++) {
            let topic = res.topics[i];
            console.log("subscription topic: " + topic);
            html += `${topic}, `;
        }
        html = html.substr(0,html.length-2)
        subscribeDOM.append(html);
    }
}


function changeSelectDOM(res, statusText, xhr) {
    console.log("chagning select DOM");
    if(xhr == "Bad Request") {
        console.log("bad request");
    }
    else {
        let selectDOM = $("#selectDOM");
        let topicsDOM = $("#currentTopics");
        topicsDOM.empty();
        selectDOM.empty();
        html = ``;
        topicsHtml = ``;
        html += `Select topic to subscribe to: <select id="subscribeTopic">`
        topicsHtml += `<br>Current topics: `

        for(let i = 0; i < res.topics.length; i++) {
            let topic = res.topics[i];
            console.log("topic: " + topic);
            //selectDOM.append(`<option value="${topic}">${topic}</option>`);
            html += `<option value="${topic}">${topic}</option>`;
            topicsHtml += `${topic}, `;
        }

        html += `</select>&nbsp`;
        html += `<button id="subscribeButton">Subscribe!</button>&nbsp`
        html += `<button id="unsubscribeButton">Unsubscribe!</button><br><br>`
        topicsHtml = topicsHtml.substr(0, topicsHtml.length-2)

        topicsDOM.append(topicsHtml);
        selectDOM.append(html);

        makeAjaxRequest("GET", "http://0.0.0.0:7777/api/app/topics/subscribedto", null, null, showSubscriptions, null);
    }
}

makeAjaxRequest("GET", "http://0.0.0.0:7777/api/app/topics/all", null, null, changeSelectDOM, null );


function makeAjaxRequest(type, url, data, headers, func200, badFunc) {
    $.ajax({
        method: type,
        dataType: "json",
        contentType: "application/json",
        url: url,
        data: data,
        statusCode: {
            200: func200,
            400: badFunc,
            500: badFunc,
        }
    });
}

function changeSubscribeDOM(res, statusText, xhr) {
    console.log("changing subscribeDOM")
    makeAjaxRequest("GET", "http://0.0.0.0:7777/api/app/topics/subscribedto", null, null, showSubscriptions, null);
}

$(document).on('click', '#subscribeButton', function(e) {
    console.log("subscribing to topic: " + $("#subscribeTopic").val());

    data = JSON.stringify({
        topic: $("#subscribeTopic").val(),
    });
    makeAjaxRequest("POST", "http://0.0.0.0:7777/api/app/topics/subscribe", data, null, changeSubscribeDOM, null);
});

$(document).on('click', '#unsubscribeButton', function(e) {
    console.log("subscribing to topic: " + $("#subscribeTopic").val());

    data = JSON.stringify({
        topic: $("#subscribeTopic").val(),
    });
    makeAjaxRequest("POST", "http://0.0.0.0:7777/api/app/topics/unsubscribe", data, null, changeSubscribeDOM, null);
});