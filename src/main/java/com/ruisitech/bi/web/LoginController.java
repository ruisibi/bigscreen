/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web;

import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.service.frame.SystemService;
import com.ruisitech.bi.service.frame.UserService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.IsModel;
import com.ruisitech.bi.util.RSAUtils;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value = "/login")
public class LoginController extends BaseController {

	private static Logger log = LogManager.getLogger(LoginController.class);

	@Autowired
	private LoggerService loggerService;

	@Autowired
	private UserService userService;

	@Autowired
	private SystemService systemService;

	/**
	 * 登录
	 * @param userName
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/dologin.action", method = RequestMethod.POST)
	public @ResponseBody
    Object dologin(String userName, String password, Boolean rememberme , HttpServletRequest request)  {
		try {
			//对 用户名密码进行解密
			userName = RSAUtils.decryptBase64(userName);
			password = RSAUtils.decryptBase64(password);  //非对称解密
			String msg = userService.shiroLogin(userName, password, rememberme, request);
			if("SUC".equals(msg)){
				//更新登陆次数及时间
				Integer userId = RSBIUtils.getLoginUserInfo().getUserId();
				userService.updateLogDateAndCnt(userId);

				//写日志
				Map<String, Object> p = new HashMap<>();
				User u = RSBIUtils.getLoginUserInfo();
				p.put("userId", u.getUserId());
				p.put("userName", u.getLoginName());
				p.put("state", 1);
				p.put("income", IsModel.check(request)?"mobile":"pc");
				loggerService.insertLoginLog(p);
				//判断是否手机端
				Map<String, Object> m = new HashMap<>();
				m.put("mobile", IsModel.check(request));
				return super.buildSucces(m);
			}else{
				return super.buildError(msg);
			}
		}catch(Exception ex) {
			log.error("登录出错。", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/getKey.action")
	public @ResponseBody
    Object getKey() {
		try {
			String publicKey = RSAUtils.generateBase64PublicKey();
			return super.buildSucces(publicKey);
		}catch(Exception ex) {
			return super.buildError(ex.getMessage());
		}
	}

	/**
	 * 判断用户是否登录
	 * @return
	 */
	@RequestMapping(value="/isLogin.action")
	public @ResponseBody Object isLogin() {
		Subject us = SecurityUtils.getSubject();
		if(us.isAuthenticated() || us.isRemembered()){
			return super.buildSucces("y");
		}else{
			return super.buildSucces("n");
		}
	}

	/**
	 * 获取产品信息，比如log图片，系统名称什么的
	 * @return
	 */
	@RequestMapping(value="/productInfo.action")
	public @ResponseBody Object productInfo() {
		return super.buildSucces(systemService.productInfo());
	}
}
