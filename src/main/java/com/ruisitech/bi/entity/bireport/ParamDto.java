/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.alibaba.fastjson.JSONObject;
import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.util.RSBIUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ParamDto extends BaseEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -390059993153377402L;
	private String type;
	private String colname;
	private String alias;
	private String valType;
	private String dateformat;
	private String tname;
	private String st;
	private String end;
	private List<String> vals;
	private List<String> valStrs;
	private String valDesc;
	private Integer id;
	private Integer tid;
	private String colDesc;
	private String tableName;
	private String dimord;
	private String tableColKey;
	private String tableColName;
	private String grouptype;
	private String name;
	private Integer filtertype;
	private String fromCol;
	/**
	 *用在数据查询中的字段
	 */
	private String expression;
	private String datasetid;
	private JSONObject filter;
	private String dispName;
	private Integer level; //父子维度的层级,从 1开始
	/** 字段结束 **/

	private String oper;
	private Integer value1;
	private Integer value2;

	/**
	 * 检测SQL注入
	 */
	public void checkSql(TableInfoVO tinfo, Map<String, String> colAlias){
		//RSBIUtils.processSql(colname);
		RSBIUtils.processSql(alias);
		//RSBIUtils.processSql(fromCol);
		RSBIUtils.processSql(tableColKey);
		RSBIUtils.processSql(tableColName);
		if(vals != null) {
			for (String v : vals) {
				RSBIUtils.processSql(v);
			}
		}
		if(tinfo != null) {
			EtlTableMetaCol col = tinfo.findColByAlias(name);
			if (col == null) {
				throw new RuntimeException("字段" + name + "映射错误");
			}
			expression = col.getExpression();
			name = col.getColName();
		}else if(colAlias != null){
			colname = colAlias.get(alias);
			if(colname == null){
				throw new RuntimeException("字段" + alias + "映射错误");
			}
		}

		if(filter != null){
			RSBIUtils.processSql((String) filter.get("filterType"));
			RSBIUtils.processSql((String) filter.get("enddt"));
			RSBIUtils.processSql((String) filter.get("stdt"));
			Object val1 =  filter.get("val1");
			if(val1 != null && val1 instanceof String) {
				RSBIUtils.processSql((String)val1);
			}
			Object val2 =  filter.get("val2");
			if(val2 != null && val2 instanceof String) {
				RSBIUtils.processSql((String)val2);
			}
		}
	}

	public boolean isDateDim(){
		if("day".equals(type) || "month".equals(type) || "quarter".equals(type) || "year".equals(type) ||
			"halfyear".equals(type) || "week".equals(type)){
			return true;
		}else{
			return false;
		}
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColname() {
		return colname;
	}
	public void setColname(String colname) {
		this.colname = colname;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getValType() {
		return valType;
	}
	public void setValType(String valType) {
		this.valType = valType;
	}
	public String getDateformat() {
		return dateformat;
	}
	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}

	public List<String> getVals() {
		return vals;
	}

	public void setVals(List<String> vals) {
		this.vals = vals;
	}

	public void setValStrs(List<String> valStrs) {
		this.valStrs = valStrs;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getValDesc() {
		return valDesc;
	}
	public void setValDesc(String valDesc) {
		this.valDesc = valDesc;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getColDesc() {
		return colDesc;
	}
	public void setColDesc(String colDesc) {
		this.colDesc = colDesc;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getDimord() {
		return dimord;
	}
	public void setDimord(String dimord) {
		this.dimord = dimord;
	}
	public String getTableColKey() {
		return tableColKey;
	}
	public void setTableColKey(String tableColKey) {
		this.tableColKey = tableColKey;
	}
	public String getTableColName() {
		return tableColName;
	}
	public void setTableColName(String tableColName) {
		this.tableColName = tableColName;
	}
	public String getGrouptype() {
		return grouptype;
	}
	public void setGrouptype(String grouptype) {
		this.grouptype = grouptype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getFiltertype() {
		return filtertype;
	}
	public void setFiltertype(Integer filtertype) {
		this.filtertype = filtertype;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getDatasetid() {
		return datasetid;
	}
	public void setDatasetid(String datasetid) {
		this.datasetid = datasetid;
	}
	public JSONObject getFilter() {
		return filter;
	}
	public void setFilter(JSONObject filter) {
		this.filter = filter;
	}
	public String getDispName() {
		return dispName;
	}
	public void setDispName(String dispName) {
		this.dispName = dispName;
	}
	public String getFromCol() {
		return fromCol;
	}
	public void setFromCol(String fromCol) {
		this.fromCol = fromCol;
	}

	public List<String> getValStrs() {
		return valStrs;
	}

	@Override
	public void validate() {

	 }
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public Integer getValue1() {
		return value1;
	}

	public void setValue1(Integer value1) {
		this.value1 = value1;
	}

	public Integer getValue2() {
		return value2;
	}

	public void setValue2(Integer value2) {
		this.value2 = value2;
	}
}
