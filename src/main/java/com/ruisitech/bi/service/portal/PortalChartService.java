/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.portal;

import com.rsbi.ext.engine.ExtConstants;
import com.rsbi.ext.engine.init.TemplateManager;
import com.rsbi.ext.engine.util.IdCreater;
import com.rsbi.ext.engine.view.context.Element;
import com.rsbi.ext.engine.view.context.MVContext;
import com.rsbi.ext.engine.view.context.MVContextImpl;
import com.rsbi.ext.engine.view.context.chart.*;
import com.rsbi.ext.engine.view.context.dc.grid.*;
import com.rsbi.ext.engine.view.context.form.InputField;
import com.rsbi.ext.engine.view.emitter.chart.AbstractChartEmitter;
import com.rsbi.ext.engine.view.emitter.chart.ChartUtils;
import com.rsbi.ispire.dc.grid.GridFilter;
import com.rsbi.ispire.dc.grid.GridProcContext;
import com.ruisitech.bi.entity.bireport.*;
import com.ruisitech.bi.entity.portal.LinkAcceptDto;
import com.ruisitech.bi.entity.portal.PortalChartQuery;
import com.ruisitech.bi.mapper.bireport.AreaMapper;
import com.ruisitech.bi.service.bireport.BaseCompService;
import com.ruisitech.bi.service.bireport.DataCenterCacheService;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.QueryRestService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.ext.service.DataControlInterface;
import com.ruisitech.ext.service.MatchDateKeyService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class PortalChartService extends BaseCompService  {

	public final static String deftMvId = "mv.portal.chart";

	@Autowired
	private TableCacheService cacheService;

	private Map<String, InputField> mvParams = new HashMap<String, InputField>(); //mv的参数

	@Autowired
	private DataControlInterface dataControl; //数据权限控制

	@Resource
	private AreaMapper areaMapper;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private DataCenterCacheService dcCacheService;

	@Autowired
	private QueryRestService restService;


	public MVContext json2MV(PortalChartQuery chartJson) throws Exception{
		TableInfoVO tvo = null;
		if(chartJson.getTid() != null) {
			tvo = cacheService.getTableInfo(chartJson.getTid());
			chartJson.checkSql(tvo);
		}
		//创建MV
		MVContext mv = new MVContextImpl();
		mv.setChildren(new ArrayList<Element>());
		String formId = ExtConstants.formIdPrefix + IdCreater.create();
		mv.setFormId(formId);
		mv.setMvid(deftMvId);
		mv.setHideMV(true);

		//处理参数,把参数设为hidden
		super.parserHiddenParam(chartJson.getPortalParams(), mv, mvParams, chartJson.getUseIn());

		if(chartJson.getMkpi() == null || !chartJson.getMkpi()) {  //单指标查询
			if(chartJson.getKpiJson().size() == 0 || chartJson.getKpiJson().get(0) == null ) {
				return mv;
			}
		}
		if(chartJson.getMkpi() != null && chartJson.getMkpi()) {
			if(chartJson.getMkpiJson() == null || chartJson.getMkpiJson().size() == 0) {
				return mv;
			}
		}

		//创建chart
		ChartContext cr = json2Chart(chartJson);

		//重新设置chartId
		cr.setId(chartJson.getId());
		GridDataCenterContext dc = null;
		if(chartJson.getTid() == null) {  //模板调用,没有tid
			dc = super.createEmptyDataCenter(chartJson.getId());
			cr.setRefDataCenter(dc.getId());
		}else {  //非模板调用
			//如果是纵表，需要通过查询指标获取纵表得字段
			TableInfoVO tinfo = tvo;
			String sql = tinfo.isRest() ? super.createRestSql(chartJson.getCompParams(), chartJson.getChartJson().getLinkAccept(), tinfo) : createSql(chartJson, null,null, null, null, tinfo);
			dc = this.createDataCenter(chartJson, sql);
			cr.setRefDataCenter(dc.getId());
		}
		if(mv.getGridDataCenters() == null){
			mv.setGridDataCenters(new HashMap<>());
		}
		mv.getGridDataCenters().put(dc.getId(), dc);

		mv.getChildren().add(cr);
		cr.setParent(mv);
		/**
		//判断是否有事件，是否需要添加参数
		LinkAcceptDto linkAccept = chartJson.getChartJson().getLinkAccept();
		if(linkAccept != null && linkAccept.getId() != null){
			//创建参数
			TextFieldContext linkText = new TextFieldContextImpl();
			linkText.setType("hidden");
			linkText.setDefaultValue(linkAccept.getDftval());
			linkText.setId(linkAccept.getTableColKey() != null && linkAccept.getTableColKey().length() > 0 ? linkAccept.getTableColKey() : linkAccept.getAlias());
			mv.getChildren().add(0, linkText);
			linkText.setParent(mv);
			this.mvParams.put(linkText.getId(), linkText);
			ExtContext.getInstance().putServiceParam(mv.getMvid(), linkText.getId(), linkText);
			mv.setShowForm(true);
		}
		**/
		Map<String, ChartContext> crs = new HashMap<String, ChartContext>();
		crs.put(cr.getId(), cr);
		mv.setCharts(crs);
		//处理scripts
		String script = this.createBaseJSFunction();
		if(this.scripts != null){
			script += this.scripts;
		}
		mv.setScripts(script);
		if(tvo != null) {
			super.createDsource(tvo.getDsource(), mv);
		}
		return mv;
	}

	public ChartContext json2Chart(PortalChartQuery chartJson){
		ChartContext ctx = new ChartContextImpl();
		//ctx.setOnlyJson(chartJson.getOnlyJSON());
		//设置文本标签
		ctx.setSeriesLabel(chartJson.getChartLabels());
		//设置x
		DimDto obj = chartJson.getChartJson().getXcol();
		if(obj != null && obj.getId() != null){
			String alias = obj.getAlias();
			String key = obj.getTableColKey();
			String txt =  obj.getTableColName();
			if(key != null && key.length() > 0 && txt != null && txt.length() > 0){  //只有在维度关联了维度表后才进行判断
				ctx.setXcolDesc(alias); //用来关联ID,用在钻取中
				ctx.setXcol(alias+"2");
			}else{
				ctx.setXcol(alias);
				ctx.setXcolDesc(alias);
			}
		}

		KpiDto kpiInfo;
		if(chartJson.getMkpi() && chartJson.getMkpiJson() != null ){  //多指标查询采用 ChartYcolContext
			List<ChartYcolContext> ycols = new ArrayList<ChartYcolContext>();
			//如果图形多指标里有计算，把计算值转换成一个图例
			for(KpiDto k : chartJson.getMkpiJson()){
				ChartYcolContext ycol = new ChartYcolContext();
				ycol.setName(k.getAlias());
				ycol.setDesc(k.getKpi_name());
				ycol.setHideLine(k.getHideLine());
				ycols.add(ycol);
				if(k.getCompute() != null && k.getCompute().length() > 0){  //多指标图形 - 计算
					String[] jss = k.getCompute().split(",");
					for(String js : jss){
						ChartYcolContext jsycl = new ChartYcolContext();
						jsycl.setName(k.getAlias()+"_" + js);
						jsycl.setDesc(k.getKpi_name()+ ChartUtils.getComputeName(js, null));
						ycols.add(jsycl);
					}
				}
				if(k.getCompareDate() != null){
					ChartYcolContext jsycl = new ChartYcolContext();
					jsycl.setName(k.getAlias()+"_zdz");
					jsycl.setDesc(k.getKpi_name()+ k.getCompareDate().getName());
					//jsycl.set
					ycols.add(jsycl);
				}
			}
			kpiInfo = chartJson.getMkpiJson().get(0);
			ctx.setYcols(ycols);
		}else{
			kpiInfo = chartJson.getKpiJson().get(0);
			ctx.setYcol(kpiInfo.getAlias());
		}

		List<KpiDto> kpis = chartJson.getKpiJson();
		//如果是散点图或气泡图，需要 y2col
		String chartType = chartJson.getChartJson().getType();
		if(chartType.equals("scatter")){
			ctx.setY2col(kpis.get(1).getAlias());
		}
		if(chartType.equals("bubble")){
			ctx.setY2col(kpis.get(1).getAlias());
			ctx.setY3col(kpis.get(2).getAlias());
		}

		//设置倍率
		if(!chartJson.getMkpi() && kpiInfo.getRate() != null){
			ctx.setRate(kpiInfo.getRate());
		}
		if(!chartJson.getMkpi() && kpis.size() > 1){
			ctx.setRate2(kpis.get(1).getRate());
		}
		if(!chartJson.getMkpi() && kpis.size() > 2){
			ctx.setRate3(kpis.get(2).getRate());
		}
		if(chartJson.getMkpi()) {
			ctx.setRate(chartJson.getMkpiJson().get(0).getRate());
			//判断是否有y2轴
			for(KpiDto k : chartJson.getMkpiJson()){
				if(k.getKpiPostion() != null && k.getKpiPostion().equals("right")){
					ctx.setRate2(k.getRate());
					break;
				}
			}
		}


		if(!chartJson.getMkpi()){  //多指标查询 没有 scol （序列）
			DimDto scol = chartJson.getChartJson().getScol();
			if(scol != null && scol.getId() != null){
				String key = scol.getTableColKey();
				String txt = scol.getTableColName();
				if(key != null && key.length() > 0 && txt != null && txt.length() > 0){  //只有在维度关联了维度表后才进行判断
					ctx.setScol(scol.getAlias()+"2");
				}else {
					ctx.setScol(scol.getAlias());
				}
			}
		}

		ctx.setShape(chartJson.getChartJson().getType());
		if("custom".equals(chartJson.getChartJson().getType())){  //当图形类型为custom（自定义）时，采用 chartJson 的 shape 做图形 shape
			ctx.setShape(chartJson.getChartJson().getShape());
		}

		//设置配置信息
		List<ChartKeyContext> properties = new ArrayList<ChartKeyContext>();
		String unitStr = super.writerUnit(kpiInfo.getRate()) + (kpiInfo.getUnit() == null ? "" : kpiInfo.getUnit());

		String ydesc = kpiInfo.getYdispName()+(unitStr.length() == 0 ? "" : "("+unitStr+")");
		if(chartJson.getMkpi() && chartJson.getMkpiJson().size() > 0) {
			KpiDto mkd = chartJson.getMkpiJson().get(0);
			ydesc = mkd.getYdispName();
			unitStr = super.writerUnit(mkd.getRate()) + (mkd.getUnit() == null ? "" : mkd.getUnit());
			ydesc += (unitStr.length() == 0 ? "" : "(" + unitStr + ")");
		}
		if("map".equalsIgnoreCase(chartType)){
			ydesc = kpiInfo.getYdispName();
		}
		properties.add(new ChartKeyContext("ydesc", ydesc));
		if("bubble".equals(ctx.getShape()) || "scatter".equals(ctx.getShape())){
			KpiDto kpiInfo2 = kpis.get(1);
			//对于散点图和气泡图，需要设置xdesc
			String unit2Str = super.writerUnit(kpiInfo2.getRate()) + (kpiInfo2.getUnit() == null ? "" : kpiInfo2.getUnit());
			properties.add(new ChartKeyContext("xdesc", kpiInfo2.getYdispName() + (unit2Str.length() == 0 ? "": "("+unit2Str+")")));
			properties.add(new ChartKeyContext("formatCol2", kpiInfo2.getFmt()));
		}else
		if(chartJson.getChartJson().getXcol() != null && chartJson.getChartJson().getXcol().getId() != null){
			properties.add(new ChartKeyContext("xdesc", chartJson.getChartJson().getXcol().getXdispName()));
		}
		//title
		/**
		String tit = (String)chartJson.get("title");
		if(tit != null && tit.length() > 0){
			properties.add(new ChartKeyContext("title", tit));
		}
		**/

		//设置图形高度，在导出html中使用
		if(chartJson.getHeight() != null) {
			ctx.setHeight(String.valueOf(chartJson.getHeight()));
		}


		//格式化配置信息
		if(kpiInfo.getFmt() != null && kpiInfo.getFmt().length() > 0){
			properties.add(new ChartKeyContext("formatCol", kpiInfo.getFmt()));
		}

		if(kpiInfo.getUnit() != null && kpiInfo.getUnit().length() > 0){
			properties.add(new ChartKeyContext("unitCol", kpiInfo.getUnit()));
		}
		if(kpiInfo.getMin() != null ){
			properties.add(new ChartKeyContext("ymin", String.valueOf(kpiInfo.getMin())));
		}
		if(kpiInfo.getMax() != null ){
			properties.add(new ChartKeyContext("ymax", String.valueOf(kpiInfo.getMax())));
		}
		if("bubble".equals(chartType) || "scatter".equals(chartType)){
			if(chartJson.getChartJson().getXcol() != null ){
				properties.add(new ChartKeyContext("legendName", chartJson.getChartJson().getXcol().getDimdesc()));
			}
		}else {
			properties.add(new ChartKeyContext("legendName", kpiInfo.getKpi_name()));
		}
		if(obj != null && obj.getId() != null){
			//取得top
			Integer top = obj.getTop();
			if(top != null){
				ChartKeyContext val1 = new ChartKeyContext("xcnt", String.valueOf(top));
				properties.add(val1);
			}
			if(obj.getTickInterval() != null){
				ChartKeyContext val1 = new ChartKeyContext("tickInterval", obj.getTickInterval());
				properties.add(val1);
			}
			if(obj.getRouteXaxisLable() != null){
				ChartKeyContext val1 = new ChartKeyContext("routeXaxisLable", obj.getRouteXaxisLable());
				properties.add(val1);
			}
		}
		//设置饼图是否显示标签
		Boolean pieDataLabel = chartJson.getChartJson().getPieDataLabel();
		if(pieDataLabel != null){
			ChartKeyContext val3 = new ChartKeyContext("showLabel", String.valueOf(pieDataLabel));
			properties.add(val3);
		}
		//地图等其他图形是否显示标签字段
		Boolean dataLabel = chartJson.getChartJson().getDataLabel();
		if(dataLabel != null){
			ChartKeyContext val3 = new ChartKeyContext("showLabel", String.valueOf(dataLabel));
			properties.add(val3);
		}

		//设置仪表盘数量
		ChartKeyContext val1 = new ChartKeyContext("gaugeCnt", "1");
		properties.add(val1);

		//如果在数据报表中，点击序列更换颜色，而在仪表盘中，点击序列刷新数据。
		/**
		if("dashboard".equals(chartJson.getUseIn())){
			properties.add(new ChartKeyContext("action","window.dashboard.seriesClick"));
		}else{
			properties.add(new ChartKeyContext("action","setSeriesColor"));
		}
		 **/
		ctx.setProperties(properties);
		//图形风格
		if("bigscreen".equals(chartJson.getStyleType())) {
			properties.add(new ChartKeyContext("style", "bigscreen"));
		}

		//判断是否有事件
		Map<String, Object> link = chartJson.getChartJson().getLink();
		if(link != null && !link.isEmpty() && "report".equals(chartJson.getUseIn())){
			ctx.setLink(createChartLink(link));
		}
		ctx.setLabel(chartJson.getId());  //都加上label

		//判断曲线图、柱状图是否双坐标轴
		Integer typeIndex = chartJson.getChartJson().getTypeIndex();
		if((chartType.equals("column")||chartType.equals("line")||"bar".equals(chartType)) && typeIndex != null && (2 == typeIndex || 4  == typeIndex) && kpis.size() > 1 && kpis.get(1) != null){
			ctx.setY2col(kpis.get(1).getAlias());
			ctx.setMergeData(kpis.get(1).getMergeData());
			ctx.setY2Aggre(kpis.get(1).getAggre());
			ctx.setyAggre(kpis.get(0).getAggre());
			ctx.setMergeY1Data(kpis.get(0).getMergeData());
			String y2unit = super.writerUnit(kpis.get(1).getRate()) + (kpis.get(1).getUnit() == null ? "" : kpis.get(1).getUnit()) ;
			ChartKeyContext y2desc = new ChartKeyContext("y2desc", kpis.get(1).getYdispName() + (y2unit.length() ==0 ? "" : "("+y2unit+")"));
			properties.add(y2desc);
			ChartKeyContext y2fmtcol = new ChartKeyContext("formatCol2", kpis.get(1).getFmt());
			properties.add(y2fmtcol);
			properties.add(new ChartKeyContext("unitCol2", kpis.get(1).getUnit() == null ? "" : kpis.get(1).getUnit()));

			//设置y2min
			Double y2min = kpis.get(1).getMin();
			if( y2min != null ) {
				properties.add(new ChartKeyContext("y2min", String.valueOf(y2min)));
			}
			Double y2max = kpis.get(1).getMax();
			if (y2max != null) {
				properties.add(new ChartKeyContext("y2max", String.valueOf(y2max)));
			}
		}
		//启用多指标查询的曲线图，柱状图，如果设置了y2轴，需要配置y2轴
		if((chartType.equals("column")||chartType.equals("line")) && typeIndex != null && typeIndex == 1 && chartJson.getMkpiJson() != null){
			KpiDto curKpi = null;
			for(KpiDto dto : chartJson.getMkpiJson()){
				if(dto.getKpiPostion() != null && "right".equals(dto.getKpiPostion())){
					curKpi = dto;
					break;
				}
			}
			if(curKpi != null) {
				String y2unit = super.writerUnit(curKpi.getRate()) + (curKpi.getUnit() == null ? "" : curKpi.getUnit());
				ChartKeyContext y2desc = new ChartKeyContext("y2desc", curKpi.getYdispName() + (y2unit.length() == 0 ? "" : "(" + y2unit + ")"));
				properties.add(y2desc);
				ChartKeyContext y2fmtcol = new ChartKeyContext("formatCol2", curKpi.getFmt());
				properties.add(y2fmtcol);
				properties.add(new ChartKeyContext("unitCol2", curKpi.getUnit() == null ? "" : curKpi.getUnit()));

				//设置y2min
				Double y2min = curKpi.getMin();
				if (y2min != null) {
					properties.add(new ChartKeyContext("y2min", String.valueOf(y2min)));
				}
				Double y2max = curKpi.getMax();
				if (y2max != null) {
					properties.add(new ChartKeyContext("y2max", String.valueOf(y2max)));
				}
			}
		}
		//判断柱状图是否显示为堆积图
		if("column".equals(chartType) && typeIndex != null && (3 == typeIndex || 4 == typeIndex)){
			ChartKeyContext stack = new ChartKeyContext("stack", "true");
			properties.add(stack);
		}
		//饼图
		if("pie".equals(chartType) && typeIndex != null && 2 == typeIndex){
			ChartKeyContext ring = new ChartKeyContext("ring", "true");
			properties.add(ring);
		}
		if("pie".equals(chartType)  && typeIndex != null && 3 == typeIndex){
			ctx.setShape("nestingPie");  //嵌套圆环图
		}
		if("map".equals(chartType)  && typeIndex != null && (2 == typeIndex || 3 == typeIndex)){
			ctx.setShape("scatterMap");  //地图散点图嵌套
		}
		if("map".equals(chartType)  && typeIndex != null && 4 == typeIndex){
			ctx.setShape("baiduMap");  //百度地图
		}
		if("map".equals(chartType) && typeIndex != null && 5 == typeIndex){
			ctx.setShape("map3D");  //3d地图
		}
		if("map".equals(chartType) && typeIndex != null && 6 == typeIndex){
			ctx.setShape("heatMap");  //地图热力图
		}
		if("map".equals(chartType)  && typeIndex != null && 3 == typeIndex){
			ChartKeyContext gisMap = new ChartKeyContext("gisMap", "true");  //地图散点图嵌套(GIS)地图
			properties.add(gisMap);
		}
		if("gauge".equals(chartType) && typeIndex != null && 2 == typeIndex) {
			ctx.setShape("gauge2");  //完成率图
		}
		if("gauge".equals(chartType) && typeIndex != null && 3 == typeIndex){
			ctx.setShape("mgauge");  //多指标完成率图
		}
		if("gauge".equals(chartType) && typeIndex != null && 4 == typeIndex){
			ctx.setShape("liquidFill");  //水球图
		}
		if("bar".equals(chartType) && typeIndex != null && 3 == typeIndex){
			ctx.setShape("capsule");  //胶囊图
		}
		//指标计算,sq/tq/lj/zdz(指定值)， 这种只在单指标中处理,多指标不用设置 jstype (放入了 ycols 中)
		if(chartJson.getMkpi() == null || !chartJson.getMkpi() ) {
			String jsType = "";
			if (kpiInfo.getCompute() != null && kpiInfo.getCompute().length() > 0) {
				jsType += kpiInfo.getCompute();
			}
			if (kpiInfo.getCompareDate() != null) {
				jsType += "zdz";
				properties.add(new ChartKeyContext("compareDateKpiName", kpiInfo.getCompareDate().getName()));
			}
			if (jsType.length() > 0) {
				ChartKeyContext compute = new ChartKeyContext("jsType", jsType);
				properties.add(compute);
			}
		}

		ctx.setSeriesColor2(chartJson.getColors());
		//如果是地图，需要设置地图的 mapJson
		if("map".equals(ctx.getShape()) || "scatterMap".equals(ctx.getShape()) || "map3D".equals(ctx.getShape())){
			properties.add(new ChartKeyContext("mapJson",chartJson.getChartJson().getMaparea()));
		}

		//控制 toOtherSer
		if(chartJson.getMkpi() != null && chartJson.getMkpi() && chartJson.getMkpiJson() != null){
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			chartJson.getMkpiJson().forEach(k->{
				if(k.getKpiShape() != null && !k.getKpiShape().equals(ctx.getShape())){
					sb.append(k.getKpi_name());
					sb.append(",");
					if(k.getCompareDate() != null){  //具有比较指定日期
						sb.append(k.getKpi_name()+k.getCompareDate().getName());
						sb.append(",");
					}
				}
				if(k.getKpiPostion() != null && k.getKpiPostion().equals("right")){
					sb2.append(k.getKpi_name());
					sb2.append(",");
					if(k.getCompareDate() != null){
						sb2.append(k.getKpi_name()+k.getCompareDate().getName());
						sb2.append(",");
					}
				}
			});
			if(sb.length() > 0){
				properties.add(new ChartKeyContext("toOtherSharpSer", sb.substring(0, sb.length() - 1)));
			}
			if(sb2.length() > 0){
				properties.add(new ChartKeyContext("toY2Ser", sb2.substring(0, sb2.length() - 1)));
			}
		}

		//复制图形控制属性
		String[] props = chartJson.getChartJson().getProps();
		for(String prop : props) {
			try {
				Object v = PropertyUtils.getProperty(chartJson.getChartJson(), prop);
				if(v == null) {
					continue;
				}
				if(v instanceof String && ((String)v).length() == 0) {
					continue;
				}
				ChartKeyContext p = new ChartKeyContext(prop, String.valueOf(v));
				properties.add(p);
			} catch (Exception e) {
				throw new RuntimeException("图形属性赋值出错："+e.getMessage(), e);
			}
		}

		return ctx;
	}
	public ChartLinkContext createChartLink(Map<String, Object> link){
		String type = (String)link.get("type");
		String target = (String)link.get("target");
		String reportId = (String)link.get("reportId");
		String paramName = (String)link.get("paramName");
		String url = (String)link.get("url");

		ChartLinkContext clink = new ChartLinkContext();
		if(url != null && url.length() > 0){
			clink.setAction("$.ext3.chartGotoReport");
			clink.setOtherPms(reportId); //额外传递的参数
		}else{
			if(target == null){
				return null;
			}
			clink.setTarget(target.split(","));
			clink.setType(type!=null?type.split(","):null);
		}
		clink.setParamName(paramName);
		return clink;
	}
	/**
	 * 创建sql语句所用函数，图形用这个函数创建SQL
	 * 其中第二个参数只用在图形中，当用户没选X轴时(xcol)时，用这个做默认xcol
	 * 其中第三个参数只用在图形中，当用户没选图例(scol)时，用这个做默认图例
	 * release 表示当前为发布状态, 0 表示定制模式(单个组件访问)，1表示发布模式(整个报表访问)
	 * @param chartJson
	 * @param verKpis
	 * @return
	 */
	public String createSql(PortalChartQuery chartJson, List<VerticalKpiDto> verKpis, DateDimDto minDate, String jsType, DateCompareDto dateCompare, TableInfoVO tinfo) {
		boolean qEs = tinfo.isSyncEs();
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		List<DimDto> dims = chartJson.getChartJson().getDimsOnlyOneLevel();
		for(int i=0; i<dims.size(); i++){
			DimDto dim = dims.get(i);
			String key = dim.getTableColKey();
			String txt = dim.getTableColName();
			if(key != null && txt != null && key.length() >0 && txt.length() >0){
				sql.append(key+" "+dim.getAlias()+", " + txt + " "+dim.getAlias()+"2,");
			}else{
				sql.append(" "+(qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname())+" "+dim.getAlias()+", ");
			}

		}
		List<KpiDto> kpis = chartJson.getMkpi() && chartJson.getMkpiJson() != null ? chartJson.getMkpiJson() : chartJson.getKpiJson();

		if(kpis.size() == 0){
			sql.append(" 0 kpi_value ");
		}else{
			if(tinfo.isVertical()) {
				sql.append(tinfo.getKpiCodeColumn());
				sql.append(",");
				super.countDistinct(verKpis.get(0), sql, tinfo);
			}else {
				for(int i=0; i<kpis.size(); i++){
					KpiDto kpi = kpis.get(i);
					sql.append(kpi.getCol_name() + " ");
					sql.append(kpi.getAlias());

					if(i != kpis.size() - 1){
						sql.append(",");
					}
				}
			}
		}

		//如果名字是SQL，加括号，是表名不加括号
		sql.append(" from ");
		sql.append(datasetService.createTableSql(tinfo, false));
		sql.append(" where 1=1 ");
		//如果是纵表，过滤查询字段
		if(tinfo.isVertical() && kpis.size() > 0) {
			sql.append(" and ");
			sql.append(tinfo.getKpiCodeColumn());
			sql.append(" in (");
			StringBuffer sb = new StringBuffer();
			for(VerticalKpiDto entity : verKpis) {
				sb.append("'");
				sb.append(entity.getCode());
				sb.append("'");
				sb.append(",");
			}
			sql.append(sb.substring(0, sb.length() - 1));
			sql.append(")");
		}
		//数据权限
		if(dataControl != null){
			String ret = dataControl.process(RSBIUtils.getLoginUserInfo(), tinfo);
			if(ret != null){
				sql.append(ret + " ");
			}
		}
		//处理父子维度，通过父子维度做数据过滤
		for(int i=0; i<dims.size(); i++){
			DimDto dim = dims.get(i);
			if("y".equals(dim.getIspcdim())) {
				sql.append(" and ");
				sql.append(dim.getLevelCol());
				sql.append(" = ");
				sql.append(dim.getPclevel());
			}
		}
		//如果是计算tq/sq/lj,时间维度只取最小维度，其他类型维度不变
		if(jsType != null) {
			for(int i=0; i<dims.size(); i++){
				DimDto dim = dims.get(i);
				String tp = dim.getType();
				String col = qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname();
				if( dim.isDateDim() && minDate != null && dim.getAlias().equals(minDate.getAlias())) {
					if("zdz".equals(jsType)){  //计算 和指定值做比较
						if("gdz".equals(dateCompare.getValtype())){  //和固定值比较
							sql.append(" and " + col + " = '"+dateCompare.getVal()+"' ");
						}else{  //和参数比较
							//区间参数
							if("datetime".equals(dateCompare.getParamType())){  //比较时间区间
								sql.append(" and " + col + " between '$" + dateCompare.getParam() + "_start' and '$" + dateCompare.getParam() + "_end' ");  //比较单个值
							}else {
								sql.append(" and " + col + " = '$" + dateCompare.getParam() + "' ");  //比较单个值
							}
						}
					}else {
						String start = minDate.getAlias() + "_start";
						String end = minDate.getAlias() + "_end";
						sql.append("#if($" + start + " != '' and $" + end + " != '') \n");
						sql.append(" and " + col + " between '$myUtils.shiftDate($" + start + ", '" + tp + "', '" + minDate.getDateformat() + "', '" + jsType + "', 1)'");
						sql.append(" and '$myUtils.shiftDate($" + end + ", '" + tp + "', '" + minDate.getDateformat() + "', '" + jsType + "', 2)' ");
						sql.append(" #end \n");
					}
				}
				if("frd".equals(tp)){
					if(dim.getVals() != null && dim.getVals().size() > 0){
						String vls =  RSBIUtils.dealStringParam(dim.getVals(), "string".equalsIgnoreCase(dim.getValType()));
						sql.append(" and " + (dim.getTableColKey() != null && dim.getTableColKey().length() > 0 ? dim.getTableColKey() : (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname())) + " in ("+vls+")");
					}
				}
			}
		}else {
			for(int i=0; i<dims.size(); i++){
				DimDto dim = dims.get(i);
				//处理日期限制
				if(dim.getType().equals("day")){
					if(dim.getDay() != null){
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " between '"+dim.getDay().getStartDay()+"' and '" + dim.getDay().getEndDay()+"'");
					}else
					if(dim.getVals() != null && dim.getVals().size() > 0){
						String vls =  RSBIUtils.dealStringParam(dim.getVals(), true);
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " in ("+vls+")");
					}
				}else
				if(dim.getType().equals("month")){
					if(dim.getMonth() != null){
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " between '"+dim.getMonth().getStartMonth()+"' and '" + dim.getMonth().getEndMonth()+"'");
					}else
					if(dim.getVals() != null && dim.getVals().size() > 0){
						String vls = RSBIUtils.dealStringParam(dim.getVals(), true);
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " in ("+vls+")");
						//isDealDate = true;
					}
				}else{
					//限制维度筛选
					if(dim.getVals() != null && dim.getVals().size() > 0){
						String vls =  RSBIUtils.dealStringParam(dim.getVals(), "string".equalsIgnoreCase(dim.getValType()));
						sql.append(" and " + (dim.getTableColKey() != null && dim.getTableColKey().length() > 0 ? dim.getTableColKey() : (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname())) + " in ("+vls+")");
					}
				}
			}
		}

		//限制参数的查询条件
		String condition = super.dealCubeParams(chartJson.getCompParams(), chartJson.getUseIn(),"dim", tinfo, jsType, dateCompare, minDate);
		if("ds".equals(tinfo.getIncome()) && sql.indexOf(DatasetService.cond)  >= 0) {
			sql = new StringBuffer(datasetService.replaceSql(sql.toString(), condition));  //替换参数到数据集中
		}else{
			sql.append(condition);
		}
		sql.append(super.dealDashboardParams(chartJson.getDashboardParam(), tinfo));
		//处理仪表盘事件筛选
		if("dashboard".equals(chartJson.getUseIn())) {  //处理仪表盘事件接收
			super.dealLinkAccept(chartJson.getChartJson().getLinkAccept(), sql);
		}
		//
		//if(release == 1){  //报表发布模式（展现模式）下出现联动
		if(!"dashboard".equals(chartJson.getUseIn())) {
			LinkAcceptDto linkAccept = chartJson.getChartJson().getLinkAccept();
			if(linkAccept != null && linkAccept.getId() != null) {
				String col = linkAccept.getExpression() == null || linkAccept.getExpression().length() == 0 ? linkAccept.getCol() : linkAccept.getExpression();
				String valtype = linkAccept.getValType();
				String val = linkAccept.getDftval();
				if ("dashboard".equals(chartJson.getUseIn()) && val != null) {  //仪表盘和报表参数处理方式不一样,仪表盘直接传值
					if ("string".equalsIgnoreCase(valtype) || "Date".equalsIgnoreCase(valtype) || "Datetime".equalsIgnoreCase(valtype)) {
						val = "'" + val + "'";
					}
					sql.append(" and " + col + " = " + val);
				} else {
					//根据关联去找 参数名称
					String compId = chartJson.getId();
					String alias = super.findEventParamName(compId);
					if(alias != null){
						String ncol = "$" + alias;
						if ("string".equalsIgnoreCase(valtype) || "Date".equalsIgnoreCase(valtype) || "Datetime".equalsIgnoreCase(valtype)) {
							ncol = "'" + ncol + "'";
						}
						sql.append("#if($" + alias + " && $" + alias + " != '') and  " + col + " = " + ncol + " #end");
					}
				}
				//}
			}
		}

		if(dims.size() > 0){
			sql.append(" group by ");
			for(int i=0; i<dims.size(); i++){
				DimDto dim = dims.get(i);
				String key = dim.getTableColKey();
				String txt = dim.getTableColName();
				if(key != null && txt != null && key.length() >0 && txt.length() >0){
					sql.append(key+", " + txt);
				}else{
					sql.append(tinfo.isEs()?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname());
				}

				if(i != dims.size() - 1){
					sql.append(",");
				}
			}
			if(tinfo.isVertical()) {
				if(dims.size() > 0) {
					sql.append(",");
				}
				sql.append(tinfo.getKpiCodeColumn());
			}
		}else{
			if(tinfo.isVertical()) {
				sql.append(" group by ");
				sql.append(tinfo.getKpiCodeColumn());
			}
		}
		//处理指标过滤
		StringBuffer filter = new StringBuffer("");
		for(KpiDto kpi : kpis){
			if(kpi.getFilter() != null){
				filter.append(" and "+(tinfo.isEs()?kpi.getAlias():kpi.getCol_name())+" ");
				String tp = kpi.getFilter().getFilterType();
				filter.append(tp);
				filter.append(" ");
				double val1 = kpi.getFilter().getVal1();
				if(kpi.getFmt() != null && kpi.getFmt().endsWith("%")){
					val1 = val1 / 100;
				}
				filter.append(val1 * (kpi.getRate() == null ? 1 : kpi.getRate()));
				if("between".equals(tp)){
					double val2 = kpi.getFilter().getVal2();
					if(kpi.getFmt() != null && kpi.getFmt().endsWith("%")){
						val2 = val2 / 100;
					}
					filter.append(" and " + val2 * (kpi.getRate() == null ? 1 : kpi.getRate()));
				}
			}
		}
		filter.append(super.dealCubeParams(chartJson.getCompParams(), "kpi", tinfo)); //处理参数中的指标筛选
		if(filter.length() > 0){
			sql.append(" having 1=1 " + filter);
		}
		String ret = sql.toString();
		//替换 ## 为 函数，##在velocity中为注释意思
		//ret = ret.replaceAll("##", "\\$extUtils.printJH()");
		return ret;
	}

	/**
	 * 创建图形的dataCenter
	 * @param chart
	 * @param sql
	 * @return
	 * @throws IOException
	 */
	public GridDataCenterContext createDataCenter(PortalChartQuery chart, String sql) throws IOException {
		DateDimDto minDate = null; //最小时间维度，用在计算同环比/累计中
		GridDataCenterContext ctx = new GridDataCenterContextImpl();
		GridSetConfContext conf = new GridSetConfContext();
		//判断是否通过 elasticsearch 查询
		TableInfoVO tinfo = this.cacheService.getTableInfo(chart.getTid());
		if(tinfo.isRest()) {
			conf.setDatasetProvider(restService);
			conf.setMaster(String.valueOf(chart.getTid()));  //rest查询，master存 tid
		}
		conf.setCache(dcCacheService);
		conf.setUseCache(false);
		if (!(tinfo.getDsourceId() == null || tinfo.getDsourceId() == -1)) {
			conf.setRefDsource("ds-" + tinfo.getDsourceId());
		}
		ctx.setConf(conf);
		ctx.setId("DC-" + IdCreater.create());
		//判断是否计算衍生指标（同比，环比，累计等）
		List<String> jsTypes = chart.getKpiComputeType();
		if(jsTypes.size() == 0) {
			String name = TemplateManager.getInstance().createTemplate(sql);
			ctx.getConf().setTemplateName(name);
		} else {
			//设置多数据集，同比，环比，累计分别对应一个数据集。 主数据集为主SQL
			//通过masterProcess 先获取最小时间维度, 获取最小时间维度得最大，最小值
			List<DateDimDto> dateDims = chart.getDateDims(this.scripts);
			Collections.sort(dateDims, (o1, o2) -> {
					Integer v1 = type2value(o1.getType());
					Integer v2 = type2value(o2.getType());
					return -v1.compareTo(v2);
			});
			if(dateDims.size() == 0) {
				throw new RuntimeException("无时间维度，不能进行计算。");
			}
			minDate = dateDims.get(dateDims.size() - 1);
			MatchDateKeyService ser = new MatchDateKeyService(dateDims);
			conf.setMasterProcess(ser);

			//每个计算设置一个DS
			List<MultiDsContext> mds = new ArrayList<MultiDsContext>();
			MultiDsContext master = new MultiDsContext();
			master.setKey("master");
			String name = TemplateManager.getInstance().createTemplate(sql);
			master.setTemplateName(name);
			master.setTname(tinfo.getTname());
			master.setMaster(true);
			master.setTid(tinfo.getTid());
			mds.add(master);
			for(String jsType : jsTypes) {
				if(jsType.equals("zdz")) {  //指标计算-比较指定日期
					List<DateCompareDto> compareDtos = chart.getKpiCompareDtos();
					for(DateCompareDto compareDto : compareDtos){
						MultiDsContext jsDs = this.createJsDs(jsType, compareDto, tinfo, chart, minDate);
						mds.add(jsDs);
					}
				}else{
					MultiDsContext jsDs = this.createJsDs(jsType, null, tinfo, chart, minDate);
					mds.add(jsDs);
				}
			}
			conf.setMultiDsContext(mds);
		}

		List<DimDto> dims = chart.getChartJson().getDims();
		String maparea = chart.getChartJson().getMaparea();
		String type = chart.getChartJson().getType();
		Integer typeIndex = chart.getChartJson().getTypeIndex();
		//设置Rest查询的别名
		if(tinfo.isRest()) {
			List<String> colNames = new ArrayList<>();
			List<String> ncolNames = new ArrayList<>();
			GridAliasResetContext reset = new GridAliasResetContext();
			dims.forEach(dto -> {
				String alias = dto.getAlias();
				String fromcol = dto.getFromCol();
				colNames.add(fromcol);
				ncolNames.add(alias);
			});
			if(chart.getMkpi() != null && chart.getMkpi()) {
				chart.getMkpiJson().forEach(k -> {
					String alias = k.getAlias();
					String col = k.getFromCol();
					colNames.add(col);
					ncolNames.add(alias);
				});
			}else {
				chart.getKpiJson().forEach(k -> {
					String alias = k.getAlias();
					String col = k.getFromCol();
					colNames.add(col);
					ncolNames.add(alias);
				});
			}
			reset.setColNames(colNames);
			reset.setColNewNames(ncolNames);
			ctx.getProcess().add(reset);
		}
		if("map".equals(type) && maparea != null && maparea.length() > 0 && !"china".equals(maparea) && typeIndex != 3){  //如果是地图，并且是省份地图。需要忽略其他省份数据
			DimDto curArea = null;
			for(int i=0; i<dims.size(); i++){
				DimDto dim = dims.get(i);
				if(dim.getType().equals("city")){
					if(curArea == null) {
						curArea = dim;
					}
				}else if(dim.getType().equals("town")) {
					curArea = dim;
				}
			}
			if(curArea != null) {
				GridFilterContext filter = new GridFilterContext();
				filter.setColumn(curArea.getTableColName() != null && curArea.getTableColName().length() > 0 ? curArea.getAlias()+"2" : curArea.getAlias());
				filter.setFilterType(GridFilter.in);
				List<Area> ls = null;
				if(curArea.getType().equals("city")) {
					ls = areaMapper.listCityByProvCode(maparea);
				}else if(curArea.getType().equals("town")) {
					ls = areaMapper.listTownByCityCode(maparea);
				}
				StringBuffer sb = new StringBuffer();
				for(int j=0; j<ls.size(); j++){
					Area a = ls.get(j);
					sb.append(a.getCityName() == null ? a.getTownName():a.getCityName());
					if(j != ls.size() - 1){
						sb.append(",");
					}
				}
				filter.setValue(sb.toString());
				ctx.getProcess().add(filter);  //过滤其他地市
			}
		}
		//处理排序
		List<String> orderCols = new ArrayList<>(); //需要进行排序的字段
		List<KpiDto> kpis = chart.getMkpi() != null && chart.getMkpi() && chart.getMkpiJson() != null ? chart.getMkpiJson() : chart.getKpiJson();
			for(int i=0; i<kpis.size(); i++){
				KpiDto kpi = kpis.get(i);
				if(kpi.getSort() != null && kpi.getSort().length() > 0){
					orderCols.add(kpi.getAlias() + "," + kpi.getSort()) ;
				}
			}
			for(int i=0; i<dims.size(); i++){
				DimDto dim = dims.get(i);
				if(dim.getDimord()!= null && dim.getDimord().length() > 0){
					if(dim.getOrdcol() != null && dim.getOrdcol().length() > 0){
						orderCols.add(dim.getOrdcol() + "," + dim.getDimord());
					}else{
						orderCols.add((dim.getTableColKey() != null && dim.getTableColKey().length() > 0 ?dim.getTableColKey():dim.getAlias()) + "," + dim.getDimord());
					}

				}
			}
		//}
		if(orderCols.size() > 0){
			GridSortContext sort = new GridSortContext();
			String[] cols = new String[orderCols.size()];
			String[] types = new String[orderCols.size()];
			for(int i=0; i<orderCols.size(); i++){
				String[] strs = orderCols.get(i).split(",");
				cols[i] = strs[0];
				types[i] = strs[1];
			}
			sort.setColumn(cols);
			sort.setType(types);
			sort.setChangeOldOrder(true);
			ctx.getProcess().add(sort);
		}
		//计算上期，同期，累计, 与指定时间比
		for(String js : jsTypes) {
			if("zdz".equals(js)) {  //指标计算 - 比较指定日期
				List<DateCompareDto> compareDtos = chart.getKpiCompareDtos();
				for(DateCompareDto compareDto : compareDtos){
					GridProcContext proc = this.createShift(chart, chart.getMkpi() != null && chart.getMkpi() ? chart.getMkpiJson() : chart.getKpiJson(), js, compareDto, minDate);
					ctx.getProcess().add(proc);
				}
			}else {
				GridProcContext proc = this.createShift(chart, chart.getMkpi() != null && chart.getMkpi() ? chart.getMkpiJson() : chart.getKpiJson(), js, null, minDate);
				ctx.getProcess().add(proc);
			}
		}
		return ctx;
	}

	/**
	 * 创建指标计算的数据集
	 * @param jsType
	 * @param tinfo
	 * @param chart
	 * @param minDate
	 * @return
	 * @throws IOException
	 */
	private MultiDsContext createJsDs(String jsType,DateCompareDto dateCompare, TableInfoVO tinfo, PortalChartQuery chart, DateDimDto minDate) throws IOException {
		MultiDsContext jsDs = new MultiDsContext();
		//设置 jsDs 的key ，通过key 关联数据集和指标计算
		if("zdz".equals(jsType)){
			jsDs.setKey(jsType+("gdz".equals(dateCompare.getValtype()) ? dateCompare.getVal():dateCompare.getParam()));
		}else {
			jsDs.setKey(jsType);  //如果是zdz， key 为 zdz_value
		}
		String jsSql = this.createSql(chart, null, minDate, jsType, dateCompare, tinfo);

		String jsTemplate = TemplateManager.getInstance().createTemplate(jsSql);
		jsDs.setTemplateName(jsTemplate);
		jsDs.setTname(tinfo.getTname());
		jsDs.setTid(tinfo.getTid());
		return jsDs;
	}

	/**
	 * 创建时间偏移process,时间偏移用来计算同比、环比、上期、同期等值
	 * @param dto
	 * @param kpis
	 * @return
	 */
	public GridProcContext createShift(ChartQueryDto dto, List<KpiDto> kpis, String compute, DateCompareDto compareDto, DateDimDto minDate) {
		//判断表中是否有时间维度
		boolean hasDateDimInTabele = false;
		GridShiftContext proc = new GridShiftContext();
		proc.setComputeType(compute);
		if("zdz".equals(compute)){
			if("gdz".equals(compareDto.getValtype())) {
				proc.setCompareValue(compareDto.getVal());
				proc.setCompareValueType(1);
			}else{
				proc.setCompareValue(compareDto.getParam());
				proc.setCompareValueType(2);
			}
		}
		if("zdz".equals(compute)){
			proc.setDataKey(compute+("gdz".equals(compareDto.getValtype()) ? compareDto.getVal():compareDto.getParam()));
		}else {
			proc.setDataKey(compute);
		}
		String[] kpiColumn = new String[kpis.size()];
		for(int i=0; i<kpis.size(); i++) {
			kpiColumn[i] = kpis.get(i).getAlias();
		}
		proc.setKpiColumn(kpiColumn);
		//设置过滤维度
		StringBuffer sb = new StringBuffer("");
		for(DimDto dim : dto.getDims()){
			String tp = dim.getType();
			if("year".equals(tp) || "quarter".equals(tp) || "month".equals(tp) || "day".equals(tp)){
				hasDateDimInTabele = true;
				continue;
			}
			String key = dim.getTableColKey();
			String txt = dim.getTableColName();
			if(key != null && txt != null && key.length() >0 && txt.length() >0){
				sb.append(dim.getAlias());
				sb.append(",");
				sb.append(dim.getAlias()+"2");
				sb.append(",");
			}else {
				sb.append(dim.getAlias());
				sb.append(",");
			}
		}
		if(sb.length() > 0){
			String str = sb.substring(0, sb.length() - 1);
			proc.setKeyColumns(str.split(","));
		}
		//只有表中有时间维度，才需要设置时间字段
		if(hasDateDimInTabele) {
			proc.setDateType(minDate.getType());
			proc.setDateFormat(minDate.getDateformat());
			proc.setDateColumn(minDate.getAlias());
		}
		return proc;
	}

	public Object queryChartColors(){
		AbstractChartEmitter.ColorVO[] vls = AbstractChartEmitter.ColorVO.values();
		String[] v = new String[vls.length];
		for(int i=0; i<vls.length; i++){
			AbstractChartEmitter.ColorVO c = vls[i];
			v[i] = c.toString();
		}
		return v;
	}

	public Map<String, InputField> getMvParams() {
		return mvParams;
	}

}
