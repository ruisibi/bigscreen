<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="维度聚合" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">维度聚合</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="tableForm" :model="prop" size="mini">
         <el-form-item label="自动聚合：" label-width="100px" prop="autoaggre">
           <el-switch v-model="prop.autoaggre"></el-switch>
           <span class="text-warning">(设置后，聚合方式的选择功能既无效)</span>
         </el-form-item>
         <el-form-item label="聚合方式：" label-width="100px" prop="dimaggre">
					<el-select v-model="prop.dimaggre" style="width:100%" placeholder="请选择">
						<el-option
						v-for="item in opt.aggres"
						:key="item.value"
						:label="item.name"
						:value="item.value">
						</el-option>
					</el-select>
				</el-form-item>
        <el-form-item label="" label-width="100px">
           <el-button type="danger" @click="clearaggre()" plain size="mini">清除维度聚合</el-button>
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
        dimaggre:null,
        autoaggre:false,
      },
      opt:{
        aggres:[
						{name:"求和",value:"sum"},
						{name:"计数",value:"count"},
						{name:"平均",value:"avg"},
						{name:"最大",value:"max"},
						{name:"最小",value:"min"},
						{name:"方差",value:"var"},
						{name:"标准差",value:"sd"},
						{name:"中位数",value:"middle"}
					],
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
      this.prop.autoaggre = dim.aggre === 'auto';
      this.prop.dimaggre = dim.aggre;
    },
    save(){
      let comp = this.comp;
      let dim = comp.dims.filter(m=>m.id===this.node.match)[0];

      var autoaggre = this.prop.autoaggre;
      if(autoaggre == true){
        dim.aggre = "auto";
      }else{
        dim.aggre = this.prop.dimaggre;
      }
      dim.issum = 'y';

      this.show = false;
      let p = this.$parent;
      p.setUpdate();
      p.tableView();
    },
    clearaggre(){
      let comp = this.comp;
      let dim = comp.dims.filter(m=>m.id===this.node.match)[0];
      dim.issum = 'n';
      delete dim.aggre;
      this.show = false;
      let p = this.$parent;
      p.setUpdate();
      p.tableView();
    },
  }
}
</script>
