/*
 * package com.start.zuul.config;
 * 
 * import java.util.LinkedHashMap; import java.util.Map;
 * 
 * import
 * org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
 * import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
 * import org.springframework.cloud.netflix.zuul.filters.ZuulProperties; import
 * org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
 * import org.springframework.util.StringUtils;
 * 
 * public class CustomRouteLocator extends SimpleRouteLocator implements
 * RefreshableRouteLocator {
 * 
 * public CustomRouteLocator(String servletPath, ZuulProperties properties) {
 * super(servletPath, properties); // TODO Auto-generated constructor stub }
 * 
 * @Override public void refresh() { // TODO Auto-generated method stub
 * 
 * }
 * 
 * @Override protected Map<String, ZuulRoute> locateRoutes() {
 * LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String,
 * ZuulRoute>(); // 从application.properties中加载路由信息
 * routesMap.putAll(super.locateRoutes()); // 从db中加载路由信息 //
 * routesMap.putAll(locateRoutesFromDB()); // 优化一下配置 LinkedHashMap<String,
 * ZuulRoute> values = new LinkedHashMap<>(); for (Map.Entry<String, ZuulRoute>
 * entry : routesMap.entrySet()) { String path = entry.getKey(); // Prepend with
 * slash if not already present. if (!path.startsWith("/")) { path = "/" + path;
 * } if (StringUtils.hasText("version")) { path = "" + path; if
 * (!path.startsWith("/")) { path = "/" + path; } } values.put(path,
 * entry.getValue()); } return values; }
 * 
 * }
 */