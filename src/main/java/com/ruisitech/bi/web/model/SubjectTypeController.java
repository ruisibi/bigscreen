/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有 
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.model;

import com.alibaba.fastjson.JSONArray;
import com.ruisitech.bi.entity.model.SubjectType;
import com.ruisitech.bi.service.model.SubjectTypeService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/model")
public class SubjectTypeController extends BaseController {
	
	@Autowired
	private SubjectTypeService serivce;

	@RequestMapping(value="/SubjectType.action")
	public @ResponseBody Object index(){
		List<Map<String, Object>> datas = serivce.selectByTree();
		return super.buildSucces(datas);
	}
	
	@RequestMapping(value="/listSubjectType.action")
	public @ResponseBody
    Object listSubjectType() {
		return super.buildSucces(serivce.selectByTree());
	}
	
	@RequestMapping(value="/saveSubjectType.action")
	public @ResponseBody
    Object saveSubjectType(SubjectType type) {
		type.setPid(0);
		type.setTp("type");
		serivce.insert(type);
		return super.buildSucces(type.getDsId());
	}
	
	@RequestMapping(value="/updateSubjectType.action")
	public @ResponseBody
    Object upateSubjectType(SubjectType type) {
		serivce.updateByPrimaryKey(type);
		return super.buildSucces();
	}
	
	@RequestMapping(value="/delSubjectType.action")
	public @ResponseBody
    Object delSubjectType(Integer id) {
		int cnt = serivce.cntTables(id);
		if(cnt > 0){
			return super.buildError("分类下含有报表，不能删除。");
		}else{
			serivce.deleteByPrimaryKey(id);
		}
		return super.buildSucces();
	}
	
	@RequestMapping(value="/getSubjectType.action")
	public @ResponseBody
    Object getSubjectType(Integer id) {
		return super.buildSucces(serivce.selectByPrimaryKey(id));
	}
}
