/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.common;

public class DSColumn {
	private Integer idx;
	private String name;
	private String type;
	private String dispName;
	private Integer length; //字段长度
	private Integer scale; //小数位长度
	private String tname;
	private Boolean isshow = true; //是否显示字段
	private String expression;
	private String defvalue; //默认值

	private Integer esKeyword; //es字段是否是 keyword 类型 1 是 0 否

	public String getName() {
		if(name != null){
			return name.trim();
		}else{
			return name;
		}
	}
	public String getType() {
		return type;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDispName() {
		return dispName;
	}
	public void setDispName(String dispName) {
		this.dispName = dispName;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public Integer getIdx() {
		return idx;
	}
	public void setIdx(Integer idx) {
		this.idx = idx;
	}
	public Boolean getIsshow() {
		return isshow;
	}
	public void setIsshow(Boolean isshow) {
		this.isshow = isshow;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public Integer getScale() {
		return scale;
	}
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	public String getDefvalue() {
		return defvalue;
	}
	public void setDefvalue(String defvalue) {
		this.defvalue = defvalue;
	}
	public Integer getEsKeyword() {
		return esKeyword;
	}

	public void setEsKeyword(Integer esKeyword) {
		this.esKeyword = esKeyword;
	}
}
