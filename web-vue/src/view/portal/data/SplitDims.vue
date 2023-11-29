<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="维度分解配置" :visible.sync="show" width="70%" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">维度分解配置</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
      </template>
     <div class="row">
      <div class="col-sm-5">
        <div style="margin-bottom:5px;">
        <button type="button" class="btn btn-xs btn-outline btn-info" @click="selectall()">全选</button>
        </div>
        <div id="valuesLeftTree" style="border:1px solid #cfdadd; height:380px; overflow:auto;border-radius: 5px;"></div>
      </div>
      <div class="col-sm-1">
        <p style="height:120px;"></p><button type="button" title="选择" class="btn btn-success btn-circle" @click="selectval()">&gt;</button><br><br><button type="button" @click="unselectval()" class="btn btn-success btn-circle" title="移除">&lt;</button>
      </div>
      <div class="col-sm-6">
        <div style="margin-bottom:5px;">
        <button type="button" @click="addNoteRow()" class="btn btn-default btn-xs">加行</button>
        <button type="button" @click="addCalc(false)" class="btn btn-default btn-xs">计算</button>
        <button type="button" @click="modifyRow()" class="btn btn-default btn-xs">编辑</button>
        <button type="button" @click="unselect()" class="btn btn-default btn-xs">删除</button>
        <button type="button" @click="addsj()" class="btn btn-default btn-xs">+缩进</button>
        <button type="button" @click="removesj()" class="btn btn-default btn-xs">-缩进</button>
        </div>
        <div id="valuesRightTree" style="border:1px solid #cfdadd; height:380px; overflow:auto;border-radius: 5px;"></div>
       </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">确 定</el-button>
        <el-button @click="show = false">取 消</el-button>
      </div>
      <el-dialog width="30%" title="插入备注行" :visible.sync="noteDailog" append-to-body :close-on-click-modal="false" custom-class="nopadding">
        <el-form label-width="90px" size="mini">
           <el-form-item label="节点名称：" label-width="100px" prop="nodeName">
             <el-input v-model="nodeName" />
			     </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="saveNoteRow()">确 定</el-button>
          <el-button @click="noteDailog = false">取 消</el-button>
        </div>
      </el-dialog>
      <el-dialog width="50%" :title="isCalcUpdate?'编辑计算行':'插入计算行'" :visible.sync="calcDailog" append-to-body :close-on-click-modal="false" custom-class="nopadding">
        <el-form label-width="90px" :model="calcNode" ref="calcForm" :rules="calcRule" size="mini">
           <el-form-item label="节点名称：" label-width="100px" prop="nodeName">
             <el-input v-model="calcNode.nodeName" />
			     </el-form-item>
           <el-form-item label="节点标识：" label-width="100px" prop="nodeId">
             <el-input v-model="calcNode.nodeId" :disabled="isCalcUpdate" placeholder="用来在计算中引用，添加后不可更改" />
			     </el-form-item>
           <el-form-item label="表达式：" label-width="100px" prop="expression">
             <codemirror :options="cmCfg" class="expscript" ref="myjscode" v-model="calcNode.expression"></codemirror>
             <div class="text-warning">通过 p.get("指标ID") 获取值，通过nvl函数把null转成0</div>
			     </el-form-item>
           <div class="actColumn">
             <template v-for="item in opt.cols">
               <button :key="item.id" type="button" style="margin-right:5px;" @click="selectme(item.id)" class="btn btn-primary btn-xs">{{item.name==item.id?item.name:item.name+'('+item.id+")"}}</button>
             </template>
           </div>
            <el-form-item label="缩进：" label-width="100px" prop="spaceNum">
             <el-input-number v-model="calcNode.spaceNum" :precision="0" :min="0" :max="30" size="small"></el-input-number>
			     </el-form-item>
            <el-form-item label="格式化：" label-width="100px" prop="fmt">
             <el-select v-model="calcNode.fmt" clearable style="width:100%">
              <el-option
                v-for="item in opt.fmtJson"
                :key="item.value"
                :label="item.text"
                :value="item.value">
              </el-option>
            </el-select>
			     </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="saveCalcRow()">确 定</el-button>
          <el-button @click="calcDailog = false">取 消</el-button>
        </div>
      </el-dialog>
      <el-dialog width="50%" title="节点属性" :visible.sync="nodeDailog" append-to-body :close-on-click-modal="false" custom-class="nopadding">
        <el-form label-width="90px" :model="mynode" ref="mynodeForm" :rules="mynodeRule" size="mini">
           <el-form-item label="节点名称：" label-width="100px" prop="mynodeName">
             <el-input v-model="mynode.mynodeName" />
			     </el-form-item>
           <el-form-item label="缩进：" label-width="100px" prop="myspaceNum">
             <el-input-number v-model="mynode.myspaceNum" :precision="0" :min="0" :max="30" size="small"></el-input-number>
			     </el-form-item>
            <el-form-item label="格式化：" label-width="100px" prop="myfmt">
             <el-select v-model="mynode.myfmt" clearable style="width:100%">
              <el-option
                v-for="item in opt.fmtJson"
                :key="item.value"
                :label="item.text"
                :value="item.value">
              </el-option>
            </el-select>
			     </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="saveModify()">确 定</el-button>
          <el-button @click="nodeDailog = false">取 消</el-button>
        </div>
      </el-dialog>
    </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl, ajax, newGuid} from '@/common/biConfig'
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
      node:null,
      comp:null,
      pos:null,
      values:[], //维度值列表
      noteDailog:false,
      nodeName:null,
      calcDailog:null,
      calcNode:{
        nodeName:null,
        nodeId:null,
        expression:null,
        spaceNum:null,
        fmt:null,
      },
      calcRule:{
        nodeName:[{ required: true, message: "必填", trigger: "blur" }],
        nodeId:[{ required: true, message: "必填", trigger: "blur" }],
        expression:[{ required: true, message: "必填", trigger: "blur" }],
      },
      opt:{
        fmtJson:utils.fmtJson,
        cols:[]
      },
      isCalcUpdate:false,  //插入计算列时 编辑/新增 状态
      curNode:null, //当前修改的节点
      nodeDailog:false, //编辑节点对话框
      mynode:{
        mynodeName:null,
        myspaceNum:null,
        myfmt:null,
      },
      mynodeRule:{
        mynodeName:[{ required: true, message: "必填", trigger: "blur" }],
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
    openDailog(node, comp, pos, isupdate){
      this.show = true;
      this.node = node;
      this.comp = comp;
      this.pos = pos;
      this.isupdate = isupdate;
      this.$nextTick(()=>{
        this.getDimValues();
      });
    },
    save(){
      let o = this.$parent;
      this.show = false;
      let rightRef = $("#valuesRightTree").jstree(true);
      let selects = rightRef.get_node("#").children;
			if(selects.length === 0){
				this.$notify.error("未选择维值");
				return;
      }
      let pos = this.pos;
      let comp = this.comp;
      let node = this.node;
      let curNode = null;
      utils.loopDims(pos==='row'?comp.rows:comp.cols, (a, b, c) => {
        if (a.match === node.match) {
          curNode = a;
          return false;
        }
      });
			//设置当前节点类型
			let a = curNode;
			a.type = 'kpiMatch';
			a.values = [];  //分解后的维值列表
			//循环生成下级节点
			selects.forEach((val, idx) => {
				//kpiMath 对应交叉表的 type=kpi
				const v = rightRef.get_node(val);
				const json = {
					id: newGuid(),
					match: a.match,
					type: v.li_attr.type,
					desc: v.li_attr.vtext,
					level: a.level,
					value: v.id,
					spaceNum: v.li_attr.spaceNum,
					fmt: v.li_attr.fmt,
					expression: v.li_attr.expression
				};
				a.values.push(json);
			});
      o.setUpdate();
      o.tableView();
    },
    //获取维度值列表
    getDimValues(){
      let comp = this.comp;
      let node = this.node;
      let ts = this;
      ajax({
        type:"POST",
        data:JSON.stringify({tid:comp.tid, dimId:node.match, params:comp.compParams}),
        postJSON:true,
        url:"bireport/dim/decompose.action",
        success:(resp)=>{
          this.values = resp.rows;
          this.initTrees();
        }
      });
    },
    initTrees(){
      let ts = this;
      //初始化Tree
      let ref =  $("#valuesLeftTree").jstree(true);
      if(ref){
        ref.destroy();
      }
      let vls = this.values.map(v => {
        return {id:v.id, text:v.name, icon:'fa fa-tag'};
      });
      let isupdate = this.isupdate;
      let pos = this.pos;
      let comp = this.comp;
      let node = this.node;
      let curNode = null;
      if(isupdate){
        utils.loopDims(pos==='row'?comp.rows:comp.cols, (a, b, c) => {
          if (a.match === node.match) {
            curNode = a;
            return false;
          }
        });
      }
      $("#valuesLeftTree").jstree({
        core:{
          check_callback:true,
          data:vls
        },
        "checkbox" : {
          "keep_selected_style" : false
        },
        "plugins" : [
          "checkbox",
          "wholerow"
        ]
      }).bind("ready.jstree", ()=>{
        if(isupdate){  //隐藏已选节点
          curNode.values.forEach(v=>{
            ref.hide_node(v.value);
          });
        }
      });
      ref = $("#valuesLeftTree").jstree(true);
      let rightRef = $("#valuesRightTree").jstree(true);
      if(rightRef){
        rightRef.destroy();
      }
      let dts = [];
      if(isupdate){  //回写数据
        curNode.values.forEach(v=>{
          let x = "";
          for(let j=0; v.spaceNum && j<v.spaceNum; j++){
            x += " &nbsp; &nbsp; ";
          }
          let icon = 'fa fa-tag';
          if(v.type === 'note'){
            icon = 'fa fa-align-left';
          }else if(v.type === 'kpiCalcMatch'){
            icon = 'fa fa-calculator';
          }
          dts.push({id:v.value, text:x + v.desc, li_attr:{type:v.type, vid:v.value, vtext:v.desc, expression:v.expression, spaceNum:v.spaceNum, fmt:v.fmt}, icon});
        });
      }
      $("#valuesRightTree").jstree({
        core:{
          check_callback:function(operation, source, node_parent, node_position, more) {
            if (operation === 'move_node') {  //控制 dnd
              var point = node_position == 0 ? "append":"";
              if(point==="append"){
                return false;
              }
              return true;
            } else {
              return true;
            }
          },
          dblclick_toggle:false,
          data:dts
        },
        dnd:{
          large_drop_target:'selected',
          large_drag_target:'selected'
        },
        "plugins" : [
          "wholerow",
          "dnd"
        ]
      }).bind("dblclick.jstree", function(e, data){
          ts.modifyRow();
      });
    },
    selectval(){
      const ref = $("#valuesLeftTree").jstree(true);
      const rightRef = $("#valuesRightTree").jstree(true);
      const selects = ref.get_checked(true);
			selects.map(s=>{
				let rightNode = rightRef.get_selected(true);
				let pos = 'last';
				if(rightNode.length > 0){
					rightRef.get_node("#").children.forEach((val, idx) => {
						if(val === rightNode[0].id){
							pos = idx + 1;
						}
					});
				}
				rightRef.create_node('#', {id:s.id, text:s.text, li_attr:{type:'kpiMatch', vid:s.id, vtext:s.text},icon:'fa fa-tag'}, pos);
				ref.hide_node(s.id);
			});
			ref.uncheck_all();
    },
    unselectval(){
      const ref = $("#valuesLeftTree").jstree(true);
      const rightRef = $("#valuesRightTree").jstree(true);
      const selects = rightRef.get_selected(true);
			if(selects.length === 0){
				utils.msginfo("未勾选右边数据");
				return;
			}
			selects.map(s=>{
				ref.show_node(s.id);
				rightRef.delete_node(s.id);
			});
    },
    addNoteRow(){
      this.nodeName = null;
      this.noteDailog = true;
    },
    saveNoteRow(){
      if(this.nodeName == null){
        this.$notify.error("录入节点名称。");
        return;
      }
      const rightRef = $("#valuesRightTree").jstree(true);
      let nodeName = this.nodeName;
      rightRef.create_node('#', {id:newGuid(), text:nodeName, li_attr:{type:'note', vid:'', vtext:nodeName},icon:'fa fa-align-left'});
      this.noteDailog = false;
    },
    //插入计算行
    addCalc(isupdate, cnd){
      this.calcDailog = true;
      let cols = [];
      this.values.forEach((v)=>{
        cols.push({id:v.id,name:v.name});
      });
      ['+','-','*', '%'].forEach(v=>{
				cols.push({id:v, name:v});
      });
      this.opt.cols = cols;
      if(this.$refs["calcForm"]){
        this.$refs["calcForm"].clearValidate();
      }
      this.isCalcUpdate = isupdate;
      if(isupdate){
        this.calcNode.nodeId = cnd.id;
        this.calcNode.nodeName = cnd.li_attr.vtext;
        this.calcNode.expression = unescape(cnd.li_attr.expression);
        this.calcNode.spaceNum = cnd.li_attr.spaceNum;
        this.calcNode.fmt = cnd.li_attr.fmt;

      }else{
        for(let c in this.calcNode){
          this.calcNode[c] = null;
        }
        this.calcNode.expression = "";
      }
    },
    saveCalcRow(){
      this.$refs["calcForm"].validate( (valid) => {
        if(valid){
          this.calcDailog = false;
          const rightRef = $("#valuesRightTree").jstree(true);
          let nodeId = this.calcNode.nodeId;
          let nodeName = this.calcNode.nodeName;
          let expression = this.calcNode.expression;
          let spaceNum = this.calcNode.spaceNum;
          let fmt = this.calcNode.fmt;
          if(this.isCalcUpdate){
            let nd = this.curNode;
            nd.li_attr.vtext = nodeName;
            nd.li_attr.expression = escape(expression);
            nd.li_attr.spaceNum = spaceNum;
            nd.li_attr.fmt = fmt;
          }else{
            rightRef.create_node('#', {id:nodeId, text:nodeName, li_attr:{type:'kpiCalcMatch', vid:'', vtext:nodeName, expression:escape(expression), spaceNum, fmt},icon:'fa fa-calculator'});
          }
        }
      });
    },
    selectme(v){
        v = "p.get('"+v+"') ";
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
    unselect(){
      const rightRef = $("#valuesRightTree").jstree(true);
      const ref = $("#valuesLeftTree").jstree(true);
      const selects = rightRef.get_selected(true);
			if(selects.length === 0){
			  this.$notify.error("未勾选右边数据");
				return;
			}
			selects.map(s=>{
				ref.show_node(s.id);
				rightRef.delete_node(s.id);
			});
    },
    modifyRow(){
      const rightRef = $("#valuesRightTree").jstree(true);
      const selects = rightRef.get_selected(true);
			if(selects.length === 0){
				this.$notify.error("未勾选数据");
				return;
			}
      const nd = selects[0];
      this.curNode = nd;
			if(nd.li_attr.type === 'kpiCalcMatch'){
				this.addCalc(true, nd);
				return
      }
      this.mynode.mynodeName = nd.li_attr.vtext;
      this.mynode.myspaceNum = nd.li_attr.spaceNum?nd.li_attr.spaceNum:0;
      this.mynode.myfmt = nd.li_attr.fmt;
      this.nodeDailog = true;
    },
    saveModify(){
      this.$refs["mynodeForm"].validate( (valid) => {
        if(valid){
          let rightRef = $("#valuesRightTree").jstree(true);
          let spaceNum = this.mynode.myspaceNum;
          let desc = this.mynode.mynodeName;
          let fmt = this.mynode.myfmt;
          let nd = this.curNode;
          let x = "";
          for(let j=0; j<spaceNum; j++){
            x += " &nbsp; &nbsp; ";
          }
          rightRef.rename_node(nd, x + desc);
          nd.li_attr.spaceNum = spaceNum;
          nd.li_attr.vtext = desc;
          if(!fmt || fmt === ''){
            delete nd.li_attr.fmt;
          }else {
            nd.li_attr.fmt = fmt;
          }
          this.nodeDailog = false;
        }
      });
    },
    selectall(){
      const ref = $("#valuesLeftTree").jstree(true);
      ref.select_all();
    },
    addsj(){
       const rightRef = $("#valuesRightTree").jstree(true);
        const selects = rightRef.get_selected(true);
        $(selects).each((a, b)=>{
          if(!b.li_attr.spaceNum){
            b.li_attr.spaceNum = 1;
          }else{
            b.li_attr.spaceNum = b.li_attr.spaceNum + 1;
          }
          let x = "";
          for(let j=0; j<b.li_attr.spaceNum; j++){
            x += " &nbsp; &nbsp; ";
          }
          rightRef.rename_node(b.id, x + b.li_attr.vtext);
        });
    },
    removesj(){
      const rightRef = $("#valuesRightTree").jstree(true);
        const selects = rightRef.get_selected(true);
        $(selects).each((a, b)=>{
          if(!b.li_attr.spaceNum){
            return true;
          }else{
            b.li_attr.spaceNum = b.li_attr.spaceNum - 1;
          }
          let x = "";
          for(let j=0; j<b.li_attr.spaceNum; j++){
            x += " &nbsp; &nbsp; ";
          }
          rightRef.rename_node(b.id, x + b.li_attr.vtext);
        });
    },
  }
}
</script>
<style lang="less">
.expscript .CodeMirror {
    border: 1px solid #DCDFE6;
    border-radius: 5px;
    height: 280px;
    width: 100%;
    font-size: 14px;
}
</style>
