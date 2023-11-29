/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.ExtConstants;
import com.ruisitech.bi.entity.common.RequestStatus;
import com.ruisitech.bi.entity.common.Result;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

/**
 * 网页使用的 shiro 拦截器
 * 由于shiro的filter会被spirngboot识别，所以不能使用spring注入，只能通过构造函数传参
 * @author hq
 *
 */
public class SessionAuthcFilter extends AdviceFilter {

	private UserService userService;



	public SessionAuthcFilter(UserService userService){
		this.userService = userService;
	}

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response)
			throws Exception {
		Subject us = SecurityUtils.getSubject();
		Session session = us.getSession();

		if(!us.isAuthenticated() && us.isRemembered() && session.getAttribute(ShiroDbRealm.SESSION_USER_KEY) == null){
			//说明是记住我过来的,恢复SESSION里的值
			Object staffId = us.getPrincipal();
			if(staffId != null){
				User u = userService.getUserByUserId((String)staffId);
				if(u == null) {
					us.logout();
					createNoLogin(response);
					return false;
				}
				u.setLoginDate(new Date());
				HttpServletRequest r =  (HttpServletRequest)request;
				String ip = r.getHeader("X-Real-IP");
				if(ip == null || ip.length() == 0) {
					ip = r.getRemoteAddr();
				}
				u.setLogIp(ip);
				session.setAttribute(ShiroDbRealm.SESSION_USER_KEY, u);
			}else{
				createNoLogin(response);
				return false;
			}
		}
		if(us.isAuthenticated() || us.isRemembered()){  //不管是认证登陆 还是 记住我登陆， 都放行

			return true;
		}else{
			createNoLogin(response);
			return false;
		}

	}

	private void createNoLogin(ServletResponse response) throws IOException {
		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("utf-8");
		Result r = new Result(RequestStatus.NOLOGIN.getStatus(), "登录信息已经超时，请重新登录！", null);
		response.getWriter().print(JSONObject.toJSONString(r));
	}
}
