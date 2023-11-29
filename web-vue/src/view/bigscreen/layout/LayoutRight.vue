<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
     <div class="layout-right" v-show="show">
        <div class="ibox">
          <div class="ibox-title">
            <h5>{{title}}</h5>
            <div class="ibox-tools">
              <button class="btn btn-link btn-xs" style="font-size:16px;" @click="closeproperty()"><i class="el-icon el-icon-close"></i></button>
            </div>
          </div>
          <div class="ibox-content" style="padding:0px 3px 0px 3px;">
            <pbox v-if="showBox" ref="boxForm" useIn="bigscreen" :userOpers="userOpers" :comp="comp"></pbox>
            <ptext v-if="showText" ref="textForm" useIn="bigscreen" :userOpers="userOpers" :comp="comp"></ptext>
            <ppic v-if="showPic" ref="picForm" useIn="bigscreen" :userOpers="userOpers" :comp="comp"></ppic>
            <pchart v-if="showChart" ref="chartForm" useIn="bigscreen" :comp="comp"></pchart>
            <pgrid v-if="showGrid" ref="gridForm" useIn="bigscreen" :userOpers="userOpers" :comp="comp"></pgrid>
            <ptable v-if="showTable" ref="tableForm" useIn="bigscreen" :comp="comp"></ptable>
            <pdetail v-if="showDetail" ref="detailForm" useIn="bigscreen" :comp="comp"></pdetail>
            <decorate v-if="showDecorate" ref="decorateForm" :comp="comp" :userOpers="userOpers"></decorate>
            <bigscreenProp v-if="showPageProp" ref="pagePropForm" :userOpers="userOpers" :pageInfo="pageInfo"></bigscreenProp>
            <pdate v-if="showDate" ref="dateForm" :comp="comp" :userOpers="userOpers" ></pdate>
            <pweather v-if="showWeather" ref="weatherForm" :comp="comp" :userOpers="userOpers" ></pweather>
            <piframe v-if="showIframe" ref="iframeForm" :comp="comp" :userOpers="userOpers" ></piframe>
            <ptab v-if="showTab" ref="tabForm" :comp="comp" :userOpers="userOpers"></ptab>
            <pline v-if="showLine" ref="lineForm" :comp="comp" :userOpers="userOpers"></pline>
            <pparam v-if="showParam" ref="paramForm" :comp="comp" :userOpers="userOpers"></pparam>
            <pcustom v-if="showCustom" ref="customForm" :comp="comp" :userOpers="userOpers"></pcustom>
          </div>
        </div>
      </div>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import pbox from '@/view/portal/prop/Box'
import ptext from '@/view/portal/prop/Text'
import pchart from '@/view/portal/prop/Chart'
import pgrid from '@/view/portal/prop/Grid'
import ptable from '@/view/portal/prop/Table'
import ppic from '@/view/portal/prop/Pic'
import bigscreenProp from '@/view/bigscreen/layout/BigscreenProp'
import pdetail from '@/view/portal/prop/Detail'
import decorate from './../prop/Decorate'
import pdate from './../prop/Date'
import pweather from './../prop/Weather'
import piframe from './../prop/Iframe'
import ptab from './../prop/Tab'
import pline from './../prop/Line'
import pparam from './../prop/Param'
import pcustom from './../prop/Custom'

