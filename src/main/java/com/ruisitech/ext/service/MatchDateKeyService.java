/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.init.ExtEnvirContext;
import com.rsbi.ext.engine.wrapper.ExtRequest;
import com.rsbi.ispire.dc.grid.MasterProcess;
import com.ruisitech.bi.entity.bireport.DateDimDto;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 在计算同环比，累计时，在主数据集查询后，需要调用此接口，查找最小时间维度得区间值，用来做同环比做过滤条件。
 * @author zxd
 * @Date 2019年10月7日
 */
public class MatchDateKeyService implements MasterProcess {

	/**
	 *时间维度列表
	 */
	private List<DateDimDto> dateDims;

	public MatchDateKeyService(List<DateDimDto> dateDims) {
		this.dateDims = dateDims;
	}

	@Override
	public void process(List<Map<String, Object>> dts, ExtEnvirContext veloContext, ExtRequest request)
			throws Exception {
		//获取最小维度得最大，最小值.
		DateDimDto minDim = dateDims.get(dateDims.size() - 1);

		//通过主查询的时间参数，设置子查询时间, 其中时间参数为最小时间维度
		String start = minDim.getAlias()+"_start";
		String end = minDim.getAlias()+"_end";
		if(minDim.getVals() != null && minDim.getVals().length() > 0){
			String[] vls = minDim.getVals().split(",");
			List<String> ls = Arrays.asList(vls);
			ls.sort(String::compareTo);
			veloContext.put(start, ls.get(0));
			veloContext.put(end, ls.get(vls.length - 1));
		}else if(minDim.getStart() != null && minDim.getEnd() != null){
			String startVal = minDim.getStart();
			String endVal = minDim.getEnd();
			if(minDim.isDyna()) {
				startVal = (String)veloContext.get(startVal);
				endVal = (String)veloContext.get(endVal);
			}
			veloContext.put(start, startVal);
			veloContext.put(end, endVal);
		}else{
			veloContext.put(start, "");
			veloContext.put(end, "");
		}

		/** 2022-08-01 hq 这串代码没看懂什么意思
		while(true) {
			boolean match = true;
			for(DateDimDto dim : dateDims) {
				String tp = dim.getType();
				if("year".equals(tp) || "halfyear".equals(tp) || "quarter".equals(tp) || "month".equals(tp) || "week".equals(tp) || "day".equals(tp)){
					if(dim.getVals() != null && dim.getVals().length() > 0) {
						hasFilter = true;
						String[] vls = dim.getVals().split(",");
						String curVal = new SimpleDateFormat(dim.getDateformat()).format(cal.getTime());
						if(!isMatch(curVal, vls)) {
							match = false;
							break;
						}
					}
				}
				if("month".equals(tp)) {
					if(dim.getStart() != null && dim.getStart().length() > 0) {
						hasFilter = true;
						String start = dim.getStart();
						String end = dim.getEnd();
						if(dim.isDyna()) {
							start = (String)veloContext.get(start);
							end = (String)veloContext.get(end);
						}
						Calendar startDt = Calendar.getInstance();
						startDt.setTime(new SimpleDateFormat(dim.getDateformat()).parse(start));
						Calendar endDt = Calendar.getInstance();
						endDt.setTime(new SimpleDateFormat(dim.getDateformat()).parse(end));
						endDt.add(Calendar.MONTH, 1); //月份增加一月
						if(!(cal.getTimeInMillis() > startDt.getTimeInMillis() && cal.getTimeInMillis() < endDt.getTimeInMillis() - 1000)) {
							match = false;
							break;
						}
					}
				}
				if("week".equals(tp)) {
					if(dim.getStart() != null && dim.getStart().length() > 0) {
						hasFilter = true;
						String start = dim.getStart();
						String end = dim.getEnd();
						if(dim.isDyna()) {
							start = (String)veloContext.get(start);
							end = (String)veloContext.get(end);
						}
						Calendar startDt = Calendar.getInstance();
						startDt.setTime(new SimpleDateFormat(dim.getDateformat()).parse(start));
						Calendar endDt = Calendar.getInstance();
						endDt.setTime(new SimpleDateFormat(dim.getDateformat()).parse(end));
						endDt.set(Calendar.DAY_OF_MONTH, endDt.get(Calendar.DAY_OF_MONTH) + 7); //给当前时间增加一周
						if(!(cal.getTimeInMillis() > startDt.getTimeInMillis() && cal.getTimeInMillis() < endDt.getTimeInMillis() - 1000)) {
							match = false;
							break;
						}
					}
				}
				if("day".equals(tp)) {
					if(dim.getStart() != null && dim.getStart().length() > 0) {
						hasFilter = true;
						String start = dim.getStart();
						String end = dim.getEnd();
						if(dim.isDyna()) {
							start = (String)veloContext.get(start);
							end = (String)veloContext.get(end);
						}
						Calendar startDt = Calendar.getInstance();
						startDt.setTime(new SimpleDateFormat(dim.getDateformat()).parse(start));
						Calendar endDt = Calendar.getInstance();
						endDt.setTime(new SimpleDateFormat(dim.getDateformat()).parse(end));
						endDt.add(Calendar.DATE, 1); //日期加一天
						if(!(cal.getTimeInMillis() > startDt.getTimeInMillis() && cal.getTimeInMillis() < endDt.getTimeInMillis() - 1000)) {
							match = false;
							break;
						}
					}
				}
			}
			if(match) {
				String fval = minDimFmt.format(cal.getTime());
				if(!matchVls.contains(fval)) {
					matchVls.add(fval);
				}
			}
			cal.add(Calendar.DATE, 1);
			if(cal.getTimeInMillis() > now.getTimeInMillis()) {
				break;
			}
		}
		 **/
		//返回值都用区间表示
		/**
		DateDimDto minDate = minDim;
		String start = minDate.getAlias()+"_start";
		String end = minDate.getAlias()+"_end";
		if(!hasFilter) {
			veloContext.put(start, "");
			veloContext.put(end, "");
			return;
		}

		String vl1 = matchVls.get(0);
		String vl2 = matchVls.get(matchVls.size() - 1);
		veloContext.put(start, vl1);
		veloContext.put(end, vl2);
		 **/
	}

	/**
	private boolean isMatch(String val, String[] vals) {
		boolean match = false;
		for(String v : vals) {
			if(val.equals(v)) {
				match = true;
				break;
			}
		}
		return match;
	}
	 **/
}
