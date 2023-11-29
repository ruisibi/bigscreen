/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * druid 配置多数据源配置
 * 数据仓库数据源
 *
 */
@Configuration
public class DruidDataWarehouseConfig
{
    @Bean
    @ConfigurationProperties("spring.datasource.druid.datawarehouse")
    public DataSource dataWareHouseDataSource()
    {
        DruidDataSource datasource = DruidDataSourceBuilder.create().build();
        return datasource;
    }

}
