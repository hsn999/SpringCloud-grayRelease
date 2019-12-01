package com.start.app.consumer.feignService;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class HelloRemoteHystrix implements HelloRemote {

    @Override
    public String printDate() {
        return "Sorry there is a error";
    }
}
