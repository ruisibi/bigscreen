<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <el-dialog title="节点属性" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
    <template slot="title">
      <span class="el-dialog__title">节点属性</span>
      <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
      </button>
    </template>
    <el-form ref="tableForm" :model="prop" :rules="rule" label-width="90px" size="mini">
      <el-form-item label="节点名称：" label-width="100px" prop="name">
        <el-input v-model="prop.name" />
      </el-form-item>
      <el-form-item label="宽度：" label-width="100px" prop="colwidth" size="mini" v-if="showSize">
        <el-input-number v-model="prop.colwidth" placeholder="默认90" :min="30"></el-input-number>
      </el-form-item>
      <el-form-item label="隐藏节点：" label-width="100px" prop="hideNodeCallback" v-if="showCallback">
        <p class="text-warning">设置隐藏节点的回调函数，函数 return true 表示隐藏，return false 表示显示。</p>
        <p class="text-warning">通过 extContext.get("参数标识") 获取参数值</p>
        function
        ( ) => {
        <codemirror :options="cmCfg" class="expscript" ref="myjscode" v-model="prop.hideNodeCallback"></codemirror>
        }
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
  import { codemirror } from 'vue-codemirror'
  require("codemirror/mode/javascript/javascript.js")

  export default {
    components:{
      codemirror
    },
    props:{

    },
    data(){
      return {
        show:false,
        showSize:true,
        showCallback:false,
        prop:{
          colwidth:null,
          name:null,
          hideNodeCallback: null,
        },
        node:null,
        comp: null,
        rule:{
          name:[{ required: true, message: "必填", trigger: "blur" }],
        },
        cmCfg:{ //sql编辑器配置
          mode: "text/javascript",
          indentWithTabs: true,
          smartIndent: true,
          lineNumbers: true,
          matchBrackets : true,
          autofocus: false,
          lineWrapping:true
        },
      }
    },
    mounted(){

    },
    computed: {
      ...mapState(["isMini"])
    },
    methods: {
      openDailog(node, comp, showSize){
        this.show = true;
        this.showSize = showSize;
        this.showCallback = !showSize;
        this.node = node;
        this.comp = comp;
        this.prop.colwidth = node.width;
        this.prop.name = node.desc;
        if(node.hideNodeCallback){
          this.prop.hideNodeCallback = unescape(node.hideNodeCallback);
        }else{
          this.prop.hideNodeCallback = "";
        }
      },
      save(){
        this.$refs['tableForm'].validate((valid)=>{
          if(valid){
            let o = this.$parent;
            this.show = false;
            let comp = this.comp;
            let node = this.node;
            if(this.showSize == true){
              node.width = this.prop.colwidth;
            }
            node.desc = this.prop.name;
            if(this.prop.hideNodeCallback){
              node.hideNodeCallback = escape(this.prop.hideNodeCallback);
            }else{
              delete node.hideNodeCallback;
            }
            /**
             //把表头宽度同步到列
             utils.loopDims(comp.rows, function (a, b) {
            if(a.level === id + 1){
              if(a.type === "dim" || a.type === 'kpiMatch'){
                var dim = comp.dims.filter(function (d) {
                  return d.id === a.match;
                })[0];
                dim.colwidth = colwidth;
              }else if(a.type === "kpi"){
                var kpi = comp.kpiJson.filter(function(k){
                  return k.kpi_id === a.match;
                })[0];
                kpi.colwidth = colwidth;
              }
            }
          });
             */
            o.setUpdate();
            o.tableView();
          }
        });
      },
    }
  }
</script>
