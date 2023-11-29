/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
import $ from 'jquery'
import h337 from 'heatmapjs'

export let cityPosJson = {};  //城市的经纬度信息
//百度地图的样式描述json
export const bmapStyleDef = [{'featureType':'water','elementType':'all','stylers':{'color':'#d1d1d1'}},{'featureType':'land','elementType':'all','stylers':{'color':'#f3f3f3'}},{'featureType':'railway','elementType':'all','stylers':{'visibility':'off'}},{'featureType':'highway','elementType':'all','stylers':{'color':'#fdfdfd'}},{'featureType':'highway','elementType':'labels','stylers':{'visibility':'off'}},{'featureType':'arterial','elementType':'geometry','stylers':{'color':'#fefefe'}},{'featureType':'arterial','elementType':'geometry.fill','stylers':{'color':'#fefefe'}},{'featureType':'poi','elementType':'all','stylers':{'visibility':'off'}},{'featureType':'green','elementType':'all','stylers':{'visibility':'off'}},{'featureType':'subway','elementType':'all','stylers':{'visibility':'off'}},{'featureType':'manmade','elementType':'all','stylers':{'color':'#d1d1d1'}},{'featureType':'local','elementType':'all','stylers':{'color':'#d1d1d1'}},{'featureType':'arterial','elementType':'labels','stylers':{'visibility':'off'}},{'featureType':'boundary','elementType':'all','stylers':{'color':'#fefefe'}},{'featureType':'building','elementType':'all','stylers':{'color':'#d1d1d1'}},{'featureType':'label','elementType':'labels.text.fill','stylers':{'color':'#999999'}}];
export const bmapStyleBlack = [{"featureType":"water","elementType":"all","stylers":{"color":"#044161"}},{"featureType":"land","elementType":"all","stylers":{"color":"#004981"}},{"featureType":"boundary","elementType":"geometry","stylers":{"color":"#064f85"}},{"featureType":"railway","elementType":"all","stylers":{"visibility":"off"}},{"featureType":"highway","elementType":"geometry","stylers":{"color":"#004981"}},{"featureType":"highway","elementType":"geometry.fill","stylers":{"color":"#005b96","lightness":1}},{"featureType":"highway","elementType":"labels","stylers":{"visibility":"off"}},{"featureType":"arterial","elementType":"geometry","stylers":{"color":"#004981"}},{"featureType":"arterial","elementType":"geometry.fill","stylers":{"color":"#00508b"}},{"featureType":"poi","elementType":"all","stylers":{"visibility":"off"}},{"featureType":"green","elementType":"all","stylers":{"color":"#056197","visibility":"off"}},{"featureType":"subway","elementType":"all","stylers":{"visibility":"off"}},{"featureType":"manmade","elementType":"all","stylers":{"visibility":"off"}},{"featureType":"local","elementType":"all","stylers":{"visibility":"off"}},{"featureType":"arterial","elementType":"labels","stylers":{"visibility":"off"}},{"featureType":"boundary","elementType":"geometry.fill","stylers":{"color":"#029fd4"}},{"featureType":"building","elementType":"all","stylers":{"color":"#1a5787"}},{"featureType":"label","stylers":{"color":"#999999"}}];

