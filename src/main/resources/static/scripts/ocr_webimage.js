var detected_text = [];
const wrapper = document.getElementById("signature-pad");
const canvas = wrapper.querySelector("canvas");
const backspace = document.getElementById("backspace");
const signaturePad = new SignaturePad(canvas, {
    backgroundColor: 'rgb(255, 255, 255)',
    minWidth: 3,
    maxWidth: 6,
});

function resizeCanvas() {
    const ratio =  Math.max(window.devicePixelRatio || 1, 1);
    canvas.width = canvas.offsetWidth * ratio;
    canvas.height = canvas.offsetHeight * ratio;
    canvas.getContext("2d").scale(ratio, ratio);
    signaturePad.fromData(signaturePad.toData());
}
window.onresize = resizeCanvas;
resizeCanvas();

document.getElementById("clearButton").addEventListener("click", () => {
    clearAll();
});

backspace.addEventListener("click", () => {
    document.getElementById("input-recog").value = (document.getElementById("input-recog").value).substring(0,(document.getElementById("input-recog").value).length-1);
});

signaturePad.addEventListener("endStroke", () => {
    const dataURL = signaturePad.toDataURL();
    const xhr = new XMLHttpRequest();
    xhr.onload = function() {
        populateOCR(this.responseText);
    }
    xhr.open("GET", `http://${window.location.host}/hw_ocr?base64=${encodeURIComponent(dataURL.substring(22))}`);
    xhr.send();
}, { once: false });

function populateOCR(json){
    const ocrResult = JSON.parse(json);
    const probability = ocrResult.result.words_block_list.sort((a,b) => (a.confidence > b.confidence) ? 1 : ((b.confidence > a.confidence) ? -1 : 0));
    if(probability.length>0){
        probability.forEach((x) => {
            if(/[a-zA-Z0-9_-]/.test(x.words)){
                let temp = x.words.replaceAll(/([a-zA-Z0-9×])/g, "");
                if(temp.length>0 && !detected_text.includes(temp)){
                    detected_text.unshift(temp);
                }
            }else{
                if(!detected_text.includes(x.words)) {
                    detected_text.unshift(x.words);
                }
            }
        });
        document.getElementById("ocr-result").innerHTML = "";
        detected_text.forEach((x) => {
            let li = document.createElement("li");
            let span = document.createElement("span");
            span.innerText = x;
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
    detected_text=[];
    signaturePad.clear();
    document.getElementById("ocr-result").innerHTML = "";
}

