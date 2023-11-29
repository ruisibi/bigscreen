/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import com.ruisitech.bi.entity.common.RequestStatus;
import com.ruisitech.bi.entity.common.Result;

public class BaseController {

	public Object buildSucces(){
		return new Result(RequestStatus.SUCCESS.getStatus(), "操作成功", null);
	}

	public Object buildSucces(Object datas){
		return new Result(RequestStatus.SUCCESS.getStatus(), "操作成功", datas);
	}

	public Object buildSucces(com.github.pagehelper.PageInfo<?> page){
		Long total = page.getTotal();
	   // Integer pageIndex = page.getPageNum();
	   // Integer pageSize = page.getPageSize();
	    Object datas = (Object) page.getList();
		return new Result(RequestStatus.SUCCESS.getStatus(), "操作成功", datas, total);
	}

	public Object buildError(String msg){
		return new Result(RequestStatus.FAIL_FIELD.getStatus(), msg, null);
	}

	public Object buildNotFindError(String msg){
		return new Result(RequestStatus.NOTFIND.getStatus(), msg, null);
	}
}
