<!--
 * Copyright 2021 本系统版权归中移在线服务有限公司
 -->
<template>
    <el-dialog title="移动字段" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
              <span class="el-dialog__title">移动字段</span>
              <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
              <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
              </button>
            </template>
        <el-form :model="val" ref="valForm" :rules="rule" label-position="left" size="mini">
          <el-form-item label="当前节点：" label-width="110px" >
              {{dim ? dim.name : ""}}
          </el-form-item>
          <el-form-item label="移动到：" label-width="110px" prop="node">
              <el-select v-model="val.node" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in opt.nodes"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="位置：" label-width="110px" prop="pos">
              <el-radio-group v-model="val.pos" size="mini">
                <el-radio label="left" border>左边</el-radio>
                <el-radio label="right" border>右边</el-radio>
              </el-radio-group>
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
import {baseUrl, newGuid, ajax} from '@/common/biConfig'
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
      val:{
        pos:null,
        node:null,
      },
      rule:{
        node: [{ required: true, message: "必填", trigger: "blur" }],
        pos: [{ required: true, message: "必填", trigger: "blur" }],
      },
      comp: null,
      dims:null,
      dim:null,
      opt:{
        nodes:[], //待移动字段
      }
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
    showDailog(comp, dims, id){
      this.show = true;
      this.comp = comp;
      this.dims = dims;
      let nodes = [];
      $(dims).each((a, b)=>{
        if(b.id != id){
          nodes.push(b);
        }
      });
      this.opt.nodes = nodes;
      this.dim = dims.filter(m=>m.id == id)[0];
      this.val.pos = null;
      this.val.node = null;
    },
    save(){
      	this.$refs['valForm'].validate((valid) => {
          if(valid){
            let curNode = null;
            $(this.dims).each((a, b)=>{
              if(b.id === this.dim.id){
                curNode = b;
                this.dims.splice(a, 1);  //先移除
                return false;
              }
            });
            $(this.dims).each((a, b)=>{
               if(b.id === this.val.node){
                 let p = this.val.pos;
                 this.dims.splice(p=='left'?a:a + 1, 0, curNode)  //再添加
                 return false;
               }
            })
            this.show = false;
            this.$parent.setUpdate();
            this.$parent.gridView();
          }
        });
    }
  },
  watch: {

  }
}
</script>
