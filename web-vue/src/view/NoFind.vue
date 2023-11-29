<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="fillcontain">
		<h3>404 页面不存在</h3>
		<button class="btn btn-sm btn-primary" @click="goback()">返回首页</button>

  	</div>
</template>

<script>
	import {baseUrl} from '@/common/biConfig'

	export default {
	    data(){
			return {

			}
		},
		mounted(){
		},
		computed: {
		},
		methods: {
			goback(){
				this.$router.push("/Welcome");
			}
		},
		watch: {

		}
	}
</script>

<style lang="less" scoped>
	@import '../style/mixin';
</style>
