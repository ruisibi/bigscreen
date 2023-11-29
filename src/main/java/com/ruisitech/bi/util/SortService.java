/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有 
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import com.ruisitech.bi.entity.common.BaseEntity;
import org.apache.commons.beanutils.PropertyUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

/**
 * 排序的工具类
 * @author gdp
 *
 */
public class SortService implements Comparator<BaseEntity> {

	//排序
	private String sort;
	private String order;
	
	public SortService(String sort, String order){
		this.sort = sort;
		this.order = order;
	}
	
	@Override
	public int compare(BaseEntity o1, BaseEntity o2) {
		Object val1;
		Object val2;
		try{
			val1 = PropertyUtils.getProperty(o1, sort);
			val2 = PropertyUtils.getProperty(o2, sort);
		}catch(Exception ex){
			val1 = null;
			val2 = null;
			ex.printStackTrace();
		}
		if(val1 == null || val2 == null){
			return 0;
		}
		int ret = 0;
		if(val1 instanceof String){
			ret = ((String)val1).compareTo((String)val2);
		}else if(val1 instanceof Long){
			ret = ((Long)val1).compareTo((Long)val2);
		}else if(val1 instanceof Integer){
			ret = ((Integer)val1).compareTo((Integer)val2);
		}else if(val1 instanceof BigDecimal){
			ret = ((BigDecimal)val1).compareTo((BigDecimal)val2);
		}else if(val1 instanceof Date){
			ret = ((Date)val1).compareTo((Date)val2);
		}
		if("desc".equalsIgnoreCase(order)){
			ret = - ret;
		}
		return ret;
	}

}
