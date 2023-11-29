<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="wrapper-content-nomargin">
		<el-row style="background-color:rgb(246, 248, 248);">
			<el-col :span="16">
				<el-menu @select="handleSelect" class="el-menu-demo" background-color="#f6f8f8" text-color="#777" mode="horizontal">
					<el-menu-item index="1"><i class="fa fa-arrow-left"></i> 返回</el-menu-item>
					<el-menu-item index="2"><i class="glyphicon glyphicon-save"></i> 保存</el-menu-item>
					<el-submenu index="3">
						<template slot="title"><i class="fa fa-plus-circle"></i> 数据</template>
						<el-menu-item index="3-1">选择立方体</el-menu-item>
						<el-menu-item index="3-2">选择数据表</el-menu-item>
					</el-submenu>
					<el-menu-item index="4"><i class="fa fa-gear"></i> 设置</el-menu-item>
					<el-menu-item index="5"><i class="glyphicon glyphicon-file"></i> 预览</el-menu-item>
					<el-menu-item index="6"><i class="fa fa-question-circle"></i> 帮助</el-menu-item>
				</el-menu>
			</el-col>
			<el-col :span="4">
				<el-slider v-model="pageInfo.style.bl" :min="-100" :max="100" @change="changebl()">
				</el-slider>
			</el-col>
			<el-col :span="3">
				<div style="padding:9px 0px 3px 10px;">
				缩放：{{pageInfo.style.bl}} %
				</div>
			</el-col>
			<el-col :span="1">
				<div style="padding:7px 3px 3px 3px;">
				<el-button size="mini" @click="resizebl()" round>还原</el-button>
				</div>
			</el-col>
		</el-row>

		<div class="bigscreen-layout">
			<layout-left :pageInfo="pageInfo" ref="layoutleftForm"></layout-left>
			<layout-right :pageInfo="pageInfo" ref="layoutRightForm" :userOpers="userOpers"></layout-right>
			<div class="layout-center">
				<layout-optarea ref="optarea" :userOpers="userOpers" :pageInfo="pageInfo"></layout-optarea>
			</div>
			<layout-bottom :pageInfo="pageInfo" ref="layoutBottomForm"></layout-bottom>
		</div>

		<!-- 保存框 -->
       <el-dialog title="数据大屏保存" :visible.sync="saveShow" :close-on-click-modal="false" custom-class="nopadding">
		 <template slot="title">
			<span class="el-dialog__title">数据大屏保存</span>
			<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
				<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
			</button>
		  </template>
         <el-form :model="saveInfo" ref="saveForm" :rules="rules" label-position="left" size="mini">
             <el-form-item label="大屏名称" label-width="120px" prop="name">
              <el-input v-model="saveInfo.name"></el-input>
            </el-form-item>
         </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="saveme()">确 定</el-button>
          <el-button @click="saveShow = false">取 消</el-button>
        </div>
      </el-dialog>
		<selectCube ref="selectCubeForm" useIn="bigscreen" :callback="selectCubeCallback"></selectCube>
        <select-dset ref="selectDsetForm" divId="bstables"></select-dset>
		<helper ref="helperForm"></helper>
	  <bigscreenPreview ref="previewForm"></bigscreenPreview>
  	</div>

</template>

