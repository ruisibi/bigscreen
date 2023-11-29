<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <div id="customDataPanel" style="margin:3px;">
        <div class="tsbd">
          <div class="ts_h">
            度量：
            <div class="h_ctx" id="kpi">
              <span v-if="!comp.comp.kpi"  class="charttip">拖拽度量到此</span>
              <span v-if="comp.comp.kpi"><span>{{comp.comp.kpi.kpi_name}}</span></span>
            </div>
          </div>
          <div class="ts_h">维度：
            <div class="h_ctx" id="dim">
              <span v-if="!comp.comp.dim" class="charttip">拖拽维度到此</span>
              <div v-if="comp.comp.dim"><span class="dimtxt">{{comp.comp.dim.dimdesc}}</span><a class="dimicon" href="javascript:;" @click="removeDim()"><i class="fa fa-remove">&nbsp;</i></a></div>
            </div>
          </div>
        </div>
    </div>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'

export default {
  components:{

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
     this.bindBoxEvent();
  },
  computed: {
  },
  methods: {
    init(){
      this.$forceUpdate();
    },
    customView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].customView();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    removeDim(){
        delete this.comp.comp.dim;
        this.$forceUpdate();
        this.customView();
    },
    bindBoxEvent(){
      let ts = this;
      $("#customDataPanel .h_ctx").droppable({
        accept:"#datasettree .jstree-node",
		    tolerance:"pointer",
        over:function(e, ui){
          var ref = $("#datasettree").jstree(true);
          var node = ref.get_node(ui.draggable[0]);
          var tp = node.li_attr.col_type;
          var id = $(this).attr("id");
          //只能拖拽度量
          if((id == 'kpi' ) && tp == 1){
            return
          }
          if((id == "dim" ) && tp == 2){
            return
          }

          //对维度拖拽设置图标
          $(ui.helper[0]).find("span").removeClass("glyphicon-remove").addClass("glyphicon-ok");
          $(this).css("border", "1px solid #ff0000");
        },
        out:function(e, ui){
          $(ui.helper[0]).find("span").removeClass("glyphicon-ok").addClass("glyphicon-remove");
			    $(this).css("border", "1px solid #7F9DB9");
        },
       drop:function(e, ui){
         //组件数据存在 comp 下面
          let json = ts.comp.comp;
          var id = ts.comp.id;

          //清除边框颜色
          $(this).css("border", "1px solid #7F9DB9");

          //获取TREE
          var ref = $("#datasettree").jstree(true);
          var node = ref.get_node(ui.draggable[0]);
          var tp = node.li_attr.col_type;

          var tarId = $(this).attr("id");
          if((tarId == 'kpi' ) && tp == 1){
            ts.$notify.error("只能拖拽度量到此显示。");
            return
          }
          if((tarId == "dim" ) && tp == 2){
            ts.$notify.error("只能拖拽维度到此显示。");
            return
          }


          json.tid = node.li_attr.tid;
          json.tname = node.li_attr.tname;
          json.tincome = node.li_attr.income;

          if(tarId == 'kpi'){
            json.kpi = {"kpi_id":node.li_attr.col_id, "kpi_name" : node.li_attr.name, "col_name":node.li_attr.col_name, "aggre":node.li_attr.aggre, "fmt":node.li_attr.fmt, "alias":node.li_attr.alias,"tid":json.tid,tname:json.tname,"unit":node.li_attr.unit,"rate":node.li_attr.rate,"fromCol":node.li_attr.fromCol,"distinctCol":node.li_attr.distinctCol};
            //设置title
            ts.customView();
          }else if(tarId == 'dim'){

            json.dim = {"id":node.li_attr.col_id, "dimdesc" : node.text,xdispName:node.text, "type":node.li_attr.dim_type, "colname":node.li_attr.col_name,"tid":json.tid,"iscas":node.li_attr.iscas, "tableName":node.li_attr.tableName, "tableColKey":node.li_attr.tableColKey,"tableColName":node.li_attr.tableColName, "dimord":node.li_attr.dimord, "dim_name":node.li_attr.dim_name, "grouptype":node.li_attr.grouptype,"valType":node.li_attr.valType, "ordcol":node.li_attr.ordcol,dateformat:node.li_attr.dateformat,"alias":node.li_attr.alias,"fromCol":node.li_attr.fromCol};
            ts.customView();
          }
          ts.$forceUpdate();
          ts.setUpdate();
        }
      });
    }
  },
  watch: {

  }
}
</script>

<style lang="less" scoped>
  .tsbd .ts_h{
    font-size:13px;
    margin:5px 20px 5px 5px;
    width:125px;
    float:left;
  }
  .tsbd .h_ctx{
    border:1px solid #7F9DB9;
    height:28px;
    overflow:hidden;
    border-radius:5px;
    padding:4px;
  }
  span.charttip {
    color:#999999;
    display:block;
  }
  a.dimicon {
      float: right;
  }
  span.dimtxt {
      display: inline-block;
      width: 100px;
      overflow: hidden;
  }
</style>
