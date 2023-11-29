/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.portal;

import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.MVContext;
import com.ruisitech.bi.entity.portal.PortalChartQuery;
import com.ruisitech.bi.service.bireport.AreaService;
import com.ruisitech.bi.service.portal.PortalChartService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.CompPreviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller("portalChartController")
@Scope("prototype")
@RequestMapping(value = "/portal")
public class ChartViewController extends BaseController {


	private static final Logger log = LogManager.getLogger(ChartViewController.class);

	@Autowired
	private PortalChartService chartService;


	@Autowired
	private AreaService areaService;

	@RequestMapping(value= {"/ChartView.action", "/share/ChartView.action"}, method = RequestMethod.POST)
	public @ResponseBody
    Object chartView(@RequestBody PortalChartQuery chartJson, HttpServletRequest req, HttpServletResponse res) {
		try {
			ExtContext.getInstance().removeMV(PortalChartService.deftMvId);
			MVContext mv = chartService.json2MV(chartJson);
			CompPreviewService ser = new CompPreviewService(req, res, req.getServletContext());
			ser.setParams(chartService.getMvParams());
			ser.initPreview();
			String ret = ser.buildMV(mv, req.getServletContext());
			JSONObject obj = JSONObject.parseObject(ret);
			if(obj.get("result") != null && obj.getInteger("result") == 500){
				return super.buildError(obj.getString("msg"));
			}
			obj = obj.getJSONObject(chartJson.getId());
			return super.buildSucces(obj);
		}catch(Exception ex) {
			log.error("chart出错了。", ex);
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/chartColors.action")
	public @ResponseBody
    Object chartColors() {
		return super.buildSucces(chartService.queryChartColors());
	}
}
