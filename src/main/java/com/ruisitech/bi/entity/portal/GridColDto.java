/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.List;

public class GridColDto extends BaseEntity {

	private String id;
	private String alias; //字段别名
	private String name;  //字段名称
	private String dispName;  //显示名
	private String tname;
	private String type; //字段类型 string/double/int/datetime 等
	private String expression;
	private String fmt;
	private String align;
	private String sort;
	private Integer sortIndex;  //排序顺序
	private String funcname;
	private String code;
	private String pm; //排名，升序排名/降序排名
	private Integer colwidth;
	private String nodeType; //节点类型， note 表示是合并后的节点, order 表示是排名字段

	private List<GridColDto> children;	//下级节点，用来合并单元格
	private Integer level = 0;  //层级，从0开始，下级是1
	private String aggre; //对指标字段进行聚合操作, 生成新的聚合列
	private String aggreSql;  //对指标字段进行聚合，按SQL操作，比如 sum(a) / sum(b) 这种操作

	private Boolean hideCol;  //开启后，SQL中会查询出此字段，但表格中不会显示。
	private Boolean frontSort; //前端排序（点击表头）
	private Boolean numberCol; //字段为序号字段

	/** 样式相关代码 */
	private Integer colFontSize;
	private String colTextColor;
	private String colfamily;
	private String colBgColor;

	public void checkSql(TableInfoVO tinfo){
		RSBIUtils.processSql(alias);
		//RSBIUtils.processSql(name);
		//RSBIUtils.processSql(expression);
		EtlTableMetaCol col = tinfo.findColByAlias(name);
		if(col == null){
			throw new RuntimeException("字段"+name+"映射错误。");
		}
		expression = col.getExpression();
		name = col.getColName();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getDispName() {
		return dispName;
	}
	public void setDispName(String dispName) {
		this.dispName = dispName;
	}
	public String getFmt() {
		return fmt;
	}
	public void setFmt(String fmt) {
		this.fmt = fmt;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getFuncname() {
		return funcname;
	}
	public void setFuncname(String funcname) {
		this.funcname = funcname;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public void validate() {
		this.name = RSBIUtils.htmlEscape(this.name);
		this.dispName = RSBIUtils.htmlEscape(this.dispName);
	}

	public String getPm() {
		return pm;
	}

	public void setPm(String pm) {
		this.pm = pm;
	}

	public Integer getColwidth() {
		return colwidth;
	}

	public void setColwidth(Integer colwidth) {
		this.colwidth = colwidth;
	}
	public List<GridColDto> getChildren() {
		return children;
	}

	public void setChildren(List<GridColDto> children) {
		this.children = children;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}

	public String getAggre() {
		return aggre;
	}

	public void setAggre(String aggre) {
		this.aggre = aggre;
	}

	public String getAggreSql() {
		return aggreSql;
	}

	public void setAggreSql(String aggreSql) {
		this.aggreSql = aggreSql;
	}

	public Boolean getHideCol() {
		return hideCol;
	}

	public void setHideCol(Boolean hideCol) {
		this.hideCol = hideCol;
	}

	public Boolean getFrontSort() {
		return frontSort;
	}

	public void setFrontSort(Boolean frontSort) {
		this.frontSort = frontSort;
	}

	public Boolean getNumberCol() {
		return numberCol;
	}

	public void setNumberCol(Boolean numberCol) {
		this.numberCol = numberCol;
	}

	public Integer getColFontSize() {
		return colFontSize;
	}

	public void setColFontSize(Integer colFontSize) {
		this.colFontSize = colFontSize;
	}

	public String getColTextColor() {
		return colTextColor;
	}

	public void setColTextColor(String colTextColor) {
		this.colTextColor = colTextColor;
	}

	public String getColfamily() {
		return colfamily;
	}

	public void setColfamily(String colfamily) {
		this.colfamily = colfamily;
	}

	public String getColBgColor() {
		return colBgColor;
	}

	public void setColBgColor(String colBgColor) {
		this.colBgColor = colBgColor;
	}
}
