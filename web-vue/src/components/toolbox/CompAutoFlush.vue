<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!--
组件自动刷新
 -->
<template>
  <el-form :model="prop" ref="propForm" label-position="left" size="mini">
      <el-form-item label="是否启用(发布后生效)：" label-width="200px">
         <el-switch v-model="prop.use" @change="changevalue('use')"></el-switch>
      </el-form-item>
      <el-form-item label="间隔(秒)：" label-width="108px" v-if="prop.use == true">
        <el-input-number size="mini" :min="5" v-model="prop.flushstep" :precision="0" controls-position="right" @change="changevalue('flushstep')"></el-input-number>
      </el-form-item>
  </el-form>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'

export default {
  components:{

  },
  props:{
      comp:{
        type:Object,
        required:true,
      },
  },
  data(){
    return {
      prop:{
        use:false,
        flushstep:5,
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
      if(comp.flush){
        this.prop.use = comp.flush.use;
        this.prop.flushstep = comp.flush.flushstep
      }
    },
    setUpdate(){
      this.$parent.$parent.$parent.$parent.setUpdate();
    },
    compRender(){
      this.$parent.$parent.$parent.$parent.compRender();
    },
    changevalue(col){
      let comp = this.comp;
      if(!comp.flush){
        comp.flush = {};
      }
      comp.flush[col] = this.prop[col];
      if(comp.flush.use == true){

      }else{
        utils.stopFlush(comp);
      }
      this.setUpdate();
    },
  },
  watch: {

  }
}
</script>
