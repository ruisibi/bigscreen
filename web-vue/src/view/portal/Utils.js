/**
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
import $ from 'jquery'
import { Message, Loading } from 'element-ui'
import {baseUrl, ajax} from '@/common/biConfig'
import * as echartsUtils from '@/common/echartsUtils'

//默认5种布局
export const layout = {
    l1 : {tr1:[{colspan:1, rowspan:1, width:100, height:100, id:1}]},
    l2 : {tr1:[{colspan:1, rowspan:1, width:50, height:100, id:1},{colspan:1, rowspan:1, width:50, height:100, id:2}]},
    l3: {tr1:[{colspan:2, rowspan:1, width:100, height:50, id:1}],tr2:[{colspan:1, rowspan:1, width:50, height:50, id:2},{colspan:1, rowspan:1, width:50, height:50, id:3}]},
    l4: {tr1:[{colspan:2, rowspan:1, width:100, height:33, id:1}],tr2:[{colspan:1, rowspan:1, width:50, height:33, id:2},{colspan:1, rowspan:1, width:50, height:33, id:3}], tr3:[{colspan:2, rowspan:1, width:100, height:33, id:4}]},
    l5:{tr1:[{colspan:2, rowspan:1, width:100, height:20, id:1}],tr2:[{colspan:1, rowspan:1, width:50, height:20, id:2},{colspan:1, rowspan:1, width:50, height:20, id:3}], tr3:[{colspan:2, rowspan:1, width:100, height:20, id:4}],tr4:[{colspan:1, rowspan:1, width:50, height:20, id:5},{colspan:1, rowspan:1, width:50, height:20, id:6}],tr5:[{colspan:2, rowspan:1, width:100, height:20, id:7}]}
}

export const fmtJson = [{
	"text":"整数",
	"value":"#,###"
},{
	"text":"小数(保留一位)",
	"value":"#,##0.0"
},{
	"text":"小数(保留两位)",
	"value":"#,##0.00"
},{
	"text":"小数(保留四位)",
	"value":"#,##0.0000"
},{
	"text":"百分比",
	"value":"#,##0%"
},{
	"text": "百分比(保留一位小数)",
	"value": "#,##0.0%"
}, {
	"text": "百分比(保留两位小数)",
	"value": "#,##0.00%"
}, {
	"text": "百分比(保留四位小数)",
	"value": "#,##0.0000%"
}];

export const rates = [{
	"text":"1",
	"value":"1"
},{
	"text":"千",
	"value":"1000"
},{
	"text":"万",
	"value":"10000"
},{
	"text":"百万",
	"value":"1000000"
},{
	"text":"亿",
	"value":"100000000"
}];

export const findRateName = (v)=>{
	var r = null;
	if(v == 1){
		return r;
	}
	$(rates).each((a, b)=>{
		if(b.value == v){
			r = b.text;
			return false;
		}
	});
	return r;
}

export const pielabels = [{"text":"名称","value":"n"},{"text":"名称：值","value":"nv"},{"text":"名称：比例","value":"np"},
	{"text": "名称：值+比例","value": "nvp"}, {"text":"名称<换行>值","value":"nbrv"},{"text":"名称<换行>比例","value":"nbrp"}, {"text":"名称<换行>值+比例", "value":"nbrvp"}];

export const legendpos = [{text: '右上', value: 'righttop'},{value:"rightbottom", text:"右下"},{text: '中上', value: 'centertop'},{text: '中下', value: 'centerbottom'}, {text:"左上", value:"lefttop"}, {text:"左下", value:"leftbottom"}];

export const borderStyles = [{name:"实线", value:"solid"},{name:"虚线", value:"dashed"},{name:"点划线", value:"dotted"}];

export const position = [{"text":"top","value":"top"},{"text":"left","value":"left"},{"text":"right","value":"right"}, {"text":"bottom", "value":"bottom"},{"text":"outside", "value":"outside"}, {"text":"inside", "value":"inside"}, {"text":"insideTop", "value":"insideTop"},{"text":"insideBottom", "value":"insideBottom"},{"text":"insideLeft", "value":"insideLeft"},{"text":"insideRight", "value":"insideRight"},{"text":"insideTopLeft", "value":"insideTopLeft"},{"text":"insideTopRight", "value":"insideTopRight"}]

export const msginfo = (msg, type)=>{
	if(!type || type === 'error'){
		Message.error({message:msg, type:"error",showClose: true});
	}else{
		Message.success({message:msg, type:"success",showClose: true});
	}
}

export const findParamById = (pageInfo, id, retIndex) => {
	var ret = null;
	for(let i=0; pageInfo.params&&i<pageInfo.params.length; i++){
		var p = pageInfo.params[i];
		if(p.id == id){
			if(retIndex){
				ret = i;
			}else{
				ret = p;
			}
		}
	}
	return ret;
}

export const getParamTypeDesc = (paramType)=>{
	var tpname = "";
	if(paramType == "text"){
	  tpname = "输入框";
	}else if(paramType == "radio"){
	  tpname = "单选框";
	}else if(paramType == "checkbox"){
	  tpname = "多选框";
	}else if(paramType == "dateselect"){
	  tpname = "日历框";
	}else if(paramType == "monthselect"){
	  tpname = "月份框";
	}else if(paramType == "yearselect"){
	  tpname = "年份框";
	}else if(paramType === 'tree'){
		tpname = "树形框";
	}
	return tpname;
}

export const getCompTypeDesc = (compType)=>{
	var name = "";
	if(compType === 'text'){
		name = "文本";
	}else if(compType === 'box'){
		name = "数据块";
	}else if(compType === 'mbox'){
		name = "多指标数据块";
	}else if(compType === 'chart'){
		name = "图表";
	}else if(compType === 'grid'){
		name = "表格";
	}else if(compType === 'table'){
		name = "交叉表";
	}else if(compType === 'pic'){
		name = "图片";
	}else if(compType === 'detail'){
		name = "详情页";
	}else if(compType === 'decorate'){
		name = "装饰框";
	}else if(compType === 'iframe'){
		name = "嵌套框";
	}else if(compType == 'date'){
		name = "日期";
	}else if(compType == 'weather'){
		name = "天气";
	}else if(compType == 'tab'){
		name = "选项卡";
	}else if(compType == 'line'){
		name = '装饰线';
	}else if(compType == 'param'){
		name = '参数';
	}else if(compType == 'dupont'){
		name = "杜邦分析";
	}
	return name;
}
/**
* 对立方体 row/col 进行递归调用
*/
export const loopDims = (dims, cb) => {
 if(dims == null || dims.length ===0){
   return;
 }
 $(dims).each((a,b)=>{
   let ret = cb(b, dims, a);
   if(ret === true){
	 return true;
   }else if(ret === false){
	 return false;
   }
   if(b.children && b.children.length > 0){
	 loopDims(b.children, cb);
   }
 });
}

