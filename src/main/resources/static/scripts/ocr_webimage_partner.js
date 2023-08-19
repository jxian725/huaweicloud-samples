var detected_text = [],detection_image = false;
const wrapper = document.getElementById("signature-pad");
const canvas = wrapper.querySelector("canvas");
var ctx = wrapper.querySelector("canvas").getContext("2d");
const backspace = document.getElementById("backspace");
var mousePressed = false;
var lastX, lastY;
var curTrace = new Object();
var coordinate = "";
window.onload = function(){
    InitThis();
}

function resizeCanvas() {
    const ratio =  Math.max(window.devicePixelRatio || 1, 1);
    canvas.width = canvas.offsetWidth * ratio;
    canvas.height = canvas.offsetHeight * ratio;
    canvas.getContext("2d").scale(ratio, ratio);
    //signaturePad.fromData(signaturePad.toData());
}
window.onresize = resizeCanvas;
resizeCanvas();

document.getElementById("clearButton").addEventListener("click", () => {
    //$("#result").text("");
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    coordinate = "";
});

backspace.addEventListener("click", () => {
    document.getElementById("input-recog").value = (document.getElementById("input-recog").value).substring(0,(document.getElementById("input-recog").value).length-1);
});

function InitThis() {
    //		触摸屏
    canvas.addEventListener('touchstart', function (event) {
        console.log(1);
        if (event.targetTouches.length == 1) {
            event.preventDefault();// 阻止浏览器默认事件，重要
            var touch = event.targetTouches[0];
            mousePressed = true;
            Draw(touch.pageX - this.offsetLeft, touch.pageY - this.offsetTop, false);
        }

    },false);

    canvas.addEventListener('touchmove', function (event) {
        console.log(2);
        if (event.targetTouches.length == 1) {
            event.preventDefault();// 阻止浏览器默认事件，重要
            var touch = event.targetTouches[0];
            if (mousePressed) {
                Draw(touch.pageX - this.offsetLeft, touch.pageY - this.offsetTop, true);
            }
        }

    },false);

    canvas.addEventListener('touchend', function (event) {
        console.log(3)
        if (event.targetTouches.length == 1) {
            event.preventDefault();
            mousePressed = false;
        }
    },false);


    //	   鼠标
    canvas.onmousedown = function (event) {
        mousePressed = true;
        Draw(event.pageX - this.offsetLeft, event.pageY - this.offsetTop, false);
    };

    canvas.onmousemove = function (event) {
        if (mousePressed) {
            Draw(event.pageX - this.offsetLeft, event.pageY - this.offsetTop, true);
        }
    };

    canvas.onmouseup = function (event) {
        mousePressed = false;
        coordinate = coordinate + "-1,0,";
        curTrace.traceStr = coordinate;
        HTRAPI();
    };
}

function Draw(x, y, isDown) {
    if (isDown) {
        ctx.beginPath();
        ctx.strokeStyle = "#ff0000";
        ctx.lineWidth = "3";
        ctx.lineJoin = "round";
        ctx.moveTo(lastX, lastY);
        ctx.lineTo(x, y);
        ctx.closePath();
        ctx.stroke();
    }
    lastX = x; lastY = y;
    coordinate = coordinate + x + "," + y + ",";
}

function HTRAPI(image){
    $.ajax({
        type: "POST",
        url: `http://${window.location.host}/hw_htr`,
        data: {"strokes": JSON.stringify(curTrace), "ip": window.location.hostname},
        success: function (msg) {
            console.log(msg)
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log(xhr.status, `HTR API Error`);
        }
    });
}

function recgHandline(){
    $.ajax({
        url:'http://handword.huaweiapi.hanvon.com/rt/ws/v1/hand/single?code= 83b798e7-cd10-4ce3-bd56-7b9e66ace93d'+lang,
        type: 'POST',
        secureuri:false,
        dataType: 'json',
        data: curTrace,
        success: function(data, status){
            if(data.code == "0"){
                console.log(data.result);
                //$("#result").text(data.result);
            }else{
                console.log("您书写的太有范儿了，我都不认识了");
                //$("#errorMsg").text("您书写的太有范儿了，我都不认识了");
                //$("#result").text("");
            }
        }
    });
}


