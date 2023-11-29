/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.etl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.dashboard.TreeQueryDto;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.dashboard.TreeParamService;
import com.ruisitech.bi.service.etl.*;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/etl")
public class TableManagerController extends BaseController {

	private static Logger log = LogManager.getLogger(TableManagerController.class);

	@Autowired
	private EtlTableMetaService service;

	@Autowired
	private DataSetService dsService;

	@Autowired
	private TableCacheService cahceService;

	@Autowired
	private QueryRestService restService;

	@Autowired
	private TreeParamService treeParamService;

	@Autowired
	private DatasetService datasetService;

	@RequestMapping(value="/loadTable.action")
	public @ResponseBody
    Object loadTable(String income, String tableLevel, PageParam page){
		PageHelper.startPage(page.getPage(), page.getRows());
		List<EtlTableMeta> ls = service.selectTables(income, tableLevel, page.getSearch(), page);
		PageInfo<EtlTableMeta> pageInfo=new PageInfo<EtlTableMeta>(ls);
		return super.buildSucces(pageInfo);
	}

	@RequestMapping(value="/loadByIncomes.action")
	public @ResponseBody
    Object loadTableByIncomes(String income, PageParam page){
		if(page != null && page.getPage() != null && page.getRows() != null){
			PageHelper.startPage(page.getPage(), page.getRows());
		}
		String[] ims = income.split(",");
		List<String> incomes = Arrays.asList(ims);
		List<EtlTableMeta> ls = service.selectByIncomes(incomes, page);
		if(page != null && page.getPage() != null && page.getRows() != null){
			PageInfo<EtlTableMeta> pageInfo=new PageInfo<EtlTableMeta>(ls);
			return super.buildSucces(pageInfo);
		}else{
			return ls;
		}
	}

	@RequestMapping(value="/tableExist.action")
	public @ResponseBody
    Object tableExist(String tableName){
		int tableCnt = service.tableExist(tableName);
		return super.buildSucces(tableCnt);
	}

	@RequestMapping(value="/updateTname.action")
	public @ResponseBody
	Object updateTname(String tableName, Integer tid){
		int tableCnt = service.tableExist(tableName);
		if(tableCnt > 0){
			return super.buildError("表名已经存在");
		}else{
			EtlTableMeta table = new EtlTableMeta();
			table.setTableId(tid);
			table.setTableName(tableName);
			service.updateTableName(table);
			return super.buildSucces();
		}
	}

	@RequestMapping(value="/saveTableInfo.action", method = RequestMethod.POST)
	public @ResponseBody
    Object saveTableInfo(@RequestBody EtlTableMeta table){
		//table.setIncome("dw");  通过前端设置
		table.setCrtUser(RSBIUtils.getLoginUserInfo().getUserId());
		try{
			EtlTableMeta ret = service.saveTableInfo(table);
			return super.buildSucces(ret);
		}catch(Exception ex){
			log.error("创建表失败", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/updateTableInfo.action", method = RequestMethod.POST)
	public @ResponseBody
    Object updateTableInfo(@RequestBody EtlTableMeta table){
		service.updateTableInfo(table);
		return super.buildSucces();
	}

	@RequestMapping(value="/delTable.action")
	public @ResponseBody
    Object deleteTable(Integer tableId){
		service.deleteTable(tableId, true);
		return super.buildSucces();
	}

	@RequestMapping(value="/getTableInfo.action")
	public @ResponseBody
    Object getTableInfo(Integer tableId){
		EtlTableMeta table = service.getTableAll(tableId);
		return super.buildSucces(table);
	}

	@RequestMapping(value={"/queryTableDataAsJson.action", "/share/queryTableDataAsJson.action"})
	public @ResponseBody
    Object queryTableDataAsJson(Integer tableId, String text, String value, String order,  String parentCol, String parentVal) {
		try {
			return super.buildSucces(service.queryTableDataAsJson(tableId, text, value, order, parentCol, parentVal));
		}catch (Exception ex){
			log.error("查询出错。", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value={"/queryTreeData.action", "/share/queryTreeData.action"})
	public @ResponseBody
	Object queryTreeData(TreeQueryDto dto) {
		try {
			return super.buildSucces(treeParamService.queryTreeDatas(dto));
		}catch (Exception ex){
			log.error("查询出错。", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value={"/queryMultiTablesDataAsJson.action", "/share/queryMultiTablesDataAsJson.action"})
	public @ResponseBody
	Object queryMultiTablesDataAsJson(@RequestBody List<Map<String, Object>> datas) {
		try {
			Map<String, Object> ret = new HashMap<>();
			for(Map<String, Object> dt : datas){
				Integer tableId = (Integer) dt.get("tableId");
				String text = (String)dt.get("text");
				String value = (String) dt.get("value");
				String paramId = (String) dt.get("paramId");
				String parentCol = (String) dt.get("parentCol");
				String parentVal =(String) dt.get("parentVal");
				String order = (String) dt.get("order");
				List<Map<String, Object>> ls = service.queryTableDataAsJson(tableId, text, value, order, parentCol, parentVal);
				ret.put(paramId, ls);
			}
			return super.buildSucces(ret);
		}catch (Exception ex){
			log.error("查询出错。", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/queryTableData.action")
	public @ResponseBody Object queryTableData(Integer tableId) {
		EtlTableMeta t = this.service.getTableOnly(tableId);
		if(t == null){
			return super.buildError("ID为 "+tableId+" 的表不存在");
		}
		String sql = "select * from " + datasetService.createTableSql(t.getTableSql(), t.getTableName());
		try {
			List<Object> ls = null;
			TableInfoVO tinfo = cahceService.getTableInfo(tableId);
			if(tinfo.isRest()) {
				ls = restService.queryRestColsAndDatas(tinfo, 20);
			}else {
				ls = dsService.queryTopN(sql, tinfo.getDsource(), 20);
			}
			return super.buildSucces(ls);
		} catch (Exception e) {
			log.error(e);
			return super.buildError(e.getMessage());
		}
	}

	/**
	 * 清除表数据及时间戳
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value="/table/clearData.action")
	public @ResponseBody Object clearData(Integer tableId,Boolean dropTable, String useIn, Integer tfId) {
		try {
			if(dropTable == null){
				dropTable = false;
			}
			service.clearData(tableId , dropTable, useIn, tfId);
		}catch (Exception ex){
			log.error(ex);
			return super.buildError(ex.getMessage());
		}
		return super.buildSucces();
	}
}