//从布局器中查询td(容器)
export const findLayoutById = (layoutId, pageInfo)=>{
	var ret = null;
	for(var i=1; true; i++){
		var tr = pageInfo.body["tr"+i];
		if(!tr || tr == null){
			break;
		}
		for(var j=0; j<tr.length; j++){
			var td = tr[j];
			if(td.id == layoutId){
				ret = td;
				break;
			}
		}
	}
	return ret;
}
//查询布局器中所以组件
export const findAllComps = (pageInfo)=>{
	let ret = [];
	if(!pageInfo.body){
		return ret;
	}
	for(var i=1; true; i++){
		var tr = pageInfo.body["tr"+i];
		if(!tr || tr == null){
		  break;
		}
		for(var j=0; j<tr.length; j++){
		  var td = tr[j];
		  if(td.children){
			td.children.forEach(element => {
			  ret.push(element);
			});
		  }
		}
	}
	return ret;
}
/**
//从布局器中查询组件
 * @param {*} pageInfo
 * @param {*} compId
 * @param {*} remove  是否移除查找到的对象
 */
export const findCompById = (pageInfo, compId, remove)=>{
	let ret = null;
	for(var i=1; true; i++){
		var tr = pageInfo.body["tr"+i];
		if(!tr || tr == null){
		  break;
		}
		for(var j=0; j<tr.length; j++){
		  var td = tr[j];
		  if(td.children){
			  $(td.children).each((a, b)=>{
				if(b.id === compId){
					ret = b;
					if(remove){
						td.children.splice(a, 1);
					}
					return false;
				}
			  });
		  }
		}
	}
	return ret;
}
/**
 * 报表链接
 * @param {*} comp
 */
export const compLink = (comp, ts, paramName, value)=>{
	if(ts.$parent.$parent.drawer == true){  //报表链接窗口已经打开
		ts.$parent.$parent.showLink(comp, {});
		return;
	}
	let dt = ts.$parent.$parent.$refs['paramViewForm'].getParamValues(); // 获取参数
	if(Array.isArray(paramName)){
		$(paramName).each((a, b)=>{
			dt[b] = value[a];
		});
	}else{
		dt[paramName] = value;
	}
	ts.$parent.$parent.$refs['compLinkForm'].showLink(comp, dt);
}
/**
 * 组件事件调用，目前只支持图形，交叉表
 * @param {*} link
 */
