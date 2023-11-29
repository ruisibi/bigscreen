<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<el-dialog title="创建维度分组" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		   <template slot="title">
				<span class="el-dialog__title">创建维度分组</span>
				<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
				<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
				</button>
			</template>
		  <el-form :model="dim" :rules="rule" ref="dimForm" size="mini" label-position="left">
			<el-form-item label="分组名称" label-width="110px" prop="groupname">
				<el-input v-model="dim.groupname"></el-input>
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
					groupname:null
				},
				rule:{
					groupname:[{ required: true, message: "必填", trigger: "blur" }],
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
						var name = this.dim.groupname;
						var dt = {id:cid,text:name, "icon":"fa fa-tasks", li_attr:{tp:'group',dispName:name,drag:true}};
						var ref = $("#cuberighttree").jstree(true);
						ref.create_node('cubewd',dt);
						if(ref.is_closed('cubewd')){
							ref.open_node('cubewd');
						}
						this.show = false;
					}
				});
			},
			showDailog(){
				this.dim.groupname = null;
				this.show = true;
			}
		},
		watch: {
		}
	}
</script>
