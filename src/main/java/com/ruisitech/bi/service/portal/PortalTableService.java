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
import com.rsbi.ext.engine.view.context.cross.*;
import com.rsbi.ext.engine.view.context.dc.grid.*;
import com.rsbi.ext.engine.view.context.form.InputField;
import com.rsbi.ispire.dc.grid.GridDataMetaData;
import com.rsbi.ispire.dc.grid.GridProcContext;
import com.ruisitech.bi.entity.bireport.*;
import com.ruisitech.bi.entity.portal.*;
import com.ruisitech.bi.service.bireport.BaseCompService;
import com.ruisitech.bi.service.bireport.DataCenterCacheService;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.QueryRestService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.ext.service.DataControlInterface;
import com.ruisitech.ext.service.MatchDateKeyService;
import com.ruisitech.ext.service.MyCrossFieldLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class PortalTableService extends BaseCompService {

	public final static String deftMvId = "mv.portal.table";

	private Map<String, InputField> mvParams = new HashMap<String, InputField>(); //mv的参数

	@Autowired
	private DataControlInterface dataControl; //数据权限控制

	@Autowired
	private TableCacheService cacheService;

	@Autowired
	private DataCenterCacheService dcCacheService;

	@Autowired
	private QueryRestService restService;

	@Autowired
	private DatasetService datasetService;

	private List<CrossFieldDto> calcNodes = new ArrayList<>();  //需要计算的节点

	/**
	 * 门户使用的JSON2MV对象
	 */
	public MVContext json2MV(PortalTableQuery table) throws Exception{
		TableInfoVO tvo = null;
		//检测SQL注入
		if(table.getTid() != null) {
			tvo = cacheService.getTableInfo(table.getTid());
			table.checkSql(tvo);
		}
		//创建MV
		MVContext mv = new MVContextImpl();
		mv.setChildren(new ArrayList<Element>());
		String formId = ExtConstants.formIdPrefix + IdCreater.create();
		mv.setFormId(formId);
		mv.setMvid(deftMvId);
		mv.setHideMV(true);

		//处理参数,把参数设为hidden
		super.parserHiddenParam(table.getPortalParams(), mv, this.mvParams, table.getUseIn());

		//是否添加排名字段 ranking
		if(table.getRanking() != null && table.getRanking()) {
			DimDto sortDim = new DimDto();
			sortDim.setType("rank");
			sortDim.setDimdesc("序号");
			sortDim.setAlias("sortKey");
			if(table.getRows().size() > 0) {
				DimDto dim = this.findDimById(table.getRows().get(0).getMatch(), table.getDims());
				sortDim.setTop( dim.getTop() != null ? dim.getTop() : 10);
				sortDim.setTopType("number");
				sortDim.setId(0);
				table.getDims().add(sortDim);

				//添加rows
				CrossFieldDto dto = new CrossFieldDto();
				dto.setId(UUID.randomUUID().toString());
				dto.setMatch(0);
				dto.setType("dim");
				dto.setDesc("序号");
				dto.setLevel(1);
				dto.setChildren(new ArrayList<>());
				table.getRows().add(0, dto);
				CrossFieldDto lvl2 = table.getRows().get(1);
				lvl2.setLevel(lvl2.getLevel() + 1);
				table.getRows().remove(lvl2);
				dto.getChildren().add(lvl2);

				//添加 crosshead
				CrossHeadDto head = new CrossHeadDto();
				head.setDesc("序号");
				head.setWidth(90);
				table.getRowHeads().add(0, head);
			}
		}

		CrossReportContext cr = null;
		cr = json2Table(table);
		//处理kpiOther
		/**
		CrossKpi mybaseKpi = null;
		List<DimDto> cols = table.getCols();
		if(cols.size() > 0 && table.getKpiJson().size() == 1){
			//如果只有一个指标，并且具有列维度，放入baseKpi
			KpiDto kpi = table.getKpiJson().get(0);
			CrossKpi baseKpi = new BaseKpiField();
			baseKpi.setAggregation(kpi.getAggre());
			baseKpi.setAlias(kpi.getAlias());
			baseKpi.setFormatPattern(kpi.getFmt());
			baseKpi.setKpiRate(kpi.getRate() == null ? null : new BigDecimal(kpi.getRate()));
			mybaseKpi = baseKpi;
			cr = json2Table(table);
			cr.setBaseKpi(mybaseKpi);
		}else{
			DimDto kpiOther = new DimDto();
			kpiOther.setType("kpiOther");
			cols.add(kpiOther);
			cr = json2Table(table);
			cols.remove(cols.size() - 1);
		}
		 **/
		//设置ID
		cr.setId(table.getId());
		cr.setOut(table.getOut());
		if(table.getColwidth() != null && table.getColwidth().length() > 0) {
			cr.setColwidth(table.getColwidth());
		}
		cr.setShowData(true);

		if(table.getRanking() != null && table.getRanking()) {
			table.getRows().remove(0);
		}

		//设置 rowHead
		if(table.getRowHeads() != null) {
			//index就是自增索引
			AtomicInteger index=new AtomicInteger(0);
			cr.setRowHeads(table.getRowHeads().stream().map(dto -> {
				RowHeadContext ctx = new RowHeadContext();
				ctx.setDesc(dto.getDesc());
				if(dto.getWidth() != null) {
					if(table.getRanking() != null && table.getRanking() && index.get() == 0){
						ctx.setWidth(70); //序号字段的默认宽度
					}else {
						ctx.setWidth(dto.getWidth());
					}
				}else{
					if(table.getColwidth() != null && !"auto".equals(table.getColwidth())){
						ctx.setWidth(new Integer(table.getColwidth()));
					}
				}
				//如果 row 节点上有回调函数，需要加到 rowHeader 上
				CrossFieldDto curRow = this.loopRows(table.getRows(), index.getAndIncrement() + 1);
				if(curRow != null) {
					DimDto dim = this.findDimById(curRow.getMatch(), table.getDims());
					if(dim != null){
						ctx.setHideNodeCallback(dim.getHideNodeCallback()); //设置隐藏字段的回调函数
					}
				}
				return ctx;
			}).collect(Collectors.toList()));
		}

		this.handleDataCenter(table, cr, mv);

		mv.getChildren().add(cr);
		cr.setParent(mv);

		//判断是否有事件，是否需要添加参数
		/**
		LinkAcceptDto linkAccept = table.getLinkAccept();
		if(linkAccept != null && linkAccept.getId() != null){
			//创建参数
			TextFieldContext linkText = new TextFieldContextImpl();
			linkText.setType("hidden");
			linkText.setDefaultValue(linkAccept.getDftval());
			linkText.setId(linkAccept.getTableColKey() == null || linkAccept.getTableColKey().length() == 0 ? linkAccept.getCol() : linkAccept.getTableColKey());
			linkText.setShow(true);
			mv.getChildren().add(0, linkText);
			linkText.setParent(mv);
			this.mvParams.put(linkText.getId(), linkText);
			ExtContext.getInstance().putServiceParam(mv.getMvid(), linkText.getId(), linkText);
			mv.setShowForm(true);
		}
		**/

		Map<String, CrossReportContext> crs = new HashMap<String, CrossReportContext>();
		crs.put(cr.getId(), cr);
		mv.setCrossReports(crs);
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

	//处理数据集和table对应关系
	public void handleDataCenter(PortalTableQuery table, CrossReportContext cr, MVContext mv) throws IOException {
		//创建datacenter
		if(table.getTid() == null) {  //模板
			GridDataCenterContext dc = this.createEmptyDataCenter(table.getId());
			cr.setRefDataCetner(dc.getId());
			if(mv.getGridDataCenters() == null){
				mv.setGridDataCenters(new HashMap<String, GridDataCenterContext>());
			}
			mv.getGridDataCenters().put(dc.getId(), dc);
		}else {  //非模板
			String sql = "";
			List<VerticalKpiDto> vkpis = null;
			TableInfoVO tinfo = this.cacheService.getTableInfo(table.getTid());
			sql = tinfo.isRest() ? super.createRestSql(table.getCompParams(), table.getLinkAccept(), tinfo) : createSql(table, null, null, null, null, 0);
			GridDataCenterContext dc = this.createDataCenter(sql, table);
			cr.setRefDataCetner(dc.getId());
			if(mv.getGridDataCenters() == null){
				mv.setGridDataCenters(new HashMap<>());
			}
			mv.getGridDataCenters().put(dc.getId(), dc);

			//判断是否有钻取维
			List<RowDimContext> drillDims = cr.getDims();
			String dsql = "";
			for(int i=0; drillDims!=null&&i<drillDims.size(); i++){
				RowDimContext drillDim = drillDims.get(i);
				//生成钻取维的DataCenter
				if(tinfo.isVertical()){
					Map<String, List<VerticalKpiDto>> aggres = vkpis.stream().collect(Collectors.groupingBy(VerticalKpiDto::getAggre));
					int idx = 0;
					for(Map.Entry<String, List<VerticalKpiDto>> aggre : aggres.entrySet()){
						dsql += createSql(table, aggre.getValue(), null, null, null, i + 1);
						if(idx != aggres.size() - 1){
							dsql += " union all ";
						}
						idx++;
					}
				}else {
					dsql = createSql(table, null, null, null, null, i+1);
				}
				GridDataCenterContext drillDc = this.createDataCenter(dsql, table);
				drillDim.setRefDataCenter(drillDc.getId());
				mv.getGridDataCenters().put(drillDc.getId(), drillDc);
			}
		}
	}

	public CrossReportContext json2Table(PortalTableQuery table) throws ParseException{
		TableInfoVO tinfo = null;
		if(table.getTid() != null) {
			tinfo = this.cacheService.getTableInfo(table.getTid());
		}
		CrossReportContext ctx = new CrossReportContextImpl();
		ctx.setHasRowPms(true);  //获取行标签的参数
		CrossCols cols = new CrossCols();
		cols.setCols(new ArrayList<CrossField>());
		ctx.setCrossCols(cols);

		CrossRows rows = new CrossRows();
		rows.setRows(new ArrayList<CrossField>());
		ctx.setCrossRows(rows);
		if(table.getMergeRow() != null) {
			ctx.getCrossRows().setUnmerge(table.getMergeRow());
		}

		ctx.setLabel(table.getId());  //给组件设置label

		boolean uselink = false;
		/**
		Map<String, Object> link = table.getLink();
		if("dashboard".equals(table.getUseIn())){   //仪表盘 点击表格行 事件
			//RowLinkContext rlink = new RowLinkContext();
			//rlink.setAction("window.dashboard.tableRowClick");  //点击行后执行的方法
			ctx.getCrossRows().setLink(rlink);
			uselink = true;
		}else{  //其他
			if(link != null && !link.isEmpty()){
				RowLinkContext rlink = new RowLinkContext();
				rlink.setParamName((String)link.get("paramName"));
				String reportId = (String)link.get("reportId");  //链接到报表
				if(url != null && url.length() >0){
					rlink.setAction("$.ext3.gotoReport");
					rlink.setOtherPms((String)link.get("reportId"));
				}else{
					String target = (String)link.get("target");
					String type = (String)link.get("type");
					rlink.setTarget(target.split(","));
					rlink.setType(type.split(","));
				}
				ctx.getCrossRows().setLink(rlink);
				uselink = true;
			}
		}
		 **/

		//表格钻取维度
		List<RowDimContext> drill = this.getDrillDim(table);
		if(drill != null && drill.size() > 0){
			ctx.setDims(drill);
			uselink = true;
		}

		//判断是否折叠父子维度
		boolean isFoldpcdim = false;
		if(table.getFoldpcdim() != null && table.getFoldpcdim()) {
			isFoldpcdim = true;
		}

		loopJsonField(table, table.getCols(), cols.getCols(), null,"col", uselink, isFoldpcdim, tinfo);

		//处理折叠父子维度, 只取一个维 (level最大的维)
		if(table.getFoldpcdim() != null && table.getFoldpcdim()) {
			CrossFieldDto dto = findLastLevelRow(table.getRows(), table);
			List<CrossFieldDto> newRows = new ArrayList<>();
			newRows.add(dto);
			table.setRows(newRows);

			//重新设置 rowHead (只有一个)
			CrossHeadDto headDto = table.getRowHeads().get(0);
			table.getRowHeads().clear();
			table.getRowHeads().add(headDto);
		}

		loopJsonField(table, table.getRows(), rows.getRows(), null,  "row", uselink, isFoldpcdim, tinfo);

		//如果没有维度，添加none维度
		if(cols.getCols().size() == 0){
			CrossField cf = new CrossField();
			cf.setType("none");
			cf.setDesc("合计");
			cols.getCols().add(cf);
		}
		if(rows.getRows().size() == 0){
			CrossField cf = new CrossField();
			cf.setType("none");
			cf.setDesc("合计");
			rows.getRows().add(cf);
		}
		return ctx;
	}

	private CrossFieldDto findLastLevelRow(List<CrossFieldDto> rows, PortalTableQuery table){
		CrossFieldDto ret = null;
		for(CrossFieldDto c : rows) {
			DimDto dim = this.findDimById(c.getMatch(), table.getDims());
			if("y".equals(dim.getIspcdim())){
				if(ret == null) {
					ret = c;
				}else if(dim.getPclevel() > this.findDimById(ret.getMatch(), table.getDims()).getPclevel()){
					ret = c;
				}
			}
			if(c.getChildren() != null && c.getChildren().size() > 0) {
				ret = this.findLastLevelRow(c.getChildren(), table);
			}
		}
		return ret;
	}

	public CrossFieldDto loopRows(List<CrossFieldDto> rows, int level){
		for(CrossFieldDto dto : rows) {
			if (dto.getLevel() == level) {
				return dto;
			}
			return this.loopRows(dto.getChildren(), level);
		}
		return null;
	}

	/**
	 * 排序的递归调用
	 * @param rows
	 * @param table
	 * @param orderCols
	 */
	public void loopNodes(List<CrossFieldDto> rows, PortalTableQuery table, List<String> orderCols){
		for(CrossFieldDto d : rows) {
			if(d.isDim()) {
				DimDto dim = this.findDimById(d.getMatch(), table.getDims());
				if (dim.getDimord() != null && dim.getDimord().length() > 0) {
					if (dim.getOrdcol() != null && dim.getOrdcol().length() > 0) {
						orderCols.add(dim.getOrdcol() + "," + dim.getDimord());
					} else {
						orderCols.add(dim.getAlias() + "," + dim.getDimord());
					}
				}
			}
			if(d.isKpi()){
				KpiDto kpi = this.findKpiById(d.getMatch(), table.getKpiJson());
				if(kpi.getSort() != null && kpi.getSort().length() > 0){
					orderCols.add(kpi.getAlias() + "," + kpi.getSort());
				}
			}
			this.loopNodes(d.getChildren(), table, orderCols);
		}
	}

	private void loopJsonField(PortalTableQuery table, List<CrossFieldDto> arrays, List<CrossField> ls, CrossField root, String pos, boolean uselink, boolean isFoldpcdim, TableInfoVO tinfo) throws ParseException{
		if(arrays == null){
			return;
		}
		for(CrossFieldDto cfDto : arrays){
			if("dim".equals(cfDto.getType())){
				DimDto obj = this.findDimById(cfDto.getMatch(), table.getDims());
				CrossField cf = new CrossField();
				cf.setType("frd"); //统一为frd
				cf.setDateType(cf.getType());
				cf.setDateTypeFmt(obj.getDateformat());
				cf.setId(String.valueOf(obj.getId()));
				cf.setDesc(cfDto.getDesc());
				cf.setSort(obj.getDimord());
				//处理节点隐藏回调
				if(obj.getHideNodeCallback() != null && obj.getHideNodeCallback().length() > 0){
					String exp = RSBIUtils.unescape(obj.getHideNodeCallback());
					String fName =  "f" + IdCreater.create();
					this.scripts.append("function ");
					this.scripts.append(fName);
					this.scripts.append("(){");
					this.scripts.append(exp);
					this.scripts.append("}");
					cf.setHideNodeFunc(fName);
					obj.setHideNodeCallback(fName); //回写回调函数名
				}
				String alias = obj.getAlias();
				String tableColKey = obj.getTableColKey();
				String tableColName = obj.getTableColName();
				if(tableColKey == null || tableColKey.length() == 0 || tableColName == null || tableColName.length() == 0){
					cf.setAlias(alias);
					cf.setAliasDesc(alias);
				}else{
					cf.setAlias(alias);
					cf.setAliasDesc(alias+"2");
				}
				cf.setCasParent(true);
				cf.setTop(obj.getTop() == null ? 1000: obj.getTop());
				if(isFoldpcdim && "y".equals(obj.getIspcdim())){
					cf.setPclevelCol(obj.getLevelCol());
					cf.setPcPidCol(obj.getPcPid());
				}
				String topType = obj.getTopType();
				if(topType != null && topType.length() > 0){
					cf.setTopType(topType);
				}
				cf.setUselink(uselink);
				cf.setValue(RSBIUtils.dealStringParam(obj.getVals(), false));
				if(obj.getColwidth() != null) {
					cf.setWidth(obj.getColwidth());
				}else{
					if(table.getColwidth() != null && !"auto".equals(table.getColwidth())){
						cf.setWidth(new Integer(table.getColwidth()));
					}
				}
				cf.setMulti(obj.getMulti());
				cf.setSubs(new ArrayList<CrossField>());
				cf.setParent(root);
				ls.add(cf);
				String issum = obj.getIssum();

				//添加合计项
				if("y".equals(issum)){
					CrossField sumcf = new CrossField();
					sumcf.setType("none");
					sumcf.setId(cf.getId()+"_agg");
					sumcf.setHideNodeFunc(cf.getHideNodeFunc());
					String aggre = obj.getAggre();
					if(aggre != null && aggre.length() > 0 && !"auto".equals(aggre)){
						sumcf.setDimAggre(aggre);
					}
					sumcf.setDesc(MyCrossFieldLoader.loadFieldName(sumcf.getDimAggre()));
					sumcf.setSubs(new ArrayList<CrossField>());
					sumcf.setParent(root);
					ls.add(sumcf);

					this.loopJsonField(table, cfDto.getChildren(), sumcf.getSubs(), sumcf, pos, uselink, isFoldpcdim, tinfo);
				}
				this.loopJsonField(table, cfDto.getChildren(), cf.getSubs(), cf, pos, uselink, isFoldpcdim, tinfo);
			}else if("kpi".equals(cfDto.getType())){
				KpiDto kpi = this.findKpiById(cfDto.getMatch(), table.getKpiJson());
				if(kpi.getHideNode() != null && kpi.getHideNode()){
					continue;
				}
				CrossField cf = new CrossField();
				cf.setType("kpiOther");
				cf.setAggregation(kpi.getAggre());
				cf.setAlias(kpi.getAlias());
				cf.setFormatPattern(kpi.getFmt());
				cf.setOrder(kpi.getOrder());
				cf.setSubs(new ArrayList<CrossField>());
				if(kpi.getColwidth() != null){
					cf.setWidth(kpi.getColwidth());
				}else{
					if(table.getColwidth() != null && !"auto".equals(table.getColwidth())) {
						cf.setWidth(new Integer(table.getColwidth()));
					}
				}
				//用 id来表示指标ID，用在OLAP中,对指标进行操作
				cf.setId(String.valueOf(kpi.getKpi_id()));
				if(kpi.getRate() != null){
					cf.setKpiRate(new BigDecimal(kpi.getRate()));
				}
				String ru = this.writerUnit(cf.getKpiRate()) +kpi.getUnit();
				if(ru != null && ru.length() > 0){
					cf.setDesc(cfDto.getDesc() + "("  + ru + ")");  //指标名称+ 单位
				}else{
					cf.setDesc(cfDto.getDesc());  //指标名称
				}

				//当回调函数和指标预警同时起作用时， 指标预警起作用
				//处理回调函数
				cf.setJsFunc(kpi.getFuncname());
				String code = kpi.getCode();
				if(code != null && code.length() > 0){
					/**
					 try {
					 code = URLDecoder.decode(code, "UTF-8");
					 } catch (UnsupportedEncodingException e) {
					 e.printStackTrace();
					 }
					 **/
					code = 	RSBIUtils.unescape(code);
					this.scripts.append("function "+cf.getJsFunc()+"(value,col,row,data,outType,rowIndex,colIndex){"+code+"}");
				}

				//处理指标预警
				Map<String, Object> warn = kpi.getWarning();
				if(warn != null && !warn.isEmpty()){
					String name = createWarning(warn, kpi.getFmt());
					cf.setJsFunc(name);
				}
				ls.add(cf);
				cf.setParent(root);

				//处理节点隐藏回调
				if(kpi.getHideNodeCallback() != null && kpi.getHideNodeCallback().length() > 0){
					String exp = RSBIUtils.unescape(kpi.getHideNodeCallback());
					String fName =  "f" + IdCreater.create();
					this.scripts.append("function ");
					this.scripts.append(fName);
					this.scripts.append("(){");
					this.scripts.append(exp);
					this.scripts.append("}");
					cf.setHideNodeFunc(fName);
				}

				//判断指标是否需要进行计算
				if (kpi.getCompute() != null && kpi.getCompute().length() > 0) {
					String[] jss = kpi.getCompute().split(",");  //可能有多个计算，用逗号分隔
					for (String js : jss) {
						CrossField compute = this.kpiCompute(js, null, kpi, scripts, table.getColwidth(), cf.getJsFunc());
						ls.add(compute);
						compute.setParent(root);
					}
				}
				//是否有和指定值进行计算
				if (kpi.getCompareDate() != null) {
					CrossField compute = this.kpiCompute("zdz", kpi.getCompareDate(), kpi, scripts, table.getColwidth(), cf.getJsFunc());
					ls.add(compute);
					compute.setParent(root);
				}
				this.loopJsonField(table, cfDto.getChildren(), cf.getSubs(), cf, pos, uselink, isFoldpcdim, tinfo);
			}else if("none".equals(cfDto.getType()) || "note".equals(cfDto.getType())){  //对应交叉表的 type = 'none' 类型 ,type = 'note' 表示  note="true" type="none" 类型的节点
				CrossField cf = new CrossField();
				cf.setType(cfDto.getType());
				cf.setDesc(cfDto.getDesc());
				cf.setSubs(new ArrayList<>());
				if("note".equals(cfDto.getType())){
					cf.setNote(true);
					cf.setStyleClass("row-line-note");
				}
				//处理节点隐藏回调
				if(cfDto.getHideNodeCallback() != null && cfDto.getHideNodeCallback().length() > 0){
					String exp = RSBIUtils.unescape(cfDto.getHideNodeCallback());
					String fName =  "f" + IdCreater.create();
					this.scripts.append("function ");
					this.scripts.append(fName);
					this.scripts.append("(){");
					this.scripts.append(exp);
					this.scripts.append("}");
					cf.setHideNodeFunc(fName);
				}
				ls.add(cf);
				cf.setParent(root);
				this.loopJsonField(table, cfDto.getChildren(), cf.getSubs(), cf, pos, uselink, isFoldpcdim, tinfo);
			}else if("kpiMatch".equals(cfDto.getType())){  //对应交叉表的 type=kpi
				//指标分解只能是最后一行/列
				for(CrossFieldDto d : cfDto.getValues()) {
					CrossField cf = new CrossField();
					String tp = d.getType();
					if("note".equals(tp)){
						cf.setType(tp);
						cf.setNote(true);
						cf.setStyleClass("row-line-note");
						cf.setDesc(d.getDesc());
					}else {
						cf.setType("kpi");
						cf.setDesc(d.getDesc());
						DimDto obj = this.findDimById(d.getMatch(), table.getDims());
						//如果分解维和参数相同，参数的查询条件在此起作用
						boolean isContinue = false;
						CompParamDto p = this.dimExistInParams(table, obj);
						if(p != null && p.getVal() != null && p.getVal().length() > 0){  //参数相同并且设置了值
							if("in".equals(p.getType())) {
								String[] vls = p.getVal().split(",");
								if(!existInArrays(d.getValue(), vls)){
									isContinue = true;
								}
							}else{
								if(!p.getVal().equals(d.getValue())){
									isContinue = true;
								}
							}
						}
						if(isContinue){
							continue;
						}

						cf.setAlias(obj.getAlias());
						cf.setValue(d.getValue());
						cf.setSpaceNum(d.getSpaceNum());
						cf.setFormatPattern(d.getFmt());
						if(cfDto.getChildren() != null && cfDto.getChildren().size() > 0){
							cf.setSubs(new ArrayList<>());
						}
						if(obj.getColwidth() != null) {
							cf.setWidth(obj.getColwidth());
						}else{
							if(table.getColwidth() != null && !"auto".equals(table.getColwidth())){
								cf.setWidth(new Integer(table.getColwidth()));
							}
						}

						if("kpiCalcMatch".equals(tp)) {
							calcNodes.add(d);
						}
						this.loopJsonField(table, cfDto.getChildren(), cf.getSubs(), cf, pos, uselink, isFoldpcdim, tinfo);
					}
					ls.add(cf);
					cf.setParent(root);
				}
			}
		}
	}

	/**
	 * 分解的维度是否在参数中
	 * @param table
	 * @param dim
	 * @return
	 */
	private CompParamDto dimExistInParams(PortalTableQuery table, DimDto dim){
		CompParamDto ext = null;
		if(table.getCompParams() == null){
			return ext;
		}
		for(CompParamDto p : table.getCompParams() ){
			if(p.getCol().equals(dim.getFromCol())){
				ext = p;
				break;
			}
		}
		return ext;
	}

	private boolean existInArrays(String val, String[] vls){
		boolean ret = false;
		for(String v:vls){
			if(v.equals(val)){
				ret = true;
				break;
			}
		}
		return ret;
	}

	public List<RowDimContext> getDrillDim(PortalTableQuery table){
		List<Map<String, Object>> drillDim = table.getDrillDim();
		if(drillDim == null || drillDim.isEmpty()){
			return null;
		}
		List<RowDimContext> ret = new ArrayList<RowDimContext>();
		for(int i=0; i<drillDim.size(); i++){
			Map<String, Object> obj = drillDim.get(i);
			RowDimContext dim = new RowDimContext();
			String tableColKey = (String)obj.get("tableColKey");
			String tableColName = (String)obj.get("tableColName");
			String code = (String)obj.get("code");
			String name = (String)obj.get("name");
			if(tableColKey == null || tableColKey.length() == 0){
				dim.setCode(code);
				dim.setName(name);
				dim.setCodeDesc(code);
			}else{
				dim.setCode(tableColKey);
				dim.setName(tableColName);
				dim.setCodeDesc(tableColName);
			}
			dim.setType("frd");
			ret.add(dim);
		}
		return ret;
	}

	/**
	 * 生成表格SQL
	 * @param drillLevel 是否有钻取，从0开始, 0表示不钻取，1表示钻取一层，以此类推
	 * @param jsType = sq/tq/lj/zdz等计算方式，如果为 null,既表示为默认不计算
	 * @return
	 * @throws ParseException
	 */
	public String createSql(PortalTableQuery table, List<VerticalKpiDto> verKpis, String jsType, DateCompareDto dateCompare, DateDimDto minDate, int drillLevel) {
		//判断是否查询es
		TableInfoVO tinfo = this.cacheService.getTableInfo(table.getTid());
		boolean qEs = tinfo.isSyncEs();
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		List<DimDto> dims = table.getDimsOnlyOneLevel();
		for(int i=0; i<dims.size(); i++){
			DimDto dim = dims.get(i);
			String key = dim.getTableColKey();
			String txt = dim.getTableColName();
			if(dim.getHideNodeCallback() != null && dim.getHideNodeCallback().length() > 0){  //需要调用隐藏节点回调函数，
				sql.append("#if($extUtils.callNodeHideFunc('"+dim.getHideNodeCallback()+"', $request) == false) ");
			}
			if(key != null && txt != null && key.length() >0 && txt.length() >0){
				sql.append(key+" as "+dim.getAlias()+", " + txt + " as "+dim.getAlias()+"2,"); //alias + alias2
			}else{
				sql.append((qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname())+" "+dim.getAlias()+", ");
			}
			if(dim.getHideNodeCallback() != null && dim.getHideNodeCallback().length() > 0){  //需要调用隐藏节点回调函数，
				sql.append(" #end ");
			}
			//查询父子维度层级字段，pid等内容，用来折叠父子维度
			if("y".equals(dim.getIspcdim())) {
				sql.append(dim.getLevelCol());
				sql.append(" ");
				sql.append("as");
				sql.append(" ");
				sql.append(dim.getLevelCol());
				sql.append(",");
				sql.append(dim.getPcPid());
				sql.append(" ");
				sql.append("as");
				sql.append(" ");
				sql.append(dim.getPcPid());
				sql.append(",");
			}
		}
		if(table.getGroupDto() != null && table.getGroupDto().getCol().length() > 0){
			sql.append(table.getGroupDto().getCol());
			sql.append(" ");
			sql.append(table.getGroupDto().getAlias());
			sql.append(",");
		}

		//处理钻取维
		List<Map<String, Object>> drillDim = table.getDrillDim();
		if(drillDim != null && drillDim.size() >= drillLevel){
			for(int i=0; i<drillLevel; i++){
				Map<String, Object> dim = drillDim.get(i);
				String key = (String)dim.get("tableColKey");
				String txt = (String)dim.get("tableColName");
				String fromCol = (String)dim.get("fromCol");
				if(key != null && txt != null && key.length() >0 && txt.length() >0){
					sql.append(key+", " + txt + ",");
				}else{
					String colname = (String)dim.get("colname");
					String alias = (String)dim.get("code");
					sql.append((qEs?(colname.equals(fromCol)?fromCol:alias):colname)+" "+ alias+", ");
				}
			}

		}

		List<KpiDto> kpis = table.getKpiJson();
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
					//if(kpi.getRate() == null){
						sql.append(kpi.getCol_name() + " ");
					//}else{
					//	sql.append("(" + kpi.getColName() + ")/"+kpi.getRate()+" ");
					//}
					sql.append(kpi.getAlias());

					if(i != kpis.size() - 1){
						sql.append(",");
					}
				}
			}
		}
		sql.append(" from " + datasetService.createTableSql(tinfo, false));
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
				if(table.getFoldpcdim() != null && table.getFoldpcdim()) {  //折叠维，需要取所有上级
					sql.append(" <= ");
				}else {
					sql.append(" = ");  //不折叠，取当前维
				}
				sql.append(dim.getPclevel());
			}
		}
		//如果是计算tq/sq/lj/zdz(指定值),时间维度只取最小维度，其他类型维度不变
		if(jsType != null) {
			for(int i=0; i<dims.size(); i++){
				DimDto dim = dims.get(i);
				String tp = dim.getType();
				String col = qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname();
				if(dim.isDateDim() && minDate != null && dim.getAlias().equals(minDate.getAlias()) && minDate.getIncome() == 2) {
					if("zdz".equals(jsType)){  //计算 和指定值做比较
						if("gdz".equals(dateCompare.getVal())){  //和固定值比较
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
						String vls = RSBIUtils.dealStringParam(dim.getVals(), "string".equalsIgnoreCase(dim.getValType()));
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
						String start = dim.getDay().getStartDay();
						String end = dim.getDay().getEndDay();
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " between '"+start+"' and '" + end + "'");
					}else
					if(dim.getVals() != null && dim.getVals().size() > 0){
						String vls = RSBIUtils.dealStringParam(dim.getVals(), "string".equalsIgnoreCase(dim.getValType()));
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " in ("+vls+")");
					}
				}
				//处理月份
				else if(dim.getType().equals("month")){
					if(dim.getMonth() != null){
						//如果有计算指标，需要重写数据区间
						String start = dim.getMonth().getStartMonth();
						String end = dim.getMonth().getEndMonth();
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " between '"+start+"' and '" + end + "'");
					}else
					if(dim.getVals() != null && dim.getVals().size() > 0){
						//如果有计算指标，需要重写数据值列表
						String vls = RSBIUtils.dealStringParam(dim.getVals(), "string".equalsIgnoreCase(dim.getValType()));
						sql.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " in ("+vls+")");
					}
				}else {

					//限制维度筛选
					if(dim.getVals() != null && dim.getVals().size() > 0){
						String vls = RSBIUtils.dealStringParam(dim.getVals(), "string".equalsIgnoreCase(dim.getValType()));
						sql.append(" and " + (dim.getTableColKey() != null && dim.getTableColKey().length() > 0 ? dim.getTableColKey() : (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname())) + " in ("+vls+")");
					}
				}
			}
		}

		//限制参数的查询条件
		String condition = super.dealCubeParams(table.getCompParams(), table.getUseIn(), "dim", tinfo, jsType, dateCompare, minDate);
		if("ds".equals(tinfo.getIncome()) && sql.indexOf(DatasetService.cond)  >= 0) {
			sql = new StringBuffer(datasetService.replaceSql(sql.toString(), condition));  //替换参数到数据集中
		}else{
			sql.append(condition);
		}
		sql.append(super.dealDashboardParams(table.getDashboardParam(), tinfo));
		if("dashboard".equals(table.getUseIn())) {  //处理仪表盘事件接收
			super.dealLinkAccept(table.getLinkAccept(), sql);
		}
		//在钻取的时候设置过滤
		if(drillLevel == 1 && table.getRows().size() == 1) {
			DimDto row = this.findDimById(table.getRows().get(0).getMatch(), table.getDims());
			String valType = row.getValType();
			if("String".equalsIgnoreCase(valType) || "Date".equalsIgnoreCase(valType) || "Datetime".equalsIgnoreCase(valType)){
				sql.append(" and " + (qEs?(row.getColname().equals(row.getFromCol())?row.getFromCol():row.getAlias()):row.getColname())+" = '$"+row.getAlias()+"'");
			}else{
				sql.append(" and " + (qEs?(row.getColname().equals(row.getFromCol())?row.getFromCol():row.getAlias()):row.getColname())+" = $"+row.getAlias());
			}
		}

		//处理事件接受的参数限制条件
		if(!"dashboard".equals(table.getUseIn())) {
			LinkAcceptDto linkAccept = table.getLinkAccept();
			if (linkAccept != null && linkAccept.getId() != null) {
				String col = linkAccept.getExpression() != null && linkAccept.getExpression().length() >0 ? linkAccept.getExpression() : linkAccept.getCol();
				String valtype = linkAccept.getValType();
				String val = linkAccept.getDftval();
				if ("dashboard".equals(table.getUseIn()) && val != null) {  //仪表盘和报表参数处理方式不一样,仪表盘直接传值
					if ("string".equalsIgnoreCase(valtype) || "Date".equalsIgnoreCase(valtype) || "Datetime".equalsIgnoreCase(valtype)) {
						val = "'" + val + "'";
					}
					sql.append(" and " + col + " = " + val);
				} else {
					String alias = super.findEventParamName(table.getId());
					if(alias != null){
						String ncol = "$" + alias;
						if ("string".equalsIgnoreCase(valtype) || "Date".equalsIgnoreCase(valtype) || "Datetime".equalsIgnoreCase(valtype)) {
							ncol = "'" + ncol + "'";
						}
						sql.append("#if($" + alias + " && $" + alias + " != '') and  " + col + " = " + ncol + " #end");
					}

				}
			}
		}

		if(dims.size() > 0 || tinfo.isVertical()){
			sql.append(" group by ");
			for(int i=0; i<dims.size(); i++){
				DimDto dim = dims.get(i);
				String key = dim.getTableColKey();
				String txt = dim.getTableColName();
				if(dim.getHideNodeCallback() != null && dim.getHideNodeCallback().length() > 0){  //需要调用隐藏节点回调函数，
					sql.append("#if($extUtils.callNodeHideFunc('"+dim.getHideNodeCallback()+"', $request) == false) ");
				}
				if(key != null && txt != null && key.length() >0 && txt.length() >0){
					sql.append(key+", " + txt);
				}else{
					sql.append(tinfo.isEs()?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname());
				}
				sql.append(",");
				if(dim.getHideNodeCallback() != null && dim.getHideNodeCallback().length() > 0){
					sql.append("#end ");
				}
				//查询父子维度层级字段，pid等内容，用来折叠父子维度
				if("y".equals(dim.getIspcdim())) {
					sql.append(dim.getPcPid());
					sql.append(",");
					sql.append(dim.getLevelCol());
					sql.append(",");
				}
			}
			//钻取的group by
			if(drillDim != null && drillDim.size() >= drillLevel){
				for(int i=0; i<drillLevel; i++){
					Map<String, Object> dim = drillDim.get(i);
					String key = (String)dim.get("tableColKey");
					String txt = (String)dim.get("tableColName");
					if (key != null && txt != null && key.length() > 0 && txt.length() > 0) {
						sql.append(key);
						sql.append(",");
					} else {
						sql.append(dim.get("code"));
						sql.append(",");
					}
				}

			}
			if(tinfo.isVertical()) {
				sql.append(tinfo.getKpiCodeColumn());
				sql.append(",");
			}
			//判断是否有分组维
			if(table.getGroupDto() != null && table.getGroupDto().getCol() != null && table.getGroupDto().getCol().length() > 0){
				sql.append(table.getGroupDto().getCol());
				sql.append(",");
			}
			sql.append(ExtConstants.ZWF);  //使用占位符， 在sql 执行前替换
		}
		//处理指标过滤
		StringBuffer filter = new StringBuffer("");
		for(KpiDto kpi : table.getKpiJson()){
			if(kpi.getFilter() != null){
				filter.append(" and "+kpi.getCol_name()+" ");
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
		filter.append(dealCubeParams(table.getCompParams(), "kpi", tinfo)); //处理参数中的指标筛选
		if(filter.length() > 0){
			sql.append(" having 1=1 " + filter);
		}
		return sql.toString();
	}

	/**
	 * 创建表格datacenter
	 * @param sql
	 * @return
	 * @throws IOException
	 */
	public GridDataCenterContext createDataCenter(String sql, PortalTableQuery table) throws IOException {
		DateDimDto minDate = null; //最小时间维度
		GridDataCenterContext ctx = new GridDataCenterContextImpl();
		GridSetConfContext conf = new GridSetConfContext();
		//判断是否通过 elasticsearch 查询
		TableInfoVO tinfo = cacheService.getTableInfo(table.getTid());
		if(tinfo.isRest()) {
			conf.setDatasetProvider(restService);
			conf.setMaster(String.valueOf(table.getTid()));  //rest查询，master存 tid
		}
		conf.setCache(dcCacheService);
		conf.setUseCache(false);
		if (!(tinfo.getDsourceId() == null || tinfo.getDsourceId() == -1)) {
			conf.setRefDsource("ds-" + tinfo.getDsourceId());
		}
		ctx.setConf(conf);
		ctx.setId("DC-" + IdCreater.create());
		//判断是否需要计算同比，环比，累计等衍生指标
		List<String> jsTypes = table.getKpiComputeType();
		if(jsTypes.size() == 0) {
			String name = TemplateManager.getInstance().createTemplate(sql);
			ctx.getConf().setTemplateName(name);
		}else {
			//设置多数据集，同比，环比，累计分别对应一个数据集。 主数据集为主SQL
			//通过masterProcess 先获取最小时间维度, 获取最小时间维度得最大，最小值
			List<DateDimDto> dateDims = table.getDateDims(this.scripts);
			Collections.sort(dateDims, (o1, o2) -> {
				/**
				 * 维度从大到小排序
				 */
				Integer v1 = type2value(o1.getType());
				Integer v2 = type2value(o2.getType());
				if(v1.equals(v2)){
					return -o1.getIncome().compareTo(o2.getIncome());
				}else {
					return -v1.compareTo(v2);
				}
			});
			if(dateDims.size() == 0) {
				throw new RuntimeException("无时间维度，不能进行计算。");
			}
			minDate = dateDims.get(dateDims.size() - 1);
			MatchDateKeyService ser = new MatchDateKeyService(dateDims);
			conf.setMasterProcess(ser);
			//每个计算设置一个DS
			List<MultiDsContext> mds = new ArrayList<>();
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
					List<DateCompareDto> compareDtos = table.getKpiCompareDtos();
					for(DateCompareDto compareDto : compareDtos){
						MultiDsContext jsDs = this.createJsDs(jsType, compareDto, minDate, tinfo, table);
						mds.add(jsDs);
					}
				}else{
					MultiDsContext jsDs = this.createJsDs(jsType, null, minDate, tinfo, table);
					mds.add(jsDs);
				}
			}
			conf.setMultiDsContext(mds);
		}
		List<DimDto> dims = table.getDims();
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
			table.getKpiJson().forEach(k -> {
				String alias = k.getAlias();
				String col = k.getFromCol();
				colNames.add(col);
				ncolNames.add(alias);
			});
			reset.setColNames(colNames);
			reset.setColNewNames(ncolNames);
			ctx.getProcess().add(reset);
		}
		if(table.getFoldpcdim() == null || !table.getFoldpcdim()) {  //不是折叠维度才取上级
			//如果是父子维度（父子维度超过2级），需要查找上级维度
			DimDto lastPcDim = null;
			int dimCnt = 0;
			for(DimDto dim : dims) {
				if("y".equals(dim.getIspcdim())) {
					if(lastPcDim == null) {
						lastPcDim = dim;
					}else {
						if(dim.getPclevel() > lastPcDim.getPclevel()) { //level最大的一个是最后层级
							lastPcDim = dim;
						}
					}
					dimCnt++;
				}
			}
			if(dimCnt > 1) { //选择了2个及以上父子维度，需要填写上级维度的数据。
				FindParentDimContext fpCtx = new FindParentDimContext();
				fpCtx.setCode(lastPcDim.getPcId());
				fpCtx.setPcode(lastPcDim.getPcPid());
				fpCtx.setName(lastPcDim.getTableColName());
				fpCtx.setTname(tinfo.getTname());
				fpCtx.setCurCode(lastPcDim.getAlias());
				List<String> appCols = new ArrayList<String>();
				List<String> appColTypes = new ArrayList<String>();
				List<String> appColsDesc = new ArrayList<String>();
				List<DimDto> pcDims = new ArrayList<DimDto>();
				for(DimDto dim : dims) {
					if("y".equals(dim.getIspcdim()) && !dim.getId().equals(lastPcDim.getId())) {
						pcDims.add(dim);
					}
				}
				//对父子维度按照level 排序
				Collections.sort(pcDims, (o1, o2) -> o1.getPclevel().compareTo(o2.getPclevel()));
				for(DimDto dim : pcDims) {
					appCols.add(dim.getAlias());
					if("Int".equals(dim.getValType()) || "Double".equals(dim.getValType()) || "Long".equals(dim.getValType())) {
						appColTypes.add(GridDataMetaData.colTypeNumber);
					}else if("Date".equals(dim.getValType()) || "Datetime".equals(dim.getValType())){
						appColTypes.add(GridDataMetaData.colTypeDate);
					}else{
						appColTypes.add(GridDataMetaData.colTypeString);
					}
					appColsDesc.add(dim.getAlias()+"2");
				}
				fpCtx.setAppendCol(appCols);
				fpCtx.setAppendColType(appColTypes);
				fpCtx.setAppendColDesc(appColsDesc);
				ctx.getProcess().add(fpCtx);
			}
		}
		//折叠父子维度，需要排序
		if(table.getFoldpcdim() !=  null && table.getFoldpcdim()) {
			PcDimSortContext pcSort = new PcDimSortContext();
			//如果是父子维度（父子维度超过2级），需要查找上级维度
			DimDto lastPcDim = null;
			for(DimDto dim : dims) {
				if("y".equals(dim.getIspcdim())) {
					if(lastPcDim == null) {
						lastPcDim = dim;
					}else {
						if(dim.getPclevel() > lastPcDim.getPclevel()) { //level最大的一个是最后层级
							lastPcDim = dim;
						}
					}
				}
			}
			if(lastPcDim != null) {
				pcSort.setIdCol(lastPcDim.getAlias());
				pcSort.setPidCol(lastPcDim.getPcPid());
				ctx.getProcess().add(pcSort);
			}
		}

		//升序排名/降序排名
		for(KpiDto kpi : table.getKpiJson()){
			if(kpi.getCompute() != null && kpi.getCompute().length() > 0){
				if("sxpm".equals(kpi.getCompute()) || "jxpm".equals(kpi.getCompute())){
					GridProcContext proc = createSort(table, kpi);
					ctx.getProcess().add(proc);
				}
			}
		}

		//处理排序
		List<String> orderCols = new ArrayList<String>(); //需要进行排序的字段
		this.loopNodes(table.getCols(), table, orderCols);
		this.loopNodes(table.getRows(), table, orderCols);
		//处理分组维
		GroupDto groupDto = table.getGroupDto();
		if(groupDto != null && groupDto.getAlias() != null && groupDto.getOrder() != null){
			orderCols.add(groupDto.getAlias()+","+groupDto.getOrder());
		}
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
		//是否生成排名列
		if(table.getRanking() != null && table.getRanking()) {
			GridIndexContext gi = new GridIndexContext();
			gi.setCol("sortKey");
			ctx.getProcess().add(gi);
		}

		//计算上期，同期，累计, 与指定时间比
		for(String js : jsTypes) {
			if("zdz".equals(js)){  //指标计算 - 比较指定日期
				List<DateCompareDto> compareDtos = table.getKpiCompareDtos();
				for(DateCompareDto compareDto : compareDtos){
					GridProcContext proc = createShift(table, table.getKpiJson(), js, compareDto, minDate);
					ctx.getProcess().add(proc);
				}
			}else {
				GridProcContext proc = createShift(table, table.getKpiJson(), js, null, minDate);
				ctx.getProcess().add(proc);
			}
		}

		//纵表计算(type='kpi' 维度分解后的行计算 )
		if(this.calcNodes.size() > 0) {
			List<String> kpis = table.getKpiJson().stream().map(KpiDto::getAlias).collect(Collectors.toList());
			if(jsTypes.size() > 0){  //如果有指标计算，需要把计算做指标
				List<String> adds = new ArrayList<>();
				for(String js : jsTypes){
					for(String k : kpis){
						adds.add(k+"_" + js);
					}
				}
				kpis.addAll(adds);
			}
			for (CrossFieldDto calcNode : this.calcNodes) {
				VerKpiCalcContext calcContext = new VerKpiCalcContext();
				calcContext.setAfterCalcName(calcNode.getValue());
				calcContext.setExpress(RSBIUtils.unescape(calcNode.getExpression()));
				calcContext.setKpiColumns(kpis);
				DimDto obj = this.findDimById(calcNode.getMatch(), table.getDims());
				calcContext.setVerKpiColumn(obj.getAlias());
				//查询除了 verKpiColumn 的其他维度, 目前只支持一个维度或没有维度
				String dimColumn = null;
				for(DimDto dim : dims){
					if(!dim.getAlias().equals(obj.getAlias())){
						dimColumn = dim.getAlias();
					}
				}
				calcContext.setDimColumn(dimColumn);
				ctx.getProcess().add(calcContext);
			}
		}

		return ctx;
	}

	/**
	 * 创建指标计算的数据集
	 * @param jsType
	 * @param minDate
	 * @param tinfo
	 * @param table
	 * @return
	 * @throws IOException
	 */
	private MultiDsContext createJsDs(String jsType, DateCompareDto dateCompare, DateDimDto minDate, TableInfoVO tinfo, PortalTableQuery table) throws IOException {
		MultiDsContext jsDs = new MultiDsContext();
		//设置 jsDs 的key ，通过key 关联数据集和指标计算
		if("zdz".equals(jsType)){
			jsDs.setKey(jsType+("gdz".equals(dateCompare.getValtype()) ? dateCompare.getVal():dateCompare.getParam()));
		}else {
			jsDs.setKey(jsType);  //如果是zdz， key 为 zdz_value
		}
		String jsSql =  this.createSql(table, null, jsType, dateCompare, minDate, 0);
		String jsTemplate = TemplateManager.getInstance().createTemplate(jsSql);
		jsDs.setTemplateName(jsTemplate);
		jsDs.setTname(tinfo.getTname());
		jsDs.setTid(tinfo.getTid());
		return jsDs;
	}

	/**
	 * 创建指标排名process
	 * @param table
	 * @param kpi
	 * @return
	 */
	public GridProcContext createSort(PortalTableQuery table, KpiDto kpi){
		GridSortContext proc = new GridSortContext();
		proc.setAppendOrder(true);
		proc.setChangeOldOrder(false);
		/**
		//创建排序的分组维
		StringBuffer sb = new StringBuffer("");
		StringBuffer orderSb = new StringBuffer("");
		List<DimDto> dims = table.getDims();
		for(DimDto dim : dims){
			//设置 col 维度 为分组维
			sb.append(dim.getAlias());
			sb.append(",");
			orderSb.append(dim.getDimord());
			orderSb.append(",");

		}
		sb.append(kpi.getAlias());
		orderSb.append("sxpm".equals(kpi.getCompute())?"asc":"desc");
		 **/
		proc.setColumn(kpi.getAlias());
		proc.setType("sxpm".equals(kpi.getCompute())?"asc":"desc");
		return proc;
	}

	public static CrossField kpiCompute(String compute, DateCompareDto compareDto, KpiDto kpi, StringBuffer scripts, String tableColWidth, String jsFunc){
		CrossField cf = new CrossField();
		if("zb".equals(compute)){
			cf.setDesc("占比");
			cf.setAggregation("avg");
			cf.setAlias(kpi.getAlias() + "_zb");
			cf.setFormatPattern("0.00%");
		}else if("sxpm".equals(compute) || "jxpm".equals(compute)){
			cf.setDesc(("sxpm".equals(compute) ? "升序":"降序") + "排名");
			cf.setAggregation("avg");
			cf.setAlias(kpi.getAlias() + "_order");
			cf.setFormatPattern("#,###");
			cf.setStyleClass("pms");
			cf.setStyleToLine(true);
		}else if("ydpj".equals(compute)){
			cf.setDesc("移动平均");
			cf.setAggregation(kpi.getAggre());
			cf.setAlias(kpi.getAlias() + "_ydpj");
			cf.setFormatPattern(kpi.getFmt());
			if(kpi.getRate() != null){
				cf.setKpiRate(new BigDecimal(kpi.getRate()));
			}
		}else if("sq".equals(compute)){
			cf.setDesc("上期值");
			cf.setAggregation(kpi.getAggre());
			cf.setAlias(kpi.getAlias()+"_sq");
			cf.setFormatPattern(kpi.getFmt());
			if(kpi.getRate() != null){
				cf.setKpiRate(new BigDecimal(kpi.getRate()));
			}
			cf.setJsFunc(jsFunc);
		}else if("tq".equals(compute)) {
			cf.setDesc("同期值");
			cf.setAggregation(kpi.getAggre());
			cf.setAlias(kpi.getAlias() + "_tq");
			cf.setFormatPattern(kpi.getFmt());
			if (kpi.getRate() != null) {
				cf.setKpiRate(new BigDecimal(kpi.getRate()));
			}
			cf.setJsFunc(jsFunc);
		}else if("lj".equals(compute)){
			cf.setDesc("累计值");
			cf.setAggregation(kpi.getAggre());
			cf.setAlias(kpi.getAlias() + "_lj");
			cf.setFormatPattern(kpi.getFmt());
			if (kpi.getRate() != null) {
				cf.setKpiRate(new BigDecimal(kpi.getRate()));
			}
			cf.setJsFunc(jsFunc);
		}else if("zje".equals(compute)){
			cf.setDesc("增减额");
			cf.setAggregation(kpi.getAggre());
			cf.setAlias(kpi.getAlias() + "_zje");
			cf.setFormatPattern(kpi.getFmt());
			if(kpi.getRate() != null){
				cf.setKpiRate(new BigDecimal(kpi.getRate()));
			}
			cf.setFinanceFmt(true);
			cf.setJsFunc(jsFunc);
		}else if("hb".equals(compute)){
			cf.setDesc("环比");
			cf.setAggregation("sum");
			cf.setAlias(kpi.getAlias());
			cf.setId("a");  //用来公式计算的标识
			cf.setFormatPattern("0.00%");
			cf.setFinanceFmt(true);
			cf.setUselink(false);
			cf.setFormula("jshb");
			cf.setJsFunc(jsFunc);
			//创建环比计算公式
			scripts.append("function jshb(a, b){ return a/b - 1; } \n");
			//添加 CrossFieldOther 进行公式计算
			CrossFieldOther other = new CrossFieldOther();
			other.setType("kpiOther");
			other.setAlias(kpi.getAlias()+"_sq");
			other.setId("b"); //用来公式计算的标识
			other.setAggregation("sum");
			cf.setOther(new ArrayList<>());
			cf.getOther().add(other);
		}else if("tb".equals(compute)){
			cf.setDesc("同比");
			cf.setAggregation("sum");
			cf.setAlias(kpi.getAlias());
			cf.setId("a");  //用来公式计算的标识
			cf.setFormatPattern("0.00%");
			cf.setFinanceFmt(true);
			cf.setUselink(false);
			cf.setFormula("jstb");
			cf.setJsFunc(jsFunc);
			//创建环比计算公式
			scripts.append("function jstb(a, b){ return a/b - 1; } \n");
			//添加 CrossFieldOther 进行公式计算
			CrossFieldOther other = new CrossFieldOther();
			other.setType("kpiOther");
			other.setAlias(kpi.getAlias()+"_tq");
			other.setId("b"); //用来公式计算的标识
			other.setAggregation("sum");
			cf.setOther(new ArrayList<>());
			cf.getOther().add(other);
		}else if("zdz".equals(compute)){
			cf.setDesc(compareDto.getName());
			cf.setAggregation(kpi.getAggre());
			cf.setAlias(kpi.getAlias() + "_zdz");
			cf.setFormatPattern(kpi.getFmt());
			if (kpi.getRate() != null) {
				cf.setKpiRate(new BigDecimal(kpi.getRate()));
			}
			String code = compareDto.getCode();
			if(code != null && code.length() > 0){
				String funcName = "f" + IdCreater.create();
				code = 	RSBIUtils.unescape(code);
				scripts.append("function "+funcName+"(value,col,row,data,outType,rowIndex,colIndex){"+code+"}");
				cf.setJsFunc(funcName);
			}
		}
		cf.setType("kpiOther");
		cf.setId("ext_" + kpi.getKpi_id()+"_"+compute); //表示当前指标是由基本指标衍生而来，比如昨日、累计、同比、环比、排名、占比等内容。

		if(kpi.getColwidth() != null){
			cf.setWidth(kpi.getColwidth());
		}else{
			if(tableColWidth != null && !"auto".equals(tableColWidth)) {
				cf.setWidth(new Integer(tableColWidth));
			}
		}

		return cf;
	}

	/**
	 * 创建时间偏移process,时间偏移用来计算同比、环比、上期、同期等值
	 * @param table
	 * @param kpis
	 * @return
	 */
	public GridProcContext createShift(PortalTableQuery table, List<KpiDto> kpis, String compute, DateCompareDto compareDto, DateDimDto minDate) {
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
		for(DimDto dim : table.getDims()){
			if(dim.isDateDim() && dim.getAlias().equals(minDate.getAlias())){
				hasDateDimInTabele = true;
			}
			if(dim.isDateDim()){
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
		//只有表中有时间维度,并且时间维度就是最小时间维度，才需要设置时间字段
		if(hasDateDimInTabele) {
			proc.setDateType(minDate.getType());
			proc.setDateFormat(minDate.getDateformat());
			proc.setDateColumn(minDate.getAlias());
		}
		return proc;
	}

	public DimDto findDimById(Integer dimId, List<DimDto> dims){
		DimDto ret = null;
		for(DimDto dim : dims){
			if(dim.getId().equals(dimId)){
				ret = dim;
				break;
			}
		}
		return ret;
	}

	public KpiDto findKpiById(Integer kpiId, List<KpiDto> kpis){
		KpiDto ret = null;
		for(KpiDto kpi : kpis){
			if(kpi.getKpi_id().equals(kpiId)){
				ret = kpi;
				break;
			}
		}
		return ret;
	}

	public Map<String, InputField> getMvParams() {
		return mvParams;
	}

}
