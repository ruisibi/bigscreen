<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 表格对话框都放这里面 -->
<template>
  	<el-dialog title="图片上传" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		 <template slot="title">
			<span class="el-dialog__title">图片上传</span>
			<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
			<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
			</button>
		 </template>
		  <el-form :model="res" :rules="rules" ref="resourceForm" size="mini">
			   <el-form-item label="上传文件" label-width="100px" prop="file">
					<el-upload
						class="upload-demo"
						ref="upload"
						:action="upurl"
						:multiple="false"
						:limit="1"
						:data="res"
						:auto-upload="false"
						 :on-change="handleChange"
						 :on-success="success"
						:file-list="fileList">
						<el-button size="small" type="primary">点击上传gif/jpg/png/svg格式图片，且不超过1MB</el-button>
					</el-upload>
				</el-form-item>
				 <el-form-item label="文件名称" label-width="100px" prop="filename">
					<el-input v-model="res.filename"></el-input>
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
	import {ajax, baseUrl} from '@/common/biConfig'
	import $ from 'jquery'

	export default {
	    data(){
			var chk = (rule, value, callback) => {
				var files = this.$refs['upload'].uploadFiles;
				if(files.length === 0){
					callback(new Error('请选择上传文件'));
				}else{
					callback();
				}

				callback();
			};
			return {
				show:false,
				res:{
					filename:null,
				},
				rules:{
					filename: [{ required: true, message: "必填", trigger: "blur" }],
					file: [{ validator: chk,  trigger: "blur" }],
				},
				fileList:[],
				upurl: baseUrl + 'bigscreen/resource/ImgUpload.action'
			}
		},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			openDailog(){
				this.show = true;
				if(this.$refs['resourceForm']){
					this.$refs['resourceForm'].clearValidate();
				}
				this.res.filename = null;
			},
			//选择文件后调用的方法
			handleChange(p){
				var name = p.name;
				var size = p.size;
				this.res.filename = name;
			},
			save(){
				 this.$refs["resourceForm"].validate(
					  (valid) => {
						  if(valid){
							  var o = this.$refs['upload'];
							  o.submit();
						  }
					  }
				 );
			},
			//上传成功后调用的方法
			success(resp){
				if(resp.result == 0) {
					this.$notify.error(resp.msg);
					this.fileList = [];
					this.$refs['upload'].uploadFiles = [];
				}else{
					this.show = false;
					this.$notify.success("文件上传成功！");
					this.$refs['upload'].uploadFiles = [];
					this.res.filename = null;
					this.$parent.loadData();
				}
			}
		},
		watch: {
		}
	}
</script>