export default {
  components:{
    pbox,ptext,pchart,pgrid,ptable,bigscreenProp,ppic,pdetail,decorate, pdate, pweather, piframe, ptab, pline, pparam, pcustom
  },
  props:{
      pageInfo:{
        type:Object,
        required:true
     },
     userOpers:{
       type:Array,
       required : true,
     }
  },
  data(){
    return {
      title:"属性面板",
      show:false,
      showBox:false,
      showChart:false,
      showGrid:false,
      showTable:false,
      showText:false,
      showPageProp:false,
      showPic:false,
      showDetail:false,
      showDecorate:false,
      showDate: false,
      showWeather: false,
      showIframe: false,
      showTab:false,
      showLine: false,
      showParam: false,
      showCustom: false,
      comp:null
    }
  },
  mounted(){

  },
  computed: {

  },
  methods: {
    openPageProp(){
       this.title = "大屏设置面板";
       this.show = true;

      this.showBox = false;
      this.showChart = false;
      this.showGrid = false;
      this.showTable = false;
      this.showText = false;
      this.showPic = false;
      this.showDetail = false;
      this.showDecorate = false;
      this.showDate = false;
      this.showWeather = false;
      this.showIframe = false;
      this.showTab = false;
      this.showLine = false;
      this.showParam = false;
      this.showCustom = false;

      this.showPageProp = true;
      this.$nextTick(()=> this.$refs['pagePropForm'].init());

      $(".layout-center").css("margin-right","260px");
      //激发resize 事件
      $(window).trigger("resize");
    },
    showPanel(comp){
      this.title = utils.getCompTypeDesc(comp.type) + "属性面板";
      this.show = true;
      this.comp = comp;

      this.showBox = false;
      this.showChart = false;
      this.showGrid = false;
      this.showTable = false;
      this.showText = false;
      this.showPageProp = false;
      this.showPic = false;
      this.showDetail = false;
      this.showDecorate = false;
      this.showDate = false;
      this.showWeather = false;
      this.showIframe = false;
      this.showTab = false;
      this.showLine = false;
      this.showParam = false;
      this.showCustom = false;

      $(".layout-center").css("margin-right","260px");

      if(comp.type === 'box'){
        this.showBox = true;
        this.$nextTick(()=> this.$refs['boxForm'].init());
      }else if(comp.type === 'chart'){
        this.showChart = true;
        this.$nextTick(()=>this.$refs['chartForm'].init());
      }else if(comp.type ==='grid'){
        this.showGrid = true;
        this.$nextTick(()=>this.$refs['gridForm'].init());
      }else if(comp.type === 'table'){
        this.showTable = true;
        this.$nextTick(()=>this.$refs['tableForm'].init());
      }else if(comp.type === 'text'){
        this.showText = true;
        this.$nextTick(()=>this.$refs['textForm'].init());
      }else if(comp.type === 'pic'){
        this.showPic = true;
        this.$nextTick(()=>this.$refs['picForm'].init());
      }else if(comp.type === 'detail'){
        this.showDetail = true;
        this.$nextTick(()=>this.$refs['detailForm'].init());
      }else if(comp.type === 'decorate'){
        this.showDecorate = true;
        this.$nextTick(()=>this.$refs['decorateForm'].init());
      }else if(comp.type == 'date'){
        this.showDate = true;
        this.$nextTick(()=>this.$refs['dateForm'].init());
      }else if(comp.type == 'weather'){
        this.showWeather = true;
        this.$nextTick(()=>this.$refs['weatherForm'].init());
      }else if(comp.type == 'iframe'){
        this.showIframe = true;
        this.$nextTick(()=>this.$refs['iframeForm'].init());
      }else if(comp.type == 'tab'){
        this.showTab = true;
        this.$nextTick(()=>this.$refs['tabForm'].init());
      }else if(comp.type == 'line'){
        this.showLine = true;
        this.$nextTick(()=>this.$refs['lineForm'].init());
      }else if(comp.type == 'param'){
        this.showParam = true;
        this.$nextTick(()=>this.$refs['paramForm'].init());
      }else if(comp.type == 'custom'){
        this.showCustom = true;
        this.$nextTick(()=>this.$refs['customForm'].init());
      }
      //激发resize 事件
      $(window).trigger("resize");
    },
   closeproperty(){
      $(".layout-center").css("margin-right","");
      this.show = false;
      //激发resize 事件
      $(window).trigger("resize");
   }
  },
  watch: {

  },
  beforeMount(){

  },
  beforeDestroy(){

  }
}
</script>

<style lang="less" scoped>
  .layout-right {
	  position: fixed;
    width: 257px;
    height: calc(100% - 118px);
    right: 0;
    .ibox {
      	height: 100%;
	      margin-bottom:0px;
	      overflow:auto;
    }

  .ibox-title h5 {
    display: inline-block;
    font-size: 14px;
    margin: 0 0 7px;
    padding: 0;
    text-overflow: ellipsis;
    float: left;
  }
  .ibox-tools {
    display: inline-block;
    float: right;
    margin-top: -6px;
    position: relative;
    padding: 0;
  }
}
</style>
