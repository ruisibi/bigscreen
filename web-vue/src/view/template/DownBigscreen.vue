<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 表格对话框都放这里面 -->
<template>
  	<el-dialog title="大屏模版下载" :visible.sync="show" :close-on-click-modal="false" width="50%" custom-class="nopadding">
		 <template slot="title">
			<span class="el-dialog__title">大屏模版下载</span>
			<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
			<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
			</button>
		 </template>
		 <div class="text-warning">请注意：只能下载模版的样式和资源，不能下载数据。</div>
		 <el-table :data="data" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
			<el-table-column label="" width="45">
				<template slot-scope="scope">
					<el-radio v-model="checked" name="myselect" :label="scope.row.id">&nbsp;</el-radio>
				</template>
			</el-table-column>
			<el-table-column align="center" prop="pageName" label="模版名称"></el-table-column>
			<el-table-column align="center" prop="createdate" width="150" label="创建时间"></el-table-column>
			<el-table-column align="center" prop="updatedate" width="150" label="最后修改时间"></el-table-column>
			<el-table-column align="center" prop="tableId" label="操作" width="80">
				<template slot-scope="scope">
					<a class="btn btn-info btn-xs" @click.stop="viewBs(scope.row.id)"> 预览 </a>
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

		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, baseUrl} from '@/common/biConfig'
	import $ from 'jquery'
	import { Loading } from 'element-ui'

	export default {
	    data(){
			return {
				show:false,
				data:[],
				checked:null,
				total:0,
				page:1,
				rows:10,
				templateUrl: null,  //模版URL
			}
		},
		mounted(){
			this.getTemplateUrl();
		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			openDailog(){
				this.show = true;
				this.checked = null;
				this.loadDatas();
			},

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
					url:"template/queryTemplates.action",
					data:{page:this.page, rows:this.rows},
					type:"GET",
					success:(r)=>{
						this.total = r.total;
						this.data = r.rows;
					}
				}, this);
			},
			getTemplateUrl(){
				ajax({
					url:"template/getTemplateUrl.action",
					data:{},
					type:"GET",
					success:(r)=>{
						this.templateUrl = r.rows;
					}
				}, this);
			},
			download(id){
				let loadingInstance = Loading.service({fullscreen:true,text:"模版下载中...", spinner:"el-icon-loading" });

				ajax({
					url:"template/download.action",
					data:{id: id},
					type:"GET",
					success:(r)=>{
						loadingInstance.close();
						this.show = false;
						this.$parent.loadDatas();
					}
				}, this, loadingInstance);
			},
			viewBs(id){
				let url = this.templateUrl+"#/bigscreen/View?id=" + id;
				window.open(url);
			},
			save(){
				if(this.checked){
					this.download(this.checked);
				}else{
					this.$notify.error("未勾选资源");
				}
			},
		},
		watch: {
		}
	}
</script>
