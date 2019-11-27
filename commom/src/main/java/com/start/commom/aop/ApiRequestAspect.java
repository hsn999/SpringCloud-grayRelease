package com.start.commom.aop;


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
 
	//@Pointcut("@annotation(org.springframework.web.bind.annotation.ResponseBody)");
	//@Pointcut("@annotation(org.springframework.web.bind.annotation.RestController)");
	
	//@Pointcut("execution (* app.service.controller.AppServiceController.*(..)) ")
	//@Pointcut("@annotation(org.springframework.web.bind.annotation.RestController)" );
	
	
	//@Pointcut("@annotation(ssm.annotation.Log)")
	//@Pointcut("@annotation(org.springframework.web.bind.annotation.RestController)" )
	//@Pointcut("@annotation(org.springframework.web.bind.annotation.RestController)" )
	//@Pointcut("within(@org.springframework.stereotype.Controller *)")
	//@Pointcut("execution (* com.start.app.consumer.controller.ApiController.*(..)) ")
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
		String username =  request.getHeader("username");
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
	public void  doAfterReturning(){
		
	}
	
}