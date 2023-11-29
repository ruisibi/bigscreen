/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.portal;

import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.MVContext;
import com.ruisitech.bi.entity.portal.PortalTableQuery;
import com.ruisitech.bi.service.portal.PortalTableService;
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

@Controller("portalTableController")
@Scope("prototype")
@RequestMapping(value = "/portal")
public class TableViewController extends BaseController {

	private static Logger log = LogManager.getLogger(TableViewController.class);

	@Autowired
	private PortalTableService serivce;

	@RequestMapping(value= {"/TableView.action", "/share/TableView.action"}, method = RequestMethod.POST)
	public @ResponseBody
    Object tableView(@RequestBody PortalTableQuery table, HttpServletRequest req, HttpServletResponse res) {
		try {
			//检测SQL注入
			ExtContext.getInstance().removeMV(PortalTableService.deftMvId);
			MVContext mv = serivce.json2MV(table);
			CompPreviewService ser = new CompPreviewService(req, res, req.getServletContext());
			ser.setParams(serivce.getMvParams());
			ser.initPreview();
			String ret = ser.buildMV(mv , req.getServletContext());
			JSONObject json = JSONObject.parseObject(ret);
			if (json.get("result") != null && json.getInteger("result") == 500) {
				return super.buildError(json.getString("msg"));
			}
			json = json.getJSONObject(table.getId());
			return super.buildSucces(json);

		}catch(Exception ex) {
			log.error("table出错了。", ex);
			return super.buildError(ex.getMessage());
		}
	}
}