export const compFireEvent = (link, ts, paramName, value)=>{
	let ret = true;
	let target = link.target;
	let types = link.type.split(",");
	$(target.split(",")).each((a, b)=>{
		if($('#report'+b).length === 0){
			Message.error("找不到联动事件的接收组件.");
			ret = false;
			return false;
		}
		let loadingInstance = Loading.service({fullscreen:false, target:document.querySelector('#report'+b)});
		let dt = ts.$parent.$parent.$refs['paramViewForm'].getParamValues(); // 获取参数
		let tp = types[a];
		if(tp === 'chart'){
		  dt['serviceid'] = "ext.sys.chart.rebuild";
		}else if(tp ==='cross'){
		  dt['serviceid'] = "ext.sys.cross.rebuild";
		}
		dt['t_from_id'] = "mv_" + ts.$parent.pageInfo.id;
		dt['id'] = b;
		dt[paramName] = value;
		ajax({
		  url:"control/extControl",
		  type:"POST",
		  data:dt,
		  success:(resp)=>{
			loadingInstance.close();
			if(!resp.rows){
				Message.error("未查询出数据.");
				return;
			}
			if(tp === 'chart'){
			  //更新图形
			  let c = ts.$parent.$refs['mv_'+b];
			  c.data = resp.rows;
			  c.$nextTick(()=>c.showChart());
			}else if(tp === 'cross'){
				//更新交叉表
				let c = ts.$parent.$refs['mv_'+b];
				c.data = resp.rows;
			}
		  }
		}, ts, loadingInstance);
	});
	return ret;
}

/**
 * 点击返回按钮触发事件返回
 * @param {*} link
 * @param {*} ts
 */
export const compBackEvent = (link, ts)=>{
	let target = link.target;
	let types = link.type.split(",");
	$(target.split(",")).each((a, b)=>{
		let loadingInstance = Loading.service({fullscreen:false, target:document.querySelector('#report'+b)});
		let dt = ts.$parent.$parent.$refs['paramViewForm'].getParamValues(); // 获取参数
		let tp = types[a];
		if(tp === 'chart'){
		  dt['serviceid'] = "ext.sys.chart.rebuild";
		}else if(tp ==='cross'){
		  dt['serviceid'] = "ext.sys.cross.rebuild";
		}
		dt['t_from_id'] = "mv_" + ts.$parent.pageInfo.id;
		dt['id'] = b;
		ajax({
		  url:"control/extControl",
		  type:"POST",
		  data:dt,
		  success:(resp)=>{
			loadingInstance.close();
			if(tp === 'chart'){
			  //更新图形
			  let c = ts.$parent.$refs['mv_'+b];
			  c.data = resp.rows;
			  c.$nextTick(()=>c.showChart());
			}else if(tp === 'cross'){
				//更新交叉表
				let c = ts.$parent.$refs['mv_'+b];
				c.data = resp.rows;
			}
		  }
		}, ts, loadingInstance);
	});
}
/**
 *
 * @param {报表导出} tp
 * @param {*} reportId
 * @param {*} paramViewForm
 * @param {*} pageInfo
 */
export const exportReport = (tp, reportId, paramViewForm, pageInfo)=>{
	let pageId = reportId;
	let burl = baseUrl;
	var ctx = `
	<form name='expff' method='post' action="${burl}portal/export.action" id='expff'>
	<input type='hidden' name='type' id='type' value='${tp}'>
	<input type='hidden' name='pageId' id='pageId' value='${pageId}'>
	<input type='hidden' name='picinfo' id='picinfo'>
	`;
	let pms = paramViewForm.getParamValues();
	$(pageInfo.params).each((a, b)=>{
	  let v = pms[b.id];
	  if(!v){
		v = "";
	  }
	  ctx += `<input type='hidden' name='${b.id}' value="${v}">`;
	});
	ctx += `</form>`;
	if($("#expff").length == 0 ){
	  $(ctx).appendTo("body");
	}
	//把图形转换成图片
	var strs = "";
	if(tp == "pdf" || tp == "excel" || tp == "word"){
	  let comps = findAllComps(pageInfo).filter(m=>m.type ==='chart'||(m.type=='box' && m.comp && m.comp.chart));
	  $(comps).each(function(index, element) {
		var id = element.type ==='chart' ? element.id : element.comp.chart.id;
		var chartId = element.type ==='chart'?("ct_"+id):("report" + id);
		var chart = echarts.getInstanceByDom(document.getElementById(chartId));
		var str = chart.getDataURL({type:'png', pixelRatio:1, backgroundColor: '#fff'});
		str = str.split(",")[1]; //去除base64标记
		str = id + "," + str+","+$("#"+chartId).width(); //加上label标记,由于宽度是100%,需要加上宽度
		strs = strs  +  str;
		if(index != comps.length - 1){
		  strs = strs + "@";
		}
	  });
	}
	$("#expff #picinfo").val(strs);
	$("#expff").submit().remove();
  }
