/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.etl;

import com.ruisitech.bi.entity.etl.DataSource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataSourceMapper {

	List<DataSource> listDataSource(DataSource ds);

	void insertDataSource(DataSource ds);

	void updateDataSource(DataSource ds);

	void delDataSource(DataSource ds);

	Integer maxDataSourceid();

	Integer linkExist(DataSource ds);

	DataSource selectDataSourceByPrimaryKey(@Param("id") Integer id);

}
