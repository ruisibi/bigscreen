/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.List;
import java.util.Map;

/**
 * 组件参数dto,对应表的一个字段
 * @author gdp
 *
 */
public class CompParamDto {

	private String id;
	private String col; //对应字段
	private String expression;  //表达式字段
	private String type;  //运算符号， 比如： >, <, = , != ,between 等
	private String val;
	private String val2;
	private String vtype;
	private String valuetype;
	private String usetype;  //gdz/param
	private String usetype2; //val2的valuetype,如果不设置使用 usetype
	private String linkparam;
	private String linkparam2;
	private String nodetype;
	private String dateformat;
	private String dimType; //维度类型
	private String code; //回调函数
	private String alias; //对呀维度的alias
	private String tableAlias; //表的别名字段，当表类型为数据集时有用

	/**
	 * 检测SQL注入，用在立方体中
	 */
	public void checkSql(Map<String, String> colAlias){
		RSBIUtils.processSql(type);
		RSBIUtils.processSql(col);
		if(usetype2 == null){
			usetype2 = usetype;
		}
		//可能是函数，不处理过滤
		if(!(usetype.equals("gdz") && val != null && val.indexOf("(") >= 0)) {
			RSBIUtils.processSql(val);
		}
		//可能是函数，不处理过滤
		if(!(usetype2.equals("gdz") && val2 != null && val2.indexOf("(") >= 0)) {
			RSBIUtils.processSql(val2);
		}
		/**
		col = colAlias.get(alias);
		if(alias != null && col == null){
			throw new RuntimeException("字段"+alias+"映射错误");
		}
		 **/
	}

	/**
	 * 检测SQL注入，用在数据表中
	 */
	public void checkSql(List<EtlTableMetaCol> cols){
		RSBIUtils.processSql(type);
		if(usetype2 == null){
			usetype2 = usetype;
		}

		//RSBIUtils.processSql(fromCol);
		RSBIUtils.processSql(col);
		if(!(usetype.equals("gdz") && val != null && val.indexOf("(") >= 0)) {
			RSBIUtils.processSql(val);
		}
		if(!(usetype2.equals("gdz") && val2 != null && val2.indexOf("(") >= 0)) {
			RSBIUtils.processSql(val2);
		}

		//根据 col 获取 表达式
		for(EtlTableMetaCol c : cols){
			if(c.getColName().equals(this.col)){
				this.expression = c.getExpression();
				break;
			}
		}
	}

	public boolean isDateDim(){
		if("day".equals(dimType) || "month".equals(dimType) || "quarter".equals(dimType) || "year".equals(dimType) ||
				"halfyear".equals(dimType) || "week".equals(dimType)){
			return true;
		}else{
			return false;
		}
	}

	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getVal2() {
		return val2;
	}
	public void setVal2(String val2) {
		this.val2 = val2;
	}
	public String getValuetype() {
		return valuetype;
	}
	public void setValuetype(String valuetype) {
		this.valuetype = valuetype;
	}
	public String getUsetype() {
		return usetype;
	}
	public void setUsetype(String usetype) {
		this.usetype = usetype;
	}
	public String getLinkparam() {
		return linkparam;
	}
	public void setLinkparam(String linkparam) {
		this.linkparam = linkparam;
	}
	public String getLinkparam2() {
		return linkparam2;
	}
	public void setLinkparam2(String linkparam2) {
		this.linkparam2 = linkparam2;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVtype() {
		return vtype;
	}
	public void setVtype(String vtype) {
		this.vtype = vtype;
	}
	public String getNodetype() {
		return nodetype;
	}
	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public String getDateformat() {
		return dateformat;
	}
	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}
	public String getDimType() {
		return dimType;
	}
	public void setDimType(String dimType) {
		this.dimType = dimType;
	}
	public String getUsetype2() {
		return usetype2;
	}

	public void setUsetype2(String usetype2) {
		this.usetype2 = usetype2;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}
}
