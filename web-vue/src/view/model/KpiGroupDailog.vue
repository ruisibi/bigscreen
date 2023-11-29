<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<el-dialog title="创建度量分类" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		   <template slot="title">
				<span class="el-dialog__title">创建度量分类</span>
				<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
				<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
				</button>
			</template>
		  <el-form :model="dim" :rules="rule" ref="dimForm" size="mini" label-position="left">
			<el-form-item label="分类名称" label-width="110px" prop="kpigroup">
				<el-input v-model="dim.kpigroup"></el-input>
			</el-form-item>
		  </el-form>

		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, newGuid} from '@/common/biConfig'
	import $ from 'jquery'

	export default {
	    data(){
			return {
				show:false,
				dim:{
					kpigroup:null
				},
				rule:{
					kpigroup:[{ required: true, message: "必填", trigger: "blur" }],
				}
			}
		},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			save(){
				this.$refs["dimForm"].validate( (valid) => {
					if(valid){
						var cid = newGuid();
						let name = this.dim.kpigroup;
						var dt = {id:cid,text:name, icon:"glyphicon glyphicon-folder-open", li_attr:{tp:'kpigroup',dispName:name,drag:true}};
						var rightRef = $("#cuberighttree").jstree(true);
						rightRef.create_node('cubedl', dt);
						if(rightRef.is_closed('cubedl')){
							rightRef.open_node("cubedl");
						}
						this.show = false;
					}
				});
			},
			showDailog(){
				this.dim.kpigroup = null;
				this.show = true;
			}
		},
		watch: {
		}
	}
</script>
