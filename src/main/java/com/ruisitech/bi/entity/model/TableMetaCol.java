/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.model;

import com.ruisitech.bi.entity.common.BaseEntity;

/**
 * 数据建模子表， 对应  olap_table_col_meta 表
 * @author hq
 *
 */
public class TableMetaCol extends BaseEntity {

	private Integer rid;
	private Integer tid;
	/**
	 * tid 对应的 tname
	 */
	private String tname;
	/**
	 * 1 维度，2 度量
	 */
	private Integer colType;
	private Integer colId;
	private String col;
	private String alias;
	/**
	 * 数据集创建的动态字段
	 * 1 是， 0 否
	 */
	private Integer calc;
	private Integer ord;

	//不存储的字段
	private Integer targetId;
	/**
	 * 维度或度量是否被修改
	 */
	private String isupdate;

	/**
	 * 维度或度量从哪个字段来
	 */
	private String fromCol;


	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public Integer getColType() {
		return colType;
	}
	public void setColType(Integer colType) {
		this.colType = colType;
	}
	public Integer getColId() {
		return colId;
	}
	public void setColId(Integer colId) {
		this.colId = colId;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Integer getCalc() {
		return calc;
	}
	public void setCalc(Integer calc) {
		this.calc = calc;
	}
	public Integer getOrd() {
		return ord;
	}
	public void setOrd(Integer ord) {
		this.ord = ord;
	}
	public Integer getRid() {
		return rid;
	}
	public void setRid(Integer rid) {
		this.rid = rid;
	}
	public Integer getTargetId() {
		return targetId;
	}
	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}
	public String getIsupdate() {
		return isupdate;
	}
	public void setIsupdate(String isupdate) {
		this.isupdate = isupdate;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getFromCol() {
		return fromCol;
	}
	public void setFromCol(String fromCol) {
		this.fromCol = fromCol;
	}
	@Override
	public void validate() {

	}
}
