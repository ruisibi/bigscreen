/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.model;

import com.ruisitech.bi.entity.model.TableMeasure;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableMeasureMapper {

	void insertKpi(TableMeasure kpi);

	void updateKpi(TableMeasure kpi);

	void deleteKpi(TableMeasure kpi);

	int getMaxKpiId();

	List<TableMeasure> getTableKpis(  @Param("tid") Integer tid);

	List<Map<String, Object>> listKpiDesc(  @Param("selectDsIds") String selectDsIds);

	void insertKpiGroup(TableMeasure kpi);

	List<String> listGroup(  @Param("tid") Integer tid);

	void deleteKpiGroup( @Param("kpiGroup") String kpiGroup);

	void deleteKpiGroupByTid( @Param("tid") Integer tid);
}
