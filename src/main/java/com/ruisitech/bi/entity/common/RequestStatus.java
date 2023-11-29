/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.common;

public enum RequestStatus {
	NOTFIND(3), //资源未找到404
	NOLOGIN(2), //未登录
	SUCCESS(1),//成功
	FAIL_FIELD(0);//失败

	private Integer status;

	private RequestStatus(int status) {
		this.status=status;
	}

	public Integer getStatus() {
		return status;
	}


}
