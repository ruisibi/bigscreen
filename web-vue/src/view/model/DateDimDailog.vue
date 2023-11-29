<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<el-dialog title="创建时间维度" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		  <template slot="title">
				<span class="el-dialog__title">创建时间维度</span>
				<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
				<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
				</button>
			</template>
		  <p class="text-warning">根据您数据表中的时间字段自动创建分析维度。</p>
		  <el-form :model="dateDim" :rules="rule" ref="dateDimForm" size="mini" label-position="left">
			<el-form-item label="时间列" label-width="110px" prop="timecol" style="100%">
				<el-select
					v-model="dateDim.timecol"
					placeholder="请选择"
					>
					<el-option
						v-for="item in opt.cols"
						:key="item.colName"
						:label="item.colName+'('+(item.colNote?item.colNote:item.colName)+')'"
						:value="item.colName"
					>
				</el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="分析维度" label-width="110px" prop="dims">
				<el-checkbox-group v-model="dateDim.dims">
					<template v-for="item in opt.dims">
						<el-checkbox :key="item.value" :label="item.value">{{item.desc}}</el-checkbox>
					</template>
				</el-checkbox-group>
			</el-form-item>
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

	export default {
	    data(){
			return {
				show:false,
				dateDim:{
					timecol:null,
					dims:["year", "month", "day"]  //时间维度
				},
				opt:{
					cols:[],
					dims:[
						{value:"year", desc:"年", fmt:"yyyy", dbfmt1:"%Y", dbfmt2:"yyyy"},
						{value:"month", desc:"月份", fmt:"yyyy-MM", dbfmt1:"%Y-%m", dbfmt2:"yyyy-MM"},
						{value:"day", desc:"日期", fmt:"yyyy-MM-dd", dbfmt1:"%Y-%m-%d", dbfmt2:"yyyy-MM-dd"},
					]
				},
				tid:null,
				rule:{
					timecol:[{ required: true, message: "必填", trigger: "blur" }],
					dims:[{ required: true, message: "必须选一个", trigger: "blur" }],
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
				this.$refs["dateDimForm"].validate( (valid) => {
					if(valid){
						var timecol = ts.dateDim.timecol;
						var tps = [];
						$(ts.dateDim.dims).each(function(a, b){
							let o = ts.opt.dims.filter(m=>m.value === b)[0];
							tps.push({key:o.value, text:o.desc, fmt:o.fmt, dbfmt1:o.dbfmt1, dbfmt2:o.dbfmt2});
						});

						var execf = function(dbname){
							//创建分组
							var gid = newGuid();
							var dt = {id:gid,text:"时间", "icon":"fa fa-tasks", state:{opened:true}, li_attr:{tp:'group',dispName:"时间",drag:true}};
							var rightRef = $("#cuberighttree").jstree(true);
							rightRef.create_node('cubewd', dt);
							for(let k=0; k<tps.length; k++){
								var mcid = ts.$parent.findCubeMaxId();
								var cid = mcid.id + k;
								var aliasId = mcid.aliasId + k;var name = tps[k].text;
								var cc = "";
								if("mysql" == dbname){
									cc = "DATE_FORMAT("+timecol+",'"+tps[k].dbfmt1+"')";
								}else if("clickhouse" == dbname){
									cc = "formatDateTime("+timecol+",'"+tps[k].dbfmt1+"')";
								}else if("oracle" == dbname || "dm" == dbname){
									cc = "to_char("+timecol+",'"+tps[k].dbfmt2+"')";
								}else if("es" == dbname){
									cc = "date_histogram("+timecol+", '"+tps[k].key+"', '"+tps[k].dbfmt2+"')";
								}else if("psql" == dbname){
									cc = "to_char("+timecol+",'"+tps[k].dbfmt2+"')";
								}else if("sqlser" === dbname){
									var t = tps[k].key;
									if(t === 'year'){
										cc = "datename(yy, "+timecol+")";
									}else if(t === 'month'){
										cc = "datename(yy, "+timecol+") + '-' + datename(mm,"+timecol+")";
									}else if(t === 'day'){
										cc = "convert(varchar(10), "+timecol+", 120)";
									}
								}
								/**
								if(tps.key == "quarter"){
									cc = "CONCAT(YEAR("+timecol+"),'-',quarter("+timecol+"))";
								}
								**/
								var o = {id:cid, text:name, li_attr:{tp:"dim",drag:true,col:cc,dispName:name,colNote:name,tname:"",dimtype:tps[k].key,dateformat:tps[k].fmt,dimord:"asc",vtype:"String",alias:"k"+aliasId,fromCol:timecol},icon:"glyphicon glyphicon-stop icon_dim", targetId:""};
								rightRef.create_node(gid, o);
							}
							if(rightRef.is_closed('cubewd')){
								rightRef.open_node('cubewd');
							}
							ts.show = false;
						}
						ajax({
							url:"etl/getDBName.action",
							data:{tid:this.tid},
							success:(resp)=>{
								execf(resp.rows);
							}
						}, this);
					}
				});
			},
			showDailog(cols, tid){
				this.tid = tid;
				this.show = true;
				if(this.$refs["dateDimForm"]){
					this.$refs["dateDimForm"].resetFields();
				}
				this.timecol = null;
				this.dims = ["year", "month", "day"];  //时间维度

				var rightRef = $("#cuberighttree").jstree(true);
				var allnodes = [];
				var exec = function(node){
					var nodes = node.children;
					if(!nodes){
						return;
					}
					$(nodes).each(function(a, b){
						var tnode = rightRef.get_node(b);
						allnodes.push(tnode);
						exec(tnode);
					});
				}
				exec(rightRef.get_node('cubewd'));
				var colUsefun = function(c){
					var cld = allnodes;
					for(let l=0;l<cld.length;l++){
						if(cld[l].li_attr && cld[l].li_attr.fromCol == c){
							return true;
						}
					}
					return false;
				};

				var r = [];
				for(let i=0; i<cols.length; i++){
					var k = cols[i];
					if((k.colType == "Datetime" || k.colType=="Date") && !colUsefun(k.colName)){
						//c = c + "<option value=\""+k.colName+"\">"+(k.colNote?k.colNote:k.colName)+"</option> ";
						r.push(k);
					}
				}
				this.opt.cols = r;
			}
		},
		watch: {
		}
	}
</script>
