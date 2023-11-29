/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有 
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.common.BaseEntity;

import java.text.ParseException;

public class QueryMonthDto extends BaseEntity {

	private String startMonth;
	private String endMonth;
	public String getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}
	public String getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}
	
	public int getBetweenMonth() throws ParseException{
		int year1 = Integer.parseInt(this.startMonth.substring(0,4));
		int year2 = Integer.parseInt(this.endMonth.substring(0,4));
		
		int month1 = Integer.parseInt(this.startMonth.substring(4,6));
		int month2 = Integer.parseInt(this.endMonth.substring(4,6));
		
		int betweenMonth = month2 - month1;
		int betweenYear = year2 - year1;
		
		
		return betweenYear * 12 + betweenMonth;
	}
	
	@Override
	public void validate() {
		 
	 }
}
