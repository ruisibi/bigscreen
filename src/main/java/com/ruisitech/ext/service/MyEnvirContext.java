/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.init.UserEnvirContext;
import com.rsbi.ispire.dc.grid.GridShift;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定义我自己的context,主要包含时间偏移计算等方法
 * 实现该接口后，用户可以在xml的配置中直接调用里面的方法
 * @author hq
 * @date 2014-8-17
 */
public class MyEnvirContext implements UserEnvirContext {

	private static Logger log = LogManager.getLogger();

	@Override
	public String getContextName() {
		return "myUtils";
	}

	@Override
	public Object getUserEnvirContext() {
		return this;
	}

	/**
	 * 值偏移
	 *
	 * @param val
	 * @param type
	 * @param jstype
	 * @return
	 */
	public String resetVals(String val, String type, String dateFormat, int jstype) {
		return null;
		//return TableService.resetVals(val, type, dateFormat, jstype);
	}

	/**
	 * 区间偏移
	 *
	 * @param start
	 * @param end
	 * @param type
	 * @param jstype
	 * @return
	 */
	public String resetBetween(String start, String end, String type, String dateFormat, int jstype) {
		//String[] q = TableService.resetBetween(start, end, type, dateFormat, jstype);
		//return  "'" + q[0] + "' and '" + q[1] + "'";
		return null;
	}

	/**
	 * 时间偏移
	 *
	 * @param start
	 * @param tp
	 * @param dateformat
	 * @param jsType
	 * @param pos        调用的位置， 1 表示 between 的第一个， 2 表示between的后一个
	 * @return
	 */
	public String shiftDate(String start, String tp, String dateformat, String jsType, String compareValue, int pos) throws Exception {
		try {
			String[] ret = GridShift.getDateShiftValue(start, tp, dateformat, jsType, compareValue);
			if (ret == null) {
				return null;
			}
			if ("lj".equals(jsType))
				if (pos == 1) {
					return ret[ret.length - 1];   //累计第一个取最小
				} else {
					return ret[0];  //累计最后一个取最大
				}
			else {
				return ret[0];
			}
		} catch (ParseException ex) {
			log.error("日期计算出错", ex);
			throw ex;
		}
	}

	public String shiftDate(String start, String tp, String dateformat, String jsType, int pos) throws Exception {
		return shiftDate(start, tp, dateformat, jsType, null, pos);
	}

	public String convertDataControl(String col, String valType) {
		if (RSBIUtils.getLoginUserInfo().getDeptCode() == null) {
			return "";
		}
		String[] vls = RSBIUtils.getLoginUserInfo().getDeptCode().split(",");
		if (vls.length == 0) {

		}
		StringBuffer sb = new StringBuffer("");
		sb.append(" and ");
		sb.append(col);
		sb.append(" in ");
		sb.append("(");
		for (int i = 0; i < vls.length; i++) {
			if ("String".equalsIgnoreCase(valType)) {
				sb.append("'");
			}
			sb.append(vls[i]);
			if ("String".equalsIgnoreCase(valType)) {
				sb.append("'");
			}
			if (i != vls.length - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
