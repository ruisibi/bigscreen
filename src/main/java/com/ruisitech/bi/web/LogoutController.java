/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web;

import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/frame")
public class LogoutController extends BaseController {

	@RequestMapping(value="/Logout2.action")
	public @ResponseBody Object logout(){
		User u = RSBIUtils.getLoginUserInfo();
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
	        try{
	            subject.logout();
	        }catch(Exception ex){
	        	ex.printStackTrace();
	        }
	    }
		return super.buildSucces();
	}

	/**
	 * 如果使用单点登录，在主系统注销登录后，需要调用BI系统的注销登录接口，此接口在页面采用Ajax调用
	 * @return
	 */
	@RequestMapping(value="/LogoutBySSO.action")
	public @ResponseBody
    Object logoutBySSO(){
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
	        try{
	            subject.logout();
	        }catch(Exception ex){

	        }
	    }
		return super.buildSucces();
	}
}
