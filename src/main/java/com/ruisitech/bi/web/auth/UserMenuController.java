/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.auth;

import com.alibaba.fastjson.JSON;
import com.ruisitech.bi.service.auth.AuthUserService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/auth")
public class UserMenuController extends BaseController {

	@Autowired
	private AuthUserService service;

	@RequestMapping(value="/userMenu/save.action", method = RequestMethod.POST)
	public @ResponseBody
    Object saveUserMenu(Integer userId, String menuIds) {
		service.saveUserMenu(userId, menuIds);
		return super.buildSucces();
	}

	@RequestMapping(value="/user/userMenu.action")
	public @ResponseBody Object  userMenu(Integer userId) {
		Map<String, Object> dts = service.listUserMenus(userId);
		return super.buildSucces(dts);
	}
}
