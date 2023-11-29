/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.frame;

import com.ruisitech.bi.entity.frame.Department;
import com.ruisitech.bi.service.frame.DepartmentService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/frame")
public class DepartmentController extends BaseController {

	@Autowired
	private DepartmentService serivce;

	@RequestMapping(value="/loadDepartment.action")
	public @ResponseBody
    Object loadDepartment(Integer id){
		return super.buildSucces(serivce.tree(id));
	}

	@RequestMapping(value="/loadAllDepartment.action")
	public @ResponseBody
    Object loadAllDepartment(){
		return super.buildSucces(serivce.list());
	}

	@RequestMapping(value="/delDepartment.action")
	public @ResponseBody
    Object delDepartment(Integer id){
		if(serivce.cntDepartmentUsers(id) == 0){
			serivce.deleteByPrimaryKey(id);
			return super.buildSucces();
		}else{
			return super.buildError("存在用户，不能删除。");
		}
	}

	@RequestMapping(value="/saveDepartment.action", method = RequestMethod.POST)
	public @ResponseBody
    Object saveDepartment(Department dept){
		serivce.insertSelective(dept);
		return super.buildSucces(dept.getId());
	}

	@RequestMapping(value="/updateDepartment.action", method = RequestMethod.POST)
	public @ResponseBody
    Object updateDepartment(Department dept){
		serivce.updateByPrimaryKeySelective(dept);
		return super.buildSucces();
	}

	@RequestMapping(value="/getDepartment.action")
	public @ResponseBody
    Object getDepartment(Integer id){
		return super.buildSucces(serivce.selectByPrimaryKey(id));
	}
}
