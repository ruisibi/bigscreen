<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div style="height:100%">
    <template v-if="useIn !=='dashboardEdit'">
       <template v-if="comp.url">
        <iframe width="100%" height="100%" frameborder="no" border="0" scrolling="auto" :src="comp.url">
        </iframe>
       </template>
        <template v-if="!comp.url">
          <div>还未配置嵌套框URL</div>
        </template>
    </template>

    <template v-if="useIn === 'dashboardEdit'">
        <el-form :model="prop" ref="urlForm" :rules="rules" label-position="left" size="mini">
        <el-form-item label="URL地址" label-width="140px" prop="url">
        <el-input v-model="prop.url"></el-input>
        </el-form-item>
      </el-form>
    </template>
  </div>
</template>>
<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'
import * as echartsUtils from '@/common/echartsUtils'

export default {
  components:{
  },
  data(){
    return {
      prop:{
        url:""
      },
      rules:{
        url:[
						{ required: true, message: '必填', trigger: 'blur' },
					],
      }
    }
  },
  props:{
      comp:{
        type:Object,
        required:true,
        default:()=>{}
      },
      editor:{
        type:Boolean,
        required:true,
        default:true
      },
      portalParams:{
        type:Array,
        required:false
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
  },
  mounted(){
    this.prop.url = this.comp.url;
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){

    },
    getUrl(){
      return this.prop.url;
    },
  },
  watch: {

  }
}
</script>
