/*
 * package com.start.zuul.config;
 * 
 * 
 * import com.netflix.zuul.ZuulFilter; import
 * com.netflix.zuul.context.RequestContext; import
 * io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder; import
 * org.springframework.context.annotation.Configuration;
 * 
 * import javax.servlet.http.HttpServletRequest;
 * 
 * import static
 * org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;
 * 
 *//**
	 * @author wuweifeng wrote on 2018/1/17.
	 *//*
		 * //@Configuration public class PreFilter extends ZuulFilter {
		 * 
		 * @Override public int filterOrder() { return PRE_DECORATION_FILTER_ORDER - 1;
		 * }
		 * 
		 * @Override public String filterType() { return PRE_TYPE; }
		 * 
		 * @Override public boolean shouldFilter() { RequestContext ctx =
		 * RequestContext.getCurrentContext(); // a filter has already forwarded // a
		 * filter has already determined serviceId return
		 * !ctx.containsKey(FORWARD_TO_KEY) && !ctx.containsKey(SERVICE_ID_KEY); }
		 * 
		 * @Override public Object run() { RequestContext ctx =
		 * RequestContext.getCurrentContext(); HttpServletRequest request =
		 * ctx.getRequest(); if (request.getParameter("foo") != null) { // put the
		 * serviceId in `RequestContext` RibbonFilterContextHolder.getCurrentContext()
		 * .add("lancher", "1"); } else { RibbonFilterContextHolder.getCurrentContext()
		 * .add("lancher", "2"); }
		 * 
		 * return null; } }
		 */