<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
	<el-dialog title="分析图链接" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
		<template slot="title">
			<span class="el-dialog__title">分析图链接</span>
			<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
			<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
			</button>
		 </template>
		<p class="text-warning">说明：定义完成链接后，在组件上双击实现链接。</p>
		<el-form :model="form" ref="linkForm" label-width="120px" size="mini" :rules="rule">
			 <el-form-item label="链接名称：" prop="linkName">
              	<el-input v-model="form.linkName"></el-input>
            </el-form-item>
			<el-form-item label="链接到：" prop="targetType">
              <el-select v-model="form.targetType" placeholder="请选择" style="width:100%">
				<el-option
					v-for="item in opts.types"
					:key="item.value"
					:label="item.label"
					:value="item.value">
				</el-option>
			  </el-select>
            </el-form-item>
			<el-form-item label="选择报表：" prop="reportls" v-if="form.targetType == 'report'">
              <el-select v-model="form.report" placeholder="请选择" style="width:100%">
				<el-option
					v-for="item in opts.reportls"
					:key="item.value"
					:label="item.label"
					:value="item.value">
				</el-option>
			  </el-select>
            </el-form-item>
			<el-form-item label="选择仪表盘：" prop="dashboard" v-if="form.targetType == 'dashboard'">
              <el-select v-model="form.dashboard" placeholder="请选择" style="width:100%">
				<el-option
					v-for="item in opts.dashboardls"
					:key="item.value"
					:label="item.label"
					:value="item.value">
				</el-option>
			  </el-select>
            </el-form-item>
			<el-form-item label="选择大屏：" prop="bigscreen" v-if="form.targetType == 'bigscreen'">
              <el-select v-model="form.bigscreen" placeholder="请选择" style="width:100%">
				<el-option
					v-for="item in opts.bigscreenls"
					:key="item.value"
					:label="item.label"
					:value="item.value">
				</el-option>
			  </el-select>
            </el-form-item>
			<el-form-item label="链接方式：" prop="linkType">
				   <el-radio-group v-model="form.linkType" size="mini">
						<el-radio label="self" border>当前页面</el-radio>
						<el-radio label="blank" border>新页面</el-radio>
					</el-radio-group>
            </el-form-item>
			<el-form-item label="">
				<button type="button" @click="clearlink()" class="btn btn-xs btn-outline btn-danger">清除链接</button>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click="save()" type="primary">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>
	</el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {baseUrl, ajax, newGuid} from '@/common/biConfig'
	import $ from 'jquery'

	export default {
	    data(){
			return {
				show:false,
				comp:null,
				form:{
					linkName:null,
					linkType:"self",
					targetType: null,
					report:null,
					bigscreen:null,
					dashboard:null,
				},
				rule:{
					 linkName: [{ required: true, message: "必填", trigger: "blur" }],
					 targetType: [{ required: true, message: "必填", trigger: "blur" }],
				},
				opts:{
					types:[{value:"report", label:"报表"}, {value:"dashboard", label:"仪表盘"}, {value:"bigscreen", label:"大屏"}],
					reportls:[],
					dashboardls:[],
					bigscreenls:[],
				}
			}
		},
		components: {

    	},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			showDailog(comp){
				this.show = true;
				this.comp = comp;
				if(comp.link){
					this.form.linkName = comp.link.linkName;
					this.form.linkType = comp.link.linkType;
					this.form.targetType = comp.link.targetType;
					this.form.report = comp.link.report;
					this.form.bigscreen  = comp.link.bigscreen;
					this.form.dashboard = comp.link.dashboard;
				}else{
					this.form.linkName = null;
					this.form.linkType = "self";
					this.form.targetType = null;
					this.form.report = null;
					this.form.bigscreen  = null;
					this.form.dashboard = null;
				}
				this.listReports();
				this.listDashboard();
				this.listBigscreen();
				if(this.$refs['linkForm']){
					this.$refs['linkForm'].clearValidate();
				}
			},
			listReports(){
				ajax({
					url:"portal/listAllReports.action",
					data:{},
					type:"POST",
					success:(resp)=>{
						this.opts.reportls = resp.rows.map(m=>{
							return {label:m.pageName, value:m.pageId};
						});
					}
				});
			},
			listDashboard(){
				ajax({
					url:"dashboard/listAll.action",
					data:{},
					type:"POST",
					success:(resp)=>{
						this.opts.dashboardls = resp.rows.map(m=>{
							return {label:m.pageName, value:m.id};
						});
					}
				});
			},
			listBigscreen(){
				ajax({
					url:"bigscreen/list.action",
					data:JSON.stringify({page:1, rows:1000}),
					type:"POST",
					postJSON:true,
					success:(resp)=>{
						this.opts.bigscreenls = resp.rows.map(m=>{
							return {label:m.pageName, value:m.id};
						});
					}
				});
			},
			clearlink(){
				let comp = this.comp;
				delete comp.link;
				this.$parent.setUpdate();
				this.show = false;
				this.$parent.$forceUpdate();
			},
			save(){
				this.$refs['linkForm'].validate((valid) => {
					if(valid){
						var linkName = this.form.linkName;
						var targetType = this.form.targetType;
						var linkType = this.form.linkType;
						let comp = this.comp;
						if(targetType == 'report' && !this.form.report){
							this.$notify.error("未选择报表.");
							return;
						}
						if(targetType == 'dashboard' && !this.form.dashboard){
							this.$notify.error("未选择仪表盘.");
							return;
						}
						if(targetType == 'bigscreen' && !this.form.bigscreen){
							this.$notify.error("未选择大屏.");
							return;
						}
						comp.link = {
							linkName : this.form.linkName,
							linkType : this.form.linkType,
							targetType : this.form.targetType,
							report : this.form.report,
							bigscreen :this.form.bigscreen,
							dashboard: this.form.dashboard,
							id: Math.round(Math.random() * 10000)
						};
						this.$parent.setUpdate();
						this.show = false;
						this.$parent.$forceUpdate();
					}
				});
			}

		},
		watch: {

		}
	}
</script>
