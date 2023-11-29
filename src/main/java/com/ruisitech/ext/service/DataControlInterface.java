/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.frame.User;


/**
 * 数据权限控制接口
 * @author hq
 * @date 2017-1-18
 */
public interface DataControlInterface {

	/**
	 * 处理类,返回sql字符串, 字符串以 and 开始， 比如： and c='y'
	 * master 表示主表名称
	 * @return
	 */
	public String process(User u, TableInfoVO tvo);

}
