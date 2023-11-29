/**
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
/**
 * 仪表盘utils类
 */
import $ from "jquery";
import {baseUrl, ajax, formatDate} from '@/common/biConfig';
import { Message, Loading } from 'element-ui'

//默认值
const baseInfo = {
    title:"我的仪表盘", //默认标题
    style:"def" //默认样式名称
};
export {
    baseInfo
}

/**
 * 根据风格 style 获取样式名称
 * @param {*} prefix
 */
export const geStyleName = (prefix, pageInfo) => {
    let s = null;
    if(pageInfo == null){
        return "";
    }
    if(pageInfo.style && pageInfo.style.style){
        s = pageInfo.style.style;
    }else{
        s = baseInfo.style;
    }
    return prefix + "-" + s;
}

export const resizeChart = (comp, prefix) => {
    if(!prefix){
        prefix = "ct_";
    }
    window.setTimeout(()=>{
        let dom = document.getElementById(prefix+comp.id);
        if(dom){
            var chart = echarts.getInstanceByDom(dom);
            if(chart){
                chart.resize({animation:{duration:300, easing:"cubicIn"} });
            }
            //如果是热力图，需要修改热力图层位置
            if(comp.comp.chartJson.typeIndex == 6){
                let option = chart.getOption();
                echartsUtils.heatMapSet(comp.comp, option.data, chart, dom);
            }
        }
    }, 200);

}

//通过参数刷新所有组件
export const flushCompsByPms = (param, pageInfo, cb)=>{
    for(let o in pageInfo.comps){
        let comp = pageInfo.comps[o];
        //判断参数是否存这个组件中
        var extParam = false;
        if(!comp.comp){
            continue;
        }
        //传递参数给组件
        var lk = false;
        $(comp.comp.compParams).each(function(a, b){
            //组件需要判断是否包含更新的参数
            if(b.type === 'between'){
                if(b.linkparam ===param.id+"_start" || b.linkparam2 === param.id+"_end"){
                    extParam = true;
                    return false;
                }
                if(b.linkparam === param.id || b.linkparam2 === param.id){
                    extParam = true;
                    return false;
                }
            }else{
                if(b.linkparam === param.id){
                    extParam = true;
                    return false;
                }
            }
        });
        if(comp.type === 'table' || comp.type === 'chart'){  //存在指标计算中的 - 比较指定日期 - 关联到参数
            let kpis = null;
            if(comp.type === 'table'){
                kpis = comp.comp.kpiJson;
            }else if(comp.type === 'chart'){
                kpis = comp.comp.mkpi == true ? comp.comp.mkpiJson : comp.comp.kpiJson;
            }
            $(kpis).each((a, b)=>{
                if(b.compareDate && b.compareDate.valtype === 'param' && b.compareDate.param === param.id){
                    extParam = true;
                    lk = true;
                    return false;
                }
            });
            if(comp.type === 'table'){  //交叉表的 [是否显示] 的回调函数是否使用了参数
                $(kpis).each((a, b)=>{
                    if(b.hideNodeCallback && b.hideNodeCallback.indexOf(param.id) >= 0){
                        extParam = true;
                        lk = true;
                        return false;
                    }
                });
            }
        }
        if(extParam === false){
            continue;
        }
        $(comp.comp.compParams).each(function(a, b){
            if (b.linkparam || b.linkparam2) {
                //连接到参数的筛选条件
                if (b.type === 'between') {//between 有两个参数
                    if(b.usetype ==='param') {
                        lk = compParamMatch(pageInfo, b, b.linkparam);
                    }
                    if(!b.usetype2 && b.usetype === 'param' || b.usetype2 === 'param') {
                        lk = compParamMatch(pageInfo, b, b.linkparam2, true);
                    }
                } else {
                    if(b.usetype ==='param') {
                        lk = compParamMatch(pageInfo, b, b.linkparam);
                    }
                }
            }
        });
        if(lk){
            deletelinkParam(comp);
            //刷新组件的回调函数
            if(cb){
                cb(comp);
            }
        }
    }
}

export const findGlobalParamById = (pid, pageInfo) => {
    var ret = null;
    $(pageInfo.globalParams).each(function(a, b){
        if(b.id == pid){
            ret = b;
            return false;
        }
    });
    return ret;
}

/**
 * 给参数设置值,吧globalParam的值给compParam, 当是between时,第二个参数 isparam2=true
 * @param {*} compParam
 * @param {*} key
 * @param {*} isparam2
 */