//清除组件数据
export const delCompData = (comp) => {
	let tp = comp.type;
	comp = comp.comp;
	var h = tp == 'chart'?comp.chartJson.height:comp.height;
	delete comp.tid;
	delete comp.tname;
	if(tp == "chart"){
		comp.chartJson = {"type":"line", height:h};
		comp.kpiJson = [null, null, null];
		comp.mkpi = false;
		delete comp.mkpiJson;
		delete comp.portalParams;
		delete comp.compParams;
		delete comp.chartJson.link;
		delete comp.chartJson.linkAccept;
	}else if(tp == "mbox"){
		comp.kpiJson = [{
            "kpi_id":1,
            "ydispName":"经营指标1",
            "img":"icon-01.png"
        },
        {
            "kpi_id":2,
            "ydispName":"经营指标2",
            "img":"icon-02.png"
        }];
		delete comp.portalParams;
		delete comp.compParams;
		delete comp.link;
		delete comp.linkAccept;
	}else if(tp == "grid"){
		comp.cols = [];
		delete comp.portalParams;
		delete comp.compParams;
		delete comp.link;
		delete comp.linkAccept;
	}else if(tp == "table"){
		comp.cols = [];
		comp.rows = [];
		comp.kpiJson = [];
		delete comp.portalParams;
		delete comp.compParams;
		delete comp.link;
		delete comp.linkAccept;
	}else if(tp === 'detail'){
		delete comp.cols;
	}
	return true;
}
//创建表格 head 样式
export const crtTableHeadStyle = (tstyle)=>{
	let hds = {};
	if(!tstyle){
		return hds;
	}
	if(tstyle.headFontSize){
	  hds['font-size'] = tstyle.headFontSize+"px";
	}
	if(tstyle.headTextBold==false){
	  hds['font-weight'] = "normal";
	}
	if(tstyle.family){
		hds['font-family'] = "'"+tstyle.family + "'";
	 }
	if(tstyle.headTextColor){
	  hds['color'] = tstyle.headTextColor;
	}
	if(tstyle.headTextAlign){
	  hds['text-align'] = tstyle.headTextAlign;
	}
	if(tstyle.headcolheight){
	  hds['height'] = tstyle.headcolheight+"px";
	}
	if(tstyle.headTopWidth >= 0){
		hds['border-top-width'] = tstyle.headTopWidth+"px";
	}
	if(tstyle.headTopType){
		hds['border-top-style'] = tstyle.headTopType;
	}
	if(tstyle.headTopColor){
		hds['border-top-color'] = tstyle.headTopColor;
	}
	if(tstyle.headRightWidth >= 0){
		hds['border-right-width'] = tstyle.headRightWidth+"px";
	}
	if(tstyle.headRightType){
		hds['border-right-style'] = tstyle.headRightType;
	}
	if(tstyle.headRightColor){
		hds['border-right-color'] = tstyle.headRightColor;
	}
	if(tstyle.headBottomWidth >= 0){
		hds['border-bottom-width'] = tstyle.headBottomWidth+"px";
	}
	if(tstyle.headBottomType){
		hds['border-bottom-style'] = tstyle.headBottomType;
	}
	if(tstyle.headBottomColor){
		hds['border-bottom-color'] = tstyle.headBottomColor;
	}
	if(tstyle.headLeftWidth >= 0){
		hds['border-left-width'] = tstyle.headLeftWidth+"px";
	}
	if(tstyle.headLeftType){
		hds['border-left-style'] = tstyle.headLeftType;
	}
	if(tstyle.headLeftColor){
		hds['border-left-color'] = tstyle.headLeftColor;
	}
	return hds;
}
//创建表格 body 样式
export const crtTableBodyStyle = (tstyle)=>{
	let hds = {};
	if(!tstyle){
		return hds;
	}

	if(tstyle.bodyTextColor){
	  hds['color'] = tstyle.bodyTextColor;
	}
	if(tstyle.bodyFontSize){
		hds['font-size'] = tstyle.bodyFontSize+"px";
	}
	if(tstyle.bodyfamily){
		hds['font-family'] = "'"+tstyle.bodyfamily + "'";
	 }
	if(tstyle.bodycolheight){
		hds['height'] = tstyle.bodycolheight+"px";
	}
	if(tstyle.bodyTextAlign){
		hds['text-align'] = tstyle.bodyTextAlign;
	}

	if(tstyle.bodyTopWidth >= 0){
		hds['border-top-width'] = tstyle.bodyTopWidth+"px";
	}
	if(tstyle.bodyTopType){
		hds['border-top-style'] = tstyle.bodyTopType;
	}
	if(tstyle.bodyTopColor){
		hds['border-top-color'] = tstyle.bodyTopColor;
	}
	if(tstyle.bodyRightWidth >= 0){
		hds['border-right-width'] = tstyle.bodyRightWidth+"px";
	}
	if(tstyle.bodyRightType){
		hds['border-right-style'] = tstyle.bodyRightType;
	}
	if(tstyle.bodyRightColor){
		hds['border-right-color'] = tstyle.bodyRightColor;
	}
	if(tstyle.bodyBottomWidth >= 0){
		hds['border-bottom-width'] = tstyle.bodyBottomWidth+"px";
	}
	if(tstyle.bodyBottomType){
		hds['border-bottom-style'] = tstyle.bodyBottomType;
	}
	if(tstyle.bodyBottomColor){
		hds['border-bottom-color'] = tstyle.bodyBottomColor;
	}
	if(tstyle.bodyLeftWidth >= 0){
		hds['border-left-width'] = tstyle.bodyLeftWidth+"px";
	}
	if(tstyle.bodyLeftType){
		hds['border-left-style'] = tstyle.bodyLeftType;
	}
	if(tstyle.bodyLeftColor){
		hds['border-left-color'] = tstyle.bodyLeftColor;
	}

	return hds;
}

