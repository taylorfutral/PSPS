
function changeSelectDOM(res, statusText, xhr) {
    console.log("chagning select DOM");
    if(xhr == "Bad Request") {
        console.log("bad request");
    }
    else {
        let selectDOM = $("#selectDOM");

        selectDOM.empty();
        html = ``;
        html += `Select topic to subscribe to: <select id="subscribeTopic">`
        //selectDOM.append(`<select>`);
        for(let i = 0; i < res.topics.length; i++) {
            let topic = res.topics[i];
            console.log("topic: " + topic);
            //selectDOM.append(`<option value="${topic}">${topic}</option>`);
            html += `<option value="${topic}">${topic}</option>`;
        }
        html += `</select>`;
        html += `<button id="subscribeButton">Subscribe!</button><br><br>`
        selectDOM.append(html);
    }
}

makeAjaxRequest("GET", "http://0.0.0.0:7777/api/app/topics/all", null, null, changeSelectDOM, null )



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
}

$(document).on('click', '#subscribeButton', function(e) {
    console.log("subscribing to topic: " + $("#subscribeTopic").val());

    data = JSON.stringify({
        topic: $("#subscribeTopic").val(),
    });
    makeAjaxRequest("POST", "http://0.0.0.0:7777/api/app/topics/subscribe", data, null, changeSubscribeDOM, null);
});