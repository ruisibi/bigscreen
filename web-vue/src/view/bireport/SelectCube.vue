<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<el-dialog title="选择数据模型" :visible.sync="show" width="70%" :close-on-click-modal="false" custom-class="nopadding" :append-to-body="true">
		  <template slot="title">
			<span class="el-dialog__title">选择数据模型</span>
			<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
			<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
			</button>
		 </template>
		  <el-row :gutter="20">
			  <el-col :span="6">
				  <div :id="'cubetypetree'+useIn"></div>
			  </el-col>
			  <el-col :span="18">
				<el-input v-model="search"	size="mini" placeholder="输入关键字搜索">
					<el-button slot="append" icon="el-icon-search" @click="searchme"></el-button>
				</el-input>
				<div class="">
					<el-table :data="tableData" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
						<el-table-column label="" width="45">
							<template slot-scope="scope">
								<el-radio v-model="checked" name="myselect2" :label="scope.row.tid">&nbsp;</el-radio>
							</template>
						</el-table-column>
						<el-table-column align="center" prop="tDesc" label="名称"></el-table-column>
						<el-table-column align="center" prop="typeName" label="分类"></el-table-column>
						<el-table-column align="center" prop="tName" label="所属表"></el-table-column>
						<el-table-column align="center" prop="tNote" label="说明"></el-table-column>
					</el-table>
					<el-pagination
						background
						@size-change="handleSizeChange"
						@current-change="handleCurrentChange"
						layout="prev, pager, next"
						:total="total">
					</el-pagination>
				</div>
			  </el-col>
		  </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="save()">确 定</el-button>
      <el-button @click="show = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax} from '@/common/biConfig'
	import $ from 'jquery'

	export default {
	    data(){
			return {
				show:false,
				checked:null,
				tableData:[],
				search:null,
				total:0,
				page:1,
				rows:10,
			}
		},
		props:{
			callback:{
				type:Function,   //点击选择立方体后的回调函数
				required:true
			},
			income:{
				type:String,
				required:false,
			},
			useIn:{
				type:String,  //在哪里使用 olap/report/dashboard/bigscreen
				required:true,
			}
		},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			select(){
				this.show = true;
				this.search = null;
				this.loadData();
				this.$nextTick(()=>this.initTypeTree());
			},
			save(){
				let chk = this.checked;
				if(!chk){
					this.$notify.error({title: '未勾选数据',offset: 50});
					return;
				}

				this.callback(chk);

				this.show = false;
			},
			handleSizeChange(v){
				this.rows = v;
				let type = this.getTypeId();
				this.loadData(type);
			},
			handleCurrentChange(v){
				this.page = v;
				let type = this.getTypeId();
				this.loadData(type);
			},
			selectme:function(a, b){
				this.checked = a.tid;
			},
			searchme(){
				var ref = $("#cubetypetree"+this.useIn).jstree(true);
				let node = ref.get_selected();
				let typeId = node.length == 0 ? null : node[0];
				if(typeId == 0){
					typeId = null;
				}
				this.loadData(typeId);
			},
			loadData(typeId){
				ajax({
					url:"model/listAuthSubject.action",
					type:"POST",
					data:{rows:this.rows, page:this.page,search:this.search, typeId:typeId, income: this.income},
					success:(resp)=>{
						this.total = resp.total;
						this.tableData = resp.rows;
					}
				}, this);
			},
			initTypeTree(){
				let ts = this;
				var ref = $("#cubetypetree"+this.useIn).jstree(true);
				if(ref){
					ref.destroy();
				}
				$("#cubetypetree"+this.useIn).jstree({
					core:{
						check_callback:true,
						data:function (obj, callback) {
							if(obj.id == '#'){
								callback.call(this, [{id:'0', text:'主题域', children:true, state:{opened:true}, icon:"fa fa-home"}]);
							}else{
								ajax({
									url:"model/listSubjectType.action",
									data:{},
									type:"POST",
									success:(resp)=>{
										callback.call(this, resp.rows);
									}
								}, ts);
							}
						}
					},
					"plugins" : [
						"wholerow"
					]
				}).bind("changed.jstree", function(e){
					let type = ts.getTypeId();
					ts.loadData(type);
				});
			},
			getTypeId(){
				var ref = $("#cubetypetree"+this.useIn).jstree(true);
				var node = ref.get_selected(true);
				if(!node || node.length == 0){
					return null;
				}
				node = node[0];
				var type = node.id;
				if('0' == node.id){
					type = null;
				}
				return type;
			}
		},
		watch: {
		}
	}
</script>
