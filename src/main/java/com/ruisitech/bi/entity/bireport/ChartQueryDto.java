/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.portal.CompBaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.ArrayList;
import java.util.List;

public class ChartQueryDto extends CompBaseEntity {

	private String id;
	private Integer tid; //所属表id
	private String tname;
	private String tincome;  //表得来源
	private Boolean mkpi; //是否多指标查询

	private List<KpiDto> kpiJson;
	private List<KpiDto> mkpiJson;  //多指标查询的指标集合

	private ChartJSONDto chartJson;

	private List<ParamDto> params;  //组件参数

	/**
	 *检测SQL注入
	 */
	public void checkSql(TableInfoVO tinfo){
		if(chartJson.getXcol() != null){
			chartJson.getXcol().checkSql(tinfo.getColALias());
		}
		if(chartJson.getScol() != null){
			chartJson.getScol().checkSql(tinfo.getColALias());
		}
		if(chartJson.getYcol() != null){
			chartJson.getYcol().checkSql(tinfo.getColALias());
		}
		if(chartJson.getLinkAccept() != null){
			chartJson.getLinkAccept().checkSql(tinfo.getCols());
		}
		if(kpiJson != null){
			for(KpiDto kpi : kpiJson){
				kpi.checkSql(tinfo.getColALias());
			}
		}
		if(params != null){
			for(ParamDto p : params){
				p.checkSql(null, tinfo.getColALias());
			}
		}
	}

	/**
	 * 获取kpi的计算方式，是计算上期值、还是计算同期值、还是累计或者其他组合
	 *
	 */
	public List<String> getKpiComputeType(){
		List<String> ret = new ArrayList<String>();
		boolean sq = false;
		boolean tq = false;
		boolean lj = false;
		boolean zdz = false; //指标计算，比较指定值
		for(KpiDto kpi : mkpi != null && mkpi == true ? mkpiJson : kpiJson){
			if(kpi == null || (kpi.getAlias() == null || kpi.getAlias().length() == 0) ) {
				continue;
			}
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

	public List<DimDto> getDims(){
		List<DimDto> ret = new ArrayList<DimDto>();
		if(chartJson.getXcol() != null && chartJson.getXcol().getId() != null) {
			ret.add(chartJson.getXcol());
		}
		if(chartJson.getScol() != null && chartJson.getScol().getId() != null) {
			ret.add(chartJson.getScol());
		}
		return ret;
	}

	public List<DateDimDto> getDateDims(StringBuffer sb) {
		List<DateDimDto> ret = new ArrayList<DateDimDto>();
		if(chartJson.getXcol() != null && chartJson.getXcol().getId() != null) {
			DimDto dim = chartJson.getXcol();
			String tp = dim.getType();
			if("year".equals(tp) || "quarter".equals(tp) || "month".equals(tp) || "day".equals(tp)){
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
			if("year".equals(tp) || "quarter".equals(tp) || "month".equals(tp) || "day".equals(tp)){
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

		/**
		for(ParamDto p : this.params) {
			String tp = p.getType();
			if("year".equals(tp) || "quarter".equals(tp) || "month".equals(tp) || "day".equals(tp)){
				DateDimDto dto = new DateDimDto();
				dto.setType(tp);
				dto.setAlias(p.getAlias());
				dto.setStart(p.getSt());
				dto.setEnd(p.getEnd());
				dto.setVals(p.getVals());
				dto.setDateformat(p.getDateformat());
				ret.add(dto);
			}
		}
		**/
		return ret;
	}

	public List<KpiDto> getKpiJson() {
		return kpiJson;
	}
	public void setKpiJson(List<KpiDto> kpiJson) {
		//过滤 null 内容
		List<KpiDto> kpis = new ArrayList<>();
		for(KpiDto k : kpiJson){
			if(k != null){
				kpis.add(k);
			}
		}
		this.kpiJson = kpis;
	}
	public ChartJSONDto getChartJson() {
		return chartJson;
	}
	public void setChartJson(ChartJSONDto chartJson) {
		this.chartJson = chartJson;
	}
	public List<ParamDto> getParams() {
		return params;
	}
	public void setParams(List<ParamDto> params) {
		this.params = params;
	}

	public Boolean getMkpi() {
		if(mkpi == null){
			return false;
		}else{
			return mkpi;
		}
	}
	public void setMkpi(Boolean mkpi) {
		this.mkpi = mkpi;
	}
	public List<KpiDto> getMkpiJson() {
		return mkpiJson;
	}
	public void setMkpiJson(List<KpiDto> mkpiJson) {
		this.mkpiJson = mkpiJson;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	@Override
	public void validate() {

	}

	public String getTincome() {
		return tincome;
	}

	public void setTincome(String tincome) {
		this.tincome = tincome;
	}
}
