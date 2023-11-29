/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.frame;

import com.ruisitech.bi.entity.frame.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoleMapper {

	List<Map<String, Object>> listRoleMenus(@Param("roleId") Integer roleId);

	/**
	 * 给角色授权表的权限
	 * @param roleId

	 * @return
	 */
	List<Map<String, Object>> roledata(@Param("roleId") Integer roleId);

	List<Role> list( @Param("keyword") String keyword);

	List<Role> listUserRole( @Param("userId") Integer userId);

	Role getById( @Param("roleId") Integer roleId);

	List<Role> listUserHasRole( @Param("userId") Integer userId);

	List<Map<String, Object>> listRoleUser( @Param("roleId") Integer roleId);

}
