/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.model;

import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.List;
import java.util.Map;

/**
 * 数据建模主表 , 对应olap_table_meta 表
 * @author hq
 *
 */
public class TableMeta extends BaseEntity  {

	private Integer tid;
	private String tName;
	private String tDesc;
	private String tNote;
	private Integer typeId;
	private String typeName;
	private String tableType;   // horizontal 横表， vertical 纵表
	private String kpiCodeColumn;  // 指标编码字段
	private String kpiNameColumn;  //指标名称字段
	private String kpiValueColumn;  //指标值字段

	private List<TableDimension> dims;
	private List<TableMeasure> kpis;

	private List<DimDto> dimDtos;

	private String key; //查询主体关键字
	private List<Map<String, Object>> delObj; //需要被删除的对象

	private Integer userId;

	private String income; //来源
	private String restUrl; //如果是Rest接口，列出 restUrl

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public String gettName() {
		return tName;
	}

	public void settName(String tName) {
		this.tName = tName;
	}

	public String gettDesc() {
		return tDesc;
	}

	public void settDesc(String tDesc) {
		this.tDesc = tDesc;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public List<TableDimension> getDims() {
		return dims;
	}

	public void setDims(List<TableDimension> dims) {
		this.dims = dims;
	}

	public List<TableMeasure> getKpis() {
		return kpis;
	}

	public void setKpis(List<TableMeasure> kpis) {
		this.kpis = kpis;
	}

	public String gettNote() {
		return tNote;
	}

	public void settNote(String tNote) {
		this.tNote = tNote;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<Map<String, Object>> getDelObj() {
		return delObj;
	}

	public void setDelObj(List<Map<String, Object>> delObj) {
		this.delObj = delObj;
	}

	@Override
	public void validate() {
		this.tName = RSBIUtils.htmlEscape(this.tName);
		this.tDesc = RSBIUtils.htmlEscape(this.tDesc);
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getKpiCodeColumn() {
		return kpiCodeColumn;
	}

	public void setKpiCodeColumn(String kpiCodeColumn) {
		this.kpiCodeColumn = kpiCodeColumn;
	}

	public String getKpiNameColumn() {
		return kpiNameColumn;
	}

	public void setKpiNameColumn(String kpiNameColumn) {
		this.kpiNameColumn = kpiNameColumn;
	}

	public String getKpiValueColumn() {
		return kpiValueColumn;
	}

	public void setKpiValueColumn(String kpiValueColumn) {
		this.kpiValueColumn = kpiValueColumn;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getRestUrl() {
		return restUrl;
	}

	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}

	public List<DimDto> getDimDtos() {
		return dimDtos;
	}

	public void setDimDtos(List<DimDto> dimDtos) {
		this.dimDtos = dimDtos;
	}
}
