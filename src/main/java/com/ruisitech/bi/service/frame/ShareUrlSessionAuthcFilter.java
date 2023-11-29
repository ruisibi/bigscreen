/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.ExtConstants;
import com.ruisitech.bi.entity.common.RequestStatus;
import com.ruisitech.bi.entity.common.Result;
import com.ruisitech.bi.entity.report.ShareUrl;
import com.ruisitech.bi.service.report.ShareUrlService;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;

/**
 * 通过共享URL, 访问共享URL时调用的 shiro 拦截器
 * @author hq
 *
 */
public class ShareUrlSessionAuthcFilter extends AdviceFilter {

	private ShareUrlService urlService;

	public ShareUrlSessionAuthcFilter(ShareUrlService urlService){
		this.urlService = urlService;
	}

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response)
			throws Exception {
		Subject us = SecurityUtils.getSubject();
		String token = request.getParameter("token");
		ShareUrl shareUrl = urlService.getByToken(token);
		if(shareUrl == null){
			response.setContentType("application/json; charset=utf-8");
			response.setCharacterEncoding("utf-8");
			Result r = new Result(RequestStatus.FAIL_FIELD.getStatus(), "资源不存在", null);
			response.getWriter().print(JSONObject.toJSONString(r));
			return false;
		}
		//判断是否过期
		Date crtdate = shareUrl.getCrtdate();
		Date now = new Date();
		long between = (now.getTime() - crtdate.getTime()) / (1000 * 60 * 60);  //相差小时
		if(shareUrl.getYxq() != - 1 && between  > shareUrl.getYxq()){
			response.setContentType("application/json; charset=utf-8");
			response.setCharacterEncoding("utf-8");
			Result r = new Result(RequestStatus.FAIL_FIELD.getStatus(), "资源已过有效期", null);
			response.getWriter().print(JSONObject.toJSONString(r));
			return false;
		}

		request.setAttribute("surl", shareUrl);
		//如果需要登录，但用户未登录，让用户登录
		if(shareUrl.getIslogin() == 1){
			if(us.isAuthenticated() || us.isRemembered()){

			}else{ //未登录，跳到登录页面

				//跳到登录页面
				//res.sendRedirect("../Login.action?backurl=report/shareView.action?token="+token);
				response.setContentType("application/json; charset=utf-8");
				response.setCharacterEncoding("utf-8");
				Result r = new Result(RequestStatus.NOLOGIN.getStatus(), "未登录",   getUrlByType(shareUrl.getrType())+ "?token="+token);
				response.getWriter().print(JSONObject.toJSONString(r));
				return false;

			}
		}

		return true;
	}

	private String getUrlByType(int tp){
		if(tp == 1){
			return "/portal/ShareView";
		}else if(tp == 2){
			return "/dashboard/ShareView";
		}else if(tp == 3){
			return "/bigscreen/ShareView"; //大屏
		}else if(tp == 4){
			return "/bireport/ShareView"; //olap
		}else{
			throw new RuntimeException("分享类型不匹配");
		}
	}
}
