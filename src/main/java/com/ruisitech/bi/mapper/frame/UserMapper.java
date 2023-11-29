/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.frame;

import com.ruisitech.bi.entity.frame.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {

	User getUserByStaffId(@Param("staffId") String staffId);

	User getUserById(@Param("userId") Integer userId);

	User getUserByWxid(@Param("wxid") String wxid);

	void updateuser(User user);

	void insertuser(User user);

	List<Map<String, Object>> listUserMenus(@Param("userId") Integer userId);

	void updateLogDateAndCnt(User user);

	String checkPsd(@Param("userId") Integer userId);

	void modPsd(User user);

	Map<String, Object> appUserinfo(@Param("userId") Integer userId);

	int updateChannel(User user);

	int userExist( @Param("staffId") String staffId);

	int maxUserId( );

	List<User> listUsers(@Param("keyword") String keyword);

	void updateWxidByStaffid(@Param("staffId") String staffId, @Param("wxid") String wxid);

	List<User> selectLockUsers();

	/**
	 * 查询不在角色的用户列表
	 * @param roleId
	 * @return
	 */
	List<User> listRoleNotUser( @Param("roleId") Integer roleId);
}
