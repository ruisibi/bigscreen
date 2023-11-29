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
          <el-form-item label="边框颜色：" label-width="210px">
              <el-color-picker v-model="prop.compBorderColor" @change="changevalue('compBorderColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="边框宽度：" label-width="110px">
            <el-slider v-model="prop.compBorderWidth" :min="0" :max="30" @change="changevalue('compBorderWidth')"></el-slider>
          </el-form-item>
          <el-form-item label="边框类型：" label-width="110px">
            <el-select v-model="prop.compBorderStyle" @change="changevalue('compBorderStyle')" :clearable="true" placeholder="请选择">
                <el-option
                v-for="item in opt.borderTypes"
                :key="item.value"
                :label="item.name"
                :value="item.value">
                </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="圆角半径：" label-width="110px">
            <el-slider v-model="prop.borderRadius" :min="0" :max="30" @change="changevalue('borderRadius')"></el-slider>
          </el-form-item>
          <el-form-item label="背景色：" label-width="210px">
              <el-color-picker v-model="prop.bgcolor" @change="changevalue('bgcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="背景色渐变：" label-width="175px">
              <el-color-picker v-model="prop.bgcolor1" :show-alpha="true" @change="changevalue('bgcolor1')"></el-color-picker>
              <el-color-picker v-model="prop.bgcolor2" :show-alpha="true" @change="changevalue('bgcolor2')"></el-color-picker>
          </el-form-item>
          <el-form-item label="旋转：" label-width="110px">
              <el-slider v-model="prop.rotate" :min="0" :max="180" @change="changevalue('rotate')"></el-slider>
          </el-form-item>
          <el-form-item label="透明度：" label-width="110px">
            <el-slider v-model="prop.opacity" :min="0" :max="100" @change="changevalue('opacity')"></el-slider>
          </el-form-item>
          <el-form-item label="缩放：" label-width="110px">
            <el-slider v-model="prop.zoom" :min="0" :max="100" @change="changevalue('zoom')"></el-slider>
          </el-form-item>
          <el-form-item label="边框阴影：" label-width="200px">
            <el-switch v-model="prop.useShadow" @change="changevalue('useShadow')"></el-switch>
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
         compBorderColor:'#dee5e7',
         compBorderWidth:1,
         compBorderStyle:'solid',
         borderRadius:0,
         bgcolor:null,
         bgcolor1:null,
         bgcolor2:null,
         opacity:100,
         useShadow:false,
         rotate:0,
         zoom:null,
      },
      opt:{
          borderTypes:[{value:"solid", name:"实线"}, {value:"dashed", name:"虚线"}, {value:"dotted", name:"点划线"}],
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
        }
    },
    compRender(){
      var o = this.$parent.$parent;
       o.$forceUpdate();
       o.$refs['optarea'].$forceUpdate();
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
        bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"decopropset", options:{col:col, val:s[col], ts: this}});
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
