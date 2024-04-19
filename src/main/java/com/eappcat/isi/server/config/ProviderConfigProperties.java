package com.eappcat.isi.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "provider")
@Component
@Data
public class ProviderConfigProperties {
    private String name="aliyun";
    private String appKey;
    private String appSecret;
    private String projectId;
    private String region="";
    private Map<String,String> voiceMap=new HashMap<>();
}
