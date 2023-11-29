<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <div class="layout-left">
        <div class="tabs">
            <div class="tabs-container">
                <ul class="nav nav-tabs" style="margin-top: 3px;">
                    <li :class="isActive('data-tab-1')"><a data-toggle="tab" @click="switchTabs('data-tab-1')" idx="0" aria-expanded="true">组件</a>
                    </li>
                    <li :class="isActive('data-tab-2')"><a data-toggle="tab" @click="switchTabs('data-tab-2')" idx="1" aria-expanded="false">立方体</a>
                    </li>
                    <li :class="isActive('data-tab-3')"><a data-toggle="tab" @click="switchTabs('data-tab-3')" idx="2" aria-expanded="false">数据表</a>
                    </li>
                </ul>
            </div>

            <div class="tab-content tab-content2">
                <div id="data-tab-1" class="tab-pane" :class="isActive('data-tab-1')">
                    <div class="panel-body" style="padding:0px;overflow:hidden;">
                        <div id="paramtree"></div>
                        <div id="comptree"></div>
                    </div>
                </div>
                <div id="data-tab-2" class="tab-pane" :class="isActive('data-tab-2')">
                    <div class="panel-body" style="padding:0px;">
                        <div id="datasettree" style="hei"></div>
                    </div>
                </div>
                <div id="data-tab-3" class="tab-pane" :class="isActive('data-tab-3')">
                    <div class="panel-body" style="padding:0px;">
                        <div id="tabletree"></div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 图层 -->
        <div class="ibox box-layer">
            <div class="ibox-title">图层</div>
            <div class="ibox-content" style="padding:0; overflow: auto; height: calc(100% - 31px);">
                <ul class="ul-layer">
                    <template v-for="item in pageInfo.comps">
                        <li :key="item.id" v-if="item.isdelete !== true" :cid="item.id" @click="selectlayer(item)" class="li-layer" :class="getSelected(item)" >{{item.title}}</li>
                    </template>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import "jstree";
import "jstree/dist/themes/default/style.min.css";
import {compExistInSelect} from './../bsUtils'

