/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.bireport.KpiDto;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.List;

public class BoxQuery extends CompBaseEntity {

	private String id;
	private String type;
	private String name;
	private Integer tid;
	private String tname;
	private String tincome;  //表得来源
	private Integer height;
	private KpiDto kpiJson;
	private List<PortalParamDto> portalParams;  //页面的参数， 一般需要把页面参数和组件参数关联起来
	private List<CompParamDto> compParams;   //组件的参数
	private List<DashboardParamDto> dashboardParam;  //仪表盘中使用的筛选参数(非rest接口)
	private String useIn; //在仪表盘或是在报表中使用
	private String mock;
	private String thbMock;  //显示同环比的模拟数据
	private DimDto thbDim; //用来显示同环比的时间维度
	private LinkAcceptDto linkAccept;  //事件接收对象
	private Boolean useArrow; //使用箭头

	private PortalChartQuery chart;  //给box添加一个图形

	private Boolean progressBar; //是否以进度条方式展现

	private String progressBarColor; //进度条颜色

	/**
	 *检测SQL注入
	 */
	public void checkSql(TableInfoVO tinfo){
		if(kpiJson != null){
			kpiJson.checkSql(tinfo.getColALias());
		}
		if(linkAccept != null){
			linkAccept.checkSql(tinfo.getCols());
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
		if(chart != null){
			chart.checkSql(tinfo);
		}
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public KpiDto getKpiJson() {
		return kpiJson;
	}
	public void setKpiJson(KpiDto kpiJson) {
		this.kpiJson = kpiJson;
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
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getUseIn() {
		return useIn;
	}
	public void setUseIn(String useIn) {
		this.useIn = useIn;
	}

	@Override
	public void validate() {
		this.name = RSBIUtils.htmlEscape(this.name);
	}
	public String getMock() {
		return mock;
	}
	public void setMock(String mock) {
		this.mock = mock;
	}
	public PortalChartQuery getChart() {
		return chart;
	}
	public void setChart(PortalChartQuery chart) {
		this.chart = chart;
	}
	public Boolean getProgressBar() {
		return progressBar;
	}
	public void setProgressBar(Boolean progressBar) {
		this.progressBar = progressBar;
	}
	public DimDto getThbDim() {
		return thbDim;
	}
	public void setThbDim(DimDto thbDim) {
		this.thbDim = thbDim;
	}
	public String getThbMock() {
		return thbMock;
	}
	public void setThbMock(String thbMock) {
		this.thbMock = thbMock;
	}
	public String getProgressBarColor() {
		return progressBarColor;
	}
	public void setProgressBarColor(String progressBarColor) {
		this.progressBarColor = progressBarColor;
	}
	public LinkAcceptDto getLinkAccept() {
		return linkAccept;
	}
	public void setLinkAccept(LinkAcceptDto linkAccept) {
		this.linkAccept = linkAccept;
	}

	public Boolean getUseArrow() {
		return useArrow;
	}

	public void setUseArrow(Boolean useArrow) {
		this.useArrow = useArrow;
	}

	public String getTincome() {
		return tincome;
	}

	public void setTincome(String tincome) {
		this.tincome = tincome;
	}

	public List<DashboardParamDto> getDashboardParam() {
		return dashboardParam;
	}

	public void setDashboardParam(List<DashboardParamDto> dashboardParam) {
		this.dashboardParam = dashboardParam;
	}

}
