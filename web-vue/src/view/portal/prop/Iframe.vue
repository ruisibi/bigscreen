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
        <el-collapse-item title="位置信息" name="4" v-if="useIn === 'bigscreen'">
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
  },
  data(){
    return {
      activeName:this.useIn === 'bigscreen' ? "4" :"1",
      prop:{
        title:null,
        content:null,
        color:null,
        size:13,
        lineheight:30,
        align:null,
        bold:false,
        italic:false,
        underline:false,

      },

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
      }

    },
    compRender(){
      this.$parent.$parent.$forceUpdate();
      this.$parent.$parent.$refs['optarea'].$forceUpdate();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    changevalue(col){
      let c = this.comp;
      let v = this.prop[col];
    }
  },
  watch: {

  }
}
</script>
