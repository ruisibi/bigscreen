/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.common.BaseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class QueryDayDto extends BaseEntity {

	private String startDay;
	private String endDay;
	public String getStartDay() {
		return startDay;
	}
	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}
	public String getEndDay() {
		return endDay;
	}
	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}

	public int getBetweenDay() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long l1 = sdf.parse(this.startDay).getTime();
		long l2 = sdf.parse(this.endDay).getTime();
		long result = Math.abs(l1 - l2) / (24 * 60 * 60 * 1000);
		return (int)result;
	}

	@Override
	public void validate() {

	 }
}
