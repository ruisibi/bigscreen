<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
	<operationDailog title="数据集信息查看" mainDiv="maindiv" :callback="saveTableInfo" ref="tableInfoPanel">
		<el-form :model="cfg" :rules="rules" ref="tableForm" size="mini">
			<el-tabs v-model="activeName" @tab-click="selectTab">
				<el-tab-pane label="基本信息" name="base">
						<el-form-item label="表名：" label-width="120px">
							{{ cfg.tableName }}
							<a v-if="!(data.income == 'rest' || data.income == 'custom' || data.income == 'es')" class="btn btn-danger btn-xs" @click.stop="modifyTableName(cfg.tableId, cfg.tableName)"> 改名 </a>
						</el-form-item>
						<el-form-item label="接口地址：" label-width="120px" v-if="data.income==='rest'">
							{{ data.restUrl }}
						</el-form-item>
						<el-form-item label="请求方式：" label-width="120px" v-if="data.income==='rest'">
							{{ data.restPostType }}
						</el-form-item>
						<el-form-item label="中文名：" label-width="120px" prop="tableNote">
							<el-input v-model="cfg.tableNote"></el-input>
						</el-form-item>
						<el-form-item label="备注信息：" label-width="120px" prop="tableDesc">
							<el-input v-model="cfg.tableDesc"></el-input>
						</el-form-item>
						<el-form-item label="来源：" label-width="120px" prop="income">
							<span v-if="cfg.income=='ds'">数据集</span>
							<span v-if="cfg.income=='custom'">已有表</span>
							<span v-if="cfg.income=='rest'">rest接口</span>
						</el-form-item>
						<el-form-item label="创建人：" label-width="120px">
							{{data.crtUserName}}
						</el-form-item>
						<el-form-item label="创建时间：" label-width="120px">
							{{data.crtDate}}
						</el-form-item>

				</el-tab-pane>
				<el-tab-pane label="表字段" name="cols">
					<el-table :data="opts.cols" :height="calcHeight()" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">

						<el-table-column align="center" prop="colId" label="标识" width="120"></el-table-column>
						<el-table-column align="left" prop="colName" label="字段名"></el-table-column>
						<el-table-column align="center" prop="colType" label="类型"></el-table-column>
						<el-table-column align="center" prop="colLength" label="长度"></el-table-column>
						<el-table-column align="center" prop="colScale" label="精度"></el-table-column>
						<el-table-column align="left" prop="defvalue" label="默认值"></el-table-column>
						<el-table-column align="left" prop="expression" label="表达式"></el-table-column>
						<el-table-column align="left" prop="colNote" label="备注"></el-table-column>
					</el-table>
				</el-tab-pane>
				<el-tab-pane label="数据权限" name="dataControl" v-if="income !=='rest'">
					<el-form-item label="数据权限：" label-width="120px" prop="datacontrol">
						 <el-radio size="mini"  v-model="cfg.datacontrol" :label="true" border>启用</el-radio>
   						 <el-radio size="mini"  v-model="cfg.datacontrol" :label="false" border>禁用</el-radio>
					</el-form-item>
					<el-form-item label="映射字段：" label-width="120px" prop="dataControlCol" v-if="cfg.datacontrol">
						<el-select v-model="cfg.dataControlCol" placeholder="请选择" style="width:100%">
							<el-option
								v-for="item in opts.cols"
								:key="item.colId"
								:label="item.colName"
								:value="item.colName">
							</el-option>
						</el-select>
						<div class="text-warning">(数据权限通过此字段过滤数据，需要用户重新登录才生效)</div>
					</el-form-item>
				</el-tab-pane>
				<el-tab-pane label="数据预览" name="dataview">
					<tableDataView ref="tableViewForm" v-if="showDatas" :tableId="cfg.tableId==null?0:cfg.tableId"></tableDataView>
				</el-tab-pane>
			</el-tabs>

		</el-form>
	</operationDailog>
