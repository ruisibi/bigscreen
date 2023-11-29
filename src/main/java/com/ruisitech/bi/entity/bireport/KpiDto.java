/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.Map;

public class KpiDto extends BaseEntity {

	private String aggre;
	private String col_name;
	private String fmt;
	private String alias;
	private String kpi_name;  //指标的显示名
	private String ydispName;
	private String tname; //指标所在表名称
	private String descKey; //指标解释KEY
	private Integer rate; //指标倍率
	private String unit; //指标单位
	private Integer kpi_id; //指标ID
	private String sort; //指标排序方式，用在SQL中
	private Boolean order; //客户端排序
	private Double min; //y轴最小值
	private Double max; //Y轴最大值，用在仪表盘中

	private Integer calc;  //是否计算指标

	private KpiFilterDto filter; //对指标进行过滤
	private Map<String, KpiFilterDto> extFilters;  //对计算指标进行过滤,计算指标可以是多个

	private Map<String, Object> style; //指标样式
	private Map<String, Object> warning;  //指标预警
	private String compute; //指标计算方式（同比、环比、占比、排名等计算）
	private DateCompareDto compareDate;  // 指标计算 - 比较指定日期 对象

	private String funcname;  //回调函数名称
	private String code;  //回调函数内容
	private Boolean mergeData; //合并第二纵轴的数据
	private Integer tid;

	/**用在数据查询中内容开始**/
	private String expression; //指标是表达式
	private String valType; //字段类型
	/**用在数据查询中结束**/

	/**
	 * 指标是否是一个表达式，用在数据查询中
	 * y/n
	 */
	private String dyna;
	/**
	 * cube的字段来源表的哪个字段
	 */
	private String fromCol;
	private String img; //指标图片，用在multiBox中

	//用在指标排序中， 表示指标的上级维度的Id， Value
	private String parentId;
	private String parentValue;

	//指标分组，用在数据块中，设置衍生指标
	private String kpiGroup;
	//指标是否是衍生指标，在数据块中使用
	private Boolean extKpi;

	private String kpiPostion;  //指标位置 y轴， y2轴 只在多指标中起作用
	private String kpiShape;	//显示方式，曲线/柱子 只在多指标中起作用

	private Integer colwidth; //表头宽度

	private String computeSort; //当对计算进行排序时，用来排序的计算方式
	private Boolean hideLine;  //是否隐藏曲线图中某条曲线,只显示点
	private Boolean hideNode; //隐藏指标节点(用在指标定制)
	private String hideNodeCallback;  //隐藏指标节点的回调函数（用在度量属性的设置）

	private String distinctCol; //去重字段

	public void checkSql(Map<String, String> colAlias){
		RSBIUtils.processSql(alias);
		//RSBIUtils.processSql(col_name);
		//RSBIUtils.processSql(fromCol);
		col_name = colAlias.get(alias);
		if (alias != null && col_name == null) {
			throw new RuntimeException("字段" + alias + "("+kpi_name+")" + "映射错误");
		}
	}

	public String getAggre() {
		return aggre;
	}
	public void setAggre(String aggre) {
		this.aggre = aggre;
	}
	public String getFmt() {
		return fmt;
	}
	public void setFmt(String fmt) {
		this.fmt = fmt;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getYdispName() {
		return ydispName;
	}
	public void setYdispName(String ydispName) {
		this.ydispName = ydispName;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getDescKey() {
		return descKey;
	}
	public void setDescKey(String descKey) {
		this.descKey = descKey;
	}
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}

	public Boolean getOrder() {
		return order;
	}

	public void setOrder(Boolean order) {
		this.order = order;
	}

	public Integer getCalc() {
		return calc;
	}
	public void setCalc(Integer calc) {
		this.calc = calc;
	}
	public KpiFilterDto getFilter() {
		return filter;
	}
	public void setFilter(KpiFilterDto filter) {
		this.filter = filter;
	}

	public Map<String, Object> getStyle() {
		return style;
	}
	public void setStyle(Map<String, Object> style) {
		this.style = style;
	}

	public Map<String, Object> getWarning() {
		return warning;
	}
	public void setWarning(Map<String, Object> warning) {
		this.warning = warning;
	}
	public String getCompute() {
		return compute;
	}
	public void setCompute(String compute) {
		this.compute = compute;
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
	public Boolean getMergeData() {
		return mergeData;
	}
	public void setMergeData(Boolean mergeData) {
		this.mergeData = mergeData;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public String getKpi_name() {
		return kpi_name;
	}
	public void setKpi_name(String kpi_name) {
		this.kpi_name = kpi_name;
	}
	public Integer getKpi_id() {
		return kpi_id;
	}
	public void setKpi_id(Integer kpi_id) {
		this.kpi_id = kpi_id;
	}
	public String getCol_name() {
		return col_name;
	}
	public void setCol_name(String col_name) {
		this.col_name = col_name;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getDyna() {
		return dyna;
	}
	public void setDyna(String dyna) {
		this.dyna = dyna;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getValType() {
		return valType;
	}
	public void setValType(String valType) {
		this.valType = valType;
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
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getParentValue() {
		return parentValue;
	}
	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}

	public String getKpiGroup() {
		return kpiGroup;
	}

	public void setKpiGroup(String kpiGroup) {
		this.kpiGroup = kpiGroup;
	}

	public Boolean getExtKpi() {
		return extKpi;
	}

	public void setExtKpi(Boolean extKpi) {
		this.extKpi = extKpi;
	}

	public String getKpiPostion() {
		return kpiPostion;
	}

	public void setKpiPostion(String kpiPostion) {
		this.kpiPostion = kpiPostion;
	}

	public String getKpiShape() {
		return kpiShape;
	}

	public void setKpiShape(String kpiShape) {
		this.kpiShape = kpiShape;
	}

	public String getComputeSort() {
		return computeSort;
	}

	public void setComputeSort(String computeSort) {
		this.computeSort = computeSort;
	}

	public Map<String, KpiFilterDto> getExtFilters() {
		return extFilters;
	}

	public void setExtFilters(Map<String, KpiFilterDto> extFilters) {
		this.extFilters = extFilters;
	}

	public Integer getColwidth() {
		return colwidth;
	}

	public void setColwidth(Integer colwidth) {
		this.colwidth = colwidth;
	}

	public Boolean getHideLine() {
		return hideLine;
	}

	public void setHideLine(Boolean hideLine) {
		this.hideLine = hideLine;
	}

	public Boolean getHideNode() {
		return hideNode;
	}

	public void setHideNode(Boolean hideNode) {
		this.hideNode = hideNode;
	}

	public DateCompareDto getCompareDate() {
		return compareDate;
	}

	public void setCompareDate(DateCompareDto compareDate) {
		this.compareDate = compareDate;
	}

	public String getHideNodeCallback() {
		return hideNodeCallback;
	}

	public void setHideNodeCallback(String hideNodeCallback) {
		this.hideNodeCallback = hideNodeCallback;
	}

	public String getDistinctCol() {
		return distinctCol;
	}

	public void setDistinctCol(String distinctCol) {
		this.distinctCol = distinctCol;
	}
}
