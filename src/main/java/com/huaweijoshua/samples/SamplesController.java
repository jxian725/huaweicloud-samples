package com.huaweijoshua.samples;

import com.cloud.apigateway.sdk.utils.Client;
import com.cloud.apigateway.sdk.utils.Request;
import org.apache.http.Header;
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


    @GetMapping("/ocr_webimage_partner")
    public String OCR_ChineseHandwritingRecognitionPartner(){
        return "ocr_webimage_partner";
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
            //Set the AK/SK to sign and authenticate the request.
            request.setKey("xxx");
            request.setSecret("xxx");

            //The following example shows how to set the request URL and parameters to query a VPC list.

            //Specify a request method, such as GET, PUT, POST, DELETE, HEAD, and PATCH.
            request.setMethod("POST");

            //Set a request URL in the format of https://{Endpoint}/{URI}.
            request.setUrl("http://handword4.huaweiapi.hanvon.com/rt/ws/v1/hand/single?code=83b798e7-cd10-4ce3-bd56-7b9e66ace93d");

            //Add header parameters, for example, x-domain-id for invoking a global service and x-project-id for invoking a project-level service.
            request.addHeader("Content-Type", "application/octet-stream");

            //Add a body if you have specified the PUT or POST method. Special characters, such as the double quotation mark ("), contained in the body must be escaped.
            request.setBody("{\"description\":\"{\"uid\":\"118.12.0.12\",\"type\":\"1\",\"lang\":\"chns\",\"data\":\"76.55,79.55,51.7,119.35,43.75,129.3,-1,0\"}\",\"format\":\"STREAM\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        CloseableHttpClient client = null;
        try {
            //Sign the request.
            HttpRequestBase signedRequest = Client.sign(request);

            //Send the request.
            client = HttpClients.custom().build();
            HttpResponse response = client.execute(signedRequest);

            //Print the status line of the response.
            System.out.println(response.getStatusLine().toString());

            //Print the header fields of the response.
            Header[] resHeaders = response.getAllHeaders();
            for (Header h : resHeaders) {
                System.out.println(h.getName() + ":" + h.getValue());
            }

            //Print the body of the response.
            org.apache.http.HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                System.out.println(System.getProperty("line.separator") + EntityUtils.toString(resEntity, "UTF-8"));
            }

            return response.getEntity().toString();
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
