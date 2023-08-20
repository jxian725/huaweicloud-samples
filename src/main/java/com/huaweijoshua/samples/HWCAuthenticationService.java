package com.huaweijoshua.samples;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class HWCAuthenticationService {

    @Cacheable("xst")
    public String getToken(String authName, String authPassword, String authDomain){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJson = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\":{\"user\":{\"name\":\""+ authName + "\",\"password\":\"" + authPassword + "\",\"domain\":{\"name\":\"" + authDomain + "\"}}}}, \"scope\":{\"project\":{\"name\":\"ap-southeast-2\"}}}}";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://iam.ap-southeast-2.myhuaweicloud.com/v3/auth/tokens", entity,String.class);
        HttpHeaders resHeaders = response.getHeaders();
        return Objects.requireNonNull(resHeaders.get("X-Subject-Token")).toString().replaceAll("\\[", "").replaceAll("\\]","");
    }

}
