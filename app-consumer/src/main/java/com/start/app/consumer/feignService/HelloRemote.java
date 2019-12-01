package com.start.app.consumer.feignService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "app-provider",fallback = HelloRemoteHystrix.class)
public interface HelloRemote {

    @RequestMapping(value = "/api")
    String printDate();

}
