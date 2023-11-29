<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <el-form :model="prop" ref="propForm" label-position="left" size="mini">
     <el-collapse v-model="activeName" accordion>
       <el-collapse-item title="组件属性" name="1" v-if="useIn === 'report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm1" :comp="comp" :useIn="useIn" :showCompProp="true" :showTitleProp="false"></boxProperties>
       </el-collapse-item>
       <el-collapse-item title="标题属性" name="2" v-if="useIn === 'report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm2" :comp="comp" :useIn="useIn" :showCompProp="false" :showTitleProp="true"></boxProperties>
       </el-collapse-item>
       <el-collapse-item title="组件属性" name="3" v-if="useIn === 'bigscreen'">
          <el-form-item label="图层名称：" label-width="100px">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
          <el-form-item label="文本内容：" label-width="100px">
            <el-input type="textarea" v-model="prop.content" placeholder="支持HTML代码" @change="changevalue('content')"></el-input>
          </el-form-item>
          <el-form-item label="字体：" label-width="100px">
            <el-select v-model="prop.family" @change="changevalue('family')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.familys"
                :key="item.en"
                :label="item.ch"
                :value="item.en">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="文本颜色：" label-width="210px">
            <el-color-picker v-model="prop.color" @change="changevalue('color')"></el-color-picker>
          </el-form-item>
          <el-form-item label="渐变色：" label-width="210px">
            <el-color-picker v-model="prop.color1" @change="changevalue('color1')"></el-color-picker>
            <el-color-picker v-model="prop.color2" @change="changevalue('color2')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-input-number v-model="prop.size" :min="9" :precision="0" controls-position="right" @change="changevalue('size')"></el-input-number>
          </el-form-item>
          <el-form-item label="行高：" label-width="110px">
               <el-input-number size="small" v-model="prop.lineheight" :min="20" :precision="0" controls-position="right" @change="changevalue('lineheight')"></el-input-number>
          </el-form-item>
           <el-form-item label="间距：" label-width="110px">
            <el-input-number v-model="prop.spacing" :min="0" :max="100" :precision="0" controls-position="right" @change="changevalue('spacing')"></el-input-number>
          </el-form-item>
          <el-form-item label="位置：" label-width="110px">
            <el-select v-model="prop.align" @change="changevalue('align')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.aligns"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="背景色：" label-width="210px">
            <el-color-picker v-model="prop.bgcolor" @change="changevalue('bgcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="是否粗体：" label-width="200px">
            <el-switch v-model="prop.bold" @change="changevalue('bold')"></el-switch>
          </el-form-item>
          <el-form-item label="是否斜体：" label-width="200px">
            <el-switch v-model="prop.italic" @change="changevalue('italic')"></el-switch>
          </el-form-item>
          <el-form-item label="是否下划线：" label-width="200px">
            <el-switch v-model="prop.underline" @change="changevalue('underline')"></el-switch>
          </el-form-item>
          <el-form-item label="文本滚动：" label-width="200px" v-if="useIn=='bigscreen'">
            <el-switch v-model="prop.scroll" @change="changevalue('scroll')"></el-switch>
          </el-form-item>
       </el-collapse-item>
        <el-collapse-item title="位置信息" name="4" v-if="useIn === 'bigscreen'">
            <posProperties :comp="comp" ref="posPropForm"></posProperties>
       </el-collapse-item>
       <el-collapse-item title="文本属性" name="6" v-if="useIn === 'report' || useIn === 'dashboard'">
         <el-form-item label="数据集/表：" label-width="110px">
            <div align="right">{{ comp.comp ? comp.comp.tname : "" }}</div>
          </el-form-item>
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
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  components:{
    boxProperties, posProperties
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
      activeName:this.useIn === 'bigscreen' ? "3" :"1",
      prop:{
        title:null,
        content:null,
        color:null,
        color1:null, //渐变色1
        color2:null,  //渐变色2
        size:13,
        lineheight:30,
        align:null,
        bold:false,
        italic:false,
        underline:false,
        family:null,
        bgcolor:null,
        scroll:false,
        spacing:null,
      },
      opt:{
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
      let comp = this.comp;
      if(this.useIn === 'report' || this.useIn === 'dashboard'){
        this.$refs['boxPropForm1'].init();
        this.$refs['boxPropForm2'].init();
      }else if(this.useIn === 'bigscreen'){
        this.$refs['posPropForm'].init();
        this.prop.title = comp.title;
        this.prop.content = comp.content;

         //回写 tstyle
        if(this.comp.style && !$.isEmptyObject(this.comp.style)){
          for(let item in comp.style){
            this.prop[item] = comp.style[item];
          }
        }else{
          this.prop.color = null,
          this.prop.color1 = null, //渐变色1
          this.prop.color2 = null,  //渐变色2
          this.prop.size = 13,
          this.prop.lineheight = 30,
          this.prop.align = null,
          this.prop.bold = false,
          this.prop.italic = false,
          this.prop.underline = false,
          this.prop.family = null;
          this.prop.bgcolor = null;
          this.prop.scroll = null;
          this.prop.spacing = null;
        }
      }
    },
    compRender(){
       var o = this.$parent.$parent;
       o.$refs['optarea'].$forceUpdate();
       o.$refs['optarea'].$refs['mv_' + this.comp.id].$forceUpdate();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    changevalue(col){
      let c = this.comp;
      let v = this.prop[col];
      if(!c.style){
        c.style = {};
      }
      let s = c.style;

      if(col === 'title'){
          c.title = v;
      }else if(col === 'content'){
          bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"textpropset", options:{use:"content",col:col, val:c.content, ts: this}});
          c.content = v;
      }else if(col === 'color' ||  col === 'bgcolor' || col == 'family' || col === 'color1' || col === 'color2' || col ==='size' || col === 'align' || col === 'bold' ||
      col === 'italic' || col === 'underline' || col === 'lineheight' || col == 'scroll' || col == 'spacing'){
         bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"textpropset", options:{use:"style",col:col, val:s[col], ts: this}});
         s[col] = v;
         this.compRender();
      }
      this.setUpdate();
    },
    //撤销操作
    goback(options){
      if(options.use === 'style'){
        this.comp.style[options.col] = options.val;
        this.prop[options.col] = options.val;
        this.compRender();
      }else if(options.use === 'content'){
        this.comp.content = options.val;
        this.prop[options.col] = options.val;
        this.compRender();
      }
    }
  },
  watch: {

  }
}
</script>
