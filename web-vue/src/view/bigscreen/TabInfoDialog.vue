<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 表格对话框都放这里面 -->
<template>
  	<el-dialog title="选项卡配置" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		<template slot="title">
          <span class="el-dialog__title">选项卡配置</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
		 <el-form label-width="110px" size="mini" :model="tab">
			 <el-form-item label="选项卡：">
				<el-radio-group v-model="tab.index" @change="chgTab()">
					<template v-for="(item, idx) in opts.tabs">
					<el-radio :key="idx" :label="idx">选项卡{{idx + 1}}</el-radio>
					</template>
				</el-radio-group>
			</el-form-item>
			<el-form-item label="当前选中：">
				<el-select v-model="tab.curSelect" placeholder="请选择" style="width:100%;">
					<el-option
					v-for="(item, idx) in opts.tabs"
					:key="idx"
					:label="'选项卡' + (idx + 1)+' ('+item.name+')'"
					:value="idx">
					</el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="名称：">
				<el-input v-model="tab.name" @change="chgname()"></el-input>
			</el-form-item>
			<el-form-item label="关联组件：">
				<el-select v-model="tab.joins" filterable multiple clearable placeholder="请选择" style="width:100%;" @change="chgJoins()">
					<el-option
					v-for="item in opts.comps"
					:key="item.id"
					:label="item.title"
					:value="item.id">
					</el-option>
				</el-select>
			</el-form-item>
		 </el-form>

		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">关 闭</el-button>
		</div>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, newGuid} from '@/common/biConfig'
	import $ from 'jquery'
	import * as bsUtils from './bsUtils'

	export default {
		 props: {
			pageInfo: {
				type: Object,
				required: true
			},
		},
	    data(){
			return {
				show:false,
				comp:null,
				tab:{
					index:0,
					name:null,
					joins:[],
					curSelect:null,  //当前选中的Tab,用来在设计界面的选项卡切换
				},
				opts:{
					tabs:[],
					comps:[],
				}
			}
		},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			showDailog(comp){
				this.show = true;
				this.opts.tabs = comp.tabs;
				this.comp = comp;
				this.tab.curSelect = this.comp.curSelect || 0;
				this.opts.comps = this.pageInfo.comps.filter(m=>m.id != comp.id).map(m=>{
					return {id:m.id, title:m.title}
				});
				this.chgTab();
			},
			chgTab(){
				let idx = this.tab.index;
				this.tab.name = this.comp.tabs[idx].name;
				this.tab.joins = this.comp.tabs[idx].joins;
			},
			chgname(){
				let idx = this.tab.index;
				this.comp.tabs[idx].name = this.tab.name;
			},
			chgJoins(){
				let idx = this.tab.index;
				this.comp.tabs[idx].joins = this.tab.joins;
			},
			save(){
				//处理tab关联组件的隐藏
				let pageInfo = this.pageInfo;
				let b = this.comp;
				b.curSelect = this.tab.curSelect;
				let curHideNodes = [];
				if(b.tabs){
					//把除了选中的选项卡，其他选项卡关联组件都隐藏
					$(b.tabs).each((c, d)=>{
						//先移除组件 isTabHide 字段
						if(d.joins){
							$(d.joins).each((e, f)=>{
								let comp = bsUtils.findCompById(f, pageInfo);
								if(comp){
									if(comp.isTabHide == true){
										curHideNodes.push(comp);
									}
									delete comp.isTabHide;
								}
							});
						}
						if(c != b.curSelect){
							if(d.joins){
								$(d.joins).each((e, f)=>{
									let comp = bsUtils.findCompById(f, pageInfo);
									if(comp){
										comp.isTabHide = true;
									}
								});
							}
						}
					});
				}
				this.show = false;
				this.$parent.$forceUpdate();
				this.$parent.$nextTick(()=>{
					//重新注册resize，drag 事件
					$(curHideNodes).each((c, d)=>{
						this.$parent.compDragResizeEvent(d);
					});
				});
			}
		},
		watch: {
		}
	}
</script>
