# SpringCloud-grayRelease
整合nacos（Euraka）实现灰度发布

在一般情况下，升级服务器端应用，需要将应用源码或程序包上传到服务器，然后停止掉老版本服务，再启动新版本。但是这种简单的发布方式存在两个问题，一方面，在新版本升级过程中，服务是暂时中断的，另一方面，如果新版本有BUG，升级失败，回滚起来也非常麻烦，容易造成更长时间的服务不可用。

什么是灰度发布呢？要想了解这个问题就要先明白什么是灰度。灰度从字面意思理解就是存在于黑与白之间的一个平滑过渡的区域，所以说对于互联网产品来说，上线和未上线就是黑与白之分，而实现未上线功能平稳过渡的一种方式就叫做灰度发布。

互联网产品的几个特点：用户规模大、版本更新频繁。新版本的每次上线，产品都要承受极大的压力，而灰度发布很好的规避了这种风险。

在了解了什么是灰度发布的定义以后，就可以来了解一下灰度发布的具体操作方法了。可以通过很多种形式来抽取一部分用户，比如说选择自己的VIP用户，或者选择一些活跃用户，把这些用户分成两批，其中一批投放A版本，另外一批投放B版本，在投放之前就要对各种可能存在的数据做到收集记录工作，这样才能在投放以后查看两个版本的用户数据反馈，通过大量的数据分析以及调查来确定最后使用哪一个版本来进行投放。

那么，在springcloud的分布式环境中，我们如何来区分用户（版本），如何来指定这部分用户使用不同版本的微服务?这篇文章将会通过实际的例子来说明这个过程。

假设用户发起一个访问，服务的调用路径为：用户--> ZUUL -->SERVICE1-->SERVICE2，那么我们在ZUUL和SERVICE1都需要实现自定义的访问路由。

下面这个设计的重点主要在：

1. 利用threadlocal+feign实现http head中实现版本信息的传递

2. 使用nacos的元数据，定义我们需要的灰度服务

3. 自定义ribbon的路由规则，根据nacos的元数据选择服务节点



公共配置：
1. ThreadLocal
~~~
package com.start.commom.threadLocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class PassParameters {

        private static final Logger log = LoggerFactory.getLogger(PassParameters.class);

        private static final ThreadLocal localParameters = new ThreadLocal();

        public static <T> T get(){
            T t = (T) localParameters.get();
            log.info("ThreadID:{}, threadLocal {}", Thread.currentThread().getId(), JSON.toJSONString(t));
            return t;
        }

        public static <T> void set(T t){
            log.info("ThreadID:{}, threadLocal set {}", Thread.currentThread().getId(), JSON.toJSONString(t));
            localParameters.set(t);
        }
    }

~~~
2. AOP拦截请求头
package com.start.commom.aop;

~~~
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.start.commom.threadLocal.PassParameters;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @author hsn
 */
@Aspect
@Order(85)
@Component
public class ApiRequestAspect {
    private static Logger logger = LoggerFactory.getLogger(ApiRequestAspect.class);


    @Pointcut("execution(* com.start.app..controller..*Controller*.*(..))")
    private void anyMethod() {
    }
 
    /**
     * 方法调用之前调用
     */
    @Before(value= "anyMethod()")
    public void doBefore(JoinPoint jp){
        logger.info("开始处理请求信息！");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
 
        Map<String,String> map = new HashMap<>();
        String username =  request.getHeader("username");
        String token = request.getHeader("token");
        String version = request.getHeader("version");
        
        
        if(version == null) {
            version = request.getParameter("v");
        }
        
        
        map.put("username", username);
        map.put("token", token);
        map.put("version", version);
        
        //将map放到threadLocal中
        PassParameters.set(map);
    }
 
    /**
     * 方法之后调用
     */
    @AfterReturning(pointcut = "anyMethod()")
    public void  doAfterReturning(){
        
    }
    
}

~~~

3. 实现自己的GrayMetadataRule
GrayMetadataRule 将会从nacos中获取元服务器的信息，并根据这个信息选择服务器

~~~
package com.start.commom.core;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.base.Optional;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
//import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.start.commom.threadLocal.PassParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryClient;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class GrayMetadataRule extends ZoneAvoidanceRule {
    public static final String META_DATA_KEY_VERSION = "version";

    private static final Logger logger = LoggerFactory.getLogger(GrayMetadataRule.class);

    @Override
    public Server choose(Object key) {

        List<Server> servers = this.getLoadBalancer().getReachableServers();

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        // 需要从head取灰度标识
        //String version = "mx";
        Map<String,String> map = PassParameters.get();  
        
        String  version = null;
        
        
        
        if(map != null && map.containsKey("version")) {
            version = map.get("version");
        }
        
        logger.info("GrayMetadataRule:"+version);
        
        /*if(StringUtils.isEmpty(version)){
           
        }*/
        


        List<Server> noMetaServerList = new ArrayList<>();
        for (Server server : servers) {
            if (!(server instanceof NacosServer)) {
                logger.error("参数非法，server = {}", server);
                throw new IllegalArgumentException("参数非法，不是NacosServer实例！");
            }

            NacosServer nacosServer = (NacosServer) server;
            Instance instance = nacosServer.getInstance();

            Map<String, String> metadata = instance.getMetadata();
            
            if(version !=null) {
                // version策略
                String metaVersion = metadata.get(META_DATA_KEY_VERSION);
                if (!StringUtils.isEmpty(metaVersion)) {
                    if (metaVersion.equals(version)) {
                        return server;
                    }
                } else {
                    noMetaServerList.add(server);
                }
            }else {
                noMetaServerList.add(server);
            }
            
        }

        if (StringUtils.isEmpty(version) && !noMetaServerList.isEmpty()) {
            logger.info("====> 无请求header...");
            return originChoose(noMetaServerList, key);
        }

        return null;

    }

    private Server originChoose(List<Server> noMetaServerList, Object key) {
        Optional<Server> server = getPredicate().chooseRoundRobinAfterFiltering(noMetaServerList, key);
        if (server.isPresent()) {
            return server.get();
        } else {
            return null;
        }
    }
}
~~~

4. 设置环境变量
自定义的路由规则，需要在 application.properties 中配置才能使用，（service1.ribbon.NFLoadBalancerRuleClassName=com.start.commom.core.GrayMetadataRule   service1就是要用这个规则的具体服务），这个配置的实际作用就是设置了一个环境变量，如果服务很多，我们创建一个数组，用代码创建 ，下面这个配置就是通过配置文件读取需要利用这个路由规则的服务列表，创建环境变量

~~~
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

~~~

