var detected_text = [],detection_image = false;
const wrapper = document.getElementById("signature-pad");
const canvas = wrapper.querySelector("canvas");
var ctx = wrapper.querySelector("canvas").getContext("2d");
const backspace = document.getElementById("backspace");
const testkey = "xxxx"; //HanWang API -> Work WeChat Scan Login
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
    //ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    coordinate = "";
    document.getElementById("ocr-result").innerHTML = "";
});

backspace.addEventListener("click", () => {
    document.getElementById("input-recog").value = (document.getElementById("input-recog").value).substring(0,(document.getElementById("input-recog").value).length-1);
});

function InitThis() {
    canvas.addEventListener('touchstart', function (event) {
        if (event.targetTouches.length == 1) {
            event.preventDefault();// 阻止浏览器默认事件，重要
            var touch = event.targetTouches[0];
            mousePressed = true;
            Draw(touch.pageX - this.offsetLeft, touch.pageY - this.offsetTop, false);
        }
    },false);

    canvas.addEventListener('touchmove', function (event) {
        if (event.targetTouches.length == 1) {
            event.preventDefault();
            var touch = event.targetTouches[0];
            if (mousePressed) {
                Draw(touch.pageX - this.offsetLeft, touch.pageY - this.offsetTop, true);
            }
        }
    },false);

    canvas.addEventListener('touchend', function (event) {
        if (event.targetTouches.length == 1) {
            event.preventDefault();
            mousePressed = false;
            console.log("touchend");
        }else if(event.targetTouches.length == 0){
            mousePressed = false;
            coordinate = coordinate + "-1,0,";
            curTrace.traceStr = coordinate;
            HTRAPI(curTrace);
        }
    },false);


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
        HTRAPI(curTrace);
    };
}

function Draw(x, y, isDown) {
    //console.log(canvas.width,canvas.offsetWidth);
    const rect = canvas.getBoundingClientRect();
    x = Math.round((x - rect.left)/(rect.right - rect.left)*canvas.offsetWidth);
    y = Math.round((y - rect.top)/(rect.bottom - rect.top)*canvas.offsetHeight);
    //console.log(x,y);
    if (isDown) {
        ctx.beginPath();
        ctx.strokeStyle = "#191919";
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

function populateHTR(array){
    if(array.length>0){
        detected_text = [];
        document.getElementById("ocr-result").innerHTML = "";
        array.forEach((x) => {
            if(x.trim() !== ""){
                let unicode = `&#${x}`
                if(!detected_text.includes(unicode) && detected_text.length<=8) {
                    detected_text.push(unicode);
                }
            }
        });
        document.getElementById("ocr-result").innerHTML = "";
        detected_text.forEach((x) => {
            let li = document.createElement("li");
            let span = document.createElement("span");
            span.innerHTML = x;
            li.appendChild(span);
            document.getElementById("ocr-result").append(li);
        });
    }
    $("ul#ocr-result>li").click(function(){
        document.getElementById("input-recog").value += this.innerText;
        clearAll();
    });
}

function clearAll(){
    detected_text = [];
    //ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    coordinate = "";
    document.getElementById("ocr-result").innerHTML = "";
}

function HTRAPI2(strokes){
    $.ajax({
        type: "POST",
        url: `http://api.hanvon.com/rt/ws/v1/hand/single?key=${testkey}&code=83b798e7-cd10-4ce3-bd56-7b9e66ace93d`,
        contentType : "application/octet-stream",
        datatype : "json",
        crossDomain: false,
        jsonp:'callback',
        data: `{"uid":"","type":"1","lang":"chns","data":"${strokes.traceStr}"}`,
        async : false,
        success: function (msg) {
            let decrypt = JSON.parse(window.atob(msg));
            if(decrypt.code == 0){
                const unicodes = (decrypt.result).split(",");
                populateHTR(unicodes);
            }else{
                alert("Unable to recognize the input. Please try again.");
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log(xhr.status, `HTR API Error`);
        }
    });
}

function HTRAPI(strokes){
    $.ajax({
        type: "POST",
        url: `http://${window.location.host}/hw_htr`,
        data: {"strokes": JSON.stringify(strokes.traceStr), "ip": window.location.hostname},
        success: function (msg) {
            let decrypt = JSON.parse(window.atob(msg));
            if(decrypt.code == 0){
                const unicodes = (decrypt.result).split(",");
                populateHTR(unicodes);
            }else{
                alert("Unable to recognize the input. Please try again.");
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log(xhr.status, `HTR API Error`);
        }
    });
}


