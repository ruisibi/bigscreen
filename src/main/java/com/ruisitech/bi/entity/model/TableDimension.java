/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.model;

/**
 * 维度表，对应  olap_dim_list
 * @author hq
 *
 */
public class TableDimension extends TableMetaCol {

	private Integer dimId;
	private String name;
	private String type;
	private String colkey;
	private String coltext;
	private String dimord;
	private String vtype;
	private String ordcol;
	private Integer tid;
	/**
	 * 如果目标表示一段SQL, tsql即为该SQL
	 */
	private String tsql;
	private String groupId;
	private String groupName;
	private String dateformat;
	private String ispcdim; //是否父子维度 y/n
	private String pcId; //父子维度主键
	private String pcPid; //父子维度父键
	private Integer pcdimlevel; //父子维度层级
	private String levelCol;  //层级字段
	private Integer pcLevel; //当前层级

	public Integer getDimId() {
		return dimId;
	}
	public void setDimId(Integer dimId) {
		this.dimId = dimId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColkey() {
		return colkey;
	}
	public void setColkey(String colkey) {
		this.colkey = colkey;
	}
	public String getColtext() {
		return coltext;
	}
	public void setColtext(String coltext) {
		this.coltext = coltext;
	}
	public String getDimord() {
		return dimord;
	}
	public void setDimord(String dimord) {
		this.dimord = dimord;
	}
	public String getVtype() {
		return vtype;
	}
	public void setVtype(String vtype) {
		this.vtype = vtype;
	}
	public String getOrdcol() {
		return ordcol;
	}
	public void setOrdcol(String ordcol) {
		this.ordcol = ordcol;
	}

	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getDateformat() {
		return dateformat;
	}
	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getTsql() {
		return tsql;
	}
	public void setTsql(String tsql) {
		this.tsql = tsql;
	}
	public String getIspcdim() {
		return ispcdim;
	}
	public void setIspcdim(String ispcdim) {
		this.ispcdim = ispcdim;
	}
	public String getPcId() {
		return pcId;
	}
	public void setPcId(String pcId) {
		this.pcId = pcId;
	}
	public String getPcPid() {
		return pcPid;
	}
	public void setPcPid(String pcPid) {
		this.pcPid = pcPid;
	}
	public Integer getPcdimlevel() {
		return pcdimlevel;
	}
	public void setPcdimlevel(Integer pcdimlevel) {
		this.pcdimlevel = pcdimlevel;
	}
	public String getLevelCol() {
		return levelCol;
	}
	public void setLevelCol(String levelCol) {
		this.levelCol = levelCol;
	}
	public Integer getPcLevel() {
		return pcLevel;
	}
	public void setPcLevel(Integer pcLevel) {
		this.pcLevel = pcLevel;
	}

}
