<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
	<operationDailog title="数据模型查看" mainDiv="maindiv" :callback="saveCubeInfo" ref="cubeInfoPanel">
			<el-tabs v-model="activeName">
				<el-tab-pane label="基本信息" name="base">
						<el-form :model="cube" :rules="rules" ref="cubeForm" size="mini">
							<el-form-item label="名称：" label-width="110px" prop="tDesc">
							<el-input v-model="cube.tDesc" ></el-input>
							</el-form-item>
						<el-form-item label="所属分类：" label-width="110px" prop="typeId">

							<el-select v-model="cube.typeId" placeholder="请选择" style="width:100%">
								<el-option v-for="item in opt.types"
									:key="item.dsId"
									:label="item.name"
									:value="item.dsId">
								</el-option>
							</el-select>


						</el-form-item>
						<el-form-item label="备注：" label-width="110px">
							<el-input v-model="cube.tNote" ></el-input>
						</el-form-item>
						<el-form-item label="对应数据表：" label-width="110px" prop="tid">
							{{ cube.tName }}
						</el-form-item>

					</el-form>

				</el-tab-pane>
				<el-tab-pane label="表字段" name="cols">
					<tableCols :pageJson="cube" :showBtn="false" ref="tableColsForm"></tableCols>
				</el-tab-pane>
				<el-tab-pane label="数据预览" name="dataview">
					<tableDataView ref="tableViewForm" :tableId="cube.tid==null?0:cube.tid"></tableDataView>
				</el-tab-pane>
				<el-tab-pane label="立方体" name="cube">
					<cube :pageJson="cube" ref="cubeForm" :showBtn="false"></cube>
				</el-tab-pane>
			</el-tabs>
	</operationDailog>
</template>

<script>
import { baseUrl, ajax, newGuid } from "@/common/biConfig";
import operationDailog from '@/components/OperationDailog'
import tableDataView from '@/view/etl/table/TableDataView'
import tableCols from '@/view/model/TableCols'
import cube from '@/view/model/Cube'
import $ from "jquery";
import { Loading } from 'element-ui'

export default {
  data() {
    return {
	  activeName:"base",
	  height:450,
	  cube:{
		tid:null,
		tName:null,
		tNote:null,
		tDesc:null,
		typeId:null,
		tableType:null,
		kpiCodeColumn:null,
		kpiNameColumn:null,
		kpiValueColumn:null,
		dims:[],
		kpis:[]
	  },
	  rules:{
		  tDesc:[{ required: true, message: "必填", trigger: "blur" }],
		typeId:[{ required: true, message: "必填", trigger: "blur" }],
	  },
	  opt:{
		 types:[]
	  },
	}
  },
  components: {
	  operationDailog, tableDataView, tableCols,cube
  },
  mounted() {

  },
  computed: {},
  methods: {
	   showInfo(tableId){
		   this.activeName = "base";
		   this.$refs['cubeInfoPanel'].showDailog();
		    this.loadCfg(tableId);
	   },
	   loadCfg(tableId){
		   ajax({
				type:"POST",
				url:"model/getSubject.action",
				data:{tableId:tableId,t:Math.random()},
				success:(resp) => {
					var c = resp.rows.table;
					c.dims = c.dimDtos; //前端使用的是 dims
					this.cube.tid = tableId;
					this.cube.tNote = c.tNote;
					this.cube.tDesc = c.tDesc;
					this.cube.typeId = c.typeId;
					this.cube.tName = c.tName;
					this.cube.dims =  c.dims;
					this.cube.kpis = c.kpis;
					this.cube.tableType = c.tableType;
					this.cube.kpiCodeColumn = c.kpiCodeColumn,
					this.cube.kpiNameColumn = c.kpiNameColumn,
					this.cube.kpiValueColumn = c.kpiValueColumn,
					this.opt.types = resp.rows.types;

					var cube = this.$refs['cubeForm'];
					cube.initLeftTree();
    				cube.initRightCubeTree();
				}
			}, this);
	   },
	   saveCubeInfo(){
		 this.$refs['cubeForm'].cube2json();
		 var pageJson = this.cube;
		if(pageJson.kpis.length === 0){
			this.$notify.error("您还未配置度量。");
			return;
		}
          let loadingInstance = Loading.service({fullscreen:true});
		ajax({
			type:"POST",
			url:"model/updateCube.action",
			dataType:"json",
			postJSON:true,
			data:JSON.stringify(pageJson),
			success:(resp)=>{
				loadingInstance.close();
				this.$notify.success('立方体更新成功。');
				this.$refs['cubeInfoPanel'].closeDailog();
				this.$parent.loadDatas();
			}
			}, this, loadingInstance);
	   }
  },
  watch: {},
};
</script>

<style lang="less" scoped>

</style>
