<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="wrapper-content">
		  <div class="ibox">
			  <div class="ibox-title">
				  用户管理
			  </div>
			  <div class="ibox-content">
				<div class="btn-group optbtncls" role="group">
					<button type="button" class="btn btn-outline btn-default" title="新增" @click="addUser(false)">
						<i class="glyphicon glyphicon-plus" aria-hidden="true"></i>
					</button>
					<button type="button" class="btn btn-outline btn-default" title="修改" @click="addUser(true)">
						<i class="glyphicon glyphicon-edit" aria-hidden="true"></i>
					</button>
					<button type="button" class="btn btn-outline btn-default" title="删除" @click="delUser()">
						<i class="glyphicon glyphicon-trash" aria-hidden="true"></i>
					</button>
				</div>
				 <el-table :data="tableData" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
					<!--
					<el-table-column type="selection" width="45"></el-table-column>
					-->
					<el-table-column label="" width="45">
						<template slot-scope="scope">
							<el-radio v-model="checked" name="myselect" :label="scope.row.userId">&nbsp;</el-radio>
						</template>
					</el-table-column>
					<el-table-column align="center" prop="userId" label="标识"></el-table-column>
					<el-table-column align="center" prop="staffId" label="工号"></el-table-column>
					<el-table-column align="center" prop="loginName" label="用户名"></el-table-column>
					<el-table-column align="center" prop="state" :formatter="fmtstate" label="状态"></el-table-column>
					<el-table-column align="center" prop="logCnt" label="登陆次数"></el-table-column>
					<el-table-column align="center" prop="loginDate" label="登录时间"></el-table-column>
					<el-table-column align="center" prop="userId" label="操作">
						<template slot-scope="scope">
								<a class="btn btn-primary btn-xs" @click.stop="addUserRole(scope.row.userId)"> 授权角色 </a>
								<a class="btn btn-primary btn-xs" @click.stop="userMenu(scope.row.userId)"> 授权菜单 </a>
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
		  	<el-dialog :title="dialogTitle" v-if="showDailog" :visible.sync="addUserDailog" :close-on-click-modal="false">
				<el-form :model="user" :rules="rules" ref="userForm" size="small">
					<el-form-item label="用户工号" label-width="100px" prop="staffId">
				     	<el-input :readonly="isupdate" v-model="user.staffId"  placeholder="登录系统使用"></el-input>
				    </el-form-item>
					<el-form-item label="登录密码" label-width="100px" prop="password">
				     	<el-input type="password" v-model="user.password"  :show-password="true" ></el-input>
				    </el-form-item>
					<el-form-item label="重复密码" label-width="100px" prop="password2">
				     	<el-input type="password" v-model="user.password2" :show-password="true"  ></el-input>
				    </el-form-item>
					<el-form-item label="用户名称" label-width="100px" prop="loginName">
				     	<el-input v-model="user.loginName"  ></el-input>
				    </el-form-item>

				    <el-form-item label="用户性别" label-width="100px" prop="gender">
						<el-select v-model="user.gender" placeholder="请选择">
							<el-option
							v-for="item in opts.sexs"
							:key="item.value"
							:label="item.label"
							:value="item.value">
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="账号状态" label-width="100px" prop="state">
				     	<el-select v-model="user.state" placeholder="请选择">
							<el-option
							v-for="item in opts.status"
							:key="item.value"
							:label="item.label"
							:value="item.value">
							</el-option>
						</el-select>
				    </el-form-item>
					<el-form-item label="组织机构" label-width="100px">
				     	<el-input v-model="user.deptName">
							 <el-button type="info" @click="selectDept()"  slot="append" >选择</el-button>
							 <el-button type="info" @click="clearDept()"  slot="append" >清除</el-button>
						</el-input>
				    </el-form-item>
					<el-form-item label="手机号码" label-width="100px">
				     	<el-input v-model="user.mobilePhone"  ></el-input>
				    </el-form-item>
					<el-form-item label="办公电话" label-width="100px">
				     	<el-input v-model="user.officeTel"  ></el-input>
				    </el-form-item>
					<el-form-item label="电子邮件" label-width="100px">
				     	<el-input v-model="user.email"  ></el-input>
				    </el-form-item>
			  	</el-form>
				  <el-dialog title="选择组织机构" :visible.sync="showdept" append-to-body  width="30%">
					<div class="el-dialog-div">
						<div id="selectdeptdiv"></div>
					</div>
					<div slot="footer" class="dialog-footer">
						<el-button type="primary" @click="selectDeptSave()">确 定</el-button>
						<el-button @click="showdept = false">取 消</el-button>
					</div>
				</el-dialog>

			  <div slot="footer" class="dialog-footer">
			    <el-button type="primary" @click="saveUser()">确 定</el-button>
				<el-button @click="addUserDailog = false;showDailog = false;">取 消</el-button>
			  </div>
			</el-dialog>

			<el-dialog title="授权角色" :visible.sync="userRoleDailog">
				  <el-checkbox-group v-model="checkList">
					<el-checkbox v-for="r in roles" :label="r.roleId" :key="r.roleId">{{r.roleName}}</el-checkbox>
				  </el-checkbox-group>
			  <div slot="footer" class="dialog-footer">
			    <el-button type="primary" @click="saveUserRole()">确 定</el-button>
				<el-button @click="userRoleDailog = false;">取 消</el-button>
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
				tableData: [],
				addUserDailog:false,
				showDailog:false,
				dialogTitle:"",
				checked:null,
				isupdate: false,
				total:0,
				page:1,
				rows:10,
				showdept:false,
				user:{
					userId:null,
					staffId:null,
					password:'',
					password2:'',
					loginName:'',
					gender:null,
					state:null,
					mobilePhone:'',
					officeTel:'',
					email:'',
					deptId:null,
					deptName:''
				},
				rules:{
					staffId:[
						{ required: true, message: '必填', trigger: 'blur' }
					],
					loginName:[
						{ required: true, message: '必填', trigger: 'blur' }
					],
					gender:[
						{ required: true, message: '必填', trigger: 'blur' }
					],
					state:[
						{ required: true, message: '必填', trigger: 'blur' }
					]
				},
				opts:{
					sexs:[{
						label:"男",
						value:"男"
					},{
						label:"女",
						value:"女"
					}],
					status:[{
						label:"启用",
						value:1
					},{
						label:"停用",
						value:0
					}]
				},
				userRoleDailog:false,
				roles:[],
				checkList:[]
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
					url:"auth/userList/list.action",
					success:function(resp){
						ts.tableData = resp.rows;
						ts.total = resp.total;
					}
				}, ts);
			},
			handleSizeChange(v){
				this.rows = v;
				this.loadDatas();
			},
			handleCurrentChange(v){
				this.page = v;
				this.loadDatas();
			},
			fmtstate:function(row, column, cellValue, index){
				return cellValue === 1 ?"启用":"停用";
			},
			addUser : function(isupdate){
				let ts = this;
				if(isupdate){
					if(!ts.checked){
						ts.$notify.error({
										title: '未勾选数据',
										offset: 50
									});
						return false;
					}

					ts.rules.password = [
						{ min: 6, max: 18, message: '密码长度6到18位', trigger: 'blur' }
					];
					ts.rules.password2 = [
						{ min: 6, max: 18, message: '密码长度6到18位', trigger: 'blur' }
					];

					this.showDailog = true;
					this.addUserDailog = true;
					this.dialogTitle = "修改用户";
					//回写值
					ajax({
						url:"auth/userList/get.action",
						data:{userId:ts.checked},
						dataType:"json",
						success:function(dt){
							dt = dt.rows;
							for(let v in ts.user){
								ts.user[v] = dt[v] == null ? "": dt[v];
							}
							ts.user.userId = dt.userId;
							ts.user.password = null;
							ts.user.password2 = null;
						}
					}, ts);
				}else{
					ts.rules.password = [
						{ required: true, message: '必填', trigger: 'blur' },
						{ min: 6, max: 18, message: '密码长度6到18位', trigger: 'blur' }
					];
					ts.rules.password2 = [
						{ required: true, message: '必填', trigger: 'blur' },
						{ min: 6, max: 18, message: '密码长度6到18位', trigger: 'blur' }
					];

					this.showDailog = true;
					this.addUserDailog = true;
					this.dialogTitle = "新增用户";
					//清空值
					for(let v in this.user){
						this.user[v] = null;
					}
					if(this.$refs['userForm']){
						this.$refs['userForm'].clearValidate();
					}
				}
				this.isupdate = isupdate;
			},
			selectme:function(a, b){
				this.checked = a.userId;
			},
			saveUser:function(){
				let ts = this;
				this.$refs['userForm'].validate((valid) => {
					if (valid) {
						if(ts.user.password != ts.user.password2){
							ts.$notify.error({
										title: '两次密码不一致',
										offset: 50
									});
							return false;
						}
						ajax({
							type:"POST",
							data: ts.user,
							postJSON:false,
							url:ts.isupdate?"auth/userList/update.action":"auth/userList/save.action",
							success:function(resp){
								ts.$notify.success({
										title: ts.isupdate?"用户修改成功":'用户创建成功',
										offset: 50
									});
								ts.addUserDailog = false;
								ts.showDailog = false;
								ts.loadDatas();
							}
						}, ts);
					} else {
						//console.log('error submit!!');
						return false;
					}
				});
			},
			delUser:function(){
				let ts = this;
				if(!this.checked){
					ts.$notify.error({
						title: '请勾选数据',
						offset: 50
					});
					return;
				}
				if(confirm("是否确认删除？")){
					ajax({
						type:"GET",
						data: {userId: ts.checked},
						postJSON:false,
						url:"auth/userList/delete.action",
						success:function(resp){
							ts.$notify.success({
									title: '用户删除成功',
									offset: 50
								});
							ts.loadDatas();
						}
					}, this);
				}
			},
			userMenu:function(userId) {
				this.$router.push({name:"userMenu", query: {userId}});
			},
			addUserRole:function(userId) {
				this.userRoleDailog = true;
				let ts = this;
				ajax({
					type:"GET",
					data:{userId:userId},
					postJSON:false,
					url:"auth/role/userRolelist.action",
					success:function(resp){
						ts.roles = resp.rows;
						ts.checkList = resp.rows.filter(r=>r.userId).map(r=>{
							return r.roleId
						});
					}
				}, ts);
				this.curUserId = userId;
			},
			saveUserRole:function(){
				let ts = this;
				ajax({
					type:"POST",
					url:"auth/role/userRoleSave.action",
					dataType:"JSON",
					postJSON:true,
					data:JSON.stringify({roleId:ts.checkList,userId:ts.curUserId}),
					success:function(resp){
						 ts.$notify.success({
							title: '授权成功',
							offset: 50
						});
						ts.userRoleDailog = false;
					}
				}, ts);
			},
			selectDept:function(){
				this.showdept = true;
				this.$nextTick(()=>{
					var ref = $('#selectdeptdiv').jstree(true);
					if(ref){
						ref.destroy();
					}
					$("#selectdeptdiv").jstree({
						core:{
							check_callback:true,
							data:function (obj, callback) {
								$.getJSON('frame/loadDepartment.action', {id:(obj.id==='#'?'0':obj.id)}, function(resp){
									callback.call(this, resp.rows);
								});
							}
						},
						"plugins" : [
							"wholerow"
						]
					});
				});
			},
			selectDeptSave(){
				var ref = $('#selectdeptdiv').jstree(true);
				var o = ref.get_selected(true);
				if(o.length === 0){
					this.$notify.error({
									title: '还未选择组织机构',
									offset: 50
								});
					return;
				}
				o = o[0];
				this.user.deptId = o.id;
				this.user.deptName = o.text;
				this.showdept = false;
			},
			clearDept:function(){
				this.user.deptId = null;
				this.user.deptName = '';
			}
		},
		watch: {
		}
	}
</script>

<style lang="less" scoped>
	@import '../../style/mixin';
	@import '../../style/common';
</style>
