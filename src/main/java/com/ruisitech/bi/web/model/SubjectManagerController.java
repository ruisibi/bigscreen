/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.model;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.entity.model.TableMeta;
import com.ruisitech.bi.service.etl.EtlTableMetaService;
import com.ruisitech.bi.service.model.SubjectManagerService;
import com.ruisitech.bi.service.model.SubjectTypeService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/model")
public class SubjectManagerController extends BaseController {

	@Autowired
	private SubjectTypeService serivce;

	@Autowired
	private SubjectManagerService managerSerivce;

	@Autowired
	private EtlTableMetaService tableMetaService;


	@RequestMapping(value="/listSubject.action")
	public @ResponseBody
    Object listSubject(TableMeta table, PageParam page) {
		PageHelper.startPage(page.getPage(), page.getRows());
		table.setKey(page.getSearch()); //设置查询条件
		List<TableMeta> ls =  managerSerivce.listAllSubject(table);
		if(page.getOrder() != null && page.getSort() != null){
			SortService s = new SortService(page.getSort(), page.getOrder());
			Collections.sort(ls, s);
		}
		PageInfo<TableMeta> pageInfo= new PageInfo<TableMeta>(ls);
		return super.buildSucces(pageInfo);
	}

	/**
	 * 不分页
	 * @param table
	 * @return
	 */
	@RequestMapping(value="/listSubjectNoPage.action")
	public @ResponseBody
    Object listSubjectNoPage(TableMeta table) {
		List<TableMeta> ls =  managerSerivce.listAllSubject(table);
		return super.buildSucces(ls);
	}

	@RequestMapping(value="/listAuthSubject.action")
	public @ResponseBody
    Object listAuthSubject(TableMeta table, PageParam page) {
		PageHelper.startPage(page.getPage(), page.getRows());
		table.setKey(page.getSearch());
		List<TableMeta> ls = managerSerivce.listAuthSubject(table);
		PageInfo<TableMeta> pageInfo= new PageInfo<TableMeta>(ls);
		return super.buildSucces(pageInfo);
	}

	@RequestMapping(value="/delSubject.action")
	public @ResponseBody
    Object delSubject(Integer tid) {
		managerSerivce.delCube(tid);
		return super.buildSucces();
	}

	@RequestMapping(value="/getSubject.action")
	public @ResponseBody
    Object getSubject(Integer tableId) {
		try {
			TableMeta table = managerSerivce.getCube(tableId);
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("table", table);
			ret.put("types", serivce.list());
			List<EtlTableMetaCol> cols = tableMetaService.queryTableColumns(tableId, true);
			ret.put("cols", cols);
			return super.buildSucces(ret);
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	/**
	 * 获取指标列表
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value="/getSubjectKpis.action")
	public @ResponseBody
    Object getSubjectKpis(Integer tableId) {
		return super.buildSucces(managerSerivce.getCubeKpis(tableId));
	}

}