//表格宽度自适应
export const autowidth = (comp, data, useIn)=>{
	let colwidth = comp.comp.colwidth;
	let borderwidth =comp && comp.tstyle ?  (comp.tstyle.headLeftWidth || 0) + (comp.tstyle.headRightWidth || 0) : 0 ;

	const fitwidth = ()=>{
		if(comp.comp.colwidth != 'auto') {
			return;
		}
		window.setTimeout(()=>{
			let w = $("#"+useIn+comp.id).width() ;
			let colsize = 0;
			$(data.header[0]).each((a, b)=>{
				if(comp.type == 'grid'){
					if(!b.name){
						return true;
					}
				}
				colsize = colsize + (b.colspan?b.colspan:1);
			});
			let c = Math.round(w / colsize);
			let id = comp.id;
			$("#"+useIn+id +" div.dg-cell").width(c - 7 - borderwidth);
			//宽度自适应后，禁用纵向滚动条
			$("#"+useIn+comp.id + " div.lock-dg-body").css({"overflow-x":"hidden"});
		}, 200);
	}

	//调整表格宽度
	const tableWidthAdjust = ()=>{
		$("#"+useIn+comp.id + " div.lock-dg-header table th[colspan=1]").resizable({
			handles:'e',
			minWidth:80,
			maxWidth:600,
			helper: "resizable-helper",
			resize:function(event, ui){
				let width = ui.size.width;
				let pos = $(this).attr("pos");
				$(this).css({width: width+"px"}).find("div.dg-cell").css({
					width: width+'px'
				});

				if(comp.type == 'table' && comp.lockrow || comp.type == 'grid' && comp.lockcols){
					let l = $("#"+useIn+comp.id+ " div.lock-dg-fixed-header table")[0];
					for(let i=0; i< l.rows.length; i++){

              let c = l.rows[i].cells[pos];
              $(c).find("div.dg-cell").css({
                width: width + 'px'
              });

					}
				}
			},
			stop:function(event, ui){
				let pos = $(this).attr("pos");
				let width = ui.size.width;
				let l = $("#"+useIn+comp.id+ " div.lock-dg-body table")[0];
				for(let i=0; i< l.rows.length; i++){
					let c = l.rows[i].cells[pos];
					$(c).find("div.dg-cell").css({
						width: width+'px'
					});
				}
				//如果有锁定列，也需要调整锁定列的宽度
				if(comp.type == 'table' && comp.lockrow || comp.type == 'grid' && comp.lockcols){
					let l = $("#"+useIn+comp.id+ " div.lock-dg-fixed-data table")[0];
					for(let i=0; i< l.rows.length; i++){
            if(comp.type == 'grid') {
              let c = l.rows[i].cells[pos];
              $(c).find("div.dg-cell").css({
                width: width + 'px'
              });
            }else{
              for(let j=0; j<l.rows[i].cells.length; j++){
                let c = $(l.rows[i].cells[j]);
                if(c.attr("lvl") == pos){
                  c.find("div.dg-cell").css({
                    width: width+'px'
                  });
                }
              }
            }
					}

					//调整 lock-dg-fixed 的宽度
					let w = 0;
					let cells = l.rows[0].cells;
					for(let k=0; k<cells.length; k++){
						let c = cells[k];
						w += $(c).find("div.dg-cell").width() + 6;
						let tstyle = comp.tstyle;
						if(tstyle && (tstyle.bodyRightWidth || tstyle.bodyLeftWidth)){
							if(tstyle.bodyRightWidth){
							  w += tstyle.bodyRightWidth;
							}
							if(tstyle.bodyLeftWidth){
							  w += tstyle.bodyLeftWidth;
							}
						}
					}

					$("#"+useIn+comp.id+ " div.lock-dg-fixed").css({width: w + "px"});
				}
			}
		});
	}
	if(colwidth === 'auto'){
		fitwidth();
	  $(window).on("resize", fitwidth);
	  try{
	  	$("#"+useIn+comp.id + " div.lock-dg-header table th").resizable("destroy");
	  }catch(e){

	  }
	}else{
	  $(window).off("resize", fitwidth);
	  tableWidthAdjust();
	  let id = comp.id;
	  $("#"+useIn+id + " div.lock-dg-body").css({"overflow-x":""});
	}
}

