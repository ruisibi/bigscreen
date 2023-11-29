/**
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
/**
 * 大屏utils类
 */
import $ from "jquery";
import * as utils from '@/view/dashboard/Utils'
import {baseUrl, ajax, formatDate} from '@/common/biConfig';

//默认值
const baseInfo = {
    width:800,
    height:460,
    bl:0,
    styleType:"bigscreen",
};
const pageBgColor = "#0d1227";
const pageWhiteBgColor = "#ffffff";
const fonts = [{
    ch: '宋体',
    en: 'SimSun'
}, {
    ch: '黑体',
    en: 'SimHei'
}, {
    ch: '微软雅黑',
    en: 'Microsoft Yahei'
}, {
    ch: '微软正黑体',
    en: 'Microsoft JhengHei'
}, {
    ch: '楷体',
    en: 'KaiTi'
}, {
    ch: '新宋体',
    en: 'NSimSun'
}, {
    ch: '仿宋',
    en: 'FangSong'
}];
const paramTypes = [{label:"单选框", value:"select"},{label:"多选框", value:"mselect"},{label:"输入框", value:"input"},{label:"树形框", value:"tree"},{label:"时间", value:"onedate"},{label:"时间区间", value:"datetime"}];
export {
    baseInfo, pageBgColor, pageWhiteBgColor, fonts, paramTypes
}
export const findCompById = (id, pageInfo)=>{
    var ret = null;
    $(pageInfo.comps).each(function (a, b){
        if(b.id === id){
            ret = b;
            return false;
        }
    });
    return ret;
}
export const compExistInSelect = (compId, mulitComps)=>{
    let ret = false;
    $(mulitComps).each((a, b)=>{
        if(b.id === compId){
            ret = true;
            return false;
        }
    });
    return ret;
}

export const createTextStyle = (comp)=>{
    let style = {};
    if(comp.style){
        if(comp.style.color){
            style['color'] = comp.style.color;
        }
        if(comp.style.size){
            style['font-size'] = comp.style.size+"px";
        }
        if(comp.style.align){
            style['text-align'] = comp.style.align;
        }
        if(comp.style.bold == true){
            style['font-weight'] = "bold";
        }
        if(comp.style.italic == true){
            style['font-style'] = 'italic';
        }
        if(comp.style.underline == true){
            style['text-decoration'] = 'underline';
        }
        if(comp.style.lineheight){
            style['line-height'] = comp.style.lineheight+"px";
        }
        if(comp.style.color1 && comp.style.color2){
            style['background-image'] = 'linear-gradient(180deg, '+comp.style.color1+' 0%, '+comp.style.color2+' 100%)';
            style['-webkit-background-clip'] = 'text';
            style['color'] = 'transparent';
        }
        if(comp.style.family){
            style['font-family'] = "'"+comp.style.family + "'";
        }
        if(comp.style.bgcolor){
            style['background-color'] = comp.style.bgcolor;
        }
        if(comp.style.spacing){
            style['letter-spacing'] = comp.style.spacing + "px";
        }
    }
    return style;
}

export const createDecorateStyle = (comp, s) => {
    if(!comp.style){
        return s;
    }
    if(comp.style.compBorderColor){
        s["border-color"] = comp.style.compBorderColor;
    }
    if(comp.style.compBorderWidth || comp.style.compBorderWidth == 0){
        s["border-width"] = comp.style.compBorderWidth+"px";
    }
    if(comp.style.compBorderStyle){
        s["border-style"] = comp.style.compBorderStyle;
    }
    if (comp.style.opacity) {
        s["opacity"] = (comp.style.opacity / 100);
    }
    if(comp.style.bgcolor){
        s["background-color"] = comp.style.bgcolor;
    }
    if(comp.style.borderRadius){
        s["border-radius"]  = comp.style.borderRadius + "px";
    }
    if(comp.style.useShadow == true){
        s["box-shadow"] = "0 2px 5px 0 rgba(0, 0, 0, 0.16), 0 2px 10px 0 rgba(0, 0, 0, 0.12)";
    }
    if(comp.style.rotate){
        let t = s['transform'];
        if(t){
            s['transform'] = t +  ' rotate(-'+comp.style.rotate+'deg)';
        }else{
            s['transform'] = 'rotate(-'+comp.style.rotate+'deg)';
        }
    }
    if(comp.style.zoom){
        let t = s['transform'];
        if(t){
            s['transform'] = t + " scale("+(100 - comp.style.zoom)/100+", "+(100 - comp.style.zoom)/100+")";
        }else{
            s['transform'] = "scale("+(100 - comp.style.zoom)/100+", "+(100 - comp.style.zoom)/100+")";
        }
    }
    if(comp.style.bgcolor1 && comp.style.bgcolor2){
        s['background'] = 'linear-gradient(90deg, '+comp.style.bgcolor1+' 0%, '+comp.style.bgcolor2+' 100%)';
    }
    return s;
}

