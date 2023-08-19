package com.huaweijoshua.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Controller
@CacheConfig(cacheNames = "token")
public class SamplesController {
    Logger logger = LoggerFactory.getLogger(SamplesController.class);
    private final ApplicationConfigProperties applicationConfig;

    @Autowired
    private HWCAuthenticationService HWCAuth;

    public SamplesController(ApplicationConfigProperties applicationConfig) {
        this.applicationConfig = applicationConfig;
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
        String token = HWCAuth.getToken(applicationConfig.authName(),applicationConfig.authPassword(),applicationConfig.authDomain());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Auth-Token", token);
        String requestJson = "{\"image\":\"" + base64 + "\",\"detect_direction\":false,\"detect_font\":false}";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://ocr.ap-southeast-2.myhuaweicloud.com/v2/" + applicationConfig.projectID() + "/ocr/web-image", entity,String.class);
        return response.getBody();
    }

    @PostMapping("/hw_htr")
    @ResponseBody
    public String HW_HTR_API(HTRRequest htrRequest) {
        logger.info("API Called");
        logger.info(String.valueOf(htrRequest));
        logger.info("END");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String requestJson = "{\"description\":\"{\"uid\":\"118.12.0.12\",\"type\":\"1\",\"lang\":\"chns\",\"data\":\"76.55,79.55,51.7,119.35,43.75,129.3,-1,0\"}\",\"format\":\"STREAM\"}";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://handword4.apistore.huaweicloud.com/rt/ws/v1/hand/single?key=f39fe3191db6403c9673bae88f275477&code=83b798e7-cd10-4ce3-bd56-7b9e66ace93d", entity,String.class);
        return response.getBody();
    }
}
