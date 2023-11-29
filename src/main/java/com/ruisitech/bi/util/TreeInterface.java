/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import java.util.Map;

public interface TreeInterface {

	public void processMap(Map<String, Object> m);

	public void processEnd(Map<String, Object> m, boolean hasChild);
}
