/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.etl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.etl.EsRegDto;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.*;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/etl")
public class TableRegController extends BaseController {

	@Autowired
	private EtlTableMetaService service;

	@Autowired
	private TableRegService regService;



	@Autowired
	private MetaService metaService;


	@Autowired
	private TableCacheService cahceService;

	@RequestMapping(value="/loadRegTable.action")
	public @ResponseBody
    Object loadRegTable(PageParam page){
		PageHelper.startPage(page.getPage(), page.getRows());
		List<EtlTableMeta> ls = service.selectTables("custom", page.getSearch(), page);
		PageInfo<EtlTableMeta> pageInfo=new PageInfo<EtlTableMeta>(ls);
		return super.buildSucces(pageInfo);
	}

	@RequestMapping(value="/loadRegEsTable.action")
	public @ResponseBody
    Object loadRegEsTable(PageParam page){
		PageHelper.startPage(page.getPage(), page.getRows());
		List<EtlTableMeta> ls = service.selectTables("es", page.getSearch(), page);
		PageInfo<EtlTableMeta> pageInfo=new PageInfo<EtlTableMeta>(ls);
		return super.buildSucces(pageInfo);
	}


	@RequestMapping(value="/delRegTable.action")
	public @ResponseBody
    Object deleteRegTable(Integer tableId){
		service.deleteTable(tableId, false);
		return super.buildSucces();
	}

	@RequestMapping(value="/flashRegTable.action")
	public @ResponseBody
    Object flashRegTable(Integer tableId, String tableName, String income, Integer dsid){
		try{
			EsRegDto dto = new EsRegDto(tableName);
			if("es".equals(income)){
				TableInfoVO tvo = cahceService.getTableInfo(tableId);
				dto.setVersion(tvo.getEsVersion());
				dto.setUrl(tvo.getEsUrl());
				dto.setUname(tvo.getEsUname());
				dto.setPsd(tvo.getEsPsd());
			}
			regService.saveOrUpdateTable(dto, tableId, dsid, income.equals("custom")?false:true, true);
			return super.buildSucces();
		}catch(Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/listSchema.action")
	public @ResponseBody
    Object listSchema(Integer dsource) {
		try {
			List<String> ret = metaService.queryDbSchemas(dsource);
			return super.buildSucces(ret);
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/currSchema.action")
	public @ResponseBody
    Object currSchema(Integer dsource) {
		String schema = metaService.getCurrentSchema(dsource);
		return super.buildSucces(schema);
	}

	@RequestMapping(value="/regSchemaTable.action")
	public @ResponseBody
    Object regSchemaTable(String tableName, Integer dsource) {
		try {
			EsRegDto dto = new EsRegDto(tableName);
			regService.reg(dto, dsource);
			return super.buildSucces();
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/regEsTable.action")
	public @ResponseBody
    Object regEsTable(EsRegDto dto) {
		try {
			regService.regEsTable(dto);
			return super.buildSucces();
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}
}
