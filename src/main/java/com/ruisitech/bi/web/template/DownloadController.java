/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.template;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.bigscreen.BigScreen;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.service.template.DownloadService;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 模版下载类
 * 以 remote 开始的URL 表示此URL 是暴露给外部调用的
 * @Author gdp
 * @Date 2013-7-18
 */
@Controller
@RequestMapping(value = "/template")
public class DownloadController extends BaseController {

	@Autowired
	private DownloadService downloadService;

	private static Logger log = LogManager.getLogger(DownloadController.class);

	/**
	 * 获取模版列表
	 * @return Object
	 */
	@GetMapping(value="/queryTemplates.action")
	public @ResponseBody
    Object queryTemplates(PageParam page) {
		try {
			List<BigScreen> bs = downloadService.queryTemplates(page);
			PageInfo<BigScreen> pageInfo=new PageInfo<>(bs);
			pageInfo.setTotal(page.getTotal());
			return super.buildSucces(pageInfo);
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	/**
	 * 远程调用接口
	 * @param page
	 * @return
	 */
	@GetMapping(value="/remote/list.action")
	public @ResponseBody
	Object list(PageParam page, String token) {
		try {
			if (page != null && page.getPage() != null && page.getRows() != null) {
				PageHelper.startPage(page.getPage(), page.getRows());
			}
			List<BigScreen> bs = downloadService.list(token);
			PageInfo<BigScreen> pageInfo = new PageInfo<>(bs);
			return super.buildSucces(pageInfo);
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	@GetMapping(value="/getTemplateUrl.action")
	public @ResponseBody
	Object getTemplateUrl() {
		return super.buildSucces(downloadService.getTemplateUrl());
	}

	@GetMapping(value="/remote/getTemplate.action")
	public @ResponseBody
	Object getTemplate(Integer id, String token) {
		try {
			BigScreen bs = downloadService.getTemplate(id, token);
			return super.buildSucces(bs);
		}catch (Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	@GetMapping(value="/download.action")
	public @ResponseBody
	Object download(Integer id) {
		try {
			downloadService.download(id);
			return super.buildSucces();
		}catch (Exception ex){
			log.error("大屏下载出错",ex);
			return super.buildError(ex.getMessage());
		}
	}

	@GetMapping(value="/remote/getImage.action")
	public @ResponseBody
	void getImage(String name, String token, HttpServletResponse resp) {
		try {
			//resp.setContentType("image/png");
			if(name.endsWith("svg")){
				resp.setContentType("image/svg+xml");
			}
			downloadService.getImage(resp.getOutputStream(), name, token);
		}catch (Exception ex){
			log.error("资源下载出错",ex);
		}
	}
}
