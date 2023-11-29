/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.init.MvGetLoader;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.MVContext;

import javax.servlet.ServletContext;

/**
 * 从新从数据库获取报表配置对象，再从新编译
 * @author hq
 * @date 2015-5-14
 */
public class LoadXmlFromDB implements MvGetLoader {

	@Override
	public MVContext load(String mvId, ServletContext sctx) throws Exception {
		return ExtContext.getInstance().getMVContext(mvId);
	}

}