<script>
	import { mapState } from "vuex";
	import {baseUrl, ajax, newGuid} from '@/common/biConfig'
	import $ from 'jquery'
	import LayoutLeft from './layout/LayoutLeft'
	import LayoutRight from './layout/LayoutRight'
	import LayoutOptarea from './layout/LayoutOptarea'
	import LayoutBottom from './layout/LayoutBottom'
	import selectCube from "@/view/bireport/SelectCube"
	import SelectDset from "@/view/portal/SelectDset"
	import Helper from './Helper'
	import {baseInfo} from './bsUtils'
	import bigscreenPreview from './BigscreenPreview'
	import * as bsUtils from './bsUtils'
	import 'jquery-ui-dist/jquery-ui'
	import 'jquery-ui-dist/jquery-ui.css'
	import "jstree";
	import "jstree/dist/themes/default/style.min.css";
	import "jquery-contextmenu";
	import "jquery-contextmenu/dist/jquery.contextMenu.min.css";

	export default {
		name:"bigscreenIndex",
	    data(){
			return {
				pageInfo:{style:baseInfo},
				isupdate:false,
				saveShow:false,
				saveInfo:{
					name:null,
				},
				rules:{
					name:[
						{ required: true, message: '必填', trigger: 'blur' },
					],
				},
				userOpers:[],  //用户操作信息，用来做撤销使用
			}
		},
		components: {
			LayoutLeft, LayoutRight, LayoutOptarea, LayoutBottom, selectCube, SelectDset,Helper, bigscreenPreview
    	},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			setUpdate(){
				this.isupdate = true;
			},
			handleSelect(key, keyPath){
				if(key === '1'){
					if(this.isupdate == true){
						if(confirm('报表还未保存，是否强制退出？')){
							this.hidePanel();
							this.$parent.$refs['navMenuForm'].chgMenuUrl('/bigscreen/Bigscreen','/bigscreen/BigscreenList');
							this.$router.push({path:"/bigscreen/BigscreenList", query:{}});
						}
					}else{
						this.hidePanel();
						//更新url
						this.$parent.$refs['navMenuForm'].chgMenuUrl('/bigscreen/Bigscreen','/bigscreen/BigscreenList');
						this.$router.push({path:"/bigscreen/BigscreenList", query:{}});
					}
				}else
				if(key === '3-1'){
					this.$refs['selectCubeForm'].select();
				}else
				if(key === '3-2'){
					this.$refs['selectDsetForm'].select();
				}else if(key === '4'){
					this.$refs['layoutRightForm'].openPageProp();
				}else if(key ==='6'){
					this.$refs['helperForm'].openDailog();
				}else if(key === '2'){
					this.savePage();
				}else if(key === '5'){
					let j = JSON.parse(JSON.stringify(this.pageInfo));
					//更改组件ID
					if(j.comps){
						$(j.comps).each((a, b)=>{
							let oldId = b.id;
							let id = newGuid();
							b.id = id;
							if(b.comp){
								b.comp.id = id;
							}
							//如果有tab组件关联到本组件的，需要同步修改
							$(j.comps).each((c, d)=>{
								if(d.type == 'tab'){
									if(d.tabs){
										$(d.tabs).each((e, f)=>{
											$(f.joins).each((g, h)=>{
												if(h == oldId){
													f.joins[g] = id;
												}
											});
										});
									}
								}
							});
						});
					}
					this.$refs['previewForm'].showPreview(j);
				}
			},
			//初始化页面
			init(id){
				ajax({
					url:"bigscreen/getJson.action",
					data:{id:id},
					type:"get",
					success:(resp)=>{
						let p = JSON.parse(resp.rows);
						//删除 isTabHide 标记
						$(p.comps).each((a, b)=>{
							delete b.isTabHide;
						});
						//隐藏tab关联的组件
						this.pageInfo = bsUtils.initTabHideEvent(p);
						this.isupdate = false;
						this.userOpers = [];
						this.$nextTick(()=>{
							let o = this.$refs['layoutleftForm'];
							o.initTableTree( );
							o.initcubes( this.pageInfo.selectDs);
							//初始化拖拽事件
							let oper = this.$refs['optarea'];
							oper.initCompsDragResizeEvent();
							oper.toCenter();
						});
					}
				});
			},
			//清理页面
			clearPage(){
				this.pageInfo = {style:baseInfo};
				this.isupdate = false;
				this.userOpers = [];
				this.$nextTick(()=>{
					this.$refs['optarea'].toCenter();
					let o = this.$refs['layoutleftForm'];
					o.initTableTree( );
					o.initcubes( );
				});
			},
			//画布缩放
			changebl(){
				this.isupdate = true;
			},
			//还原到0比例
			resizebl(){
				this.pageInfo.style.bl = 0;
				this.$refs['optarea'].toCenter();
				//this.isupdate = true;
			},
			selectCubeCallback(cubeId){
				this.pageInfo.selectDs = cubeId;
				var o = this.$refs['layoutleftForm'];
				o.tabActive = 'data-tab-2';
				o.initcubes(cubeId);
			},
			//更新图层界面
			updateLayer(){
				this.$refs['layoutleftForm'].$forceUpdate();
			},
			savePage(){
				 var tz = this;
				 if(!tz.pageInfo.comps || tz.pageInfo.comps.length == 0){
					 this.$notify.error("还未配置组件");
					 return;
				 }
				 //删除 isdelete = true 的组件
				tz.pageInfo.comps = tz.pageInfo.comps.filter(m=>m.isdelete != true);
				if(!tz.pageInfo.id) {  //新增
					this.saveShow = true;
					this.saveInfo.name = null;
				}else{ //更新
					ajax({
						type: "POST",
						url: "bigscreen/save.action",
						dataType: "JSON",
						data: {"pageInfo": JSON.stringify(tz.pageInfo), "id": tz.pageInfo.id},
						success: () => {
							tz.isupdate = false;
							tz.$notify.success("大屏更新成功！");
						}
					}, this);
				}
			},
			saveme(){
				this.$refs['saveForm'].validate((valid) => {
					if(valid){
						ajax({
							url:"bigscreen/save.action",
							type:"POST",
							data:{"pageInfo": JSON.stringify(this.pageInfo), pageName:this.saveInfo.name},
							success:(resp)=>{
								this.$notify.success({
									title: '保存成功!',
									offset: 50
								});
								this.isupdate = false;
                            	this.pageInfo.id = resp.rows;
								this.saveShow = false;
							}
						}, this);
					}
				});
			},
			//获取选中的组件对象
			getSelectComps(){
				return this.$refs['optarea'].mulitComps;
			},
			//选中组件
			setSelect(comp){
				this.$refs['optarea'].mulitComps = [comp];
				this.showPropPanel(comp);
			},
			 //显示数据面板
			showDataPanel(comp){
				this.$refs['layoutBottomForm'].showPanel(comp);
				this.$refs['layoutRightForm'].closeproperty();
			},
			//关闭所有面板
			hidePanel(){
				this.$refs['layoutRightForm'].closeproperty();
				this.$refs['layoutBottomForm'].closeDatapanel();
			},
			//显示属性面板
			showPropPanel(comp){
				if(this.$refs['layoutBottomForm']){
					this.$refs['layoutBottomForm'].closeDatapanel();
				}
				if(this.$refs['layoutRightForm']){
					this.$refs['layoutRightForm'].showPanel(comp);
				}
			},
		},
		watch: {
		},
		beforeRouteEnter(to, from, next){
			next(vm=>{
				if(from.name === 'bigscreenList'){
					let id =  vm.$route.params.id;
					if(id){
						vm.init(id);
					}else{
						vm.clearPage();
					}
				}else{
					vm.$refs['optarea'].toCenter();
				}
				vm.$refs['optarea'].bindkeyboardEvent();
			});

		},
		beforeRouteLeave: function(to, from, next) {
			this.$refs['optarea'].unbindkeyboardEvent();
			if(to.name === 'bigscreenList'){
				//this.$destroy();
				//清除组件
				this.pageInfo = {style:baseInfo};
				this.isupdate = false;
			}
			next();
		},
	}
</script>

<style lang="less" scoped>
	.bigscreen-layout {
		height: calc(100% - 35px);
		width: 100%;
	}
  .layout-center {
    margin-right: 0px;
    height: 100%;
    position: inherit;
    margin: 0 0 0 223px;
  }
</style>
