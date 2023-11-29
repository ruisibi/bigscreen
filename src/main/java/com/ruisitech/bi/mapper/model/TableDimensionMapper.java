/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.model;

import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.model.TableDimension;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableDimensionMapper {

	void insertDim(TableDimension dim);

	void updateDim(TableDimension dim);

	void deleteDim(TableDimension dim);

	void insertGroup(TableDimension dim);

	/**
	 * 根据 fromcol 更新 维度表的 valtype 字段
	 * @param dim
	 */
	void updateDimByFromcol(TableDimension dim);

	Integer getMaxDimId();

	void deleteGroupById( @Param("groupId") String groupId);

	void deleteGroupByTid( @Param("tid") Integer tid);

	List<String> listGroup( @Param("tid") Integer tid);

	/**
	 * 忽略父子维度

	 * @param tid
	 * @return
	 */
	List<DimDto> getTableDims( @Param("tid") Integer tid);

	/**
	 * 根据纬度ID,表ID查询维度信息

	 * @param dimId
	 * @param tid
	 * @return
	 */
	TableDimension queryDimCol( @Param("dimId") Integer dimId, @Param("tid") Integer tid);

	TableDimension findDimensionByAlias( @Param("alias") String alias, @Param("tid") Integer tid);
}