//性别图里表示性别的图片
export const sexIcon ={
    'male': 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAoCAYAAAD+MdrbAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6ODkzRDIwQTE0MTIxMTFFODkyOTU4RUU5NzM3MjE3MDMiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6ODkzRDIwQTA0MTIxMTFFODkyOTU4RUU5NzM3MjE3MDMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKFdpbmRvd3MpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NTQ4MERFMjNCRDNDMTFFNzgyQTFFRkM1MDA3MjdBRTYiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NTQ4MERFMjRCRDNDMTFFNzgyQTFFRkM1MDA3MjdBRTYiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4iuAMWAAABl0lEQVR42uzWTSgEYRzH8ZmJcPDOkRMXG1cl5SInJ1e5aEtuLhwoNy5K0mpzWCXJyQEH5eDEwUVecuKCKEJ5qU2L8R2e1fT0zLM7O3NR869P284zz69nnmeemTFt2zac6kz+/kpVjG6MoB0ZbGMWJ/jKnrg3bP78WoZ3OW2DWEcPKlGHAWyiC6aqk1fFMI4yRVsDJlDrJ7AXjZp2Z4RtfgLrDX0VocJP4H2OwE+8+Ancwa2mfR9nfgIPMYMnxchuMI071TzoKoFHxMXKOjfrERaw6zWxuvrACjZQLW5kZ27fdSslT0ETyqXjGdeuqHH1e8WFe8e4A0vQL7ZZs2oXSOVc/jnmsJodtTuwDynDX7WKPmmsyas8ahReY6rbpiVAYEwVaBkhlCVNcqFlhzoqI+zLjAL/ygyQY4Z9H1qqkKsAgdeqwESAwHnV4yslnnVD4oWeTz1gEUuqwGdMYQtV4tgkOjzCklgWnyRprye203Dg+h/XjO5YOjevldW9c0qjrRcF/tdA029brsBT8aEke8OlqsO3AAMAxyBOvxLL2/sAAAAASUVORK5CYII=',
    'female': 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAdCAYAAACaCl3kAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OTgxQUVBQkE0MTIwMTFFODlBRjc4REM5QkNCNEQ3NkEiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OTgxQUVBQjk0MTIwMTFFODlBRjc4REM5QkNCNEQ3NkEiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKFdpbmRvd3MpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NTQ4MERFMjNCRDNDMTFFNzgyQTFFRkM1MDA3MjdBRTYiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NTQ4MERFMjRCRDNDMTFFNzgyQTFFRkM1MDA3MjdBRTYiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz5Op4glAAABOUlEQVR42mL8//8/Awj8zY1lgAJ2IG4C4lQg/gbEy4C4Foh/giSZJy8GK2JhwAQzgDgByhYE4lIgFgXiRGRFTGiaQArjsBgWC5XDqVEcixjYhVA5nBrvA/F7LBrfQeVwagQFQDEorJDEQOwSWODAALbAmQ/EN5ACaAEQH0dXxPgnJ4aBHIAtILYB8X80vJWQRiEgdsFimCtUDqfGACBmxaKRFSqHU2M4Hm+F49IoAsROeDQ6QdVgaAzEET3IUReITWMYEbEQhq5RDIgdidDoCFUL1xgETciEADNULVxjGAmJJgymUQKI7UjQaA/SA9JoRaQzkcPFCkScQs8yBABI7SmQxidQDz8gQtMDqNonTEg5QhGUzYC4A4uGDqicIlQtAwusuEMrIrHHBZJaJgYywRDX+IkYMYAAAwB6sjfXWpdRXAAAAABJRU5ErkJggg=='
};
/**
 * 显示图形的 tooltip
 * @param params
 * @param fmt
 * @param unit
 * @param fmt2
 * @param unit2
 * @param y2ser 在曲线图，柱状图的多指标图形中，在第二纵轴显示的指标
 * @returns {string}
 */
