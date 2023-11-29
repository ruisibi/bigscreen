<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!--
大屏组件位置信息面板
 -->
<template>
  <el-form :model="comp" ref="propForm" label-position="left" size="mini">
      <el-form-item label="纵向(Top)：" label-width="110px">
        <el-input-number size="small" v-model="comp.top" :precision="0" controls-position="right" @change="changevalue('top')"></el-input-number>
      </el-form-item>
      <el-form-item label="横向(Left)：" label-width="110px">
        <el-input-number size="small" v-model="comp.left" :precision="0" controls-position="right" @change="changevalue('left')"></el-input-number>
      </el-form-item>
      <el-form-item label="宽度(Width)：" label-width="110px">
        <el-input-number size="small" :min="10" v-model="comp.width" :precision="0" controls-position="right" @change="changevalue('width')"></el-input-number>
      </el-form-item>
      <el-form-item label="高度(Height)：" label-width="110px">
        <el-input-number size="small" :min="10" v-model="comp.height" :precision="0" controls-position="right" @change="changevalue('height')"></el-input-number>
      </el-form-item>
      <el-form-item label="图层(z-Index)：" label-width="110px">
        <el-input-number size="small" v-model="comp.zIndex" :precision="0" controls-position="right" @change="changevalue('zIndex')"></el-input-number>
      </el-form-item>
  </el-form>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import {baseInfo, findCompById, compExistInSelect, createTextStyle} from '@/view/bigscreen/bsUtils'

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

    }
  },
  mounted(){

  },
  computed: {

  },
  methods: {
    init(){

    },
    setUpdate(){
      this.$parent.$parent.$parent.$parent.setUpdate();
    },
    compRender(){
      this.$parent.$parent.$parent.$parent.compRender();
    },
    changevalue(col){

      this.setUpdate();
    },
  },
  watch: {

  }
}
</script>
