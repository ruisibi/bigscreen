<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
	<operationDailog :title="title" mainDiv="maindiv" :callback="saveDset" ref="dsetAddDailog">
		<el-form :model="sqlcfg" :rules="rules" ref="sqlForm" size="mini">
			<!--
			<p class="text-warning">通过sql语句汇总数据到DW/DM层。</p>
			-->
			<el-tabs v-model="activeName" @tab-click="selectTab">
				<el-tab-pane label="基本信息" name="base">
						<el-form-item label="数据集名称：" label-width="120px" prop="tableName">
							<el-input v-model="sqlcfg.tableName" placeholder="英文字符" :disabled="isupdate"></el-input>
						</el-form-item>
						<el-form-item label="数据集中文名：" label-width="120px" prop="tableNote">
							<el-input v-model="sqlcfg.tableNote"></el-input>
						</el-form-item>
						<el-form-item label="备注信息：" label-width="120px" prop="tableDesc">
							<el-input v-model="sqlcfg.tableDesc"></el-input>
						</el-form-item>
				</el-tab-pane>
				<el-tab-pane label="SQL脚本" name="sql">
				<el-row :gutter="10">
					<el-col :span="14">
					<el-form-item label="数据源：" label-width="120px" prop="dsourceId">
						<el-select v-model="sqlcfg.dsourceId" :disabled="this.isupdate" placeholder="请选择" style="width:100%" @change="chgdsource">
							<el-option
							v-for="item in opts.dss"
							:key="item.id"
							:label="item.name"
							:value="item.id">
							</el-option>
						</el-select>
					</el-form-item>

					<el-form-item label="主表：" label-width="120px" prop="srcTables" >
						<el-input v-model="sqlcfg.srcTables" />
					</el-form-item>
					<el-form-item label="SQL脚本：" label-width="120px" prop="tableSql">
						<codemirror v-if="showSql" class="sqlscript" :options="cmCfg" ref="mysqlcode" v-model="sqlcfg.tableSql"></codemirror>
						<p class="text-warning">可在SQL中添加 [cond] 关键字(带中括号)，在数据大屏中会自动替换为查询条件</p>
					</el-form-item>
					</el-col>
					<el-col :span="10">
						<div id="alltables" style="height:380px; width: 100%; overflow:auto; border:1px solid #cfdadd"></div>
					</el-col>
				</el-row>

				</el-tab-pane>
				<el-tab-pane v-if="isupdate === true" label="数据预览" name="dataview">
					<tableDataView ref="tableViewForm" v-if="showDatas" :tableId="tableId==null?0:tableId"></tableDataView>
				</el-tab-pane>
			</el-tabs>
		</el-form>
	</operationDailog>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import operationDailog from '@/components/OperationDailog'
import $ from "jquery";
import { codemirror } from 'vue-codemirror'
import { Loading } from "element-ui";
import {containSpecial, managerType} from '@/view/etl/EtlUtils.js'
import tableDataView from '@/view/etl/table/TableDataView'
require("codemirror/mode/sql/sql.js")