</template>

<script>
import { baseUrl, ajax, newGuid } from "@/common/biConfig";
import operationDailog from '@/components/OperationDailog'
import $ from "jquery";
import {containSpecial} from '@/view/etl/EtlUtils.js'
import tableDataView from '@/view/etl/table/TableDataView'

export default {
  data() {
    return {
	  activeName:"base",
	  checked:null,
	  showDatas: false,  //默认不显示数据预览
	  cfg:{
		  tableId:null,
		  tableName:null,
		  tableNote:null,
		  tableDesc:null,
		  income:null,
		  datacontrol:false,
		  dataControlCol:null
	  },
	  data:{},
	  income:null, //来源对应导入表/转换表/填报表/自定义
	  rules:{

	  },
	  opts:{
		 cols:[]
	  },
	}
  },
  components: {
	  operationDailog, tableDataView
	},
  mounted() {

  },
  computed: {},
  methods: {
	  calcHeight(){
		  var h = $(".wrapper-content").height();
      		return h - (this.income==='imp' || this.income ==='db' ? 180 : 160);
	  },
	   showInfo(tableId, income){
		   this.activeName = "base";
		   this.cfg.tableId = tableId;
		   this.$refs['tableInfoPanel'].showDailog();
		   this.getTableInfo();
		   this.income = income;
	   },
	   //选项卡切换
	   selectTab(a, b){
		   if(this.activeName === 'dataview'){
			   this.showDatas = true;
		   }else{
			   this.showDatas = false;
		   }
		   //console.log(this.activeName);
	   },
	   saveTableInfo(){
		ajax({
			type: "post",
			url: "etl/updateTableInfo.action",
			dataType: "json",
			contentType: "application/json",
			data: JSON.stringify({
				tableId: this.cfg.tableId,
				tableNote: this.cfg.tableNote,
				tableDesc: this.cfg.tableDesc,
				dataControlCol: this.cfg.datacontrol ===false ? "" : this.cfg.dataControlCol
			}),
			postJSON:true,
			success:()=>{
				this.saveBack();
			}
		 }, this);
	   },
	   saveBack(){
		   this.$refs['tableInfoPanel'].closeDailog();
		   this.$parent.loadDatas();
	   },
	   getTableInfo(){
		   ajax({
			   url:"etl/getTableInfo.action",
			   type:"POST",
			   data:{tableId:this.cfg.tableId},
			   success:(r)=>{
				   r = r.rows;
				   this.opts.cols = r.metaCols;
				   this.cfg.tableName = r.tableName;
				   this.cfg.tableNote = r.tableNote;
				   this.cfg.tableDesc = r.tableDesc;
				   this.cfg.income = r.income;
				   if(r.dataControlCol){
					   this.cfg.datacontrol = true;
					   this.cfg.dataControlCol = r.dataControlCol;
				   }else{
					   this.cfg.datacontrol = false;
					   this.cfg.dataControlCol = null;
				   }
				   this.data = r;
			   }
		   }, this);
	   },
	   loadCols(){
		   ajax({
			   url:"etl/getTableColumns.action",
			   type:"GET",
			   data:{tableId:this.cfg.tableId},
			   success:(r)=>{
				   this.opts.cols = r.rows;
			   }
		   }, this);
	   },
	   selectme:function(a, b){
			this.checked = a.colId;
		},

		modifyTableName(tid, tname){
			if(confirm("请注意：改名需要源表和新表字段一致!")){
				this.$prompt('请输入新的表名称：', "改名", {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						inputValue: tname,
					}).then(({ value }) => {
						ajax({
							url:"etl/updateTname.action",
							data:{tableName: value, tid: tid},
							type:"POST",
							success:(resp)=>{
								this.saveBack();
							}
						});
					}).catch(() => {

					}
				);
			}
		}
  },
  watch: {},
};
</script>

<style lang="less" scoped>

</style>