export const compParamMatch = (pageInfo, compParam, key, isparam2) => {
    var b = compParam;
    var match = 0;
    var gp = findGlobalParamById(key, pageInfo);  //匹配上参数
    if(!gp && key.indexOf("_start") >=0){
        gp = findGlobalParamById(key.split("_")[0], pageInfo);   //匹配上时间参数的start
        match = 1;
    }
    if(!gp && key.indexOf("_end") >= 0){
        gp = findGlobalParamById(key.split("_")[0], pageInfo);   //匹配上时间参数的 end
        match = 2;
    }
    if(!gp){
        throw "组件关联的参数不存在，参数标识" + key;
    }
    if (match === 2) {//匹配上end 取值 selval2
        if (compParam.type === 'between') {
            //只有between 需要设置 val2
            if(gp.selval2){
                b.val2 = getParamselval(gp.selval2);
            }else{
                b.val2 = parserDefvalue2(gp);
            }
        } else {
            if(gp.selval){
                b.val = getParamselval(gp.selval2);
            }else{
                b.val = parserDefvalue(gp);
            }
        }
        return true;
    } else {
        if(isparam2===true){  //between的第二个参数
            if(gp.selval){
                b.val2 = getParamselval(gp.selval);
            }else{
                b.val2 = parserDefvalue(gp);
            }
        }else{
            if(gp.selval){
                b.val = getParamselval(gp.selval);
            }else{
                b.val = parserDefvalue(gp);
            }
        }
        return true;
    }

    return false;
}
export const getParamselval = (selval) => {
    let val = null;
    if(selval.length){ //数组
        let v = "";
        $(selval).each((a, b)=>{
            v += b.value;
            if(a != selval.length - 1){
                v += ",";
            }
        });
        val = v;
    } else{
        val = selval.value;
    }
    return val;
}
/**
//删除组件得defval (事件接收用得参数)
 * @param {*} comp
 */
export const deletelinkParam = (comp) => {
    if(comp.type === 'chart'){
        if(comp.comp.chartJson.linkAccept){
            delete comp.comp.chartJson.linkAccept.dftval;
        }
    }else{
        if(comp.comp.linkAccept){
            delete comp.comp.linkAccept.dftval;
        }
    }
}

/**
 * 处理参数默认值
 * @param gparam 全局参数, pageInfo里面的 globalParams
 * @returns {number}
 */
export const parserDefvalue = (gparam) => {
    var val = !gparam.defvalue ? null : gparam.defvalue; //替换 日期类型参数 的 now
    if (gparam.type === 'datetime' || gparam.type === 'onedate') {
        val = parserDefDate(val, gparam.format);
    }
    return val;
}
/**
 * 解析第二个默认值（时间区间类型）
 * @param gparam
 * @returns {null}
 */
export const parserDefvalue2 = (gparam) => {
  var val = !gparam.defvalue2 ? null : gparam.defvalue2; //替换 日期类型参数 的 now
  if (gparam.type === 'datetime' || gparam.type === 'onedate') {
    val = parserDefDate(val, gparam.format);
  }
  return val;
}
/**
 * 根据 now, now + 1 等字符串提取日期
 */
export const parserDefDate = (input, type) => {
    var reg = /\s*now\s*([+|-])*\s*([0-9]*)/g;
    var r = reg.exec(input);
    if(r){
        if(r[1] && r[2]){
            //时间运算
            var dt = new Date();
            var v = Number(r[2]);
            if(r[1] === '-'){
                v = -v;
            }
            if("yyyy" === type){
                dt.setFullYear(dt.getFullYear() + v);
            }else if("yyyy-MM" === type || "yyyyMM" === type){
                dt.setMonth(dt.getMonth() + v);
            }else if("yyyy-MM-dd" === type || "yyyy-MM-dd") {
                dt.setDate(dt.getDate() + v);
            }else{
                dt.setSeconds(dt.getSeconds() + v);
            }
            return formatDate(dt, type);
        }else{
            //当前时间
            return formatDate(new Date(), type);
        }
    }else{
        return input;
    }
}

/**
 * 在组件 mounted 时，获取组件参数
 * @param {*} comp
 * @param {*} pageInfo
 */
