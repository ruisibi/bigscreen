/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import java.io.Serializable;

/**
 * 	时间 维度，用来封装在同环比，累计中使用得时间维度。
 * @author zxd
 * @Date 2019年10月8日
 */
public class DateDimDto implements Serializable {

	private String type;  //类型
	private String alias; //别名
	private String dateformat;
	private String start;  //时间区间
	private String end; //时间区间
	private String vals; //值列表
	private boolean dyna; //是否动态值
	private Integer income; //时间维度的来源， 1 参数 /  2 维度

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getDateformat() {
		return dateformat;
	}
	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}
	public String getVals() {
		return vals;
	}
	public void setVals(String vals) {
		this.vals = vals;
	}
	public boolean isDyna() {
		return dyna;
	}
	public void setDyna(boolean dyna) {
		this.dyna = dyna;
	}

	public Integer getIncome() {
		return income;
	}

	public void setIncome(Integer income) {
		this.income = income;
	}
}
