/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.etl;

import com.ruisitech.bi.entity.etl.EtlTableMeta;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EtlTableMetaMapper {

	List<EtlTableMeta> selectTables(@Param("income") String income, @Param("tableLevel") String tableLevel, @Param("crtUser") Integer crtUser, @Param("keyword") String keyword, @Param("srcTable") String srcTable);

	List<EtlTableMeta> selectTablesNotEsAndRest( @Param("crtUser") Integer crtUser);

	List<EtlTableMeta> selectTablesNotRest(@Param("keyword") String keyword, @Param("income") String income,  @Param("crtUser") Integer crtUser);

	List<EtlTableMeta> selectByIncomes(@Param("incomes") List<String> incomes, @Param("keyword") String keyword, @Param("crtUser") Integer crtUser);

	Integer tableExist(EtlTableMeta table);

	Integer maxTableId(  );

	void insertMetaTable(EtlTableMeta table);

	void updateMetaTable(EtlTableMeta table);

	EtlTableMeta getTable( @Param("tableId") Integer tableId );

	EtlTableMeta getTableByTname( @Param("tableName") String tableName );

	void deleteTable(EtlTableMeta table);

	/**
	 * 执行了es同步后，需要把表设置为已同步, 并且更新执行时间
	 * @param table
	 * @return
	 */
	int updateEsEnable(EtlTableMeta table);

	/**
	 * 设置表和es同步
	 * @param table
	 * @return
	 */
	int updateUseEs(EtlTableMeta table);

	List<EtlTableMeta> dwselectTables();

	/**
	 * 根据表类型计算表的数量
	 * @param type

	 * @return
	 */
	Integer cntTables(@Param("type") String type, @Param("crtUser") Integer crtUser);


	int clearExeDate(@Param("tableId") Integer tableId);

	List<Map<String, Object>> queryTfByTableId(@Param("tableId") Integer tableId);
}
