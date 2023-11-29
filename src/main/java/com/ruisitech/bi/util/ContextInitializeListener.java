/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import com.rsbi.ext.engine.ExtConstants;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.ruisitech.bi.service.etl.ClearMvJob;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * 在项目启动或结束是做一些初始化或清理工作
 * @author hq
 * @date 2015-4-5
 */
public class ContextInitializeListener implements ServletContextListener {

	@Value("${spring.datasource.dwType}")
	private String dwName;

	@Value("${spring.datasource.dbType}")
	private String dbName;

	@Value("${rsbi.upFilePath}")
	private String upFilePath;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		//ServletContext sctx = sce.getServletContext();
		this.destoryDrivers();
	}

	private void destoryDrivers() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while(drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//把变量放入 ExtContext
		ExtContext.getInstance().putConstants("dbName", dbName);
		ExtContext.getInstance().putConstants("upFilePath", upFilePath);
		ExtContext.getInstance().putConstants(ExtConstants.dwName, dwName);
		RSBIUtils.setServletContext(sce.getServletContext()); //放入RSBIUtils对象
		//fastjson_safemode 漏洞
		//ParserConfig.getGlobalInstance().setSafeMode(true);

		ClearMvJob job = new ClearMvJob(sce.getServletContext());
		job.start();
	}
}
