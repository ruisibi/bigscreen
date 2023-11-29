/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.List;
import java.util.Map;

public class DimDto extends BaseEntity implements Cloneable  {

	private Integer id;
	private String type; //维度类型，frd/month/year/day/quarter/area/city等
	private String colname; //码表在事实表中对应的字段名
	private String alias; //别名
	private List<String> vals; //码表的限制维
	private List<String> valDesc; //码表限制维的名称
	private String issum; //y,n两值
	private Integer tid;
	private String tname; //维度所在表name
	private Integer calc;  //是否计算列
	private String tableName; //维度码表表名
	private String tableColKey; //码表表KEY字段
	private String tableColName; //码表表name字段

	private String dimord; //维度排序方式
	private String ordcol; //维度排序字段
	private String dimdesc; //维度名称
	private String dimDisp; //维度显示名称, 这个值会覆盖 dimdesc 值
	private String valType; //维度value 字段的类型，用在拼接sql中，判断是否增加单引号

	private String dimpos; //维度所在位置，行维度还是列维度
	private String pos; //col还是row, 用在图形中表示钻取维度的来源
	private String dateformat; //如果是时间维度，设置时间类型
	private String grouptype;  //维度分组
	private String groupname;  //维度分组名称
	private String iscas;
	private Integer top;
	private String topType;
	private String aggre;
	private Integer filtertype;
	private String st;
	private String end;
	private String xdispName;
	private String tickInterval;
	private String routeXaxisLable;
	/**用在数据查询中内容开始**/
	private String dyna; //是否是表达式，y/n,用在数据查询中
	private String expression; //维度是表达式
	/**用在数据查询中结束**/
	private String fromCol;
	/**在父子维度中使用*/
	private String ispcdim; //是否父子维度 y/n
	private String pcId; //父子维度主键
	private String pcPid; //父子维度父键
	private Integer pcdimlevel; //父子维度层级
	private String levelCol;  //层级字段
	private Integer pclevel; //当前层级（父子维度中使用）

	private Integer colwidth; //表头宽度
	private Boolean multi = true; //是否扩展维度
	private String hideNodeCallback;  //隐藏维度的回调函数

	private String oper;
	private Integer value1;
	private Integer value2;

	@JsonIgnore
	private QueryDayDto day;
	@JsonIgnore
	private QueryMonthDto month;
	@JsonIgnore
	private QueryWeekDto week;
	/**
	 * 检测SQL注入
	 */
	public void checkSql(Map<String, String> aliasCache){
		RSBIUtils.processSql(alias);
		//RSBIUtils.processSql(fromCol);
		RSBIUtils.processSql(tableColKey);
		RSBIUtils.processSql(tableColName);
		RSBIUtils.processSql(levelCol);
		RSBIUtils.processSql(pcPid);
		if("y".equals(ispcdim)){  //父子维度特殊处理
			String nalias = alias.substring(0, alias.length() - 2);
			colname = aliasCache.get(nalias);
		}else {
			colname = aliasCache.get(alias);
		}
		if (alias != null && colname == null) {
			throw new RuntimeException("字段" + alias + "(" + this.dimdesc + ")" + "映射错误");
		}
	}

