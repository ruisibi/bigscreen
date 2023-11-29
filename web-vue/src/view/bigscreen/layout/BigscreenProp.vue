<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<el-form :model="prop" ref="propForm" label-position="left" size="mini">
		<el-collapse v-model="activeName" accordion>
			<el-collapse-item title="页面属性" name="1">
				<el-form-item label="页面主题：" label-width="110px">
					<el-select v-model="prop.styleType" :clearable="true" @change="changevalue('styleType')" placeholder="请选择" style="width:100%;">
						<el-option
						v-for="item in opt.styleTypes"
						:key="item.value"
						:label="item.name"
						:value="item.value">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="背景色：" label-width="210px">
					<el-color-picker v-model="prop.pageBgColor" @change="changevalue('pageBgColor')"></el-color-picker>
				</el-form-item>
				<el-form-item label="背景图片：" label-position="top" label-width="110px">
					<resourceSelect ref="resourceSelectForm" column="pageBgImg" :cb="changevalue" :prop="prop"></resourceSelect>
				</el-form-item>

				<el-form-item label="页面宽度：" label-width="110px" >
					<el-input-number v-model="prop.width" controls-position="right" @change="changevalue('width')" :precision="0" :min="1" size="small"></el-input-number>
				</el-form-item>
				<el-form-item label="页面高度：" label-width="110px" >
					<el-input-number v-model="prop.height" controls-position="right" @change="changevalue('height')" :precision="0" :min="1" size="small"></el-input-number>
				</el-form-item>
				<!--
				<el-form-item label="自动刷新：" label-width="110px">
					<el-select v-model="prop.autoflash" :clearable="true" @change="changevalue('autoflash')" placeholder="请选择" style="width:100%;">
						<el-option
						v-for="item in opt.autoflashs"
						:key="item.value"
						:label="item.name"
						:value="item.value">
						</el-option>
					</el-select>
				</el-form-item>
				-->
			</el-collapse-item>
		</el-collapse>
  </el-form>
</template>

<script>
	import {ajax, newGuid} from '@/common/biConfig'
	import resourceSelect from '@/components/toolbox/ResourceSelect'
	import $ from 'jquery'
	import {baseInfo, writeOperLogs} from './../bsUtils'

	export default {
	    data(){
			return {
				prop:{
					pageBgColor:null,
					pageBgImg:null,
					height:null,
					width:null,
					autoflash:null,
					styleType:null,
				},
				activeName:"1",
				opt:{
					autoflashs:[{name:"间隔1分钟", value:"1"},{name:"间隔5分钟", value:"5"},{name:"间隔10分钟", value:"10"},{name:"间隔20分钟", value:"20"}],
					styleTypes:[{name:"浅色", value:"def"}, {name:"深色", value:"bigscreen"}]
				}
			}
		},
		props:{
			pageInfo:{
				type:Object,
				required:true,
				default:{}
			},
			userOpers: {
				type:Array,
				required: false,
				default: null
			},
		},
		components:{
			resourceSelect
		},
		mounted(){

		},
		computed: {
		},
		methods: {
			setUpdate(){
				this.$parent.$parent.setUpdate();
			},
			//刷新组件
			renderComp(){
				this.$parent.$parent.$refs['optarea'].$forceUpdate();
			},
			init(){
				let s = this.pageInfo.style;
				this.prop.pageBgColor = s.pageBgColor;
				this.prop.pageBgImg = s.pageBgImg;
				this.prop.height = s.height;
				this.prop.width = s.width;
				this.prop.autoflash = s.autoflash;
				this.prop.styleType = s.styleType || "bigscreen";
			},
			changevalue(prop){
				 let col = prop;
				 let val = this.prop[prop];
				 writeOperLogs(this.userOpers, {comp:this.comp, oper:"pagepropset", options:{col:col, val:this.pageInfo.style[col], ts: this}});
				 this.pageInfo.style[col] = val;
				 this.setUpdate();
				 this.renderComp();
			},
			 //撤销操作
			goback(options){
				let s = this.pageInfo.style;
				s[options.col] = options.val;
				this.prop[options.col] = options.val;
				this.renderComp();
			}
		},
		watch: {
		}
	}
</script>
