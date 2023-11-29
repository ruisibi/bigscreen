/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统信息
 * @author gdp
 *
 */
@Service
public class SystemService {

	@Value("${spring.datasource.dbType}")
	private String dbName;

	@Value("${spring.datasource.dwType}")
	private String dwType;

	@Value("${rsbi.name}")
	private String rsbiName;

	@Value("${rsbi.version}")
	private String rsbiVersion;

	@Value("${rsbi.versionNumber}")
	private String rsbiVersionNumber;

	@Value("${rsbi.lastupdate}")
	private String rsbiLastupdate;

	@Value("${rsbi.net}")
	private String rsbiNet;

	@Value("${rsbi.upFilePath}")
	private String upFilePath;


	@Autowired
	private RestTemplate restTemplate;

	public Map<String, Object> productInfo(){
		Map<String, Object> m = new HashMap<>();
		String name = RSBIUtils.getConstant("sys.productName");
		String log = RSBIUtils.getConstant("sys.login.log");
		String company = RSBIUtils.getConstant("sys.companyName");
		String active = RSBIUtils.getConstant("sys.show.active");
		String products = RSBIUtils.getConstant("sys.Login.products");
		m.put("name", name);
		m.put("log", log);
		m.put("company", company);
		m.put("active", active);
		m.put("products", products);
		return m;
	}

	public Map<String, Object> getSystemInfo(HttpServletRequest request){
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("dbName", dbName);
		ret.put("dwType", dwType);
		ret.put("rsbiName", rsbiName);
		ret.put("rsbiVersion", rsbiVersion);
		ret.put("rsbiVersionNumber", rsbiVersionNumber);
		ret.put("rsbiLastupdate", rsbiLastupdate);
		ret.put("rsbiNet", rsbiNet);
		ret.put("upFilePath", upFilePath);
		ret.put("jdk", System.getProperty("java.version"));
		Map<String, Object> cfg = RSBIUtils.getCfg(request.getServletContext());
		if(cfg != null) {
			ret.put("users", cfg.get("maxUserCnt"));
			ret.put("onlyOne", cfg.get("onlyOne"));
		}
		return ret;
	}

}
