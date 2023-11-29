/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.common.PageParam;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 明细提取dto
 * @author hq
 *
 */
public class TableDetailDto extends PageParam implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5277885507548632100L;

	private Map<String, String> pms;

	private Integer tid;

	private List<ParamDto> pagePms;  //页面参数

	public Map<String, String> getPms() {
		return pms;
	}

	public void setPms(Map<String, String> pms) {
		this.pms = pms;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public List<ParamDto> getPagePms() {
		return pagePms;
	}

	public void setPagePms(List<ParamDto> pagePms) {
		this.pagePms = pagePms;
	}
}
