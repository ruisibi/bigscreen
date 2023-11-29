<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
<div>
  <el-form :model="prop" ref="propForm" label-position="left" size="mini">
    <el-collapse v-model="activeName" accordion>
      <el-collapse-item title="组件属性" name="1" v-if="useIn === 'report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm1" :comp="comp" :useIn="useIn" :showCompProp="true" :showTitleProp="false"></boxProperties>
       </el-collapse-item>
       <el-collapse-item title="标题属性" name="2" v-if="useIn === 'report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm2" :comp="comp" :useIn="useIn" :showCompProp="false" :showTitleProp="true"></boxProperties>
       </el-collapse-item>
      <el-collapse-item title="数据块属性" name="3">
          <el-form-item label="图层名称：" label-width="100px" v-if="useIn=='bigscreen'">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
            <el-form-item label="度量单位：" label-width="100px">
              <el-input v-model="prop.unit" @blur="changevalue('unit')"></el-input>
            </el-form-item>
            <el-form-item label="格式化：" label-width="100px">
               <el-select v-model="prop.fmt" :clearable="true" placeholder="请选择" @change="changevalue('fmt')">
                  <el-option
                    v-for="item in opts.fmt"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
            </el-form-item>
             <el-form-item label="背景颜色：" label-width="210px">
              <el-color-picker v-model="prop.bgcolor" @change="changevalue('bgcolor')"></el-color-picker>
            </el-form-item>
             <el-form-item label="度量比例：" label-width="100px">
               <el-select v-model="prop.rate" :clearable="true" placeholder="请选择" @change="changevalue('rate')">
                  <el-option
                    v-for="item in opts.rates"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="回调函数：" label-width="180px">
               <button type="button" @click="boxcallbackfunc()" class="btn btn-sm btn-primary"><i class="fa fa-wrench"></i> 设置</button>
            </el-form-item>
            <el-form-item label="数据集/表：" label-width="100px">
              <div align="right" :title="comp.comp.tname">{{ comp.comp ? comp.comp.tname : "" }}</div>
            </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="字体属性" name="4">
          <el-form-item label="字体：" label-width="110px">
              <el-select v-model="prop.family" @change="changevalue('family')" :clearable="true" placeholder="请选择">
                <el-option
                  v-for="item in opts.familys"
                  :key="item.en"
                  :label="item.ch"
                  :value="item.en">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="字体大小：" label-width="100px">
              <el-slider v-model="prop.fontsize" :max="99" :min="9" @change="changevalue('fontsize')"></el-slider>
            </el-form-item>
            <el-form-item label="字体颜色：" label-width="210px">
              <el-color-picker v-model="prop.fontcolor" @change="changevalue('fontcolor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="渐变色：" label-width="210px">
              <el-color-picker v-model="prop.color1" @change="changevalue('color1')"></el-color-picker>
              <el-color-picker v-model="prop.color2" @change="changevalue('color2')"></el-color-picker>
            </el-form-item>
             <el-form-item label="位置：" label-width="100px">
               <el-select v-model="prop.textAlign" @change="changevalue('textAlign')" :clearable="true" placeholder="请选择">
                <el-option
                  v-for="item in opts.aligns"
                  :key="item.value"
                  :label="item.name"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="是否粗体：" label-width="200px">
              <el-switch v-model="prop.weight" @change="changevalue('weight')"></el-switch>
            </el-form-item>
            <el-form-item label="是否斜体：" label-width="200px">
              <el-switch v-model="prop.titalic" @change="changevalue('titalic')"></el-switch>
            </el-form-item>
            <el-form-item label="是否下划线：" label-width="200px">
              <el-switch v-model="prop.underscore" @change="changevalue('underscore')"></el-switch>
            </el-form-item>
      </el-collapse-item>
       <el-collapse-item title="图形属性" name="5" v-if="useIn === 'report' || useIn === 'dashboard'">
          <el-form-item label="图形颜色：" label-width="210px" v-if="comp.comp.progressBar">
            <el-color-picker v-model="prop.progressBarColor" @change="changevalue('progressBarColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="图形颜色：" label-width="110px" v-if="comp.comp.chart">
            <el-select v-model="prop.chartColor" @change="changevalue('chartColor')" placeholder="请选择颜色">
              <el-option v-for="item in opts.allSeriesColors"  :key="item.id" :label="item.name" :value="item.id">
                <div :style="{'background-color':item.name}">{{item.name}}</div>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="图形类型：" label-width="110px" v-if="comp.comp.chart">
              <el-select v-model="prop.chartType" @change="changevalue('chartType')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opts.chartTypes"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
       </el-collapse-item>
       <el-collapse-item title="数字背景属性" name="6" v-if="useIn === 'bigscreen'">
          <el-form-item label="是否启用：" label-width="200px">
            <el-switch v-model="prop.kpiBg" @change="changevalue('kpiBg')"></el-switch>
          </el-form-item>
            <el-form-item label="背景颜色：" label-width="210px" v-if="prop.kpiBg==true">
              <el-color-picker v-model="prop.kpiBgColor" @change="changevalue('kpiBgColor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="背景图片：" label-width="110px" v-if="prop.kpiBg==true">
               <resourceSelect ref="resourceSelectForm" column="kpiBgImg" :cb="getBgImg" :prop="prop"></resourceSelect>
            </el-form-item>
            <el-form-item label="间距：" label-width="110px" v-if="prop.kpiBg==true">
               <el-slider v-model="prop.kpiSpacing" :max="50" :min="0" @change="changevalue('kpiSpacing')"></el-slider>
            </el-form-item>
            <el-form-item label="总长度：" label-width="110px" v-if="prop.kpiBg==true">
               <el-slider v-model="prop.kpiLength" :max="10" :min="0" @change="changevalue('kpiLength')"></el-slider>
            </el-form-item>
       </el-collapse-item>
       <el-collapse-item title="位置信息" name="7" v-if="useIn === 'bigscreen'">
            <posProperties :comp="comp" ref="posPropForm"></posProperties>
       </el-collapse-item>
       <el-collapse-item title="自动刷新" name="8" v-if="useIn === 'bigscreen' || useIn === 'dashboard'">
          <compAutoFlush :comp="comp" ref="autoFlushForm"/>
       </el-collapse-item>
    </el-collapse>
  </el-form>
  <boxCallback ref="boxCallbackForm"></boxCallback>
