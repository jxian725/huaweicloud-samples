$(document).ready(function() {
    document.getElementById("hwcloud-btn").onchange = function () {
        switch (this.value){
            case "ocr_webimage":
                location.href = `/ocr_webimage`;
                break;
            case "htr_webimage":
                location.href = `/htr_webimage`;
                break;
            default:
        }
    }
});