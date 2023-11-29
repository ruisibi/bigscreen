/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.bireport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.util.IdCreater;
import com.rsbi.ext.engine.view.context.MVContext;
import com.rsbi.ext.engine.view.context.dc.grid.GridDataCenterContext;
import com.rsbi.ext.engine.view.context.dc.grid.GridDataCenterContextImpl;
import com.rsbi.ext.engine.view.context.dc.grid.GridSetConfContext;
import com.rsbi.ext.engine.view.context.dsource.DataSourceContext;
import com.rsbi.ext.engine.view.context.form.InputField;
import com.rsbi.ext.engine.view.context.form.TextFieldContext;
import com.rsbi.ext.engine.view.context.form.TextFieldContextImpl;
import com.rsbi.ext.engine.view.exception.ExtConfigException;
import com.ruisitech.bi.entity.bireport.*;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.entity.portal.*;
import com.ruisitech.bi.util.RSBIUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 组件基类Service
 * @author hq
 *
 */
public abstract class BaseCompService {

	protected JSONObject pageBody; //页面配置信息

	protected StringBuffer scripts = new StringBuffer(); //用来构造js 脚本的字符串对象

	//输出单位比例
	public String writerUnit(Object bd){
		if(bd == null){
			return "";
		}else{
			int v = 0;
			if(bd instanceof BigDecimal){
				v = ((BigDecimal)bd).intValue();
			}else if(bd instanceof Integer){
				v = ((Integer)bd).intValue();
			}
			if(v == 1){
				return "";
			}else if(v == 100){
				return "百";
			}else if(v == 1000){
				return "千";
			}else if(v == 10000){
				return "万";
			}else if(v == 1000000){
				return "百万";
			}else if(v == 100000000){
				return "亿";
			}else{
				return "*" + v;
			}
		}
	}

	public int type2value(String tp){
		int curDate = 6;
		if(tp.equals("year")) {
			curDate = 6;
		}else if(tp.equals("halfyear")){
			curDate = 5;
		}else if(tp.equals("quarter")){
			curDate = 4;
		}else if(tp.equals("month")){
			curDate = 3;
		}else if(tp.equals("week")){
			curDate = 2;
		}else if(tp.equals("day")){
			curDate = 1;
		}
		return curDate;
	}

	public String createWarning(Map<String, Object> warn, String kpiFmt){
		String funcName = "warn"+ IdCreater.create();
		scripts.append("function " +funcName+"(val, a, b, c, d){");
		//先输出值
		scripts.append("var ret = '';");
		scripts.append("if(val == null){ret += ('-')}else{ret += utils.numberFmt(val, '"+kpiFmt+"');}");
		scripts.append("if(d != 'json'){"); //只在html模式下起作用
		scripts.append(" return ret;");
		scripts.append("}");
		scripts.append("if(val "+warn.get("logic1")+" "+warn.get("val1")+"){");
		scripts.append("ret += (\"<span class='"+warn.get("pic1")+"'></span>\")");
		scripts.append("}else if(val "+(warn.get("logic1").equals(">=")?"<":"<=")+" "+warn.get("val1")+" && val "+warn.get("logic2")+" "+warn.get("val2")+"){");
		scripts.append("ret += (\"<span class='"+warn.get("pic2")+"'></span>\")");
		scripts.append("}else{");
		scripts.append("ret += (\"<span class='"+warn.get("pic3")+"'></span>\")");
		scripts.append("}");
		scripts.append("return ret;");
		scripts.append("}");
		return funcName;
	}

	/**
	 * 处理 now, now-1, now + 1等字符串
	 * @return s
	 */
	protected String parserDefDate(String type, String dtformat, String defVal){
		String regEx = "\\s*now\\s*([+|-]*)\\s*([0-9]*)";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(defVal);
		if(m.find()){
			String m1 = m.group(1).trim();
			String m2 = m.group(2).trim();
			Calendar cal = Calendar.getInstance();
			if(m1.length() > 0 && m2.length() > 0){
				int tp = Calendar.DAY_OF_MONTH;
				if("dateselect".equals(type)){
					tp = Calendar.DAY_OF_MONTH;
				}else if("monthselect".equals(type)){
					tp = Calendar.MONTH;
				}else if("yearselect".equals(type)){
					tp = Calendar.YEAR;
				}
				Integer step = new Integer(m2);
				if("+".equals(m1)){
					cal.add(tp, step);
				}else{
					cal.add(tp, -step);
				}
				return new SimpleDateFormat(dtformat).format(cal.getTime());
			}else if(defVal.trim().equals("now")){
				return new SimpleDateFormat(dtformat).format(cal.getTime());
			}else{
				return defVal;
			}
		}else{
			return defVal;
		}
	}

