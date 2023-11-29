<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="度量属性" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">度量属性</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="tableForm" :model="prop" size="mini">
        <el-tabs v-model="activeName" @tab-click="chgtab">
          <el-tab-pane label="基本信息" name="base">
            <el-form-item label="度量名称：" label-width="120px">
               {{ prop.name }}
            </el-form-item>
            <el-form-item label="显示名称：" label-width="120px">
               <el-input v-model="prop.dispName"></el-input>
            </el-form-item>
            <el-form-item label="宽度：" label-width="120px">
              <el-input-number v-model="prop.colwidth" placeholder="默认值90" :min="60" :precision="0"></el-input-number>
            </el-form-item>
            <el-form-item label="度量所属表：" label-width="120px">
               {{ prop.tname }}
            </el-form-item>
            <el-form-item label="对应字段：" label-width="120px">
               {{ prop.col }}
            </el-form-item>
            <el-form-item label="度量单位：" label-width="120px">
                <el-select
                  v-model="prop.kpiunit" clearable
                  placeholder="请选择"
                  >
                  <el-option
                    v-for="item in opt.units"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  >
                </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="格式化：" label-width="120px">
                <el-select
                  v-model="prop.fmt" clearable
                  placeholder="请选择"
                  >
                  <el-option
                    v-for="item in opt.fmts"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  >
                 </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="启用表头排序：" label-width="120px">
                <el-switch v-model="prop.headsort" ></el-switch>
            </el-form-item>

          </el-tab-pane>
          <el-tab-pane label="回调函数" name="cb">
             function
            <el-input style="width:100px;" v-model="prop.funcname" size="mini"></el-input>
            (<b>value</b>, <b>col</b>, <b>row</b>, <b>data</b>, <b>outType</b>, <b>rowIndex</b>, <b>colIndex</b>){
              <el-input type="textarea" :rows="8" v-model="prop.code" ></el-input>
            }
            <br/>
            <a @click="tablecallbackhelper()" class="btn btn-primary btn-xs" href="javascript:;">帮助</a>
          </el-tab-pane>
          <el-tab-pane label="隐藏节点" name="hidenode">
            <p class="text-warning">设置隐藏节点的回调函数，函数 return true 表示隐藏，return false 表示显示。</p>
            <p class="text-warning">通过 extContext.get("参数标识") 获取参数值</p>
             function
            ( ) => {
            <codemirror :options="cmCfg" v-if="showhidenode" class="expscript" ref="myjscode2" v-model="prop.hideNodeCallback"></codemirror>
             }
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
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import { codemirror } from 'vue-codemirror'
import * as utils from '@/view/portal/Utils'
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
      activeName:"base",
      prop:{
       name:null,
       dispName:null,
       colwidth:null,
       tname:null,
       fmt:null,
       kpiunit:null,
       headsort:false,
       funcname:null,
       code:null,
       expression: null,
       hideNodeCallback: null,
      },
      opt:{
        units:utils.rates,
        fmts: utils.fmtJson,
        kpis:[],
      },
      node:null,
      comp:null,
      cmCfg:{ //sql编辑器配置
        mode: "text/javascript",
        indentWithTabs: true,
        smartIndent: true,
        lineNumbers: true,
        matchBrackets : true,
        autofocus: false,
        lineWrapping:true
      },
      showcompute:false,
      showhidenode: false,
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
      this.activeName = "base";
      this.showcompute = false;
      this.showhidenode = false;
      let kpi = comp.kpiJson.filter(k=>k&&k.kpi_id === node.match)[0];
      this.prop.name = kpi.kpi_name
      this.prop.dispName = node.desc;
      this.prop.tname = comp.tname;
      this.prop.col = kpi.col_name
      this.prop.fmt = kpi.fmt;
      this.prop.kpiunit = kpi.rate;
      this.prop.headsort = kpi.order;
      if(kpi.funcname){
       this.prop.funcname = kpi.funcname;
      }else{
        this.prop.funcname = "f"+Math.round(Math.random() * 10000);
      }
      if(kpi.code){
        this.prop.code = unescape(kpi.code);
      }else{
        this.prop.code = "";
      }
      if(kpi.expression){
        this.prop.expression = unescape(kpi.expression);
      }else{
        this.prop.expression = "";
      }
      if(kpi.hideNodeCallback){
        this.prop.hideNodeCallback = unescape(kpi.hideNodeCallback);
      }else{
        this.prop.hideNodeCallback = "";
      }

      this.prop.colwidth = kpi.colwidth;
      this.getKpis();
    },
    save(){
      let comp = this.comp;
      let node = this.node;
      let kpi = comp.kpiJson.filter(k=>k&&k.kpi_id === node.match)[0];
      kpi.colwidth = this.prop.colwidth;
      kpi.fmt = this.prop.fmt;
      kpi.rate = this.prop.kpiunit;
      kpi.funcname = this.prop.funcname;
      if(this.prop.code){
        kpi.code = escape(this.prop.code);
      }else{
        delete kpi.code;
        delete kpi.funcname;
      }
      node.desc = this.prop.dispName;
      kpi.order = this.prop.headsort;
      if(this.prop.expression){
        kpi.expression = escape(this.prop.expression);
      }else{
        delete kpi.expression;
      }
      if(this.prop.hideNodeCallback){
        kpi.hideNodeCallback = escape(this.prop.hideNodeCallback);
      }else{
        delete kpi.hideNodeCallback;
      }

      this.show = false;
      let p = this.$parent;
      p.setUpdate();
      p.tableView();
    },
    getKpis(){
      ajax({
        url:"model/getSubjectKpis.action",
        type:"GET",
        data:{tableId:this.comp.tid},
        success:(resp)=>{
          this.opt.kpis = resp.rows;

        }
      });
    },
    tablecallbackhelper(){
      window.open("#/portal/TableCallbackHelper");
    },
    selecme(v){
      v += " ";
      var o = this.$refs['myjscode'].codemirror;
      let pos1 = o.getCursor();
      let pos2 = {};
      pos2.line = pos1.line;
      pos2.ch = pos1.ch;
      if(o.somethingSelected){
        o.replaceSelection(v);
      }else{
        o.replaceRange(v,pos2);
      }
    },
    selectme2(v){
      v += " ";
      var o = this.$refs['myjscode2'].codemirror;
      let pos1 = o.getCursor();
      let pos2 = {};
      pos2.line = pos1.line;
      pos2.ch = pos1.ch;
      if(o.somethingSelected){
        o.replaceSelection(v);
      }else{
        o.replaceRange(v,pos2);
      }
    },
    chgtab(){
        if(this.activeName == 'compute'){
          this.showcompute = true;
           //var o = this.$refs['myjscode'].codemirror;
           //o.setValue("CCC");
        }else if(this.activeName == 'hidenode'){
          this.showhidenode = true;
        }
    },
  },
  watch:{
    "showcompute":function(v){
      //console.log(v);
    }
  }
}
</script>
<style lang="less">
.expscript .CodeMirror {
    border: 1px solid #DCDFE6;
    border-radius: 5px;
    height: 230px;
    width: 100%;
    font-size: 14px;
}
</style>
