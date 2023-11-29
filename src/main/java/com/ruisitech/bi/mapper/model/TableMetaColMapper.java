/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.model;

import com.ruisitech.bi.entity.model.TableMetaCol;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableMetaColMapper {

	void insertMeta(TableMetaCol meta);

	Integer getMaxRid();

	void deleteKpiMeta( @Param("tid") Integer tid);

	void deleteDimMeta( @Param("tid") Integer tid);

	void deleteByTid( @Param("tid") Integer tid);

	/**
	 * 获取立方体的字段列表

	 * @param tid
	 * @return
	 */
	List<Map<String, Object>> queryMetaCols( @Param("tid") Integer tid);
}
