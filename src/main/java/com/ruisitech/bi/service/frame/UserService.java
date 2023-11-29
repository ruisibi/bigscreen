/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.mapper.frame.UserMapper;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
public class UserService {

	@Resource
	private UserMapper mapper;

	@Resource
	private RestTemplate restTemplate;

	@Autowired
	private SessionDao sessionDao;

	@Autowired
	private MenuService menuService;

	private static Logger log = LogManager.getLogger(UserService.class);

	public User getUserByStaffId(String staffId){
		User u = mapper.getUserByStaffId(staffId);
		if(u != null){
			u.setUrls(menuService.listUserVisitUrl(u.getUserId()));
		}
		return u;
	}


	public List<User> listOnlineUsers(){
		List<User> ret = new ArrayList<User>();
		Collection<Session> sessions = sessionDao.getActiveSessions();
		for(Session s : sessions) {
			User u = (User)s.getAttribute(ShiroDbRealm.SESSION_USER_KEY);
			if(u != null) {
				ret.add(u);
			}
		}
		return ret;
	}

	/**
	 * 用户强制下线
	 * @param staffId
	 */
	public void outUser(String staffId) {
		Collection<Session> sessions = sessionDao.getActiveSessions();
		List<String> rmk = new ArrayList<String>();
		for(Session s : sessions) {
			User u = (User)s.getAttribute(ShiroDbRealm.SESSION_USER_KEY);
			if(u != null && u.getStaffId().equals(staffId)) {
				//不能移除自己
				if(u.getStaffId().equals(SecurityUtils.getSubject().getPrincipal().toString())) {
					continue;
				}
				sessionDao.delSession(s);  //从 redis 移除
				rmk.add(s.getId().toString());
				//ShiroSessionCacheService.getCache().remove(s.getId());  //从缓存移除
			}
		}
	}



	public User getUserByUserId(String staffId){
		return mapper.getUserByStaffId(staffId);
	}
	public void updateLogDateAndCnt(Integer userId){
		User u = new User();
		u.setUserId(userId);
		mapper.updateLogDateAndCnt(u);
	}

	public void modPsd(User u){
		mapper.modPsd(u);
	}

	public String checkPsd(Integer userId){
		return mapper.checkPsd(userId);
	}

	public String shiroLogin(String userName, String password, Boolean rememberme, HttpServletRequest request){
		RsbiUsernamePasswordToken token = new RsbiUsernamePasswordToken(userName, password, null);
	    //token.setRememberMe(rememberme);
		token.setRememberMe(true);
		token.setRequest(request);
	    // shiro登陆验证
	    try {
	        SecurityUtils.getSubject().login(token);
	    } catch (UnknownAccountException ex) {
	        return "账号不存在！";
	    } catch (IncorrectCredentialsException ex) {
	        return "密码错误！";
	    } catch (AuthenticationException ex) {
	    	String ret = null;
	    	Throwable t = ex;
	    	while(true){
	    		if(t.getCause() == null) {
	    			ret = t.getMessage();
	    			break;
	    		}
	    		t = t.getCause();
	    		if(t.getCause() == null){
	    			ret = t.getMessage();
	    			break;
	    		}
	    	}
	    	return ret;
	    } catch (Exception ex) {
	    	log.error("登录出错。", ex);
	        return "内部错误，请重试！";
	    }
	    return "SUC";
	}

	/**
	 * 查询不在角色下的用户列表
	 * @param roleId
	 * @return
	 */
	public List<User> listRoleNotUser(Integer roleId){
		return mapper.listRoleNotUser(roleId);
	}

}