	public void parserHiddenParam(List<PortalParamDto> params, MVContext mv, Map<String, InputField> mvParams, String useIn) throws ExtConfigException, CloneNotSupportedException {
		if(params != null){
			for(int i=0; i<params.size(); i++){
				PortalParamDto param = params.get(i);
				TextFieldContext target = new TextFieldContextImpl();
				target.setDesc(param.getName());
				target.setOutBox(true);
				String dtformat = null;
				if("dashboard".equals(useIn) || "tobig".equals(useIn) || "dashboardEdit".equals(useIn)){
					target.setId(param.getId());
					dtformat  = param.getFormat();
				} else {
					target.setId(param.getParamid());
					dtformat  = param.getDtformat();
				}
				String defvalue = param.getDefvalue();
				String type = param.getType();
				if(defvalue != null) {
					//在仪表盘中叫onedate, datetime
					if ("dateselect".equals(type) || "monthselect".equals(type) || "yearselect".equals(type) || "onedate".equals(type) || "datetime".equals(type)) {
						String tp = type;
						if ("datetime".equals(type) || "onedate".equals(type)) {  //转换仪表盘的日期类型为报表的日期类型
							if ("yyyy".equals(dtformat)) {
								tp = "yearselect";
							} else if ("yyyy-MM".equals(dtformat) || "yyyyMM".equals(dtformat)) {
								tp = "monthselect";
							} else {
								tp = "dateselect";
							}
						}
						defvalue = this.parserDefDate(tp, dtformat, defvalue);
					}
				}
				if("dashboard".equals(useIn) || "tobig".equals(useIn) || "dashboardEdit".equals(useIn)){
					Map<String, String> val = param.getSelval();
					if(val != null && val.size() > 0){
						target.setValue(val.get("value"));
					}else{
						target.setValue(defvalue);
					}
				}else{
					target.setValue(defvalue);
				}

				//target.setType("hidden");
				if("datetime".equals(type)){  //在仪表盘中的时间区间, 有两个值
					TextFieldContext val1 = target.clone();
					val1.setId(target.getId()+"_start");
					mvParams.put(val1.getId(), val1);
					mv.setMvParam(val1.getId(), val1);
					mv.getChildren().add(val1);
					val1.setParent(mv);

					TextFieldContext val2 = target.clone();
					val2.setId(target.getId()+"_end");
					val2.setDesc("至");
					//重新设置value2的值
					if("dashboard".equals(useIn) || "tobig".equals(useIn) || "dashboardEdit".equals(useIn)){
						Map<String, String> val = param.getSelval2();
						if(val != null && val.size() > 0){
							val2.setValue(val.get("value"));
						}else{
							val2.setValue(defvalue);
						}
					}else{
						val2.setValue(defvalue);
					}

					mvParams.put(val2.getId(), val2);
					mv.setMvParam(val2.getId(), val2);

					mv.getChildren().add(val2);
					val2.setParent(mv);
				}else{
					mvParams.put(target.getId(), target);
					mv.setMvParam(target.getId(), target);

					mv.getChildren().add(target);
					target.setParent(mv);
				}



			}
		}
	}

	public boolean isNumber(String tp) {
		if(tp.equalsIgnoreCase("Double") || tp.equalsIgnoreCase("Int") || tp.equalsIgnoreCase("Number") || tp.equalsIgnoreCase("Long")) {
			return true;
		}else {
			return false;
		}
	}

	public String dealCubeParams(List<CompParamDto> params, String useIn, String nodetype, TableInfoVO tinfo){
		return dealCubeParams(params, useIn, nodetype, tinfo, null, null, null);
	}

	public String dealCubeParams(List<CompParamDto> params, String nodetype, TableInfoVO tinfo){
		return dealCubeParams(params, "report", nodetype, tinfo, null, null, null);
	}

