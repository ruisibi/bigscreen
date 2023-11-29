/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.dao.DaoHelperImpl;
import com.rsbi.ext.engine.dao.DaoProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

/**
 * springboot 提供 ext3 框架使用的 daoHelper 类
 * @ClassName SpringBootDaoProvider
 * @Description SpringBootDaoProvider
 * @Author huangqin
 * @Date 2020/12/31 4:18 下午
 */
public class SpringBootDaoProvider implements DaoProvider {

    @Override
    public DaoHelper getDaoHelper(ServletContext sctx) {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
        //DaoHelperImpl dao = new DaoHelperImpl();
        //DataSource ds = (DataSource)ctx.getBean("dataWareHouseDataSource");
        //dao.setJdbcTemplate(new JdbcTemplate(ds));
        return (DaoHelper) ctx.getBean("daoHelper");
    }
}
