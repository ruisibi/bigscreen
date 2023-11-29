<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 大屏 view, 带框架 -->
<template>
	<div class="bslayout" :style="bsstyle">
		<layout-view ref="optarea" useIn="view" :pageInfo="pageInfo"></layout-view>
	</div>
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
			}
		},
		components: {
			LayoutView
    	},
		mounted(){
			let id =  this.$route.query.id;
			if(id){
				this.init(id);
			}
		},
		computed: {
		},
		methods: {
			//初始化页面
			init(id){
				ajax({
					url:"bigscreen/getJson.action",
					data:{id:id},
					type:"get",
					success:(resp)=>{
						let p = JSON.parse(resp.rows);
						if(!p.style.pageBgColor){
							this.bsstyle['background-color'] =  p.style.styleType==='bigscreen' ? bsUtils.pageBgColor:bsUtils.pageWhiteBgColor;
						}else{
							this.bsstyle['background-color'] = p.style.pageBgColor;
						}
						this.pageInfo = bsUtils.initTabHideEvent(p);
						window.document.title = "大屏浏览";
					}
				}, this);
			},
			//组件链接
			linkurl(link, url, param){
				this.$router.push({path:url, query:param});
				//if(link.linkType==='blank'){
					//添加菜单
					//this.$parent.$refs['navMenuForm'].menuAdd({menuId:link.id, menuName:link.linkName, menuUrl:url});
				//}
			},
		},
		watch: {
		},
		beforeRouteLeave: function(to, from, next) {
			this.$destroy();
			next();
		},
	}
</script>

<style lang="less" scoped>
	.bslayout{
		width: 100%;
		height: 100%;
	}
</style>
