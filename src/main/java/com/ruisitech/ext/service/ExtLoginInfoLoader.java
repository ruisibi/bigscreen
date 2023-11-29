/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.service.loginuser.LoginUserInfoLoader;
import com.rsbi.ext.engine.wrapper.ExtRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * ext 获取登录信息的方法
 * @author hq
 * @date Mar 25, 2010
 */
public class ExtLoginInfoLoader  implements LoginUserInfoLoader {

	@Override
	public String getUserId() {
		return null;
	}

	@Override
	public Map<String, Object> loadUserInfo(ExtRequest request, DaoHelper dao) {
		/**
		User user = (User)request.getSession().getAttribute(VdopConstant.USER_KEY_IN_SESSION);
		Map<String, Object> m = new HashMap();
		if(user == null){
			return m;
		}
		m.put("userId", user.getUserId());
		m.put("staffId", user.getStaffId());
		m.put("state", user.getState());

		return m;
		**/
		return new HashMap<String, Object>();
	}

}
