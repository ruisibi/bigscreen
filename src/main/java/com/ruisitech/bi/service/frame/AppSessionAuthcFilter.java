/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有 
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.ruisitech.bi.entity.frame.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * App 使用的shiro 拦截器
 * @author hq
 *
 */
public class AppSessionAuthcFilter extends AdviceFilter {
	
	@Autowired
	private UserService userService;

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response)
			throws Exception {
		Subject us = SecurityUtils.getSubject();
		Session session = us.getSession();
		
		if(!us.isAuthenticated() && us.isRemembered() && session.getAttribute(ShiroDbRealm.SESSION_USER_KEY) == null){
			//说明是记住我过来的,恢复SESSION里的值
			Object staffId = us.getPrincipal();
			if(staffId != null){
				User u = userService.getUserByStaffId(staffId.toString());
				session.setAttribute(ShiroDbRealm.SESSION_USER_KEY, u);
			}else{
				request.getRequestDispatcher("/pages/control/NoLogin_app.jsp").forward(request, response);
				return false;
			}
		}
		if(us.isAuthenticated() || us.isRemembered()){  //不管是认证登陆 还是 记住我登陆， 都放行
			return true;
		}else{
			request.getRequestDispatcher("/pages/control/NoLogin_app.jsp").forward(request, response);
			return false;
		}
	}

}