export default {
  components:{

  },
  props:{
      pageInfo:{
        type:Object,
        required:true,
        default:{}
     }
  },
  data(){
    return {
        tabActive:"data-tab-1"
    }
  },
  mounted(){
    this.regComp();
    //this.initLayerMenu();
  },
  computed: {

  },
  methods: {
    switchTabs(val){
        this.tabActive = val;
    },
    isActive(p){
        return this.tabActive === p ? "active" : ""
    },
    regComp(){
        let ts = this;
        var compDt = [
            {id:"date", text:"时间",li_attr:{tp:"date"},icon:"fa fa-calendar-check-o"},
            {id:"text", text:"文本",li_attr:{tp:"text"},icon:"fa fa-file-text-o"},
            {id:"weather", text:"天气",li_attr:{tp:"weather"},icon:"fa fa-sun-o"},
            {id:"param", text:"参数",li_attr:{tp:"param"},icon:"fa fa-binoculars"},
            {id:"box", text:"数据块",li_attr:{tp:"box"},icon:"fa fa-inbox"},
            {id:"tab", text:"选项卡",li_attr:{tp:"tab"},icon:"fa fa-list-ul"},
            {id:"pic", text:"图片",li_attr:{tp:"pic"},icon:"fa fa-image"},
            {id:"decorate", text:"装饰框",li_attr:{tp:"decorate"},icon:"fa fa-square-o"},
            {id:"line", text:"装饰线",li_attr:{tp:"line"},icon:"fa fa-minus"},
            {id:"chart", text:"图表",li_attr:{tp:"chart"},icon:"fa fa-line-chart"},
            {id:"grid", text:"表格",li_attr:{tp:"grid"},icon:"fa fa-table"},
            {id:"table", text:"交叉表",li_attr:{tp:"table"},icon:"fa fa-list-alt"},
            {id:"detail", text:"详情页",li_attr:{tp:"detail"},icon:"fa fa-newspaper-o"},
            {id:"iframe", text:"嵌套框",li_attr:{tp:"iframe"},icon:"fa fa-file-code-o"},
            {id:"custom", text:"自定义",li_attr:{tp:"custom"},icon:"fa fa-wrench"}
        ];

        var dragfunc = function(treeDiv){
            $("#"+treeDiv+" .jstree-node").draggable({
                cursor: "point",
                appendTo: "body",
                revert: 'invalid',
                revertDuration: 250,
                scroll :false,
                cursorAt: { top: 0, left: 'comptree' == treeDiv ? -10 :-35 },
                helper:function(e){
                    var id = $(this).find("a.jstree-anchor:first").text();
                    return "<div class=\"vakata-dnd\"><span class=\"miconcancel glyphicon glyphicon-remove\"></span>"+id+"</div>";
                },
                start:function(e){
                    var ref = $('#'+treeDiv).jstree(true),node = ref.get_node(this);
                    if(node.id == 'params'){
                        return false;
                    }
                    if(node.li_attr.tp == 'comp'){
                        //resetWindows('min');
                    }
                    return true;
                },
                stop:function(e){
                    var ref = $('#'+treeDiv).jstree(true),node = ref.get_node(this);
                    if(node.li_attr.tp == 'comp'){
                        //resetWindows('max');
                    }
                    $(".indicator").hide();
                }
            });
        }

        $('#comptree').jstree({
            core:{
                data:compDt
            },
            "plugins" : [
                "wholerow"
            ]
        }).bind("ready.jstree",function(a, b){
            dragfunc('comptree');
        });
    },
    initTableTree(){
        var pageInfo = this.pageInfo;
        var ref = $("#tabletree").jstree(true);
        if(ref){
            ref.destroy();
        }
        if(!pageInfo.table || !pageInfo.table.tid){
            $('#tabletree').jstree({
                core:{
                    data:{id:'nodata', text:'您还未选择数据表!', icon:'fa fa-warning icon_kpi'}
                },
                "plugins" : [
                    "wholerow"
                ]
            });
        }else{
            var t = pageInfo.table;
            var dt = {id:t.tid, text:t.tname+"("+t.tnote+")",icon:'fa fa-table', state:{opened:true}, children:[]};
            ajax({
                url:"etl/getTableColumns.action",
                type:"get",
                data:{tableId:t.tid, t:Math.random()},
                success:(ret)=>{
                    ret = ret.rows;
                    for(let i=0; i<ret.length; i++){
                        dt.children.push({id:ret[i].colName, text:ret[i].colName, icon:'fa fa-list icon_kpi',li_attr:{id:ret[i].colName,name:ret[i].colName,tid:t.tid, tname:t.tname,income:t.income, type:ret[i].colType, expression: ret[i].expression}});
                    }
                    var dragfunc = function(){
                        $("#tabletree .jstree-node").draggable({
                            cursor: "point",
                            appendTo: "body",
                            revert: 'invalid',
                            revertDuration: 250,
                            cursorAt: { top: 0, left: -35 },
                            scroll :false,
                            helper:function(e){
                                var id = $(this).find("a.jstree-anchor:first").text();
                                return "<div class=\"vakata-dnd\"><span class=\"miconcancel glyphicon glyphicon-remove\"></span>"+id+"</div>";
                            },
                            start:function(e){
                                var ref = $('#tabletree').jstree(true),node = ref.get_node(this);
                                var attr = node.li_attr;
                                delete attr.id;
                                if($.isEmptyObject(attr)){
                                    return false;
                                }
                                return true;
                            }
                        });
                    }
                    $('#tabletree').jstree({
                        core:{
                            data:dt
                        },
                        "plugins" : [
                            "wholerow"
                        ]
                    }).bind("ready.jstree",function(a, b){
                        dragfunc();
                    }).bind("after_open.jstree", function(){
                        dragfunc();
                    });
                }
            });
        }
    },
    initcubes(cubeId){
        let ref = $("#datasettree").jstree(true);
        if(ref){
            ref.destroy();
        }
        if (!cubeId) {
            $('#datasettree').jstree({
                core: {
                    data: {
                        id: 'nodata',
                        text: '您还未选择立方体',
                        icon: 'fa fa-warning icon_kpi'
                    }
                },
                "plugins": ["wholerow"]
            });
            return;
        } else {
            const dragfunc = () => {
                $("#datasettree .jstree-node").draggable({
                    cursor: "point",
                    appendTo: "body",
                    revert: 'invalid',
                    revertDuration: 250,
                    scroll :false,
                    cursorAt: { top: 0, left: -35 },
                    helper:function(e){
                        var id = $(this).find("a.jstree-anchor:first").text();
                        return "<div class=\"vakata-dnd\"><span class=\"miconcancel glyphicon glyphicon-remove\"></span>"+id+"</div>";
                    },
                    start:function(e){
                        var ref = $('#datasettree').jstree(true),node = ref.get_node(this);
                        var attr = node.li_attr;
                        delete attr.id;
                        if($.isEmptyObject(attr)){
                            return false;
                        }
                        return true;
                    }
                });
            }
            var ts = this;
            $('#datasettree').jstree({
                core: {
                    data:function (obj, callback) {
                        ajax({
                            url:"model/cubeTree.action",
                            data:{selectDsIds:ts.pageInfo.selectDs},
                            success:(resp)=>{
                                callback.call(this, resp.rows);
                            }
                        }, ts);
                    },
                    check_callback: false
                },
                "plugins": ["wholerow"]
            }).bind("ready.jstree", function (a, b) {
                dragfunc();
            }).bind("after_open.jstree", function () {
                dragfunc();
            }).bind("select_node.jstree", function(c, d){
                if(d.node.li_attr && d.node.li_attr.kpiDesc){
                    ts.$notify.success( {title: d.node.li_attr.name, message: d.node.li_attr.kpiDesc});
                }
            });
        }
    },
    /**
     * 选中图层
     */
    getSelected(comp){
        let selects = this.$parent.getSelectComps();
        let r = compExistInSelect(comp.id, selects);
        if(r==true){
            return "layerselect";
        }else{
            return "";
        }
    },
    selectlayer(comp){
        this.$parent.setSelect(comp);
    },
  },
  watch: {
  },
  beforeMount(){

  },
  beforeDestroy(){

  }
}
</script>

<style lang="less" scoped>
    .layout-left {
        position: fixed;
        width: 220px;
        height: calc(100% - 4px);
        background-color: white;
    }
    .layout-left .nav-tabs > li > a {
        padding: 5px 10px 5px 10px;
        width:70px;
        text-align: center;
        font-size: 13px;
        color: #A7B1C2;
        font-weight: 600;
        cursor: pointer;
    }
    .nav-tabs > li.active > a {
        color: #555555;
        cursor: default;
    }
    .tabs {
        height: 300px;
        border: 1px solid #dee5e7;
    }
    .tab-content {
        height: 264px;
        overflow: auto;
    }
    .box-layer {
        height: calc(100% - 304px);
        margin-top: 2px;
    }
    ul.ul-layer {
        margin: 0;
        padding: 0;
    }
    li.li-layer {
        padding: 6px;
        margin: 0;
        border-bottom: solid 1px #edf1f2;
        cursor: pointer;
    }
    li.li-layer:hover {
        background-color: antiquewhite;
    }
    li.layerselect {
        background-color: antiquewhite;
    }
</style>
