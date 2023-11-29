<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div style="margin-top:15px;">
    <div style="margin-bottom:5px;" v-if="showBtn">
      <button type="button" class="btn btn-info btn-xs" @click="autoCube()">自动生成模型</button>
    </div>
    <div class="row">
      <div class="col-sm-4">
        <div id="cubelefttree" style="border:1px solid #cfdadd; height:420px; overflow:auto;border-radius: 5px;"></div>
      </div>
      <div class="col-sm-1">

        <p style="height:150px;"></p>
        <button type="button" title="选择" @click="ds2cube()" class="btn btn-success btn-circle">&gt;</button>
        <br/>
      <br/>
        <button type="button" @click="cube2ds()" title="移除" class="btn btn-success btn-circle">&lt;</button>
      </div>
      <div class="col-sm-4">
        <div id="cuberighttree" style="border:1px solid #cfdadd; height:420px; overflow:auto;border-radius: 5px;"></div>
      </div>
      <div class="col-sm-2" style="line-height:26px">
        <button class="btn btn-default btn-xs" style="width:70px;" @click="addDateDim()" type="button"><i class="fa fa-calendar-plus-o"></i>时间维度</button><br>
        <button class="btn btn-default btn-xs" style="width:70px;" @click="addgroup()" type="button"><i class="fa fa-plus"></i> 维度分组</button> <br>
        <button style="width:70px;" class="btn btn-default btn-xs" @click="addDyna(false)" type="button"><i class="fa fa-plus-square"></i> 计算度量</button> <br>
        <button style="width:70px;" class="btn btn-default btn-xs" @click="kpiCatalog();" type="button"><i class="fa fa-folder-open-o"></i>度量分类</button> <br>
        <button @click="editcubecol();" class="btn btn-default btn-xs" style="width:70px;" type="button"><i class="fa fa-edit"></i> 编辑</button> <br>
        <button @click="cube2ds();" class="btn btn-default btn-xs" style="width:70px;" type="button"><i class="fa fa-remove"></i> 删除</button>
    </div>
    </div>
    <div align="left" style="margin-top:15px;" v-if="showBtn">
        <el-button  @click="before()"><i class="fa fa-arrow-left"></i> 上一步</el-button>
        <el-button type="primary" @click="saveCube()">保 存 <i class="fa fa-save"></i></el-button>
     </div>
     <dateDim ref="dateDimForm"></dateDim>
     <editCubeCol ref="cubeColForm"></editCubeCol>
     <dimGroup ref="dimGroupForm"></dimGroup>
     <kpiGroup ref="kpiGroupForm"></kpiGroup>
     <calcKpi :pageJson="pageJson" ref="calcKpiForm"></calcKpi>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import dateDim from '@/view/model/DateDimDailog'
import editCubeCol from '@/view/model/EditCubeCol'
import dimGroup from '@/view/model/DimGroupDailog'
import kpiGroup from '@/view/model/KpiGroupDailog'
import calcKpi from '@/view/model/CalcKpiDailog'
import $ from "jquery";

