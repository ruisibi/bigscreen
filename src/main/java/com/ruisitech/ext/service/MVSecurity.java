/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.view.builder.BuilderInterceptor;
import com.rsbi.ext.engine.view.context.Element;
import com.rsbi.ext.engine.wrapper.ExtRequest;

/**
 * mv的权限验证，用来对试用用户的页面进行拦截
 * @author hq
 * @date 2013-8-7
 */
public class MVSecurity implements BuilderInterceptor {

	/**
	 * 写日志
	 */
	@Override
	public void end(Element mv, ExtRequest req, DaoHelper dao) {

	}

	@Override
	public void start(Element mv, ExtRequest req, DaoHelper dao) {

	}

	/**
	public static boolean canViewMV(User user, String mvid){
		if(user == null){
			return true;  //在app端， 多维分析的 user 就是 == null
		}
		Map urls = user.getMvs();
		if(urls == null){
			return true; //如果未配置用户访问的mv,直接通过
		}
		//如果用户访问的mv不属于 整体mv, 说明这个mv未控制， 直接通过。
		//如果访问的mv属于控制的mv并且不存在用户的mv列表中，直接拒绝访问
		if(!VDOPUtils.getAllmvs().containsKey(mvid)){
			return true;
		}else{
			if(urls.containsKey(mvid)){
				return true;
			}else{
				return false;
			}
		}

	}

**/

}
