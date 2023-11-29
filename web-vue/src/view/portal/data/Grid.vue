<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <div style="margin:10px;">
       <div class="tableDatasty" id="gridData">
        <template v-if=" !comp.comp.cols || comp.comp.cols.length === 0">
          <div class="tipinfo">拖拽数据表字段到此处作为表格的列字段</div>
        </template>
        <template v-if="comp.comp.cols && comp.comp.cols.length > 0">
          <table>
            <tbody>
              <tr>
                <template v-for="item in comp.comp.cols">
                  <td :key="item.id" valign='top'>
                    <template v-if="item.children && item.children.length > 0">
                        <ul class='hascld'>
                          <div class="note">
                             <span class="dimcol notecol">
                                <span class="text">{{item.name}}</span>
                                <div class="ibox-tools"><button class="btn btn-outline btn-success btn-xs" @click="setGridCol(item)"><i class="fa fa-wrench"></i></button>
                                </div>
                              </span>
                          </div>
                          <table>
                            <tr>
                              <template v-for="c in item.children">
                                 <td :key="c.id" >
                                  <span class="dimcol">
                                    <span class="text">{{c.name}}</span>
                                    <div class="ibox-tools"><button class="btn btn-outline btn-success btn-xs" @click="setGridCol(c)"><i class="fa fa-wrench"></i></button>
                                    </div>
                                  </span>
                                </td>
                              </template>
                            </tr>
                          </table>
                        </ul>
                    </template>
                     <template v-if="!item.children || item.children.length == 0">
                        <span class="dimcol">
                          <span class="text">{{item.name}}</span>
                          <div class="ibox-tools"><button class="btn btn-outline btn-success btn-xs" @click="setGridCol(item)"><i class="fa fa-wrench"></i></button>
                          </div>
                        </span>
                     </template>
                  </td>
                </template>
              </tr>
            </tbody>
          </table>
        </template>
       </div>
         	<el-dialog title="表格字段属性" :visible.sync="propshow" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
             <template slot="title">
              <span class="el-dialog__title">表格字段属性</span>
              <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
              <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
              </button>
            </template>
             <el-form :model="val" ref="valForm" label-position="left" size="mini">
               <template v-if="col.nodeType != 'note'">
                <el-tabs v-model="activeName" >
                  <el-tab-pane label="基本信息" name="base">
                    <el-form-item label="显示名称：" label-width="100px">
                      <el-input v-model="val.dispName" label="名称"></el-input>
                    </el-form-item>
                    <el-form-item label="宽度：" label-width="100px">
                      <el-input-number v-model="val.colwidth" :min="60" controls-position="right" :precision="0"></el-input-number>
                    </el-form-item>
                    <el-form-item label="所属表：" label-width="100px">
                      {{col.tname}}
                    </el-form-item>
                    <el-form-item label="对应字段：" label-width="100px">
                      {{col.name}}
                    </el-form-item>
                    <el-form-item label="位置：" label-width="100px">
                      <el-radio-group v-model="val.palign" size="small">
                        <el-radio-button label="left">居左</el-radio-button>
                        <el-radio-button label="center">居中</el-radio-button>
                        <el-radio-button label="right">居右</el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                    <template v-if="col.type === 'Date' || col.type ==='Datetime'">
                      <el-form-item label="格式化：" label-width="100px">
                        <el-input v-model="val.fmt"></el-input>
                      </el-form-item>
                    </template>
                    <template v-if="col.type === 'Double' || col.type ==='Int' || col.type ==='Long'">
                    <el-form-item label="格式化：" label-width="100px">
                        <el-select
                          v-model="val.fmt"
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
                    <el-form-item label="数据聚合：" label-width="100px">
                      <el-row>
                        <el-col :span="11">
                          <el-select
                            style="width:100%"
                            v-model="val.aggre"
                            clearable
                            placeholder="直接聚合"
                            >
                            <el-option
                              v-for="item in opt.aggres"
                              :key="item"
                              :label="item"
                              :value="item"
                            >
                          </el-option>
                          </el-select>
                        </el-col>
                        <el-col :span="2" align="center">或</el-col>
                         <el-col :span="11">
                        <el-input v-model="val.aggreSql" placeholder="编写SQL" />
                         </el-col>
                      </el-row>
                    </el-form-item>
                    </template>
                    <el-form-item label="隐藏字段：" label-width="100px">
                        <el-switch v-model="val.hideCol" ></el-switch>
                        <span class="text-warning">开启后，SQL中会查询出此字段，但表格中不会显示。</span>
                    </el-form-item>
                    <el-form-item label="前端排序：" label-width="100px">
                        <el-switch v-model="val.frontSort" ></el-switch>
                        <span class="text-warning">开启后，点击表头对数据进行排序。</span>
                    </el-form-item>
                  </el-tab-pane>
                  <el-tab-pane label="回调函数" name="code">
                     function
                    <el-input style="width:100px;" v-model="val.funcname" size="mini"></el-input>
                    (<b>value</b>, <b>rowData</b>, <b>column</b>, <b>index</b>,  <b>outType</b>){
                      <el-input type="textarea" :rows="8" v-model="val.code" ></el-input>
                    }
                    <br/>
                    <a @click="tablecallbackhelper()" class="btn btn-primary btn-xs" href="javascript:;">帮助</a>
                  </el-tab-pane>
                </el-tabs>
               </template>
                <template v-if="col.nodeType === 'note'">
                   <el-form-item label="显示名称：" label-width="100px">
                      <el-input v-model="val.dispName" label="名称"></el-input>
                    </el-form-item>
                </template>
             </el-form>
             <div slot="footer" class="dialog-footer">
              <el-button type="primary" @click="saveProp()">确 定</el-button>
              <el-button @click="propshow = false">取 消</el-button>
            </div>
          </el-dialog>
          <gridJoin ref="gridJoinForm"></gridJoin>
          <gridSort ref="gridSortForm"></gridSort>
          <gridMove ref="gridMoveForm"></gridMove>
    </div>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import gridJoin from './GridJoin'
