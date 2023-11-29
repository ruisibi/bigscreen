/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.bireport;

import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OlapMapper {
	/**
	 * 查询缓存对象
	 * @param tid
	 * @return
	 */
	TableInfoVO getQueryTable(@Param("tid") Integer tid);

	/**
	 * 查询缓存对象的字段信息
	 * @return
	 */
	List<EtlTableMetaCol> getQueryTableCols(@Param("tid") Integer tid);

	/*
	查询立方体的所有字段
	 */
	List<Map<String, String>> getCubeCols(@Param("tid") Integer tid);

	/**
	 * 查询立方体的指标及指标聚合方式
	 * @param tid
	 * @return
	 */
	List<Map<String, String>> getKpisAlias(@Param("tid") Integer tid);

}