export const createTableBorderStyle = (comp, style) => {
    if(!comp.tstyle){
        return style;
    }
    if(comp.tstyle.compBorderColor){
       style['border-color'] = comp.tstyle.compBorderColor;
    }
    if(comp.tstyle.compBorderWidth >= 0){
        style['border-width'] = comp.tstyle.compBorderWidth+"px";
    }
    if(comp.tstyle.compBorderStyle){
        style['border-style'] = comp.tstyle.compBorderStyle;
    }
    return style;
}
/**
 * 在用户操作大屏时记录日志，用来实现撤销功能。
 * @param {*} userOpers
 * @param {*} obj
 */
export const writeOperLogs = (userOpers, obj) => {
    if(userOpers == null){
        return;
    }
    if(userOpers.length >= 20){  //最大存储50步操作记录
        userOpers.pop();
    }
    userOpers.unshift(obj);
}
/**
 * 回滚用户操作
 * @param {*} userOpers
 * @param {*} ts
 */
export const rollbackOper = (userOpers, ts) =>{
    if(userOpers.length == 0){
        ts.$notify.error("无可撤销数据.");
        return;
    }
    let obj = userOpers.shift();
    let comp = obj.comp;
    let oper = obj.oper;
    if(oper === 'resize' || oper === 'move'){
        let options = obj.options;
        if(oper === 'resize'){
            comp.height = options.height;
            comp.width = options.width;
        }else if(oper === 'move'){
            comp.left = options.left;
            comp.top = options.top;
        }
        if(oper === 'resize'){  //更新组件大小
            if(comp.type === 'chart'){
                //更新图形大小
                var o = document.getElementById("ct_"+comp.id);
                if(!o){
                    return;
                }
                var chart = echarts.getInstanceByDom(o);
                if(chart){
                    $("#ct_"+comp.id).height(comp.height).width(comp.width);
                    chart.resize(comp.width, comp.height);
                }
            }else if(comp.type === 'table' || comp.type ==='grid'){
                ts.$refs['mv_'+comp.id].computeTableHeight();
            }
        }
    }else if(oper === 'addComp'){
        ts.removeComp(comp.id, false, false);
    }else if(oper === 'zindex'){
        let opt = obj.options;
        ts.setCompZindex(opt.id, opt.zIndex);
    }else if(oper === 'deletecomp'){
        ts.removeComp(comp.id, true , false);
    }else if(oper === 'boxpropset'){
        let options = obj.options;
        options.ts.goback(options);
    }else if(oper === 'textpropset'){
        let options = obj.options;
        options.ts.goback(options);
    }else if(oper === 'decopropset'){
        let options = obj.options;
        options.ts.goback(options);
    }else if(oper === 'picpropset'){
        let options = obj.options;
        options.ts.goback(options);
    }else if(oper === 'pagepropset'){
        let options = obj.options;
        options.ts.goback(options);
    }else if(oper == 'date' || oper == 'weather' || oper == 'iframe' || oper == 'tab' || oper == 'param' || oper == 'dupont'){
        let options = obj.options;
        options.ts.goback(options);
    }
}
//根据tab组件点击事件，设置需要隐藏的组件,
//tabCompId 表示 选项卡组件的ID
//idx 表示当前选中是哪个tab, 默认是 0
export const tabClickEvent = (pageInfo, tabCompId, idx) =>{
    //先获取最大z-index
    let maxzIndex = 0;
    $(pageInfo.comps).each((a, b)=>{
        if(b.zIndex > maxzIndex){
            maxzIndex = b.zIndex;
        }
    });
    let ret = [];
    $(pageInfo.comps).each((a, b)=>{
        if(b.type == 'tab' && b.id == tabCompId){
            if(b.tabs){
                //把除了选中的选项卡，其他选项卡关联组件都隐藏
                $(b.tabs).each((c, d)=>{
                    //先移除组件 isTabHide 字段
                    if(d.joins){
                        $(d.joins).each((e, f)=>{
                            let comp = findCompById(f, pageInfo);
                            if(comp){
                                delete comp.isTabHide;
                            }
                        });
                    }
                    if(c != idx){
                        if(d.joins){
                            $(d.joins).each((e, f)=>{
                                let comp = findCompById(f, pageInfo);
                                if(comp){
                                    comp.isTabHide = true;
                                }
                            });
                        }
                    }else{
                        if(d.joins){
                            $(d.joins).each((e, f)=>{
                                let comp = findCompById(f, pageInfo);
                                if(comp){
                                   ret.push(comp);
                                   //设 zIndex 让组件可视
                                   comp.zIndex = maxzIndex + 1;
                                }
                            });
                        }
                    }
                });
            }
        }
    });
    return ret;
}
//根据tab组件，设置需要隐藏的组件,
export const initTabHideEvent = (pageInfo) =>{
    $(pageInfo.comps).each((a, b)=>{
        if(b.type == 'tab'){
            if(b.tabs){
                //把除了第一个选项卡后，其他选项卡关联组件都隐藏
                $(b.tabs).each((c, d)=>{
                    if(c != 0){
                        if(d.joins){
                            $(d.joins).each((e, f)=>{
                                let comp = findCompById(f, pageInfo);
                                if(comp){
                                    comp.isTabHide = true;
                                }
                            });
                        }
                    }
                });
            }
        }
    });
    return pageInfo;
}

