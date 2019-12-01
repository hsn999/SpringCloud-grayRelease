package com.start.commom.core;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConditionalOnClass(com.netflix.loadbalancer.ZoneAvoidanceRule.class)
public class MyRibbonConfiguration implements InitializingBean {

 


    @Value("#{'${loadbalanced.services}'.split(',')}")
    private List<String> loadbalancedServices;

     

    /**

     * 默认使用切流量的负载均衡策略

     */

    @Value("${ribbon.NFLoadBalancerRuleClassName}")
    private String ribbonLoadBancerRule;

 

    @Override

    public void afterPropertiesSet() throws Exception {

        if (null != loadbalancedServices){

            for (String service : loadbalancedServices){

                String key = service + ".ribbon.NFLoadBalancerRuleClassName";

                System.setProperty(key, ribbonLoadBancerRule);

            }

        }

    }

 

}