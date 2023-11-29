/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.rsbi.ext.engine.init.XmlParser;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.MVContext;
import com.ruisitech.bi.util.RSBIUtils;

import javax.servlet.ServletContext;
import java.util.*;

/**
 * 更新mv 缓存
 * @author gdp
 *
 */
public class ClearMvJob extends Thread {

	private ServletContext ctx;

	public ClearMvJob(ServletContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void run() {
		while (true) {
			this.execute();
			try {
				Thread.sleep(1000 * 60 * 5);  //间隔5分钟执行一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void execute() {
		//移除超过1小时得mv缓存
		long now = new Date().getTime();
		List<String> removeIds = new ArrayList<>();
		Map<String, MVContext> mvs = ExtContext.getInstance().getAllMV();
		for(Map.Entry<String, MVContext> mv : mvs.entrySet()) {
			Date crtdate = mv.getValue().getCreateDate();
			if(crtdate != null && now - crtdate.getTime() >= 1000 * 60  * 60 ) { //报表缓存1小时
				removeIds.add(mv.getKey());
			}
		}
		for(String mvId : removeIds) {
			ExtContext.getInstance().removeMV(mvId);
		}
	}
}
