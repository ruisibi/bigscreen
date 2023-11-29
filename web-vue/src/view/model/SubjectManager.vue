<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="wrapper-content">
	   <div class="row" id="maindiv">
		   <div class="col-sm-3">
				<div class="ibox">
					<div class="ibox-title">模型分类</div>
					<div class="ibox-content" style="padding:5px;">
						<div id="typetree"></div>
					</div>
				</div>
		   </div>
		    <div class="col-sm-9">
				<div class="ibox">
					<div class="ibox-title">
					已建模型列表
					</div>
					<div class="ibox-content" style="padding:10px;">

					<el-row>
						<el-col :span="16">
							<div class="btn-group" role="group" style="padding-bottom:10px;">
								<button type="button" class="btn btn-outline btn-default" title="新增" @click="newSubject()">
									<i class="glyphicon glyphicon-plus" aria-hidden="true"></i>
								</button>
								<button type="button" class="btn btn-outline btn-default" title="编辑" @click="viewsub()">
									<i class="glyphicon glyphicon-edit" aria-hidden="true"></i>
								</button>
								<button type="button" class="btn btn-outline btn-default" title="删除" @click="delsub()">
									<i class="glyphicon glyphicon-trash" aria-hidden="true"></i>
								</button>
							</div>
						</el-col>
						<el-col :span="8">
							<el-input placeholder="请输入关键字" size="mini" v-model="search" clearable class="input-with-select">
								<el-button slot="append" icon="el-icon-search" @click="loadDatas()"></el-button>
							</el-input>
						</el-col>
					</el-row>


						<el-table :data="tableData" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
							<el-table-column label="" width="45">
							<template slot-scope="scope">
								<el-radio v-model="checked" name="myselect" :label="scope.row.tid">&nbsp;</el-radio>
							</template>
							</el-table-column>
							<el-table-column align="center" width="80" prop="tid" label="标识"></el-table-column>
							<el-table-column align="center" prop="tDesc" label="名称"></el-table-column>
							<el-table-column align="center" prop="tName" label="对应表"></el-table-column>
							<el-table-column align="center" prop="tNote" label="说明"></el-table-column>
							<el-table-column align="center" prop="restUrl" label="restUrl"></el-table-column>
						</el-table>
						<el-pagination
							background
							@size-change="handleSizeChange"
							@current-change="handleCurrentChange"
							:page-sizes="[10, 20, 50, 100]"
							:current-page="page"
							:page-size="rows"
							layout="total, sizes, prev, pager, next, jumper"
							:total="total">
						</el-pagination>
					</div>
				</div>
		    </div>
	   </div>
	   <cubeEditor ref="cubeEditorForm"></cubeEditor>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import cubeEditor from '@/view/model/CubeEditor'
import operationDailog from '@/components/OperationDailog'
import $ from "jquery";

export default {
  data() {
    return {
	  tableData:[],
	  checked:null,
	  typeId: null,
	  search:null,
		total:0,
		page:1,
		rows:10,
    }
  },
  components: {
	  operationDailog,cubeEditor
	},
  mounted() {
	this.loadDatas();
	this.initTree();
  },
  computed: {},
  methods: {
	   loadDatas:function(){
			let ts = this;
			ajax({
				type:"GET",
				data:{page:this.page, rows:this.rows, typeId: this.typeId, search: this.search},
				url:"model/listSubject.action",
				success:function(resp){
					ts.tableData = resp.rows;
					ts.total = resp.total;
				}
			}, ts);
		},
		selectme:function(a, b){
			this.checked = a.tid;
		},
		handleSizeChange(v){
			this.rows = v;
			this.loadDatas();
		},
		handleCurrentChange(v){
			this.page = v;
			this.loadDatas();
		},
		newSubject(){
			this.$router.push({path:"/model/newCubeStep", query:{}});
			//添加菜单
			this.$parent.$refs['navMenuForm'].menuAdd({menuId:109, menuName:"创建新的模型", menuUrl:"/model/newCubeStep"});
		},
		viewsub(){
			if(!this.checked){
				this.$notify.error("您还未勾选数据。");
				return;
			}
			this.$refs['cubeEditorForm'].showInfo(this.checked);
		},
		delsub(){
			if(!this.checked){
				this.$notify.error("您还未勾选数据。");
				return;
			}
			if(confirm("是否确认删除？")){
				ajax({
					url:"model/delSubject.action",
					type:"GET",
					data:{tid:this.checked},
					dataType:"json",
					success:()=>{
						this.checked = null;
						this.loadDatas();
					}
				}, this);
			}
		},
		initTree: function () {
			let ts = this;
			$("#typetree")
				.jstree({
				core: {
					check_callback: true,
					data: function (obj, callback) {
					if (obj.id == "#") {
						callback.call(this, [
						{
							id: "zty",
							text: "主题域",
							children: true,
							state: { opened: true },
							icon: "glyphicon glyphicon-globe",
						},
						]);
					} else {
						ajax({
						type: "GET",
						data: {id: obj.id},
						postJSON: false,
						url: 'model/SubjectType.action',
						success: function (resp) {
							callback.call(this, resp.rows);
						},
						}, ts);
					}
					},
				},
				plugins: ["wholerow"],
			}).bind("changed.jstree", function(e){
				var ref = $("#typetree").jstree(true);
				var node = ref.get_selected(true);
				node = node[0];
				var type = node.id;
				if(type == "zty"){
					type = "";
				}
				ts.typeId = type;
				ts.loadDatas();
			});
		},
  },
  watch: {},
  beforeRouteLeave(to, from, next) {
    this.$destroy();
    next();
  }
};
</script>
