/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.List;
import java.util.Map;

public class PortalParamDto extends BaseEntity {

	private String paramid; //在报表中，参数叫 paramid
	private String defvalue;
	private String type;
	private String dtformat; //在报表中，参数叫 dtformat
	private String format;  //在仪表盘中，参数叫 format
	private String name;
	private String valtype;
	private String hiddenprm;
	private String maxval;
	private String minval;
	private String id; //在仪表盘中，参数叫 paramid
	private Map<String, Object> option;
	private List<Map<String, Object>> values;
	private String cascade;  //级联参数
	private String defFirstValue; //下拉框把第一行做默认数据
	private String tableIdCol;
	private String tableNameCol;
	private String tablePidCol;
	private String matchTable;
	private String matchTableName;

	//仪表盘页面参数值是使用的 selval, selval2 表示
	private Map<String, String> selval;
	private Map<String, String> selval2;

	public String getParamid() {
		return paramid;
	}
	public void setParamid(String paramid) {
		this.paramid = paramid;
	}
	public String getDefvalue() {
		return defvalue;
	}
	public void setDefvalue(String defvalue) {
		this.defvalue = defvalue;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDtformat() {
		return dtformat;
	}
	public void setDtformat(String dtformat) {
		this.dtformat = dtformat;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValtype() {
		return valtype;
	}
	public void setValtype(String valtype) {
		this.valtype = valtype;
	}
	public String getHiddenprm() {
		return hiddenprm;
	}
	public void setHiddenprm(String hiddenprm) {
		this.hiddenprm = hiddenprm;
	}
	public String getMaxval() {
		return maxval;
	}
	public void setMaxval(String maxval) {
		this.maxval = maxval;
	}
	public String getMinval() {
		return minval;
	}
	public void setMinval(String minval) {
		this.minval = minval;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, Object> getOption() {
		return option;
	}
	public void setOption(Map<String, Object> option) {
		this.option = option;
	}
	public List<Map<String, Object>> getValues() {
		return values;
	}
	public void setValues(List<Map<String, Object>> values) {
		this.values = values;
	}

	@Override
	public void validate() {
		this.name = RSBIUtils.htmlEscape(this.name);
	}
	public String getCascade() {
		return cascade;
	}
	public void setCascade(String cascade) {
		this.cascade = cascade;
	}
	public String getDefFirstValue() {
		return defFirstValue;
	}
	public void setDefFirstValue(String defFirstValue) {
		this.defFirstValue = defFirstValue;
	}

	public String getTableIdCol() {
		return tableIdCol;
	}

	public void setTableIdCol(String tableIdCol) {
		this.tableIdCol = tableIdCol;
	}

	public String getTableNameCol() {
		return tableNameCol;
	}

	public void setTableNameCol(String tableNameCol) {
		this.tableNameCol = tableNameCol;
	}

	public String getTablePidCol() {
		return tablePidCol;
	}

	public void setTablePidCol(String tablePidCol) {
		this.tablePidCol = tablePidCol;
	}

	public String getMatchTable() {
		return matchTable;
	}

	public void setMatchTable(String matchTable) {
		this.matchTable = matchTable;
	}

	public String getMatchTableName() {
		return matchTableName;
	}

	public void setMatchTableName(String matchTableName) {
		this.matchTableName = matchTableName;
	}

	public Map<String, String> getSelval() {
		return selval;
	}

	public void setSelval(Map<String, String> selval) {
		this.selval = selval;
	}

	public Map<String, String> getSelval2() {
		return selval2;
	}

	public void setSelval2(Map<String, String> selval2) {
		this.selval2 = selval2;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
