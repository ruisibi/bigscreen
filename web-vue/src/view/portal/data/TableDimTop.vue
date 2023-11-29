<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="维度取Top" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">维度取Top</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="tableForm" :model="prop" size="mini">
         <el-form-item label="维度取Top：" label-width="100px" prop="top">
             <el-input-number v-model="prop.top" :min="1" size="mini"></el-input-number>
         </el-form-item>
         <el-form-item label="" label-width="100px" prop="type">
           <el-radio-group v-model="prop.type" size="mini">
              <el-radio label="number" border>数字</el-radio>
              <el-radio label="percent" border>百分比</el-radio>
            </el-radio-group>
				</el-form-item>
         <el-form-item label="" label-width="100px">
           <el-button type="danger" @click="clearTop()" plain size="mini">清除取Top</el-button>
        </el-form-item>
      </el-form>
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
import * as utils from '@/view/portal/Utils'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
      show:false,
      prop:{
        top:null,
        type:"number",
      },
      node:null,
      comp:null,
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    openDailog(node, comp){
      this.show = true;
      this.node = node;
      this.comp = comp;
      let dim = comp.dims.filter(m=>m.id===this.node.match)[0];
      this.prop.top = dim.top;
      this.prop.type = dim.topType || "number";
    },
    save(){
      let comp = this.comp;
      let dim = comp.dims.filter(m=>m.id===this.node.match)[0];

      dim.top = this.prop.top;
      dim.topType = this.prop.type;

      this.show = false;
      let p = this.$parent;
      p.setUpdate();
      p.tableView();
    },
    clearTop(){
      let comp = this.comp;
      let dim = comp.dims.filter(m=>m.id===this.node.match)[0];
      delete dim.top;
      delete dim.topType;

      this.show = false;
      let p = this.$parent;
      p.setUpdate();
      p.tableView();
    }
  }
}
</script>
