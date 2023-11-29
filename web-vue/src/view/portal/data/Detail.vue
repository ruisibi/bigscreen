<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <div>
        <div class="tableDatasty" id="detailData">
          <template v-if="comp.comp.cols && comp.comp.cols.length > 0">
            <b>字段：</b>
            <template v-for="item in comp.comp.cols">
                <span :key="item.id" class="dimcol">
                  <span class="text">{{item.name}}</span><div class="ibox-tools"><button @click="setDetailCol(item)" class="btn btn-outline btn-success btn-xs"><i class="fa fa-wrench"></i></button></div>
                </span>
            </template>
          </template>
          <template v-if="!comp.comp.cols || comp.comp.cols.length == 0">
            <div class="tipinfo">拖拽数据表字段到此处作为详情页的字段</div>
          </template>
        </div>
        <detailColProperty ref="detailColPropertyForm"></detailColProperty>
    </div>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import detailColProperty from './DetailColProperty'

export default {
  components:{
    detailColProperty
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

    }
  },
  mounted(){
     this.bindDropEvent();
  },
  computed: {
  },
  methods: {
    detailView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].detailView();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    bindDropEvent(){
      const ts = this;
      //注册接收度量及维度拖放事件
      $("#detailData").droppable({
        accept:"#tabletree .jstree-node",
        tolerance:"pointer",
        over:function(e, ui){
          //对维度拖拽设置图标
          $(ui.helper[0]).find("span").removeClass("glyphicon-remove").addClass("glyphicon-ok");
          $("#detailData").css("border", "1px solid #ff0000");
        },
        out:function(e, ui){
          $(ui.helper[0]).find("span").removeClass("glyphicon-ok").addClass("glyphicon-remove");
          $("#detailData").css("border", "none");
        },
        drop:function(e, ui){
          var grid = ts.comp.comp;
          //清除边框颜色
          $("#detailData").css("border", "none");

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
              if(grid.cols[j].id === gid){
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
          ts.$forceUpdate();
          ts.detailView();
        }
      });
    },
    setDetailCol(col){
      $.contextMenu( 'destroy');
      var comp = this.comp.comp;
      var ts = this;
      let name = col.name;
      var items = {
        "move":{name:"移动", icon:"fa-arrows-alt", items:{left:{name:"左移", icon:"fa-arrow-left"}, right:{name:"右移", icon:"fa-arrow-right"}}},
        "prop": {name: "属性"},
        "clear": {name: "删除", icon:"fa-times"}
      };
      $.contextMenu({
        selector: '#detailData span.dimcol button',
        trigger: 'left',
        delay: 500,
        autoHide:true,
        /**
         position: function(opt, x, y){
              var offset = $(this).offset();
                opt.$menu.css({left:offset.left, top:offset.top + 15});
            },
        **/
        callback: function(opt) {
          if(opt === "prop"){
            ts.$refs['detailColPropertyForm'].openDailog(col, ts.comp.comp);
          }else if(opt === "clear"){
            $(comp.cols).each(function(a, b){
              if(b.name === name){
                comp.cols.splice(a, 1);
                ts.setUpdate();
                ts.$forceUpdate();
                ts.detailView();
                return false;
              }
            });
          }else if(opt == "left" || opt == "right"){
            ts.detailColmove(opt, col);
          }
        },
        items: items
      });
    },
    detailColmove(opt, col){
      var comp = this.comp.comp;
      var dims = comp.cols;
      if(dims.length <= 1){
        utils.msginfo('无效移动。');
        return;
      }
      for(let i=0; i<dims.length; i++){
        if(dims[i].id == col.id){
          if(opt == 'left'){
            if(i <= 0){
              utils.msginfo('无效移动。');
              return;
            }else{
              var tp = dims[i - 1];
              dims[i - 1] = dims[i];
              dims[i] = tp;
              //交换维度
              this.setUpdate();
              this.$forceUpdate();
              this.detailView();
              return;
            }
          }else
          if(opt == 'right'){
            if( i >= dims.length - 1){
              utils.msginfo('无效移动。');
              return;
            }else{
              var tp = dims[i + 1];
              dims[i + 1] = dims[i];
              dims[i] = tp;
              this.setUpdate();
              this.$forceUpdate();
              this.detailView();
              return;
            }
          }
          break;
        }
      }
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
.dimcol {
    border: 1px solid #0C6ADF;
    display: inline-block;
    margin: 5px;
    padding: 5px;
    text-align: left;
    font-size: 13px;
    width: 120px;
    border-radius: 5px;
    .text {
      word-break: break-all;
    }
}
.ibox-tools {
    display: inline-block;
    float: right;
    margin-top: -3px;
    position: relative;
    padding: 0;
}
</style>
