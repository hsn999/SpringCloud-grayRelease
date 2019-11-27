package com.start.commom.core;


import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.start.commom.threadLocal.PassParameters;

 
@Configuration
@ConditionalOnClass(Feign.class)
public class DefaultFeignConfig implements RequestInterceptor {
 
    @Value("${spring.application.name}")
    private String appName;
 
    @Override
    public void apply(RequestTemplate requestTemplate)
    {
    	Map<String,String> map = PassParameters.get();
    	
        String username = map.get("username");
        if(StringUtils.isNotEmpty(username)){
            requestTemplate.header("username", username);
        }
        String token = map.get("token");
        if(StringUtils.isNotEmpty(token)){
            requestTemplate.header("token", token);
        }
        
        String  version = map.get("version");
        if(StringUtils.isNotEmpty(version)){
            requestTemplate.header("version", version);
        }
        
        
        
    }
 
}
