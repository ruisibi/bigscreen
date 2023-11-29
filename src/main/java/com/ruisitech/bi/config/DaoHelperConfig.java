/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.config;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.dao.DaoHelperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 配置 daoHelper， 分数据仓库使用的 daoHelper 和 支撑库使用的 sysDaoHelper
 * @Author huangqin
 * @Date 2020/10/19 5:17 下午
 */
@Configuration
public class DaoHelperConfig {

    @Resource(name = "dataWareHouseDataSource")
    private DataSource dataWareHouseDataSource;

    @Resource(name = "masterDataSource")
    private DataSource masterDataSource;

    /**
     * 支的数据仓库的daoHelper
     * @return
     */
    @Bean
    public DaoHelper daoHelper(){
        DaoHelperImpl dao = new DaoHelperImpl();
        dao.setJdbcTemplate(new JdbcTemplate(dataWareHouseDataSource));
        return dao;
    }

    /**
     * 支撑库使用的sysDaoHelper
     * @return
     */
    @Bean
    public DaoHelper sysDaoHelper(){
        DaoHelperImpl dao = new DaoHelperImpl();
        dao.setJdbcTemplate(new JdbcTemplate(masterDataSource));
        return dao;
    }
}
