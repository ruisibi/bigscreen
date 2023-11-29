/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.service.bireport.TableCacheService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 默认数据权限控制类，如果需要用户可以再扩展
 * @author hq
 *
 */
public class DataControlImpl implements DataControlInterface {

	@Override
	public String process(User u, TableInfoVO tvo) {
		//用户未登录访问，u == null
		if(u == null || tvo == null){
			return "";
		}
		String valType = null, colName = null;
		if(tvo.getDataControlCol() != null && tvo.getDataControlCol().length() > 0){
			EtlTableMetaCol col = tvo.findColByAlias(tvo.getDataControlCol());
			if("dw".equals(tvo.getIncome()) && col == null){
				colName = tvo.getDataControlCol();
				valType = "Int";
			}else if(col == null){
				throw new RuntimeException("配置的数据权限字段"+tvo.getDataControlCol()+"不存在。");
			}else {
				valType = col.getColType();
				colName = tvo.getDataControlCol();
			}
			return "$myUtils.convertDataControl('"+colName+"','"+valType+"')";
		}else {
			return "";
		}

	}

}
