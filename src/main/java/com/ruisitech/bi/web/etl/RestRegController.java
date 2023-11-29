/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.etl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.service.etl.EtlTableMetaService;
import com.ruisitech.bi.service.etl.TableRegService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 注册Rest接口到BI系统，BI直接查询Rest接口
 * @author hq
 * @Date 2020年3月21日
 */
@Controller
@RequestMapping(value = "/etl")
public class RestRegController extends BaseController {

	@Autowired
	private EtlTableMetaService service;

	@Autowired
	private TableRegService regService;

	@RequestMapping(value="/loadRegRestTable.action")
	public @ResponseBody
    Object loadRegTable(PageParam page){
		PageHelper.startPage(page.getPage(), page.getRows());
		List<EtlTableMeta> ls = service.selectTables("rest", page.getSearch(), page);
		PageInfo<EtlTableMeta> pageInfo=new PageInfo<EtlTableMeta>(ls);
		return super.buildSucces(pageInfo);
	}

	@PostMapping(value="/regRestTable.action")
	public @ResponseBody
    Object regRestTable(@RequestBody EtlTableMeta table){
		try {
			regService.regRestTable(table);
			return super.buildSucces();
		}catch(Exception ex) {
			return super.buildError(ex.getMessage());
		}
	}

	@PostMapping(value="/updateRestTable.action")
	public @ResponseBody
    Object update(@RequestBody EtlTableMeta table){
		try {
			regService.updateRestTable(table);
			return super.buildSucces();
		}catch(Exception ex) {
			return super.buildError(ex.getMessage());
		}
	}

	@GetMapping(value="/regRest/get.action")
	public @ResponseBody
    Object get(Integer tableId){
		return super.buildSucces(regService.getRestCfg(tableId));
	}
}
