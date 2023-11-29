<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 大屏 view, 带框架 -->
<template>
	<div class="bslayout" :style="bsstyle">
		<layout-view v-if="notFind==false" ref="optarea" useIn="pushView" :pageInfo="pageInfo"></layout-view>
		<el-alert
			v-if="notFind == true"
			title="404 NotFind"
			type="error"
			:closable="false"
			:description="'出错了，ID为 ' + id + ' 的大屏报表不存在。'"
			show-icon>
		</el-alert>
	</div>
</template>

<script>
	import {baseUrl, ajax} from '@/common/biConfig'
	import { MessageBox } from 'element-ui';
	import $ from 'jquery'
	import LayoutView from './layout/LayoutView'
	import * as bsUtils from './bsUtils'
	import * as utils from '@/view/portal/Utils'

	export default {
	    data(){
			return {
				pageInfo:{},
				bsstyle:{},  //大屏样式
				id:null, //大屏ID
				notFind: false,
			}
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
			init(id){
				ajax({
					url:"bigscreen/View-"+id+".action",
					data:{},
					type:"get",
					success:(resp)=>{
						this.notFind = false;
						let p = JSON.parse(resp.rows);
						if(!p.style.pageBgColor){
							this.bsstyle['background-color'] =  p.style.styleType==='bigscreen' ? bsUtils.pageBgColor:bsUtils.pageWhiteBgColor;
						}else{
							this.bsstyle['background-color'] = p.style.pageBgColor;
						}
						this.pageInfo = bsUtils.initTabHideEvent(p);
					}
				}, this, null, (resp)=>{
					if(resp.result === 3){ //文件没找到
						this.notFind = true;
					}
				});
			},
			//组件链接
			linkurl(link, url, param){
				this.$router.push({path:url, query:param});
				if(link.linkType==='blank'){
					//添加菜单
					this.$parent.$refs['navMenuForm'].menuAdd({menuId:link.id, menuName:link.linkName, menuUrl:url});
				}
			},
		},
		computed:{
			getBigscreenId:function(){
				return this.$route.query.bsid;
			}
		},
		watch: {
			getBigscreenId:{  //监控参数变化
				handler(val){
					this.id = val;
					if(val){
						this.init(val);
					}
				},
				immediate: true
			},
		},

		beforeRouteLeave(to, from, next){
			/**
			//通过 destroy 清楚定时器
			 */
			this.$destroy();
			next();
		},
	}
</script>

<style lang="less" scoped>
	.bslayout{
		width: 100%;
		height: calc(100% - 30px);
	}
</style>
