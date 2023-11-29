<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="移至下级" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">移至下级</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="tableForm" :model="prop" :rules="rule" label-width="90px" size="mini">
        <el-form-item label="当前节点：" label-width="100px">
          {{ prop.name }}
        </el-form-item>
         <el-form-item label="上级节点：" label-width="100px" prop="upnode">
					<el-select v-model="prop.upnode" style="width:100%" placeholder="请选择">
						<el-option
						v-for="item in opt.nodes"
						:key="item.value"
						:label="item.name"
						:value="item.value">
						</el-option>
					</el-select>
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
        upnode:null,
        name:null
      },
      opt:{
        nodes:[]
      },
      rule:{
        upnode:[{ required: true, message: "必填", trigger: "blur" }],
      },
      ls:null,
      item:null,
      pos:null,
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    openDailog(ls, item, pos){
      this.show = true;
      this.ls = ls;
      this.item = item;
      this.pos = pos;
      this.prop.name = item.desc;
      this.opt.nodes = [];
      this.prop.upnode = null;
      utils.loopDims(ls, t=>{
        if(t.level >= item.level && t.id !== item.id){
          if(t.type === 'note') {
            return;
          }
          let name = t.desc;
          this.opt.nodes.push({value:t.id, name: name});
        }
      });
    },
    save(){
      this.$refs['tableForm'].validate((valid)=>{
        if(valid){
          let o = this.$parent;
            let target = null;
            let ls = this.ls;
            let item = this.item;
            let pos = this.pos;
            utils.loopDims(ls, (node, nodes, idx)=>{
              if(node.id === item.id){
                target = node;
                nodes.splice(idx, 1);  //同时移除此节点
              }
            });
            utils.loopDims(ls, node=>{
              if(node.id == this.prop.upnode ){
                node.children.push(target);
                //更新level
                target.level = node.level + 1;
              }
            });
            o.setRowHeader(pos, target);
            this.show = false;
            o.setUpdate();
            o.tableView();
        }
      });
    },
  }
}
</script>
