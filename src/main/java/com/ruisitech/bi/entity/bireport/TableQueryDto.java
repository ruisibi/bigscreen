/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruisitech.bi.entity.portal.CompBaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.ArrayList;
import java.util.List;

public class TableQueryDto extends CompBaseEntity {

	private String id;

	/**
	 * 所对应的表 id
	 */
	private Integer tid;
	private String tname;
	private String tincome;  //表得来源
	private List<KpiDto> kpiJson;
	private List<DimDto> cols;
	private List<DimDto> rows;
	private List<ParamDto> params;

	/**
	 *检测SQL注入
	 */
	public void checkSql(TableInfoVO tinfo){
		if(cols != null){
			for(DimDto dim : cols){
				dim.checkSql(tinfo.getColALias());
			}
		}
		if(rows != null){
			for(DimDto dim : rows){
				dim.checkSql(tinfo.getColALias());
			}
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

	public List<KpiDto> getKpiJson() {
		return kpiJson;
	}
	public void setKpiJson(List<KpiDto> kpiJson) {
		this.kpiJson = kpiJson;
	}
	public List<DimDto> getCols() {
		return cols;
	}
	public void setCols(List<DimDto> cols) {
		this.cols = cols;
	}
	public List<DimDto> getRows() {
		return rows;
	}
	public void setRows(List<DimDto> rows) {
		this.rows = rows;
	}
	public List<ParamDto> getParams() {
		return params;
	}
	public void setParams(List<ParamDto> params) {
		this.params = params;
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
		for(KpiDto kpi : kpiJson){
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
		return ret;
	}

	/**
	 * 查询表格所有维度
	 * @return
	 */
	@JsonIgnore
	public List<DimDto> getDims() {
		List<DimDto> ret = new ArrayList<DimDto>();
		ret.addAll(this.cols);
		ret.addAll(this.rows);
		return ret;
	}

	/**
	 * 查询表格时间类型维度（包括参数）
	 * @return
	 */
	@JsonIgnore
	public List<DateDimDto> getDateDims(StringBuffer scripts) {
		List<DateDimDto> ret = new ArrayList<DateDimDto>();
		for(DimDto dim : cols) {
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
				if("week".equals(tp) && dim.getWeek() != null){
					dto.setStart(dim.getWeek().getStartWeek());
					dto.setEnd(dim.getWeek().getEndWeek());
				}
				if("day".equals(tp) && dim.getDay() != null) {
					dto.setStart(dim.getDay().getStartDay());
					dto.setEnd(dim.getDay().getEndDay());
				}
				ret.add(dto);
			}
		}
		for(DimDto dim : rows) {
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
		for(ParamDto p : this.params) {
			String tp = p.getType();
			if(p.isDateDim()){
				DateDimDto dto = new DateDimDto();
				dto.setType(tp);
				dto.setAlias(p.getAlias());
				dto.setStart(p.getSt());
				dto.setEnd(p.getEnd());
				dto.setVals(RSBIUtils.dealStringParam(p.getVals(), false));
				dto.setDateformat(p.getDateformat());
				ret.add(dto);
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
		for(DimDto col : this.cols) {
			if("y".equals(col.getIspcdim())) {
				if(lastPcDim == null) {
					lastPcDim = col;
				}else {
					if(col.getPclevel() > lastPcDim.getPclevel()) { //level最大的一个是最后层级
						lastPcDim = col;
					}
				}
			}else {
				ret.add(col);
			}
		}

		for(DimDto row : this.rows) {
			if("y".equals(row.getIspcdim())) {
				if(lastPcDim == null) {
					lastPcDim = row;
				}else {
					if(row.getPclevel() > lastPcDim.getPclevel()) { //level最大的一个是最后层级
						lastPcDim = row;
					}
				}
			}else {
				ret.add(row);
			}
		}
		if(lastPcDim != null) {
			ret.add(lastPcDim);
		}
		return ret;
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
