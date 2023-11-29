<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="fillcontain">
		<frame-top :productInfo="productInfo"></frame-top>
		<transition name="el-fade-in">
			<div class="navbar-static-side" v-show="isShowMenu">
				<el-menu style="min-height: 100%;" router @select="selectMenu">
					<template v-for="(item) in menus">
						<el-submenu v-if="item.children&&item.children.length>0" :key="item.menuId" :index=" 'p' + item.menuId">
							<template slot="title">
								<i :class="item.avatar"></i><span>{{item.menuName}}</span>
							</template>
							<template v-for="(sub) in item.children">
								<template v-if="sub.children&&sub.children.length>0">
									<el-submenu :key="sub.menuId" :index=" 'p' + sub.menuId" >
										<template slot="title">
											<i :class="sub.avatar"></i><span>{{sub.menuName}}</span>
										</template>
										<template v-for="(sub2) in sub.children">
											<el-menu-item :index="sub2.menuUrl" :key="sub2.menuId">
												<i :class="sub2.avatar"></i><span slot="title">{{sub2.menuName}}</span>
											</el-menu-item>
										</template>
									</el-submenu>
								</template>
								<template v-else>
									<el-menu-item :index="sub.menuUrl" :key="sub.menuId"><i :class="sub.avatar"></i>{{sub.menuName}}</el-menu-item>
								</template>
							</template>
						</el-submenu>
						<el-menu-item v-else :key="item.menuId" :index="item.menuUrl"><i :class="item.avatar"></i>{{item.menuName}}</el-menu-item>
					</template>
				</el-menu>
			</div>
		</transition>
		<div class="page-wrapper">
			<navMenu ref="navMenuForm"></navMenu>
			<keep-alive>
				<router-view></router-view>
			</keep-alive>
		</div>
  	</div>
</template>

<script>
	import frameTop from '../components/FrameTop'
	import {baseUrl, ajax} from '@/common/biConfig'
	import navMenu from '@/components/NavMenu'
	import $ from 'jquery'
	import "jstree";
	import "jstree/dist/themes/default/style.min.css";

	export default {
	    data(){
			return {
				isShowMenu:true,
				menus:[],
				clearPage:[],  //需要清除缓存的页面
				productInfo:{},
			}
		},
		components: {
			frameTop,navMenu
    	},
		mounted(){
			let ts = this;
			ajax({
				type:"GET",
				data:{},
				url:"frame/Menus.action",
				success:function(resp){
					ts.menus = resp.rows;
				}
			}, ts);
			this.loadProductInfo();
		},
		computed: {
		},
		methods: {
			/**
			isCacheRouter(){
				let r = this.$route;
				if(r.meta && r.meta.cache == false){
					return false;
				}else{
					return true;
				}
			},
			*/
			selectMenu(index){
				let m = null;
				this.menus.forEach(element => { //第一层
					if(element.children && element.children.length > 0){
						$(element.children).each((a,b)=>{  //第二层
							if(b.children && b.children.length > 0){
								$(b.children).each((c,d)=>{  //第三层
									if(d.menuUrl === index){
										m = d;
										return false;
									}
								});
							}else if(b.menuUrl === index){
								m = b;
								return false;
							}
						});
					}else if(element.menuUrl === index){
						m = element;
						return false;
					}
				});
				this.$refs['navMenuForm'].menuAdd(m);
			},
			loadProductInfo(){
				ajax({
					url:"login/productInfo.action",
					data:{},
					type:"GET",
					success:(r)=>{
						this.productInfo = r.rows;
						window.document.title = this.productInfo.name;
					}
				}, this);
			}
		},
		watch: {
		},
		beforeRouteLeave: function(to, from, next) {
			this.$destroy();
			next();
		}
	}
</script>

<style lang="less" scoped>
	.navbar-static-side {
		position: fixed;
		width: 220px;
		height: calc(100% - 52px);
		overflow: auto;
	}
	.page-wrapper {
		position: inherit;
		margin: 0 0 0 220px;
		height: calc(100% - 51px);
		overflow: auto;
		width: calc(100% - 220px);
	}
	.el-menu-item {
		color: #0064b2;
		i {
			color: #0064b2;
			margin-right: 3px;
		}
	}
	.el-submenu__title {
		color: #0064b2;
		i {
			color: #0064b2;
			margin-right: 3px;
		}
		span {
			color: #0064b2;
		}
	}
</style>
