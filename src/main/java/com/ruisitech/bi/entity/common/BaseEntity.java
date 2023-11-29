/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Value;

public class BaseEntity {

	@JsonIgnore
	private String dbName =  RSBIUtils.getConstant("dbName");

	@JsonIgnore
	private String dwName = RSBIUtils.getConstant("dwName");

	/**
	 * id类型，
	 *  1是自增加，比如MySQL。sqlserver, db2等，
	 *  2 是  手动赋值，比如oracle
	 */
	@JsonIgnore
	private Integer idType;

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Integer getIdType() {
		if("oracle".equals(dbName)){
			idType = 2;
		}else{
			idType = 1;
		}
		return idType;
	}

	/**
	public void setIdType(Integer idType) {
		this.idType = idType;
	}
	**/

	public String getDwName() {
		return dwName;
	}

	public void setDwName(String dwName) {
		this.dwName = dwName;
	}

	/**
	 * 获取指定数据库的取时间函数
	 * @return
	 */
	@JsonIgnore
	public String getDateString(){
		String key = "";
		if(dbName.equalsIgnoreCase("mysql")){
			key = "now()";
		}else if(dbName.equalsIgnoreCase("oracle")){
			key = "sysdate";
		}else if(dbName.equalsIgnoreCase("sqlser")){
			key = "getdate()";
		}else if("db2".equalsIgnoreCase(dbName)){
			key = "current timestamp";
		}else if("psql".equalsIgnoreCase(dbName)) {
			key = "current_timestamp";
		}else if("sqlite".equalsIgnoreCase(dbName)){
			key = "strftime('%s','now') * 1000";
		}else if("dm".equalsIgnoreCase(dbName)){
			key = "sysdate()";
		}
		return key;
	}

	public void validate(){

	}
}
