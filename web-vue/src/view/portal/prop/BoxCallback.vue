<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="数据块回调函数设置" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">数据块回调函数设置</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
      </template>
     function
     <el-input style="width:100px;" v-model="box.funcname" ></el-input>
     (<b>value</b>, <b>row</b>){
       <el-input type="textarea" :rows="10" v-model="box.code" ></el-input>
     }
     <p class="text-warning">说明：value表示当前值, row表示当前行，此回调函数需要通过return返回内容</p>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">确 定</el-button>
        <el-button @click="show = false">取 消</el-button>
      </div>
    </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
        show:false,
        box:{
          funcname:null,
          code:null
        },
        kpi:null
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    openDailog(kpi){
      this.show = true;
      this.kpi = kpi;
      if(kpi.funcname){
        this.box.funcname = kpi.funcname;
      }else{
        this.box.funcname = "f"+Math.round(Math.random() * 10000);
      }
      if(kpi.code){
        this.box.code = unescape(kpi.code);
      }
    },
    save(){
      var funcname = this.box.funcname;
      var code =  this.box.code;
      var kpi = this.kpi;
      if( code == null || code === ""){
        delete kpi.funcname;
        delete kpi.code;
      }else{
        kpi.funcname = funcname;
        kpi.code = escape(code);
      }
      this.show = false;
      this.$parent.setUpdate();
      this.$parent.boxView();
    }
  }
}
</script>