//通过参数刷新所有组件
export const flushCompsByPms = (param, pageInfo, cb)=>{
    for(let o in pageInfo.comps){
        let comp = pageInfo.comps[o];
        if(comp.type == 'param'){
            continue;
        }
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
            utils.deletelinkParam(comp);
            //刷新组件的回调函数
            if(cb){
                cb(comp);
            }
        }
    }
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
                b.val2 = utils.getParamselval(gp.selval2);
            }else{
                b.val2 = utils.parserDefvalue2(gp);
            }
        } else {
            if(gp.selval){
                b.val = utils.getParamselval(gp.selval2);
            }else{
                b.val = utils.parserDefvalue(gp);
            }
        }
        return true;
    } else {
        if(isparam2===true){  //between的第二个参数
            if(gp.selval){
                b.val2 = utils.getParamselval(gp.selval);
            }else{
                b.val2 = utils.parserDefvalue(gp);
            }
        }else{
            if(gp.selval){
                b.val = utils.getParamselval(gp.selval);
            }else{
                b.val = utils.parserDefvalue(gp);
            }
        }
        return true;
    }

    return false;
}

export const findGlobalParamById = (id, pageInfo) =>{
    let ret = null;
    for(let o in pageInfo.comps){
        let comp = pageInfo.comps[o];
        if(comp.type == 'param' && comp.id == id){
           ret = comp.comp;
           break;
        }
    }
    return ret;
}
/**
 * 组件是否在tab 关联组件中, 并返回tab
 * @param {*} pageInfo
 * @param {*} compId
 */
export const compInTabs = (pageInfo, compId) =>{
    let tab = null;
    $(pageInfo.comps).each((a, b)=>{
        if(b.type == 'tab'){
            if(b.tabs){
                //把除了第一个选项卡后，其他选项卡关联组件都隐藏
                $(b.tabs).each((c, d)=>{
                    if(d.joins){
                        $(d.joins).each((e, f)=>{
                            if(f == compId){
                                tab = b;
                                return false;
                            }
                        });
                    }
                });
            }
        }
        if(tab){
            return false;
        }
    });
    return tab;
}

/**
 * 初始化参数默认值
 * @param {*} pageInfo
 */
export const calcParamDefval = (pageInfo, token)=>{
    //把参数放入 pageInfo 的 globalParams 中，方便 组件调用
    let globalParams = [];
    $(pageInfo.comps).each((a, b)=>{
        if(b.type == 'param'){
            let param = b.comp;
            globalParams.push(param);
        }
    });
    pageInfo.globalParams = globalParams;
    let firstValuePms = globalParams.filter(m=>(m.type === 'select' || m.type === 'mselect') && m.defFirstValue == true );
    if(firstValuePms.length > 0){
        let u = "etl"+(token?"/share":"")+"/queryMultiTablesDataAsJson.action"+(token?"?token="+token:"");
        let p = [];
        $(firstValuePms).each((a, b)=>{
            if(b.valtype ==='dynamic'){ //动态值
                let o = {
                    tableId: b.tid,
                    text: b.text,
                    value: b.value,
                    paramId : b.paramid,
                    order: b.order,
                };
                if((b.type === 'select' || b.type === 'mselect') && b.cascade){ //有及联
                    $(globalParams).each((c, d)=>{
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
            async:false,  //由于参数执行顺序的问题，需要等ajax执行完后再渲染组件，所以必须 同步
            success:(r)=>{
                for(let p in r.rows){
                    let param = firstValuePms.filter(m=>m.paramid === p)[0];
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
            }
        });
    }
}
