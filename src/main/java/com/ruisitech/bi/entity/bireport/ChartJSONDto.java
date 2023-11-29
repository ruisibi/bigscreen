/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.portal.LinkAcceptDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartJSONDto {

	private String type;
	private Integer typeIndex;
	private String shape; //当 type == 'custom' 时，需要用 shape 来指定图形的类型，映射 ext-config.xml 中的 chart 配置
	private List<DimDto> params;
	private String label;
	private DimDto xcol;
	private DimDto scol;
	private DimDto ycol; //"ycol":{"type":"kpi"}
	private Map<String, Object> link;
	private LinkAcceptDto linkAccept;

	private String maparea;
	private String mapAreaName;
	private String showLegend;
	private String legendLayout;
	private String legendPosition;
	private String legendBgColor;
	private String legendBorderWidth;  //图例边框大小
	private String legendBorderColor;	//图例边框颜色
	private Boolean dataLabel;
	private String dataLabelColor;
	private String dataLabelPosition;
	private Boolean dataLabelWeight;  //标签文字是否粗体
	private String labelType;
	private String marginLeft;
	private String marginRight;
	private String marginTop;
	private String marginBottom;
	private String markerEnabled;
	private Boolean spline;
	private Boolean splitLine;
	private String splitLineColor; //网格线颜色
	private String splitLineWidth = "1"; //网格线宽度
	private String splitLineType = "solid"; //网格线类型
	private Integer innerRing;
	private Boolean makeLine;
	private String margin;
	private Boolean hideYaxis;
	private Boolean hideXaxis; //是否隐藏 x轴
	private Integer splitNumber; //坐标轴的分割段数
	private Integer barWidth;
	private Boolean axisTick;
	private Boolean axisLine;
	private String colors;
	private Integer mapTop;
	private Boolean yAxisTick;
	private Boolean yAxisLine;
	private Boolean boundaryGap;
	private String chartbgcolor;
	private Boolean animation;
	private Integer barBorderRadius;
	private Boolean axisLabel;
	private Boolean yAliasLabel;
	private String axisNameColor; //坐标横轴颜色
	private String yAxisNameColor; //坐标Y轴颜色
	private Integer yAxisNameSize; //坐标Y轴字体大小

	private String legendTextColor;  //图例文本颜色
	private String mapBorderColor;  //地图边框颜色
	private Integer mapBorderWidth; //地图边框宽度
	private String mapAreaColor;  //地图区域颜色,无数据
	private String mapHoverColor; //地图鼠标移动到区域上的颜色
	private String mapDataAreaColor; //地图数据颜色,3种颜色
	private Boolean hideY2axis;
	private Boolean y2AxisLine = true;  //是否显示Y2轴坐标轴线
	private Boolean y2AxisTick = true; //是否显示Y2轴刻度
	private Boolean y2AliasLabel = true; //是否显示Y2轴标签
	private String y2SplitNumber; //y2坐标轴的分割段数
	private String axisLineColor;  //横轴坐标线颜色
	private String yAxisLineColor;  //y轴坐标线颜色
	private String y2AxisLineColor;  //y2轴坐标线颜色
	private String y2AxisNameColor;  //坐标y2颜色
	private Boolean pieRoseType;
	private String outerRing;
	private Boolean pieDataLabel; //是否显示饼图标签
	private String pieDataLabelPos; //饼图标签显示位置
	private String yAxisTickColor; //y轴的tick的颜色
	private String radarShape; //雷达类型 'polygon' 和 'circle'
	private String treeMapGap; //treeMap各块之间的间距
	private String dataLabelSize; //图形标签字体大小
	private Boolean hidePieLine;
	private String colorSeries; //颜色系列
	private String scatterSymbol;  //散点图显示状态
	private String bmapDataAreaColor; //百度地图数据颜色
	private String bmapCenterPos; //百度地图中心位置经纬度
	private String bmapZoom;  //百度地图显示的层级
	private String centerPos; //饼图中心点位置10%-90%
	private Boolean hideLine = false; //曲线图中只显示点，隐藏曲线
	private Boolean useDataZoom = false;  //使用缩放
	private Integer dataZoomStart; //起始百分比
	private Integer dataZoomEnd;  //结束百分比
	private Boolean null2zero = false; //图形null值转成0
	private Boolean showTooltip; //是否展示showTooltip， 默认true
	private Boolean intervalTp; //是否轮询Tooltip
	private String tooltipClass; //tooltip 的class

	private String fFamily;  //图形字体，全局
	private String fsize; //字体大小，全局

	/**
	 * 已废弃的属性，为了保持兼容性， 先保留
	 */
	@Deprecated
	private String legendpos;

	/**
	 * 动态赋值的字段
	 */
	public String[] props = new String[] {"showLegend", "legendLayout", "legendPosition" , "labelType",
			"marginLeft", "marginRight", "marginTop", "marginBottom", "markerEnabled", "spline", "splitLine", "innerRing", "makeLine",
			"margin", "hideYaxis", "splitNumber","barWidth", "axisTick", "axisLine", "colors","mapTop", "yAxisTick", "boundaryGap",
			"chartbgcolor", "animation", "hideXaxis", "yAxisLine", "barBorderRadius", "axisLabel", "yAliasLabel", "splitLineColor", "splitLineWidth",
			"splitLineType","axisNameColor", "yAxisNameColor", "legendTextColor", "mapBorderColor", "mapAreaColor", "mapDataAreaColor", "legendBgColor",
			"legendBorderWidth", "legendBorderColor", "dataLabelColor", "dataLabelPosition", "hideY2axis", "y2AxisLine", "y2AxisTick", "y2AliasLabel",
			"y2SplitNumber", "axisLineColor", "yAxisLineColor", "y2AxisLineColor", "y2AxisNameColor", "pieRoseType", "outerRing", "pieDataLabelPos", "yAxisTickColor",
			"radarShape", "mapHoverColor", "treeMapGap", "dataLabelSize", "hidePieLine", "colorSeries", "scatterSymbol", "bmapDataAreaColor", "bmapCenterPos", "bmapZoom",
			"centerPos", "hideLine", "dataLabelWeight", "yAxisNameSize", "useDataZoom", "dataZoomStart", "dataZoomEnd", "null2zero", "mapBorderWidth", "showTooltip",
			"intervalTp", "tooltipClass", "fFamily", "fsize"
	};

	public List<DimDto> getDims(){
		List<DimDto> dims = new ArrayList<DimDto>();
		if(this.getXcol() != null && this.getXcol().getId() != null){
			dims.add(this.getXcol());
		}
		if(this.getScol() != null && this.getScol().getId() != null){
			dims.add(this.getScol());
		}
		if(this.getParams() != null && this.getParams().size() > 0){
			dims.addAll(this.getParams());
		}
		return dims;
	}

	/**
	 * 查询图形所有维度， 但如果有层级维度，只取最后一个层级
	 * @return
	 */
	public List<DimDto> getDimsOnlyOneLevel(){
		List<DimDto> ret = new ArrayList<DimDto>();
		DimDto lastPcDim = null; //最后一个层级维度
		DimDto xcol = this.getXcol();
		if(xcol != null && xcol.getId() != null){
			if("y".equals(xcol.getIspcdim())) {
				lastPcDim = xcol;
			}else {
				ret.add(xcol);
			}
		}
		DimDto scol = this.getScol();
		if(scol != null && scol.getId() != null){
			if("y".equals(scol.getIspcdim())) {
				if(lastPcDim == null) {
					lastPcDim = scol;
				}else {
					if(scol.getPclevel() > lastPcDim.getPclevel()) { //level最大的一个是最后层级
						lastPcDim = scol;
					}
				}
			}else {
				ret.add(scol);
			};
		}
		if(this.getParams() != null) {
			for(DimDto col : this.getParams()) {
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
		}
		if(lastPcDim != null) {
			ret.add(lastPcDim);
		}
		return ret;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public List<DimDto> getParams() {
		return params;
	}
	public void setParams(List<DimDto> params) {
		this.params = params;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public DimDto getXcol() {
		return xcol;
	}
	public void setXcol(DimDto xcol) {
		this.xcol = xcol;
	}
	public DimDto getScol() {
		return scol;
	}
	public void setScol(DimDto scol) {
		this.scol = scol;
	}
	public DimDto getYcol() {
		return ycol;
	}
	public void setYcol(DimDto ycol) {
		this.ycol = ycol;
	}
	public Map<String, Object> getLink() {
		return link;
	}
	public void setLink(Map<String, Object> link) {
		this.link = link;
	}

	public LinkAcceptDto getLinkAccept() {
		return linkAccept;
	}

	public void setLinkAccept(LinkAcceptDto linkAccept) {
		this.linkAccept = linkAccept;
	}

	public String getMaparea() {
		return maparea;
	}
	public void setMaparea(String maparea) {
		this.maparea = maparea;
	}
	public String getMapAreaName() {
		return mapAreaName;
	}
	public void setMapAreaName(String mapAreaName) {
		this.mapAreaName = mapAreaName;
	}
	public Integer getTypeIndex() {
		return typeIndex;
	}
	public void setTypeIndex(Integer typeIndex) {
		this.typeIndex = typeIndex;
	}

	public String getShowLegend() {
		return showLegend;
	}

	public void setShowLegend(String showLegend) {
		this.showLegend = showLegend;
	}

	public String getLegendLayout() {
		return legendLayout;
	}

	public void setLegendLayout(String legendLayout) {
		this.legendLayout = legendLayout;
	}
	public String getLegendPosition() {
		return legendPosition;
	}

	public void setLegendPosition(String legendPosition) {
		this.legendPosition = legendPosition;
	}

	public Boolean getDataLabel() {
		return dataLabel;
	}

	public void setDataLabel(Boolean dataLabel) {
		this.dataLabel = dataLabel;
	}

	public String getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(String marginLeft) {
		this.marginLeft = marginLeft;
	}

	public String getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(String marginRight) {
		this.marginRight = marginRight;
	}

	public String getMarkerEnabled() {
		return markerEnabled;
	}

	public void setMarkerEnabled(String markerEnabled) {
		this.markerEnabled = markerEnabled;
	}

	public String getLabelType() {
		return labelType;
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	public Boolean getSpline() {
		return spline;
	}

	public void setSpline(Boolean spline) {
		this.spline = spline;
	}

	public Boolean getSplitLine() {
		return splitLine;
	}

	public void setSplitLine(Boolean splitLine) {
		this.splitLine = splitLine;
	}

	public Integer getInnerRing() {
		return innerRing;
	}

	public void setInnerRing(Integer innerRing) {
		this.innerRing = innerRing;
	}

	public Boolean getMakeLine() {
		return makeLine;
	}

	public void setMakeLine(Boolean makeLine) {
		this.makeLine = makeLine;
	}

	public String getMargin() {
		return margin;
	}

	public void setMargin(String margin) {
		this.margin = margin;
	}

	public Boolean getHideYaxis() {
		return hideYaxis;
	}

	public void setHideYaxis(Boolean hideYaxis) {
		this.hideYaxis = hideYaxis;
	}

	public Integer getSplitNumber() {
		return splitNumber;
	}

	public void setSplitNumber(Integer splitNumber) {
		this.splitNumber = splitNumber;
	}

	public Integer getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(Integer barWidth) {
		this.barWidth = barWidth;
	}

	public Boolean getAxisTick() {
		return axisTick;
	}

	public void setAxisTick(Boolean axisTick) {
		this.axisTick = axisTick;
	}

	public Boolean getAxisLine() {
		return axisLine;
	}

	public void setAxisLine(Boolean axisLine) {
		this.axisLine = axisLine;
	}

	public String getColors() {
		return colors;
	}

	public void setColors(String colors) {
		this.colors = colors;
	}

	public Integer getMapTop() {
		return mapTop;
	}

	public void setMapTop(Integer mapTop) {
		this.mapTop = mapTop;
	}

	public Boolean getyAxisTick() {
		return yAxisTick;
	}

	public void setyAxisTick(Boolean yAxisTick) {
		this.yAxisTick = yAxisTick;
	}

	public Boolean getBoundaryGap() {
		return boundaryGap;
	}

	public void setBoundaryGap(Boolean boundaryGap) {
		this.boundaryGap = boundaryGap;
	}

	public String getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(String marginTop) {
		this.marginTop = marginTop;
	}

	public String getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(String marginBottom) {
		this.marginBottom = marginBottom;
	}

	public String getChartbgcolor() {
		return chartbgcolor;
	}

	public void setChartbgcolor(String chartbgcolor) {
		this.chartbgcolor = chartbgcolor;
	}

	public Boolean getAnimation() {
		return animation;
	}

	public void setAnimation(Boolean animation) {
		this.animation = animation;
	}

	public String[] getProps() {
		return props;
	}

	public void setProps(String[] props) {
		this.props = props;
	}

	public String getLegendpos() {
		return legendpos;
	}

	public void setLegendpos(String legendpos) {
		this.legendpos = legendpos;
	}

	public Boolean getHideXaxis() {
		return hideXaxis;
	}

	public void setHideXaxis(Boolean hideXaxis) {
		this.hideXaxis = hideXaxis;
	}

	public Boolean getyAxisLine() {
		return yAxisLine;
	}

	public void setyAxisLine(Boolean yAxisLine) {
		this.yAxisLine = yAxisLine;
	}

	public Integer getBarBorderRadius() {
		return barBorderRadius;
	}

	public void setBarBorderRadius(Integer barBorderRadius) {
		this.barBorderRadius = barBorderRadius;
	}

	public Boolean getAxisLabel() {
		return axisLabel;
	}

	public void setAxisLabel(Boolean axisLabel) {
		this.axisLabel = axisLabel;
	}

	public Boolean getyAliasLabel() {
		return yAliasLabel;
	}

	public void setyAliasLabel(Boolean yAliasLabel) {
		this.yAliasLabel = yAliasLabel;
	}

	public String getSplitLineColor() {
		return splitLineColor;
	}

	public void setSplitLineColor(String splitLineColor) {
		this.splitLineColor = splitLineColor;
	}

	public String getSplitLineWidth() {
		return splitLineWidth;
	}

	public void setSplitLineWidth(String splitLineWidth) {
		this.splitLineWidth = splitLineWidth;
	}

	public String getSplitLineType() {
		return splitLineType;
	}

	public void setSplitLineType(String splitLineType) {
		this.splitLineType = splitLineType;
	}

	public String getAxisNameColor() {
		return axisNameColor;
	}

	public void setAxisNameColor(String axisNameColor) {
		this.axisNameColor = axisNameColor;
	}

	public String getyAxisNameColor() {
		return yAxisNameColor;
	}

	public void setyAxisNameColor(String yAxisNameColor) {
		this.yAxisNameColor = yAxisNameColor;
	}

	public String getLegendTextColor() {
		return legendTextColor;
	}

	public void setLegendTextColor(String legendTextColor) {
		this.legendTextColor = legendTextColor;
	}

	public String getMapBorderColor() {
		return mapBorderColor;
	}

	public void setMapBorderColor(String mapBorderColor) {
		this.mapBorderColor = mapBorderColor;
	}

	public String getMapAreaColor() {
		return mapAreaColor;
	}

	public void setMapAreaColor(String mapAreaColor) {
		this.mapAreaColor = mapAreaColor;
	}

	public String getMapDataAreaColor() {
		return mapDataAreaColor;
	}

	public void setMapDataAreaColor(String mapDataAreaColor) {
		this.mapDataAreaColor = mapDataAreaColor;
	}

	public String getLegendBgColor() {
		return legendBgColor;
	}

	public void setLegendBgColor(String legendBgColor) {
		this.legendBgColor = legendBgColor;
	}

	public String getLegendBorderWidth() {
		return legendBorderWidth;
	}

	public void setLegendBorderWidth(String legendBorderWidth) {
		this.legendBorderWidth = legendBorderWidth;
	}

	public String getLegendBorderColor() {
		return legendBorderColor;
	}

	public void setLegendBorderColor(String legendBorderColor) {
		this.legendBorderColor = legendBorderColor;
	}

	public String getDataLabelColor() {
		return dataLabelColor;
	}

	public void setDataLabelColor(String dataLabelColor) {
		this.dataLabelColor = dataLabelColor;
	}

	public String getDataLabelPosition() {
		return dataLabelPosition;
	}

	public void setDataLabelPosition(String dataLabelPosition) {
		this.dataLabelPosition = dataLabelPosition;
	}

	public Boolean getHideY2axis() {
		return hideY2axis;
	}

	public void setHideY2axis(Boolean hideY2axis) {
		this.hideY2axis = hideY2axis;
	}

	public Boolean getY2AxisLine() {
		return y2AxisLine;
	}

	public void setY2AxisLine(Boolean y2AxisLine) {
		this.y2AxisLine = y2AxisLine;
	}

	public Boolean getY2AxisTick() {
		return y2AxisTick;
	}

	public void setY2AxisTick(Boolean y2AxisTick) {
		this.y2AxisTick = y2AxisTick;
	}

	public Boolean getY2AliasLabel() {
		return y2AliasLabel;
	}

	public void setY2AliasLabel(Boolean y2AliasLabel) {
		this.y2AliasLabel = y2AliasLabel;
	}

	public String getY2SplitNumber() {
		return y2SplitNumber;
	}

	public void setY2SplitNumber(String y2SplitNumber) {
		this.y2SplitNumber = y2SplitNumber;
	}

	public String getAxisLineColor() {
		return axisLineColor;
	}

	public void setAxisLineColor(String axisLineColor) {
		this.axisLineColor = axisLineColor;
	}

	public String getyAxisLineColor() {
		return yAxisLineColor;
	}

	public void setyAxisLineColor(String yAxisLineColor) {
		this.yAxisLineColor = yAxisLineColor;
	}

	public String getY2AxisLineColor() {
		return y2AxisLineColor;
	}

	public void setY2AxisLineColor(String y2AxisLineColor) {
		this.y2AxisLineColor = y2AxisLineColor;
	}

	public String getY2AxisNameColor() {
		return y2AxisNameColor;
	}

	public void setY2AxisNameColor(String y2AxisNameColor) {
		this.y2AxisNameColor = y2AxisNameColor;
	}

	public Boolean getPieRoseType() {
		return pieRoseType;
	}

	public void setPieRoseType(Boolean pieRoseType) {
		this.pieRoseType = pieRoseType;
	}

	public String getOuterRing() {
		return outerRing;
	}

	public void setOuterRing(String outerRing) {
		this.outerRing = outerRing;
	}

	public String getPieDataLabelPos() {
		return pieDataLabelPos;
	}

	public void setPieDataLabelPos(String pieDataLabelPos) {
		this.pieDataLabelPos = pieDataLabelPos;
	}

	public String getyAxisTickColor() {
		return yAxisTickColor;
	}

	public void setyAxisTickColor(String yAxisTickColor) {
		this.yAxisTickColor = yAxisTickColor;
	}

	public String getRadarShape() {
		return radarShape;
	}

	public void setRadarShape(String radarShape) {
		this.radarShape = radarShape;
	}

	public String getMapHoverColor() {
		return mapHoverColor;
	}

	public void setMapHoverColor(String mapHoverColor) {
		this.mapHoverColor = mapHoverColor;
	}

	public String getTreeMapGap() {
		return treeMapGap;
	}

	public void setTreeMapGap(String treeMapGap) {
		this.treeMapGap = treeMapGap;
	}

	public String getDataLabelSize() {
		return dataLabelSize;
	}

	public void setDataLabelSize(String dataLabelSize) {
		this.dataLabelSize = dataLabelSize;
	}

	public Boolean getHidePieLine() {
		return hidePieLine;
	}

	public void setHidePieLine(Boolean hidePieLine) {
		this.hidePieLine = hidePieLine;
	}

	public String getColorSeries() {
		return colorSeries;
	}

	public void setColorSeries(String colorSeries) {
		this.colorSeries = colorSeries;
	}

	public String getScatterSymbol() {
		return scatterSymbol;
	}

	public void setScatterSymbol(String scatterSymbol) {
		this.scatterSymbol = scatterSymbol;
	}

	public String getBmapDataAreaColor() {
		return bmapDataAreaColor;
	}

	public void setBmapDataAreaColor(String bmapDataAreaColor) {
		this.bmapDataAreaColor = bmapDataAreaColor;
	}

	public String getBmapCenterPos() {
		return bmapCenterPos;
	}

	public void setBmapCenterPos(String bmapCenterPos) {
		this.bmapCenterPos = bmapCenterPos;
	}

	public String getBmapZoom() {
		return bmapZoom;
	}

	public void setBmapZoom(String bmapZoom) {
		this.bmapZoom = bmapZoom;
	}

	public String getCenterPos() {
		return centerPos;
	}

	public void setCenterPos(String centerPos) {
		this.centerPos = centerPos;
	}

	public Boolean getHideLine() {
		return hideLine;
	}

	public void setHideLine(Boolean hideLine) {
		this.hideLine = hideLine;
	}

	public Boolean getDataLabelWeight() {
		return dataLabelWeight;
	}

	public void setDataLabelWeight(Boolean dataLabelWeight) {
		this.dataLabelWeight = dataLabelWeight;
	}

	public Boolean getPieDataLabel() {
		return pieDataLabel;
	}

	public void setPieDataLabel(Boolean pieDataLabel) {
		this.pieDataLabel = pieDataLabel;
	}

	public Integer getyAxisNameSize() {
		return yAxisNameSize;
	}

	public void setyAxisNameSize(Integer yAxisNameSize) {
		this.yAxisNameSize = yAxisNameSize;
	}

	public Boolean getUseDataZoom() {
		return useDataZoom;
	}

	public void setUseDataZoom(Boolean useDataZoom) {
		this.useDataZoom = useDataZoom;
	}

	public Integer getDataZoomStart() {
		return dataZoomStart;
	}

	public void setDataZoomStart(Integer dataZoomStart) {
		this.dataZoomStart = dataZoomStart;
	}

	public Integer getDataZoomEnd() {
		return dataZoomEnd;
	}

	public void setDataZoomEnd(Integer dataZoomEnd) {
		this.dataZoomEnd = dataZoomEnd;
	}

	public Boolean getNull2zero() {
		return null2zero;
	}

	public void setNull2zero(Boolean null2zero) {
		this.null2zero = null2zero;
	}

	public Integer getMapBorderWidth() {
		return mapBorderWidth;
	}

	public void setMapBorderWidth(Integer mapBorderWidth) {
		this.mapBorderWidth = mapBorderWidth;
	}

	public Boolean getShowTooltip() {
		return showTooltip;
	}

	public void setShowTooltip(Boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

	public Boolean getIntervalTp() {
		return intervalTp;
	}

	public void setIntervalTp(Boolean intervalTp) {
		this.intervalTp = intervalTp;
	}

	public String getTooltipClass() {
		return tooltipClass;
	}

	public void setTooltipClass(String tooltipClass) {
		this.tooltipClass = tooltipClass;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getfFamily() {
		return fFamily;
	}

	public void setfFamily(String fFamily) {
		this.fFamily = fFamily;
	}

	public String getFsize() {
		return fsize;
	}

	public void setFsize(String fsize) {
		this.fsize = fsize;
	}
}
