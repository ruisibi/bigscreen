<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
     <div class="layout-bottom" v-show="show">
        <div class="ibox" style="margin-bottom:0px;">
          <div class="ibox-title" style="height:30px;">
            <h5> {{ title }} </h5>
            <div class="ibox-tools">
            <button class="btn btn-link btn-xs" style="font-size:16px;" @click="closeDatapanel()"><i class="el-icon el-icon-close"></i></button>
            </div>
          </div>
          <div class="ibox-content" style="padding:0px; overflow:auto; height:150px; width:100%">
            <dbox v-if="showBox" :comp="comp" useIn="bigscreen" ref="boxForm"></dbox>
            <dchart v-if="showChart" :comp="comp" :pageInfo="pageInfo" useIn="bigscreen" ref="chartForm"></dchart>
            <dgrid v-if="showGrid" :comp="comp" useIn="bigscreen" ref="gridForm"></dgrid>
            <dtable v-if="showTable" :comp="comp" :pageInfo="pageInfo" useIn="bigscreen" ref="tableForm"></dtable>
            <ddetail v-if="showDetail" :comp="comp" useIn="bigscreen" ref="detailForm"></ddetail>
            <dcustom v-if="showCustom" :comp="comp" useIn="bigscreen" ref="customForm"></dcustom>
          </div>
        </div>
      </div>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import dbox from '@/view/portal/data/Box'
import dchart from '@/view/portal/data/Chart'
import dgrid from '@/view/portal/data/Grid'
import dtable from '@/view/portal/data/Table'
import ddetail from '@/view/portal/data/Detail'
import dcustom from '@/view/bigscreen/data/Custom'

export default {
  name:"layoutBottom",
  components:{
    dbox, dchart, dgrid, dtable, ddetail, dcustom
  },
  props:{
      pageInfo:{
        type:Object,
        required:true
     }
  },
  data(){
    return {
      title:"数据面板",
      show:false,
      showBox:false,
      showChart:false,
      showGrid:false,
      showTable:false,
      showDetail: false,
      showCustom: false,
      comp:null
    }
  },
  mounted(){

  },
  computed: {

  },
  methods: {
    showPanel(comp){
      this.title = utils.getCompTypeDesc(comp.type) + "数据面板";
      $(".layout-left").css("height", "calc(100% - 298px)");
      $(".layout-center").css("height", "calc(100% - 180px)");
      this.show = true;

      this.showBox = false;
      this.showChart = false;
      this.showGrid = false;
      this.showTable = false;
      this.showDetail = false;
      this.showCustom = false;

      if(comp.type === 'box'){
        this.showBox = true;
        this.$nextTick(()=>this.$refs['boxForm'].init());
      }else if(comp.type === 'chart'){
        this.showChart = true;
      }else if(comp.type ==='grid'){
        this.showGrid = true;
      }else if(comp.type === 'table'){
        this.showTable = true;
      }else if(comp.type ==='detail'){
        this.showDetail = true;
      }else if(comp.type == 'custom'){
        this.showCustom = true;
      }
      this.comp = comp;
    },
   closeDatapanel(){
      $(".layout-left").css("height", "calc(100% - 118px)");
      $(".layout-center").css("height", "100%");
      this.showBox = false;
      this.showChart = false;
      this.showGrid = false;
      this.showTable = false;
      this.showDetail = false;
      this.showCustom = false;
      this.show = false;
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
</style>
