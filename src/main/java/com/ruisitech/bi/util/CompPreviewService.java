/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.rsbitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import com.rsbi.ext.engine.ConstantsEngine;
import com.rsbi.ext.engine.ExtConfigConstants;
import com.rsbi.ext.engine.ExtConstants;
import com.rsbi.ext.engine.control.InputOption;
import com.rsbi.ext.engine.control.InputOptionFactory;
import com.rsbi.ext.engine.cross.CrossFieldLoader;
import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.init.ExtEnvirContext;
import com.rsbi.ext.engine.init.ExtEnvirContextImpl;
import com.rsbi.ext.engine.service.loginuser.LoginUserFactory;
import com.rsbi.ext.engine.service.loginuser.LoginUserInfoLoader;
import com.rsbi.ext.engine.util.DaoUtils;
import com.rsbi.ext.engine.util.IdCreater;
import com.rsbi.ext.engine.view.builder.BuilderManager;
import com.rsbi.ext.engine.view.context.Element;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.MVContext;
import com.rsbi.ext.engine.view.context.form.InputField;
import com.rsbi.ext.engine.view.emitter.ContextEmitter;
import com.rsbi.ext.engine.view.emitter.json.JSON2Emitter;
import com.rsbi.ext.engine.view.exception.ExtRuntimeException;
import com.rsbi.ext.engine.wrapper.ExtRequest;
import com.rsbi.ext.engine.wrapper.ExtRequestImpl;
import com.rsbi.ext.engine.wrapper.ExtResponse;
import com.rsbi.ext.engine.wrapper.ExtResponseImpl;
import com.ruisitech.ext.service.ExtLoginInfoLoader;
import org.apache.commons.fileupload.FileUploadException;

import javax.script.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 组件预览类.创建必要的组件预览对象
 * @author hq
 * @date Aug 18, 2010
 */
public class CompPreviewService {

	private ExtRequest request;
	private ExtResponse response;
	private ServletContext context;
	private DaoHelper dao;

	private static Object lock = new Object();

	public DaoHelper getDao() {
		return dao;
	}

	private ExtEnvirContext envirCtx;
	public ExtEnvirContext getEnvirCtx() {
		return envirCtx;
	}

	Map<String, InputField> params = null; //请求的参数

	public Map<String, InputField> getParams() {
		return params;
	}

	public void setParams(Map<String, InputField> params) {
		this.params = params;
	}

	public CompPreviewService(ExtRequest request, ExtResponse response, ServletContext context, DaoHelper dao){
		this.request = request;
		this.response = response;
		this.context = context;
		this.dao = dao;
	}

	public CompPreviewService(HttpServletRequest req, HttpServletResponse resp, ServletContext sctx) throws IOException{
		request = new ExtRequestImpl(req);
		response = new ExtResponseImpl(resp);
		context = sctx;
		dao = DaoUtils.getDaoHelper(context);
		//out = new ExtWriterImpl(response.getWriter());
	}

	public void initPreview() throws FileUploadException {

		LoginUserFactory loginUserFactory = LoginUserFactory.getInstance();
		LoginUserInfoLoader loginUserInfoLoader = new ExtLoginInfoLoader();
		loginUserFactory.createLoginUser(request, response, dao, loginUserInfoLoader);

		//servletContext放入request, 显示图形时需要调用
		InputOption option = InputOptionFactory.createInputOption(request, response, params);
		request.setAttribute(ExtConstants.optionIdKey, option);
		ExtEnvirContext ec = new ExtEnvirContextImpl(option);
		this.envirCtx = ec;
		//request.setAttribute(ExtConstants.contextSessionKey, ec);

		//请求为post,设置fromId
		//request.setAttribute(ExtConstants.fromIdKey, TableService.deftMvId);

		//创建crossReport需要用到的loader
		CrossFieldLoader loader = null;
		String fieldLoader = ExtContext.getInstance().getConstant("fieldLoader");
		if(fieldLoader == null){
			throw new ExtRuntimeException("未配置 fieldLoader.");
		}
		try {
			loader = (CrossFieldLoader)Class.forName(fieldLoader).newInstance();
			loader.setRequest(this.request);
		} catch (Exception e) {
			String err = ConstantsEngine.replace(ExtConfigConstants.crossColNotExist, fieldLoader);
			throw new ExtRuntimeException(err);
		}
		request.setAttribute(ExtConstants.fieldLoader, loader);
	}

	public String buildMV(Element mv, ServletContext sctx) throws Exception{
		ContextEmitter emitter = new JSON2Emitter();
		return buildMV(mv, emitter, sctx);
	}

	public String buildMV(Element mv, ContextEmitter emitter, ServletContext sctx) throws Exception{
		//把MV放入内存对象
		if(mv instanceof MVContext){
			MVContext mvo = (MVContext)mv;
			String formId = ExtConstants.formIdPrefix + IdCreater.create();
			mvo.setFormId(formId);
			mvo.setCreateDate(new Date());
			ExtContext.getInstance().putMVContext(mvo, false);
		}
		Map<String, InputField> params =  this.params;
		InputOption option = InputOptionFactory.createInputOption(request, response, params);
		String ret = BuilderManager.buildContext2String(mv, request, this.envirCtx,  response, dao, emitter, option, sctx);
		return ret;
	}

	/**
	 * 预先编译js运算公式
	 * @throws ScriptException
	 */
	public void initJSFunc(String scripts) throws ScriptException{
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		//把一些常用的对象放入其中
		//engine.put("out", out);
		engine.put("request", this.request);
		Compilable compilable = (Compilable) engine;
		CompiledScript compiled = compilable.compile(scripts);
		compiled.eval();

		//把engine放入request,方便调用
		request.setAttribute(ExtConstants.scriptEngineKey, engine);
	}

	public ExtRequest getRequest() {
		return request;
	}

	public ExtResponse getResponse() {
		return response;
	}

	public ServletContext getContext() {
		return context;
	}
}
