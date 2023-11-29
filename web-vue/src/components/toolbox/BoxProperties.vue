<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!--
报表 组件 外框 的样式设置组件
 -->
<template>
  <el-form :model="prop" ref="propForm" label-position="left" size="mini">
      <template v-if="showCompProp">
         <el-form-item label="标签：" label-width="110px" v-if="useIn==='dashboard'">
          <el-input v-model="prop.labelName" placeholder="用在合并选项卡中" @change="changevalue('labelName')"></el-input>
        </el-form-item>
        <el-form-item label="背景颜色：" label-width="210px">
         <el-color-picker v-model="prop.pageBgColor" @change="changevalue('pageBgColor')"></el-color-picker>
        </el-form-item>
        <el-form-item label="背景图片：" label-position="top" label-width="110px">
          <resourceSelect ref="resourceSelectForm" column="compBgImage" :cb="getBgImg" :prop="prop"></resourceSelect>
        </el-form-item>
        <el-form-item label="禁用背景：" label-width="200px">
          <el-switch v-model="prop.disableBg" @change="changevalue('disableBg')"></el-switch>
        </el-form-item>
        <el-form-item label="组件高度：" label-width="110px" v-if="useIn=='report'">
            <el-input-number v-model="comp.height" controls-position="right" :precision="0"  @change="changevalue('height')"></el-input-number>
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
        <el-form-item label="圆角边框：" label-width="110px">
          <el-input-number v-model="prop.radius" :max="100" :min="0" @change="changevalue('radius')"></el-input-number>
        </el-form-item>
        <el-form-item label="禁用中线边框：" label-width="200px">
          <el-switch v-model="prop.disableCenterBorder" @change="changevalue('disableCenterBorder')"></el-switch>
        </el-form-item>
        <el-form-item label="启用阴影：" label-width="200px">
          <el-switch v-model="prop.useShadow" @change="changevalue('useShadow')"></el-switch>
        </el-form-item>
      </template>
        <template v-if="showTitleProp">
           <el-form-item label="标题：" label-width="110px">
              <el-input v-model="prop.name" @change="changevalue('name')"></el-input>
            </el-form-item>
            <el-form-item label="位置：" label-width="110px">
              <el-select v-model="prop.titleAlign" @change="changevalue('titleAlign')" :clearable="true" placeholder="请选择">
                <el-option
                  v-for="item in opt.aligns"
                  :key="item.value"
                  :label="item.name"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="字体大小：" label-width="110px">
              <el-slider v-model="prop.titleSize" :min="9" :max="42" @change="changevalue('titleSize')"></el-slider>
            </el-form-item>
             <el-form-item label="隐藏标题(浏览模式生效)：" v-if="showCompTitle()" label-width="200px">
              <el-switch v-model="prop.hideTitle" @change="changevalue('hideTitle')"></el-switch>
            </el-form-item>
            <el-form-item label="是否粗体：" label-width="200px">
              <el-switch v-model="prop.titleBold" @change="changevalue('titleBold')"></el-switch>
            </el-form-item>
            <el-form-item label="字体颜色：" label-width="200px">
              <el-color-picker v-model="prop.titleColor" @change="changevalue('titleColor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="背景颜色：" label-width="200px">
              <el-color-picker v-model="prop.titleBgColor" @change="changevalue('titleBgColor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="禁用背景：" label-width="200px">
              <el-switch v-model="prop.disableHeadBg" @change="changevalue('disableHeadBg')"></el-switch>
            </el-form-item>
            <el-form-item label="边框颜色：" label-width="200px">
              <el-color-picker v-model="prop.titleBorderColor" @change="changevalue('titleBorderColor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="边框宽度：" label-width="110px">
              <el-slider v-model="prop.titleBorderWidth" :min="0" :max="32" @change="changevalue('titleBorderWidth')"></el-slider>
            </el-form-item>
            <el-form-item label="边框类型：" label-width="110px">
              <el-select v-model="prop.titleBorderStyle" @change="changevalue('titleBorderStyle')" :clearable="true" placeholder="请选择">
                <el-option
                  v-for="item in opt.borderTypes"
                  :key="item.value"
                  :label="item.name"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
        </template>
  </el-form>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import resourceSelect from '@/components/toolbox/ResourceSelect'
import boxProperties from '@/components/toolbox/BoxProperties'

export default {
  name:"boxProperties",
  components:{
    boxProperties, resourceSelect
  },
  props:{
      comp:{
        type:Object,
        required:true
      },
      showCompProp:{
          type:Boolean,
          required:true
      },
      showTitleProp:{
          type:Boolean,
          required:true
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:false,
      },
  },
  data(){
    return {
      prop:{
        pageBgColor:null,
        compBgImage:null,
        disableBg:null,
        compBorderColor:"#edf1f2",
        compBorderWidth:1,
        compBorderStyle:"solid",
        disableCenterBorder:null,
        useShadow:null,
        labelName:null,
        hideTitle: false,
        name:null,
        radius:null,
        titleAlign:"left",
        titleSize:14,
        titleBold:null,
        titleColor:null,
        titleBgColor:"#f6f8f8",
        disableHeadBg:null,
        titleBorderColor:null,
        titleBorderWidth:null,
        titleBorderStyle:"solid",
      },
      opt:{
        borderTypes:[{value:"solid", name:"实线"}, {value:"dashed", name:"虚线"}, {value:"dotted", name:"点划线"}],
        aligns:[{value:"left", name:"居左"},{value:"center", name:"居中"},{value:"right", name:"居右"}]
      }
    }
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    init(){
      this.prop.name = this.comp.name;
      this.prop.labelName = this.comp.labelName;
      //回写 组件 style 的值
      let cs = this.comp.style;
      if(cs){
        for(let c in cs){
          let v = cs[c];
          if(v){
            this.prop[c] = v;
          }
        }
      }
    },
    showCompTitle(){
      return this.useIn == 'report'
    },
    setUpdate(){
      this.$parent.$parent.$parent.$parent.setUpdate();
    },
    compRender(){
      this.$parent.$parent.$parent.$parent.compRender();
    },
    getBgImg(col, val){
      this.changevalue(col);
    },
    changevalue(col){
      let comp = this.comp;
       let v = this.prop[col];
        if(!comp.style){
        comp.style = {};
      }
      if(col === 'name'){
        comp.name = v;
      }else if(col === 'labelName'){
        comp.labelName = v;
      }else if(col === 'height'){
        //comp.height = v;
      }else{
        comp.style[col] = v;
      }
      //console.log(comp.style);
      this.setUpdate();
      this.compRender();
    }
  },
  watch: {

  }
}
</script>
