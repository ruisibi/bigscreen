<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 布局器 -->
<script>
import { baseUrl, newGuid } from "@/common/biConfig";
import ChartDailog from '@/view/portal/ChartDailog.vue'
import ChartSeriesColor from '@/view/portal/ChartSeriesColor.vue'
import CompFilter from '@/view/portal/CompFilter.vue'
import CompEvent from '@/view/portal/CompEventDailog.vue'
import $ from "jquery";
import * as utils from '@/view/portal/Utils'
import BoxView from "@/view/portal/view/Box.vue"
import ChartView from "@/view/portal/view/Chart.vue"
import GridView from "@/view/portal/view/Grid.vue"
import GridTranspositionView from '@/view/portal/view/GridTransposition.vue'
import TableView from "@/view/portal/view/Table.vue"
import ImageView from "./../view/Image.vue"
import DetailView from "@/view/portal/view/Detail.vue"
import boxTypes from '@/view/portal/prop/BoxTypes'
import picUpload from '@/view/portal/prop/PicUpload'
import * as bsUtils from './../bsUtils'
import CompLink from '@/view/dashboard/CompLink'
import DateView from '@/view/bigscreen/view/Date'
import WeatherView from '@/view/bigscreen/view/Weather'
import IframeView from '@/view/bigscreen/view/Iframe'
import TabView from '@/view/bigscreen/view/Tab'
import TabInfoDialog from './../TabInfoDialog'
import LineView from '@/view/bigscreen/view/Line'
import TextView from '@/view/bigscreen/view/Text'
import ParamView from '@/view/bigscreen/view/Param'
import ParamSet from '@/view/bigscreen/data/Param'
import CustomView from '@/view/bigscreen/view/Custom'
import InsertComp from './InsertComp'
import * as echartsUtils from '@/common/echartsUtils'

