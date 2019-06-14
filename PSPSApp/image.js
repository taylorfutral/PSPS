
$("#getImage").click(function() {
    let time = new Date().getTime();
    console.log("getting new picture: " + time)
    $("#main-image").attr("src", "http://0.0.0.0:7777/api/app/image?timestamp="+time);
});