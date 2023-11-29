/**
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
/**
 * 图形的工具类，在报表中使用
 */
import $ from 'jquery'
import { ajax } from "@/common/biConfig";

export const chartsort = (tp, sorttp, comp, curObj, cb) =>{
	var json = comp;
	if(tp == 'xcol'){
		//清除度量排序,因为度量排序最优先
		if(json.kpiJson && json.kpiJson.length > 0){
			delete json.kpiJson[0].sort;
		}
		json.chartJson.xcol.dimord = sorttp;
	}else
	if(tp == 'ycol'){
		json.kpiJson[0].sort = sorttp;
	}else
	if(tp == 'scol'){
		//清除度量排序
		delete json.kpiJson[0].sort;
		json.chartJson.scol.dimord = sorttp;
	}else if(tp =="ycols"){
		for(var k=0; k<json.mkpiJson.length; k++){
			delete json.mkpiJson[k].sort;
		}
		var k = curObj;
		k.sort = sorttp;
	}
	if(cb){
		cb();
	}
}

export const delChartKpiOrDim = (tp, json, curObj, cb) => {
	if(tp == 'xcol'){
		json.chartJson.xcol = {};
	}else
	if(tp == 'ycol'){
		json.chartJson.ycol = {};
		if(json.kpiJson.length > 1){
			json.kpiJson[0] = null;
		}else{
			json.kpiJson = [];
		}
	}else
	if(tp == 'y2col'){
		if(json.kpiJson.length > 1){
			json.kpiJson[1] = null;
		}else{
			json.kpiJson = [];
		}
		delete json.chartJson.alignZeroScale;

	}else
	if(tp == 'y3col'){
		json.kpiJson[2] = null;
	}else
	if(tp == 'scol'){
		json.chartJson.scol = {};
	}else
	if(tp == "ycols"){
		//从集合中删除
		var idx = -1;
		for(let i=0; i<json.mkpiJson.length; i++){
			var k = json.mkpiJson[i];
			if(k.kpi_id == curObj.kpi_id){
				idx = i;
				break;
			}
		}
		json.mkpiJson.splice(idx, 1);
	}
	if(cb){
		cb();
	}
}
