<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="列信息设置" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">列信息设置</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="tableForm" :model="prop" :rules="rule" size="mini">
        <el-tabs v-model="activeName">
          <el-tab-pane label="基本信息" name="base">
         <el-form-item label="显示名称：" label-width="100px" prop="dispName">
           <el-input v-model="prop.dispName" />
				</el-form-item>
        <el-form-item label="所属表：" label-width="100px" prop="dispName">
           {{prop.tname}}
				</el-form-item>
        <el-form-item label="对应字段：" label-width="100px" prop="colname">
           {{prop.colname}}
				</el-form-item>
         <el-form-item label="格式化：" label-width="100px" prop="fmt">
           <el-input v-model="prop.fmt" />
				</el-form-item>
        <el-form-item label="位置：" label-width="100px" prop="fmt">
           <el-select v-model="prop.palign" :clearable="true" style="width:100%" placeholder="请选择">
              <el-option
                v-for="item in opt.aligns"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
				</el-form-item>
          </el-tab-pane>
           <el-tab-pane label="回调函数" name="cb">
            function
            <el-input style="width:100px;" v-model="prop.funcname" readonly="true" size="mini"></el-input>
            (<b>value</b>, <b>row</b>){
              <el-input type="textarea" :rows="10" v-model="prop.code" size="mini"></el-input>
            }
            <p class="text-warning">说明：value参数表示当前值，row表示当前行，此回调函数需要通过return返回内容</p>
          </el-tab-pane>
        </el-tabs>
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
      activeName: "base",
      prop:{
        dispName:null,
        tname:null,
        colname:null,
        fmt:null,
        palign:null,
        funcname: null,
        code: null,
      },
      node:null,   //当前列
      comp: null,
      rule:{
      },
      opt:{
        aligns:[{value:"left", name:"居左"},{value:"center", name:"居中"},{value:"right", name:"居右"}],
      }
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
    openDailog(node,  comp){
      this.show = true;
      this.node = node;
      this.comp = comp;
      this.prop.dispName = node.dispName?node.dispName:node.name;
      this.prop.tname = comp.tname;
      this.prop.colname = node.name;
      this.prop.fmt = node.fmt;
      this.prop.palign = node.align;
      this.prop.funcname = node.funcname || ("f"+ (Math.round(Math.random() * 1000)));
      if(node.code){
        this.prop.code = unescape(node.code);
      }else{
        this.prop.code = null;
      }
    },
    save(){
      this.$refs['tableForm'].validate((valid)=>{
        if(valid){
          let o = this.$parent;
          this.show = false;
          let comp = this.comp;
          let col = this.node;

          col.dispName = this.prop.dispName;
					col.fmt = this.prop.fmt;
          col.align = this.prop.palign;
          col.funcname = this.prop.funcname;
          if(this.prop.code){
            col.code = escape(this.prop.code);
          }else{
            col.code = null;
          }
          o.setUpdate();
          o.detailView();
        }
      });
    },
  }
}
</script>
