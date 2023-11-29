/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.common;


public class PageParam {
	/**
	 * 分页参数
	 */
	private Integer total;
	private Integer page;  //页码。从1开始
	private Integer rows;  //每页记录数

	//排序
	private String sort;
	private String order;

	//搜索
	private String search;

	public Integer getTotal() {
		if(total==null){
			total = 0;
		}
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPage() {
		if(page == null) {
			page = 1;
		}
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getRows() {
		if(rows == null) {
			rows = 10;
		}
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	@Override
	public String toString() {
		return "PageParam [total=" + total + ", pageIndex=" + page
				+ ", pageSize=" + rows + "]";
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
}
