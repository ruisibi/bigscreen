<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 组件筛选，和查询条件关联 -->
<template>
  	<el-dialog title="组件筛选" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		<template slot="title">
          <span class="el-dialog__title">组件筛选</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
		  <div class="el-dialog-div">
		 <div align="left" style="margin:10px;">
			<button type="button" class="btn btn-primary" @click="addFilter()">
				<i class="glyphicon glyphicon-plus" aria-hidden="true"></i>新增筛选条件
			</button>
		 </div>
		<el-table :data="comp&&comp.compParams?comp.compParams:[]" border style="width: 100%" header-row-class-name="tableHeadbg">
			<el-table-column align="center" prop="col" label="筛选字段"></el-table-column>
			<el-table-column align="center" prop="type" label="判断条件"></el-table-column>
			<el-table-column align="center" prop="val" :formatter="fmtvalue" label="筛选值"></el-table-column>
			<el-table-column align="center" prop="valuetype" label="值类型"></el-table-column>
			<el-table-column align="center" prop="id" label="操作">
				<template slot-scope="scope">
						<a class="btn btn-primary btn-xs" @click="modifyFilter(scope.row.id)"> 修改 </a>
						<a class="btn btn-danger btn-xs" @click="delFilter(scope.row.id)"> 删除 </a>
				</template>
			</el-table-column>
		</el-table>
		  </div>
		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="filtersSave()">关 闭</el-button>
		</div>

		<el-dialog :title="title" width="40%" :visible.sync="addShow" custom-class="nopadding" append-to-body>
			<el-form :model="filter" ref="filterForm" :rules="rules" size="mini">
				<el-tabs v-model="activeName" >
   					<el-tab-pane label="基本信息" name="base">
						<el-form-item label="筛选字段：" label-width="100px" prop="col" size="mini">
							<el-select v-model="filter.col" placeholder="请选择" style="width:100%">
								<el-option
								v-for="item in opts.cols"
								:key="item.colName"
								:label="item.colName"
								:value="item.colName">
								</el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="表别名：" label-width="100px" prop="tableAlias" v-if="tincome == 'ds'">
							<el-input v-model="filter.tableAlias" placeholder="当使用SQL语句数据集时，对应筛选字段所属表的别名"/>
						</el-form-item>
						<el-form-item label="判断条件：" label-width="100px" prop="type">
							<el-select v-model="filter.type" placeholder="请选择">
								<el-option
								v-for="item in tincome==='rest'?opts.resttypes : opts.types"
								:key="item"
								:label="item"
								:value="item">
								</el-option>
							</el-select>
						</el-form-item>

						<el-form-item label="筛选值：" label-width="100px" prop="value">
							<template v-if="filter.usetype === 'gdz'">
								<el-input v-model="filter.value"  placeholder="录入固定值或函数"></el-input>
							</template>
							<template v-if="filter.usetype === 'param'">
								<el-select v-model="filter.linkparam" style="width:100%" placeholder="请选择定义的参数">
									<el-option
									v-for="item in opts.compParams"
									:key="item.value"
									:label="item.label"
									:value="item.value">
									</el-option>
								</el-select>
							</template>
							<div style="margin-top:3px;">
							<el-radio-group v-model="filter.usetype" size="mini">
								<el-radio-button label="gdz">固定值</el-radio-button>
								<el-radio-button label="param">链接到参数</el-radio-button>
							</el-radio-group>
							<el-button type="success" size="mini" @click="showHelper=!showHelper" round>定义函数</el-button>
							</div>
						</el-form-item>
						<div class="tip" v-if="showHelper">
								<div style="line-height:30px;">通过函数，实现动态设置筛选值。目前支持的函数：<li>获取当前时间函数： cur_date() </li><li>时间加减函数： date_add(date, step, type) </li><div class="mycode">date: 时间参数。<br> step: 数字类型，对时间加减的数量。 <br> type: 运算类型，支持 day/month/year 3种类型。</div><li>时间格式化函数：date_format(data, format) </li><div class="mycode">date: 时间参数。<br> format: 格式化参数，比如 yyyy-MM-dd 表示格式化为天。</div></div>
							</div>
						<template v-if="filter.type =='between'">
							<el-form-item label="筛选值2：" label-width="100px" prop="value">
								<template v-if="filter.usetype === 'gdz'">
									<el-input v-model="filter.value2"  style="width:100%;" placeholder="录入固定值或函数"></el-input>
								</template>
								<template v-if="filter.usetype === 'param'">
									<el-select v-model="filter.linkparam2" style="width:100%" placeholder="请选择定义的参数">
										<el-option
										v-for="item in opts.compParams"
										:key="item.value"
										:label="item.label"
										:value="item.value">
										</el-option>
									</el-select>
								</template>

							</el-form-item>
						</template>
						<el-form-item label="值类型：" label-width="100px" prop="vtype">
							<el-radio-group v-model="filter.valuetype" size="mini">
								<el-radio label="string" border>字符类型</el-radio>
								<el-radio label="number" border>数字类型</el-radio>
							</el-radio-group>
						</el-form-item>
					</el-tab-pane>
   					<el-tab-pane label="回调函数" name="cb" v-if="useIn==='dashboard'">
						<p class="text-warning">当参数是链接到参数时，通过定义回调函数动态修改参数值，当判断条件是between时，回掉函数会调用两次，并传递不同的值。</p>
						function
						<el-input style="width:100px;" v-model="filter.funcname" size="mini"></el-input>
						(<b>value</b>, <b>vpos</b>){
						<el-input type="textarea" :rows="8" v-model="filter.code" ></el-input>
						}
						<br/>
						<p class="text-warning">说明：需要通过return函数返回值。</p>
					</el-tab-pane>
				</el-tabs>

			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="filterSave()">确 定</el-button>
				<el-button @click="addShow = false">取 消</el-button>
			</div>
		</el-dialog>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, newGuid} from '@/common/biConfig'
	import $ from 'jquery'
	import * as utils from './Utils'
	import * as bsUtils from '@/view/bigscreen/bsUtils'

	export default {
	    data(){
			return {
				show:false,
				addShow:false,
				showHelper:false,
				title:"创建筛选条件",
				activeName:"base",
				comp:null,
				tableData:[],
				isupdate:false,
				paramId:null,
				tincome: null, //表来源，可能是 rest 接口
				filter:{
					col:null,
					type:null,
					value:null,
					value2:null,
					vtype:null,
					valuetype:null,
					usetype:null,
					linkparam:null,
					linkparam2:null,
					funcname:null,
					code:null,
					tableAlias: null,
				},
				opts:{
					cols:[],
					types:["=",">",">=","<", "<=", "!=", "between", "in", "like", "is", "is not"],
					resttypes:["="],
					compParams:[]
				},
				rules:{
					col:[
						{ required: true, message: '必填', trigger: 'blur' }
					],
					type:[
						{ required: true, message: '必填', trigger: 'blur' }
					]
				}
			}
		},
		 props: {
			pageInfo: {
				type: Object,
				required: true
			},
			//在哪里使用report/dashboard/bigscreen
			useIn:{
				type:String,
				required:true,
			},
		},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			init(comp){
				if(!comp.tid){
					utils.msginfo("组件还未定义数据，不能定义筛选。");
					return;
				}
				this.comp = comp;
				this.show = true;
				this.activeName = "base";
				this.tincome = comp.tincome;
				this.getCols();
				if(this.useIn==='dashboard'){
					if(this.pageInfo.globalParams){
						let pms = [];

						$(this.pageInfo.globalParams).each((a, b) => {
							if(b.type === 'datetime'){	//时间区间
								pms.push({value:b.id+"_start", label:b.name+"-起始"});
								pms.push({value:b.id+"_end", label:b.name+"-截至"});
							}else{
								pms.push({value:b.id, label:b.name});
							}
						});

						this.opts.compParams = pms;
					}
				}else if(this.useIn === 'report'){
					if(this.pageInfo.params){
						this.opts.compParams = this.pageInfo.params.map(m=>{
							return {value:m.id, label:m.name}
						});
					}
				}else if(this.useIn == 'bigscreen'){
					//查询大屏的所有参数组件
					let ret = [];
					$(this.pageInfo.comps).each((a, b)=>{
						if(b.type == 'param'){
							b = b.comp;
							if(b.type == 'datetime'){
								ret.push({value:b.id+"_start", label:b.name+"-起始"});
								ret.push({value:b.id+"_end", label:b.name+"-截至"});
							}else{
								ret.push({value: b.id, label:b.name});
							}
						}
					});
					this.opts.compParams = ret;
				}
			},
			filtersSave(){
				let comp = this.comp;
				this.show = false;
				if(this.useIn === 'dashboard'){
					let c = this.$parent.$refs['r_'+comp.id][0].$refs['mv_'+comp.id];
					c.view();
				}else{
					let c = this.$parent.$refs['mv_' + comp.id];
					c.view(); //调用通用方法
				}
			},
			fmtvalue(row, column, cellValue, index){
				if(row.usetype === 'param'){
					return "链接到参数";
				}else{
					return row.val +  (row.val2? '/' + row.val2: '');
				}
			},
			addFilter(){
				this.addShow = true;
				this.isupdate = false;
				this.filter.valuetype = "string";
				this.filter.usetype = "gdz";
				this.filter.col = null;
				this.filter.type = null;
				this.filter.value = null;
				this.filter.value2 = null;
				this.filter.vtype = null;
				this.filter.linkparam = null;
				this.filter.linkparam2 = null;
				this.filter.funcname = "callback";
				this.filter.tableAlias = null;
				this.filter.code = null;
			},
			modifyFilter(id){
				this.isupdate = true;
				this.paramId = id;
				this.addShow = true;
				let t = this.comp.compParams.filter(m=>m.id===id)[0];
				this.filter.col = t.col;
				this.filter.type = t.type;
				this.filter.value = t.val;
				this.filter.value2 = t.val2;
				this.filter.valuetype = t.valuetype;
				this.filter.vtype = t.vtype;
				this.filter.linkparam = t.linkparam;
				this.filter.linkparam2 = t.linkparam2;
				this.filter.usetype = t.usetype;
				this.filter.funcname = "callback";
				this.filter.tableAlias = t.tableAlias;
				if(t.code){
					this.filter.code = unescape(t.code);
				}else{
					this.filter.code = null;
				}
			},
			delFilter(id){
				if(confirm("是否确认删除？")){
					$(this.comp.compParams).each((a, b)=>{
						if(b.id === id){
							this.comp.compParams.splice(a, 1);
							return false;
						}
					});
				}
			},
			filterSave(){
				let ts = this;
				let comp = this.comp;
				this.$refs['filterForm'].validate((valid) => {
					if (valid) {
						if(!comp.compParams){
							comp.compParams = [];
						}
						if(ts.filter.usetype === 'gdz' && ts.filter.value == null){
							ts.$notify.error("未定义筛选值。");
							return;
						}
						if(ts.filter.usetype === 'param' && ts.filter.linkparam == null){
							ts.$notify.error("未选择链接参数。");
							return;
						}
						if(ts.isupdate == true){
							let t = comp.compParams.filter(m=>m.id===ts.paramId)[0];
							//获取字段
							var retCol = ts.opts.cols.filter(m=>m.colName === ts.filter.col)[0];
							t.col = retCol.colName;
							t.expression = retCol.expression;
							t.dimType = retCol.dimType;
							t.vtype = retCol.type;
							t.alias = retCol.alias;
							t.dateformat = retCol.dateformat;
							t.type = ts.filter.type;
							t.val = ts.filter.value;
							t.val2 = ts.filter.value2;
							t.valuetype = ts.filter.valuetype;
							t.linkparam = ts.filter.linkparam;
							t.linkparam2 = ts.filter.linkparam2;
							t.usetype = ts.filter.usetype;
							t.tableAlias = ts.filter.tableAlias;
							t.nodetype = "dim"; //维度上的筛选
							if(ts.filter.code){
								t.code = escape(ts.filter.code);
							}else{
								t.code = null;
							}
						}else{
							var o = {id:newGuid(), type:ts.filter.type, val:ts.filter.value, val2:ts.filter.value2, valuetype:ts.filter.valuetype, linkparam:ts.filter.linkparam, linkparam2:ts.filter.linkparam2,usetype:ts.filter.usetype, nodetype:'dim', tableAlias: ts.filter.tableAlias, code:ts.filter.code?escape(ts.filter.code):null};
							//获取字段
							var retCol = ts.opts.cols.filter(m=>m.colName === ts.filter.col)[0];
							o.col = retCol.colName;
							o.vtype = retCol.type;
							o.expression = retCol.expression;
							o.dimType = retCol.dimType;
							o.alias = retCol.alias;
							o.dateformat = retCol.dateformat;
							comp.compParams.push(o);
						}
						this.addShow = false;
					}
				});

			},
			getCols(){
				let comp = this.comp;
				let ts = this;
				if(comp.tincome==='rest'){ //rest类型数据，获取rest参数
					ajax({
						type:"POST",
						url:"etl/getRestParam.action",
						dataType:'JSON',
						data: {tid:comp.tid},
						success: function(resp){
							if(resp.rows){
								let pms = JSON.parse(resp.rows);
								ts.opts.cols = pms.map(m=>{
									return {colId:m.paramName, colName:m.paramName, colType: m.paramType, defvalue: m.paramDefvalue};
								});
							}
						}
					}, this);
				}else{
					ajax({
						type:"POST",
						url:"model/columnsWithDim.action",
						data: {tableId:comp.tid},
						success: function(resp){
							ts.opts.cols = resp.rows;
						}
					}, this);
				}
			}

		},
		watch: {
		}
	}
</script>

<style lang="less" scoped>
.mycode {
	padding: 5px;
    border: solid 1px #eaeefb;
	background-color:#f9fafc;
	border-radius: 4px;
}
.tip {
	padding: 8px 16px;
    background-color: #ecf8ff;
    border-radius: 4px;
    border-left: 5px solid #50bfff;
    margin: 20px 0;
}
</style>
