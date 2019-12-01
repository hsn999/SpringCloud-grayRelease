package com.start.app.provider.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.start.commom.threadLocal.PassParameters;

import java.util.Date;
import java.util.Map;

@Configuration
@EnableWebMvc
@RestController
@Slf4j
public class AppServiceController {

    @RequestMapping("/api")
    public String printDate() {
    	Map<String,String> map = PassParameters.get();
    	String username = map.get("username");
        log.info("req: username={}", username);
        if (username != null) {
            return new Date().toString() + " service " + username;
        }
        return new Date().toString();
    }
}
