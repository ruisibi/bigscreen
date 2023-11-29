/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.model;

import com.ruisitech.bi.entity.model.TableMeta;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableMetaMapper {

	/**
	 * 无权限控制立方体列表
	 * @param table
	 * @return
	 */
	List<TableMeta> listSubject(TableMeta table);

	Integer tableExist(  @Param("tid") Integer tid);

	int insertTable(TableMeta table);

	int updateTable(TableMeta table);

	int updateTableName(TableMeta table);

	int deleteTable(   @Param("tid") Integer tid );

	TableMeta getTable( @Param("tid") Integer tid);

	Integer getDefTid();

	/**
	 * 根据id 列表获取 立方体
	 * @param selectDsIds
	 * @return
	 */
	List<Map<String, Object>> listDs( @Param("selectDsIds") String selectDsIds);

	/**
	 * 获取立方体的元数据，用来构建tree

	 * @param tid
	 * @return
	 */
	List<Map<String, Object>> listDsMeta(  @Param("tid") Integer tid);

	/**
	 * app 中使用的 主题列表

	 * @return
	 */
	List<Map<String, Object>> applistSubject();
}
