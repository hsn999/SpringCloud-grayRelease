package com.start.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
//import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;

import com.start.zuul.config.GrayFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * zuul使用swagger2聚合微服务api示例
 * 
 * @author oKong
 *
 */
@SpringBootApplication(scanBasePackages = { "com.start" }, exclude = DataSourceAutoConfiguration.class)
@EnableZuulProxy
@EnableDiscoveryClient
@Slf4j
public class ZuulSwaggerApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ZuulSwaggerApplication.class, args);
		log.info("spring-cloud-zuul-gateway启动!");
	}

	@Bean
	public GrayFilter grayFilter() {
		return new GrayFilter();
	}

}
