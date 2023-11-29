/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.control.sys.LoginSecurityAdapter;
import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.wrapper.ExtRequest;
import com.rsbi.ext.engine.wrapper.ExtResponse;

import javax.servlet.ServletContext;

public class ExtLoginChecker implements LoginSecurityAdapter {

	public boolean loginChk(ExtRequest req, ExtResponse arg1, ServletContext ctx, DaoHelper arg2) {
		/**
		User user = (User)req.getSession().getAttribute(VdopConstant.USER_KEY_IN_SESSION);
		if(user == null){
			return false;
		}else{
			return true;
		}
		**/
		return true;
	}

}
