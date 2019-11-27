package com.start.zuul;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.start.zuul.filter.GrayFilter;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class AppZuul
{
    public static void main( String[] args )
    {
    	SpringApplication.run(AppZuul.class, args);
    }
    
    @Bean
	public GrayFilter grayFilter() {
		return new GrayFilter();
	}
}
