<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="数据块类型" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">数据块类型</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
      </template>
        <el-row>
          <el-col :span="6">
            <ul>
              <template v-for="item in types">
                <li :key="item.tp" @click="selectme(item.tp)" :btp="item.tp" :class="item.tp===selectType?'select':''">{{ item.name }}</li>
              </template>
            </ul>
          </el-col>
          <el-col :span="18">
          <div class="selright">
            <template v-for="item in types">
              <div :key="item.tp" class="one" :btp="item.tp" :style="'display:'+(item.tp === selectType ? 'block' : 'none')" style="text-align: center;">
                <img :src="require('@/assets/chart/' + item.img)">
              </div>
            </template>
          </div>
         </el-col>
        </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">确 定</el-button>
        <el-button @click="show = false">取 消</el-button>
      </div>
    </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl, newGuid} from '@/common/biConfig'
import $ from 'jquery'

export default {
  components:{

  },
  props:{
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
  },
  data(){
    return {
        show:false,
        types:[
          {tp:"base", name:"基础类型", img:"box-1.png"},
          {tp:"mkpi", name:"多指标", img:"box-7.png"},
          {tp:"line", name:"带曲线图", img:"box-2.png"},
          {tp:"column", name:"带柱状图", img:"box-4.png"},
          {tp:"area", name:"带面积图", img:"box-3.png"},
          {tp:"bl", name:"带比例", img:"box-6.png"},
          {tp:"thb", name:"带同环比", img:"box-5.png"},
        ],
        selectType:"base",
        comp:null
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    openDailog(comp){
      this.show = true;
      this.comp = comp;
      var c = comp.comp; //表示真正组件对象
      var tp = "base";
      if(comp.type == "mbox"){
        tp = "mkpi";
      }else if(c && c.chart){
        tp = c.chart.chartJson.type;
      }else if(c && c.thbDim){
        tp = "thb";
      }else if(c && c.progressBar == true){
        tp = "bl";
      }
      this.selectType = tp;
    },
    selectme(tp){
      this.selectType = tp;
    },
    save(){
      var btp = this.selectType;
      if(!this.comp.comp){
        this.comp.comp = {id:this.comp.id};
      }
      var comp = this.comp.comp;

      //删除数据
      delete comp.kpiJson;
      delete comp.chart;
      delete comp.thbDim;
      delete comp.progressBar;
      delete comp.kpisize;
      delete comp.colsize;
       //清除tid
      delete comp.tid;
      delete comp.tname;
      delete comp.tincome;
      delete this.comp.style; //删除样式

      if(btp == "base"){
        comp.type = 'box';
        this.comp.type = 'box'
      }else if(btp == "mkpi"){
        comp.type = "mbox";
        this.comp.type = 'mbox';
        comp.kpiJson = [{
                  "kpi_id":1,
                  "ydispName":"经营指标1",
                  "img":"icon-01.png"
              },
              {
                  "kpi_id":2,
                  "ydispName":"经营指标2",
                  "img":"icon-02.png"
              }]
      }else if(btp == "line" || btp == "column" || btp == "area"){
        comp.type = 'box';
        this.comp.type = 'box'
        if(!comp.chart){
          var chart = {id:newGuid(),chartJson:{"type":btp, xcol:{}, ycol:{},
                "margin":"0,0,0,0",
                "markerEnabled":false,
                "splitLine":false,
                "showLegend":false,
                "boundaryGap":false,
                "hideYaxis":true
              }, kpiJson:[]};
          comp.chart = chart;
        }else{
          comp.chart.chartJson.type = btp;
          //boxView(comp);
        }
      }else if(btp == "bl"){
        comp.type = 'box';
        this.comp.type = 'box'
        comp.progressBar = true;
      }else if(btp == "thb"){
        comp.type = 'box';
        this.comp.type = 'box'
        comp.thbDim = {};
      }
      if(this.useIn === 'report'){
        this.$parent.setUpdate();
        var o = this.$parent.$refs['mv_'+comp.id];
        o.data = null;
        o.$forceUpdate();
      }else if(this.useIn === 'dashboardEdit'){
        this.$parent.setUpdate();
      }
      this.$parent.$forceUpdate();
      this.show = false;
    }
  }
}
</script>
<style lang="less" scoped>
.one {
  margin: 5px;
  line-height: 25px;
  text-align: center;
}
ul {
      margin: 0px;
    padding: 0px;
}
li {
  margin: 0px;
    padding: 6px;
    background-color: #EEE;
    border-bottom: solid 1px #CCCCCC;
    cursor: pointer;

}
.select {
          background-color: #FFF;
    }
</style>>
