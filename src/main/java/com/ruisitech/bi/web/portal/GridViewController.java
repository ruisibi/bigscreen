/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.portal;

import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.MVContext;
import com.ruisitech.bi.entity.portal.GridQuery;
import com.ruisitech.bi.service.portal.PortalGridService;
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

@Controller("portalGridController")
@Scope("prototype")
@RequestMapping(value = "/portal")
public class GridViewController extends BaseController {

	private static Logger log = LogManager.getLogger(GridViewController.class);

	@Autowired
	private PortalGridService serivce;

	@RequestMapping(value= {"/GridView.action", "/share/GridView.action"}, method = RequestMethod.POST)
	public @ResponseBody
    Object gridView(@RequestBody GridQuery grid, HttpServletRequest req, HttpServletResponse res) {
		try {
			ExtContext.getInstance().removeMV(PortalGridService.deftMvId);
			MVContext mv = serivce.json2MV(grid);
			CompPreviewService ser = new CompPreviewService(req, res, req.getServletContext());
			ser.setParams(serivce.getMvParams());
			ser.initPreview();
			String ret = ser.buildMV(mv , req.getServletContext());
			JSONObject json = JSONObject.parseObject(ret);
			if (json.get("result") != null && json.getInteger("result") == 500) {
				return super.buildError(json.getString("msg"));
			}
			json = json.getJSONObject(grid.getId());
			return super.buildSucces(json);
		}catch(Exception ex) {
			log.error("grid出错了。", ex);
			return super.buildError(ex.getMessage());
		}
	}

}
