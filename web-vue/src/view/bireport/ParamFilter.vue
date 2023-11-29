<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<el-dialog :title="title" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
		<template slot="title">
			<span class="el-dialog__title">{{title}}</span>
			<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
			<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
			</button>
		 </template>
		 <!-- 维度是数字的筛选 -->
		 <template v-if=" isNumberDim() ">
					 <el-select
						v-model="oper"
						placeholder="筛选条件"
						size="mini"
						clearable
						style="width:100%;"
						>
						<el-option
							v-for="item in opt.oper"
							:key="item.value"
							:label="item.text"
							:value="item.value"
						>
					</el-option>
					</el-select>
				<div style="margin:10px">
					筛选值：<el-input-number v-model="value1" label="描述文字" size="mini"></el-input-number>
				</div>
				<div style="margin:10px" v-if="oper=='between'">
					筛选值：<el-input-number v-model="value2" label="描述文字" size="mini" ></el-input-number>
				</div>
		 </template>
		 <template v-else>
			<template v-if="!(param.type === 'month' || param.type === 'day')">
				<el-row :gutter="1">
					<el-col :span="6">
<button type="button" class="btn btn-xs btn-outline btn-danger" @click="clearCond()">清除筛选值</button>
<button type="button" class="btn btn-xs btn-outline btn-info" @click="selectAll()">全选</button>

					</el-col>
					<el-col :span="18">
						<el-input v-model="search"	size="mini" :clearable="true" placeholder="输入关键字搜索">
					<el-button slot="append" icon="el-icon-search" @click="searchme"></el-button>
				</el-input>
					</el-col>
				</el-row>



				<div class="el-dialog-div loaddiv">
					<el-checkbox-group v-model="checkList" >
						<template v-for="(item) in dimValus">
							<div :key="item.id">
								<el-checkbox name="vls" :key="item.id" @change="chgval" :label="item.id">{{item.name}}</el-checkbox>
							</div>
						</template>
					</el-checkbox-group>
				</div>
			</template>
			<template v-if="param.type === 'month'">
				<div class="loaddiv">
					<div class="block">
						<span class="demonstration">开始月份</span>
						<el-date-picker
						v-model="st"
						type="month"
						size="mini"
						:value-format="format"
						placeholder="选择月份">
						</el-date-picker>
					</div>
					<div style="height:30px;"></div>
					<div class="block">
						<span class="demonstration">结束月份</span>
						<el-date-picker
						size="mini"
						v-model="end"
						type="month"
						:value-format="format"
						placeholder="选择月份">
						</el-date-picker>
					</div>
				</div>
			</template>
			<template v-if="param.type === 'day'">
				<div class="loaddiv">
					<div class="block">
						<span class="demonstration">开始日期</span>
						<el-date-picker
						v-model="st"
						type="date"
						size="mini"
						:value-format="format"
						placeholder="选择日期">
						</el-date-picker>
					</div>
					<div style="height:30px;"></div>
					<div class="block">
						<span class="demonstration">结束日期</span>
						<el-date-picker
						v-model="end"
						type="date"
						size="mini"
						:value-format="format"
						placeholder="选择日期">
						</el-date-picker>
					</div>
				</div>
			</template>
		 </template>
		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax} from '@/common/biConfig'
	import $ from 'jquery'
	import { Loading } from "element-ui"
	import {findParamById,msginfo} from '@/view/bireport/bireportUtils'

	export default {
		props:{
			//在何处使用，比如 多维/报表/仪表盘/大屏
			usein:{
				type:String,
				required:true
			},
			token:{
				type:String,
				required: false,
			}
		},
	    data(){
			return {
				show:false,
				title:"",
				param:{},
				tid:null,
				dsid:null,
				checkList:[],
				dimValus:[],
				st:null,  //日期/月份的开始日期
				end:null,	//日期/月份的结束日期,
				search:null,
				useType: null,
				format:null,
				opt:{
					oper:[
						{value:">", text:"大于"},
						{value:"<", text:"小于"},
						{value:"=", text:"等于"},
						{value:"between", text:"区间"}
					],
				},
				oper: null,
				value1: null,
				value2: null,
				curVals:[],
				curValStrs: [],
				parentJSON: null, //筛选的上级维度
			}
		},
		mounted(){
		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			//维度是否数字类型，比如年龄
			isNumberDim(){
				let param = this.param;
				return (param.valType == 'Long' || param.valType == 'Int') && (param.tableColKey == null || param.tableColKey == '');
			},
			createDimFilter(dim, comp, useType, pos){   //用在维度
				this.show = true;
				this.title = dim.dimdesc+" - 维度筛选";
				this.param = dim;
				this.tid = comp.tid;
				this.search = null;
				this.dimValus = [];
				this.curVals = dim.vals || [];
				this.curValStrs = dim.valStrs || [];
				if(this.isNumberDim()){
					this.oper = dim.oper;
					this.value1 = dim.value1;
					this.value2 = dim.value2;
				}else if(dim.type === 'month'){
					this.st = dim.st;
					this.end = dim.end;
					this.format = dim.dateformat;
				}else if(dim.type === 'day'){
					this.st = dim.st;
					this.end = dim.end;
					this.format = dim.dateformat;
				}else{
					this.checkList = dim.vals || [];
				}
				this.$nextTick(()=>{
					let load = Loading.service({ fullscreen: false, target: document.querySelector('.loaddiv') });
					this.useType = useType;
					//确定上级
					let parent = [];
					if(pos == 'row'){
						let p = comp.rows.indexOf(dim);
						$(comp.rows).each((a, b)=>{
							if(a >= p){
								return false;
							}
							parent.push(b);
						});

					}else if(pos == 'col'){
						let p = comp.cols.indexOf(dim);
						$(comp.cols).each((a, b)=>{
							if(a >= p){
								return false;
							}
							parent.push(b);
						});
					}
					this.parentJSON = parent;
					ajax({
						url:"bireport"+(this.token?"/share":"")+"/paramFilter.action",
						type:"post",
						data:{id:dim.id, tid:this.tid, token:this.token, parentJSON:JSON.stringify(parent)},
						success:(resp)=>{
							this.dimValus = resp.rows.datas;
						}
					}, this, load);
				});

			},
			create(p){	//用在参数筛选
				this.show = true;
				this.title = p.name + " - 参数筛选";
				this.param = p;
				this.tid = p.tid;
				this.dsid = p.dsid;
				this.search = null;
				this.dimValus = [];
				this.checkList = p.vals || [];
				this.st = p.st?p.st: null;
				this.end = p.end?p.end:null;
				this.useType = 'param';
				this.format = p.dateformat;
				this.oper = p.oper;
				this.value1 = p.value1;
				this.value2 = p.value2;
				this.curVals = p.vals || [];
				this.curValStrs = p.valStrs || [];
				this.parentJSON = null;
				this.$nextTick(()=>{
					let load = Loading.service({ fullscreen: false, target: document.querySelector('.loaddiv') });
					ajax({
						url:"bireport"+(this.token?"/share":"")+"/paramFilter.action",
						type:"post",
						data:{id:p.id, tid:p.tid, token:this.token},
						success:(resp)=>{
							this.dimValus = resp.rows.datas;
						}
					}, this, load);
				});
			},
			clearCond(){
				this.curValStrs = [];
				this.curVals = [];
				this.checkList = [];
			},
			selectAll(){
				$(this.dimValus).each((a, b)=>{
					if(this.curVals.indexOf(b.id) == -1){ //不存在，添加
						this.curVals.push(b.id);
						this.curValStrs.push(b.name);
					}
					if(this.checkList.indexOf(b.id) == -1){
						this.checkList.push(b.id);
					}
				});
			},
			chgval(v, v2){
				let dv = v2.currentTarget._value;
				let desc = dv;
				this.checkList.forEach(e=>{
					let t = this.dimValus.filter(m=>m.id===dv)[0];
					desc = t.name;
				});
				if(v == true){ //添加
					this.curVals.push(dv);
					this.curValStrs.push(desc);
				}else{ //移除
					this.curVals.splice( this.curVals.indexOf(dv) , 1);
					this.curValStrs.splice( this.curValStrs.indexOf(desc ), 1 );
				}
			},
			save(){
				let p = this.param;
				if(this.isNumberDim()){
					p.oper = this.oper;
					p.value1 = this.value1;
					p.value2 = this.value2;
				}else if(p.type === 'month'){
					p.st =  this.st;
					p.end =  this.end;
					//判断是否st < ed
					if(Number(p.st) > Number(p.end)){
						msginfo("您选择的开始月份不能大于结束月份。", "error");
						return;
					}
				}else if(p.type === 'day'){
					p.st =  this.st;
					p.end =  this.end;
					//判断是否st < ed
					if(Number(p.st.replace(/-/g, "")) > Number(p.end.replace(/-/g, ""))){
						msginfo("您选择的开始日期不能大于结束日期。", "error");
						return;
					}
				}else{
					/**
					var strs = [];
					var vls = [];
					this.checkList.forEach(e=>{
						let t = this.dimValus.filter(m=>m.id===e)[0];
						if(t) {
						strs.push(t.name);
						vls.push(t.id);
						}
					});
					 */
					this.param.valStrs = this.curValStrs;
					this.param.vals = this.curVals;
				}
				this.show = false;
				if(this.usein === 'olap'){  //多维中筛选
					//刷新组件
					this.$parent.$refs['paramForm'].$forceUpdate();
					if(this.useType === 'param'){
						this.$parent.$refs['tableForm'].tableView();
						this.$parent.$refs['chartForm'].chartView();
					}else if(this.useType === 'chart'){
						this.$parent.$refs['chartForm'].chartView();
					}else if(this.useType === 'table'){
						this.$parent.$refs['tableForm'].tableView();
					}
				}else if(this.usein === 'report'){
					let p = this.$parent;
					p.setUpdate();
					if(this.useType === 'chart'){
						p.chartView();
					}else if(this.useType === 'table'){
						p.tableView();
					}
				}else if(this.usein == 'share'){
					if(this.useType === 'param'){
						this.$parent.$refs['tableForm'].tableView();
						this.$parent.$refs['chartForm'].chartView();
					}else if(this.useType === 'chart'){
						this.$parent.$refs['chartForm'].chartView();
					}else if(this.useType === 'table'){
						this.$parent.$refs['tableForm'].tableView();
					}
				}
			},
			searchme(){
				let load = Loading.service({ fullscreen: false,  target: document.querySelector('.loaddiv')});
				let p = this.param;
				ajax({
					url:"bireport"+(this.token?"/share":"")+"/paramFilter.action",
					type:"post",
					data:{id:p.id, tid:this.tid, keyword:this.search, parentJSON:this.parentJSON==null?null:JSON.stringify(this.parentJSON), token:this.token},
					success:(resp)=>{
						this.dimValus = resp.rows.datas;
					}
				}, this, load);
			},
		},
		watch: {
		}
	}
</script>

<style lang="css" scoped>
</style>
