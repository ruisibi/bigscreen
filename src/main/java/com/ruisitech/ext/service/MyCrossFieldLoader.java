/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.cross.CrossFieldLoader;
import com.rsbi.ext.engine.view.context.cross.CrossField;
import com.rsbi.ext.engine.wrapper.ExtRequest;

import java.util.List;
import java.util.Map;

public class MyCrossFieldLoader implements CrossFieldLoader {

	private ExtRequest request;

	@Override
	public void setRequest(ExtRequest request) {
		this.request = request;
	}



	@Override
	public CrossField loadFieldByKpiCode(String kpiCode) {
		// TODO Auto-generated method stub
		return null;
	}

	public String loadFieldName(String type, String value) {
		return "合计";
	}

	@Override
	public List<String> loadUserCustomize(String userId, String mvId) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String loadFieldName(String aggre) {
		if("sum".equalsIgnoreCase(aggre)){
			return "合计值";
		}else if("avg".equalsIgnoreCase(aggre)){
			return "均值";
		}else if("max".equalsIgnoreCase(aggre)){
			return "最大值";
		}else if("min".equalsIgnoreCase(aggre)){
			return "最小值";
		}else if("count".equalsIgnoreCase(aggre)){
			return "计数";
		}else if("var".equalsIgnoreCase(aggre)){
			return "方差";
		}else if("sd".equalsIgnoreCase(aggre)){
			return "标准差";
		}else if("middle".equalsIgnoreCase(aggre)){
			return "中位数";
		}else{
			return "合计";
		}
	}

	@Override
	public List loadField(String type, String values, Map keys) {
		// TODO 自动生成的方法存根
		return null;
	}

}
