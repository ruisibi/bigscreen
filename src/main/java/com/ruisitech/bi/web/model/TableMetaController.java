/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.model;

import com.ruisitech.bi.service.model.SubjectManagerService;
import com.ruisitech.bi.service.model.TableMetaServcice;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/model")
public class TableMetaController extends BaseController {

	@Autowired
	private TableMetaServcice service;

	@Autowired
	private SubjectManagerService managerSerivce;

	/**
	 * 获取立方体树形结构
	 * @param selectDsIds
	 * @return
	 */
	@RequestMapping(value="/cubeTree.action")
	public @ResponseBody
    Object cubeTree(String selectDsIds) {
		return super.buildSucces(service.getCubeTree(selectDsIds));
	}

	/**
	 * 获取立方体信息， 立方体的 维度，度量信息
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value="/cubeInfo.action")
	public @ResponseBody
    Object cubeInfo(Integer tableId) {
		return super.buildSucces(managerSerivce.getCube(tableId));
	}

	/**
	 * 获取表字段段信息，并包括维度字段类型
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value="/columnsWithDim.action")
	public @ResponseBody
	Object columnsWithDim(Integer tableId) {
		return super.buildSucces(managerSerivce.columnsWithDim(tableId));
	}
}
