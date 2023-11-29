/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.common.BaseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 周对象
 */
public class QueryWeekDto extends BaseEntity {

	private String startWeek;  //年-月-日格式
	private String endWeek; //年-月-日格式

	public String getStartWeek() {
		return startWeek;
	}

	public void setStartWeek(String startWeek) {
		this.startWeek = startWeek;
	}

	public String getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(String endWeek) {
		this.endWeek = endWeek;
	}

	public int getBetweenDay() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long l1 = sdf.parse(this.startWeek).getTime();
		long l2 = sdf.parse(this.endWeek).getTime();
		long result = Math.abs(l1 - l2) / (24 * 60 * 60 * 1000 * 7);
		return (int)result;
	}

	@Override
	public void validate() {

	 }
}
