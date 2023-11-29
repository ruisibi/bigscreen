/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.rsbi.ext.engine.util.IdCreater;
import com.ruisitech.bi.entity.bireport.*;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PortalTableQuery extends CompBaseEntity {

	private String id;
	private String name;
	private String type;

	/**
	 * 所对应的表 id
	 */
	private Integer tid;
	private String tname;
	private String tincome;  //表得来源
	private List<KpiDto> kpiJson;  //所有度量

	//行/列标签
	private List<CrossFieldDto> cols;
	private List<CrossFieldDto> rows;
	private List<DimDto> dims;  //所有维度
	private List<ParamDto> params;
	private LinkAcceptDto linkAccept;
	private Map<String, Object> link;

	private List<PortalParamDto> portalParams;
	private List<CompParamDto> compParams;
	private List<DashboardParamDto> dashboardParam;  //仪表盘中使用的筛选参数(非rest接口)
	private String lockhead;

	private List<Map<String, Object>> drillDim;

	private String useIn; //在仪表盘或是在报表中使用

	private String dashboardStyle;
	private String styleType; //风格类型，白色背景还是黑色背景
	//输出方式,默认未 lockUI
	private String out = "lockUI";
	private String mock; //所使用的模拟数据，模拟数据由程序产生
	private Boolean ranking; //是否给交叉表生成排名字段
	private String colwidth; //单元格宽度，auto表示自动
	private Boolean foldpcdim; //折叠父子维度
	private Boolean mergeRow; //合并行单元格
	private List<CrossHeadDto> rowHeads;
	private GroupDto groupDto;

	/**
	 *检测SQL注入
	 */
	public void checkSql(TableInfoVO info){
		if(dims != null){
			for(DimDto dim : dims){
				dim.checkSql(info.getColALias());
			}
		}
		if(kpiJson != null){
			for(KpiDto kpi : kpiJson){
				kpi.checkSql(info.getColALias());
			}
		}
		if(compParams != null){
			for(CompParamDto p : compParams){
				p.checkSql(info.getColALias());
			}
		}
		if(dashboardParam != null){
			for(DashboardParamDto d : dashboardParam){
				d.checkSql(info.getColALias());
			}
		}
	}

	/**
	 * 查询表格时间类型维度（包括参数）
	 * @return
	 */
	public List<DateDimDto> getDateDims(StringBuffer scripts) {
		List<DateDimDto> ret = new ArrayList<DateDimDto>();
		for(int i=0; compParams!=null&&i<this.compParams.size(); i++) {
			CompParamDto p = this.compParams.get(i);
			String tp = p.getDimType();
			if(p.isDateDim()){
				//把 paramDto 转换成 DimDto
				DateDimDto dim = new DateDimDto();
				dim.setType(tp);
				dim.setDateformat(p.getDateformat());
				dim.setAlias(p.getAlias());
				dim.setIncome(1);
				String usetype = p.getUsetype();
				if("gdz".equals(usetype)) {
					if(p.getVal() != null && p.getVal().indexOf("(") >= 0) {  //固定值是表达式
						String varName =  "dyn" + IdCreater.create();
						String script =  "var v"+ varName + " = " + p.getVal() + "; \n";
						script += " extContext.put('"+varName+"', v"+varName+"); \n";
						scripts.append(script);
						dim.setDyna(true);
						dim.setStart(varName);
						if(p.getVal2() != null && p.getVal2().indexOf("(") >= 0) {
							String varName2 =  "dyn" + IdCreater.create();
							String script2 =  "var v"+ varName2 + " = " + p.getVal2() + "; \n";
							script2 += " extContext.put('"+varName2+"', v"+varName2+"); \n";
							scripts.append(script2);
							dim.setEnd(varName2);
						}else {
							dim.setEnd(varName);
						}
					}else {
						String val1 = p.getVal();
						String val2 = p.getVal2();
						dim.setVals(val1 + (val2!=null&&val2.length()>0 ? ","+val2:""));
					}
				}else {
					//动态值如何处理？
					//动态值在 MasterProcess 的接口中处理。（MasterProcess接口是在主数据集执行后做处理）
					dim.setStart(p.getLinkparam());
					if(p.getLinkparam2() != null && p.getLinkparam2().length() > 0) {
						dim.setEnd(p.getLinkparam2());
					}else {
						dim.setEnd(p.getLinkparam());
					}
					dim.setDyna(true);
				}
				ret.add(dim);
			}
		}
		for(DimDto dim : this.getDims()) {
			String tp = dim.getType();
			if(dim.isDateDim()){
				DateDimDto dto = new DateDimDto();
				dto.setType(tp);
				dto.setAlias(dim.getAlias());
				dto.setVals(RSBIUtils.dealStringParam(dim.getVals(), false));
				dto.setDateformat(dim.getDateformat());
				dto.setIncome(2);
				if("month".equals(tp) && dim.getMonth() != null) {
					dto.setStart(dim.getMonth().getStartMonth());
					dto.setEnd(dim.getMonth().getEndMonth());
				}
				if("day".equals(tp) && dim.getDay() != null) {
					dto.setStart(dim.getDay().getStartDay());
					dto.setEnd(dim.getDay().getEndDay());
				}
				ret.add(dto);
			}
		}

		return ret;
	}

	/**
	 * 获取kpi的计算方式，是计算上期值、还是计算同期值、还是累计或者其他组合
	 *
	 */
	public List<String> getKpiComputeType(){
		List<String> ret = new ArrayList<>();
		boolean sq = false;
		boolean tq = false;
		boolean lj = false;
		boolean zdz = false; //指标计算，比较指定值
		for(KpiDto kpi : kpiJson){
			//指标计算
			String compute = kpi.getCompute();
			if(compute != null && compute.length() > 0){
				String[] jss = compute.split(",");
				for(String js : jss){
					if("sq".equals(js) || "zje".equals(js) || "hb".equals(js)){
						sq = true;
					}else if("tq".equals(js) || "tb".equals(js)){
						tq = true;
					}else if("lj".equals(js)) {
						lj = true;
					}
				}
			}
			//指标  比较指定日期
			if(kpi.getCompareDate() != null){
				zdz = true;
			}
		}
		if(sq) {
			ret.add("sq");
		}
		if(tq) {
			ret.add("tq");
		}
		if(lj) {
			ret.add("lj");
		}
		if(zdz) {
			ret.add("zdz");
		}
		return ret;
	}

	/**
	 * 获取 比较指定日期 计算列表
	 * @return
	 */
	public List<DateCompareDto> getKpiCompareDtos(){
		List<DateCompareDto> ret = new ArrayList<>();
		for(KpiDto kpi : kpiJson){
			DateCompareDto dto = kpi.getCompareDate();
			if(dto != null){
				//判断是否存在
				long c = ret.stream().filter(p->p.getValtype().equals(dto.getValtype()) && p.getParam().equals(dto.getParam())).count();
				if(c == 0L) {
					ret.add(dto);
				}
			}
		}
		return ret;
	}

	/**
	 * 查询表格所有维度， 但如果有层级维度，只取最后一个层级
	 * @return
	 */
	public List<DimDto> getDimsOnlyOneLevel(){
		List<DimDto> ret = new ArrayList<DimDto>();
		DimDto lastPcDim = null; //最后一个层级维度
		for(DimDto col : this.dims) {
			if("y".equals(col.getIspcdim())) {
				if(lastPcDim == null) {
					lastPcDim = col;
				}else {
					if(col.getPclevel() > lastPcDim.getPclevel()) { //level最大的一个是最后层级
						lastPcDim = col;
					}
				}
			}else {
				if(!"rank".equals(col.getType())) {  //过滤排序字段
					ret.add(col);
				}
			}
		}
		if(lastPcDim != null) {
			ret.add(lastPcDim);
		}
		return ret;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getLink() {
		return link;
	}
	public void setLink(Map<String, Object> link) {
		this.link = link;
	}
	public List<PortalParamDto> getPortalParams() {
		return portalParams;
	}
	public void setPortalParams(List<PortalParamDto> portalParams) {
		this.portalParams = portalParams;
	}

	public List<CompParamDto> getCompParams() {
		return compParams;
	}
	public void setCompParams(List<CompParamDto> compParams) {
		this.compParams = compParams;
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

	public List<Map<String, Object>> getDrillDim() {
		return drillDim;
	}
	public void setDrillDim(List<Map<String, Object>> drillDim) {
		this.drillDim = drillDim;
	}
	public String getLockhead() {
		return lockhead;
	}
	public void setLockhead(String lockhead) {
		this.lockhead = lockhead;
	}
	public String getUseIn() {
		return useIn;
	}
	public void setUseIn(String useIn) {
		this.useIn = useIn;
	}
	public String getDashboardStyle() {
		return dashboardStyle;
	}
	public void setDashboardStyle(String dashboardStyle) {
		this.dashboardStyle = dashboardStyle;
	}
	public String getOut() {
		return out;
	}
	public void setOut(String out) {
		this.out = out;
	}
	public String getMock() {
		return mock;
	}
	public void setMock(String mock) {
		this.mock = mock;
	}
	public Boolean getRanking() {
		return ranking;
	}
	public void setRanking(Boolean ranking) {
		this.ranking = ranking;
	}
	public String getColwidth() {
		return colwidth;
	}
	public void setColwidth(String colwidth) {
		this.colwidth = colwidth;
	}
	public Boolean getFoldpcdim() {
		return foldpcdim;
	}
	public void setFoldpcdim(Boolean foldpcdim) {
		this.foldpcdim = foldpcdim;
	}

	public Boolean getMergeRow() {
		return mergeRow;
	}

	public void setMergeRow(Boolean mergeRow) {
		this.mergeRow = mergeRow;
	}

	public List<DashboardParamDto> getDashboardParam() {
		return dashboardParam;
	}

	public void setDashboardParam(List<DashboardParamDto> dashboardParam) {
		this.dashboardParam = dashboardParam;
	}

	public String getStyleType() {
		return styleType;
	}

	public void setStyleType(String styleType) {
		this.styleType = styleType;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getTincome() {
		return tincome;
	}

	public void setTincome(String tincome) {
		this.tincome = tincome;
	}

	public List<KpiDto> getKpiJson() {
		return kpiJson;
	}

	public void setKpiJson(List<KpiDto> kpiJson) {
		this.kpiJson = kpiJson;
	}

	public LinkAcceptDto getLinkAccept() {
		return linkAccept;
	}

	public void setLinkAccept(LinkAcceptDto linkAccept) {
		this.linkAccept = linkAccept;
	}

	public List<DimDto> getDims() {
		return dims;
	}

	public void setDims(List<DimDto> dims) {
		this.dims = dims;
	}

	public List<ParamDto> getParams() {
		return params;
	}

	public void setParams(List<ParamDto> params) {
		this.params = params;
	}

	public List<CrossFieldDto> getCols() {
		return cols;
	}

	public void setCols(List<CrossFieldDto> cols) {
		this.cols = cols;
	}

	public List<CrossFieldDto> getRows() {
		return rows;
	}

	public void setRows(List<CrossFieldDto> rows) {
		this.rows = rows;
	}

	public List<CrossHeadDto> getRowHeads() {
		return rowHeads;
	}

	public void setRowHeads(List<CrossHeadDto> rowHeads) {
		this.rowHeads = rowHeads;
	}

	public GroupDto getGroupDto() {
		return groupDto;
	}

	public void setGroupDto(GroupDto groupDto) {
		this.groupDto = groupDto;
	}
}
