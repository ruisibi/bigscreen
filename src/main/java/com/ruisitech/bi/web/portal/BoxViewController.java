/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.portal;

import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.MVContext;
import com.ruisitech.bi.entity.portal.BoxQuery;
import com.ruisitech.bi.entity.portal.PortalChartQuery;
import com.ruisitech.bi.service.portal.BoxService;
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

@Controller("portalBoxController")
@Scope("prototype")
@RequestMapping(value = "/portal")
public class BoxViewController extends BaseController {

	private static Logger log = LogManager.getLogger(BoxViewController.class);

	@Autowired
	private BoxService serivce;

	@RequestMapping(value={"/BoxView.action","/share/BoxView.action"}, method = RequestMethod.POST)
	public @ResponseBody
    Object view(@RequestBody BoxQuery box, HttpServletRequest req, HttpServletResponse res)  {
		try {
			ExtContext.getInstance().removeMV(BoxService.deftMvId);
			MVContext mv = serivce.json2MV(box);
			CompPreviewService ser = new CompPreviewService(req, res, req.getServletContext());
			ser.setParams(serivce.getMvParams());
			ser.initPreview();
			String ret = ser.buildMV(mv, req.getServletContext());
			JSONObject json = JSONObject.parseObject(ret);
			if (json.get("result") != null && json.getInteger("result") == 500) {
				return super.buildError(json.getString("msg"));
			}
			JSONObject boxJson = json.getJSONObject(box.getId());
			//判断是否有图形
			if(box.getChart() != null && box.getChart().getId() != null){
				boxJson.put(box.getChart().getId(), json.getJSONObject(box.getChart().getId()));
			}
			return super.buildSucces(boxJson);
		}catch(Exception ex) {
			log.error("box出错了。", ex);
			return super.buildError(ex.getMessage());
		}
	}

}
