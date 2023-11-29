<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <div id="boxDataPanel" style="margin:3px;">
        <div class="tsbd">
          <div class="ts_h">
            数据块度量：
            <div class="h_ctx" id="boxKpi">
              <span v-if="!comp.comp || !comp.comp.kpiJson" class="charttip">拖拽度量到此</span>
              <span v-if="comp.comp && comp.comp.kpiJson"><span>{{comp.comp.kpiJson.kpi_name}}</span></span>
            </div>
          </div>
          <!-- 同环比，需要时间维度 -->
          <template v-if="comp.comp && comp.comp.thbDim">
          <div class="ts_h">时间维度：
            <div class="h_ctx" id="thbDim">
              <span v-if="!comp.comp || !comp.comp.thbDim || !comp.comp.thbDim.id " class="charttip">拖拽时间维度到此</span>
              <span v-if="comp.comp && comp.comp.thbDim"><span>{{comp.comp.thbDim.dimdesc}}</span></span>
            </div>
          </div>
          </template>
          <!-- 数据库带图形 -->
          <template v-if="comp.comp.chart">
             <div class="ts_h">图形横轴：
               <div class="h_ctx" id="chartDim">
                 <span class="charttip" v-if="!comp.comp.chart.chartJson.xcol.dimdesc">拖拽维度到此处</span>
                 <span v-if="comp.comp.chart.chartJson.xcol">{{comp.comp.chart.chartJson.xcol.dimdesc}}</span>
               </div>
             </div>
            <div class="ts_h">图形纵轴：
              <div class="h_ctx" id="chartKpi">
                <span class="charttip" v-if="comp.comp.chart.kpiJson.length == 0">拖拽度量到此处</span>
                <span v-if="comp.comp.chart.kpiJson.length > 0">{{comp.comp.chart.kpiJson[0].kpi_name}}</span>
              </div>
            </div>
          </template>
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
    boxView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].boxView();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    bindBoxEvent(){
      let ts = this;
      $("#boxDataPanel .h_ctx").droppable({
        accept:"#datasettree .jstree-node",
		    tolerance:"pointer",
        over:function(e, ui){
          var ref = $("#datasettree").jstree(true);
          var node = ref.get_node(ui.draggable[0]);
          var tp = node.li_attr.col_type;
          var id = $(this).attr("id");
          //只能拖拽度量
          if((id == 'boxKpi' || id == 'chartKpi') && tp == 1){
            return
          }
          if((id == "chartDim" || id == 'thbDim') && tp == 2){
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
          if((tarId == 'boxKpi' || tarId == 'chartKpi') && tp == 1){
            ts.$notify.error("只能拖拽度量到此显示。");
            return
          }
          if((tarId == "chartDim" || tarId == 'thbDim' ) && tp == 2){
            ts.$notify.error("只能拖拽维度到此显示。");
            return
          }


          json.tid = node.li_attr.tid;
          json.tname = node.li_attr.tname;
          json.tincome = node.li_attr.income;

          if(tarId == 'boxKpi'){
            json.kpiJson = {"kpi_id":node.li_attr.col_id, "kpi_name" : node.li_attr.name, "col_name":node.li_attr.col_name, "aggre":node.li_attr.aggre, "fmt":node.li_attr.fmt, "alias":node.li_attr.alias,"tid":json.tid,tname:json.tname,"unit":node.li_attr.unit,"rate":node.li_attr.rate,"fromCol":node.li_attr.fromCol,"distinctCol":node.li_attr.distinctCol};
            //设置title
            json.name = node.li_attr.name;
            ts.boxView();
          }else if(tarId == 'chartKpi'){
            json.chart.tid = json.tid;
            var k = {"kpi_id":node.li_attr.col_id, "kpi_name" : node.li_attr.name, ydispName:node.li_attr.name, "col_name":node.li_attr.col_name, "aggre":node.li_attr.aggre, "fmt":node.li_attr.fmt, "alias":node.li_attr.alias,"tid":json.tid,"unit":node.li_attr.unit,"rate":node.li_attr.rate,"fromCol":node.li_attr.fromCol,"distinctCol":node.li_attr.distinctCol};
            json.chart.kpiJson[0] = k;
            if(json.chart.chartJson.xcol.tid){
              ts.boxView();
            }
          }else if(tarId == 'chartDim'){
            json.chart.tid = json.tid;
            json.chart.chartJson.xcol = {"id":node.li_attr.col_id, "dimdesc" : node.text,xdispName:node.text, "type":node.li_attr.dim_type, "colname":node.li_attr.col_name,"tid":json.tid,"iscas":node.li_attr.iscas, "tableName":node.li_attr.tableName, "tableColKey":node.li_attr.tableColKey,"tableColName":node.li_attr.tableColName, "dimord":node.li_attr.dimord, "dim_name":node.li_attr.dim_name, "grouptype":node.li_attr.grouptype,"valType":node.li_attr.valType, "ordcol":node.li_attr.ordcol,dateformat:node.li_attr.dateformat,"alias":node.li_attr.alias,"fromCol":node.li_attr.fromCol};
            if(json.chart.kpiJson[0] && json.chart.kpiJson[0].tid){
              ts.boxView();
            }
          }else if(tarId == 'thbDim'){
            var tp = node.li_attr.dim_type;
            if(!(tp == "year" || tp == "quarter" || tp =="month" || tp == "day")){
              ts.$notify.error("此处只能拖拽时间类型维度。");
              return;
            }
            json.thbDim = {"id":node.li_attr.col_id, "dimdesc" : node.text,xdispName:node.text, "type":node.li_attr.dim_type, "colname":node.li_attr.col_name,"tid":json.tid,"iscas":node.li_attr.iscas, "tableName":node.li_attr.tableName, "tableColKey":node.li_attr.tableColKey,"tableColName":node.li_attr.tableColName, "dimord":node.li_attr.dimord, "dim_name":node.li_attr.dim_name, "grouptype":node.li_attr.grouptype,"valType":node.li_attr.valType, "ordcol":node.li_attr.ordcol,dateformat:node.li_attr.dateformat,"alias":node.li_attr.alias,"fromCol":node.li_attr.fromCol};
            ts.boxView();
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
</style>
