package com.start.app.consumer.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;

@Configuration
@EnableWebMvc
@RestController
@Slf4j
@Api(tags="consumer")
public class ApiController {
	
	/* @Autowired
	 HelloRemote helloRemote;*/

    @RequestMapping("/api")
    @ApiOperation(value="api")
    public String printDate(@RequestParam(name = "username", required = false) String username,String token) {
        log.info("req: username={}", username);
        
        //String helloReturn = helloRemote.printDate();
        
        //log.info("helloReturn from service" + helloReturn);
        
        if (username != null) {
            return new Date().toString() + " " + username;
        }
        
        
        
        return new Date().toString();
    }
}
