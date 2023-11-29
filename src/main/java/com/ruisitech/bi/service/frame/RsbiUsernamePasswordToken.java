/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有 
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import org.apache.shiro.authc.UsernamePasswordToken;

import javax.servlet.http.HttpServletRequest;

public class RsbiUsernamePasswordToken extends UsernamePasswordToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3245428649330999429L;
	
	/**
	 * 是否验证密码，通过SSO的shiro登录不验证密码
	 */
	private Boolean chkpsd;

	private Boolean wxLogin; //是否微信登录

	private Boolean jwtLogin; //是否通过 jwt登录

	private HttpServletRequest request;
	
	public RsbiUsernamePasswordToken(final String username, final String password, final String host){
		super(username, password, host);
	}


	public Boolean getChkpsd() {
		return chkpsd;
	}


	public void setChkpsd(Boolean chkpsd) {
		this.chkpsd = chkpsd;
	}


	public Boolean getWxLogin() {
		return wxLogin;
	}


	public void setWxLogin(Boolean wxLogin) {
		this.wxLogin = wxLogin;
	}

	public Boolean getJwtLogin() {
		return jwtLogin;
	}

	public void setJwtLogin(Boolean jwtLogin) {
		this.jwtLogin = jwtLogin;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}