export const toolTips = (params, fmt, unit, ydesc, fmt2, unit2, y2desc, thb, chartId, y2ser) => {
    if(params.length){  //数组
        //o = params[0];
        var s = '<b>' + params[0].name  + "</b><br/>";
        $(params).each(function(a, b){
            s += b.seriesName === "" ?"": b.seriesName +": ";
            if(y2ser){
                if(y2ser.indexOf(b.seriesName) >= 0){
                    s += formatNumber(b.data, fmt2) + unit2;
                }else{
                    s += formatNumber(b.data, fmt) + unit;
                }

            }else if(thb === true) {  //具体同环比计算等衍生指标
                if (b.seriesName.indexOf("同比") >= 0 || b.seriesName.indexOf("环比") >= 0) {
                    s += formatNumber(b.data, "0.00%");
                } else {
                    s += formatNumber(b.data, fmt) + unit;
                }
            }else{
                if (fmt2) { //双指标
                    var cid = "ct_" + chartId; //图形以ct_开头
                    var chart = echarts.getInstanceByDom(document.getElementById(cid));
                    var opts = chart.getOption();
                    $(opts.series).each(function(c, d){
                        if(d.name === b.seriesName){
                            if(d.yAxisIndex === 1 || d.xAxisIndex === 1) {
                                s += formatNumber(b.data, fmt2) + unit2;  //y2轴
                            }else{
                                s += formatNumber(b.data, fmt) + unit;	// y轴
                            }
                            return false;
                        }
                    });
                } else {
                    s += formatNumber(b.data, fmt) + unit;
                }
            }
            if(!(a === params.length - 1)){
                s += "<br/>";
            }
        });
        return s;
    }else{
        var o = params;
        return  o.name + ': '+ formatNumber(o.value, fmt)+unit;
    }
}
//地图的Tooltips， 一个地图区域显示多个指标
export const mapToolTips = (params, fmt, unit, kpis, tpClass)=>{
    if(kpis){
        var o = params;
        var dt = params.data;
        let j = ['<b>'+o.name+'</b><br/>'];
        if(dt){
            if(tpClass == 'black'){  //tooltip 暗黑风格
                j.push("<ul>");
                $(kpis).each((a, b)=>{
                    j.push("<li>");
                    j.push("<span class='header'>");
                    j.push(b.desc);
                    j.push("：");
                    j.push("</span>");
                    j.push("<span class='data'>");
                    j.push(formatNumber(dt[b.name], fmt)+unit);
                    j.push("</span>");
                    j.push("</li>");
                });
                j.push("</ul>");
            }else{
                $(kpis).each((a, b)=>{
                    j.push(b.desc);
                    j.push("：");
                    j.push(formatNumber(dt[b.name], fmt)+unit);
                    j.push("<br/>");
                });
            }
        }
        return  j.join("");
    }else{
        var o = params;
        var v = o.value.length ? o.value[2] : o.value;
        return  o.name + ': '+ formatNumber(v, fmt)+unit;
    }
}
//百度地图 tooltip
export const baiduMapToolTips = (params, fmt, unit, kpis)=>{
    let name = params.data[3];
    if(kpis){
        var o = params;
        var dt = params.data[4];
        let j = ['<b>'+name+'</b><br/>'];
        if(dt){
            $(kpis).each((a, b)=>{
                j.push(b.desc);
                j.push("：");
                j.push(formatNumber(dt[b.name], fmt)+unit);
                j.push("<br/>");
            });
         }
        return  j.join("");
    }else{
        var o = params;
        var v = o.value.length ? o.value[2] : o.value;
        return  name + ': '+ formatNumber(v, fmt)+unit;
    }
}
//数字格式化
export const formatNumber = (num,pattern, shortname)=>{
    if(num == null){
        return "";
    }
    if(!pattern || pattern.length == 0){
        return num;
    }
    var negative = false;  //负数
    if(num < 0 ){
        num = Math.abs(num);
        negative = true;
    }
    var shortdw;
     if(shortname && num > 100000000){
        num = num / 100000000;
        shortdw = "亿";
     }else if(shortname && num > 10000000){
        num = num / 10000000;
        shortdw = "千万";
     }else if(shortname && num > 1000000){
        num = num / 1000000;
        shortdw = "百万";
     }else if(shortname && num > 10000){
         num = num / 10000;
         shortdw = "万";
     }else if(shortname && num > 1000){
         num = num / 1000;
         shortdw = "千";
     }
     if(pattern.indexOf("%") <= 0 && shortname){
         return (negative?"-":"") + (Math.round(num * 10) / 10) + (shortdw?shortdw:"");
     }
     if(pattern.indexOf("%") > 0){
         num = num * 100;
     }
     var fmtarr = pattern?pattern.split('.'):[''];
     var retstr='';

     //先对数据做四舍五入
     var xsw = 0;
     if(fmtarr.length > 1){
         xsw = fmtarr[1].length;
     }
     var bl = 1;
     for(i=0; i<xsw; i++){
         bl = bl * 10;
     }
     num = num * bl;
     num = Math.round(num);
     num = num / bl;

     var strarr = num?num.toString().split('.'):['0'];

     // 整数部分
     var str = strarr[0];
     var fmt = fmtarr[0];
     var i = str.length-1;
     var comma = false;
     for(var f=fmt.length-1;f>=0;f--){
       switch(fmt.substr(f,1)){
         case '#':
           if(i>=0 ) retstr = str.substr(i--,1) + retstr;
           break;
         case '0':
           if(i>=0) retstr = str.substr(i--,1) + retstr;
           else retstr = '0' + retstr;
           break;
         case ',':
           comma = true;
           retstr=','+retstr;
           break;
       }
     }
     if(i>=0){
       if(comma){
         var l = str.length;
         for(;i>=0;i--){
           retstr = str.substr(i,1) + retstr;
           if(i>0 && ((l-i)%3)==0) retstr = ',' + retstr;
         }
       }
       else retstr = str.substr(0,i+1) + retstr;
     }

     retstr = retstr+'.';
     // 处理小数部分
     str=strarr.length>1?strarr[1]:'';
     fmt=fmtarr.length>1?fmtarr[1]:'';
     i=0;
     for(var f=0;f<fmt.length;f++){
       switch(fmt.substr(f,1)){
         case '#':
           if(i<str.length) retstr+=str.substr(i++,1);
           break;
         case '0':
           if(i<str.length) retstr+= str.substr(i++,1);
           else retstr+='0';
           break;
       }
     }

     var r = retstr.replace(/^,+/,'').replace(/\.$/,'');
     if(pattern.indexOf("%") > 0){
         r = r + "%";
     }
     if(shortdw){
         r = r + shortdw;
     }
     if(negative){
         r = "-" + r;
     }
     return r;
   }
