package com.huaweijoshua.samples;

import com.cloud.apigateway.sdk.utils.Client;
import com.cloud.apigateway.sdk.utils.Request;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Controller
@CacheConfig(cacheNames = {"token"})
public class SamplesController {
    Logger logger = LoggerFactory.getLogger(SamplesController.class);
    private final ApplicationConfigPropertiesOCR acOCR;

    @Autowired
    private HWCAuthenticationService HWCAuth;

    public SamplesController(ApplicationConfigPropertiesOCR acOCR) {
        this.acOCR = acOCR;
    }

    @RequestMapping
    public String Home_Index(){
        return "index";
    }

    @GetMapping("/ocr_webimage")
    public String OCR_ChineseHandwritingRecognition(){
        return "ocr_webimage";
    }


    @GetMapping("/htr_webimage")
    public String OCR_ChineseHandwritingRecognitionPartner(){
        return "htr_webimage";
    }

    @PostMapping("/hw_ocr")
    @ResponseBody
    public String HW_OCR_API(@RequestBody String enc_base64) {
        String base64 = enc_base64.replaceAll("%2F", "/");
        String token = HWCAuth.getToken(acOCR.authName(),acOCR.authPassword(),acOCR.authDomain());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Auth-Token", token);
        String requestJson = "{\"image\":\"" + base64 + "\",\"detect_direction\":false,\"detect_font\":false}";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://ocr.ap-southeast-2.myhuaweicloud.com/v2/" + acOCR.projectID() + "/ocr/web-image", entity,String.class);
        return response.getBody();
    }

    @PostMapping("/hw_htr")
    @ResponseBody
    public String HW_HTR_API(HTRRequest htrRequest) {
        Request request = new Request();
        try {
            request.setKey(acOCR.hanwangKey());
            request.setSecret(acOCR.hanwangSecret());
            request.setMethod("POST");
            request.setUrl("http://handword4.apistore.huaweicloud.com/rt/ws/v1/hand/single?code=83b798e7-cd10-4ce3-bd56-7b9e66ace93d");
            request.addHeader("Content-Type", "application/octet-stream");
            request.setBody("{\"uid\":\""+htrRequest.getip()+"\",\"type\":\"1\",\"lang\":\"chns\",\"data\":"+htrRequest.getStrokes()+"}");
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        String responseStr = "";
        CloseableHttpClient client = null;
        try {
            HttpRequestBase signedRequest = Client.sign(request);
            client = HttpClients.custom().build();
            HttpResponse response = client.execute(signedRequest);
            org.apache.http.HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(System.getProperty("line.separator") + responseStr);
            }

            return responseStr;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
