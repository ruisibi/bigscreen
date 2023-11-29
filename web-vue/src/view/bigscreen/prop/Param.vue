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
          <el-form-item label="参数标识：" label-width="100px">
           <div align="right"> {{ comp.comp.paramid }} </div>
          </el-form-item>
          <el-form-item label="参数类型：" label-width="100px">
           <div align="right"> {{ getParamName() }} </div>
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="参数样式" name="style">
          <div align='right' style="color:#cccccc;">深色模式起作用</div>
           <el-form-item label="字体颜色：" label-width="210px">
            <el-color-picker v-model="prop.color" @change="changevalue('color')"></el-color-picker>
          </el-form-item>
          <el-form-item label="背景颜色：" label-width="210px">
            <el-color-picker v-model="prop.bgcolor" @change="changevalue('bgcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="边框颜色：" label-width="210px">
            <el-color-picker v-model="prop.bordercolor" @change="changevalue('bordercolor')"></el-color-picker>
          </el-form-item>
           <el-form-item label="选中后颜色：" label-width="210px">
            <el-color-picker v-model="prop.selectcolor" @change="changevalue('selectcolor')"></el-color-picker>
          </el-form-item>
           <el-form-item label="清除颜色：" label-width="200px">
              <el-switch v-model="prop.clear" @change="changevalue('clear')"></el-switch>
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
         color:null,
         bgcolor: null,
         bordercolor:null,
         selectcolor:null,
      },
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
        if(this.comp.style && this.comp.style.color){
           this.prop.color = this.comp.style.color;
        }else{
           this.prop.color = null;
        }
        if(this.comp.style && this.comp.style.bgcolor){
           this.prop.bgcolor = this.comp.style.bgcolor;
        }else{
           this.prop.bgcolor = null;
        }
        if(this.comp.style && this.comp.style.bordercolor){
           this.prop.bordercolor = this.comp.style.bordercolor;
        }else{
           this.prop.bordercolor = null;
        }
        if(this.comp.style && this.comp.style.selectcolor){
           this.prop.selectcolor = this.comp.style.selectcolor;
        }else{
           this.prop.selectcolor = null;
        }

    },
    getParamName(){
      if(this.comp.comp.type){
        let r = bsUtils.paramTypes.filter(m=>m.value == this.comp.comp.type)[0];
        return r.label;
      }else{
        return "";
      }
    },
    compRender(){
      var o = this.$parent.$parent;
      var mv = o.$refs['optarea'].$refs['mv_' + this.comp.id];
      mv.initColors();
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
      }else if(col =='clear'){
        delete this.comp.style;
      }else{
        bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"param", options:{col:col, val:s[col], ts: this}});
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
