<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
<el-drawer title="大屏预览"  :visible.sync="drawer" size="100%" :destroy-on-close="true">
	<div class="bslayout" :style="bsstyle">
		<layout-view ref="optarea" useIn="preview" :pageInfo="pageInfo"></layout-view>
	</div>
</el-drawer>
</template>

<script>
	import {baseUrl, ajax} from '@/common/biConfig'
	import { MessageBox } from 'element-ui';
	import $ from 'jquery'
	import LayoutView from './layout/LayoutView'
	import * as bsUtils from './bsUtils'

	export default {
	    data(){
			return {
				pageInfo:{},
				bsstyle:{},  //大屏样式
				drawer:false,
			}
		},
		props: {

		},
		components: {
			LayoutView
    	},
		mounted(){

		},
		computed: {
		},
		methods: {
			//初始化页面
			showPreview(pageInfo){
				this.pageInfo = bsUtils.initTabHideEvent(pageInfo);
				this.drawer = true;
			},
		},
		watch: {
		},
	}
</script>

<style lang="less" scoped>
	.bslayout{
		width: 100%;
		height: 100%;
	}
</style>
