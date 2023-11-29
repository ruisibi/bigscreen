<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="wrapper-content">
		  <div class="ibox">
			  <div class="ibox-title">
				  在线用户列表
			  </div>
			  <div class="ibox-content">
					<div class="btn-group optbtncls" role="group">
						<button type="button" class="btn btn-outline btn-default" @click="loadDatas()" title="刷新">
							<i class="glyphicon glyphicon-refresh" aria-hidden="true"></i>
						</button>
					</div>
					<el-table :data="tableData" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
						<!--
						<el-table-column type="selection" width="45"></el-table-column>

						<el-table-column label="" width="45">
							<template slot-scope="scope">
								<el-radio v-model="checked" name="myselect2" :label="scope.row.userId">&nbsp;</el-radio>
							</template>
						</el-table-column>
						-->
						<el-table-column align="center" prop="userId" label="标识"></el-table-column>
						<el-table-column align="center" prop="staffId" label="账号"></el-table-column>
						<el-table-column align="center" prop="loginName" label="用户名"></el-table-column>
						<el-table-column align="center" prop="loginDate" label="登录时间"></el-table-column>
						<el-table-column align="center" prop="logIp" label="登录IP"></el-table-column>
					</el-table>

			  </div>

		  </div>


  	</div>
</template>

<script>
	import {baseUrl, ajax} from '@/common/biConfig'
	import $ from 'jquery'



	export default {
		components: {

		},
	    data(){
			return {
				tableData:[],
				checked:null,
				editor: null,
			}
		},
		mounted(){
			this.loadDatas();
		},
		computed: {
		},
		methods: {
			loadDatas:function(){
				let ts = this;
				ajax({
					type:"GET",
					data:{},
					postJSON:true,
					url:"frame/OnlineUser/list.action",
					success:function(resp){
						ts.tableData = resp.rows;
					}
				}, ts);
			},
			selectme:function(a, b){
				this.checked = a.userId;
			},
		},
		watch: {
		},
		beforeRouteLeave(to, from, next) {
			this.$destroy();
			next();
		}
	}
</script>