/**
	 * 对于散点图的ToolTip,如果值相同，会覆盖后面的点，现在通过第一个点抓出值相同的所有点，显示再 tooltip中
	 * @returns
	 */
export const scatterTooltip = (option, params)=>{
    var target = {x:params.data[0], y:params.data[1], name:params.data[2]};
    var dts = option.series[params.seriesIndex].data;
    var r = [];
    for(let k=0; k<dts.length; k++){
        var t = dts[k];
        if(t[0] == target.x && t[1] == target.y){
            r.push(t[2]);
        }
    }
    return r.join(",");
}
/**
配置气泡大小
转换到 10 到 50
**/
export const bubbleSize = (maxval, minval, val, targetMax)=>{
	if(maxval == minval){
		return 40;
	}
	if(!targetMax){
		targetMax = 50;
	}
	var r = (targetMax-10)/(maxval-minval)*(val-minval)+10;
	return r;
}
export const converMapData2 = (name, value) => {
    var geoCoordMap = cityPosJson;
    var res;
    var geoCoord = geoCoordMap[name];
    if (geoCoord) {
        res = geoCoord.concat(value);
    }
    return res;
}
/**
 * 转换地图 地域名称 到 经纬度
 * @param data
 * @param geoCoordMap
 * @returns {[]}
 */
export const converMapData = (data, geoCoordMap)=>{
    geoCoordMap = cityPosJson;//从全局变量中取经纬度
    var res = [];
    for (var i = 0; i < data.length; i++) {
        var o = data[i];
        var geoCoord = geoCoordMap[o.name];
        if (geoCoord) {
            res.push([geoCoord[0], geoCoord[1], o.value, o.name, o]);
        }
    }
    return res;
};
//雷达图的tooptip
export const radarTooltip = (data, chartId, fmt, unit) => {
    var s = "";
    var serName = data.seriesName;
    s += '<b>' + serName + '</b><br/>';
    var chart = echarts.getInstanceByDom(document.getElementById("ct_" + chartId));
    var opt = chart.getOption();
    var dts = opt.radar[0].indicator;
    var value = data.value;
    $(dts).each(function (a, b) {
        s += b.name + "："+ formatNumber(value[a], fmt) + unit + '<br/>';
    });
    return s;
};

export const heatMapSet = (comp, data, echartsInstance, dom) => {
    if(!data || data.length == 0){
        return;
    }
    let radius = comp.chartJson.barBorderRadius;
    let max = 0;
    let min = 0;
    let dt = [];
    $(data).each((a, b)=>{
        let v = b.value;
        if(a == 0){
            max =v;
            min = v;
        }else{
            if(max < v) max = v;
            if(min > v) min = v;
        }

        let ret = echartsInstance.convertToPixel('geo', [b.lon, b.lat]);
        let o = {x:ret[0], y: ret[1], value:v};
        if(radius > 0){
            o.radius = radius;
        }
        dt.push(o);
    });
    //给地图添加热力图覆盖层dom
    let clazz = "heatmap";
    if($(dom).find("."+clazz).length > 0){
        $(dom).find("."+clazz).remove();
    }
    let id = "t" + Math.random();
    $(dom).append("<div class='"+clazz+"' id='"+id+"' style='width:100%;height:100%;'></div>");

    var heatmap = h337.create({
        container: document.getElementById(id),
        //backgroundColor: 'rgba(0,0,0,.95)',
        // the maximum opacity (the value with the highest intensity will have it)
        //maxOpacity: .8,
        // minimum opacity. any value > 0 will produce
        // no transparent gradient transition
       // minOpacity: .1

    });
    max = max * 1.5;
    min = min * 0.5;
    if(comp.kpiJson[0].max){
        max = comp.kpiJson[0].max;
    }
    if(comp.kpiJson[0].min){
        min = comp.kpiJson[0].min;
    }
    heatmap.setData({
        max: max,
        min: min,
        data: dt,
    });
    $(dom).find("."+clazz).css({
        position:"absolute",
        left : "0px",
        top:"0px",
        "pointer-events" : "none"
    });
}
