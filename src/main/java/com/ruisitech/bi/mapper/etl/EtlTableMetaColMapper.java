/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.etl;

import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EtlTableMetaColMapper {

	/**
	 * 从数据库查询表的字段列表
	 * colId 为空，表示查询表的所有col
	 * @return
	 */
	List<EtlTableMetaCol> queryTableColumns(@Param("tableId") Integer tableId, @Param("colId") Integer colId);

	/**
	 * 从数据库查询表的字段列表(不包含表达式)
	 * @return
	 */
	List<EtlTableMetaCol> queryTableColumnsNotExpress(@Param("tableId") Integer tableId);

	/**
	 * 从数据库查询表的字段列表(只包含表达式)
	 * @return
	 */
	List<EtlTableMetaCol> queryTableColumnsOnlyExpress(@Param("tableId") Integer tableId);

	Integer maxColId(  );

	void insertMetaTableCol(EtlTableMetaCol meta);

	void delTableColByTableId(EtlTableMeta table);

	/**
	 * 删除表字段不删除动态字段
	 * @param table
	 */
	void delTableColByTableIdNotExpress(EtlTableMeta table);

	void updateTableCol(EtlTableMetaCol meta);

	void delTableColById(@Param("colId") Integer colId);

	/**
	 * 获取下拉框的值列表
	 * @param col
	 * @return
	 */
	EtlTableMetaCol getComboCol(EtlTableMetaCol col);

	/**
	 * 返回 DSColumn 类型的字段列表
	 * @param tableId

	 * @return
	 */
	List<DSColumn> queryColumnsRetDsColumn(@Param("tableId") Integer tableId);

	/**
	 * 更新模型的维度表达式
	 * @param col
	 * @return
	 */
	int updDimExpressByFromCol(EtlTableMetaCol col);

	/**
	 * 更新模型的度量的表达式
	 * @param col
	 * @return
	 */
	int updKpiExpressByFromCol(EtlTableMetaCol col);
}