//设置报表背景样式
export const createReportStyle = (reportStyle, pageInfo)=>{
	 let pageStyle = reportStyle;

      let ps = pageInfo.style;
      if(ps && ps.pageBgColor){
        pageStyle['background-color'] = ps.pageBgColor;
      }
      if(ps && ps.pageBgImg){
        pageStyle['background'] = "url('" + baseUrl + 'bigscreen/' + ps.pageBgImg +"') no-repeat 0 0";
        pageStyle['background-attachment'] = "fixed";
        pageStyle['background-size'] = "100% 100%";
      }
}

export const getLoadingbackground = (styleType)=>{
	if(styleType && styleType === 'bigscreen'){
		return "rgba(0, 0, 0, 0.3)";
	}else{
		//默认色
		return null;
	}
}

export const resizeChart = (comps, useIn)=>{
	comps.forEach(m=>{
		if(m.type === 'chart'){
			let dom = document.getElementById("ct_"+m.id);
			if(dom){
				var chart = echarts.getInstanceByDom(dom);
				if(chart){
					let height = m.height?m.height:$(dom).height();
					chart.resize($(dom).width(), height);
				}
				//如果是热力图，需要修改热力图层位置
				if(m.comp.chartJson.typeIndex == 6){
					let option = chart.getOption();
					echartsUtils.heatMapSet(m.comp, option.data, chart, dom);
				}
			}
		}else if(m.type === 'box' && m.comp && m.comp.chart){  //带有图形的box
			let dom = document.getElementById(useIn+m.comp.chart.id);
			if(dom){
				var chart = echarts.getInstanceByDom(dom);
				if(chart){
					let height = m.height?m.height:$(dom).height();
					chart.resize($(dom).width(), height);
				}
			}
		}
  	});
}
/**
 * 表格滚动
 */
