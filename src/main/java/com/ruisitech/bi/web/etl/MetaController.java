/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.etl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.DataSourceService;
import com.ruisitech.bi.service.etl.EtlTableMetaService;
import com.ruisitech.bi.service.etl.MetaService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(value = "/etl")
public class MetaController extends BaseController {

	private Logger log = LogManager.getLogger(MetaController.class);

	@Autowired
	private EtlTableMetaService tableMetaService;

	@Autowired
	private MetaService service;

	@Autowired
	private TableCacheService cacheService;

	@Autowired
	private DataSourceService dsService;

	/**
	 * 查询 etl 已经定义的表 , 根据income 查询 etl/custom/dw/sql/tf等
	 * @param income
	 * @return
	 */
	@RequestMapping(value="/listTables.action")
	public @ResponseBody
    Object listTables(String income, String keyword) {
		List<EtlTableMeta>  ret = tableMetaService.selectTablesIncomeAndLevel(income, null, keyword);
		return super.buildSucces(ret);
	}

	/**
	 * 查询所有不是ES/Rest注册的表
	 * @param income
	 * @return
	 */
	@RequestMapping(value="/listTablesNotEs.action")
	public @ResponseBody
    Object listTablesNotEs(String income, Integer dsource) {
		List<EtlTableMeta> ret = tableMetaService.selectTablesNotEsAndRest(dsource, true);
		return super.buildSucces(ret);
	}

	/**
	 * 查询所有不是Rest注册的表
	 * @param income
	 * @return
	 */
	@RequestMapping(value="/listTablesNotRest.action")
	public @ResponseBody
    Object listTablesNotRest(String keyword, String income) {
		List<EtlTableMeta> ret = tableMetaService.selectTablesNotRest(keyword, income);
		return super.buildSucces(ret);
	}

	/**
	 * 数据查询选择表
	 * @param income
	 * @return
	 */
	@RequestMapping(value="/dataQuerySearch.action")
	public @ResponseBody
    Object dataQuerySearch(String keyword, String income) {
		Integer crtUser = null;
		return super.buildSucces(tableMetaService.selectTablesNotRest(keyword, crtUser, income));
	}

	/**
	 * 数据填报 选择表
	 * @return
	 */
	@RequestMapping(value="/dwselectTables.action")
	public @ResponseBody
    Object dwselectTables() {
		List<EtlTableMeta> ret = tableMetaService.dwselectTables();
		return super.buildSucces(ret);
	}

	/**
	 * 从元数据中查询表，查询数据库中存在的表
	 * @param dsource
	 * @return
	 */
	@RequestMapping(value="/listdbTables.action")
	public @ResponseBody
    Object listdbTables(DataSource dsource) {
		try {
			List<Map<String, Object>> ls = service.queryTables(null, dsource);
			return this.buildSucces(ls);
		}catch(Exception ex){
			log.error("出错了。", ex);
			return this.buildError(ex.getMessage());
		}
	}

	/**
	 * 获取制定模式名下的表
	 * @param
	 * @returnid
	 */
	@RequestMapping(value="/listTablesBySchema.action")
	public @ResponseBody
    Object listTablesBySchema(String id, Integer dsource) {
		try {
			DataSource ds = null;
			if(dsource != null && dsource != -1){
				ds = dsService.selectDataSourceByPrimaryKey(dsource);
			}
			List<Map<String, Object>> ls = service.queryTables(id, ds);
			return super.buildSucces(ls);
		}catch(Exception ex){
			log.error("出错了。", ex);
			return this.buildError(ex.getMessage());
		}
	}

	/**
	 * 通过数据源ID获取表字段
	 * @param tname
	 * @param dsId
	 * @return
	 */
	@RequestMapping(value="/ds/listTableColumns.action")
	public @ResponseBody
    Object listTableColumns(String tname, Integer dsId) {
		if(dsId == null || dsId == -1){
			return this.listTableColumnsByDs(tname, null);
		}else {
			DataSource ds = dsService.selectDataSourceByPrimaryKey(dsId);
			return this.listTableColumnsByDs(tname, JSON.toJSONString(ds));
		}
	}

	/**
	 * 通过元数据获取表字段
	 * @param tname
	 * @param dsource dsource的 json 格式数据
	 * @return
	 */
	@RequestMapping(value="/listTableColumns.action", method = RequestMethod.POST)
	public @ResponseBody
    Object listTableColumnsByDs(String tname, String dsource) {
		try {
			DataSource ds = null;
			String sql = "select * from " + tname;
			if(dsource != null){
				JSONObject json = (JSONObject)JSON.parse(dsource);
				ds = JSONObject.toJavaObject(json, DataSource.class);
			}else{
				TableInfoVO tvo = cacheService.getTableInfoByTname(tname);
				ds = tvo.getDsource();
			}
			List<DSColumn> ls = service.queryTableCols(sql, ds, false);
			//传换成easyUI tree 结构
			List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
			for(int i=0; i<ls.size(); i++){
				DSColumn t = (DSColumn)ls.get(i);
				String name = t.getName();
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("id", name);
				m.put("text", name);
				m.put("name", name);
				m.put("icon", "glyphicon glyphicon-menu-hamburger");
				Map<String, Object> attr = new HashMap<String, Object>();
				attr.put("tp", "col");
				m.put("li_attr", attr);
				ret.add(m);
			}
			return super.buildSucces(ret);
		}catch(Exception ex){
			log.error("出错了。", ex);
			return this.buildError(ex.getMessage());
		}
	}

	/**
	 * 根据表ID,查询表字段列表
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value="/getTableColumns.action")
	public @ResponseBody
    Object getTableColumns(Integer tableId) {
		return super.buildSucces(tableMetaService.queryTableColumns(tableId, true));
	}

	/**
	 * 更新表ID,查询表字段列表,不包含表达式
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value="/getTableColumnsNotExpress.action")
	public @ResponseBody
    Object getTableColumnsNotExpress(Integer tableId) {
		List<EtlTableMetaCol> ret = tableMetaService.queryTableColumns(tableId, false);
		return super.buildSucces(ret);
	}



	@RequestMapping(value="/getDBName.action")
	public @ResponseBody
    Object getDBName(Integer tid) {
		String dbName = RSBIUtils.getConstant("dwName");
		TableInfoVO tvo = cacheService.getTableInfo(tid);
		if(tvo.isRegEsTable()) {
			dbName = "es";  //是不是es查询
		}else{
			if(tvo.getDsource() != null){
				dbName = tvo.getDsource().getLinkType();
			}
		}
		return super.buildSucces(dbName);
	}

	@RequestMapping(value={"/getRestParam.action", "/share/getRestParam.action"})
	public @ResponseBody
    Object getRestParam(Integer tid) {
		EtlTableMeta table = tableMetaService.getTableOnly(tid);
		return super.buildSucces(table.getRestParams());
	}

	/**
	 * 根据表类型计算表的数量
	 * @return
	 */
	@RequestMapping(value="/dataware/cntTables.action")
	public @ResponseBody
	Object cntTables() {
		Map<String, Integer> ret = new HashMap<>();
		ret.put("ods", tableMetaService.cntTables("ods"));
		ret.put("dw", tableMetaService.cntTables("dw"));
		ret.put("dm", tableMetaService.cntTables("dm"));
		ret.put("code", tableMetaService.cntTables("code"));
		return super.buildSucces(ret);
	}

}
