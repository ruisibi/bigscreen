/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.auth;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.service.auth.AuthUserService;
import com.ruisitech.bi.service.frame.UserService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("authUser")
@RequestMapping(value = "/auth")
public class UserController extends BaseController {

	@Autowired
	private AuthUserService service;

	@Autowired
	private UserService userService;

	@RequestMapping(value="/userList/list.action", method = RequestMethod.POST)
	public @ResponseBody
    Object userList(@RequestBody PageParam page) {
		if(page != null && page.getPage() != null && page.getRows() != null){
			PageHelper.startPage(page.getPage(), page.getRows());
		}
		List<User> ls = service.listUsers(page.getSearch());
		PageInfo<User> pageInfo=new PageInfo<User>(ls);
		return super.buildSucces(pageInfo);
	}

	@RequestMapping(value="/userList/delete.action", method = RequestMethod.GET)
	public @ResponseBody
    Object userDelete(Integer userId) {
		service.deleteUser(userId);
		return super.buildSucces();
	}

	@RequestMapping(value="/userList/save.action", method = RequestMethod.POST)
	public @ResponseBody
    Object userSave(User u) {
		String msg = service.saveUser(u);
		if(msg == null) {
			return super.buildSucces();
		}else {
			return super.buildError(msg);
		}
	}

	@RequestMapping(value="/userList/update.action", method = RequestMethod.POST)
	public @ResponseBody
    Object userUpdate(User u) {
		service.updateUser(u);
		return super.buildSucces();
	}

	@RequestMapping(value="/userList/get.action")
	public @ResponseBody
    Object getUser(Integer userId) {
		User u = service.getUserById(userId);
		return super.buildSucces(u);
	}
}
