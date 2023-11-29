<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="login_page fillcontain">
		<div style="height:50px;" role="navigation">
		  <nav class="navbar navbar-static-top">
			  <div class="navbar-header">
				  <template v-if="!productInfo.log">
					<img src="../assets/image/log3.png" class="img-responsive">
				  </template>
				  <template v-if="productInfo.log">
					<img :src="productInfo.log"  width="218" height="47">
				  </template>
			</div>
		</nav>
	  </div>
		<transition name="el-fade-in">
	  		<section class="form_contianer">
		    	<el-form :model="loginForm" :rules="rules" ref="loginForm"  @keyup.enter.native="submitForm('loginForm')">
					<h2>用户登录</h2>
					<div class="row">
						<div class="col-sm-6">
							<div class="logimg">
								<img src="../assets/image/xsqq.png">
							</div>
						</div>
						<div class="col-sm-6">
							<el-form-item prop="username">
								<el-input v-model="loginForm.username" placeholder="用户名" suffix-icon="el-icon-user"></el-input>
							</el-form-item>
							<el-form-item prop="password">
								<el-input type="password" placeholder="密码" v-model="loginForm.password" suffix-icon="el-icon-lock"></el-input>
							</el-form-item>
							<el-form-item>
								<el-button type="primary" @click="submitForm('loginForm')" class="submit_btn">登录</el-button>
							</el-form-item>
							<div>账号/密码：admin/123456</div>
						</div>
					</div>
				</el-form>
	  		</section>
		</transition>

		<div align="center" class="bottomtxt">

			<div class="txt" align="center">
				<template v-if="productInfo.products=='y'">
				 <a href='https://www.ruisitech.com' target="_blank">睿思BI</a> - <a href='https://www.ruisitech.com/ultimate.html' target="_blank">旗舰版</a> - <a href='https://www.ruisitech.com/productent.html' target="_blank">企业版</a>  - <a href='https://www.ruisitech.com/opensource.html' target="_blank">开源版</a> - <a href='https://www.ruisitech.com/yun.html' target="_blank">睿思云</a> - <a href='http://www.ruisibi.cn/book.htm' target="_blank">使用手册</a> - <a href='https://www.ruisitech.com/suggest.html' target="_blank">问题反馈</a>
				</template>
			</div>
				©{{ productInfo.company ?  productInfo.company : '成都睿思商智科技有限公司'}}
                2021 版权所有
		</div>
  	</div>
</template>

<script>
	import {baseUrl, ajax, isMobile} from '@/common/biConfig'
	import $ from 'jquery'
	import { JSEncrypt } from 'jsencrypt'
	import { Loading } from "element-ui";

	export default {
	    data(){
			return {
				loginForm: {
					username: '',
					password: '',
				},
				rules: {
					username: [
			            { required: true, message: '请输入用户名', trigger: 'blur' },
			        ],
					password: [
						{ required: true, message: '请输入密码', trigger: 'blur' }
					],
				},
				productInfo:{}
			}
		},
		mounted(){
			this.isLogin();
			this.loadProductInfo();
		},
		computed: {
		},
		methods: {
			submitForm: function(formName) {
				let ts = this;
				this.$refs[formName].validate(async (valid) => {
					if (valid) {
						//获取key
						ajax({
							url:"login/getKey.action",
							type:"GET",
							data:{},
							success:(resp)=>{
								let encrypt = new JSEncrypt();
								encrypt.setPublicKey(resp.rows);
								var psd = encrypt.encrypt(this.loginForm.password);
								var name = encrypt.encrypt(this.loginForm.username);

								let load = Loading.service({});
								ajax({
									type:"POST",
									data:{userName:name, password:psd},
									url:"login/dologin.action",
									success:function(resp){
										if (resp.result == 1) {
											//清除菜单缓存
											localStorage.removeItem("menus");
											//处理callback
											let callback = ts.$route.query.callback;
											if(callback){
												ts.$router.push(callback);
											}else{
												if(resp.rows.mobile === true){
													ts.$router.push('mobile/Index')
												}else{
													ts.$router.push('Welcome')
												}
											}
										}else{
											ts.$notify.error({
												title: '登录错误',
												message: resp.msg,
												offset: 50
											});
										}
									},
								}, ts, load);
							}
						}, ts);
					} else {
						return false;
					}
				});
			},
			//判断是否已经登录
			isLogin(){
				ajax({
					url:"login/isLogin.action",
					data:{},
					type:"GET",
					success:(r)=>{
						if(r.rows == 'y'){
							//清除菜单缓存
							localStorage.removeItem("menus");
							if(isMobile()){
								this.$router.push('mobile/Index');
							}else{
								this.$router.push('Welcome');
							}
						}
					}
				}, this);
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
	.login_page{
		background-color: #f0f3f4;
		background:url("../assets/image/login_bg.jpg") 100% center
	}
	.form_contianer{
		max-width: 600px;
		padding: 20px;
		top: 40%;
		margin: 0 auto;
    	position: relative;
    	transform: translateY(-55%);
 		box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.16), 0 2px 10px 0 rgba(0, 0, 0, 0.12);
    	border-radius: .5rem;
		background-color: #fff;
		text-align: center;
		.submit_btn{
			width: 100%;
			font-size: 16px;
		}
		h2 {
			font-size: 24px;
			padding: 0 20px 20px 20px;
			font-weight: normal;
		}
	}
	.tip{
		font-size: 12px;
		color: red;
	}
	.logimg {
    	padding:20px 10px 20px 0px;
	}
	@media (max-width: 600px) {
		.form_contianer {
			max-width: 400px;
		}
		.logimg {
			padding:5px 0px 20px 0px;
		}
	}
	@media (max-width: 500px) {
		.form_contianer {
			max-width: 300px;
		}
		.logimg {
			padding:5px 0px 20px 0px;
		}
	}
	.navbar-static-top {
		background-color: #034d8f;
	}
	.bottomtxt{
		bottom:0px;
		width: 100%;
		position: absolute;
		.txt {
			color:#fff;
		}
		a {
			color:#fff;
		}
	}
</style>
