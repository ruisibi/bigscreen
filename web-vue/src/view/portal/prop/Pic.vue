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
          <el-form-item label="图层名称：" label-width="110px">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
           <el-form-item label="图片地址：" label-width="110px">
               <resourceSelect ref="resourceSelectForm" column="src" :cb="getImg" :prop="prop"></resourceSelect>
            </el-form-item>
          <el-form-item label="动画：" label-width="110px">
            <el-select v-model="prop.animation" @change="changevalue('animation')" :clearable="true">
              <el-option
                v-for="item in opts.animations"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="动画速度：" label-width="110px" v-if="prop.animation">
            <el-slider v-model="prop.duration" :max="50" :min="1" @change="changevalue('duration')"></el-slider>
          </el-form-item>
          <el-form-item label="透明度：" label-width="110px">
            <el-slider v-model="prop.opacity" :max="100" :min="0" @change="changevalue('opacity')"></el-slider>
          </el-form-item>
       </el-collapse-item>
         <el-collapse-item title="位置信息" name="7" v-if="useIn === 'bigscreen'">
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
      activeName: this.useIn === 'report' || this.useIn === 'dashboard'?"1":"3",
      prop:{
         title:null, //图层名称
         src:null,
         animation:false,
         duration:1,
         opacity:100,
      },
      opts:{
           animations:[{name:"旋转", value:"rotate"},{name:"上下跳动", value:"translate"},{name:"左右移动", value:"translate-left"}]
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
        this.prop.src = this.comp.src;
        this.prop.opacity = this.comp.opacity;
        this.prop.animation = this.comp.animation;
        this.prop.duration = this.comp.duration;
      }
    },
    compRender(){
      this.$parent.$parent.$forceUpdate();
      var o = this.$parent.$parent.$refs['optarea'];
      o.$forceUpdate();
      o.$refs['mv_'+this.comp.id].$forceUpdate();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    changevalue(col){
      let val = this.prop[col];
      let comp = this.comp;
      if(col != 'title'){ //title 图层名称不记录
       bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"picpropset", options:{col:col, val:comp[col], ts: this}});
      }
      if(col === 'src' || col === 'opacity' || col === 'animation' || col ==='duration' || col === 'title'){
          comp[col] = val;
      }
      this.compRender();
    },
    getImg(col, val){
      this.changevalue(col);
    },
    //撤销操作
    goback(options){
        this.comp[options.col] = options.val;
        this.prop[options.col] = options.val;
        this.compRender();
    }
  },
  watch: {

  }
}
</script>
