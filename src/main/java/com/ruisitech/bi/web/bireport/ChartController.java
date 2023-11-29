/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.bireport;

import com.ruisitech.bi.entity.bireport.Area;
import com.ruisitech.bi.service.bireport.AreaService;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller("bireportChart")
@Scope("prototype")
@RequestMapping(value = "/bireport")
public class ChartController extends BaseController {

	@Autowired
	private AreaService areaService;



	private static Logger logger = LogManager.getLogger(ChartController.class);

	@RequestMapping(value={"/getProvByName.action", "/share/getProvByName.action"})
	public @ResponseBody
	Object getProvByName(String name){
		return super.buildSucces(areaService.getProvByName(name));
	}

	@RequestMapping(value={"/listAreas.action", "/share/listAreas.action"})
	public @ResponseBody Object listAreas(){
		List<Map<String, Object>> areas = areaService.listProvAndCitys();
		return super.buildSucces(areas);
	}

	@RequestMapping(value={"/getCityByName.action", "/share/getCityByName.action"})
	public @ResponseBody
    Object getCityByName(String name){
		return areaService.getCityByName(name);
	}

	@RequestMapping(value={"/getProvByCityCode.action", "/share/getProvByCityCode.action"})
	public @ResponseBody
    Object getProvByCityCode(String code){
		Area area = areaService.getProvByCityCode(code);
		return super.buildSucces(area);
	}
}
