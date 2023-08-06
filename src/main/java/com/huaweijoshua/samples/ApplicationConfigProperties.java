package com.huaweijoshua.samples;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ocrwi")
public record ApplicationConfigProperties(String authDomain, String authName, String authPassword, String projectID) {
}
