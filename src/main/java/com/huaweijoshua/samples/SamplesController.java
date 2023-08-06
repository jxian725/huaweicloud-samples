package com.huaweijoshua.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@CacheConfig(cacheNames = "token")
public class SamplesController {
    Logger logger = LoggerFactory.getLogger(SamplesController.class);
    private final ApplicationConfigProperties applicationConfig;

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

    @RequestMapping(value = "/hw_ocr", method = GET)
    @ResponseBody
    public String HW_OCR_API(@RequestParam("base64") String base64) {
        String token = getToken();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Auth-Token", token);
        String requestJson = "{\"image\":\"" + base64 + "\",\"detect_direction\":false,\"detect_font\":false}";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://ocr.ap-southeast-2.myhuaweicloud.com/v2/" + applicationConfig.projectID() + "/ocr/web-image", entity,String.class);
        return response.getBody();
    }

    @Cacheable("xst")
    public String getToken(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJson = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\":{\"user\":{\"name\":\""+ applicationConfig.authName() + "\",\"password\":\"" + applicationConfig.authPassword() + "\",\"domain\":{\"name\":\"" + applicationConfig.authDomain() + "\"}}}}, \"scope\":{\"project\":{\"name\":\"ap-southeast-2\"}}}}";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://iam.ap-southeast-2.myhuaweicloud.com/v3/auth/tokens", entity,String.class);
        HttpHeaders resHeaders = response.getHeaders();
        String xst = resHeaders.get("X-Subject-Token").toString().replaceAll("\\[", "").replaceAll("\\]","");;
        return xst;
    }
}
