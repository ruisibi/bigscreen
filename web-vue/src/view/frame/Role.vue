<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="wrapper-content">
		  <div class="ibox">
			  <div class="ibox-title">
				  角色管理
			  </div>
			  <div class="ibox-content">
					<div class="btn-group optbtncls" role="group">
						<button type="button" class="btn btn-outline btn-default" title="新增" @click="addRole(false)">
							<i class="glyphicon glyphicon-plus" aria-hidden="true"></i>
						</button>
						<button type="button" class="btn btn-outline btn-default" title="修改" @click="addRole(true)">
							<i class="glyphicon glyphicon-edit" aria-hidden="true"></i>
						</button>
						<button type="button" class="btn btn-outline btn-default" title="删除" @click="delRole()">
							<i class="glyphicon glyphicon-trash" aria-hidden="true"></i>
						</button>
					</div>
					<el-table :data="tableData" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
						<!--
						<el-table-column type="selection" width="45"></el-table-column>
						-->
						<el-table-column label="" width="45">
							<template slot-scope="scope">
								<el-radio v-model="checked" name="myselect2" :label="scope.row.roleId">&nbsp;</el-radio>
							</template>
						</el-table-column>
						<el-table-column align="center" prop="roleId" label="标识"></el-table-column>
						<el-table-column align="center" prop="roleName" label="角色名称"></el-table-column>
						<el-table-column align="center" prop="roleDesc" label="备注信息"></el-table-column>
						<el-table-column align="center" prop="createUser" label="创建人"></el-table-column>
						<el-table-column align="center" prop="createDate" label="创建时间"></el-table-column>
						<el-table-column align="center" prop="ord" label="排序"></el-table-column>
						<el-table-column align="center" prop="roleId" label="操作" width="100px">
							<template slot-scope="scope">
								<a class="btn btn-primary btn-xs" @click.stop="userRole(scope.row.roleId)"> 授权菜单 </a>

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

		  <el-dialog :title="dialogTitle" :visible.sync="addRoleDailog">
			  	<el-form :model="role" :rules="rules" ref="roleForm" size="small">

				    <el-form-item label="角色名称" label-width="100px" prop="roleName">
				     	<el-input v-model="role.roleName" ></el-input>
				    </el-form-item>

					<el-form-item label="角色备注" label-width="100px" prop="roleDesc">
				     	<el-input v-model="role.roleDesc"  ></el-input>
				    </el-form-item>
					<el-form-item label="排序" label-width="100px">
						<el-input-number v-model="role.ord" :min="0" :max="100"></el-input-number>
					</el-form-item>

			  	</el-form>
			  <div slot="footer" class="dialog-footer">
			    <el-button type="primary" @click="saveRole(isupdate)">确 定</el-button>
				<el-button @click="addRoleDailog = false">取 消</el-button>
			  </div>
		</el-dialog>
  	</div>
</template>

<script>
	import {baseUrl, ajax} from '@/common/biConfig'
	import $ from 'jquery'

	export default {
	    data(){
			return {
				tableData:[],
				checked:null,
				addRoleDailog:false,
				dialogTitle:"",
				total:0,
				page:1,
				rows:10,
				role:{
					roleId:null,
					roleName:"",
					roleDesc:"",
					ord:1
				},
				rules:{
					roleName:[
						{ required: true, message: '必填', trigger: 'blur' }
					]
				},
				isupdate: false   //是否修改数据
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
					type:"POST",
					data:JSON.stringify({page:ts.page, rows:ts.rows}),
					postJSON:true,
					url:"auth/role/list.action",
					success:function(resp){
						ts.tableData = resp.rows;
						ts.total = resp.total;
					}
				}, ts);
			},
			selectme:function(a, b){
				this.checked = a.roleId;
			},
			addRole:function(isupdate){
				let ts = this;
				if(isupdate){
					this.dialogTitle = "修改角色";
					//回写值
					ajax({
						url:"auth/role/get.action",
						data:{roleId:ts.checked},
						dataType:"json",
						success:function(dt){
							dt = dt.rows;
							for(let v in ts.role){
								ts.role[v] = dt[v];
							}
						}
					}, ts);
				}else{
					this.dialogTitle = "新增角色";
					//清空值
					for(let v in this.role){
						this.role[v] = null;
					}
				}
				this.isupdate = isupdate;
				this.addRoleDailog = true;
			},
			saveRole:function(update){
				let ts = this;
				this.$refs['roleForm'].validate((valid) => {
					if (valid) {
						ajax({
							type:"POST",
							data: ts.role,
							postJSON:false,
							url:update?"auth/role/update.action":"auth/role/save.action",
							success:function(resp){
								ts.loadDatas();
								ts.addRoleDailog = false;
							}
						}, ts);
					}
				});
			},
			delRole:function(){
				let ts = this;
				if(!this.checked){
					ts.$notify.error({title: '请勾选数据',offset: 50});
					return;
				}
				if(confirm("是否确认删除？")){
					ajax({
						type:"GET",
						data: {roleId: ts.checked},
						postJSON:false,
						url:"auth/role/delete.action",
						success:function(resp){
							ts.$notify.success({title: '用户删除成功',offset: 50});
							ts.loadDatas();
						}
					}, this);
				}
			},
			userRole:function(roleId) {
				this.$router.push({name:"roleMenu", query: {roleId}});
			},
			handleSizeChange(v){
				this.rows = v;
				this.loadDatas();
			},
			handleCurrentChange(v){
				this.page = v;
				this.loadDatas();
			},
		},
		watch: {
		}
	}
</script>

<style lang="less" scoped>
	@import '../../style/mixin';
</style>
