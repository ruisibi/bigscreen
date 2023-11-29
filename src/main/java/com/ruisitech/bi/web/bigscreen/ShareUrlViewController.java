/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.bigscreen;

import com.ruisitech.bi.entity.bigscreen.BigScreen;
import com.ruisitech.bi.entity.report.ShareUrl;
import com.ruisitech.bi.service.bigscreen.BigScreenService;
import com.ruisitech.bi.service.report.ShareUrlService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.IsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 访问 shareUrl 方式展现的报表 (报表共享)
 * @author hq
 *
 */
@Controller
@Scope("prototype")
@RequestMapping(value = "/report")
public class ShareUrlViewController extends BaseController {

	@Autowired
	private BigScreenService bigScreenService;

	@Autowired
	private ShareUrlService urlService;

	@RequestMapping(value="/share/get.action")
	public @ResponseBody
	Object shareGet(String token, HttpServletRequest request) {
		ShareUrl surl = urlService.getByToken(token);
		String cfg =  null;
		if(surl.getrType() != null && 3 == surl.getrType()){ //大屏
			BigScreen bs = bigScreenService.selectByPrimaryKey(new Integer(surl.getReportId()));
			cfg = bs.getPageInfo();
		}
		if(cfg == null){
			return this.buildError("token不存在");
		}
		Map<String, Object> m = new HashMap<>();
		boolean is3g = IsModel.check(request);
		m.put("mobile", is3g);
		m.put("cfg", cfg);
		m.put("compId", surl.getCompId());
		return this.buildSucces(m);
	}
}
