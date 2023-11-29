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
import com.rsbi.ext.engine.view.context.chart.ChartContext;
import com.rsbi.ext.engine.view.context.dc.grid.*;
import com.rsbi.ext.engine.view.context.form.InputField;
import com.rsbi.ext.engine.view.context.html.*;
import com.rsbi.ispire.dc.grid.GridProcContext;
import com.ruisitech.bi.entity.bireport.*;
import com.ruisitech.bi.entity.portal.BoxQuery;
import com.ruisitech.bi.entity.portal.PortalChartQuery;
import com.ruisitech.bi.service.bireport.BaseCompService;
import com.ruisitech.bi.service.bireport.DataCenterCacheService;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.QueryRestService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.ext.service.DataControlInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class BoxService extends BaseCompService {

	public final static String deftMvId = "mv.portal.box";

	private Map<String, InputField> mvParams = new HashMap<String, InputField>(); //mv的参数

	@Autowired
	private DataControlInterface dataControl; //数据权限控制

	@Autowired
	private TableCacheService cacheService;

	@Autowired
	private PortalChartService chartService;


	@Autowired
	private DataCenterCacheService dcCacheService;

	@Autowired
	private QueryRestService restService;

	@Autowired
	private DatasetService datasetService;

	public MVContext json2MV(BoxQuery box) throws Exception{
		TableInfoVO tvo = null;
		if(box.getTid() != null) {
			tvo = cacheService.getTableInfo(box.getTid());
			box.checkSql(tvo);
		}
		//创建MV
		MVContext mv = new MVContextImpl();
		mv.setChildren(new ArrayList<Element>());
		String formId = ExtConstants.formIdPrefix + IdCreater.create();
		mv.setFormId(formId);
		mv.setMvid(deftMvId);
		mv.setHideMV(true);

		//处理参数,把参数设为hidden
		parserHiddenParam(box.getPortalParams(), mv, mvParams, box.getUseIn());

		this.json2Box(box, mv, mv);

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

	/**
	 * 通过数据生成 box 块
	 * @param mv
	 * @throws IOException
	 */
	public void json2Box(BoxQuery box, Element curEle, MVContext mv) throws IOException {
		if (box.getKpiJson() == null || box.getKpiJson().getKpi_id() == null) {
			return;
		}

		Integer tid = box.getTid();
		GridDataCenterContext dc = null;
		if (tid == null) {
			dc = super.createEmptyDataCenter(box.getId());
		}else if(box.getThbDim() == null){
			TableInfoVO tinfo = cacheService.getTableInfo(tid);
			String  sql = tinfo.isRest() ? this.createRestSql(box.getCompParams(), box.getLinkAccept(), tinfo) : createSql(box, null, tinfo, false);
			dc = this.createDataCenter(sql, box, tinfo);
		}else if(box.getThbDim() != null) {
			//创建同环比的数据中心
			dc = this.createThbDataCenter(box);
		}
		if (mv.getGridDataCenters() == null) {
			mv.setGridDataCenters(new HashMap<>());
		}
		mv.getGridDataCenters().put(dc.getId(), dc);
		//创建box 的 data 标签
		DataContext data = new DataContextImpl();
		data.setKey("k" + box.getKpiJson().getKpi_id());
		data.setRefDataCenter(dc.getId());
		curEle.getChildren().add(data);
		data.setParent(curEle);

		//创建box 显示 text 标签
		KpiDto kpi = box.getKpiJson();
		TextContext text = new TextContextImpl();
		text.setId(box.getId()); //组件的ID
		Integer id = box.getKpiJson().getKpi_id();
		String alias = box.getKpiJson().getAlias();
		String p1 = id+"."+alias;
		String alias2 = box.getKpiJson().getAlias()+"_sq";
		String p2 = id + "." + alias2;
		String fmt =box.getKpiJson().getFmt();
		Integer rate = kpi.getRate();
		String str = "#if($outType=='json')\n";  //对于json输出，进行特殊处理
		str += " {\"trueValue\":#if($!k"+p1+") "+"$!k"+p1+" #else null #end, value:\"";

		String func = kpi.getFuncname();
		String code = kpi.getCode();
		//调用回调函数
		if(code != null && code.length() > 0 && func != null && func.length() > 0) {
			this.scripts.append("function "+func+"(value, row){"+RSBIUtils.unescape(code)+"}");
			str += "$extUtils.callFunction($!k" + p1 + ", $!k"+id+",'" + func + "', $request)";
		}else{
			str += "$extUtils.numberFmt($!k"+p1+", '"+(fmt==null?"":fmt)+"')";
		}
		str += "\", alias:\""+alias+"\", unit:'"+kpi.getUnit()+"', rate:"+rate+", desc:\""+kpi.getKpi_name() + "\"";
		//处理同环比
		if(box.getThbDim() != null && box.getThbDim().getId() != null) {
			str += ",tb:" + "#if($!k"+p1+"_tb) $!{k"+p1+"_tb} #else null #end";
			str += ",hb:" + "#if($!k"+p1+"_hb) $!{k"+p1+"_hb} #else null #end";
		}
		str += "}";
		str += "#else\n";
		if(box.getProgressBar() != null && box.getProgressBar()) {   //显示成进度条, 只能显示 %
			str += "#set($b_val  = $!k"+p1+") \n";
			str += "$extUtils.numberFmt($b_val, '0.00%') \n";
		}else {
			//调用回调函数
			if(code != null && code.length() > 0 && func != null && func.length() > 0) {
				this.scripts.append("function "+func+"(value, row){"+RSBIUtils.unescape(code)+"}");
				str += "$extUtils.callFunction($!k" + p1 + ", $!k"+id+",'" + func + "', $request)";
			}else {
				str += "#if($!k" + p1 + ") $extUtils.numberFmt($!k" + p1 + ", '" + (fmt == null ? "" : fmt) + "') \n";
				if (rate != null) {
					str += writerUnit(rate);
				}
				String unit = box.getKpiJson().getUnit();
				str += (unit != null && unit.length() > 0 ? unit : "");
				str += "#else '' #end\n";
			}
		}

		str += "#end";
		String word = TemplateManager.getInstance().createTemplate(str);
		text.setTemplateName(word);
		text.setFormatHtml(true);
		curEle.getChildren().add(text);
		text.setParent(curEle);
		//定义字体样式（用在html导出中）
		TextProperty tp = new TextProperty();
		//带图形
		if(box.getChart() != null || (box.getProgressBar() != null && box.getProgressBar()) || box.getThbDim() != null) {
			tp.setHeight("60");
			tp.setLineHeight(60);
			if(box.getThbDim() != null){
				tp.setAlign("center");
			}else{
				tp.setAlign("left");
			}
		}else {
			tp.setHeight("120");
			tp.setLineHeight(120);
			tp.setAlign("center");
		}
		tp.setSize("32");
		text.setTextProperty(tp);

		//添加同环比
		if(box.getThbDim() != null) {
			String s = "#if($outType=='html')";
			s += "<div class=\"stat-percent font-bold\"> "
					+ "#if($!k"+p1+"_tb) $extUtils.numberFmt(${k"+p1+"_tb},'0.00%') "
					+ "<i class=\"fa #if($k"+p1+"_tb < 0) fa-long-arrow-down text-info #else fa-long-arrow-up text-danger #end \"></i> #else - #end<br/>"
					+ "#if($!k"+p1+"_hb) $extUtils.numberFmt(${k"+p1+"_hb}, '0.00%') "
					+ "<i class=\"fa #if($k"+p1+"_hb < 0) fa-long-arrow-down text-info #else fa-long-arrow-up text-danger #end \"></i> #else - #end"
					+ "</div>";
			s += "<small>同比：<br/>环比：</small>";
			s += "#else";
			s += "同比：#if($!k"+p1+"_tb) $extUtils.numberFmt(${k"+p1+"_tb},'0.00%') #else - #end";
			s += "环比：#if($!k"+p1+"_hb) $extUtils.numberFmt(${k"+p1+"_hb},'0.00%') #else - #end";
			s += "#end";
			TextContext thbText = new TextContextImpl();
			thbText.setDealInJsonOutput(false);
			String thbStr = TemplateManager.getInstance().createTemplate(s);
			thbText.setTemplateName(thbStr);
			TextProperty thbTp = new TextProperty();
			thbTp.setStyleClass("box-thb");
			thbTp.setStyle("height:60px;line-height:25px;");
			thbText.setTextProperty(thbTp);
			curEle.getChildren().add(thbText);
			thbText.setParent(curEle);
		}
	}

	public String createSql(BoxQuery box, List<VerticalKpiDto> verKpis, TableInfoVO tinfo, boolean thb){
		boolean qEs = tinfo.isSyncEs();
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		if(box.getThbDim() != null && box.getThbDim().getTid() != null) {
			DimDto dim = box.getThbDim();
			String key = dim.getTableColKey();
			String txt = dim.getTableColName();
			if(key != null && txt != null && key.length() >0 && txt.length() >0){
				sql.append(key+" "+dim.getAlias()+", " + txt + " "+dim.getAlias()+"2");
			}else{
				sql.append(" "+(qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname())+" "+dim.getAlias()+" ");
			}
			sql.append(",");
		}
		if(tinfo.isVertical()) {
			sql.append(tinfo.getKpiCodeColumn());
			sql.append(",");
			super.countDistinct(verKpis.get(0), sql, tinfo);
		}else {
			sql.append(box.getKpiJson().getCol_name());
			Integer rate = box.getKpiJson().getRate();
			if(rate != null ){
				sql.append("/" + rate);
			}
			sql.append(" as ");
			sql.append(box.getKpiJson().getAlias());
		}
		sql.append(" from ");
		sql.append(datasetService.createTableSql(tinfo, false));
		sql.append(" where 1=1 ");
		//如果是纵表，过滤查询字段
		if(tinfo.isVertical()) {
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
		if(dataControl != null){
			String ret = dataControl.process(RSBIUtils.getLoginUserInfo(), tinfo);
			if(ret != null){
				sql.append(ret + " ");
			}
		}
		sql.append(" ");
		DateDimDto dateDimDto = null;
		if(thb){
			dateDimDto = new DateDimDto();
			dateDimDto.setType(box.getThbDim().getType());
			dateDimDto.setDateformat(box.getThbDim().getDateformat());
			dateDimDto.setAlias(box.getThbDim().getAlias());
		}
		//String cond = super.dealCubeParams(box.getCompParams(), box.getUseIn(), "dim", tinfo);
		String cond = super.dealCubeParams(box.getCompParams(), box.getUseIn(), "dim", tinfo, thb?"tb":null, null, dateDimDto);
		if("ds".equals(tinfo.getIncome()) && sql.indexOf(DatasetService.cond)  >= 0){
			sql = new StringBuffer(datasetService.replaceSql(sql.toString(), cond));  //替换参数到数据集中
		}else{
			sql.append(cond);
		}
		sql.append(super.dealDashboardParams(box.getDashboardParam(), tinfo));
		if("dashboard".equals(box.getUseIn())) {  //处理仪表盘事件接收
			super.dealLinkAccept(box.getLinkAccept(), sql);
		}
		if(box.getThbDim() != null && box.getThbDim().getTid() != null) {
			sql.append(" group by ");
			DimDto dim = box.getThbDim();
			String key = dim.getTableColKey();
			String txt = dim.getTableColName();
			if(key != null && txt != null && key.length() >0 && txt.length() >0){
				sql.append(key+", " + txt);
			}else{
				sql.append(tinfo.isEs()?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname());
			}
			if(tinfo.isVertical()) {
				sql.append(",");
				sql.append(tinfo.getKpiCodeColumn());
			}
		}else {
			if(tinfo.isVertical()) {
				sql.append(" group by " + tinfo.getKpiCodeColumn());
			}
		}
		return sql.toString();
	}

	/**
	 * 创建表格datacenter
	 * @param sql
	 * @return
	 * @throws IOException
	 */
	public GridDataCenterContext createDataCenter(String sql, BoxQuery box, TableInfoVO tinfo) throws IOException{
		GridDataCenterContext ctx = new GridDataCenterContextImpl();
		GridSetConfContext conf = new GridSetConfContext();
		if(tinfo.isRest()) {
			conf.setDatasetProvider(restService);
			conf.setMaster(String.valueOf(box.getTid()));  //rest查询，master存 tid
		}
		conf.setCache(dcCacheService);
		conf.setUseCache(false);
		if (!(tinfo.getDsourceId() == null || tinfo.getDsourceId() == -1)) {
			conf.setRefDsource("ds-" + tinfo.getDsourceId());
		}
		ctx.setConf(conf);
		ctx.setId("DC-" + IdCreater.create());
		String name = TemplateManager.getInstance().createTemplate(sql);
		ctx.getConf().setTemplateName(name);

		//设置Rest查询的别名
		if(tinfo.isRest()) {
			List<String> colNames = new ArrayList<>();
			List<String> ncolNames = new ArrayList<>();
			GridAliasResetContext reset = new GridAliasResetContext();
			KpiDto kpi = box.getKpiJson();
			String alias = kpi.getAlias();
			String col = kpi.getFromCol();
			colNames.add(col);
			ncolNames.add(alias);
			reset.setColNames(colNames);
			reset.setColNewNames(ncolNames);
			ctx.getProcess().add(reset);
		}


		//如果有同环比,倒叙排序
		if(box.getThbDim() != null && box.getThbDim().getTid() != null) {
			DimDto dim = box.getThbDim();
			GridSortContext sort = new GridSortContext();
			String[] cols = new String[] {dim.getAlias()};
			String[] types = new String[] {"desc"};
			sort.setColumn(cols);
			sort.setType(types);
			sort.setChangeOldOrder(true);
			ctx.getProcess().add(sort);
		}

		/**
		GridTopContext top = new GridTopContext();
		top.setFrom(0);
		top.setTo(1);
		ctx.getProcess().add(top);
		**/
		//如果有箭头，需要本期，上期数据，第二行数据为 上期
		if(box.getUseArrow() != null && box.getUseArrow()) {
			KpiArrowContext arrow = new KpiArrowContext();
			List<String> kpis = new ArrayList<>();
			kpis.add(box.getKpiJson().getAlias());
			arrow.setKpiCol(kpis);
			ctx.getProcess().add(arrow);
		}


		return ctx;
	}

	public GridDataCenterContext createThbDataCenter(BoxQuery box) throws IOException{
		TableInfoVO tinfo = cacheService.getTableInfo(box.getTid());
		String sql = tinfo.isRest() ? this.createRestSql(box.getCompParams(), box.getLinkAccept(), tinfo) : createSql(box, null, tinfo, false);

		GridDataCenterContext ctx = new GridDataCenterContextImpl();
		GridSetConfContext conf = new GridSetConfContext();
		if(tinfo.isRest()) {
			conf.setDatasetProvider(restService);
			conf.setMaster(String.valueOf(box.getTid()));  //rest查询，master存 tid
		}
		conf.setCache(dcCacheService);
		conf.setUseCache(false);
		if(tinfo.getDsourceId() != null) {
			conf.setRefDsource("ds-" + tinfo.getDsourceId());
		}
		ctx.setConf(conf);
		ctx.setId("DC-" + IdCreater.create());
		//String name = TemplateManager.getInstance().createTemplate(sql);
		//ctx.getConf().setTemplateName(name);

		List<MultiDsContext> mds = new ArrayList<>();
		MultiDsContext master = new MultiDsContext();
		master.setKey("master");
		String name = TemplateManager.getInstance().createTemplate(sql);
		master.setTemplateName(name);
		master.setTname(tinfo.getTname());
		master.setMaster(true);
		master.setTid(tinfo.getTid());
		mds.add(master);

		String jssql = tinfo.isRest() ? this.createRestSql(box.getCompParams(), box.getLinkAccept(), tinfo) : createSql(box, null, tinfo, true);


		MultiDsContext client = new MultiDsContext();
		client.setKey("thb");
		String clientTemplate = TemplateManager.getInstance().createTemplate(jssql);
		client.setTemplateName(clientTemplate);
		client.setTname(tinfo.getTname());
		client.setTid(tinfo.getTid());
		mds.add(client);

		conf.setMultiDsContext(mds);

		//设置Rest查询的别名
		if(tinfo.isRest()) {
			List<String> colNames = new ArrayList<>();
			List<String> ncolNames = new ArrayList<>();
			GridAliasResetContext reset = new GridAliasResetContext();
			KpiDto kpi = box.getKpiJson();
			String alias = kpi.getAlias();
			String col = kpi.getFromCol();
			colNames.add(col);
			ncolNames.add(alias);
			reset.setColNames(colNames);
			reset.setColNewNames(ncolNames);
			ctx.getProcess().add(reset);
		}

		//创建排序
		DimDto dim = box.getThbDim();
		GridSortContext sort = new GridSortContext();
		String[] cols = new String[] {dim.getAlias()};
		String[] types = new String[] {"desc"};
		sort.setColumn(cols);
		sort.setType(types);
		sort.setChangeOldOrder(true);
		ctx.getProcess().add(sort);

		//计算同环比
		GridProcContext tb = this.createShift(box, "tb");
		GridProcContext hb = this.createShift(box, "hb");
		ctx.getProcess().add(tb);
		ctx.getProcess().add(hb);

		//只取一行
		GridTopContext top = new GridTopContext();
		top.setFrom(0);
		top.setTo(1);
		ctx.getProcess().add(top);

		return ctx;
	}

	/**
	 * 创建时间偏移process,时间偏移用来计算同比、环比、上期、同期等值
	 * @param box
	 * @param compute
	 * @return
	 */
	private GridProcContext createShift(BoxQuery box, String compute){
		//查询最小时间维度
		int minDate = 10;
		DimDto minDim = null;
		DimDto dim = box.getThbDim();
		String tp = dim.getType();
		int curDate = type2value(tp);
		if(curDate <= minDate){
			minDim = dim;
		}
		GridShiftContext proc = new GridShiftContext();
		proc.setDateType(minDim.getType());
		proc.setDateFormat(minDim.getDateformat());
		proc.setDateColumn(minDim.getAlias());
		proc.setComputeType(compute);
		proc.setKpiColumn(new String[]{box.getKpiJson().getAlias()});
		proc.setDataKey("thb");
		return proc;
	}

	public Map<String, InputField> getMvParams() {
		return mvParams;
	}
}