export const setCompDefValue = (comp, pageInfo)=>{
    if(comp.compParams && pageInfo){
      $(comp.compParams).each((a, b)=>{
        if(b.usetype ==='param'){
           if(b.linkparam){
                //需要区分between(关联到时间区间) 和 非 between
                $(pageInfo.globalParams).each((c, d)=>{
                    if(d.id + (b.type=='between'?"_start":"") == b.linkparam){
                        if(d.selval){
                            b.val = d.selval.value;
                        }else{
                            b.val = parserDefvalue(d);
                        }
                        //return false;
                    }
                    if(b.type=='between' && (d.id + "_end" == b.linkparam2)){
                        if(d.selval2){
                            b.val2 = d.selval2.value;
                        }else{
                            b.val2 = parserDefvalue2(d);
                        }
                        //return false;
                    }

                    //下面处理 between 关联到两个参数 的情况
                    if(b.type === 'between' && b.linkparam == d.id){
                        if(d.selval){
                            b.val = d.selval.value;
                        }else{
                            b.val = parserDefvalue(d);
                        }
                    }
                    if(b.type === 'between' && b.linkparam2 == d.id){
                        if(d.selval2){
                            b.val2 = d.selval2.value;
                        }else{
                            b.val2 = parserDefvalue2(d);
                        }
                    }
                });

           }
          /**
           if(b.linkparam2 && b.type === 'between'){
            $(pageInfo.globalParams).each((c, d)=>{
              if(d.id + (b.type==='between'?'_end':'') == b.linkparam2){
                if(d.selval){
                  b.val2 = d.selval2.value;
                }else{
                  b.val2 = parserDefvalue(d);
                }
                return false;
              }
            });
          }
          **/
        }
      });
    }
}

