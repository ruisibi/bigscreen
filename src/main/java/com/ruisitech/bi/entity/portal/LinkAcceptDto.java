/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.List;
import java.util.Map;

/**
 * 事件接收(对应数据表的一个字段)
 * @author hq
 *
 */
public class LinkAcceptDto extends BaseEntity {

	private Integer id;
	//对应字段
	private String col;
	//表达式
	private String expression;
	private String dftval;
	private String valType;
	private String compId; //组件ID

	/**
	 * 检测SQL注入
	 */
	public void checkSql(List<EtlTableMetaCol> cols){
		RSBIUtils.processSql(col);
		RSBIUtils.processSql(dftval);
		for(EtlTableMetaCol c : cols){
			if(c.getColName().equals(col)){
				expression = c.getExpression();
			}
		}
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}

	public String getDftval() {
		return dftval;
	}
	public void setDftval(String dftval) {
		this.dftval = dftval;
	}
	public String getValType() {
		return valType;
	}
	public void setValType(String valType) {
		this.valType = valType;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getCompId() {
		return compId;
	}

	public void setCompId(String compId) {
		this.compId = compId;
	}

	@Override
	public void validate() {

	}
}
