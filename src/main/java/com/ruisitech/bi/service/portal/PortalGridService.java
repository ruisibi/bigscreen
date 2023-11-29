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
import com.rsbi.ext.engine.view.context.dc.grid.*;
import com.rsbi.ext.engine.view.context.form.InputField;
import com.rsbi.ext.engine.view.context.grid.PageInfo;
import com.rsbi.ext.engine.view.context.gridreport.GridCell;
import com.rsbi.ext.engine.view.context.gridreport.GridReportContext;
import com.rsbi.ext.engine.view.context.gridreport.GridReportContextImpl;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.portal.CompParamDto;
import com.ruisitech.bi.entity.portal.GridColDto;
import com.ruisitech.bi.entity.portal.GridQuery;
import com.ruisitech.bi.service.bireport.BaseCompService;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.QueryRestService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.ext.service.DataControlInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class PortalGridService extends BaseCompService {

public final static String deftMvId = "mv.portal.gridReport";

	private Map<String, InputField> mvParams = new HashMap<String, InputField>(); //mv的参数

	@Autowired
	private DataControlInterface dataControl; //数据权限控制

	@Autowired
	private TableCacheService cacheService;

	@Autowired
	private QueryRestService restService;

	@Autowired
	private DatasetService datasetService;

	public MVContext json2MV(GridQuery grid) throws Exception{
		TableInfoVO tvo = null;
		if(grid.getTid() != null) {
			tvo = cacheService.getTableInfo(grid.getTid());
			grid.checkSql(tvo);
		}
		//创建MV
		MVContext mv = new MVContextImpl();
		mv.setChildren(new ArrayList<Element>());
		String formId = ExtConstants.formIdPrefix + IdCreater.create();
		mv.setFormId(formId);
		mv.setMvid(deftMvId);
		mv.setHideMV(true);

		//处理参数,把参数设为hidden
		super.parserHiddenParam(grid.getPortalParams(), mv, this.mvParams, grid.getUseIn());

		//创建corssReport
		GridReportContext cr = json2Grid(grid, tvo);
		//设置ID
		cr.setId(grid.getId());
		cr.setLabel(grid.getId());
		cr.setColwidth(grid.getColwidth());
		GridDataCenterContext dc = null;
		if(grid.getTid() == null) {  //模板调用,没有tid
			dc = super.createEmptyDataCenter(grid.getId());
			cr.setRefDataCenter(dc.getId());
		}else {
			//创建数据sql
			String sql = this.createSql(grid, tvo);
			dc = this.createDataCenter(grid, sql);
			cr.setRefDataCenter(dc.getId());
		}
		if(mv.getGridDataCenters() == null){
			mv.setGridDataCenters(new HashMap<String, GridDataCenterContext>());
		}
		mv.getGridDataCenters().put(dc.getId(), dc);

		mv.getChildren().add(cr);
		cr.setParent(mv);

		Map<String, GridReportContext> crs = new HashMap<String, GridReportContext>();
		crs.put(cr.getId(), cr);
		mv.setGridReports(crs);
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

	public GridDataCenterContext createDataCenter(GridQuery grid, String sql) throws IOException {
		GridDataCenterContext ctx = new GridDataCenterContextImpl();
		GridSetConfContext conf = new GridSetConfContext();
		//判断是否通过 elasticsearch 查询
		TableInfoVO tinfo = cacheService.getTableInfo(grid.getTid());
		if(tinfo.isRest()) {
			conf.setMaster(String.valueOf(grid.getTid()));
			conf.setDatasetProvider(restService);
		}
		//明细数据不缓存
		//conf.setCache(dcCacheService);
		conf.setUseCache(false);
		if (!(tinfo.getDsourceId() == null || tinfo.getDsourceId() == -1)) {
			conf.setRefDsource("ds-" + tinfo.getDsourceId());
		}
		ctx.setConf(conf);
		ctx.setId("DC-" + IdCreater.create());

		//判断是否有聚合
		boolean hasAggre = false;
		for(GridColDto col : grid.getCols() ){
			if(col.getHideCol() != null && col.getHideCol()){
				continue;
			}
			if(col.getAggre() != null && col.getAggre().length() > 0 ||
				col.getAggreSql() != null && col.getAggreSql().length() > 0){
				hasAggre = true;
				break;
			}
		}
		if(!hasAggre) {
			String name = TemplateManager.getInstance().createTemplate(sql);
			ctx.getConf().setTemplateName(name);
		}else{
			//主查询
			List<MultiDsContext> mds = new ArrayList<>();
			MultiDsContext master = new MultiDsContext();
			master.setKey("master");
			String name = TemplateManager.getInstance().createTemplate(sql);
			master.setTemplateName(name);
			master.setTname(tinfo.getTname());
			master.setTid(tinfo.getTid());
			master.setMaster(true);
			mds.add(master);

			//聚合查询
			MultiDsContext total = new MultiDsContext();
			total.setKey("total");
			String totalSql = this.createTotalSql(grid, tinfo);
			String totalName = TemplateManager.getInstance().createTemplate(totalSql);
			total.setTemplateName(totalName);
			total.setTid(tinfo.getTid());
			total.setTname(tinfo.getTname());
			mds.add(total);
			ctx.getConf().setMultiDsContext(mds);
		}

		//升序排名，降序排名
		for(GridColDto col : grid.getAllCols(true)){
			if("sxpm".equals(col.getPm()) || "jxpm".equals(col.getPm())){
				GridSortContext proc = new GridSortContext();
				proc.setAppendOrder(true);
				proc.setChangeOldOrder(false);
				proc.setColumn(col.getName());
				proc.setType(col.getPm().equals("sxpm")?"asc":"desc");
				ctx.getProcess().add(proc);
			}
		}
		return ctx;
	}

	/**
	 * 创建grid表格的表头
	 * 把含有 children 类型转成二维数组
	 * @param gridJson
	 * @return
	 */
	public GridCell[][] createHeaders(GridQuery gridJson, TableInfoVO tvo){
		String colwidth = gridJson.getColwidth();
		//生成head 数组
		List<GridColDto> allCols = gridJson.getAllColsHasChildrenParent();
		//最大level
		int level = 0;
		for(GridColDto col : allCols){
			if(col.getLevel() > level){
				level = col.getLevel();
			}
		}
		int colSize = 0;  //列数
		for(GridColDto col : gridJson.getCols()){
			if(col.getHideCol() != null && col.getHideCol()){
				continue;
			}
			colSize += col.getChildren() == null ? 1: col.getChildren().size();
		}
		GridCell[][] headers = new GridCell[level + 1][colSize];
		/**
		//置空对象
		for(int i=0; i<headers.length; i++){
			GridCell[] head = headers[i];
			for(int j=0; j<head.length; j++){
				GridCell c = new GridCell();
				head[j] = c;
			}
		}
		 **/
		for(int i=0; i<=level; i++){
			final int curLevel = i;
			List<GridColDto> rowCols = allCols.stream().filter(c->c.getLevel() == curLevel).collect(Collectors.toList());
			int pos = 0;
			for(int j=0; j<rowCols.size(); j++){
				GridColDto col = rowCols.get(j);
				GridCell cell = new GridCell();
				int colSpan = col.getChildren() == null ? 1 : col.getChildren().size();
				cell.setColSpan(colSpan);
				int rowSpan = col.getChildren() != null ? 1 : level + 1 - col.getLevel();
				cell.setRowSpan(rowSpan);
				String name = col.getName();
				String id = col.getId();
				String dispName = col.getDispName();
				cell.setDesc(dispName == null || dispName.length() == 0 ? name : dispName);
				if(tvo.isRegEsTable() && tvo.isEsKeyword(id)){
					id = id.replaceAll(".keyword", "");
				}
				cell.setAlias(id);
				cell.setAlign(col.getAlign());
				cell.setOrder(col.getFrontSort()); //前端排序标识

				if (col.getColwidth() != null) {    //列定义的 colwidth 起作用
					cell.setWidth(col.getColwidth());
				} else if (colwidth != null && colwidth.length() > 0 && !"auto".equalsIgnoreCase(colwidth)) {
					cell.setWidth(new Integer(colwidth));
				}
				for(int p=0; true; p++){
					GridCell cur = headers[i][pos + p];
					if(cur == null ){
						headers[i][pos + p] = cell;
						break;
					}
				}
				if(cell.getColSpan() > 1){  //增加 col 占位符号
					for(int k=1; k<cell.getColSpan(); k++){
						headers[i][pos + k] = new GridCell();
					}
				}
				if(cell.getRowSpan() > 1){ //增加 row 占位符
					for(int k=1; k<cell.getRowSpan(); k++){
						headers[i + k][pos] = new GridCell();
					}
				}
				pos += cell.getColSpan();
			}
		}
		return headers;
	}

	public GridReportContext json2Grid(GridQuery gridJson, TableInfoVO tvo) throws CloneNotSupportedException {
		if(tvo == null){
			tvo = cacheService.getTableInfo(gridJson.getTid());
		}
		if(gridJson.getNumberCol() != null && gridJson.getNumberCol()){  //创建序号字段
			GridColDto c = new GridColDto();
			c.setName("序号");
			c.setId("numberCol");
			c.setColwidth(80);
			c.setAlign("center");
			c.setNumberCol(true);
			gridJson.getCols().add(0, c);
		}
		GridReportContext grid = new GridReportContextImpl();
		grid.setOut("lockUI");
		grid.setUse("dashboard");
		GridCell[][] headers = this.createHeaders(gridJson, tvo);
		grid.setHeaders(headers);

		//生成Detail
		List<GridColDto> cols = gridJson.getAllCols(true);
		//确定length
		int size = 0;
		for(GridColDto c : cols){
			size++;
			if(c.getPm() != null){
				size++;
			}
		}
		String colwidth = gridJson.getColwidth();
		GridCell[][] detail = new GridCell[1][size];
		int idx = 0;
		for(int i=0; i<cols.size(); i++){
			GridColDto col = cols.get(i);
			GridCell cell = new GridCell();
			String id = col.getId();
			if(tvo.isRegEsTable() && tvo.isEsKeyword(col.getId())){
				id = id.replaceAll(".keyword", "");
			}
			cell.setAlias(id);
			String fmt = col.getFmt();
			String align = col.getAlign();
			if(fmt != null && fmt.length() > 0){
				cell.setFormatPattern(fmt);
			}
			if(align != null && align.length() > 0){
				cell.setAlign(align);
			}
			if(col.getFuncname() != null && col.getFuncname().length() > 0 && col.getCode() != null && col.getCode().length() > 0) {
				cell.setJsFunc(col.getFuncname());
				String code = col.getCode();
				code = RSBIUtils.unescape(code);
				this.scripts.append("function "+col.getFuncname()+"(value, rowData, column, index, outType){"+code+"}");

			}
			if (col.getColwidth() != null) {    //列定义的 colwidth 起作用
				cell.setWidth(col.getColwidth());
			}
			else if(colwidth != null && colwidth.length() > 0 && !"auto".equalsIgnoreCase(colwidth)){
				cell.setWidth(new Integer(colwidth));
			}
			cell.setNumberCol(col.getNumberCol());
			detail[0][idx] = cell;
			idx++;
			if(col.getPm() != null){
				GridCell orderCell = new GridCell();
				orderCell.setAlias(id+"_order");
				orderCell.setAlign("center");
				if(colwidth != null && colwidth.length() > 0 && !"auto".equalsIgnoreCase(colwidth)){
					orderCell.setWidth(new Integer(colwidth));
				}
				detail[0][idx] = orderCell;
				idx++;
			}
		}
		grid.setDetails(detail);

		//判断是否有合计字段，如果有，需要设置 footer 行
		boolean hasAggre = false;
		for(GridColDto col : cols ){
			if(col.getAggre() != null && col.getAggre().length() > 0 ||
				col.getAggreSql() != null && col.getAggreSql().length() > 0){
				hasAggre = true;
				break;
			}
		}
		if(hasAggre) {
			GridCell[][] footer = new GridCell[1][size];
			for (int i = 0; i < cols.size(); i++) {
				GridColDto col = cols.get(i);
				if (col.getAggre() != null && col.getAggre().length() > 0 ||
						col.getAggreSql() != null && col.getAggreSql().length() > 0) {
					GridCell c = detail[0][i].clone();
					c.setDynamicText(true);
					c.setDesc("");
					footer[0][i] = c;
				}else {
					GridCell c = new GridCell();
					c.setColSpan(1);
					c.setRowSpan(1);
					c.setDesc("");
					footer[0][i] = c;
				}
			}
			grid.setFooters(footer);
		}

		//设置分页
		Integer pageSize = gridJson.getPageSize();
		if(pageSize == null){
			pageSize = 10;
		}
		PageInfo page = new PageInfo();
		page.setPagesize(pageSize);
		page.setCurtpage(gridJson.getCurPage());
		//是否禁用分页
		Boolean isnotfy = gridJson.getIsnotfy();
		if(isnotfy == null || !isnotfy){
			grid.setPageInfo(page);
		}
		grid.setTransposition(gridJson.getTransposition());
		return grid;
	}

	/**
	 * 创建聚合sql
	 * @param grid
	 * @return
	 */
	public String createTotalSql(GridQuery grid, TableInfoVO tvo){
		if(tvo.isRest()){
			throw new RuntimeException("Rest接口不支持字段聚合。");
		}
		StringBuffer sb = new StringBuffer("select ");
		List<GridColDto> cols = grid.getAllCols(false);
		StringBuffer selects = new StringBuffer();
		for(int i=0; i<cols.size(); i++){
			GridColDto col = cols.get(i);
			if(col.getAggre() != null && col.getAggre().length() > 0){  //aggre比aggreSql优化
				selects.append(col.getAggre());
				selects.append("(");
				String exp = col.getExpression();
				if(exp == null || exp.length() == 0) {
					selects.append(col.getName());
				}else{
					selects.append(exp);
				}
				selects.append(")");
				selects.append(" as ");
				selects.append(col.getName());
				selects.append(",");
			}else if(col.getAggreSql() != null && col.getAggreSql().length() > 0){
				selects.append(col.getAggreSql());
				selects.append(" as ");
				selects.append(col.getName());
				selects.append(",");
			}
		}
		sb.append(selects.substring(0, selects.length() - 1));
		sb.append(" from ");
		sb.append(datasetService.createTableSql(tvo, false));
		sb.append(" where 1=1 ");
		//数据权限
		if(dataControl != null){
			String ret = dataControl.process(RSBIUtils.getLoginUserInfo(), tvo);
			if(ret != null){
				sb.append(ret + " ");
			}
		}

		//添加参数筛选
		List<CompParamDto> pageParams = grid.getCompParams();
		String condition = super.dealCubeParams(pageParams, grid.getUseIn(),"dim", tvo);
		if("ds".equals(tvo.getIncome()) && sb.indexOf(DatasetService.cond)  >= 0) {
			sb = new StringBuffer(datasetService.replaceSql(sb.toString(), condition));  //替换参数到数据集中
		}else{
			sb.append(condition);
		}
		sb.append(super.dealDashboardParams(grid.getDashboardParam(), tvo));
		if("dashboard".equals(grid.getUseIn())) {  //处理仪表盘事件接收
			super.dealLinkAccept(grid.getLinkAccept(), sb);
		}
		return sb.toString().replaceAll("@", "'").replaceAll("\\[x\\]", "\\$");
	}

	public String createSql(GridQuery grid, TableInfoVO tvo){
		if(tvo.isRest()) {
			return super.createRestSql(grid.getCompParams(),  grid.getLinkAccept(), tvo);
		}
		//boolean qEs = tvo.isSyncEs();
		StringBuffer sb = new StringBuffer("select ");
		List<GridColDto> cols = grid.getAllCols(false);
		for(int i=0; i<cols.size(); i++){
			GridColDto col = cols.get(i);
			if(col.getNumberCol() != null && col.getNumberCol()){ //忽略排序字段
				continue;
			}
			String name = col.getName();
			String expression = col.getExpression();  //表达式字段
			if(expression != null && expression.length() > 0){
				sb.append(" "+ expression + " as " + name);
			}else if("Datetime".equalsIgnoreCase(col.getType())){  //es需要格式化
				if(tvo.isEs()) {
					sb.append("date_format("+name+",'yyyy-MM-dd HH:mm:ss')  as "+ name);
				}else {
					sb.append(" " + name + " as " + name);
				}
			}else if(tvo.isRegEsTable() && tvo.isEsKeyword(col.getName())){
				String n = name.replaceAll(".keyword", "");
				sb.append(" " + n + " as " + n);
			}else{
				sb.append(" " + name + " as " + name);
			}
			if(i != cols.size() - 1){
				sb.append(",");
			}
		}

		sb.append(" from ");

		sb.append(datasetService.createTableSql(tvo, false));

		sb.append(" where 1=1 ");
		//数据权限
		if(dataControl != null){
			String ret = dataControl.process(RSBIUtils.getLoginUserInfo(), tvo);
			if(ret != null){
				sb.append(ret + " ");
			}
		}

		//添加参数筛选
		List<CompParamDto> pageParams = grid.getCompParams();
		String condition = super.dealCubeParams(pageParams, grid.getUseIn(),"dim", tvo);
		if("ds".equals(tvo.getIncome()) && sb.indexOf(DatasetService.cond)  >= 0) {
			sb = new StringBuffer(datasetService.replaceSql(sb.toString(), condition));  //替换参数到数据集中
		}else{
			sb.append(condition);
		}
		sb.append(super.dealDashboardParams(grid.getDashboardParam(), tvo));
		if("dashboard".equals(grid.getUseIn())) {  //处理仪表盘事件接收
			super.dealLinkAccept(grid.getLinkAccept(), sb);
		}
		//排序字段
		List<GridColDto> sorts = new ArrayList<>();
		for(GridColDto col : cols){
			String sort = col.getSort();
			if(sort != null && sort.length() > 0){
				sorts.add(col);
			}
		}
		//前端排序
		sb.append(" #if($!t_sort_col && $t_sort_col != '') ");
		sb.append(" order by  $t_sort_col $t_sort_type ");
		sb.append(" #else ");
		if(sorts.size() > 0) {
			sb.append(" order by ");
			//按sortIndex排序
			sorts.sort((a, b) -> {
				if (a.getSortIndex() == null || b.getSortIndex() == null) {
					return 0;
				}
				return a.getSortIndex() - b.getSortIndex();
			});
			for (int i=0; i<sorts.size(); i++) {
				GridColDto col = sorts.get(i);
				String id = col.getId();
				String sort = col.getSort();
				if (tvo.isEs()) {
					sb.append(createEsOrderBy(col.getName(), col.getType()));
					sb.append(sort);
				} else {
					sb.append(" " + id + " ");
					sb.append(sort);
				}
				if(i != sorts.size() - 1){
					sb.append(",");
				}
			}
		}
		sb.append(" #end ");
		return sb.toString().replaceAll("@", "'").replaceAll("\\[x\\]", "\\$");
	}

	public Map<String, InputField> getMvParams() {
		return mvParams;
	}
}
