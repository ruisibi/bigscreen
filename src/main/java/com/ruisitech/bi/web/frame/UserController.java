/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.frame;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.service.frame.UserService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/frame")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private DaoHelper daoHelper;

	@RequestMapping(value="/OnlineUser/list.action")
	public @ResponseBody
    Object listOnlineUser(){
		return super.buildSucces(userService.listOnlineUsers());
	}

	/**
	 * 强制下线
	 * @return
	 */
	@RequestMapping(value="/OnlineUser/out.action")
	public @ResponseBody
    Object outUser(String staffId){
		userService.outUser(staffId);
		return super.buildSucces();
	}

	@RequestMapping(value="/User.action")
	public @ResponseBody
    Object getUserInfo(){
		String staffId = RSBIUtils.getLoginUserInfo().getStaffId();
		User u = userService.getUserByUserId(staffId);
		return super.buildSucces(u);
	}

	@RequestMapping(value="/chgPsd.action", method = RequestMethod.POST)
	public @ResponseBody
    Object chgPsd(String password1, String password2, String password3){
		Integer userId = RSBIUtils.getLoginUserInfo().getUserId();
		String userPassword = userService.checkPsd(userId);
		if(!userPassword.equals(RSBIUtils.getEncodedStr(password1)))
		{
			return this.buildError("原始密码错误");
		}
		else
		{
			User u = new User();
			u.setUserId(userId);
			u.setPassword(RSBIUtils.getEncodedStr(password2));
			userService.modPsd(u);
			return this.buildSucces();
		}
	}
}