//仪表盘组件联动（链接）
export const compLink = (comp, val, key, ts) => {
    let pageInfo = ts.pageInfo;
    let isok = true;
    let link = comp.type === 'chart' ? comp.comp.chartJson.link : comp.comp.link;
    if(!link){
        isok = false;
        return isok;
    }
    var ids = link.target;
    if(ids) {  //链接到组件
        var compIds = ids.split(",");
        $(compIds).each(function (a, b) {
            var c = pageInfo.comps[b];
            if (c) {
                var la = c.type == 'chart' ? c.comp.chartJson.linkAccept : c.comp.linkAccept;
                if (!la) {
                    Message.error("分析图 [" + c.name + "] 未定义事件接收。");
                    isok = false;
                    return false;
                }
                la.dftval = val;
                ts.refreshComp(c.id);
                //如果联动的组件属于join，需要用join组件覆盖当前组件
                /**
                $(pageInfo.layout).each(function (a, b) {
                    if (b.id == c.id) {
                        islink = true;
                        if (b.join) {
                            tz.compView(c, "#" + b.id + " div.bi-ibox-content div.join_first");
                        } else {
                            tz.compView(c);
                        }
                        return false;
                    } else if (b.join == c.id) {
                        islink = true;
                        if (b.jointype == 'dj') {
                            tz.compView(c, "#" + b.id + " div.bi-ibox-content div.join_second");
                        } else {
                            tz.setCompTitle(b.id, c.name);
                            tz.compView(c, "#" + b.id + " div.bi-ibox-content");
                        }
                        return false;
                    }
                });
                 */
            }else{
                Message.error("分析图 [" + comp.name + "] 联动的组件已不存在。");
                isok = false;
            }
        });
    }else if(link.linkurl) {  //连接到URL
        /**
        var u = link.linkurl;
        if(u.indexOf("?") === -1){
            u += "?" + key + "=" + escape(val);
        }else{
            u += "&" + key+"="+escape(val);
        }
        //追加参数
        u += tz.getPageParams();
        $.ext3.openFUllWindow({url:u,close:true});
         */
    }
    return isok;
}
//组件关联点击返回按钮
export const compLinkBack = (comp, ts) => {
    let pageInfo = ts.pageInfo;
    let link = comp.type === 'chart' ? comp.comp.chartJson.link : comp.comp.link;
    if(!link){
        return;
    }
    var ids = link.target;
    var compIds = ids.split(",");
    $(compIds).each(function (a, b) {
        var c = pageInfo.comps[b];
        if (c) {
            deletelinkParam(c);
            ts.refreshComp(c.id);
        }
    });
}
//定义组件公共样式（仪表盘属性设置的样式）
export const calcCompStyle = (pageInfo)=>{
    let r = {};
    let s = pageInfo.style;
    if(s && s.compBgColor){
        r['background-color'] = s.compBgColor;
    }
    if(s && s.compBgImage){
        r['background'] = "url('" + baseUrl + 'bigscreen/' + s.compBgImage +"') no-repeat 0 0";
        r['background-attachment'] = "fixed";
        r['background-size'] = "100% 100%";
    }
    if(s && s.compBorderWidth >= 0){
        r['border-width'] = s.compBorderWidth+"px";
    }
    if(s && s.compBorderStyle){
        r['border-style'] = s.compBorderStyle;
    }
    if(s && s.compBorderColor){
        r['border-color'] = s.compBorderColor;
    }
    if(s && s.useShadow == true){
        r["box-shadow"] = "0 2px 5px 0 rgba(0, 0, 0, 0.16), 0 2px 10px 0 rgba(0, 0, 0, 0.12)";
    }
    if( s && s.radius > 0){
        r['border-radius'] = s.radius + "px";
    }
    return r;
}
//定义组件自己的演示（组件属性中定义的样式，需要覆盖公共样式）
export const calcCompOwnerStyle = (style, comp)=>{
    var cs = comp.style || {};
    let o = style; //已经存在的样式
    if(cs.disableBg == true){
        o['background'] = "none";
        o['background-image'] = "none";
        o['box-shadow'] = "none";
        o['border'] = "none";
    }else{
      if(cs.pageBgColor){
        o['background-color'] = cs.pageBgColor;
      }
      if(cs.compBgImage){
        o['background-image'] = "url("+baseUrl+"bigscreen/"+cs.compBgImage+")";
        o['background-size'] = "100% 100%";
        //o['background-position'] = "center";
        o['background-repeat'] = "no-repeat";
      }
    }
    if(cs.useShadow===true){
     o["box-shadow"] = "0 2px 5px 0 rgba(0, 0, 0, 0.16), 0 2px 10px 0 rgba(0, 0, 0, 0.12)";
    }
    if(cs.compBorderColor){
        o['border-color'] = cs.compBorderColor
    }
    if(cs.compBorderWidth != null){
        o['border-width'] = cs.compBorderWidth+"px";
    }
    if(cs.compBorderStyle){
        o['border-style'] = cs.compBorderStyle;
    }
    if( cs.radius > 0){
        o['border-radius'] = cs.radius + "px";
    }
    return o;
}
export const calcTitleStyle = (comp, pageInfo)=>{
    let r = {};
    let s = pageInfo.style;
    if(s && s.compFontSize){
        r['font-size'] = s.compFontSize + "px";
    }
    if(s && s.compFontColor){
        r['color'] = s.compFontColor;
    }
    let cs = comp.style;
    if(cs){
        if(cs.titleAlign){
            r['text-align'] = cs.titleAlign;
        }
        if(cs.titleSize){
            r['font-size'] = cs.titleSize+"px";
        }
        if(cs.titleBold == true){
            r['font-weight'] = "bold";
        }
        if(cs.titleColor){
            r['color'] = cs.titleColor;
        }
        if(cs.titleBorderColor){
            r['border-color'] = cs.titleBorderColor;
        }
        if(cs.titleBorderWidth){
            r['border-width'] = cs.titleBorderWidth;
        }
        if(cs.titleBorderStyle){
            r['border-style'] = cs.titleBorderStyle;
        }
        if(cs.disableHeadBg === true){
            r['background'] = "none";
            r['background-image'] = "none";
        }else{
            if(cs.titleBgColor){
                r['background-color'] = cs.titleBgColor;
            }
            /**
            if(cs.compBgImage){
                r['background-image'] = "url("+baseUrl+"bigscreen/"+cs.compBgImage+")";
                 r['background-size'] = "100% 100%";
                 r['background-position'] = "center";
                r['background-repeat'] = "no-repeat";
            }
             */
        }
    }
    return r;
}
export const calcPageStyle = (pageInfo) => {
    let r = {};
    let s = pageInfo.style;
    if(s && s.pageBgColor){
        r['background-color'] = s.pageBgColor;
    }
    if(s && s.pageBgImage){
        r['background-image'] = "url(" + baseUrl + 'bigscreen/' + s.pageBgImage +")";
        r['background-repeat'] = "no-repeat";
        //r['background-attachment'] = "fixed";
        r['background-size'] = "100% 100%";
    }
    return r;
}

export const sortComps = (a, b)=>{
        var c1 = a.x;
        var r1 = a.y;
        var c2 = b.x;
        var r2 = b.y;

        if (r1 == r2) {
            return c1 - c2;
        } else {
            return r1 - r2;
        }

}
//计算堆积组件的高度
export const calcDjHeight = (item, compId, pageInfo) => {
    let ret = {};
    let comp = pageInfo.comps[compId];
    if(comp.joinpos){
        ret['height'] = (comp.joinpos * 10) + "%";
    }else{
        //没有取默认值
        let s = 1 + item.joins.length;
        ret['height'] = Math.round(100 / s) + "%";
    }
    return ret;
}
//计算堆积组件的宽度
export const calcDjWidth = (item, compId, pageInfo)=>{
    let comp = pageInfo.comps[compId];
    if(comp.joinpos){
        return Math.round(comp.joinpos * 2.4);
    }
    //没有取默认值
    let s = 1 + item.joins.length;
    return Math.round(24 / s);
}
/**
 * 获取全局参数默认值（如果默认值是从数据库里获取数据，需要先获取值）
 */
