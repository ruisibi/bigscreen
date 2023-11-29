/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.etl;

import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.entity.model.TableMeta;
import com.ruisitech.bi.service.etl.EtlTableMetaColService;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 表字段管理controller
 * @author hq
 *
 */
@Controller
@RequestMapping(value = "/etl")
public class TableColumnController extends BaseController {

	private static Logger log = LogManager.getLogger(TableColumnController.class);

	@Autowired
	private EtlTableMetaColService service;



	/**
	 * 创建动态字段
	 * @param col
	 * @return
	 */
	@RequestMapping(value="/createTableDyna.action")
	public @ResponseBody
    Object createTableDyna(EtlTableMetaCol col){
		col.setColOrd(99);  //动态字段默认99
		service.insertMetaTableCol(col, true);
		return super.buildSucces();
	}

	/**
	 * 添加数据库字段
	 * @param col
	 * @return
	 */
	@RequestMapping(value="/addTableColumn.action")
	public @ResponseBody
    Object addTableColumn(EtlTableMetaCol col){
		col.setColOrd(66);  //动态字段默认66
		try{
			service.insertMetaTableCol(col, false);
			return super.buildSucces();
		}catch(Exception ex){
			ex.printStackTrace();
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/updateTableDyna.action")
	public @ResponseBody
    Object updateTableDyna(EtlTableMetaCol col){
		service.updateTableCol(col, true);
		return super.buildSucces();
	}

	@RequestMapping(value="/updateTableColumn.action")
	public @ResponseBody
    Object updateTableCol(EtlTableMetaCol col, Integer only){
		try{
			if(only != null && only == 1) {
				service.updateTableColOnly(col);
			}else {
				service.updateTableCol(col, false);
			}
			return super.buildSucces();
		}catch(Exception ex){
			log.error("出错了。", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/getTableColumn.action")
	public @ResponseBody
    Object getTableColumn(Integer tableId, Integer colId){
		EtlTableMetaCol ret = service.getTableColumn(tableId, colId);
		return super.buildSucces(ret);
	}

	@RequestMapping(value="/delTableColumn.action")
	public @ResponseBody
    Object delTableColumn(Integer tableId, Integer colId){
		try{
			service.delTableColumn(tableId, colId);
			return super.buildSucces();
		}catch(Exception ex){
			log.error("出错了。", ex);
			return super.buildError(ex.getMessage());
		}
	}

}
