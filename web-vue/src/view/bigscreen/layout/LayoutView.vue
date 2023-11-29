<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 大屏布局器浏览模式 -->
<script>
import { baseUrl, newGuid } from "@/common/biConfig";
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
import DateView from '@/view/bigscreen/view/Date'
import WeatherView from '@/view/bigscreen/view/Weather'
import IframeView from '@/view/bigscreen/view/Iframe'
import TabView from '@/view/bigscreen/view/Tab'
import LineView from '@/view/bigscreen/view/Line'
import TextView from '@/view/bigscreen/view/Text'
import ParamView from '@/view/bigscreen/view/Param'
import CustomView from '@/view/bigscreen/view/Custom'
import animate from 'animate.css'

export default {
  components: {
    BoxView,
    ChartView,
    GridView,
    GridTranspositionView,
    TableView,
    CompFilter,
    ChartSeriesColor,
    CompEvent,
    boxTypes,
    ImageView,
    picUpload,
    DetailView,
    DateView,
    WeatherView,
    IframeView,
    TabView,
    LineView,
    TextView,
    ParamView,
    CustomView,
  },
  props: {
    pageInfo: {
      type: Object,
      required: true,
      default:{}
    },
    //在哪里使用 preview / view / pushView 模式
    useIn:{
      type:String,
      required:true,
    },
    //在大屏分享后，需要使用token
    token:{
      type:String,
      required:false,
      default:null,
    }
  },
  render(h) {
    //大屏样式
    let style = this.pageInfo.style;
    let s = this.style;
    if(style){
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
      }else{
         s['background-image'] = null;
      }
      if(this.useIn === "view" || this.useIn === 'pushView'){
        this.computeFunc(s, false);
      }
    }
    //先初始化参数默认值
    bsUtils.calcParamDefval(this.pageInfo, this.token);
    //先获取最大z-index
    let maxzIndex = 0;
    $(this.pageInfo.comps).each((a, b)=>{
        if(b.zIndex > maxzIndex){
            maxzIndex = b.zIndex;
        }
    });
    //大屏组件
    let comps = [];
    if(this.pageInfo.comps){
      if(this.pageInfo.comps){
        $(this.pageInfo.comps).each((a, b) => {
          if(b.isdelete == true){  //已经被删除的组件
            return;
          }
          let s = this.createCompStyle(b);
          if(this.pageInfo.style.styleType === 'bigscreen'){
            s['color'] = "white";
          }else{
            s['color'] = "#201e1e";
          }
          //设置tab选项卡切换过度动画,放在 comp_item 中
          let existTab = bsUtils.compInTabs(this.pageInfo, b.id);
          if(!existTab){
            let rt = h('div', {class:"comp_item", attrs:{id:"bigscreen"+b.id}}, [this.createComp(b, h)]);
            comps.push(h('div', {class:"comp_style",style:s,attrs:{cid:b.id}}, [rt]));
          }else{
            if(b.isTabHide != true){  //如果是在选项卡中，并且不是隐藏, 设置  zIndex 为最大
               //设 zIndex 让组件可视
               s['z-index'] = maxzIndex + 1;
            }
            let rt = h('div', {class:"comp_item", directives:[{name: 'show',value: !b.isTabHide}], attrs:{id:"bigscreen"+b.id}}, [this.createComp(b, h)]);
            let an = existTab.style && existTab.style.animate ? existTab.style.animate : null;
            if(an == 'SlideIn'){  //滑入 (采用 animate.css)
              s['overflow'] = "hidden";
              comps.push(h('div', {class:"comp_style",style:s,attrs:{cid:b.id}}, [h('transition', {attrs:{name:"animate__animated animate__bounce", "enter-active-class":"animate__fadeInRight","leave-active-class":"animate__fadeOutLeft"}}, [rt])]));
            }else if(an){
              comps.push(h('div', {class:"comp_style",style:s,attrs:{cid:b.id}}, [h('transition', {attrs:{name:an}}, [rt])]));
            }else {  //无过度动画
              comps.push(h('div', {class:"comp_style",style:s,attrs:{cid:b.id}}, [rt]));
            }
          }

        })
      }
    }
    return h('div', {class:"opeareastyle", style:s, attrs:{id:"optarea"}}, comps);
  },
  data() {
    return {
       style:{},  //大屏页面样式
    };
  },
  mounted() {

  },
  computed: {},
  methods: {

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
         if(comp.link){
           s['cursor'] = "pointer";
         }
         return h('div', {class:"comp_body", style:s , on:{dblclick:()=>{this.complink(comp)}}}, [h('text-view',{ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:"bigscreen", editor:false}})]);
      }else if(comp.type === 'pic'){
        let style = {};
        if(comp.link){
          style['cursor'] = "pointer";
        }
        return h('div', {class:"comp_body", style:style , on:{dblclick:()=>{this.complink(comp)}}}, [h('image-view',{ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", editor:false}})]);
      }else if(comp.type === 'decorate'){
        let style = {"border-color":"#dee5e7", "border-width":"1px", "border-style":"solid"};
        if(this.pageInfo.style && this.pageInfo.style.styleType === 'def'){
          style['border-color'] = "#2a2e33";
        }
        let s = bsUtils.createDecorateStyle(comp, style);
        return h('div', {class:"comp_body", style:s , on:{dblclick:()=>{this.complink(comp)}}});
      }else if(comp.type === 'box'){
        let style = {};
        return h('div', {class:"comp_body", style:style , on:{dblclick:()=>{this.complink(comp)}}}, [h('box-view',{key:'k_' + comp.id, ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true, editor:false}})]);
      }else if(comp.type === 'chart'){
        let style = {};
        return h('div', {class:"comp_body", style:style , on:{dblclick:()=>{this.complink(comp)}}}, [h('chart-view',{key:'k_' + comp.id, ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true, editor:false}})]);
      }else if(comp.type === 'grid'){
        let style = {"border-color":"#e7eaec", "border-width":"1px", "border-style":"solid"};
        if(this.pageInfo.style.styleType === 'def'){
          style['border-color'] = "#DCDFE6";
        }
        let s = bsUtils.createTableBorderStyle(comp, style);
        let label = comp.comp.transposition == true ? "grid-transposition-view" : "grid-view";
        return h('div', {class:"comp_body", style:s , on:{dblclick:()=>{this.complink(comp)}}}, [h(label,{key:'k_' + comp.id, ref:'mv_'+comp.id, attrs:{comp:comp,pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true, editor:false}})]);
      }else if(comp.type === 'table'){
        let style = {"border-color":"#e7eaec", "border-width":"1px", "border-style":"solid"};
        if(this.pageInfo.style.styleType === 'def'){
          style['border-color'] = "#DCDFE6";
        }
        let s = bsUtils.createTableBorderStyle(comp, style);
        return h('div', {class:"comp_body", style:s , on:{dblclick:()=>{this.complink(comp)}}}, [h('table-view',{ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true, editor:false}})]);
      }else if(comp.type === 'detail'){
        let style = {};
        return h('div', {class:"comp_body", style:style, on:{dblclick:()=>{this.complink(comp)}}}, [h('detail-view',{ref:'mv_'+comp.id, attrs:{comp:comp,pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true, editor:false}})]);
      }else if(comp.type === 'date'){
        let style = {};
        return h('div', {class:"comp_body", style:style, on:{dblclick:()=>{this.complink(comp)}}}, [h('date-view',{ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null,useIn:"bigscreen", token:this.token, autuFlush:true, editor:false}})]);
      }else if(comp.type === 'weather'){
        let style = {};
        return h('div', {class:"comp_body", style:style, on:{dblclick:()=>{this.complink(comp)}}}, [h('weather-view',{ref:'mv_'+comp.id,  attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true,  editor:false}})]);
      }else if(comp.type === 'iframe'){
        let style = {};
        return h('div', {class:"comp_body", style:style, on:{dblclick:()=>{this.complink(comp)}}}, [h('iframe-view',{ref:'mv_'+comp.id,  attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true,  editor:false}})]);
      }else if(comp.type === 'tab'){
        let style = {};
        return h('div', {class:"comp_body", style:style, on:{dblclick:()=>{this.complink(comp)}}}, [h('tab-view',{ref:'mv_'+comp.id,  attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true,  editor:false}})]);
      }else if(comp.type === 'line'){
        let style = {};
        return h('div', {class:"comp_body", style:style, on:{dblclick:()=>{this.complink(comp)}}}, [h('line-view',{ref:'mv_'+comp.id,  attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, autuFlush:true,  editor:false}})]);
      }else if(comp.type == 'param'){
        let style = {};
        return h('div', {class:"comp_body", style:style, on:{dblclick:()=>{this.complink(comp)}}}, [h('param-view',{ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, useIn:"bigscreen",  editor:false}})]);
      }else if(comp.type === 'custom'){
        let style = {};
        return h('div', {class:"comp_body", style:style , on:{dblclick:()=>{this.complink(comp)}}}, [h('custom-view',{key:'k_' + comp.id, ref:'mv_'+comp.id, attrs:{comp:comp, pageInfo:this.pageInfo, portalParams:null, useIn:"bigscreen", token:this.token, editor:false}})]);
      }
    },
     //展现模式下，根据屏幕宽度/高度调整大小，用来自适应屏幕
    computeFunc(s, isUpdate){
        let style = this.pageInfo.style;
        if(style){
          var w = this.useIn === 'view' ? $(window) : $(".bslayout");
          var width = w.width();
          var height = w.height();
          var pageWidth = Number(style.width);
          var pageHeight = Number(style.height);
          //计算比例
          var bl1 = width / pageWidth;
          var bl2 = height / pageHeight;
          //采用最小比例
          var scale = bl1 > bl2 ? bl2: bl1;
          this.pageInfo.style.bl = scale;
          s['transform'] =  "scale(" + (scale) + ", " + (scale) + ")";
          s['transform-origin'] = "0 0";
          if(isUpdate == true){
            this.$forceUpdate();
          }
        }
    },
    bsResize(){
      if(this.useIn === "view" || this.useIn === 'pushView' || this.useIn === 'preview'){
        this.computeFunc(this.style, true);
      }
    },
    complink(comp){
      if(comp.link){
				let link = comp.link;
				if(!link){
					return;
				}
				let url = "";
				let p = {};
				if(link.targetType ==="dashboard"){
					url = "/dashboard/PushView"
					p['dashboard'] = link.dashboard;
				}else if(link.targetType === 'report'){
					url = "/portal/PushView";
					p['reportId'] = link.report;
				}else if(link.targetType == 'bigscreen'){
					url = "/bigscreen/PushView";
					p['bsid'] = link.bigscreen;
				}
				this.$parent.linkurl(link, url, p);
      }

    },
    //根据参数刷新参数相关的组件
    flushCompsByPms(param){
      bsUtils.flushCompsByPms(param, this.pageInfo, (comp)=>{
        this.refreshComp(comp.id);
      });
    },
    //刷新组件
    refreshComp(compId){
      let o = this.$refs['mv_'+compId];
      o.view();
    },

  },
  watch:{

  },
  destroyed: function() {

  },
  beforeMount() {
    window.addEventListener('resize', this.bsResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.bsResize);
  },
};
</script>

<style lang="less" scoped>
  .opeareastyle {
    background-size: 100% 100%;
    position: absolute;
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

/**  采用 elementui 的动画
.fade-leave, .fade-enter-to {
  opacity: 1;
}
.fade-leave-active, .fade-enter-active {
  transition: all 1.5s;
}
.fade-leave-to, .fade-enter {
  opacity: 0;
}
**/
</style>
