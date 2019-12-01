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
		
		String  version = null;
		
		
		
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
