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

