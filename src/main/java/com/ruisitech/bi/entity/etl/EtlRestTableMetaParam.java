/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.etl;

import java.io.Serializable;

/**
 * 封装 Rest 接口的参数
 * @author hq
 * @Date 2020年3月22日
 */
public class EtlRestTableMetaParam implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -5419977362434897634L;
	private String paramName;
	private String paramNote;
	private String paramDefvalue;
	private String paramType;  //参数类型。 String/Number/Date
	private Boolean requared;  //是否必填
	private Boolean paramisPageSize;  //是否分页的pagesize参数
	private Boolean paramisCurPage;  //是否分页的当前页参数

	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamNote() {
		return paramNote;
	}
	public void setParamNote(String paramNote) {
		this.paramNote = paramNote;
	}
	public String getParamDefvalue() {
		return paramDefvalue;
	}
	public void setParamDefvalue(String paramDefvalue) {
		this.paramDefvalue = paramDefvalue;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public Boolean getRequared() {
		return requared;
	}
	public void setRequared(Boolean requared) {
		this.requared = requared;
	}

	public Boolean getParamisPageSize() {
		return paramisPageSize;
	}

	public void setParamisPageSize(Boolean paramisPageSize) {
		this.paramisPageSize = paramisPageSize;
	}

	public Boolean getParamisCurPage() {
		return paramisCurPage;
	}

	public void setParamisCurPage(Boolean paramisCurPage) {
		this.paramisCurPage = paramisCurPage;
	}
}
