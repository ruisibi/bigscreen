<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="wrapper-content">

       <div class="row">

		<div class="col-sm-12">
			<div class="ibox">
				<div class="ibox-content" style="border:0px;">
					<div class="mail-box-header">
						<h2>数据大屏列表</h2>
					</div>
					 <el-row>
				<el-col :span="14">
					<div style="margin-bottom:10px;" class="btn-group" role="group">
						<button type="button" class="btn btn-outline btn-default" title="新建" @click="newReport()">
							<i class="glyphicon glyphicon-plus" aria-hidden="true"></i>
						</button>
						<button type="button" class="btn btn-outline btn-default" title="改名" @click="renameReport()">
							<i class="glyphicon glyphicon-edit" aria-hidden="true"></i>
						</button>
						 <button type="button" class="btn btn-outline btn-default" title="复制" @click="copyreport()">
							<i class="glyphicon glyphicon-copy" aria-hidden="true"></i>
						</button>
						<button type="button" class="btn btn-outline btn-default" title="删除" @click="delreport()">
							<i class="glyphicon glyphicon-trash" aria-hidden="true"></i>
						</button>
						<button type="button" class="btn btn-outline btn-default" title="模版下载" @click="downTemplate()">
							模版下载
						</button>

					</div>
				</el-col>
				<el-col :span="10" align="right">
					<el-input placeholder="请输入关键字" style="width:220px;" size="mini" v-model="search" clearable class="input-with-select">
											<el-button slot="append" icon="el-icon-search" @click="loadDatas()"></el-button>
										</el-input>
					</el-col>
				</el-row>
					<el-table :data="data" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
						<el-table-column label="" width="45">
							<template slot-scope="scope">
								<el-radio v-model="checked" name="myselect" :label="scope.row.id">&nbsp;</el-radio>
							</template>
						</el-table-column>
						<el-table-column align="center" prop="pageName" label="大屏报表名称"></el-table-column>
						<el-table-column align="center" prop="userName" label="创建人"></el-table-column>
						<el-table-column align="center" prop="createdate" label="创建时间"></el-table-column>
						<el-table-column align="center" prop="updatedate" label="最后修改时间"></el-table-column>
						<el-table-column align="center" prop="tableId" label="操作" width="180">
							<template slot-scope="scope">
								<a class="btn btn-info btn-xs" @click.stop="viewReport(scope.row.id)"> 预览 </a>
								<a class="btn btn-info btn-xs" @click.stop="editreport(scope.row.id)"> 定制 </a>
								<a class="btn btn-info btn-xs" @click.stop="sharereport(scope.row.id)"> 分享 </a>
							</template>
						</el-table-column>
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
	<bigscreenShare ref="bigscreenShareForm"></bigscreenShare>
	<bigScreenRename ref="bigscreenRenameForm"></bigScreenRename>
	<bigscreenCopy ref="bigscreenCopyForm"></bigscreenCopy>
	<downBigscreen ref="downloadForm"/>
  </div>
</template>

<script>
	import {baseUrl, ajax} from '@/common/biConfig'
	import { MessageBox } from 'element-ui';
	import $ from 'jquery'
	import bigscreenShare from './BigscreenShare'
	import bigScreenRename from './BigScreenRename'
	import bigscreenCopy from './BigscreenCopy'
	import downBigscreen from '@/view/template/DownBigscreen'

	export default {
		name:"dashboardIndex",
	    data(){
			return {
				data:[],
				checked:null,
				total:0,
				page:1,
				rows:10,
				search: null,
			}
		},
		components: {
			bigscreenShare, bigScreenRename, bigscreenCopy, downBigscreen
    	},
		mounted(){

		},
		computed: {
		},
		methods: {
			handleSizeChange(v){
				this.rows = v;
				this.loadDatas();
			},
			handleCurrentChange(v){
				this.page = v;
				this.loadDatas();
			},
			selectme(a, b){
				this.checked = a.id;
			},
			loadDatas(){
				ajax({
					url:"bigscreen/list.action",
					data:JSON.stringify({page:this.page, rows:this.rows, search: this.search}),
					postJSON:true,
					type:"POST",
					success:(r)=>{
						this.total = r.total;
						this.data = r.rows;
					}
				}, this);
			},
			newReport(){
				//更新url
				this.$parent.$refs['navMenuForm'].chgMenuUrl('/bigscreen/BigscreenList','/bigscreen/Bigscreen');
				this.$router.push({name:"bigscreen"});
			},
			viewReport(id){
				let routeData = this.$router.resolve({
			         name: 'bigscreenView',
			         query: { id: id }
			       });
			    window.open(routeData.href, '_blank');
			},
			editreport(id){
				//更新url
				this.$parent.$refs['navMenuForm'].chgMenuUrl('/bigscreen/BigscreenList','/bigscreen/Bigscreen');
				this.$router.push({name:"bigscreen", params:{id: id}});
			},
			sharereport(id){
				this.$refs['bigscreenShareForm'].share(id);
			},
			delreport(){
				if(!this.checked){
					this.$notify.error("未勾选数据.");
					return;
				}
				if(confirm('是否确认删除？')){
					ajax({
						url:"bigscreen/delete.action",
						data:{id: this.checked},
						type:"get",
						success:(resp)=>{
							this.$notify.success("删除成功！");
							this.checked = null;
							this.loadDatas();
						}
					}, this);
				}
			},
			renameReport(){
				 if(!this.checked){
					this.$notify.error("未勾选数据.");
					return;
				}
				let r = this.data.filter(m=>m.id == this.checked)[0];
				this.$refs['bigscreenRenameForm'].showDailog(r);
			},
			copyreport(){
				if(!this.checked){
					this.$notify.error("未勾选数据.");
					return;
				}
				let r = this.data.filter(m=>m.id == this.checked)[0];
				this.$refs['bigscreenCopyForm'].showDailog(r);
			},
			downTemplate(){
				this.$refs['downloadForm'].openDailog();
			},
		},
		watch: {
		},
		beforeRouteEnter: function(to, from, next) {
			next((vm)=>vm.loadDatas());
		},
	}
</script>

<style lang="less" scoped>
</style>
