<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!--
定义组件的滚动条样式
 -->
<template>
  <el-form :model="prop" ref="propForm" label-position="left" size="mini">
      <el-form-item label="滚动条宽度：" label-width="110px">
        <el-slider v-model="prop.scrollWidth" :max="20" :min="0" @change="changevalue('scrollWidth')"></el-slider>
      </el-form-item>
      <el-form-item label="滑块颜色：" label-width="110px">
        <el-color-picker v-model="prop.scrollThumbColor" @change="changevalue('scrollThumbColor')"></el-color-picker>
      </el-form-item>
      <el-form-item label="滑块圆角：" label-width="110px">
        <el-slider v-model="prop.scrollThumbRadius" :min="0" :max="10" @change="changevalue('scrollThumbRadius')" />
      </el-form-item>
      <el-form-item label="轨道颜色：" label-width="110px">
        <el-color-picker v-model="prop.scrollTrackColor" @change="changevalue('scrollTrackColor')"></el-color-picker>
      </el-form-item>
  </el-form>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'

export default {
  components:{

  },
  props:{
      comp:{
        type:Object,
        required:true
      },
  },
  data(){
    return {
      prop:{
        scrollWidth: 10,
        scrollThumbColor:'#F5F5F5',
        scrollThumbRadius: 0,
        scrollTrackColor: '#F5F5F5',
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
      if(comp.scrollStyle){
       for(let s in comp.scrollStyle){
         this.prop[s] = comp.scrollStyle[s];
       }
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
      if(!comp.scrollStyle){
        comp.scrollStyle = {};
      }
      comp.scrollStyle[col] = this.prop[col];
      this.compRender();
      this.setUpdate();
    },
  },
  watch: {

  }
}
</script>
