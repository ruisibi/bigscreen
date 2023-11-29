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
		  <el-form :model="dim" :rules="rule" ref="dimForm" label-position="left" size="mini">
			  <template v-if="curDim.li_attr.tp === 'kpi'">
				  <el-form-item label="度量字段：" label-width="110px" >
					{{  curDim.li_attr.col }}
				</el-form-item>
				<el-form-item label="别名：" label-width="110px" >
					{{  curDim.li_attr.alias }}
				</el-form-item>
				<el-form-item label="显示名称：" label-width="110px" prop="kpiname" >
					<el-input v-model="dim.kpiname"></el-input>
				</el-form-item>
				<el-form-item label="计算方式：" label-width="110px" prop="kpiaggre">
					<el-select
						v-model="dim.kpiaggre"
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
				<el-form-item label="去重字段：" label-width="110px" prop="distinctCol" v-if="dim.kpiaggre == 'count(distinct)'">
					<el-select
						v-model="dim.distinctCol"
						placeholder="请选择"
						style="width:100%;"
						:clearable="true"
						>
						<el-option
							v-for="item in opt.distinctCols"
							:key="item.id"
							:label="item.name"
							:value="item.id"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="度量单位：" label-width="110px" prop="kpiunit" >
					<el-input v-model="dim.kpiunit"></el-input>
				</el-form-item>
				<el-form-item label="格式化：" label-width="110px" prop="kpifmt" >
					<el-select
						v-model="dim.kpifmt"
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
					<el-input type="textarea" v-model="dim.kpinote"></el-input>
				</el-form-item>
			  </template>
			  <template v-if="curDim.li_attr.tp === 'dim'">
				<el-form-item label="维度字段：" label-width="110px" >
					{{  curDim.li_attr.col }}
				</el-form-item>
				<el-form-item label="别名：" label-width="110px" >
					{{  curDim.li_attr.alias }}
				</el-form-item>
				<el-form-item label="显示名称：" label-width="110px" prop="dimname" >
					<el-input v-model="dim.dimname"></el-input>
				</el-form-item>
				<el-form-item label="维度类型：" label-width="110px" prop="dimtype">
					<el-select
						v-model="dim.dimtype"
						placeholder="请选择"
						style="width:100%;"
						:clearable="false"
						>
						<el-option
							v-for="item in opt.types"
							:key="item.id"
							:label="item.name"
							:value="item.id"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="维度格式：" label-width="110px" prop="dateformat" v-if="isDateCol(dim)">
					<el-select
						v-model="dim.dateformat"
						placeholder="请选择"
						:clearable="true"
						style="width:100%;"
						>
						<el-option
							v-for="item in opt.dateformats"
							:key="item"
							:label="item"
							:value="item"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="维度Key字段：" label-width="110px" prop="colkey" >
					<el-select
						v-model="dim.colkey"
						placeholder="请选择"
						style="width:100%;"
						:clearable="true"
						>
						<el-option
							v-for="item in opt.cols"
							:key="item.id"
							:label="item.id+'('+item.name+')'"
							:value="item.id"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="维度Text字段：" label-width="110px" prop="coltext" >
					<el-select
						v-model="dim.coltext"
						placeholder="请选择"
						style="width:100%;"
						:clearable="true"
						>
						<el-option
							v-for="item in opt.cols"
							:key="item.id"
							:label="item.id+'('+item.name+')'"
							:value="item.id"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="维度排序：" label-width="110px" prop="dimord" >
					<el-select
						v-model="dim.dimord"
						placeholder="请选择"
						style="width:100%;"
						>
						<el-option
							v-for="item in opt.ords"
							:key="item.id"
							:label="item.name"
							:value="item.id"
						>
					</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="父子维度：" label-width="110px" prop="ispcdim">
					<el-radio size="mini"  v-model="dim.ispcdim" label="y" border>是</el-radio>
					<el-radio size="mini"  v-model="dim.ispcdim" label="n" border>否</el-radio>
				</el-form-item>

				<template v-if="dim.ispcdim==='y'">
					<el-form-item label="主键：" label-width="110px" prop="pcId" >
						<el-select
							v-model="dim.pcId"
							placeholder="请选择"
							style="width:100%;"
							:clearable="true"
							>
							<el-option
								v-for="item in opt.cols"
								:key="item.id"
								:label="item.id+'('+item.name+')'"
								:value="item.id"
							>
						</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="父键：" label-width="110px" prop="pcPid" >
						<el-select
							v-model="dim.pcPid"
							placeholder="请选择"
							style="width:100%;"
							:clearable="true"
							>
							<el-option
								v-for="item in opt.cols"
								:key="item.id"
								:label="item.id+'('+item.name+')'"
								:value="item.id"
							>
						</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="层级：" label-width="110px" prop="pcdimlevel" >
						<el-input-number v-model="dim.pcdimlevel" :min="1" :max="10" :precision="0" :step="1"></el-input-number>
					</el-form-item>
					<el-form-item label="级别字段：" label-width="110px" prop="levelCol" >
						<el-select
							v-model="dim.levelCol"
							placeholder="请选择"
							style="width:100%;"
							:clearable="true"
							>
							<el-option
								v-for="item in opt.cols"
								:key="item.id"
								:label="item.id+'('+item.name+')'"
								:value="item.id"
							>
						</el-option>
						</el-select>
						<span class="text-warning">(字段值从1开始)</span>
					</el-form-item>
				</template>
			  </template>
			  <template v-if="curDim.li_attr.tp === 'group'">
					<el-form-item label="分组名称：" label-width="110px" prop="groupname" >
						<el-input v-model="dim.groupname"></el-input>
					</el-form-item>
				</template>
				<template v-if="curDim.li_attr.tp === 'kpigroup'">
					<el-form-item label="度量分类：" label-width="110px" prop="kpigroup" >
						<el-input v-model="dim.kpigroup"></el-input>
					</el-form-item>
				</template>
		  </el-form>
		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, newGuid} from '@/common/biConfig'
	import $ from 'jquery'
	import {fmtJson, rates} from '@/view/portal/Utils'

	export default {
	    data(){
			return {
				show:false,
				title:"编辑维度或度量",
				dim:{
					dimname:null,
					dimtype:null,
					dateformat:null,
					colkey:null,
					coltext:null,
					dimord:null,
					ispcdim:'n',
					pcId:null,
					pcPid:null,
					pcdimlevel:null,
					levelCol:null,
					//指标相关
					kpiaggre:null,
					kpifmt:null,
					kpiunit:null,
					kpiname:null,
					kpinote:null,
					//维度分组
					groupname:null,
					//指标分类
					kpigroup:null,
					distinctCol: null,
				},
				curDim:{
					li_attr: {

					}
				}, //当前维度
				opt:{
					types:[
						{id:"frd",name:"默认"},
						{id:"year",name:"年"},
						{id:"halfyear",name:"半年"},
						{id:"quarter",name:"季度"},
						{id:"month",name:"月"},
						{id:"week",name:"周"},
						{id:"day",name:"日"},
						{id:"prov",name:"省份"},
						{id:"city",name:"地市"},
						{id:"town",name:"区县"},
						{id:"lonlat",name:"经纬度"},
					],
					dateformats:['yyyyMMdd', 'yyyy-MM-dd', 'yyyy年MM月dd日', 'yyyyMM', 'yyyy-MM', 'yyyy年MM月', 'yyyy', 'yyyy年'],
					cols:[],
					distinctCols: [],
					ords:[
						{id:"asc", name:"正序"},
						{id:"desc",name:"倒序"},
					],
					atp : ["sum","avg","count", "count(distinct)" , "max", "min"],
					fmtJson: fmtJson,
				},

				rule:{
					 dimname:[{ required: true, message: "必填", trigger: "blur" }],
					 kpiaggre:[{ required: true, message: "必填", trigger: "blur" }],
					 kpiname:[{ required: true, message: "必填", trigger: "blur" }],
					 groupname:[{ required: true, message: "必填", trigger: "blur" }],
					 kpigroup:[{ required: true, message: "必填", trigger: "blur" }],
					 distinctCol: [{ required: true, message: "必填", trigger: "blur" }],
				}
			}
		},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			save(){
				var ts = this;
				var right = this.curDim;
				var dim = this.dim;
				this.$refs["dimForm"].validate( (valid) => {
					if(valid){
						if(right.li_attr.tp == 'kpi'){
							right.li_attr.aggre = dim.kpiaggre;
							right.li_attr.fmt = dim.kpifmt;
							right.li_attr.unit = dim.kpiunit;
							right.li_attr.dispName = dim.kpiname;
							right.li_attr.kpinote = dim.kpinote;
							right.li_attr.distinctCol = dim.distinctCol;
							right.li_attr.isupdate = "y";  //表示指标已经更改过了。
							var rightRef = $("#cuberighttree").jstree(true);
							rightRef.rename_node(right.id, right.li_attr.aggre+ "(" + right.li_attr.dispName+")");
						}else if(right.li_attr.tp == 'dim'){
							//设置了时间类型维度后，必须设置维度格式
							var dtp = dim.dimtype;
							var dtfmt = dim.dateformat;
							if(dtp != "" && (dtp == "year" || dtp == "month" || dtp == "quarter" || dtp == "day") && dtfmt == "" ){
								ts.$notify.error("请选择时间维度格式。");
								return;
							}
							//处理父子维度
							var ispcdim = dim.ispcdim;
							var pcId = dim.pcId;
							var pcPid = dim.pcPid;
							var pcdimlevel = dim.pcdimlevel;
							var levelCol = dim.levelCol;
							if(ispcdim == 'y'){
								if(!dim.colkey || dim.colkey == ''){
									ts.$notify.error("父子维度必须设置 [维度Key字段]。");
									return;
								}
								if(!dim.coltext || dim.coltext == ''){
									ts.$notify.error("父子维度必须设置 [维度Text字段]。");
									return;
								}
								if(pcId == null || pcId == ''){
									ts.$notify.error("请设置父子维度的主键。");
									return;
								}
								if(pcPid == null || pcPid == ''){
									ts.$notify.error("请设置父子维度的父键。");
									return;
								}
								if(pcdimlevel == null){
									ts.$notify.error("请设置父子维度的层级。");
									return;
								}
							}
							if(ispcdim == 'y'){
								right.li_attr.ispcdim = 'y';
								right.li_attr.pcId = pcId;
								right.li_attr.pcPid = pcPid;
								right.li_attr.pcdimlevel = pcdimlevel;
								right.li_attr.levelCol = levelCol;
							}else{
								right.li_attr.ispcdim = 'n';
								delete right.li_attr.pcId;
								delete right.li_attr.pcPid;
								delete right.li_attr.pcdimlevel;
								delete right.li_attr.levelCol;
							}
							right.li_attr.dispName =dim.dimname;
							right.li_attr.dimtype = dtp;
							var colkey = dim.colkey;
							if(colkey != null && colkey.length > 0){
								var c = ts.opt.cols.filter(m=>m.id === colkey)[0];
								right.li_attr.colkey = c.id;
								right.li_attr.vtype = c.vtype;
							}else{
								right.li_attr.colkey = null;
								//vtype 恢复以前的
								var c = right.li_attr.fromCol;
								var leftRef = $("#cubelefttree").jstree(true);
								var n = leftRef.get_node(c);
								right.li_attr.vtype = n.li_attr.vtype;
							}
							right.li_attr.coltext = dim.coltext;
							right.li_attr.dimord = dim.dimord;
							right.li_attr.dateformat = dtfmt;
							right.li_attr.isupdate = "y";  //表示维度已经更改过了。
							var rightRef = $("#cuberighttree").jstree(true);
							rightRef.rename_node(right.id, dim.dimname);
						}else if(right.li_attr.tp == 'group'){
							right.li_attr.dispName =  dim.groupname;
							right.li_attr.isupdate = "y";  //表示分组已经更改过了。
							var rightRef = $("#cuberighttree").jstree(true);
							rightRef.rename_node(right, dim.groupname);
						}else if(right.li_attr.tp == 'kpigroup'){
							right.li_attr.dispName =  dim.kpigroup;
							right.li_attr.isupdate = "y";  //表示分组已经更改过了。
							var rightRef = $("#cuberighttree").jstree(true);
							rightRef.rename_node(right.id, dim.kpigroup);
						}
						ts.show = false;
					}
				});
			},
			showDailog(curDim, cols){
				this.show = true;
				this.curDim = curDim;
				this.opt.cols = cols;
				this.opt.distinctCols = cols;
				if(this.$refs["dimForm"]){
					this.$refs["dimForm"].clearValidate();
				}
				var tp = curDim.li_attr.tp;
				//回写值
				var d = this.dim;
				if(tp === 'dim'){
					d.dimname = curDim.li_attr.dispName;
					d.ispcdim = curDim.li_attr.ispcdim?curDim.li_attr.ispcdim:"n";
					d.pcId = curDim.li_attr.pcId;
					d.pcPid = curDim.li_attr.pcPid;
					d.pcdimlevel = curDim.li_attr.pcdimlevel;
					d.levelCol = curDim.li_attr.levelCol;
					d.dimtype = curDim.li_attr.dimtype;
					d.colkey = curDim.li_attr.colkey;
					d.vtype = curDim.li_attr.vtype;
					d.coltext = curDim.li_attr.coltext;
					d.dimord = curDim.li_attr.dimord;
					d.dateformat = curDim.li_attr.dateformat;
					this.title = "编辑维度";
				}

				//指标
				if(tp === 'kpi'){
					d.kpiaggre = curDim.li_attr.aggre;
					d.kpifmt = curDim.li_attr.fmt;
					d.kpiunit = curDim.li_attr.unit;
					d.kpiname = curDim.li_attr.dispName;
					d.kpinote = curDim.li_attr.kpinote;
					d.distinctCol = curDim.li_attr.distinctCol;
					this.title = "编辑度量";
				}
				if(tp === 'group'){
					d.groupname = curDim.li_attr.dispName;
					this.title = "编辑分组";
				}
				if(tp === 'kpigroup'){
					d.kpigroup = curDim.li_attr.dispName;
					this.title = "编辑分组";
				}
			},
			isDateCol(dim){
				if(dim.dimtype==='year'||
				dim.dimtype==='month'||
				dim.dimtype==='quarter' ||
				dim.dimtype==='day' ||
				dim.dimtype==='halfyear' ||
				dim.dimtype==='week' ){
					return true;
				}else{
					return false;
				}
			}
		},
		watch: {
		}
	}
</script>
