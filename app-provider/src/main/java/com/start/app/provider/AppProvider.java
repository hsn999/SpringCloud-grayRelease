package com.start.app.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;



/**
 * Hello world!
 *
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.start"}, exclude = DataSourceAutoConfiguration.class)
public class AppProvider
{
    public static void main( String[] args )
    {
    	SpringApplication.run(AppProvider.class, args);
    }
}