export default {
  data() {
    return {
      delObj:[],
      cols:null
    }
  },
  props:{
    pageJson: {
      type: Object,
      required: true,
    },
    //是否显示上一步，保存按钮
    showBtn:{
      type:Boolean,
      required:false,
      default:true
    }
  },
  components: {
    dateDim,editCubeCol,dimGroup,kpiGroup,calcKpi
	},
  mounted() {

  },
  computed: {},
  methods: {
	  before(){
       //this.cube2json();
       this.$parent.active = 1;
    },
    initLeftTree(){
      var pageJson = this.pageJson;
      if(!pageJson.tid){
        return;
      }
      var ts = this;
      var o = {};
      o.tableType = pageJson.tableType;
      o.kpiCodeColumn = pageJson.kpiCodeColumn;
      o.kpiNameColumn = pageJson.kpiNameColumn;
      o.kpiValueColumn = pageJson.kpiValueColumn;
      o.tid = pageJson.tid;
      o.tName = pageJson.tName;
      ajax({
        type: "POST",
        url: "model/queryMetaTableCols.action",
        dataType:"JSON",
        data: o,
        success: function(resp){
          resp = resp.rows;
          ts.cols = resp;
          var dt = [{id:'leftroot',text:pageJson.tName,icon:'fa fa-table', state:{opened:true}, children:[]}];
          for(let i=0; i<resp.length; i++){
            var node = {id:resp[i].colName,text:(resp[i].colNote?resp[i].colNote:resp[i].colName),icon:'glyphicon glyphicon-menu-hamburger',li_attr:{tp:'node', vtype:resp[i].colType, col:resp[i].colName, expression:resp[i].expression}}
            dt[0].children.push(node);
          }
          var ref =  $("#cubelefttree").jstree(true);
          if(ref){
            ref.destroy();
          }
          $("#cubelefttree").jstree({
            core:{
                check_callback:true,
                data:dt
              },
              checkbox:{
                three_state:false,
              },
              "plugins" : [
                  "wholerow", "checkbox"
                ]
          }).bind("ready.jstree", ()=>{
            if(ts.showBtn){
              return;
            }
            //隐藏已选择的维度和指标 列
            var cubeObj = pageJson;
            var findcol = function(cid){
              var ret = null;
              for(let j=0;j<cubeObj.dims.length;j++){
                if(cubeObj.dims[j].fromCol == cid){
                  ret = cubeObj.dims[j];
                  break;
                }
              }
              if(ret == null){
                for(let j=0;j<cubeObj.kpis.length;j++){
                  if(cubeObj.kpis[j].fromCol == cid){
                    ret = cubeObj.kpis[j];
                    break;
                  }
                }
              }
              return ret;
            };

            var lRef = $("#cubelefttree").jstree(true);
            var nodes = lRef.get_node('leftroot').children;
            for(let i=0; nodes&&i<nodes.length; i++){
              var id = nodes[i];
              if(findcol(id) != null){
                lRef.hide_node(id);
              }
            }
          });
        }
      }, this);
    },
    initRightCubeTree(){
       var ts = this;
       var cube = this.pageJson;
       if(!cube.tid){
         return;
       }
        //加载立方体字段
        var targdt = [{id:'cbroot', text:'数据立方体', icon:'fa fa-cubes', state:{opened:true}, children:[]}];
        targdt[0].children.push({id:"cubewd", text:"维度",icon:'fa fa-gears', state:{opened:true}, children:[]});
        targdt[0].children.push({id:"cubedl", text:"度量",icon:'glyphicon glyphicon-signal', state:{opened:true}, children:[]});
        if(cube && cube.dims && cube.kpis){ //给立方体添加维度及指标
          var dims = targdt[0].children[0].children;
          var groupexist = function(grouptype){
            var ls = dims;
            var ret = null;
            for(let k=0; k<ls.length; k++){
              if(ls[k].id == grouptype){
                ret = ls[k];
                break;
              }
            }
            return ret;
          }
          for(let i=0; i<cube.dims.length; i++){
            var d = cube.dims[i];
            var obj = {id: "d" + d.id, text:d.dimdesc, li_attr:{tp:"dim",drag:true,col:d.colname,dispName:d.dimdesc,vtype:d.vtype,alias:d.alias, dimtype:d.type,colkey:(d.tableColKey==null?"":d.tableColKey), coltext:(d.tableColName==null?"":d.tableColName),ordcol:(d.ordcol==null?"":d.ordcol), dimord:(d.dimord==null?"":d.dimord), dateformat:(d.dateformat==null?"":d.dateformat), targetId:d.id, fromCol:d.fromCol,
              ispcdim:d.ispcdim,pcId:d.pcId,pcPid:d.pcPid,pcdimlevel:d.pcdimlevel,levelCol:d.levelCol},icon:"glyphicon glyphicon-stop icon_dim"};

            if(d.grouptype != "" && d.grouptype != null){
              var group = groupexist(d.grouptype);
              if(group == null){
                obj = {id:d.grouptype,text:d.groupname, icon:"fa fa-tasks", state:{opened:true}, children:[obj],li_attr:{tp:'group',dispName:d.groupname,drag:true,targetId:d.grouptype}};
                targdt[0].children[0].children.push(obj);
              }else{
                group.children.push(obj);
              }
            }else{
              targdt[0].children[0].children.push(obj);
            }
          }
          var kpis = targdt[0].children[1].children;
          var kpigroupexist = function(kpiGroup){
            var ret = null;
            for(let l=0; l<kpis.length; l++){
              if(kpis[l].id == kpiGroup){
                ret = kpis[l];
                break;
              }
            }
            return ret;
          }
          for(let i=0; i<cube.kpis.length; i++){
            var k = cube.kpis[i];
            //对于计算指标，colname 存的是计算公式，而对于非计算指标，需要取alias来代替colname, 在保存的时候会自动拼接
            var obj = {id:"k" + k.colId, text:k.aggre+'('+k.name+")",state:{opened:true},li_attr:{tp:"kpi",drag:true,aggre:k.aggre,col:(k.calcKpi==1||k.calc == 1?k.col:k.fromCol), unit:(k.unit==null?"":k.unit), fmt:(k.fmt==null?"":k.fmt), dispName:k.name, alias:k.alias,kpinote:(k.kpinote==null?"":k.kpinote),calcKpi:k.calcKpi,calc:k.calc,targetId:k.colId, fromCol:k.fromCol, distinctCol: k.distinctCol},icon:(k.calcKpi==0?"glyphicon glyphicon-stop icon_kpi":"fa fa-circle icon_kpi")};
            if(k.kpiGroup != null && k.kpiGroup != "" ){
              var group = kpigroupexist(k.kpiGroup);
              if(group == null){
                kpis.push({id:k.kpiGroup,text:k.kpiGroupName, icon:"glyphicon glyphicon-folder-open", state:{opened:true}, children:[obj],li_attr:{tp:'kpigroup',dispName:k.kpiGroupName,drag:true,targetId:k.kpiGroup}});
              }else{
                group.children.push(obj);
              }
            }else{
              kpis.push(obj);
            }
          }
        }
        var rightRef = $("#cuberighttree").jstree(true);
        if(rightRef){
          rightRef.destroy();
        }
        $("#cuberighttree").jstree({
          core:{
              check_callback:function(operation, source, node_parent, node_position, more){
                if(operation == 'move_node'){  //控制 dnd
                  var point = node_position == 0 ? "append":"";
                  if(!more.ref){
                    return true;
                  }
                  var node = more.ref;
                  if(!node.li_attr || !node.li_attr.drag || node.li_attr.drag ==false ){
                    return false;
                  }
                  var s = source.li_attr.tp, c = node.li_attr.tp;
                  //指标和分类不能放到维度区域
                  if((s == 'kpigroup' || s=="kpi") && (c == 'dim' || c == 'group')){
                    return false;
                  }
                  //维度和分组不能拖到指标区域
                  if((s == 'dim' || s == 'group') && ( c == "kpigroup" || c == "kpi")){
                    return false;
                  }
                  //分组不能拖放到维度下
                  if(((s == "group" && c == "dim") || (s == "kpigroup" && c == "kpi")) && point == "append"){
                    return false;
                  }
                  //指标不能放到指标下，维度不能放到维度下,分类不能拖放到分类下
                  if((s=="kpi" && c == "kpi" && point == "append") || (s == "dim" && c == "dim" && point == "append") || (s=="group" && c=="group" && point=="append") || (s=="kpigroup" && c=="kpigroup" && point=="append" )){
                    return false;
                  }
                  source.li_attr.isupdate = 'y'
                  return true;
                }else{
                  return true;
                }
              },
              dblclick_toggle:false,
              data:targdt
            },
            dnd:{
              is_draggable:function(node){
                node = node[0];
                if(node.li_attr && node.li_attr.drag){
                return true;
              }else{
                return false;
              }
              },
              large_drop_target:'selected',
              large_drag_target:'selected'
            },
            "plugins" : [
                "wholerow","dnd"
              ]
        }).bind("ready.jstree", function(){

        }).bind("dblclick.jstree", function(e, data){
          ts.editcubecol();
        });
      },
    addDateDim(){
      this.$refs['dateDimForm'].showDailog(this.cols, this.pageJson.tid);
    },
    addgroup(){
      this.$refs['dimGroupForm'].showDailog();
    },
    kpiCatalog(){
      this.$refs['kpiGroupForm'].showDailog();
    },
    //动态指标
    addDyna(isupdate){
      this.$refs['calcKpiForm'].showDailog(isupdate, this.pageJson.tid);
    },
    editcubecol(){
      var rightRef = $("#cuberighttree").jstree(true);
      var right = rightRef.get_selected(true);
      if(right.length == 0 || !right[0].li_attr.tp){
        this.$notify.error("您还未选择需要编辑的度量或维度。");
        return;
      }
      right = right[0];
      var colid = right.id;
      if(!colid){
        return;
      }
      //计算指标特殊处理
      if(right.li_attr.tp == 'kpi' && right.li_attr.calcKpi == 1){
        this.addDyna(true, right.id);
        return;
      }
      var leftRef = $("#cubelefttree").jstree(true);
      var cols = leftRef.get_node('leftroot').children;
      var ncols = [];
      for(let i=0; i<cols.length; i++){
        var c = leftRef.get_node(cols[i]);
        ncols.push({
          id:c.id, name:c.text, vtype:c.li_attr.vtype
        });
      }
      this.$refs['cubeColForm'].showDailog(right, ncols);
    },
    cube2ds(){
      var rightRef = $("#cuberighttree").jstree(true);
      var leftRef = $("#cubelefttree").jstree(true);
      var right = rightRef.get_selected(true);
      if(right.length == 0 || !(right[0].li_attr) || !right[0].li_attr.tp){
        this.$notify.error("您还未选择需要删除的度量或维度。");
        return;
      }
      right = right[0];
      if(right.li_attr.tp == 'group'){
        if(right.children && right.children.length > 0){
          this.$notify.error("您要删除的分组含有维度，不能删除。");
          return;
        }
      }
      if(right.li_attr.tp == 'kpigroup'){
        if(right.children && right.children.length > 0){
          this.$notify.error("您要删除的分类下含有度量，不能删除。");
          return;
        }
      }
      if(right.li_attr.tp != 'group'){ //分组删除不用关联左边树
        var id = right.li_attr.fromCol;   //通过 refId 引用s数据集的字段ID
        leftRef.show_node(id);
      }
      if(this.delObj){
        this.delObj.push({'type':right.li_attr.tp, id: right.li_attr.targetId}); //在修改立方体时用来删除的内容
      }
      rightRef.delete_node(right);
    },
    ds2cube(){
      var leftRef = $("#cubelefttree").jstree(true);
      var lefts = leftRef.get_selected(true);
      if(lefts.length == 0){
        this.$notify.error("您还未从左边选择字段。");
        return;
      }
      const exef = (left)=>{
          if(!left.li_attr){
            this.$notify.error("请选择字段。");
            return false;
          }
          if(leftRef.is_hidden(left)){
            return true;
          }
          var rightRef = $("#cuberighttree").jstree(true);
          var right = rightRef.get_selected(true);
          if(right.length == 0){
            this.$notify.error("您还未选择右边度量或维度。");
            return false;
          }
          right = right[0];
          var parent = right.parent;
          if(!parent){
            this.$notify.error("您还未选择右边度量或维度。");
            return false;
          }
          parent = rightRef.get_node(parent);
          if(right.id == 'cubedl' || parent.id == 'cubedl' || (parent.li_attr && parent.li_attr.tp == "kpigroup")){
            //生成ID
            var cid = this.findCubeMaxId();
            //calc 表示是否是动态字段，
            //calcKpi 表示是否是计算指标
            var o = {id:cid.id, text:'sum('+left.text+")",li_attr:{tp:"kpi",fmt:'#,###',drag:true,aggre:"sum",col:(left.li_attr.expression==null?left.li_attr.col:left.li_attr.expression), dispName:left.text,alias:"d"+cid.aliasId,fromCol:left.li_attr.col,calcKpi:0, calc:(left.li_attr.expression==null?0:1)},icon:"glyphicon glyphicon-stop icon_kpi"};
            if(right.id == 'cubedl' || (parent.id=="cubedl" && right.li_attr.tp == "kpigroup")){
              rightRef.create_node(right.id, o);
              rightRef.open_node(right.id);
            }else{
              //获取位置
              var cnodes = rightRef.get_node(right.parent);
              var idx = -1;
              for(let j=0; j<cnodes.children.length; j++){
                if(cnodes.children[j] == right.id){
                  idx = j;
                  break;
                }
              }
              rightRef.create_node(right.parent, o, idx + 1);
            }
            leftRef.hide_node(left.id);
          }else if(right.id == 'cubewd' || parent.id == 'cubewd' || (parent.li_attr && parent.li_attr.tp == 'group')){
            var cid = this.findCubeMaxId();
            var o = {id:cid.id, text:left.text, li_attr:{tp:"dim",drag:true,col:(left.li_attr.expression==null?left.li_attr.col:left.li_attr.expression),dispName:left.text,tname:left.li_attr.tname,vtype:left.li_attr.vtype,
                alias:"k"+cid.aliasId,fromCol:left.li_attr.col},icon:"glyphicon glyphicon-stop icon_dim", targetId:""};  //通过targetId 来指引对应数据库的的字段 ID, 用在修改上
            if(right.id == 'cubewd' || (parent.id == 'cubewd' && right.li_attr.tp == 'group')){
              rightRef.create_node(right.id, o);
              rightRef.open_node(right.id);
            }else{
              //获取位置
              var cnodes = rightRef.get_node(right.parent);
              var idx = -1;
              for(let j=0; j<cnodes.children.length; j++){
                if(cnodes.children[j] == right.id){
                  idx = j;
                  break;
                }
              }
              rightRef.create_node(right.parent, o, idx + 1);
            }
            leftRef.hide_node(left.id);
          }
      }
      $(lefts).each((a, b)=>{
        if(b.id === 'leftroot'){
          return;
        }
        return exef(b);
      });

    },
    findCubeMaxId(){
      var ret = 0;
      var maxAliasId = 0;
      var ref = $("#cuberighttree").jstree(true);
      var node = ref.get_node('#');
      var exec = function(node){
        var nodes = node.children;
        if(!nodes){
          return;
        }
        $(nodes).each(function(a, b){
          var tnode = ref.get_node(b);
          if(tnode.id > ret){
            ret = Number(tnode.id);
          }
          if(tnode.li_attr && tnode.li_attr.alias) {
            var alias = Number(tnode.li_attr.alias.replace(/[d|k]/g, ''));
            if(!isNaN(alias) && alias > maxAliasId) {
              maxAliasId = alias;
            }
          }
          exec(tnode);
        });
      }
      exec(node);
      return {id:ret + 1, aliasId:maxAliasId + 1};
    },
    //根据模型自动生成维度和度量
    autoCube(){
      var cols = this.cols;
      var leftRef = $("#cubelefttree").jstree(true);
      var rightRef = $("#cuberighttree").jstree(true);
      var cols = leftRef.get_node("leftroot").children;
      for(let x=0; x<cols.length; x++){
        var left = leftRef.get_node(cols[x]);
        if(leftRef.is_hidden(left)){
          continue;
        }
        var vtype = left.li_attr.vtype;
        if(vtype == "Double" || vtype == "Int" || vtype === 'Long'){
          //添加到度量
          var cid = this.findCubeMaxId();
          var o = {id:cid.id, text:'sum('+left.text+")",li_attr:{tp:"kpi",fmt:'#,###',drag:true,aggre:"sum",col:(left.li_attr.expression==null?left.li_attr.col:left.li_attr.expression),colNote:left.li_attr.colNote, dispName:left.text,
              alias:"d"+cid.aliasId,vtype:vtype,fromCol:left.li_attr.col,calcKpi:0, calc:(left.li_attr.expression==null?0:1)},icon:"glyphicon glyphicon-stop icon_kpi"};
          rightRef.create_node('cubedl', o);
          leftRef.hide_node(left);
        }else if(vtype == "Datetime" || vtype == 'Date'){  //忽略

        }else{
          //添加到维度
          var cid = this.findCubeMaxId();
          var o = {id:cid.id, text:left.text, li_attr:{tp:"dim",drag:true,col:(left.li_attr.expression==null?left.li_attr.col:left.li_attr.expression),dispName:left.text,colNote:left.li_attr.colNote,tname:left.li_attr.tname,vtype:left.li_attr.vtype,
              alias:"k"+cid.aliasId,fromCol:left.li_attr.col},icon:"glyphicon glyphicon-stop icon_dim", targetId:""};
          rightRef.create_node('cubewd', o);
          leftRef.hide_node(left);
        }
      }
      rightRef.open_node('cubewd');
      rightRef.open_node('cubedl');
    },
    //立方体(jstree中)信息转换到 pageInfo json 中
    cube2json(){
        var rightRef = $("#cuberighttree").jstree(true);
        var exec = function(node, allnodes){
          var nodes = node.children;
          if(!nodes){
            return;
          }
          $(nodes).each(function(a, b){
            var tnode = rightRef.get_node(b);
            allnodes.push(tnode);
            exec(tnode, allnodes);
          });
        }
        var cubeDim = [];
        var dims = []
        exec(rightRef.get_node('cubewd'), dims);
        //if(dims.length == 0){
        //	msginfo("您还未配置维度。");
        //	return;
        //}
        var curGroup = null;
        for(let i=0; i<dims.length; i++){
          var d = dims[i];
          if(d.li_attr.tp == "group"){
            curGroup = d;
          }else{
            var obj = {name:d.li_attr.dispName, type:d.li_attr.dimtype,col:d.li_attr.col, alias:d.li_attr.alias, vtype: d.li_attr.vtype, colkey:d.li_attr.colkey,coltext:d.li_attr.coltext,ordcol:d.li_attr.ordcol,dimord:d.li_attr.dimord, dateformat:d.li_attr.dateformat,targetId:d.li_attr.targetId,isupdate:d.li_attr.isupdate,fromCol:d.li_attr.fromCol,
                ispcdim:d.li_attr.ispcdim, pcId:d.li_attr.pcId, pcPid:d.li_attr.pcPid, pcdimlevel: d.li_attr.pcdimlevel, levelCol: d.li_attr.levelCol};
            var p =  rightRef.get_node(d.parent);
            if(p.li_attr && p.li_attr.tp == "group"){
              obj.groupName = p.text;
              obj.groupId = p.id;
            }else{
              obj.groupName = "";
              obj.groupId = "";
            }
            cubeDim.push(obj);
          }
        }
        var cubeKpi = [];
        var kpis = [];
        exec(rightRef.get_node('cubedl'), kpis);
        /**
        if(kpis.length == 0){
          this.$notify.error("您还未配置度量。");
          return;
        }
         */
        for(let i=0; i<kpis.length; i++){
          var t = kpis[i];
          if(t.li_attr.tp == "kpigroup"){

          }else{
            var o = {name:t.li_attr.dispName,col:t.li_attr.col,alias:t.li_attr.alias,fmt:t.li_attr.fmt,unit:t.li_attr.unit,aggre:t.li_attr.aggre,kpinote:t.li_attr.kpinote,calcKpi:t.li_attr.calcKpi,calc:t.li_attr.calc,targetId:t.li_attr.targetId,isupdate:t.li_attr.isupdate,fromCol:t.li_attr.fromCol, distinctCol: t.li_attr.distinctCol};
            var p = rightRef.get_node(kpis[i].parent);
            if(p.li_attr && p.li_attr.tp == "kpigroup"){
              o.kpiGroupName = p.text;
              o.kpiGroup = p.id;
            }else{
              o.kpiGroupName = "";
              o.kpiGroup = "";
            }
            cubeKpi.push(o);
          }
        }
        var pageJson = this.pageJson;
        pageJson.dims = cubeDim;
        pageJson.kpis = cubeKpi;

        if(!this.showBtn){
          pageJson.delObj = this.delObj;
        }
    },
    saveCube(){
       this.cube2json();
       var pageJson = this.pageJson;
       if(pageJson.kpis.length === 0){
          this.$notify.error("您还未配置度量。");
          return;
       }
       ajax({
          type:"POST",
          url:"model/saveCube.action",
          dataType:"json",
          postJSON:true,
          data:JSON.stringify(pageJson),
          success:(resp)=>{
            this.$notify.success('立方体配置成功。');
            //跳转到已建模管理　
            this.$parent.gotoSubject();
          }
        }, this);
    }
  },
  watch: {
    "pageJson.tid":function(v){
      if(v){
        //this.initLeftTree();
        //this.initRightCubeTree();
      }
    }
  }
};
</script>
