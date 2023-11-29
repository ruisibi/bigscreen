<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="插入备注行" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">插入备注行</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="tableForm" :model="prop" :rules="rule" label-width="90px" size="mini">
        <el-form-item label="当前节点：" label-width="100px" prop="name">
          {{prop.name}}
        </el-form-item>
        <el-form-item label="插入位置：" label-width="100px" prop="insertpos">
            <el-radio-group v-model="prop.insertpos" size="small">
              <el-radio label="up" border>上方</el-radio>
              <el-radio label="down" border>下方</el-radio>
            </el-radio-group>
				</el-form-item>
        <el-form-item label="节点名称：" label-width="100px" prop="nodeName">
           <el-input v-model="prop.nodeName" />
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
import {baseUrl, newGuid} from '@/common/biConfig'
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
        name:null,
        insertpos:"up",
        nodeName:null
      },
      node:null,
      comp:null,
      rule:{
        nodeName:[{ required: true, message: "必填", trigger: "blur" }],
      },
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
      utils.loopDims(comp.rows, (a) => {
        if(a.id === node.id) {
          this.prop.name = a.desc;
          return false;
        }
      });
    },
    save(){
      this.$refs['tableForm'].validate((valid)=>{
        if(valid){
          let o = this.$parent;
          this.show = false;
          let comp = this.comp;
          let node = this.node;
          let insertpos = this.prop.insertpos;
          //调整JSON
          utils.loopDims(comp.rows, (a, b, c) =>{
            if(a.id === node.id){
              const node = {id:newGuid(), type:"note",desc:this.prop.nodeName, level:a.level, children:[]};
              if(b.length === 1){
                if("up" === insertpos){
                  b.splice(c, 0, node);
                }else{
                  b.push(node);
                }
              }else {
                b.splice("up" === insertpos ? c : c+ 1, 0, node);
              }
              return false;
            }
          });
          o.setUpdate();
          o.tableView();
        }
      });
    },
  }
}
</script>