	public String getOrdcol() {
		return ordcol;
	}
	public void setOrdcol(String ordcol) {
		this.ordcol = ordcol;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColname() {
		return colname;
	}
	public void setColname(String colname) {
		this.colname = colname;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getIssum() {
		return issum;
	}
	public void setIssum(String issum) {
		this.issum = issum;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public Integer getCalc() {
		return calc;
	}
	public void setCalc(Integer calc) {
		this.calc = calc;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableColKey() {
		return tableColKey;
	}
	public void setTableColKey(String tableColKey) {
		this.tableColKey = tableColKey;
	}
	public String getTableColName() {
		return tableColName;
	}
	public void setTableColName(String tableColName) {
		this.tableColName = tableColName;
	}
	public String getDimord() {
		return dimord;
	}
	public void setDimord(String dimord) {
		this.dimord = dimord;
	}
	public String getDimdesc() {
		return dimdesc;
	}
	public void setDimdesc(String dimdesc) {
		this.dimdesc = dimdesc;
	}
	public String getValType() {
		return valType;
	}
	public void setValType(String valType) {
		this.valType = valType;
	}
	public String getDimpos() {
		return dimpos;
	}
	public void setDimpos(String dimpos) {
		this.dimpos = dimpos;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getDateformat() {
		return dateformat;
	}
	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}
	public String getGrouptype() {
		return grouptype;
	}
	public void setGrouptype(String grouptype) {
		this.grouptype = grouptype;
	}
	public String getIscas() {
		return iscas;
	}
	public void setIscas(String iscas) {
		this.iscas = iscas;
	}
	public QueryDayDto getDay() {
		if(day == null){
			if("day".equals(type) && st != null && st.length() > 0 && end != null && end.length() > 0){
				day = new QueryDayDto();
				day.setStartDay(this.st);
				day.setEndDay(this.end);
			}
		}
		return day;
	}
	public void setDay(QueryDayDto day) {
		this.day = day;
	}
	public QueryMonthDto getMonth() {
		if(month == null){
			if("month".equals(type) && st != null && st.length() > 0 && end != null && end.length() > 0){
				month = new QueryMonthDto();
				month.setStartMonth(st);
				month.setEndMonth(end);
			}
		}
		return month;
	}
	public void setMonth(QueryMonthDto month) {
		this.month = month;
	}

	public QueryWeekDto getWeek() {
		if(week == null){
			if("week".equals(type) && st != null && st.length() > 0 && end != null && end.length() > 0){
				week = new QueryWeekDto();
				week.setStartWeek(st);
				week.setEndWeek(end);
			}
		}
		return week;
	}

	public void setWeek(QueryWeekDto week) {
		this.week = week;
	}

	public Integer getTop() {
		if(top == null){
			return 500; //默认返回TOP 500
		}
		return top;
	}
	public void setTop(Integer top) {
		this.top = top;
	}
	public String getTopType() {
		if(topType == null || topType.length() == 0){  //默认类型为 number
			return "number";
		}
		return topType;
	}
	public void setTopType(String topType) {
		this.topType = topType;
	}
	public String getAggre() {
		return aggre;
	}
	public void setAggre(String aggre) {
		this.aggre = aggre;
	}
	public Integer getFiltertype() {
		return filtertype;
	}
	public void setFiltertype(Integer filtertype) {
		this.filtertype = filtertype;
	}
	public String getXdispName() {
		return xdispName;
	}
	public void setXdispName(String xdispName) {
		this.xdispName = xdispName;
	}
	public String getTickInterval() {
		return tickInterval;
	}
	public void setTickInterval(String tickInterval) {
		this.tickInterval = tickInterval;
	}
	public String getRouteXaxisLable() {
		return routeXaxisLable;
	}
	public void setRouteXaxisLable(String routeXaxisLable) {
		this.routeXaxisLable = routeXaxisLable;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
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
	public String getFromCol() {
		return fromCol;
	}
	public void setFromCol(String fromCol) {
		this.fromCol = fromCol;
	}
	@Override
	public void validate() {

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
	public Integer getPclevel() {
		return pclevel;
	}
	public void setPclevel(Integer pclevel) {
		this.pclevel = pclevel;
	}
	public String getDimDisp() {
		return dimDisp;
	}
	public void setDimDisp(String dimDisp) {
		this.dimDisp = dimDisp;
	}
	public Integer getColwidth() {
		return colwidth;
	}
	public void setColwidth(Integer colwidth) {
		this.colwidth = colwidth;
	}
	@Override
	public DimDto clone() throws CloneNotSupportedException {
		return (DimDto)super.clone();
	}

	public boolean isDateDim(){
		String tp = this.getType();
		if("year".equals(tp) || "quarter".equals(tp) || "month".equals(tp) || "day".equals(tp) ||
			"week".equals(tp) || "halfyear".equals(tp)){
			return true;
		}else{
			return false;
		}
	}

	public Boolean getMulti() {
		return multi;
	}

	public void setMulti(Boolean multi) {
		this.multi = multi;
	}

	public List<String> getVals() {
		return vals;
	}

	public void setVals(List<String> vals) {
		this.vals = vals;
	}

	public List<String> getValDesc() {
		return valDesc;
	}

	public void setValDesc(List<String> valDesc) {
		this.valDesc = valDesc;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getHideNodeCallback() {
		return hideNodeCallback;
	}

	public void setHideNodeCallback(String hideNodeCallback) {
		this.hideNodeCallback = hideNodeCallback;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public Integer getValue1() {
		return value1;
	}

	public void setValue1(Integer value1) {
		this.value1 = value1;
	}

	public Integer getValue2() {
		return value2;
	}

	public void setValue2(Integer value2) {
		this.value2 = value2;
	}
}
