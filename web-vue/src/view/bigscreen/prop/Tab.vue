<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<template>
    <el-form :model="prop" ref="propForm" label-position="left" size="mini">
      <el-collapse v-model="activeName" accordion>
        <el-collapse-item title="组件属性" name="1">
           <el-form-item label="图层名称：" label-width="110px">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
         <el-form-item label="选项卡数量：" label-width="110px">
            <el-slider v-model="prop.tabCount" :min="2" :max="10" @change="changevalue('tabCount')"></el-slider>
          </el-form-item>
          <el-form-item label="文本颜色：" label-width="210px">
            <el-color-picker v-model="prop.color" @change="changevalue('color')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-slider v-model="prop.size" :max="60" :min="9" @change="changevalue('size')"></el-slider>
          </el-form-item>
          <el-form-item label="显示模式：" label-width="110px">
            <el-select v-model="prop.tabModel" placeholder="请选择" @change="changevalue('tabModel')">
                <el-option
                  v-for="item in opts.tabModels"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="风格：" label-width="110px">
            <el-select v-model="prop.tabStyle" placeholder="请选择" @change="changevalue('tabStyle')">
                <el-option
                  v-for="item in opts.tabStyles"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="过度动画：" label-width="110px">
            <el-select v-model="prop.animate" placeholder="请选择" clearable @change="changevalue('animate')">
                <el-option
                  v-for="item in opts.animates"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="自动切换：" label-width="200px">
              <el-switch v-model="prop.autoswitch" @change="changevalue('autoswitch')"></el-switch>
          </el-form-item>
          <el-form-item label="间隔(秒)：" label-width="110px" v-if="prop.autoswitch == true">
            <el-input-number v-model="prop.switchStep" :min="1" @change="changevalue('switchStep')"></el-input-number>
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
         tabCount: null,
         color: null,
         size: null,
         animate: null,
         tabStyle: "def",
         autoswitch: null,
         switchStep: 5,
         tabModel: "def",
      },
      opts:{
        tabStyles:[{text:"默认", value:"def"}, {text:"风格1", value:"style1"}],
        animates: [{text:"淡入淡出", value:"el-fade-in"},{text:"缩放", value:"el-zoom-in-center"}, {text:"向左滑动", value:"SlideIn"}],
        tabModels:[{text:"选项卡", value:"def"},
        // {text:"圆点", value:"circle"},
         {text:"方块", value:"block"}]
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
          this.prop.color = null;
          this.prop.size = null;
          this.prop.tabStyle = "def";
          this.prop.autoswitch = null;
          this.prop.switchStep = 5;
          this.prop.tabModel = "def";
          this.prop.animate = null;
        }
        //通过comp.tabs 等length 控制 选项卡个数
        this.prop.tabCount = this.comp.tabs.length;
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
      }else if(col == 'tabCount'){
        if(val > this.comp.tabs.length){  //添加
         for(let i=this.comp.tabs.length; i<val; i++){
            this.comp.tabs.push({name:"tab" + (i+ 1)});
          }
        }else{ //移除
          this.comp.tabs.splice(val, this.comp.tabs.length - val);
        }
      }else{
        bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"tab", options:{col:col, val:s[col], ts: this}});
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
