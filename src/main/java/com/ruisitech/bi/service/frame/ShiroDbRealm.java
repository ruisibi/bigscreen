/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ShiroDbRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	public static final String SESSION_USER_KEY = "session.user.key";

	@Resource
	private RedisTemplate<String, Object> redisTemplate;


	@Autowired
	private SessionDao sessionDao;

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用,负责在应用程序中决定用户的访问控制的方法
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		//User user = (User) SecurityUtils.getSubject().getSession().getAttribute(ShiroDbRealm.SESSION_USER_KEY);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		//info.addRole(user.getRole().trim());
		return info;
	}

	/**
	 * 认证回调函数，登录信息和用户验证信息验证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		// 把token转换成User对象
		RsbiUsernamePasswordToken tk = (RsbiUsernamePasswordToken) authcToken;
		User userLogin = tokenToUser(tk);
		User ui = null;

		/**
		 * 10分钟内，只能登录5次。
		 */
		String key = "login-"+userLogin.getStaffId();
		long cnt = redisTemplate.opsForValue().increment(key, 1);
		if(cnt == 1L){
			redisTemplate.expire(key, 10, TimeUnit.MINUTES);
		}
		if(cnt > 5){
			throw new AuthenticationException("密码错误次数超过5次，账号被锁定10分钟!"); //账号已经被锁定
		}
		ui = userService.getUserByStaffId(userLogin.getStaffId());
		if (ui == null){
			throw new UnknownAccountException(); // 异常处理，找不到数据
		}
		if(ui.getPassword() == null) {
			throw new AuthenticationException("账号未获取到密码信息，不能进行登录验证!"); //账号已经停用
		}
		if(!ui.getPassword().equals(RSBIUtils.getMD5(userLogin.getPassword().getBytes()))){
			throw new IncorrectCredentialsException(); //密码错误
		}
		if(ui.getState() == 0) {
			throw new AuthenticationException("账号已经被停用!"); //账号已经停用
		}
		// 设置session
		Session session = SecurityUtils.getSubject().getSession();
		ui.setLoginDate(new Date());
		//获取IP
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0) {
			ip = request.getRemoteAddr();
		}
		ui.setLogIp(ip);

		session.setAttribute(ShiroDbRealm.SESSION_USER_KEY, ui);
		//移除redis缓存
		redisTemplate.delete("login-"+userLogin.getStaffId());
		// 当前 Realm 的 name
		String realmName = this.getName();
		// 登陆的主要信息: 可以是一个实体类的对象, 但该实体类的对象一定是根据 token 的 username 查询得到的.
		Object principal = authcToken.getPrincipal();
		return new SimpleAuthenticationInfo(principal, userLogin.getPassword(), realmName);
	}


	private User tokenToUser(UsernamePasswordToken authcToken) {
		User user = new User();
		user.setStaffId(authcToken.getUsername());
		user.setPassword(authcToken.getPassword() == null?null:String.valueOf(authcToken.getPassword()));
		return user;
	}

}
