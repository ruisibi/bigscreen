/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.rsbi.ext.engine.util.IdCreater;
import com.rsbi.ext.engine.view.context.chart.SeriesLabelContext;
import com.ruisitech.bi.entity.bireport.*;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalChartQuery extends ChartQueryDto {

	private List<PortalParamDto> portalParams;
	private List<CompParamDto> compParams;
	private List<DashboardParamDto> dashboardParam;  //仪表盘中使用的筛选参数(非rest接口)
	private String id;
	private String name;
	private String type;
	private String styleType; //风格类型，白色背景还是黑色背景
	private String mock; //所使用的模拟数据，模拟数据由程序产生
	private Integer height; //图形高度，用在导出html中

	private Map<String, String> colors;  //系列颜色, 当只有一个系列时，只有配置 Map 的 value 即可。 key 为系列名称， val 为系列值

	private String useIn; //在仪表盘或是在报表中使用

	private Map<String, Object> style = new HashMap<String, Object>();

	private Map<String, SeriesLabelContext> chartLabels; //系列label （文本标签）

	public List<PortalParamDto> getPortalParams() {
		return portalParams;
	}
	public void setPortalParams(List<PortalParamDto> portalParams) {
		this.portalParams = portalParams;
	}

	/**
	 *检测SQL注入
	 */
	public void checkSql(TableInfoVO tinfo){
		if(getChartJson().getXcol() != null){
			getChartJson().getXcol().checkSql(tinfo.getColALias());

		}
		if(getChartJson().getScol() != null){
			getChartJson().getScol().checkSql(tinfo.getColALias());
		}
		if(getChartJson().getYcol() != null){
			getChartJson().getYcol().checkSql(tinfo.getColALias());
		}
		if(getKpiJson() != null){
			for(KpiDto kpi : getKpiJson()){
				kpi.checkSql(tinfo.getColALias());
			}
		}
		if(getMkpiJson() != null){
			for(KpiDto kpi : getMkpiJson()){
				kpi.checkSql(tinfo.getColALias());
			}
		}
		if(compParams != null){
			for(CompParamDto p : compParams){
				p.checkSql(tinfo.getColALias());
			}
		}
		if(dashboardParam != null){
			for(DashboardParamDto d : dashboardParam){
				d.checkSql(tinfo.getColALias());
			}
		}
	}

	/**
	 * 获取 比较指定日期 计算列表
	 * @return
	 */
	public List<DateCompareDto> getKpiCompareDtos(){
		List<DateCompareDto> ret = new ArrayList<>();
		for(KpiDto kpi : super.getMkpi() != null && super.getMkpi() ? super.getMkpiJson() : super.getKpiJson()){
			DateCompareDto dto = kpi.getCompareDate();
			if(dto != null){
				//判断是否存在
				long cnt = ret.stream().filter(m->m.getValtype().equals(dto.getValtype()) && m.getParam().equals(dto.getParam())).count();
				if(cnt == 0) {
					ret.add(dto);
				}
			}
		}
		return ret;
	}

	public List<DateDimDto> getDateDims(StringBuffer sb) {
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
				String usetype = p.getUsetype();
				if("gdz".equals(usetype)) {
					if(p.getVal() != null && p.getVal().indexOf("(") >= 0) {  //固定值是表达式
						String varName =  "dyn" + IdCreater.create();
						String script =  "var v"+ varName + " = " + p.getVal() + "; \n";
						script += " extContext.put('"+varName+"', v"+varName+"); \n";
						sb.append(script);
						dim.setDyna(true);
						dim.setStart(varName);
						if(p.getVal2() != null && p.getVal2().indexOf("(") >= 0) {
							String varName2 =  "dyn" + IdCreater.create();
							String script2 =  "var v"+ varName2 + " = " + p.getVal2() + "; \n";
							script2 += " extContext.put('"+varName2+"', v"+varName2+"); \n";
							sb.append(script2);
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
		ChartJSONDto chartJson = super.getChartJson();
		if(chartJson.getXcol() != null && chartJson.getXcol().getId() != null) {
			DimDto dim = chartJson.getXcol();
			String tp = dim.getType();
			if(dim.isDateDim()){
				DateDimDto dto = new DateDimDto();
				dto.setType(tp);
				dto.setAlias(dim.getAlias());
				dto.setVals(RSBIUtils.dealStringParam(dim.getVals(), false));
				dto.setDateformat(dim.getDateformat());
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
		if(chartJson.getScol() != null && chartJson.getScol().getId() != null) {
			DimDto dim = chartJson.getScol();
			String tp = dim.getType();
			if(dim.isDateDim()){
				DateDimDto dto = new DateDimDto();
				dto.setType(tp);
				dto.setAlias(dim.getAlias());
				dto.setVals(RSBIUtils.dealStringParam(dim.getVals(), false));
				dto.setDateformat(dim.getDateformat());
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



	public List<CompParamDto> getCompParams() {
		return compParams;
	}
	public void setCompParams(List<CompParamDto> compParams) {
		this.compParams = compParams;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, Object> getStyle() {
		return style;
	}
	public void setStyle(Map<String, Object> style) {
		this.style = style;
	}

	public Map<String, String> getColors() {
		return colors;
	}

	public void setColors(Map<String, String> colors) {
		this.colors = colors;
	}

	public String getUseIn() {
		return useIn;
	}
	public void setUseIn(String useIn) {
		this.useIn = useIn;
	}
	public String getMock() {
		return mock;
	}
	public void setMock(String mock) {
		this.mock = mock;
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

	public Map<String, SeriesLabelContext> getChartLabels() {
		return chartLabels;
	}

	public void setChartLabels(Map<String, SeriesLabelContext> chartLabels) {
		this.chartLabels = chartLabels;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
}