export const tableScroll = (comp, useIn, dataSize)=>{
	//隐藏滚动条
	$("#"+useIn+comp.id+" .lock-dg-body").css({"overflow-y":"hidden"});
	var curHeight = $("#"+useIn+comp.id+" .lock-dg-body").height();  //当前组件高度
	var o = $("#"+useIn+comp.id+" .lock-dg-body table");
	var idx = 1;
	var tableHeight = 0;
	var trheight = 0;  //当前行高度
	o.find("tr").each(function(){
		var h = $(this).height();
		tableHeight += h;
		trheight = h;
	})
	tableHeight = Math.round(tableHeight);  //表格高度
	var viewSize = Math.round(curHeight / trheight);  //在当前组件视觉效果内能装几个组件
	var scollIdx = 1;
	var exescroll = function(){
		scollIdx++;
		if(scollIdx % 130 == 0) {
			//剩余行的个数
			let leaveCnt = dataSize * 2 - idx;

			var h = idx * trheight;
			if(leaveCnt <= viewSize){
				o.animate({"margin-top": "-" + Math.round(h) + "px"}, null, 'linear', ()=>{
					o.css({"margin-top": "-" + ((dataSize - viewSize ) * trheight) + "px"});
				});
			}else{
				o.animate({"margin-top": "-" + Math.round(h) + "px"}, null);
			}
			idx++;

			if (leaveCnt <= viewSize) {
				idx = (dataSize - viewSize + 1);
			}
			//console.log("i am doing..." + comp.id);
		}
		if(scollIdx >= 10000){
			scollIdx = 1;
		}
		comp.scrollEventId = requestAnimationFrame(exescroll);
	}
	comp.scrollEventId = requestAnimationFrame(exescroll);
	/**
	//鼠标移上，停止滚动
	o.hover(function(){
		cancelAnimationFrame(comp.scrollEventId);
		delete comp.scrollEventId;
	}, function(){
		comp.scrollEventId = requestAnimationFrame(exescroll);
	});
	 */
}
//表格转置后的滚动
export const tableTranspositionScroll = (comp, useIn, colLength, dataSize)=>{
	//隐藏滚动条
	$("#"+useIn+comp.id+" .lock-dg-body").css({"overflow":"hidden"});
	var curWidth = Math.round($("#"+useIn+comp.id+" .lock-dg-body").width());  //当前视图的宽度
	var o = $("#"+useIn+comp.id+" .lock-dg-body table");
	var idx = 1;
	var tdWidth = 0;
	o.find("tr td").each(function(){
		var w = $(this).width();
		tdWidth = w;
		return false;
	})
	var viewSize = Math.round(curWidth / tdWidth);  //在当前组件视觉效果内能装几个组件
	//var tableWidth = colLength * tdWidth;
	//tableWidth = Math.round(tableWidth);
	tdWidth = Math.round(tdWidth);
	var scollIdx = 1;
	var exescroll = function(){
		scollIdx++;
		if(scollIdx % 130 == 0) {

			//剩余的td个数
			let leaveCnt = colLength - idx;
			var h = idx * tdWidth;
			if(leaveCnt  <= viewSize){
				o.animate({"margin-left": "-" + Math.round(h) + "px"}, 'slow', 'linear', ()=>{
					o.css({"margin-left": "-" + ((dataSize - viewSize ) * tdWidth) + "px"});
				});
			}else{
				o.animate({"margin-left": "-" + Math.round(h) + "px"});
			}

			idx++;
			//如果剩余的 TD 个数小于表格可视个数，重新开始滚动
			if(leaveCnt  <= viewSize){
				idx = (dataSize - viewSize + 1);
			}
		}
		if(scollIdx >= 10000){
			scollIdx = 1;
		}
		comp.scrollEventId = requestAnimationFrame(exescroll);
	}
	comp.scrollEventId = requestAnimationFrame(exescroll);
	/**
	//鼠标移上，停止滚动
	o.hover(function(){
		cancelAnimationFrame(comp.scrollEventId);
		delete comp.scrollEventId;
	}, function(){
		comp.scrollEventId = requestAnimationFrame(exescroll);
	});
	 */
}
export const stopScroll = (comp)=>{
	if((comp.type =='grid' || comp.type === 'table') && comp.scrollEventId){
		cancelAnimationFrame(comp.scrollEventId);
		delete comp.scrollEventId;
	}
}

/**
 * 组件自动刷新
 */
export const autoFlush = (comp, cb) =>{
	if(comp.flush && comp.flush.use == true){
		let scollIdx = 1;
		const flush = () => {
			scollIdx++;
			let step = comp.flush.flushstep || 5;  //5秒起步
			if(scollIdx % (60 * step) == 0) {  //一秒
				cb();
				//console.log("i am do " + comp.id);
			}
			if(scollIdx >= 100000){
				scollIdx = 1;
			}
			comp.autoflushflag = requestAnimationFrame(flush);
		}
		comp.autoflushflag = requestAnimationFrame(flush);
	}
}

export const stopFlush = (comp)=>{
	if(comp.autoflushflag){
		cancelAnimationFrame(comp.autoflushflag);
		delete comp.autoflushflag;
		//console.log("i am stop flush");
	}
}

//创建组件/组件标题的样式
export const createCompStyle = (c) => {
	if(!c.style){
		c.style = {};
	}
	var cs = c.style;
	var o = {
		hstyle:{},  //组件标题样式
		htxtstyle:{}, //组件标题文本的样式
		bstyle:{}, //组件body样式
		style:{}  //组件样式
	};
	if(cs.disableBg === true){
		o.style['background'] = "none";
		o.style['background-image'] = "none";
		o.style['box-shadow'] = "";
	}else{
		if(cs.pageBgColor){
		o.style['background-color'] = cs.pageBgColor;
		}
		if(cs.compBgImage){
		o.style['background-image'] = "url("+baseUrl+"bigscreen/"+cs.compBgImage+")";
		o.style['background-size'] = "100% 100%";
		o.style['background-position'] = "center";
		o.style['background-repeat'] = "no-repeat";
		}
	}
	if(cs.useShadow===true){
		o.style["box-shadow"] = "0 2px 5px 0 rgba(0, 0, 0, 0.16), 0 2px 10px 0 rgba(0, 0, 0, 0.12)";
	}
		if(cs.compBorderColor){
		o.style['border-color'] = cs.compBorderColor
		}
		if(cs.compBorderWidth != null){
		o.style['border-width'] = cs.compBorderWidth+"px";
		}
		if(cs.compBorderStyle){
		o.style['border-style'] = cs.compBorderStyle;
		}
		if(cs.disableCenterBorder){
		o.bstyle['border-top'] = "none";
		}
		if(cs.titleAlign){
		o.hstyle['text-align'] = cs.titleAlign;
		}
		if(cs.titleSize){
		o.htxtstyle['font-size'] = cs.titleSize + "px";
		}
		if(cs.titleBold){
		o.htxtstyle['font-weight'] = "bold";
		}
		if(cs.titleColor){
		o.htxtstyle['color'] = cs.titleColor;
		}
		if(cs.titleBgColor){
		o.hstyle['background-color'] = cs.titleBgColor;
		}
		if(cs.disableHeadBg){
		o.hstyle['background'] = "none";
		o.hstyle['background-image'] = "none";
		}
		if(cs.titleBorderColor){
		o.hstyle['border-color'] = cs.titleBorderColor;
		}
		if(cs.titleBorderWidth != null){
		o.hstyle['border-width'] = cs.titleBorderWidth+"px";
		}
		if(cs.titleBorderStyle){
		o.hstyle['border-style'] = cs.titleBorderStyle;
		}
	return o;
}
/**
 * 组件是否有链接
 * @param {*} comp
 */
