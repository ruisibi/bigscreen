/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.auth;

import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.service.auth.AuthRoleService;
import com.ruisitech.bi.service.frame.UserService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色授权
 * @author gdp
 *
 */
@Controller
@RequestMapping(value = "/auth")
public class RolePermissionController extends BaseController {

	@Autowired
	private AuthRoleService service;

	@Autowired
	private UserService userService;

	@RequestMapping(value="/role/roleMenu.action")
	public @ResponseBody Object roleMenu(Integer roleId) {
		Map<String, Object> menus = service.listRoleMenus(roleId);
		return super.buildSucces(menus);
	}

	@RequestMapping(value="/role/menuSave.action", method = RequestMethod.POST)
	public @ResponseBody
    Object roleMenuSave(String menuIds, Integer roleId) {
		service.roleMenu(menuIds, roleId);
		return super.buildSucces();
	}

	@RequestMapping(value="/role/roleData.action")
	public @ResponseBody Object roleData(Integer roleId) {
		return super.buildSucces(service.roledata(roleId));
	}

	@RequestMapping(value="/role/dataSave.action", method = RequestMethod.POST)
	public @ResponseBody
    Object roleDataSave(Integer roleId, String dataIds) {
		service.roleDataSave(roleId, dataIds);
		return super.buildSucces();
	}

	@RequestMapping(value="/role/userSave.action", method = RequestMethod.POST)
	public @ResponseBody
    Object roleUserSave(@RequestBody Map<String, Object> pms) {
		service.roleUserSave(pms);
		return super.buildSucces();
	}

	@RequestMapping(value="/role/roleReport/save.action", method = RequestMethod.POST)
	public @ResponseBody
    Object roleReportSave(@RequestBody Map<String, Object> pms) {
		service.roleReportSave(pms);
		return super.buildSucces();
	}

	@RequestMapping(value="/role/dashboard/save.action", method = RequestMethod.POST)
	public @ResponseBody
    Object roleDashboardSave(@RequestBody Map<String, Object> pms) {
		service.roleDashboardSave(pms);
		return super.buildSucces();
	}

	/**
	 * 查询不在角色列表的用户
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/role/notInRole/list.action", method = RequestMethod.POST)
	public @ResponseBody
    Object notInRoleUsers(Integer roleId, String search) {
		List<User> ls = userService.listRoleNotUser(roleId);
		return ls;
	}

	@RequestMapping(value="/role/user/list.action", method = RequestMethod.GET)
	public @ResponseBody
    Object userList(Integer roleId) {
		return service.listRoleUsers(roleId);
	}
}
