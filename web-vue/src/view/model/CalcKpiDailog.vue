<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<el-dialog :title="title" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		  	 <template slot="title">
				<span class="el-dialog__title">{{title}}</span>
				<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
				<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
				</button>
			</template>
		  <el-form :model="kpi" :rules="rule" ref="kpiForm" label-position="left" size="mini">
			<el-form-item label="度量标识：" label-width="110px" prop="alias">
				<el-input v-model="kpi.alias" :disabled="true"></el-input>
			</el-form-item>
			<el-form-item label="显示名称：" label-width="110px" prop="kpiname">
				<el-input v-model="kpi.kpiname"></el-input>
			</el-form-item>
			<el-form-item  label="表 达 式：" label-width="110px" prop="expression">
				<label slot="label">表达式 <a @click="helpbdsinfo" href="javascript:;"><i class="fa fa-question-circle"></i></a>：</label>
				<el-row>
					<el-col :span="24" class="myexp">
							<codemirror :options="cmCfg" ref="mysqlcode" v-model="kpi.expression"></codemirror>
					</el-col>
				</el-row>
			</el-form-item>
			<div class="actColumn">
				<template v-for="item in opt.kpis">
					<button @click="selectCol(item.name)" :key="item.name" :name="item.name" :expression="item.expression" type="button" class="btn btn-primary btn-xs kpiInfo">{{item.desc}}</button>
				</template>
			</div>
			<el-form-item label="计算方式：" label-width="110px" prop="kpiaggre">
					<el-select
						v-model="kpi.kpiaggre"
						placeholder="请选择"
						style="width:100%;"
						>
						<el-option
							v-for="item in opt.atp"
							:key="item"
							:label="item"
							:value="item"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="度量单位：" label-width="110px" prop="kpiunit" >
					<el-input v-model="kpi.kpiunit"></el-input>
				</el-form-item>
				<el-form-item label="格式化：" label-width="110px" prop="kpifmt" >
					<el-select
						v-model="kpi.kpifmt"
						placeholder="请选择"
						style="width:100%;"
						:clearable="true"
						>
						<el-option
							v-for="item in opt.fmtJson"
							:key="item.value"
							:label="item.text"
							:value="item.value"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="度量解释：" label-width="110px" prop="kpinote" >
					<el-input type="textarea" v-model="kpi.kpinote"></el-input>
				</el-form-item>
		  </el-form>

		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>

		<el-dialog custom-class="nopadding" width="50%" title="表达式度量说明" :visible.sync="innerVisible" append-to-body>
			<div style="line-height:30px; padding:15px;">表达式度量是一句SQL片段，用来提高系统灵活性。<li>通过表达式来对度量进行运算；</li>字段相加：<div class="mycode"> sum(a)+sum(b) </div>或：<div class="mycode"> sum(a + b) </div><li>数据量计数；</li><div class="mycode">count(*)</div><p class="text-warning">请注意：此处创建的表达式度量必须使用sum/avg/max/min/count等聚合函数。</p><p></p></div>
			<div slot="footer" class="dialog-footer">
				<el-button @click="innerVisible = false">关 闭</el-button>
			</div>
		</el-dialog>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, newGuid, insertText2focus} from '@/common/biConfig'
	import $ from 'jquery'
	import { codemirror } from 'vue-codemirror'
	require("codemirror/mode/sql/sql.js")
	import {fmtJson, rates} from '@/view/portal/Utils'

	export default {
	    data(){
			return {
				show:false,
				title:"",
				isupdate:false,
				innerVisible:false,
				kpi:{
					alias:null,
					kpiname:null,
					expression:null,
					kpiaggre:null,
					kpinote:null,
					kpifmt:null,
					kpiunit:null,
					tid : null,
				},
				opt:{
					atp : ["sum","avg","count", "max", "min", "distinct", "first", "last"],
					fmtJson: fmtJson,
					kpis:[],
				},
				rule:{
					alias:[{ required: true, message: "必填", trigger: "blur" }],
					kpiname:[{ required: true, message: "必填", trigger: "blur" }],
					expression:[{ required: true, message: "必填", trigger: "blur" }],
					kpiaggre:[{ required: true, message: "必填", trigger: "blur" }]
				},
				cmCfg:{ //sql编辑器配置
					mode: "text/x-sql",
					indentWithTabs: true,
					smartIndent: true,
					lineNumbers: false,
					matchBrackets : true,
					autofocus: false,
					lineWrapping:true
				},
			}
		},
		props:{
			pageJson: {
				type: Object,
				required: true,
			},
		},
		mounted(){

		},
		components:{
			codemirror
		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			save(){
				this.$refs["kpiForm"].validate( (valid) => {
					if(valid){
						var execf = ()=>{
							var rightRef = $("#cuberighttree").jstree(true);
							if(this.isupdate){
								var rightRef = $("#cuberighttree").jstree(true);
								var kpi = rightRef.get_selected(true)[0];
								kpi.li_attr.aggre = this.kpi.kpiaggre;
								kpi.li_attr.fmt = this.kpi.kpifmt;
								kpi.li_attr.unit = this.kpi.kpiunit;
								kpi.li_attr.dispName = this.kpi.kpiname;
								kpi.li_attr.kpinote = this.kpi.kpinote;
								kpi.li_attr.col = this.kpi.expression;
								kpi.li_attr.alias = this.kpi.alias;
								kpi.li_attr.tid = this.kpi.tid;
								kpi.li_attr.isupdate = "y";  //表示计算指标已经更改过了。
								//$("#cuberighttree").tree("update", {target:kpi.target, text:kpi.li_attr.aggre+"("+name+")"});
								rightRef.rename_node(kpi.id, kpi.li_attr.aggre+"("+this.kpi.kpiname+")");
							}else{
								var cid = this.$parent.findCubeMaxId();
								var o = {id:cid.id, text:this.kpi.kpiaggre+"("+this.kpi.kpiname+")",li_attr:{tp:"kpi",calcKpi:1,calc:0,drag:true,aggre:this.kpi.kpiaggre,col:this.kpi.expression,
										alias:this.kpi.alias, tid: this.kpi.tid, dispName:this.kpi.kpiname,tname:"",fmt:this.kpi.kpifmt,unit:this.kpi.kpiunit,kpinote:this.kpi.kpinote},icon:"fa fa-circle icon_kpi"};
								//$("#cuberighttree").tree("append", {parent:$("#cuberighttree").tree("find", "cubedl").target, data:[o]});
								rightRef.create_node('cubedl', o);
								if(rightRef.is_closed('cubedl')){
									rightRef.open_node('cubedl');
								}
							}
							this.show = false;
						}
						execf();
						/**
						var pageJson = this.pageJson;
						var o = {
							tableType : "horizontal",
							kpiCodeColumn: "",
							kpiNameColumn : "",
							kpiValueColumn:  "",
							tid : pageJson.tid,
							tName : pageJson.tName,
							onlyTableId : 'n'
						};
						o.expression = escape(this.kpi.expression);
						o.mustAgg = true;
						ajax({
							type: "POST",
							url: "etl/testDynaColumn.action",
							dataType:"json",
							data: o,
							success: function(resp){
								execf();
							}
						}, this);
						 */
					}
				});
			},
			initKpis(){
				var kpis = [];
				var leftRef = $("#cubelefttree").jstree(true);
				//查询已选指标
				var ls = leftRef.get_node("leftroot").children;
				for(let i=0; i<ls.length; i++){
					var k = leftRef.get_node(ls[i]).li_attr;
					kpis.push({name:k.col, expression:(k.expression==null?"":k.expression), desc:k.col});
				}
				for(let i=0; i<this.opt.atp.length - 2; i++){
					var t = this.opt.atp[i];
					kpis.push({name:t+'( )', desc:t});
				}
				this.opt.kpis = kpis;
			},
			showDailog(isupdate, tid){
				this.isupdate = isupdate;
				//先清空值
				if(this.$refs['kpiForm']){
					this.$refs['kpiForm'].resetFields();
				}
				for(let c in this.kpi){
					this.kpi[c] = null;
				}
				if(!isupdate){
					this.title="创建表达式度量";
					this.kpi.alias = "k" +  Math.round(Math.random() * 100000);
					this.kpi.expression = "";
				}else{
					this.title="编辑表达式度量";
					//回写
					var rightRef = $("#cuberighttree").jstree(true);
					var kpi = rightRef.get_selected(true)[0];
					this.kpi.kpiaggre = kpi.li_attr.aggre;
					this.kpi.kpifmt = kpi.li_attr.fmt;
					this.kpi.kpiunit = kpi.li_attr.unit;
					this.kpi.kpiname = kpi.li_attr.dispName;
					this.kpi.kpinote = kpi.li_attr.kpinote;
					this.kpi.expression = kpi.li_attr.col;
					this.kpi.alias = kpi.li_attr.alias;
				}
				this.kpi.tid = tid;
				this.show = true;
				this.initKpis();
			},
			helpbdsinfo(){
				this.innerVisible = true;
			},
			selectCol(v){
				v += " ";
				var o = this.$refs['mysqlcode'].codemirror;
				let pos1 = o.getCursor();
				let pos2 = {};
				pos2.line = pos1.line;
				pos2.ch = pos1.ch;
				if(o.somethingSelected){
					o.replaceSelection(v);
				}else{
					o.replaceRange(v,pos2);
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
    border: solid 1px #cccccc;
    background-color: #ffffff;
}
.actColumn {
    height: 110px;
    overflow: auto;
}
.kpiInfo {
	margin-right: 5px;
	margin-bottom: 5px;
}
</style>
<style lang="less">
.myexp .CodeMirror {
    border: 1px solid #DCDFE6;
    border-radius: 5px;
    height: 100px;
    width: 100%;
    font-size: 14px;
}
</style>>