export default {
  data() {
    return {
	  title:"",
	  isupdate:false,
	  activeName:"sql",
	  showDatas:false,
	  showSql:false,
	  sqlcfg:{
		  tableName:null,
		  tableNote:null,
		  tableDesc:null,
		  dsourceId:-1,
		  tableSql:"",
		  srcTables:null,
	  },
	  tableId:null,  //表ID
	  rules:{
		 tableName: [{ required: true, message: "必填", trigger: "blur" }],
		 tableNote: [{ required: true, message: "必填", trigger: "blur" }],
		 srcTables: [{ required: true, message: "必填", trigger: "blur" }],
		 tableSql: [{ required: true, message: "必填", trigger: "blur" }],
		 dsource: [{ required: true, message: "必填", trigger: "blur" }],
	  },
	cmCfg:{ //sql编辑器配置
        mode: "text/x-sql",
        indentWithTabs: true,
        smartIndent: true,
        lineNumbers: true,
        matchBrackets : true,
        autofocus: false,
        lineWrapping:true
      },
	  opts:{
		  dss:[{
			id:-1,name:"BI数据仓"
		}],
		tables:[]

	  }
    }
  },
  components: {
	  operationDailog,codemirror,tableDataView
	},
  mounted() {

  },
  computed: {},
  methods: {
	   addDataset(){
			this.isupdate = false;
			this.activeName = "base";
			this.$refs['dsetAddDailog'].showDailog();
			this.sqlcfg.tableSql = "select  \n from ";
			this.title = "创建SQL语句数据集";
			this.showSql = false;
			this.loadDsource();
			this.initTables(true);

			this.sqlcfg.tableName = null;
			this.sqlcfg.tableNote = null;
			this.sqlcfg.tableDesc = null;
			this.sqlcfg.dsourceId = -1;
			this.sqlcfg.srcTables = null;
	   },
	   updateDataset(id){
			this.isupdate = true;
			this.activeName = "base";
			this.tableId = id;
			ajax({
				url:"etl/getTableInfo.action",
				data:{tableId: id},
				type:"get",
				success:(resp)=>{
					let r = resp.rows;
					this.sqlcfg.tableName = r.tableName;
					this.sqlcfg.tableNote = r.tableNote;
					this.sqlcfg.tableDesc = r.tableDesc;
					this.sqlcfg.dsourceId = r.dsourceId;
					this.sqlcfg.tableSql = r.tableSql;
					this.sqlcfg.srcTables = r.srcTables;
					this.title = "编辑SQL语句数据集";
					this.showSql = false;
					this.loadDsource();
					this.initTables(false);
					this.$refs['dsetAddDailog'].showDailog();
				}
			}, this);
	   },
	   saveDset(){
		   this.$refs['sqlForm'].validate(valid=>{
			   if(valid){
				    var tableName = this.sqlcfg.tableName;

					var reg = /^[a-z|A-Z]/;
					var rt = reg.test(tableName);

					if (rt == false) {
						this.$notify.error("数据集名必须是以字母开始。");
						this.activeName = "base";
						return;
					}
					if (containSpecial(tableName)) {
						this.$notify.error("数据集名称不能包含特殊符号。");
						this.activeName = "base";
						return;
					}


					const exec = ()=>{
						var json = {
							tableName: (this.isupdate==false?"ds"+"_" : "") + tableName,
							dsourceId: this.sqlcfg.dsourceId,
							tableNote: this.sqlcfg.tableNote,
							tableDesc: this.sqlcfg.tableDesc,
							tableSql: this.sqlcfg.tableSql,
							srcTables:this.sqlcfg.srcTables,
						};
						if(this.isupdate==true){
							json.tableId = this.tableId;
						}
						let load = Loading.service({ fullscreen: true });
						ajax({
							type: "POST",
							url: this.isupdate == false ? "model/dataset/save.action" : "model/dataset/update.action",
							dataType: "json",
							data: JSON.stringify(json),
							postJSON:true,
							success: (resp) => {
								this.$refs['dsetAddDailog'].closeDailog();
								if(this.isupdate){
									this.$notify.success("编辑成功");
								}else{
									this.$notify.success("创建成功");
								}
								this.$parent.loadDatas();
							}
						}, this, load);
					}

					//判断表名是否重复
					ajax({
						type: "POST",
						async: false,
						url: "etl/tableExist.action",
						dataType: "json",
						data: {
							"tableName": (this.isupdate==false?"ds_" : "")+ tableName
						},
						success: (resp) => {
							if(resp.rows >= 1 && this.isupdate === false){
								this.$notify.error("数据集名称"+tableName+"已经存在。");
								this.activeName = "base";
								return;
							}
							exec();
						}
					}, this);
			   }
		   });
	   },
	   loadDsource(){
		   //加载数据源
			ajax({
				type:"GET",
				data:{ },
				url:"etl/listDataSource.action",
				success:(resp)=>{
					this.opts.dss.splice(1, this.opts.dss.length);
					this.opts.dss = this.opts.dss.concat(resp.rows);
					//加载默认表
					//this.initTableTree(-1);
				}
			}, this);
	   },
	   chgdsource(){
		   this.initTables(true);
	   },
		//选项卡切换
	   selectTab(){
			if(this.activeName == 'sql'){
			  this.showSql = true;
			}
		   this.showDatas = this.activeName === 'dataview';
	   },
	   loadDsource(){
		   //加载数据源
			ajax({
				type:"GET",
				data:{ },
				url:"etl/listDataSource.action",
				success:(resp)=>{
					this.opts.dss.splice(1, this.opts.dss.length);
					this.opts.dss = this.opts.dss.concat(resp.rows);
					//加载默认表
					//this.initTableTree(-1);
				}
			}, this);
	   },
	   initTables(clearMaster){
		   var ts = this;
		   ajax({
				type: "post",
				url: "etl/listTablesNotEs.action",
				dataType: "json",
				data: {
					t: Math.random(),
					dsource: this.sqlcfg.dsourceId
				},
				success: (resp) => {
					resp = resp.rows;
					this.opts.tables = resp;
					if(clearMaster == true){
						this.sqlcfg.srcTables = null;
					}
					for (let i = 0; i < resp.length; i++) {
						resp[i].id = resp[i].tableName;
						resp[i].text = resp[i].tableName + "(" + resp[i].tableNote + ")";
						resp[i].icon = 'fa fa-table';
						resp[i].children = true;
						resp[i].li_attr = {
							tp: 'table'
						};
					}
					var ref = $("#alltables").jstree(true);
					if(ref){
						ref.destroy();
					}
					$("#alltables").jstree({
					core: {
						check_callback: true,
						data: function data(obj, callback) {
							if (obj.id == '#') {
								callback.call(this, resp);
							} else {
								ajax({
									type:"post",
									url:"etl/ds/listTableColumns.action",
									dataType:"json",
									data:{
										tname: obj.id,
                  						dsId: ts.sqlcfg.dsourceId
									},
									success:function(r){
										callback.call(this, r.rows);
									}
								}, ts);
						 	}
						}
					},
					"plugins": ["types", "wholerow"]
					}).bind("changed.jstree", function (event) {
						var ref = $("#alltables").jstree(true);
						var node = ref.get_node(ref.get_selected());
						var txt = node.id;

						if (node.li_attr.tp == 'col') {
							txt = txt + ",";
						} else {
							txt = txt + " ";
						}
						var o = ts.$refs['mysqlcode'].codemirror;
						let pos1 = o.getCursor();
						let pos2 = {};
						pos2.line = pos1.line;
						pos2.ch = pos1.ch;
						if(o.somethingSelected){
							o.replaceSelection(txt);
						}else{
							o.replaceRange(txt,pos2);
						}

					});
				}
			}, this);
	   },
  },
  watch: {},
};
</script>

<style lang="less">
.sqlscript .CodeMirror {
    border: 1px solid #DCDFE6;
    border-radius: 5px;
    height: 280px;
    width: 100%;
    font-size: 14px;
}
</style>>
