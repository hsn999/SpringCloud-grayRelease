package com.start.zuul.config;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import com.start.commom.threadLocal.PassParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GrayFilter extends ZuulFilter {

	private static final String HEADER_TOKEN = "token";
	private static final Logger logger = LoggerFactory.getLogger(GrayFilter.class);

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 1000;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		String token = ctx.getRequest().getHeader(HEADER_TOKEN);

		String userId = token;
		log.info("======>userId:{}", userId);

		// List<String> userIdList = grayUserConfigProp.getUserIdList();
		// String version = userIdList.contains(userId) ?
		// grayUserConfigProp.getVersion() : null;
		// logger.info("=====>userId:{},version:{}", userId, version);

		// zuul本身调用微服务 //CoreHeaderInterceptor.initHystrixRequestContext(version); //
		// 传递给后续微服务
		String v = ctx.getRequest().getParameter("v");
		String version = v;
		if (v != null) {
			ctx.addZuulRequestHeader("version", version);
			Map<String, String> map = new HashMap<String, String>();
			map.put("version", version);
			PassParameters.set(map);
		}

		return null;
	}
}
