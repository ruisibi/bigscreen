/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.etl;

import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.io.Serializable;

/**
 * BI 数据表子表（字段表）
 * @author hq
 *
 */
public class EtlTableMetaCol extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -3160720659714446528L;

	private Integer colId;//

    private String colName;//

    private String colType;//

    private Integer colLength;//

    private Integer colScale;  //精度

    private String colNote;//

    private Integer colOrd;//

    private Integer companyId;//

    private Integer tableId;//

    private String tableName;  //对应的表名称

    private String incomeTname; //字段的来源表，用在数据聚合中

    private String expression;//表达式

    private String inputType;//输入类型，文本/选择

    private String defvalue;//字段默认值

    private String colDesc;//填报时的提示信息

    private String options;//被选值

    private String valuestype;//值类型(固定值、动态值)

    private String matchTable;//值通过关联表获取

    private String matchCol;//对应的字段

    private String useCol;//用一个字段

    private String updateCol;//去更新目标表的字段

    private String tCondition;//条件

    private String matchColText;//

    private String tmpid; //数据填报中使用

    private String fromCol;

    private Integer required;  //是否必填

    private Integer searchCol; //是否做查询条件

	private String dimType; //当字段做立方体的维度，dimType表示维度类型
	private String alias; //当字段做立方体的维度，alias表示维度别名
	private String dateformat; //当字段做立方体的维度，dateformat表示日期维度格式
	private Integer esKeyword;  //是否ES keyword 字段

	public Integer getColId() {
		return colId;
	}

	public void setColId(Integer colId) {
		this.colId = colId;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public Integer getColLength() {
		return colLength;
	}

	public void setColLength(Integer colLength) {
		this.colLength = colLength;
	}

	public String getColNote() {
		return colNote;
	}

	public void setColNote(String colNote) {
		this.colNote = colNote;
	}

	public Integer getColOrd() {
		return colOrd;
	}

	public void setColOrd(Integer colOrd) {
		this.colOrd = colOrd;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getTableId() {
		return tableId;
	}

	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getDefvalue() {
		return defvalue;
	}

	public void setDefvalue(String defvalue) {
		this.defvalue = defvalue;
	}

	public String getColDesc() {
		return colDesc;
	}

	public void setColDesc(String colDesc) {
		this.colDesc = colDesc;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getValuestype() {
		return valuestype;
	}

	public void setValuestype(String valuestype) {
		this.valuestype = valuestype;
	}

	public String getMatchTable() {
		return matchTable;
	}

	public void setMatchTable(String matchTable) {
		this.matchTable = matchTable;
	}

	public String getMatchCol() {
		return matchCol;
	}

	public void setMatchCol(String matchCol) {
		this.matchCol = matchCol;
	}

	public String getUseCol() {
		return useCol;
	}

	public void setUseCol(String useCol) {
		this.useCol = useCol;
	}

	public String getUpdateCol() {
		return updateCol;
	}

	public void setUpdateCol(String updateCol) {
		this.updateCol = updateCol;
	}

	public String gettCondition() {
		return tCondition;
	}

	public void settCondition(String tCondition) {
		this.tCondition = tCondition;
	}

	public String getMatchColText() {
		return matchColText;
	}

	public void setMatchColText(String matchColText) {
		this.matchColText = matchColText;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTmpid() {
		return tmpid;
	}

	public void setTmpid(String tmpid) {
		this.tmpid = tmpid;
	}

	public String getIncomeTname() {
		return incomeTname;
	}

	public void setIncomeTname(String incomeTname) {
		this.incomeTname = incomeTname;
	}

	public Integer getColScale() {
		return colScale;
	}

	public void setColScale(Integer colScale) {
		this.colScale = colScale;
	}

	public String getFromCol() {
		return fromCol;
	}

	public void setFromCol(String fromCol) {
		this.fromCol = fromCol;
	}

	public Integer getRequired() {
		return required;
	}

	public void setRequired(Integer required) {
		this.required = required;
	}

	public Integer getSearchCol() {
		return searchCol;
	}

	public void setSearchCol(Integer searchCol) {
		this.searchCol = searchCol;
	}

	public String getDimType() {
		return dimType;
	}

	public void setDimType(String dimType) {
		this.dimType = dimType;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDateformat() {
		return dateformat;
	}

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	@Override
	public void validate() {
		this.colName = RSBIUtils.htmlEscape(this.colName);
		this.colType = RSBIUtils.htmlEscape(this.colType);
		this.colNote = RSBIUtils.htmlEscape(this.colNote);

	}

	public Integer getEsKeyword() {
		return esKeyword;
	}

	public void setEsKeyword(Integer esKeyword) {
		this.esKeyword = esKeyword;
	}
}
