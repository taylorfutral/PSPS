
$("#getImage").click(function() {
    let time = new Date().getTime();
    console.log("getting new picture: " + time)
    $("#main-image").attr("src", "http://0.0.0.0:7777/api/app/image/get?timestamp="+time);
});


function readFile() {

    if (this.files && this.files[0]) {

        var FR= new FileReader();

        FR.addEventListener("load", function(e) {
            document.getElementById("img").src       = e.target.result;
            document.getElementById("b64").innerHTML = "image sent!";

            let topics = [];
            for(let i = 1; i <= 3; i++ ) {
                if($("#topic"+i).val() == "") {
                    ;
                }
                else {
                    topics.push($("#topic"+i).val());
                }
            }

            if(topics.length == 0) {
                document.getElementById("b64").innerHTML = "No topics specified, add a topic!";
                return;
            }


            console.log(topics);


            data = JSON.stringify( {
                topics: topics,
                image: e.target.result,
            });

            //makeAjaxRequest("POST", "http://0.0.0.0:7777/api/app/image/upload", data, null, null, null,)
        });

        FR.readAsDataURL( this.files[0] );
    }
}

document.getElementById("inp").addEventListener("change", readFile);