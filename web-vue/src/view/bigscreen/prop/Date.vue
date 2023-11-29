<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<template>
    <el-form :model="prop" ref="propForm" label-position="left" size="mini">
      <el-collapse v-model="activeName" accordion>
        <el-collapse-item title="组件属性" name="1">
           <el-form-item label="图层名称：" label-width="100px">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
          <el-form-item label="字体：" label-width="100px">
            <el-select v-model="prop.family" @change="changevalue('family')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opts.familys"
                :key="item.en"
                :label="item.ch"
                :value="item.en">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="字体颜色：" label-width="210px">
              <el-color-picker v-model="prop.fontcolor" @change="changevalue('fontcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-slider v-model="prop.fontsize" :min="9" :max="56" @change="changevalue('fontsize')"></el-slider>
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
             <el-form-item label="显示：" label-width="80px">
               <el-checkbox-group v-model="prop.showType"  @change="changevalue('showType')" >
                <el-checkbox-button label="date">日期</el-checkbox-button>
                <el-checkbox-button label="week">星期</el-checkbox-button>
                <el-checkbox-button label="time">时间</el-checkbox-button>
              </el-checkbox-group>
             </el-form-item>
        </el-collapse-item>
         <el-collapse-item title="位置信息" name="2">
            <posProperties :comp="comp" ref="posPropForm"></posProperties>
       </el-collapse-item>
      </el-collapse>
    </el-form>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import boxProperties from '@/components/toolbox/BoxProperties'
import posProperties from '@/components/toolbox/BsCompProperties'
import resourceSelect from '@/components/toolbox/ResourceSelect'
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  components:{
    boxProperties, posProperties, resourceSelect
  },
  props:{
      comp:{
        type:Object,
        required:true
      },
      userOpers: {
        type: Array,
        required: true,
        default:[]
      },
  },
  data(){
    return {
      activeName: "1",
      prop:{
         title:null,
         fontcolor:null,
         fontsize:12,
         textAlign: null,
         family:null,
         showType:['date', 'week', 'time']
      },
      opts:{
          aligns:[{value:"left", name:"居左"},{value:"center", name:"居中"},{value:"right", name:"居右"}],
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
        this.$refs['posPropForm'].init();
        this.prop.title = this.comp.title;
        if(this.comp.style){
          for(let s in this.comp.style){
            this.prop[s] = this.comp.style[s];
          }
        }else{
          this.prop.fontcolor = null;
          this.prop.fontsize = 12;
          this.prop.textAlign = null;
          this.prop.showType = ['date', 'week', 'time'];
        }
    },
    compRender(){
      var o = this.$parent.$parent;
       o.$forceUpdate();
       o.$refs['optarea'].$forceUpdate();
       o.$refs['optarea'].$refs['mv_' + this.comp.id].$forceUpdate();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    changevalue(col){
      let val = this.prop[col];
      if(!this.comp.style){
        this.comp.style = {};
      }
      let s = this.comp.style;
      if(col === 'title'){
        this.comp.title = val;
      }else{
        bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"date", options:{col:col, val:s[col], ts: this}});
        s[col] = val;
      }
      this.compRender();
      this.setUpdate();
    },
    //回撤操作
    goback(options){
       let col = options.col;
       let val = options.val;
       this.comp.style[col] = val;
       this.init();
       this.compRender();
    }
  },
  watch: {

  }
}
</script>
