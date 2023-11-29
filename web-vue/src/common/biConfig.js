/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

/**
 * 配置编译环境和线上环境之间的切换
 *
 * baseUrl: 域名地址
 *
 */
import $ from 'jquery'
import { Message, Notification } from 'element-ui'

//const baseUrl = '../rsbi-ent-v5-bg/'
//const baseUrl = 'http://localhost:8082/';
const baseUrl = "";

export {
	baseUrl
}
/**
 * 封装Ajax请求
 * @param {*} cfg
 * @param {*} ts
 * @param {*} loadingObj   loading对象，如果有
 * @param {*} errorCallback, 系统错误后的回掉函数，如果有
 */
export const ajax = (cfg, ts, loadingObj, errorCallback) => {
	/**
	if(!ts){
		Message.error({message:cfg.url + "未定义this参数", showClose:true});
		return;
	}
	 */
	//单点登录token
	let token = ts && ts.$route  ? ts.$route.query.rsbiToken : null;
	if(token){
		if(cfg.url.indexOf('?') >= 0){
			cfg.url = cfg.url + "&rsbiToken="+token;
		}else{
			cfg.url = cfg.url + "?rsbiToken=" + token;
		}
	}
	let o = {
		type:cfg.type,
		data:cfg.data,
		dataType:"JSON",
		xhrFields: {withCredentials: true},
		contentType: "application/json; charset=utf-8",
		crossDomain: true,
		url:baseUrl+cfg.url,
		//timeout: 30000, //超时时间：30秒
		async:true,
		success:function(resp){
			if(loadingObj){
				loadingObj.close();
			}
			if(resp.result === 1){
				cfg.success(resp);
			}else if(resp.result === 2){
				if(!ts){
					return;
				}
				if(ts.$route.path === '/'){
					return;
				}
				Notification.error({title:"出错了", message:"登录信息已经失效！"});
				let callback = resp.rows;
				if(callback){
					ts.$router.push("/?callback="+callback);
				}else{
					ts.$router.push("/");
				}
				if(errorCallback){
					errorCallback(resp);
				}
			}else if(resp.result === 0){
				if(ts){
					const h = ts.$createElement;
					Message.error({message:h('div',[h('h5','系统错误'), h('div', resp.msg==null?"null":resp.msg)]), type:"error",showClose: true});
				}else{
					Message.error({message:resp.msg, showClose:true});
				}
				if(errorCallback){
					errorCallback(resp);
				}
			}else if(resp.result === 3){ //not find
				if(errorCallback){
					errorCallback(resp);
				}
			}else{
				Message.error(baseUrl+cfg.url + " 接口返回格式错误，未包含 result 值。");
				if(errorCallback){
					errorCallback(resp);
				}
			}
		},
		error: function(e){
			if(loadingObj){
				loadingObj.close();
			}
			Message.error("系统错误！" + (e.responseText?e.responseText:"网络链接失败"));
			if(errorCallback){
				errorCallback(e);
			}
		}
	};
	if(!cfg.postJSON || cfg.postJSON === false){
		delete o.contentType;
	}
	if(cfg.async === false){ //同步请求
		o.async = cfg.async;
	}
	if(token){  //单点登录，分批进行请求，防止多线程同时进行单点登录
		var l = Math.floor( Math.random() * 3000 );
		window.setTimeout(()=>{
			$.ajax(o);
		}, l);
	}else{
		$.ajax(o);
	}

}

//生成唯一标识
export const newGuid = ()=>
{
    var guid = "";
    for (var i = 1; i <= 32; i++){
      var n = Math.floor(Math.random()*16.0).toString(16);
      guid +=   n;
      //if((i==8)||(i==12)||(i==16)||(i==20))
      //  guid += "-";
    }
    return guid;
}
//在textarea光标处插入文本
export const insertText2focus = (obj,str) => {
	str = str　+ " ";
	obj.focus();
	if (document.selection) {
		var sel = document.selection.createRange();
		sel.text = str;
	} else if (typeof obj.selectionStart == 'number' && typeof obj.selectionEnd == 'number') {
		var startPos = obj.selectionStart,
			endPos = obj.selectionEnd,
			cursorPos = startPos,
			tmpStr = obj.value;
		obj.value = tmpStr.substring(0, startPos) + "" + str + tmpStr.substring(endPos, tmpStr.length);
		cursorPos += str.length;
		obj.selectionStart = obj.selectionEnd = cursorPos;
	} else {
		obj.value += str;
	}
}

/**
 * list 转 String
 * @param {*} ls
 */
export const list2string = (ls)=>{
	if(!ls){
		return "";
	}
	return ls.join(",");
}

/**
 * 解析图形JSON中的f$标志为字符串
 * @param json
 */
export const loopChartJson = (json)=>{
	const exec = (jsons)=>{
		for(let key in jsons) {
			let o = jsons[key];
			if(typeof o  === 'object'){
				exec(o);  //如果是Object则递归
			}else if(typeof o === 'string'){
				if(o.indexOf&&o.indexOf('f$')>-1){  //吧f$字符串解析成函数
					jsons[key] = eval("("+o.replace("f$", "")+")");
				}
			}
			//else if(typeof o === 'number'){

			//}
		}
	}
	exec(json);
	return json;
}

export const formatDate = function(date, fmt){
	var o = {
	   "M+" : date.getMonth()+1,                 //月份
	   "d+" : date.getDate(),                    //日
	   "H+" : date.getHours(),                   //小时
	   "m+" : date.getMinutes(),                 //分
	   "s+" : date.getSeconds(),                 //秒
	   "q+" : Math.floor((date.getMonth()+3)/3), //季度
	   "S"  : date.getMilliseconds()             //毫秒
   };
   if(/(y+)/.test(fmt)) {
		   fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
   }
	for(var k in o) {
	   if(new RegExp("("+ k +")").test(fmt)){
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
		}
	}
   return fmt;
};

/**
 * 是否移动端检查
 */
export const isMobile =()=>{
	let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i);
	if(flag){
		return true;
	}else{
		return false;
	}
}
