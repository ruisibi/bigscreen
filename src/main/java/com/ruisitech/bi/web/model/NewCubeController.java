/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.model;

import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.entity.model.SubjectType;
import com.ruisitech.bi.entity.model.TableMeta;
import com.ruisitech.bi.service.etl.EtlTableMetaService;
import com.ruisitech.bi.service.model.SubjectManagerService;
import com.ruisitech.bi.service.model.SubjectTypeService;
import com.ruisitech.bi.service.model.TableMetaServcice;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/model")
public class NewCubeController extends BaseController {

	@Autowired
	private SubjectTypeService serivce;

	@Autowired
	private SubjectManagerService managerSerivce;

	@Autowired
	private EtlTableMetaService tableService;

	@Autowired
	private SubjectManagerService subjectService;

	@Autowired
	private TableMetaServcice tableMetaService;

	private final static Logger log = LogManager.getLogger(NewCubeController.class);

	@RequestMapping(value="/newCubeStep1.action")
	public @ResponseBody Object newCubeStep1(String tableLevel){
		List<SubjectType> types = serivce.list();
		List<EtlTableMeta> ls = tableService.selectTablesIncomeAndLevel(null, tableLevel, null);
		Map<String, Object> ret = new HashMap<>();
		ret.put("types", types);
		ret.put("tables", ls);
		return super.buildSucces(ret);
	}

	@RequestMapping(value="/queryMetaTableCols.action")
	public @ResponseBody
    Object queryMetaTableCols(TableMeta table, String onlyTableId, ModelMap model){
		try {
			if (onlyTableId != null && "y".equals(onlyTableId)) {
				table = tableMetaService.getTable(table.getTid());
			}
			List<EtlTableMetaCol> ls = tableService.queryTableColumns(table.getTid(), true);
			return super.buildSucces(ls);
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/newCubeStep2.action")
	public @ResponseBody Object newCubeStep2(TableMeta table, String onlyTableId){
		try {
			if (onlyTableId != null && "y".equals(onlyTableId)) {
				table = tableMetaService.getTable(table.getTid());
			}
			List<EtlTableMetaCol> ls = tableService.queryTableColumns(table.getTid(), true);
			return super.buildSucces(ls);
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	/**
	 * 判断数据表是否已经建模
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value="/tableExist.action")
	public @ResponseBody
    Object tableExist(Integer tableId) {
		Integer ret =  subjectService.tableExist(tableId);
		return super.buildSucces(ret);
	}

	@RequestMapping(value="/saveCube.action", method = RequestMethod.POST)
	public @ResponseBody
    Object saveCube(@RequestBody TableMeta table) {
		try {
			managerSerivce.saveCube(table);
			return super.buildSucces();
		}catch (Exception ex){
			log.error("立方体保存出错.", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/updateCube.action", method = RequestMethod.POST)
	public @ResponseBody
    Object updateCube(@RequestBody TableMeta table) {
		try {
			managerSerivce.updateCube(table);
			return super.buildSucces();
		}catch (Exception ex){
			log.error("立方体保存出错.", ex);
			return super.buildError(ex.getMessage());
		}
	}
}