export const compHasLink = (comp) => {
	let hasLink = false;
	if(comp.type === 'chart' && comp.comp.chartJson.link && comp.comp.chartJson.link.target){
		hasLink = true;
	}else if(comp.type === 'table' && comp.comp.link && comp.comp.link.target){
		hasLink = true;
	}
	//链接到报表
	if(comp.type == 'chart' && comp.comp.chartJson.link && comp.comp.chartJson.link.reportId){
		hasLink = true;
	}else if(comp.type === 'table' && comp.comp.link && comp.comp.link.reportId){
		hasLink = true;
	}
	//交叉表钻取
	if(comp.type === 'table' && comp.comp.drillDim && comp.comp.drillDim.length > 0){
		hasLink = true;
	}
	return hasLink;
}
/**
 * 获取报表链接参数名
 * @param {*}} comp
 * @param {*} type
 */
export const getLinkParam = (comp, type, gridLinkParams) => {
	var param = null;
	if(type === 'chart'){
		var x = comp.chartJson.xcol;
		let p = x.alias;
		if(x.tableColKey && x.tableColKey.length > 0){  //有tableColKey, tableColText
			p = x.alias+"2";
		}
		param = {key:p, name:p};
	}else if(type === 'table'){
		if(!comp.rows || comp.rows.length == 0){
			throw Error("交叉表行标签未设置维度。");
		}
		let ret = [];
		let retkey = [];
		loopDims(comp.rows, item=>{
			let matchId = item.match;
			let dim = comp.dims.filter(m=>m.id === matchId)[0];
			ret.push(dim.alias + "("+dim.dimdesc+")");
			retkey.push(dim.alias);
		});
		param = {
			name : ret.join(","),
			key : retkey.join(",")
		}
	}else if(type === 'grid'){
		if(comp.link && comp.link.colId){
			let p = comp.link.colId;
			param = {key : p, name : p};
		}else{
			let p = gridLinkParams[0].id;
			param = {key : p, name : p};
		}
	}
	return param;
}

export const resetWindows = (tp)=>{
	if(tp == 'min'){
	  $("div.cctx").hide();
	}else{
	  $("div.cctx").show();
	}
}
/**
 * 处理指标定制
 * @param {*} json
 */
export const kpiCustomized = (json) => {
	let comps = findAllComps(json);
	$(comps).each((a, b)=>{
		if(b.type === 'table'){
			if(b.kpiCustomized === true){  //启用了指标定制
				let selKpis = JSON.parse(localStorage.getItem("c_" + b.id) || "[]");
				if(selKpis.length > 0) {
					$(b.comp.kpiJson).each((c, d)=>{
						d.hideNode = true;
						if(selKpis.indexOf(d.alias) >= 0 ){
							d.hideNode = false;
						}
					});
				}
			}else{
				$(b.comp.kpiJson).each((c, d)=>{
					delete d.hideNode;
				});
			}
		}
	});
}

export const resizePortalCharts = (json) => {
	window.setTimeout(()=>{
		let comps = findAllComps(json);
		$(comps).each((a, comp)=>{
			let dom = document.getElementById("ct_"+comp.id);
			if(dom){
				var chart = echarts.getInstanceByDom(dom);
				if(chart){
					chart.resize({animation:{duration:300, easing:"cubicIn"} });
				}
			}
		});
    }, 200);
}

export const getGridColById = (id, cols, first) => {
	let node = null;
	if(first){
		node = cols[0];
		if(node.children){
			node = node.children[0];
		}
		return node;
	}
	$(cols).each((a, b)=>{
	  if(b.id == id){
		node = b;
		return false;
	  }
	   if(b.children){
		  $(b.children).each((c, d)=>{
			  if(d.id == id){
				node = d;
				return false;
			  }
		  });
	   }
	});
	return node;
  }