export default {
  components: {
    BoxView,
    ChartView,
    GridView,
    GridTranspositionView,
    TableView,
    ChartDailog,
    CompFilter,
    ChartSeriesColor,
    CompEvent,
    boxTypes,
    ImageView,
    picUpload,
    DetailView,
    CompLink,
    DateView,
    WeatherView,
    IframeView,
    TabView,
    TabInfoDialog,
    LineView,
    TextView,
    ParamView,
    ParamSet,
    InsertComp,
    CustomView,
  },
  props: {
    pageInfo: {
      type: Object,
      required: true,
      default:{}
    },
    userOpers: {
      type: Array,
      required: true,
      default:[]
    },
  },
  render(h) {
    //大屏样式
    let style = this.pageInfo.style;
    let s = {};
    s.width = style.width + "px";
    s.height = style.height + "px";
    if(style.pageBgColor){
      s['background-color'] = style.pageBgColor;
    }else{
      let color = null;
      if(style.styleType==='bigscreen'){
        color = bsUtils.pageBgColor;
      } else {
        color = bsUtils.pageWhiteBgColor;
      }
      s['background-color'] = color;
    }
    if(style.pageBgImg){  //背景图
      if(style.pageBgImg.startsWith('http')){
        s['background-image'] =  "url('" + style.pageBgImg +"')";
      }else{
        s['background-image'] =  "url('" + baseUrl + 'bigscreen/' + style.pageBgImg +"')";
      }
    }
    var scale = 1 + 1 *  (style.bl / 100);
    s['transform'] = "scale("+(scale)+", "+(scale)+")";

    //大屏组件
    let comps = [];
    if(this.pageInfo.comps){
      $(this.pageInfo.comps).each((a, b) => {
        let s = this.createCompStyle(b);
        if(this.pageInfo.style.styleType === 'bigscreen'){
          s['color'] = "white";
        }else{
          s['color'] = "#201e1e";
        }
        //判断是否选中
        let selectClassName = "";
        if(bsUtils.compExistInSelect(b.id, this.mulitComps)){
          selectClassName += " compselect";
        }
        if(this.hoverObj && this.hoverObj.id === b.id){
          selectClassName+= " comphover";
        }
        //处理删除标记 和 选项卡关联组件
        //console.log(b.title+":"+b.isTabHide);
        if(b.isdelete == true || b.isTabHide == true){
          comps.push(h('div', {style:{display:"none"}}));
        }else{
          comps.push(h('div', {class:"comp_style",style:s,attrs:{id: "c_" + b.id,cid:b.id}, on:{
            click:()=>this.compClick(window.event||$event, b),
            mouseover:()=>this.mouseovercomp(b),
            mouseleave:()=>this.mouselevelcomp(b),
            contextmenu:()=>this.compRightMenus(b),
            }}, [h('div', {class:"comp_item", attrs:{id:this.useIn+b.id}}, [this.createComp(b, h)]), h('div', {class:"comp_mask"+selectClassName})]));
        }
      })
    }

    return h('div',{class:"bs-warpper"},
    [h('div', {class:"layout-center-body", attrs:{id:"container"}, on:{click:()=>{this.bodyEvent(window.event||$event)}}},
      [h('div', {class:"opeareastyle", style:s, attrs:{id:"optarea"}}, comps)]
    ),
      h('ChartDailog', {props:{useIn:this.useIn},ref:"chartDailogForm"}),
      h('CompFilter', {ref:"compFilterForm", props:{pageInfo:this.pageInfo, useIn:this.useIn}}),
      h('comp-link', {ref:"compLinkForm"}),
      h('tab-info-dialog', {ref: "tabInfoForm", props:{pageInfo:this.pageInfo}}),
      h('param-set', {ref:"paramForm", props:{pageInfo:this.pageInfo}}),
      h('insert-comp', {ref:"insertCompForm"})
    ]
   );
  },
  data() {
    return {
      mulitComps:[], //选中的组件，可以是多个
      hoverObj:null, //当前hover对象
      useIn:"bigscreen",
    };
  },
  mounted() {
    //this.toCenter();
    this.compDropEvent();
  },
  beforeDestroy(){

  },
  computed: {},
  methods: {
    setUpdate(){
      this.$parent.isupdate = true;
    },
    /*移入布局器的中间*/
    toCenter(){
        var w = $(".bs-warpper").width();
        var h = $(".bs-warpper").height();
        var cd = 3000;
        $(".bs-warpper").scrollLeft(cd/2 - w/2).scrollTop(cd/2 - h / 2);
    },
    createCompStyle(comp){
      let s = {};
      s['top'] = comp.top + "px";
      s['left'] = comp.left + "px";
      s['width'] = comp.width + "px";
      s['height'] = comp.height + "px";
      s['z-index'] = comp.zIndex;
      return s;
    },
    //创建组件渲染对象
    createComp(comp, h){
      if(comp.type === 'text'){
         let s = {"overflow":"hidden"};
         return h('div', {class:"comp_body", style:s }, [h('text-view',{ref:'mv_'+comp.id, key: "k_" + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, editor:true}})]);
      }else if(comp.type === 'pic'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('image-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type === 'decorate'){
        let style = {"border-color":"#dee5e7", "border-width":"1px", "border-style":"solid"};
        if(this.pageInfo.style && this.pageInfo.style.styleType === 'def'){
          style['border-color'] = "#2a2e33";
        }
        let s = bsUtils.createDecorateStyle(comp, style);
        return h('div', {class:"comp_body", style:s});
      }else if(comp.type === 'box'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('box-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, editor:true}})]);
      }else if(comp.type === 'chart'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('chart-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type === 'grid'){
        let style = {"border-color":"#e7eaec", "border-width":"1px", "border-style":"solid"};
        if(this.pageInfo.style.styleType === 'def'){
          style['border-color'] = "#DCDFE6";
        }
        let s = bsUtils.createTableBorderStyle(comp, style);
        let label = comp.comp.transposition == true ? "grid-transposition-view" : "grid-view";
        return h('div', {class:"comp_body", style:s}, [h(label,{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, editor:true}})]);
      }else if(comp.type === 'table'){
        let style = {"border-color":"#e7eaec", "border-width":"1px", "border-style":"solid"};
        if(this.pageInfo.style.styleType === 'def'){
          style['border-color'] = "#DCDFE6";
        }
        let s = bsUtils.createTableBorderStyle(comp, style);
        return h('div', {class:"comp_body", style:s}, [h('table-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, editor:true}})]);
      }else if(comp.type === 'detail'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('detail-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, editor:true}})]);
      }else if(comp.type === 'date'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('date-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type === 'weather'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('weather-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type === 'iframe'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('iframe-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type === 'tab'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('tab-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type == 'line'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('line-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type == 'param'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('param-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }else if(comp.type == 'custom'){
        let style = {};
        return h('div', {class:"comp_body", style:style}, [h('custom-view',{ref:'mv_'+comp.id, key:'k_' + comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn,  editor:true}})]);
      }
    },
     //布局器接受组件拖拽事件
    compDropEvent(){
      var ts = this;
      $("#optarea").droppable({
          accept: "#comptree .jstree-node",
          tolerance: "pointer",
          over: function (e, ui) {
              $(ui.helper[0]).find("span").removeClass("glyphicon-remove").addClass("glyphicon-ok");
          },
          out: function (e, ui) {
              $(ui.helper[0]).find("span").removeClass("glyphicon-ok").addClass("glyphicon-remove");
          },
          drop: function (e, ui) {
              var ref = $("#comptree").jstree(true);
              var node = ref.get_node(ui.draggable[0]);
              //计算位置
              var top = ui.offset.top;
              var left = ui.offset.left;
              var picLeft = $("#optarea").offset();
              top = top - picLeft.top;
              left = left - picLeft.left;
              //计算放大/缩小比例
              if(ts.pageInfo.style.bl) {
                  var bl = ts.computeBl();
                  left = left * bl;
                  top =  top *  bl;
              }
              var zidx = ts.pageInfo.zIndex;
              if(!zidx){
                  zidx = ts.pageInfo.zIndex = 1;
              }else{
                  zidx = ts.pageInfo.zIndex = zidx + 1;
              }
              //创建JSON, 组件json 信息放 comp 里面
              let id = newGuid();
              var json = { width:90, height:90, top:top, left:left, zIndex:zidx, title:node.text, id: id, type:node.li_attr.tp, comp:{id:id}};
              if(json.type ==='box'){
                  json.style = {kpiBg:"true"};  //模式数字设置背景色
              }
              if(json.type ==='text') {
                  json.content = node.text;
                  json.height = 40;
                  delete json.comp;
              }
              if(json.type ==='chart'){  //初始化图形数据
                  json.width = 220;
                  json.height = 160;
                  json.comp.chartJson = {"type":'line', typeIndex:1, xcol:{}, ycol:{}, scol:{}};
                  json.comp.kpiJson = [null, null, null];
              }else if(json.type === 'table' || json.type === 'grid' || json.type ==='detail'){
                  json.width = 220;
                  json.height = 160;
              }else if(json.type == 'date' || json.type == 'weather'){
                  json.width = 220;
                  json.height = 40;
              }else if(json.type == 'tab'){ //选项卡，默认2个选项卡
                  json.width = 240;
                  json.height = 40;
                  json.tabs = [{
                    name:"tab1",
                  },{
                    name:"tab2"
                  },{
                    name:"tab3"
                  }];
              }else if(json.type == 'line'){
                json.width = 240;
                json.height = 20;
              }else if(json.type == 'param'){
                json.width = 300;
                json.height = 30;
              }
              //加入pageInfo
              if(!ts.pageInfo.comps){
                  ts.pageInfo.comps = [];
              }
              ts.pageInfo.comps.push(json);
              ts.setUpdate();
              ts.$forceUpdate();
              ts.$parent.updateLayer();
              //写操作日志
              bsUtils.writeOperLogs(ts.userOpers, {comp:json, oper:'deletecomp'});
              ts.$nextTick(()=>ts.compDragResizeEvent(json));
          }
      });
    },
    initCompsDragResizeEvent(){
       //注册组件的resize/move事件
        $(this.pageInfo.comps).each((a, b)=>{
          this.compDragResizeEvent(b);
        });
    },
     //组件拖拽/resize/选中事件
    compDragResizeEvent(comp){
        //drag事件
        var ts = this;
        var stx = null;
        var sty = null;
        $("#c_"+comp.id).draggable({
            //containment: '#container',
            scroll: false ,
            start: function(event, ui) {
                stx =ui.position.left;
                sty = ui.position.top;
                //ui.position.left = 0;
                //ui.position.top = 0;
                if(ts.mulitComps.length == 1){
                  ts.mulitComps = [];
                }
            },
            drag: function(event, ui) {
                //计算移动距离
                var betweenx = ui.position.left - stx;
                var betweeny = ui.position.top - sty;
                stx = ui.position.left;
                sty = ui.position.top;
                //重置位置
                var bl = ts.computeBl();
                ui.position.left = ui.position.left * bl;
                ui.position.top = ui.position.top * bl;

                ts.setUpdate();
                //判断 mulitComps，如果是多选也要移动其他组件位置
                $(ts.mulitComps).each(function (a, b) {
                    if (b.id === comp.id) {
                        return true;
                    }
                    var mp = $("#c_"+b.id).position();
                    var mx = mp.left * bl + betweenx * bl;
                    var my = mp.top * bl + betweeny * bl;
                    b.left = mx;
                    b.top = my;
                });
            },
            stop:function(event, ui){
                var id = $(ui.helper).attr("cid");
                var comp = bsUtils.findCompById(id, ts.pageInfo);
                //记录用户操作信息
                bsUtils.writeOperLogs(ts.userOpers, {comp:comp, oper:"move", options:{ left : comp.left, top: comp.top } });
                comp.left = ui.position.left;
                comp.top = ui.position.top;
            },
        });
        //resize事件
        $( "#c_"+comp.id ).resizable({
            //containment: '#container',
            autoHide: true ,
            minHeight:20,
            minWidth:20,
            //handles:'n, e, s, w, ne, se, sw, nw',
            resize: function(event, ui) {
                var bl = ts.computeBl();
                var changeWidth = ui.size.width - ui.originalSize.width; // find change in width
                var newWidth = ui.originalSize.width + changeWidth * bl; // adjust new width by our zoomScale

                var changeHeight = ui.size.height - ui.originalSize.height; // find change in height
                var newHeight = ui.originalSize.height + changeHeight * bl; // adjust new height by our zoomScale

                ui.size.width = newWidth;
                ui.size.height = newHeight;
                ts.setUpdate();
            },
            stop:function (event, ui) {
                var id = $(ui.element).attr("cid");
                var comp = bsUtils.findCompById(id, ts.pageInfo);
                //记录用户操作信息
                bsUtils.writeOperLogs(ts.userOpers, {comp:comp, oper:"resize", options:{ height : comp.height, width: comp.width } });

                 //重新计算高度
                comp.height = ui.size.height;
                comp.width = ui.size.width;
                if(comp.type === 'chart'){
                    //更新图形大小
                    var o = document.getElementById("ct_"+id);
                    if(!o){
                        return;
                    }
                    var chart = echarts.getInstanceByDom(o);
                    if(chart){
                        $("#ct_"+id).height(ui.size.height).width(ui.size.width);
                        chart.resize(ui.size.width, ui.size.height);
                        //如果是热力图，需要修改热力图层位置
                        if(comp.comp.chartJson.typeIndex == 6){
                          let option = chart.getOption();
                          echartsUtils.heatMapSet(comp.comp, option.data, chart, o);
                        }
                    }
                }else if(comp.type === 'table' || comp.type ==='grid'){
                  ts.$refs['mv_'+id].computeTableHeight();
                }else if(comp.type == 'custom'){
                  ts.$refs['mv_'+id].resize();
                }
            }
        });
    },
    //组件属性面板
    compProp(id){
      let comp = bsUtils.findCompById(id, this.pageInfo);
      this.$parent.showPropPanel(comp);
    },
    //组件选中事件
    compClick(e, comp){
      //阻止事件冒泡
      e.stopPropagation();
      e.preventDefault();
      let ts = this;
      if(e.shiftKey){ //按住shift键可以选择多个组件
          //如果mulit中存在就取消，不存在就选中
          var exist = ts.mulitComps.filter(function(m){
              return m.id === comp.id;
          });
          if(exist.length == 0) {  //不存在就选中
              ts.mulitComps.push(comp);
              //关闭属性面板
              ts.$parent.hidePanel();
          }else{ //存在移除
              $(ts.mulitComps).each(function(a, b){
                  if(b.id === comp.id){
                      ts.mulitComps.splice(a, 1);
                      return false;
                  }
              });
          }
      }else {
         //if(ts.mulitComps.length == 1){
            //放入 mulitComps 对象中
            ts.mulitComps = [comp];
            //this.$forceUpdate();
            //打开属性框
            ts.compProp(comp.id);
         //}
      }
      return false;
    },
    //比例换行，放大后需要缩小，缩小后需要放大
    computeBl(){
      var ts = this;
      var bl = ts.pageInfo.style.bl;
      if(!bl){
          return 1;
      }
      bl = bl / 100;
      if(bl < 0){  //缩小了，需要放大
          bl = (1 / ( 1 - Math.abs(bl) ));
      }else if(bl > 0){  // 放大了，需要缩小
          bl = (1/ (1 + bl) );
      }
      return bl;
    },
    //点击空白的事件(container区域)
    bodyEvent(e){
        //移除属性面板
        //this.$parent.hidePanel();
        //阻止事件冒泡
        e.stopPropagation();
        e.preventDefault();
        this.mulitComps = [];
    },
    mouseovercomp(comp){
      this.hoverObj = comp;
    },
    mouselevelcomp(comp){
      this.hoverObj = null;
    },
    //组件右键事件
    compRightEvent(){

    },
    compRightMenus(){
      $.contextMenu( 'destroy');
      let ts = this;
      let curComp = null;
      $.contextMenu({
        selector: 'div.comp_style, li.li-layer',
        className: "compMenu",
        trigger: 'right',
        delay: 500,
        zIndex:999999,
        autoHide:true,
        events:{
            preShow:function (opts) {
                //预览模式，停止右键
                if(ts.$parent.$refs['previewForm'].drawer == true){
                  return false;
                }
                var id = $(opts).attr("cid");
                curComp = bsUtils.findCompById(id, ts.pageInfo);
                if(ts.mulitComps.length == 1){
                  var comp = bsUtils.findCompById(id, ts.pageInfo);
                  //选中组件,图层
                  ts.mulitComps = [comp];
                  //打开属性框
                  ts.compProp(id);
                }else{
                  ts.$parent.hidePanel();
                }
            }
        },
        callback: function(key, opt) {
            var id = $(this).attr("cid");
            if(key === 'remove'){
                ts.removeComp(id, true, true);
            }else if(key === 'prop'){
                ts.compProp(id);
            }else if(key === 'data'){
                ts.compDataSet(id);
            }else if(key === 'charttype'){
                ts.setcharttype(id);
            }else if(key === 'top' || key === 'bottom' || key === 'up' || key === 'down'){
                ts.operCompZindex(key, id);
            }else if(key === 'clone'){
                let json = bsUtils.findCompById(id, ts.pageInfo);
                ts.coloneComp(json);
            }else if(key === 'filter'){
                let c = bsUtils.findCompById(id, ts.pageInfo);
                ts.$refs['compFilterForm'].init(c.comp);
            }else if(key === 'link'){
                let comp = bsUtils.findCompById(id, ts.pageInfo);
                ts.$refs['compLinkForm'].showDailog(comp);
            }else if(key === 'tabInfo'){
                let comp = bsUtils.findCompById(id, ts.pageInfo);
                ts.$refs['tabInfoForm'].showDailog(comp);
            }else if(key == 'paramSet'){
              let comp = bsUtils.findCompById(id, ts.pageInfo);
               ts.$refs['paramForm'].showDailog(comp.comp);  //参数信息放入 comp.comp 中
            }else if(key == 'insert'){
              ts.$refs['insertCompForm'].showDailog(curComp);
            }
        },
        items: {
            "insert":{
              name:"插入"
            },
            "data": {name: "数据", icon:"fa-database", visible:function (key, opt) {
                var id = $(opt.$trigger).attr("cid");
                var comp = bsUtils.findCompById(id, ts.pageInfo);
                if(comp.type === 'box' || comp.type === 'grid' || comp.type === 'chart' || comp.type === 'table' || comp.type === 'detail' || comp.type=='custom' ){
                    return true;
                }else{
                    return false;
                }
            }},
            "charttype": {name: "图表类型", visible:function (key, opt) {
                var id = $(opt.$trigger).attr("cid");
                var comp = bsUtils.findCompById(id, ts.pageInfo);
                if(comp.type === 'chart'){
                    return true;
                }else{
                    return false;
                }
            }},
            "paramSet": {name:"配置", visible:function(key, opt){
                var id = $(opt.$trigger).attr("cid");
                var comp = bsUtils.findCompById(id, ts.pageInfo);
                return comp.type == 'param';
              }
            },
            "filter": {name: "筛选", icon:"fa-filter", visible:function (key, opt) {
                    var id = $(opt.$trigger).attr("cid");
                    var comp = bsUtils.findCompById(id, ts.pageInfo);
                    if(comp.type === 'box' || comp.type === 'grid' || comp.type === 'chart' || comp.type === 'table' || comp.type === 'detail' || comp.type == 'custom'){
                        return true;
                    }else{
                        return false;
                    }
                }},
            "prop": {name: "属性"},
            "tabInfo": {name: "配置", visible:function(key, opt){
                var id = $(opt.$trigger).attr("cid");
                var comp = bsUtils.findCompById(id, ts.pageInfo);
                if(comp.type == 'tab'){
                  return true;
                }else{
                  return false;
                }
            }},
            "link":{name:"链接", icon:"fa-link", visible:function(key, opt){
                var id = $(opt.$trigger).attr("cid");
                var comp = bsUtils.findCompById(id, ts.pageInfo);
                if(comp.type === 'text' || comp.type === 'pic'){ //只给text/pic 组件增加链接
                    return true;
                }else{
                    return false;
                }
            }},
            "top": {name: "置顶", icon:"fa-angle-double-up"},
            "bottom": {name: "置底", icon:"fa-angle-double-down"},
            "up": {name: "上移", icon:"fa-angle-up"},
            "down": {name: "下移", icon:"fa-angle-down"},
            "clone": {name: "复制", icon: "fa-clone"},
            "remove": {name: "删除",icon:"fa-remove"}
        }
      });
    },
    operCompZindex(key, id){
      var layer = bsUtils.findCompById(id, this.pageInfo);
      bsUtils.writeOperLogs(this.userOpers, {comp:layer, oper:"zindex", options:{id:id, zIndex:layer.zIndex}});
      if(key === 'top'){
          layer.zIndex = this.pageInfo.zIndex + 1;
          this.pageInfo.zIndex = layer.zIndex;
      }else if(key === 'bottom'){
          layer.zIndex = 1;
      }else if(key === 'up'){
          layer.zIndex = layer.zIndex + 1;
      }else if(key === 'down'){
          layer.zIndex = layer.zIndex - 1;
      }
      this.$forceUpdate();
      this.setUpdate();
    },
    setCompZindex(id, zindex){
       var layer = bsUtils.findCompById(id, this.pageInfo);
       layer.zIndex = zindex;
       this.$forceUpdate();
    },
    removeComp(compId, isdelete, pushLogs){
        var ts = this;
        $(this.pageInfo.comps).each(function (a, b) {
            if(b.id === compId){
                 //不直接删除，添加删除标记
                //ts.pageInfo.comps.splice(a, 1);
                b.isdelete = isdelete;
                if(pushLogs == true){
                  bsUtils.writeOperLogs(ts.userOpers, {comp:b, oper:"addComp"});
                }
                return false;
            }
        });
        //重置布局
        ts.$forceUpdate();
        //ts.$parent.updateLayer();
        ts.setUpdate();
        ts.$parent.hidePanel();
    },
    compDataSet(compId, layoutId){
      let comp = bsUtils.findCompById(compId, this.pageInfo);
      if(comp.type ==='grid' || comp.type === 'detail'){
        this.$parent.$refs['layoutleftForm'].tabActive = 'data-tab-3';
      }else if(comp.type === 'box' || comp.type === 'chart' || comp.type === 'table' || comp.type == 'custom'){
        this.$parent.$refs['layoutleftForm'].tabActive = 'data-tab-2';
      }
      this.$parent.showDataPanel(comp);
    },
    setcharttype(compId){
      let comp = bsUtils.findCompById(compId, this.pageInfo);
      this.$refs['chartDailogForm'].changeType(comp);
    },
    coloneComp(comp){
      let json = JSON.parse(JSON.stringify(comp));
      json.top =  Number(json.top) + 50;
      json.left = Number(json.left) + 50;
      json.id = newGuid();
      if(json.comp){
        json.comp.id = json.id;
      }
      json.zIndex = this.pageInfo.zIndex + 1;  //zIndex 设置为最大
      this.pageInfo.zIndex = json.zIndex;
      this.pageInfo.comps.push(json);
      //移除选中框
      this.mulitComps = [];
      this.setUpdate();
      this.$nextTick(()=>{
        this.compDragResizeEvent(json);
      });
      bsUtils.writeOperLogs(this.userOpers, {comp:json, oper:'deletecomp'});
    },
    bindkeyboardEvent(){
      let ts = this;
      const moveFcn = (pos, step) => {
          var o = this.mulitComps;
          if(o.length === 0){
              return;
          }
          $(o).each(function(a, b){
              if(pos === 'sx'){
                  var top = b.top;
                  top  = top + step;
                  b.top = top;
                  //b.css({"top":top+"px"});
              }
              if(pos === 'zy'){
                  var left = b.left;
                  left  = left + step;
                  b.left = left;
                  //b.css({"left":left+"px"});
              }
          });
      }
      $(document).bind("keyup",function(event){
          event.preventDefault();
          if(event.shiftKey == false){
              return;
          }
          switch(event.keyCode) {
              case 38:   //上
                  moveFcn('sx', -1);
                  break;
              case 40: //下
                  moveFcn('sx', 1);
                  break;
              case 37: //左
                  moveFcn('zy', -1);
                  break;
              case 39:    //右
                  moveFcn('zy', 1);
                  break;
          }
      });

      //ctrl + s 保存页面
      $(document).bind('keydown', function(event) {
          if (event.ctrlKey || event.metaKey) {
              switch (String.fromCharCode(event.which).toLowerCase()) {
                  case 's':
                      event.preventDefault();
                      ts.$parent.savePage();
                      break;
                  case 'z':
                      event.preventDefault();
                      ts.rollbackOper();
                      break;
                  case 'v':   //ctrl + v 复制组件
                      if(event.target === document.body) {
                          event.preventDefault();
                          $(ts.mulitComps).each(function (a, b) {
                              ts.coloneComp(b);
                          });
                      }
                      break;
              }
          }
      });

      //放大缩小（macbook 两指触控） https://stackoverflow.com/questions/15416851/catching-mac-trackpad-zoom/37183950#37183950
      $(".bs-warpper").bind("mousewheel", (e)=>{
        if (e.ctrlKey) {
          e.preventDefault();
          e.stopImmediatePropagation();
          let delta = e.originalEvent.wheelDelta / 120;
          if(delta > 0){  //放大
            let bl = this.pageInfo.style.bl;
            bl += 1;
            this.pageInfo.style.bl = bl;
          }else{ //缩小
            let bl = this.pageInfo.style.bl;
            bl -= 1;
            this.pageInfo.style.bl = bl;
          }
        }
      });

    },
    unbindkeyboardEvent(){
      $(document).unbind("keyup");
      $(document).unbind("keydown");
      $(".bs-warpper").unbind("mousewheel");
    },
    rollbackOper(){
      bsUtils.rollbackOper(this.userOpers, this);
    }
  },
  watch:{

  },
  destroyed: function() {
    //删除右键菜单
    $.contextMenu( 'destroy');
  },
};
</script>

<style lang="less" scoped>
  .opeareastyle {
    background-size: 100% 100%;
    position: absolute;
  }
  .bs-warpper {
    width: 100%;
    height: 100%;
    overflow: auto;
  }
  .layout-center-body {
    width: 3000px;
    height: 3000px;
    overflow: auto;
    background-image: url(../../../assets/image/bsbg.png);
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
  }
  .comp_style {
    position: absolute !important;
  }
  .comp_item {
    width: 100%;
    height: 100%;
  }
  .comp_body {
    width: 100%;
    height: 100%;
    position: relative;
  }
  .compselect {
    border: dotted 2px #dedede !important;
    background-color: #0a6aa1;
  }
  .comphover {
    background-color: #0a6aa1;
  }
  .comp_mask {
    position: absolute;
    width: 100%;
    height: 100%;
    top:0;
    left:0;
    right: 0;
    bottom: 0;
    z-index: 10;
    display: block;
    opacity: 0.4;
  }
</style>
