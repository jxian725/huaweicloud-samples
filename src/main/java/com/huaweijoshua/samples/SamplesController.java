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

    //@RequestMapping(value = "/hw_ocr", method = POST)
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
}