	/**
	 * 处理仪表盘的 参数 ，只用在仪表盘中
	 * @param params
	 * @return
	 */
	public String dealDashboardParams(List<DashboardParamDto> params, TableInfoVO tinfo){
		//判断是否查询es
		boolean qEs = tinfo.isSyncEs();
		StringBuffer sb = new StringBuffer("");
		for(int i=0; params != null && i<params.size(); i++){
			DashboardParamDto dto = params.get(i);
			if(!dto.getTid().equals(tinfo.getTid())){  //不是一个表，查询条件不起作用
				continue;
			}
			if(dto.getValue() == null || dto.getValue().length() == 0 || "null".equalsIgnoreCase(dto.getValue())) {
				continue;
			}
			sb.append(" and ");
			sb.append(qEs?(dto.getColname().equals(dto.getFromCol())?dto.getFromCol():dto.getKey()):(dto.getTableColKey() != null && dto.getTableColKey().length() > 0 ? dto.getTableColKey() : dto.getColname()));
			sb.append(" ");
			sb.append(dto.getOper());
			sb.append(" ");
			if("in".equals(dto.getOper())) {
				String[] vls = dto.getValue().split(",");
				sb.append("(");
				for(int k=0; k<vls.length; k++) {
					if(dto.getValType() == null){
						throw new RuntimeException("字段"+dto.getColname()+"未定义类型。");
					}
					if(!isNumber(dto.getValType())){
						sb.append("'");
					}
					sb.append(vls[k]);
					if(!isNumber(dto.getValType())){
						sb.append("'");
					}
					if(k != vls.length - 1) {
						sb.append(",");
					}
				}
				sb.append(")");
			}else {
				if(!isNumber(dto.getValType())){
					sb.append("'");
				}
				sb.append(dto.getValue());
				if(!isNumber(dto.getValType())){
					sb.append("'");
				}
			}
			if("between".equals(dto.getOper())) {
				sb.append(" and ");
				if(!isNumber(dto.getValType())){
					sb.append("'");
				}
				sb.append(dto.getValue2());
				if(!isNumber(dto.getValType())){
					sb.append("'");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * nodetype 表示筛选的类型，分为维度筛选和指标筛选两类，维度筛选和指标筛选对应的SQL位置不一样，维度放where 后， 指标放 having 后
	 * @param params
	 * @param useIn 在哪里使用 report/dashboard
	 * @param nodetype dim/kpi
	 * @param jsType null/sq/tq/lj 等值
	 * @param minDate 最小时间维度
	 * @return
	 */
	public String dealCubeParams(List<CompParamDto> params, String useIn, String nodetype, TableInfoVO tinfo, String jsType, DateCompareDto compareDto, DateDimDto minDate){
		//判断是否查询es
		boolean qEs = tinfo.isSyncEs();
		StringBuffer sb = new StringBuffer("");
		//如果是计算tq/sq/lj,时间维度只取最后一个，其他类型维度不变
		if(jsType != null) {
			for(int i=0; params != null && i<params.size(); i++) {
				CompParamDto param = params.get(i);
				boolean region = false;
				String type = param.getDimType();
				if(param.isDateDim() && minDate != null && minDate.getAlias().equals(param.getAlias())) {
					String start = minDate.getAlias()+"_start";
					String end = minDate.getAlias()+"_end";
					String col = param.getCol();
					if(param.getTableAlias() != null && param.getTableAlias().length() > 0){
						col = param.getTableAlias() + "." + col;
					}
					String express = param.getExpression();
					String v = null;  //当js == zdz 时，需要指定获取的值
					if("zdz".equals(jsType)){
						if("gdz".equals(compareDto.getValtype())){
							v = "'" + compareDto.getVal() + "'";
						}else{
							v =  "$" + compareDto.getParam();
						}
						String paramType = compareDto.getParamType();
						region = "datetime".equalsIgnoreCase(paramType);
					}
					sb.append(" #if($"+start+" != '' and $"+end+" != '') \n");
					sb.append(" and " + (express==null||express.length() == 0?col:express) + " between '$myUtils.shiftDate($"+start+", '"+type+"', '"+minDate.getDateformat()+"', '"+jsType+"',"+v + (region?"_start":"")  +" , 1)'");
					sb.append(" and '$myUtils.shiftDate($"+end+", '"+type+"', '"+minDate.getDateformat()+"', '"+jsType+"',"+v + (region?"_end":"")+", 2)' ");
					sb.append(" #end \n");

				}
				if(type == null || "frd".equals(type)){
					this.dealCubeParam(sb, useIn, nodetype, qEs, param);
				}
			}
		}else {
			for(int i=0; params != null && i<params.size(); i++){
				CompParamDto param = params.get(i);
				this.dealCubeParam(sb, useIn, nodetype, qEs, param);
			}
		}
		return sb.toString().replaceAll("\\[x\\]", "\\$");
	}

	private void dealCubeParam(StringBuffer sb, String useIn, String nodetype, boolean qEs, CompParamDto param) {
		String col = param.getCol();
		if(param.getExpression() != null && param.getExpression().length() > 0){ //表达式替换字段
			col = param.getExpression();
		}else{
			//是否拼接表别名（在数据集中）
			if(param.getTableAlias() != null && param.getTableAlias().length() > 0){
				col = param.getTableAlias()+"." + col;
			}
		}
		String type = param.getType();
		String val = param.getVal();
		String val2 = param.getVal2();
		String valuetype = param.getValuetype();
		String usetype = param.getUsetype();
		String usetype2 = param.getUsetype2();
		String linkparam = param.getLinkparam();
		String linkparam2 = param.getLinkparam2();
		String code = param.getCode();
		String ntype = param.getNodetype();
		if(ntype == null){
			throw new RuntimeException("参数未定义nodeType。");
		}
		if(!ntype.equals(nodetype)){
			return;
		}
		if(usetype2 == null){  //兼容以前代码
			usetype2 = usetype;
		}
		//为了让value支持动态值，需要解析成JS函数
		//如果有(表示是表达式
		if(usetype.equals("gdz") && val != null && val.indexOf("(") >= 0) {
			String varName =  "dyn" + IdCreater.create();
			String script =  "var v"+ varName + " = " + val + "; \n";
			script += "if(verifySql(v"+varName+")){ ";
			script += " extContext.put('"+varName+"', 'SQL注入'); \n";
			script += " }else{ \n";
			script += " extContext.put('"+varName+"', v"+varName+"); \n";
			script += "}";
			scripts.append(script);
			val = "$"+varName;
		}
		//处理 value2
		if(usetype2.equals("gdz") && val2 != null && val2.indexOf("(") >= 0) {
			String varName =  "dyn" + IdCreater.create();
			String script =  "var v"+ varName + " = " + val2 + "; \n";
			script += "if(verifySql(v"+varName+")){";
			script += " extContext.put('"+varName+"', 'SQL注入'); \n";
			script += "}else{ \n";
			script += " extContext.put('"+varName+"', v"+varName+"); \n";
			script += "}";
			scripts.append(script);
			val2 = "$"+varName;
		}
		String fName = null;
		if(code != null && code.length() > 0){
			fName = "f_"+ IdCreater.create();
			String script = "function "+fName+"(value, vpos){"+ RSBIUtils.unescape(code)+"} \n";
			scripts.append(script);
		}
		//链接到参数的回调函数
		if(usetype.equals("param") && val != null && code != null && code.length() > 0){
			val = "$extUtils.callParamFunc("+(!isNumber(valuetype)?"'"+val+"'":val)+", 'val1', '"+fName+"', $request)";
		}
		if(usetype.equals("param") && val2 != null && code != null && code.length() > 0){
			val2 = "$extUtils.callParamFunc("+(!isNumber(valuetype)?"'"+val2+"'":val2)+", 'val2', '"+fName+"', $request)";
		}
		if(type.equals("like")){
			if(val != null){
				val = "%"+val+"%";
			}
			if(val2 != null){
				val2 = "%"+val2+"%";
			}
		}
		if(!isNumber(valuetype)){
			if(val != null && val.length() > 0){
				if("in".equals(type)){  //in需要把数据用逗号分隔的重新生成
					String[] vls = val.split(",");
					val = "";
					for(int j=0; j<vls.length; j++){
						val = val + "'" + vls[j] + "'";
						if(j != vls.length - 1){
							val = val + ",";
						}
					}
				}else{
					val = "'" + val + "'";
				}
			}
			if(val2 != null && val2.length() > 0){
				val2 = "'" + val2 + "'";
			}
		}
		if(type.equals("between")){
			if(usetype.equals("gdz")){
				sb.append(" and " +  col + " " + type + " " + val + " and " + val2);
			}else{
				if(useIn == null || "report".equals(useIn)){
					sb.append(" #if([x]" + linkparam + " && [x]" + linkparam + " != '' && [x]" + linkparam2 + " && [x]" + linkparam2 + " != '') ");
					sb.append(" and " + col + " " + type + " " + (!isNumber(valuetype) ? "'" : "") + "[x]" + linkparam + (!isNumber(valuetype) ? "'" : "") + " and " + (!isNumber(valuetype) ? "'" : "") + "[x]" + linkparam2 + (!isNumber(valuetype) ? "'" : "") + " #end");
				}else {
					if(val != null && val2 != null) {
						sb.append(" and " + col + " " + type + " " + val + " and " + val2);
					}
				}
			}
		}else if(type.equals("in")){
			if(usetype.equals("gdz")){
				sb.append(" and " + col + " in (" + val + ")");
			}else{
				if(useIn == null || "report".equals(useIn)){
					sb.append(" #if([x]"+linkparam+" && [x]"+linkparam+" != '') ");
					sb.append(" and " + col + " in (" + "$extUtils.printVals([x]"+linkparam + ", '"+valuetype+"'))");
					sb.append("  #end");
				}else{
					if(val != null && val.length() > 0) {
						sb.append(" and " + col + " in (" + val + ")");
					}
				}
			}
		}else{
			if(usetype.equals("gdz")){
				sb.append(" and " + col + " " + type + " " + val);
			}else{
				if(useIn == null || "report".equals(useIn)){  //在 dashboard 和 report 中取值不一样
					sb.append(" #if([x]"+linkparam+" && [x]"+linkparam+" != '') ");
					sb.append(" and " + col + " "+type+" " + (!isNumber(valuetype) ? "'"+("like".equals(type)?"%":"")+""+"[x]"+linkparam+""+("like".equals(type)?"%":"")+"'":"[x]"+linkparam) + "");
					sb.append("  #end");
				}else{
					if(val != null && val.length() > 0) {
						sb.append("	and " + col + " " + type + " " + val);
					}
				}
			}
		}
	}

	/**
	 * 根据 tname 获取  表的 alias
	 * 用在数据查询中
	 * @param map
	 * @param tname
	 * @return
	 */
	public String getTableAliasByTname(Map<TableInfoVO, String> map, String tname){
		String ret = null;
		for(Map.Entry<TableInfoVO, String> ent : map.entrySet()){
			if(ent.getKey().getTname().equals(tname)){
				ret = ent.getValue();
				break;
			}
		}
		return ret;
	}

	/**
	 * 组件联动时，获取 paranName
	 * @param compId
	 * @return
	 */
	public String findEventParamName(String compId){
		if(pageBody == null){
			//throw new RuntimeException("pageBody 未初始化...");
			return null; //在设计模式下， pageBody为 null
		}
		String paramName = null;
		for(int i=1; true; i++){
			Object tmp = pageBody.get("tr" + i);
			if(tmp == null){
				break;
			}
			JSONArray trs = (JSONArray)tmp;
			for(int j=0; j<trs.size(); j++){
				JSONObject td = trs.getJSONObject(j);
				Object cldTmp = td.get("children");
				if(cldTmp != null){
					JSONArray children = (JSONArray)cldTmp;
					for(int k=0; k<children.size(); k++){
						JSONObject comp = children.getJSONObject(k);
						String type = comp.getString("type");
						if("chart".equals(type)){
							JSONObject compData = comp.getJSONObject("comp");
							PortalChartQuery chart = JSONObject.toJavaObject(compData, PortalChartQuery.class);
							Map<String, Object> link = chart.getChartJson().getLink();
							if(link != null){
								String target = (String)link.get("target");
								if(target != null && target.indexOf(compId) >= 0){
									paramName = (String)link.get("paramName");
									break;
								}
							}
						}else if("table".equals(type)){
							JSONObject compData = comp.getJSONObject("comp");
							PortalTableQuery table = JSONObject.toJavaObject(compData, PortalTableQuery.class);
							Map<String, Object> link =  table.getLink();
							if(link != null){
								String target = (String)link.get("target");
								if(target != null && target.indexOf(compId) >= 0){
									paramName = (String)link.get("paramName");
									break;
								}
							}
						}
					}
				}
			}
		}
		return paramName;
	}

	public void setPageBody(JSONObject pageBody) {
		this.pageBody = pageBody;
	}

	/**
	 * 根据 dataId 创建一个空的 dataCenter
	 * @param dataId
	 * @return
	 */
	public GridDataCenterContext createEmptyDataCenter(String dataId) {
		GridDataCenterContext ctx = new GridDataCenterContextImpl();
		GridSetConfContext conf = new GridSetConfContext();
		conf.setDataId(dataId);
		ctx.setConf(conf);
		ctx.setId("DC-" + IdCreater.create());
		return ctx;
	}

	/**
	 * 创建共有JS函数
	 * @return
	 */
	public String createBaseJSFunction() {
		StringBuffer sb = new StringBuffer();
		sb.append(" function date_format(dt, fmt){  \n");
		sb.append(" 	var sdf = new java.text.SimpleDateFormat(fmt);	\n");
		sb.append("		return sdf.format(dt); \n");
		sb.append(" } \n");
		sb.append("	function cur_date(){	\n");
		sb.append("		return new java.util.Date(); \n");
		sb.append("	} \n");
		sb.append("	function date_add(dt, step, tp){ \n");
		sb.append("		if(tp == 'day') { \n");
		sb.append("			dt.setDate(dt.getDate() + step); \n");
		sb.append("		} \n");
		sb.append("		if(tp == 'month') { \n");
		sb.append("			dt.setMonth(dt.getMonth() + step); \n");
		sb.append("		} \n");
		sb.append("		if(tp == 'year') { \n");
		sb.append("			dt.setYear(dt.getYear() + step); \n");
		sb.append("		} \n");
		sb.append("		return dt; \n");
		sb.append("	} \n");
		//检查下有没有SQL注入关键字
		sb.append("	function verifySql(val){ \n");
		sb.append("		var vls = [\"union\",\"and\",\"exec\",\"insert\",\"select\",\"delete\",\"update\",\";\",\"load_file\",\"'\",\"or\",\"(\",\")\"]; \n");
		sb.append(" 	var ret = false; \n");
		sb.append(" 	for(var j=0;j<vls.length;j++){ \n");
		sb.append("			if(val.indexOf(vls[j]) >= 0 ){ ret = true; break; }");
		sb.append("		} \n");
		sb.append("		return ret; \n");
		sb.append("	} \n");
		return sb.toString();
	}
	/**
	 * Rest查询得SQL和数据库查询SQL不一致，Rest查询Sql只保存表ID，参数内容
	 * @param tinfo
	 * @param linkAccept 事件接收字段
	 * @return
	 */
	public String createRestSql(List<CompParamDto> params, LinkAcceptDto linkAccept, TableInfoVO tinfo) {
		StringBuffer sb = new StringBuffer("");
		sb.append(tinfo.getTid());
		sb.append("?");
		if(params != null && params.size() > 0) {
			params.forEach(p ->{
				String col = p.getCol();
				String type = p.getType();
				String val = p.getVal();
				String val2 = p.getVal2();
				String usetype = p.getUsetype();
				String linkparam = p.getLinkparam();
				if(linkAccept != null && linkAccept.getCol().equals(col)) {
					return;
				}

				//为了让value支持动态值，需要解析成JS函数
				//如果有(表示是表达式
				if(usetype.equals("gdz") && val != null && val.indexOf("(") >= 0) {
					String varName =  "dyn" + IdCreater.create();
					String script =  "var v"+ varName + " = " + new String(val) + "; \n";
					script += " extContext.put('"+varName+"', v"+varName+"); \n";
					scripts.append(script);
					val = "$"+varName;
				}
				//处理 value2
				if(usetype.equals("gdz") && val2 != null && val2.indexOf("(") >= 0) {
					String varName =  "dyn" + IdCreater.create();
					String script =  "var v"+ varName + " = " + new String(val2) + "; \n";
					script += " extContext.put('"+varName+"', v"+varName+"); \n";
					scripts.append(script);
					val2 = "$"+varName;
				}
				if(usetype.equals("gdz")){
					sb.append(col + type + val);
				}else{
					if(val != null && val.length() > 0 ) {  //在仪表盘页面，参数通过dashboardValue传递
						sb.append(col +type+ val);
					}else {
						sb.append("#if(${"+linkparam+"} && ${"+linkparam+"} != '')");
						sb.append(col +type+ "${"+linkparam+"}");
						sb.append("#end");
					}
				}
				sb.append("&");
			});
		}
		String s = Optional.ofNullable(linkAccept).filter(l -> l.getCol() != null && l.getCol().length() > 0).map(l -> {
			String a = findEventParamName(linkAccept.getCompId());  //如果 a == null, 表示在仪表盘中，a ！= null， 表示在 报表中
			if(a == null) {
				return l.getCol()+"="+(l.getDftval()==null?"":l.getDftval())+"&";
			}else {
				return "#if(${"+a+"} && ${"+a+"} != '')"+l.getCol()+"=${"+a+"}#end&";
			}
		}).orElse("");
		sb.append(s);
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 仪表盘事件接收
	 * @param linkAccept
	 * @param sb
	 */
	public void dealLinkAccept(LinkAcceptDto linkAccept, StringBuffer sb){
		if(linkAccept != null && linkAccept.getId() != null){
			String col = linkAccept.getCol();
			String express = linkAccept.getExpression();
			if(express != null && express.length() > 0){
				col = express;
			}
			String valtype =linkAccept.getValType();
			//Integer calc = linkAccept.getCalc();
			//String tableColKey =linkAccept.getTableColKey();
			//如果有 tableColKey，用  tableColKey 替换 col (checkSql 里面已经替换了)
			//if(tableColKey != null && tableColKey.length() > 0){
			//	col = tableColKey;
			//}
			String val = linkAccept.getDftval();
			if(val == null || val.length() == 0){
				return;
			}
			if("string".equalsIgnoreCase(valtype) || "Date".equalsIgnoreCase(valtype) || "Datetime".equalsIgnoreCase(valtype)){
				val = "'" + val + "'";
			}
			sb.append(" and " + col + " = " + val);
		}
	}

	public String createDsource(DataSource ds, MVContext mv){
		if(ds == null){
			return null;
		}
		DataSourceContext dsource = new DataSourceContext();
		dsource.putProperty("id","ds-"+ds.getId());
		String use = dsource.getUse();
		dsource.putProperty("usetype", "jdbc");
		String linktype = ds.getLinkType();
		dsource.putProperty("linktype", linktype);
		dsource.putProperty("linkname", ds.getUname());
		dsource.putProperty("linkpwd", ds.getPsd());
		dsource.putProperty("linkurl", ds.getLinkUrl());

		//放入MV
		if(mv.getDsources() == null){
			mv.setDsources(new HashMap<String, DataSourceContext>());
		}
		mv.getDsources().put(dsource.getId(), dsource);
		return dsource.getId();
	}

	public static String createEsOrderBy(String col, String colType){
		return " sort_func("+col+", '"+(colType.equals("Datetime")||colType.equals("Date")?"string":(colType.equalsIgnoreCase("String")?"string":"number"))+"') ";
	}

	public void countDistinct(VerticalKpiDto k, StringBuffer sql, TableInfoVO tinfo){
		String aggre = k.getAggre();
		if("count(distinct)".equals(aggre)){
			sql.append("count(distinct ");
			if(k.getDistinctCol() != null && k.getDistinctCol().length() > 0){
				sql.append(k.getDistinctCol());
			}else {
				sql.append(tinfo.getKpiValueColumn());
			}
			sql.append(")");
		}else{
			sql.append(aggre);
			sql.append("(" + tinfo.getKpiValueColumn() + ")");
		}
		sql.append(" as ");
		sql.append(tinfo.getKpiValueColumn());
	}

	public StringBuffer getScripts() {
		return scripts;
	}

}
