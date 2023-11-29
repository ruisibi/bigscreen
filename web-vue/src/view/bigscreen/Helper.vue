<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 表格对话框都放这里面 -->
<template>
  	<el-dialog title="操作说明" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">

		  <div style="padding:15px;min-height:200px; height:auto;">
			  1.拖拽组件到操作区域，快速创建可视化对象;<br>
			  2.在组件上点击右键可以配置组件数据，属性等内容;<br>
			  3.按 ctrl + s 保存页面;<br>
			  4.选中组件后，按shift + 上下左右键，可以移动组件位置;<br>
			  5.按shift后在选中组件，可以实现组件多选，多选后移动某个组件后，其他组件也一起移动;<br>
			  6.在选中组件后，按 ctrl + v 实现组件复制;<br>
			  7.按 ctrl + z 撤销操作;<br>
		  </div>

		<div slot="footer" class="dialog-footer">
			<el-button @click="show = false">关 闭</el-button>
		</div>
  </el-dialog>
</template>

<script>
	import {ajax, baseUrl} from '@/common/biConfig'
	import $ from 'jquery'

	export default {
	    data(){
			return {
				show:false,
			}
		},
		mounted(){

		},
		computed: {
		},
		methods: {
			openDailog(){
				this.show = true;
			},
		},
		watch: {
		}
	}
</script>