</div>
</template>

<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import boxProperties from '@/components/toolbox/BoxProperties'
import resourceSelect from '@/components/toolbox/ResourceSelect'
import boxCallback from './BoxCallback'
import posProperties from '@/components/toolbox/BsCompProperties'
import compAutoFlush from '@/components/toolbox/CompAutoFlush'
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  components:{
    boxProperties, boxCallback, posProperties, resourceSelect, compAutoFlush
  },
  props:{
      comp:{
        type:Object,
        required:true
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
      userOpers: {
        type:Array,
        required: false,
        default: null
      }
  },
  data(){
    return {
      activeName:this.useIn === 'report' || this.useIn === 'dashboard'?"1":"3",
      prop:{
        title:null, //图层名称
        unit:null,
        fmt:null,
        rate:null,
        fontsize:32,
        fontcolor:null,
        color1:null,
        color2:null,
        textAlign:"center",
        weight:null,
        titalic:null,
        underscore:null,
        bgcolor:null,
        chartColor:null,
        kpiBg:false,
        kpiBgColor: null,
        kpiBgImg:null,
        kpiBorderColor:null,
        chartType:null,
        chartColor:null,
        family: null,
        kpiSpacing:2,
        kpiLength: null,
      },
      opts:{
        fmt:utils.fmtJson,
        rates: utils.rates,
        aligns:[{value:"left", name:"居左"},{value:"center", name:"居中"},{value:"right", name:"居右"}],
        chartTypes:[{value:"line", name:"曲线图"},{value:"column", name:"柱状图"},{value:"area", name:"面积图"}],
        allSeriesColors:[], //系列所有默认颜色
        familys:bsUtils.fonts,
      }
    }
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    init(){
      if(this.useIn === 'report' || this.useIn === 'dashboard'){
        this.$refs['boxPropForm1'].init();
        this.$refs['boxPropForm2'].init();
      }else if(this.useIn === 'bigscreen'){
        this.$refs['posPropForm'].init();
        this.prop.title = this.comp.title;
      }
      if(this.useIn === 'bigscreen' || this.useIn === 'dashboard'){
        this.$refs['autoFlushForm'].init();
      }
      let p = this.prop;
      let c = this.comp.comp;

      if(c.kpiJson){
        p.unit = c.kpiJson.unit;
        p.fmt = c.kpiJson.fmt;
        p.rate = c.kpiJson.rate;
      }
      let cs = this.comp.style;
      if(cs && cs.fontsize){
        p.fontsize = cs.fontsize;
      }
      if(cs && cs.textAlign){
        p.textAlign = cs.textAlign;
      }
      if(cs){
        p.fontcolor = cs.fontcolor;
        p.weight = cs.weight,
        p.titalic = cs.titalic;
        p.underscore= cs.underscore;
        p.bgcolor = cs.bgcolor;
        p.progressBarColor = cs.progressBarColor;
        p.kpiBg = cs.kpiBg;
        p.kpiBgColor = cs.kpiBgColor;
        p.kpiBgImg = cs.kpiBgImg;
        p.kpiSpacing = cs.kpiSpacing;
        p.kpiLength = cs.kpiLength;
        p.color1 = cs.color1;
        p.color2 = cs.color2;
         p.family = cs.family;
      }

      if(c.chart){
        p.chartType = c.chart.chartJson.type;
        if(c.chart.colors){
          p.chartColor = c.chart.colors['-'];
        }
        this.loadSeriesColors();
      }

    },
    //设置box回调函数
    boxcallbackfunc(){
      if(this.comp.comp && this.comp.comp.kpiJson){
      this.$refs['boxCallbackForm'].openDailog(this.comp.comp.kpiJson);
      }else{
        this.$notify.error("未绑定数据。");
      }
    },
    boxView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].boxView();
    },
    compRender(){
      var o = this.$parent.$parent;
      o.$forceUpdate();
      o = o.$refs['optarea'];
      o.$forceUpdate();
      o.$refs['mv_'+this.comp.id].$forceUpdate();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    changevalue(prop){
      let c = this.comp.comp;
      let v = this.prop[prop];
      if(!this.comp.style){
        this.comp.style = {};
      }
      let cs = this.comp.style;
      if(prop === 'unit' || prop === 'fmt' || prop == 'rate'){
        //写日志
        bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"boxpropset", options:{use:"kpiJson",col:prop, val:c.kpiJson[prop], ts: this}});
        c.kpiJson[prop] = v;
        this.boxView();
      }else if(prop === 'bgcolor' || prop === "fontsize" || prop==='fontcolor' || prop==="textAlign"
        || prop==='weight' || prop === 'titalic' || prop ==='underscore' || prop ==='progressBarColor'
        ||prop ==='kpiBg' || prop === 'kpiBgColor' || prop === 'kpiBgImg' || prop == 'kpiSpacing' || prop == 'kpiLength'
        || prop == 'color1' || prop == 'color2' || prop == 'family'){
        //写日志
        bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"boxpropset", options:{use:"style",col:prop, val:cs[prop], ts: this}});
        cs[prop] = v;
        this.compRender();
      }else if(prop=="chartType"){
        c.chart.chartJson.type = v;
        this.boxView();
      }else if(prop==='chartColor'){
        c.chart.colors = {"-": v};
        this.boxView();
      }else if(prop === 'title'){
        this.comp.title = v;
        this.compRender();
      }
      this.setUpdate();
    },
    getBgImg(col, val){
      this.changevalue(col);
    },
    loadSeriesColors(){
      ajax({
        url:"portal/chartColors.action",
        data:{},
        type:"GET",
        success:(resp)=>{
          this.opts.allSeriesColors = resp.rows.map((m, idx)=>{
            return {id:idx+1, name:m}
          });
        }
      }, this);
    },
    //撤销操作
    goback(options){
      if(options.use === 'style'){
        this.comp.style[options.col] = options.val;
        this.prop[options.col] = options.val;
        this.compRender();
      }else if(options.use === 'kpiJson'){
        this.comp.comp.kpiJson[options.col] = options.val;
        this.prop[options.col] = options.val;
        this.boxView();
      }
    }
  },
  watch: {

  }
}
</script>

<style lang="less" scoped>

</style>