export const calcParamDefval = (pageInfo, token, pms, cb)=>{
    var q = {};
    let exist = false;
    $(pageInfo.globalParams).each(function(a, b){
        if(b.defvalueTableId && b.defvalueTableCol && b.defvaltype === 'dtz'){
            q[b.id] = ({paramId:b.id, tid:b.defvalueTableId, col:b.defvalueTableCol});
            exist = true;
        }
    });
    //给参数赋值上个页面的参数
    if(pms){
        $(pageInfo.globalParams).each((a, b)=>{
            let v = pms[b.id];
            if(v){
                if(b.type == 'datetime'){  //时间区间
                    b.selval = {value:v[0], name:v[0]};
                    b.selval2 = {value:v[1], name:v[1]};
                }else{
                    b.selval = {value:v, name:v};
                }
            }
        });
    }
    if(exist == false){
        queryFirstRow(pageInfo, token, cb);
    }else{
        ajax({
            url:"dashboard/"+(token?"share/":"")+"queryParamDefvalue.action?token="+(token?token:""),
            data:JSON.stringify(q),
            dataType:"JSON",
            type:"POST",
            postJSON:true,
            success:function (resp) {
                 //设置默认值(动态值)
                let pms = resp.rows;
                $(pageInfo.globalParams).each((c, d) => {
                    if(d.defvalueTableId && d.defvalueTableCol && d.defvaltype === 'dtz'){
                        if(pms[d.id]){
                            d.defvalue = pms[d.id];
                        }
                    }
                });
                queryFirstRow(pageInfo, token, cb);
            }
        });
    }
}
//参数设置 （第一行设为下拉框的默认值）后， 通过此方法获取值
export const queryFirstRow = (pageInfo, token, cb)=>{
    if(!pageInfo.globalParams){
        cb();
        return;
    }
    let firstValuePms = pageInfo.globalParams.filter(m=>(m.type === 'select' || m.type === 'mselect') && m.defFirstValue == true );
    if(firstValuePms.length > 0){
        let u = "etl"+(token?"/share":"")+"/queryMultiTablesDataAsJson.action"+(token?"?token="+token:"");
        let p = [];
        $(firstValuePms).each((a, b)=>{
            if(b.valtype ==='dynamic'){ //动态值
                let o = {
                    tableId: b.tid,
                    text: b.text,
                    value: b.value,
                    paramId : b.id,
                    order: b.order,
                };
                if((b.type === 'select' || b.type === 'mselect') && b.cascade){ //有及联
                    $(pageInfo.globalParams).each((c, d)=>{
                        if(d.id == b.cascade){
                            o.parentCol = d.value;
                            if(d.selval){
                                o.parentVal = d.selval.value;
                            }else{
                                o.parentVal = d.defvalue;
                            }
                            return false;
                        }
                    });
                }
                p.push(o)
            }
        });
        ajax({
            url:u,
            data:JSON.stringify(p),
            postJSON:true,
            type:"POST",
            success:(r)=>{
                for(let p in r.rows){
                    let param = firstValuePms.filter(m=>m.id === p)[0];
                    let vls = r.rows[p].map(m=>{
                        let v = m[param.value];
                        if(!v){
                            v = m[param.value.toUpperCase()];
                        }
                        if(!v){
                            v = m[param.value.toLowerCase()];
                        }
                        if(!v){
                            throw new Error(JSON.stringify(param)+" 未获取到值。");
                        }
                        return v;
                    });
                    if(param.type === 'select'){
                       param.selval = {value: vls[0], name: vls[0]};
                    }else if(param.type === 'mselect'){
                       param.selval = {value: vls[0], name: vls[0]};
                       param.selval2 = {value: vls[0], name: vls[0]};
                    }
               }
               cb();
            }
        });
    }else{
        cb();
    }
}
//获取仪表盘页面所有参数
export const getPageParams = (pageInfo)=>{
    let ret = {};
    if(pageInfo.globalParams){
        //selval 表示参数选择值， defval 表示参数默认值
        $(pageInfo.globalParams).each((a, b)=>{
            if(b.type === 'datetime'){  //时间区间, 以数组形式传递
                if(b.selval && b.selval2){
                    ret[b.id] = [b.selval.value, b.selval2.value];
                }else if(b.defvalue){
                    ret[b.id] = parserDefvalue(b);
                }
            }else{
                if(b.selval){
                    ret[b.id] = b.selval.value;
                }else if(b.defvalue){
                    ret[b.id] = parserDefvalue(b);
                }
            }
        });
    }
    return ret;
}