import gridSort from './GridSort'
import gridMove from './GridMove'

export default {
  components:{
    gridJoin, gridSort, gridMove
  },
  props:{
      comp:{
        type:Object,
        required:true
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
  },
  data(){
    return {
      propshow:false,
      val:{
        dispName:"",
        palign:"left",
        fmt:"",
        colwidth:null,
        funcname:null,
        code:null,
        aggre:null,
        aggreSql: null,
        hideCol: false,
        frontSort:false,
      },
      col:{
        tname:null,
        name:null
      },
      opt:{
        fmts:utils.fmtJson,
        aggres: ["max", "min", "avg", "sum", "count"]
      },
      activeName:"base"
    }
  },
  mounted(){
    this.bindDropEvent();
  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
     setUpdate(){
      this.$parent.$parent.isupdate = true;
      this.$forceUpdate();
    },
    gridView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].gridView();
    },
    setGridCol(col){
      $.contextMenu( 'destroy');
      let ts = this;
      var comp = this.comp.comp;
      var items = {
            "sort":{name:"排序"},
            "pm":{name:"排名", items: {
                sxpm: {name: "升序排名"},
                jxpm: {name: "降序排名"},
                removepm: {name: "默认"}
              }},
            "move":{name:"移动", icon:"fa-arrows-alt", items:{left:{name:"左移", icon:"fa-arrow-left"}, right:{name:"右移", icon:"fa-arrow-right"}, moveMore:{name:"移动多步"}}},
            "join": {name: "合并"},
            "prop": {name: "属性"},
            "clear": {name: "删除", icon:"fa-times"}
        };
         if(col.nodeType === 'note'){
            delete items.sort;
            delete items.pm;
            delete items.join;
          }
          if(col.level >= 1){
            delete items.join;
          }
        $.contextMenu({
          selector: '#gridData span.dimcol button',
          trigger: 'left',
          delay: 500,
          autoHide:true,
          callback: function(opt) {
            if(opt == "sort"){
              ts.gridColsort(comp, col);
            }else if(opt == "prop"){
              ts.setGridColProp(comp, col);
            }else if(opt == "clear"){
              ts.delGridCol(comp, col.id);
            }else if(opt == "left" || opt == "right" || opt === 'moveMore'){
              ts.tableColmove(opt, comp, col.id);
            }else if(opt === 'sxpm' || opt === 'jxpm'){
              col.pm = opt;
              ts.setUpdate();
              ts.gridView();
            }else if(opt === 'removepm'){
              delete col.pm;
               ts.setUpdate();
              ts.gridView();
            }else if(opt === 'join'){
               ts.$refs['gridJoinForm'].showDailog(comp);
            }
          },
          items: items
      });
    },
    setGridColProp(comp, col){
      this.propshow = true;
      this.activeName = "base";
      this.col = col;
      this.val.dispName = col.dispName || col.name;
      this.val.fmt = col.fmt;
      this.val.colwidth = col.colwidth;
      this.val.palign = col.align || "center";
      this.val.funcname = col.funcname || "f" + Math.round(Math.random() * 10000);
      this.val.code = col.code?unescape(col.code):null;
      this.val.aggre = col.aggre;
      this.val.aggreSql = col.aggreSql;
      this.val.hideCol = col.hideCol;
      this.val.frontSort = col.frontSort;
    },
    saveProp(){
      let col = this.col;
      col.dispName = this.val.dispName;
			col.fmt = this.val.fmt;
      col.align = this.val.palign;
      col.colwidth = this.val.colwidth;
      col.aggre = this.val.aggre;
      col.aggreSql = this.val.aggreSql;
      col.funcname = this.val.funcname;
      col.hideCol = this.val.hideCol;
      col.frontSort = this.val.frontSort;
      if(this.val.code){
        col.code = escape(this.val.code);
      }else{
        delete col.code;
      }

      this.propshow = false;
      this.setUpdate();
      this.gridView();
    },
    tableColmove(tp, comp, id){
      var dims = comp.cols;
      $(dims).each((a, b) => {
        if(b.children){
          $(b.children).each((c, d)=>{
            if(d.id == id){
              dims = b.children;
            }
          });
        }
      });

      if(dims.length <= 1){
        utils.msginfo('无效移动。');
        return;
      }

      if(tp === 'moveMore'){
        this.$refs['gridMoveForm'].showDailog(comp, dims, id);
        return;
      }

      for(var i=0; i<dims.length; i++){
        if(dims[i].id == id){
          if(tp == 'left'){
            if(i <= 0){
              utils.msginfo('无效移动。');
              return;
            }else{
              var tp = dims[i - 1];
              dims[i - 1] = dims[i];
              dims[i] = tp;
              this.setUpdate();
              this.gridView();
              return;
            }
          }else
          if(tp == 'right'){
            if( i >= dims.length - 1){
              utils.msginfo('无效移动。');
              return;
            }else{
              var tp = dims[i + 1];
              dims[i + 1] = dims[i];
              dims[i] = tp;
              this.setUpdate();
              this.gridView();
              return;
            }
          }
          break;
        }
      }
    },
    delGridCol(comp, id){
      if(comp.cols.length == 1){
        utils.msginfo("表格至少需要含有一个字段。");
        return;
      }
      //从json移除
      var idx = -1;
      for(let i=0; i<comp.cols.length; i++){
        var p = comp.cols[i];
        if(p.id == id){
          idx = i;
          break;
        }
      }
      comp.cols.splice(idx, 1);
      this.setUpdate();
      this.gridView();
    },
    gridColsort(comp, col){
      this.$refs['gridSortForm'].showDailog(comp, col);
    },
    bindDropEvent(){
      let ts = this;
      $("#gridData").droppable({
          accept:"#tabletree .jstree-node",
		      tolerance:"pointer",
         over:function(e, ui){
            //对维度拖拽设置图标
            $(ui.helper[0]).find("span").removeClass("glyphicon-remove").addClass("glyphicon-ok");
            $("#gridData").css("border", "1px solid #ff0000");
          },
          out:function(e, ui){
            $(ui.helper[0]).find("span").removeClass("glyphicon-ok").addClass("glyphicon-remove");
            $("#gridData").css("border", "1px dotted #666");
          },
         drop:function(e, ui){
           let grid = ts.comp.comp;
            //清除边框颜色
            $("#gridData").css("border", "1px dotted #666");

           //获取TREE
          var ref = $("#tabletree").jstree(true);
          var node = ref.get_node(ui.draggable[0]);

            if(grid.tid && grid.tid != node.li_attr.tid){
                utils.msginfo("你拖入的字段"+node.text+"与表格已有的内容不在同一个表中，拖放失败！");
                return;
              }else{
                grid.tid = node.li_attr.tid;
                grid.tname = node.li_attr.tname;
                grid.tincome = node.li_attr.income;
              }
            if(!grid.cols){
              grid.cols = [];
            }
            //判断是否存在
            var exist = function(gid){
              var r = false;
              for(let j=0; j<grid.cols.length; j++){
                if(grid.cols[j].id == gid){
                  r = true;
                  break;
                }
              }
              return r;
            };
            if(exist(node.id)){
              utils.msginfo("您拖拽的字段 " + node.text+" 已经存在。");
              return;
            }
            grid.cols.push({id:node.id,name:node.li_attr.name,tname:node.li_attr.tname,type:node.li_attr.type,expression:node.li_attr.expression});

           ts.setUpdate();
           ts.gridView();
          }
        });

    },
    tablecallbackhelper(){
      window.open("#/portal/TableCallbackHelper");
    }
  },
  watch: {

  }
}
</script>

<style lang="less" scoped>
  .tipinfo {
    color:#999;
    padding:10px;
  }
  .tableDatasty{
    border: 1px dotted #666;
    padding:5px;
    border-radius:5px;
    .col {
      border: 1px solid #DF7809;
      display: inline-block;
      margin: 3px;
      padding: 3px;
      text-align: center;
      width:120px;
      font-size:14px;
      border-radius:5px;
    }
    .dimcol {
        border: 1px solid #0C6ADF;
        display: inline-block;
        margin: 3px;
        padding: 3px;
        text-align: left;
      font-size:14px;
      width:120px;
      border-radius:5px;
    }
  }
  .ibox-tools {
    display: inline-block;
    float: right;
    margin-top: 0;
    position: relative;
    padding: 0;
  }
  .hascld {
    display: inline-block;
    margin: 0px;
    padding: 0px;
    text-align: left;
    font-size: 13px;
  }
  .notecol {
    border:1px solid #DF7809 !important;
  }
  .dimcol {
    border: 1px solid #0C6ADF;
    display: inline-block;
    margin: 3px;
    padding: 3px;
    text-align: left;
    font-size:13px;
    width:120px;
    border-radius:5px;
    .text {
      word-break: break-all;
    }
  }
</style>
