/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.model;

/**
 * 度量表，对应  olap_kpi_list 表
 * @author hq
 *
 */
public class TableMeasure extends TableMetaCol {

	private Integer kpiId;
	private String name;
	private String kpinote;
	private String unit;
	private String fmt;
	private String aggre;

	/**新增度量那创建的计算指标
	 * 0否，1是
	 */
	private Integer calcKpi;
	private Integer tid;
	//度量分类
	private String kpiGroup;
	private String kpiGroupName;

	private String distinctCol;  //聚合distinctCol字段

	public Integer getKpiId() {
		return kpiId;
	}
	public void setKpiId(Integer kpiId) {
		this.kpiId = kpiId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKpinote() {
		return kpinote;
	}
	public void setKpinote(String kpinote) {
		this.kpinote = kpinote;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getFmt() {
		return fmt;
	}
	public void setFmt(String fmt) {
		this.fmt = fmt;
	}
	public String getAggre() {
		return aggre;
	}
	//获取聚合字段字符串
	public String getAggreCol() {
		if("count(distinct)".equals(aggre)){  //需要特殊处理
			return "count(distinct " + super.getCol() + ")";
		}else{  //sum/avg/max/min/count 不需要特殊处理
			return aggre +"(" + super.getCol() +")";
		}
	}
	public void setAggre(String aggre) {
		this.aggre = aggre;
	}
	public Integer getCalcKpi() {
		return calcKpi;
	}
	public void setCalcKpi(Integer calcKpi) {
		this.calcKpi = calcKpi;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getKpiGroup() {
		return kpiGroup;
	}
	public void setKpiGroup(String kpiGroup) {
		this.kpiGroup = kpiGroup;
	}
	public String getKpiGroupName() {
		return kpiGroupName;
	}
	public void setKpiGroupName(String kpiGroupName) {
		this.kpiGroupName = kpiGroupName;
	}

	public String getDistinctCol() {
		return distinctCol;
	}

	public void setDistinctCol(String distinctCol) {
		this.distinctCol = distinctCol;
	}
}
