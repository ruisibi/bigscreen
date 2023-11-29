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
		<div class="">
			<el-form :model="param" :rules="rules" ref="paramForm" size="mini">
				<el-form-item label="参数标识：" label-width="100px" prop="paramid">
					<el-input v-model="param.paramid" ></el-input>
				</el-form-item>
				<el-form-item label="参数名称：" label-width="100px" prop="paramname">
					<el-input v-model="param.paramname"></el-input>
				</el-form-item>
				<el-form-item label="参数类型：" label-width="100px" prop="type">
					<el-select style="width:100%" v-model="param.type" placeholder="请选择" >
						<el-option v-for="item in opts.types" :key="item.value" :label="item.label" :value="item.value">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="时间格式：" label-width="100px" prop="format" v-if="param.type =='onedate' || param.type=='datetime' ">
					<el-select style="width:100%" v-model="param.format" placeholder="请选择" >
						<el-option v-for="item in opts.foramts" :key="item.value" :label="item.label" :value="item.value">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="默认值：" label-width="100px" prop="defvaltype">
					 <el-radio-group v-model="param.defvaltype" size="mini">
						<el-radio label="static" border="">静态值</el-radio>
						<!--
						<el-radio label="dtz" border="">动态值</el-radio>
						-->
					</el-radio-group>
          <el-row v-if="param.defvaltype === 'static'">
            <el-col :span="11">
              <el-input v-model="param.defvalue" ></el-input>
            </el-col>
            <el-col :span="2" align="center" v-if="param.type=='datetime'">
              至
            </el-col>
            <el-col :span="11" v-if="param.type=='datetime'">
              <el-input v-model="param.defvalue2" ></el-input>
            </el-col>
          </el-row>
					<el-row v-if="param.defvaltype === 'dtz'">
						<el-col :span="12">
							<el-select style="width:100%" @change="loadCols(true)" v-model="param.defvalue_table_id" :clearable="true" placeholder="请选择" >
								<el-option v-for="item in opts.tables" :key="item.tableId" :label="item.tableName+'('+item.tableNote+')'" :value="item.tableId"/>
							</el-select>
						</el-col>
						<el-col :span="12">
							<el-select style="width:100%" v-model="param.defvalue_table_col" :clearable="true" placeholder="请选择" >
								<el-option v-for="item in opts.cols" :key="item.colName" :label="item.colName" :value="item.colName"/>
							</el-select>
						</el-col>
					</el-row>
				</el-form-item>
				<el-form-item label="" label-width="100px" v-if="param.type == 'select' || param.type ==='mselect'">
					<el-switch v-model="param.defFirstValue"></el-switch> 第一行设为下拉框的默认值
				</el-form-item>
				<!--
				<el-form-item label="级联上级：" label-width="100px" v-if="param.type == 'select' || param.type ==='mselect'">
					<el-select v-model="param.cascade" :clearable="true" placeholder="级联上级参数，支持单选、多选框" style="width:100%">
						<el-option
						v-for="item in opts.parentParams"
						:key="item.id"
						:label="item.name"
						:value="item.id">
						</el-option>
					</el-select>
				</el-form-item>
				-->
				<el-form-item label="最小值：" label-width="100px" v-if="param.type =='onedate' || param.type=='datetime' ">
					<el-input v-model="param.minval"></el-input>
				</el-form-item>
				<el-form-item label="最大值：" label-width="100px" v-if="param.type =='onedate' || param.type=='datetime' ">
					<el-input v-model="param.maxval" ></el-input>
				</el-form-item>
				<!--
				<el-form-item label="隐藏参数：" label-width="100px" prop="hiddenprm">
					<el-switch v-model="param.hiddenprm"></el-switch>
					<span class="text-warning">
					隐藏参数不会在页面中显示
					</span>
				</el-form-item>
				-->
				<template v-if="param.type == 'select' || param.type ==='mselect'">
            <fieldset>
                <legend>值列表</legend>
                <el-radio v-model="param.valtype" label="static">静态值</el-radio>
                <el-radio v-model="param.valtype" label="dynamic">动态值</el-radio>
                <template v-if="param.valtype === 'static'">
                  <div><el-button @click="addStaticVal(false)" size="small">添加</el-button><el-button @click="addMutliVal()" size="small">批量添加</el-button><el-button @click="clearDatas()" size="small">清空</el-button></div>
                   <el-table :data="param.values" style="width: 100%" border header-row-class-name="tableHeadbg">
                    <el-table-column
                      prop="value"
                      label="Value">
                    </el-table-column>
                    <el-table-column
                      prop="text"
                      label="Text">
                    </el-table-column>
                   <el-table-column
                    fixed="right"
                    label="操作">
                    <template slot-scope="scope">
					<el-button @click="addStaticVal(true, scope.row.value)" type="text" size="small">编辑</el-button>
                      <el-button @click="deleteStaticVal(scope.row.value)" type="text" size="small">删除</el-button>
                    </template>
                  </el-table-column>
                  </el-table>
                </template>
                <template v-if="param.valtype === 'dynamic'">
                   <el-form-item label="数据表：" label-width="150px">
                    <el-select v-model="param.dataset" @change="chgtable(true)" filterable placeholder="请选择" style="width:100%">
                      <el-option
                      v-for="item in opts.datasetlist"
                      :key="item.tableId"
                      :label="item.tableName"
                      :value="item.tableId">
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="映射Value字段：" label-width="150px" >
                    <el-select v-model="param.param_option_val" placeholder="请选择" style="width:100%">
                      <el-option
                      v-for="item in opts.collist"
                      :key="item.colName"
                      :label="item.colName+'('+(item.colNote?item.colNote:item.colName)+')'"
                      :value="item.colName">
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="映射Text字段：" label-width="150px" >
                    <el-select v-model="param.param_option_txt" placeholder="请选择" style="width:100%">
                      <el-option
                      v-for="item in opts.collist"
                      :key="item.colName"
                      :label="item.colName+'('+(item.colNote?item.colNote:item.colName)+')'"
                      :value="item.colName">
                      </el-option>
                    </el-select>
                  </el-form-item>
				  <el-form-item label="排序：" label-width="150px" >
                    <el-select v-model="param.param_order" :clearable="true" placeholder="对value字段进行排序" style="width:100%">
                      <el-option
                      v-for="item in opts.orders"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                      </el-option>
                    </el-select>
                  </el-form-item>
                </template>
            </fieldset>
            </template>
			<template v-if="param.type==='tree'">
              <fieldset>
                <legend>值映射</legend>
              </fieldset>
              <el-form-item label="选择表：" label-width="120px" prop="matchTable">
                  <el-select v-model="param.matchTable" placeholder="请选择表" style="width:100%" filterable @change="selectMatchCols(true)">
                    <el-option
                    v-for="item in opts.matchTables"
                    :key="item.tableId"
                    :label="item.tableName"
                    :value="item.tableId">
                    </el-option>
                  </el-select>
              </el-form-item>
              <el-form-item label="ID字段：" label-width="120px" prop="tableIdCol">
                  <el-select v-model="param.tableIdCol" placeholder="请选择字段" style="width:100%">
                    <el-option
                    v-for="item in opts.matchTableCols"
                    :key="item.colId"
                    :label="item.colName"
                    :value="item.colName">
                    </el-option>
                  </el-select>
              </el-form-item>
              <el-form-item label="Pid字段：" label-width="120px" prop="tablePidCol">
                  <el-select v-model="param.tablePidCol" placeholder="请选择字段" style="width:100%">
                    <el-option
                    v-for="item in opts.matchTableCols"
                    :key="item.colId"
                    :label="item.colName"
                    :value="item.colName">
                    </el-option>
                  </el-select>
              </el-form-item>
              <el-form-item label="名称字段：" label-width="120px" prop="tableNameCol">
                  <el-select v-model="param.tableNameCol" placeholder="请选择字段" style="width:100%">
                    <el-option
                    v-for="item in opts.matchTableCols"
                    :key="item.colId"
                    :label="item.colName"
                    :value="item.colName">
                    </el-option>
                  </el-select>
              </el-form-item>
            </template>
			</el-form>
		</div>
		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>

		<el-dialog width="30%" :title="valaddtitle"
        :visible.sync="innerVisible" :close-on-click-modal="false" custom-class="nopadding"
        append-to-body>
			<el-form :model="pval" ref="pvalForm" :rules="pvalrules" size="mini" label-position="left">
				<el-form-item label="Value" label-width="100px" prop="value">
				<el-input v-model="pval.value"></el-input>
				</el-form-item>
				<el-form-item label="Text" label-width="100px" prop="text">
				<el-input v-model="pval.text"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="addValueSave()">确 定</el-button>
				<el-button @click="innerVisible = false">取 消</el-button>
			</div>
      </el-dialog>

	  <el-dialog width="40%" title="批量添加值"
        :visible.sync="innerMultiVisible" :close-on-click-modal="false" custom-class="nopadding"
        append-to-body>
			<el-form :model="multi" ref="multiForm" :rules="multirules" size="mini" label-position="left">
				<el-form-item label="选择表：" label-width="120px" prop="tableId">
                    <el-select v-model="multi.tableId" placeholder="请选择" @change="chgmtable()" style="width:100%">
                      <el-option
                      v-for="item in opts.datasetlist"
                      :key="item.tableId"
                      :label="item.tableName"
                      :value="item.tableId">
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="Value字段：" label-width="120px" prop="tableValue">
                    <el-select v-model="multi.tableValue" placeholder="请选择" style="width:100%">
                      <el-option
                      v-for="item in opts.mcols"
                      :key="item.colName"
                      :label="item.colName"
                      :value="item.colName">
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="Text字段：" label-width="120px" prop="tableText">
                    <el-select v-model="multi.tableText" placeholder="请选择" style="width:100%">
                      <el-option
                      v-for="item in opts.mcols"
                      :key="item.colName"
                      :label="item.colName"
                      :value="item.colName">
                      </el-option>
                    </el-select>
                  </el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="multiSave()">确 定</el-button>
				<el-button @click="innerMultiVisible = false">取 消</el-button>
			</div>
      </el-dialog>

	  <el-dialog width="40%" title="选择值"
        :visible.sync="innerMulti2Visible" :close-on-click-modal="false" custom-class="nopadding"
        append-to-body>
			<div class="el-dialog-div">
				<div id="valuesTree"></div>
			</div>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="multiSave2()">确 定</el-button>
				<el-button @click="innerMulti2Visible = false">取 消</el-button>
			</div>
      </el-dialog>
	</el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {baseUrl, ajax, newGuid} from '@/common/biConfig'
	import $ from 'jquery'
	import * as bsUtils from '@/view/bigscreen/bsUtils'

	export default {
	    data(){
			return {
				show:false,
				title:null,
				param:{
					paramid:null,
					paramname:null,
					type:"select",
					defvaltype:"static",
					defvalue:null,
          defvalue2:null,
					defvalue_table_id:null,
					defvalue_table_col:null,
					hiddenprm:false,
					format:null,
					valtype:"static",
					 values:[],  //固定值值列表
					 dataset:null,
					 param_option_val:null,
					 param_option_txt:null,
					 param_order: null,
					 minval:null,
					 maxval:null,
					 cascade: null, //及联上级参数
					 /**
					 * 以下是树形参数字段
					 */
					matchTable:null,
					tableIdCol:null,
					tablePidCol:null,
					tableNameCol:null,
					defFirstValue:null,  //下拉框第一行做参数默认值
				},
				curparam : null,
				rules:{
					paramid: [{ required: true, message: "必填", trigger: "blur" }],
					paramname: [{ required: true, message: "必填", trigger: "blur" }],
					type: [{ required: true, message: "必填", trigger: "blur" }],
					format: [{ required: true, message: "必填", trigger: "blur" }],
					matchTable: [{ required: true, message: "必填", trigger: "blur" }],
					tableIdCol: [{ required: true, message: "必填", trigger: "blur" }],
					tablePidCol: [{ required: true, message: "必填", trigger: "blur" }],
					tableNameCol: [{ required: true, message: "必填", trigger: "blur" }],
				},
				opts:{
					types:bsUtils.paramTypes,
					tables:[],
					 foramts:[{label:"yyyy", value:"yyyy"},{label:"yyyy-MM", value:"yyyy-MM"},{label:"yyyyMM", value:"yyyyMM"},{label:"yyyy-MM-dd", value:"yyyy-MM-dd"},{label:"yyyyMMdd", value:"yyyyMMdd"}, {label:"yyyyMMddHHmmss",value:"yyyyMMddHHmmss"}, {label:"yyyy-MM-dd HH:mm:ss", value:"yyyy-MM-dd HH:mm:ss"}],
					 datasetlist:[],
					 collist:[],
					 mcols:[],
					 cols:[],
					 parentParams:[], //及联时的上级参数列表
					 matchTables:[],
					 orders:[{value:"asc", label:"升序"},{value:"desc", label:"降序"}], //排序

				},
				isvalupdate:false,
				pval:{
					value:null,
					text:null
				},
				pvalrules:{
					value:[{ required: true, message: '必填', trigger: 'blur' }],
					text:[{ required: true, message: '必填', trigger: 'blur' }]
				},
				valaddtitle:null,
				innerVisible:false,
				innerMultiVisible:false,
				innerMulti2Visible:false,
				multi:{
					tableId:null,
					tableValue:null,
					tableText:null,
				},
				multirules:{
					tableId:[{ required: true, message: '必填', trigger: 'blur' }],
					tableValue:[{ required: true, message: '必填', trigger: 'blur' }],
					tableText:[{ required: true, message: '必填', trigger: 'blur' }],
				},
			}
		},
		props: {
			pageInfo: {
				type: Object,
				required: true,
				default:{}
			}
		},
		components: {

    	},
		mounted(){

		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			showDailog(item){
				this.show = true;
				this.title = "配置参数";
				this.loadTables();
				//回写值
				this.curparam = item;
				let p = this.param;
				if(item.paramid){
					p.paramid = item.paramid
				}else{
					p.paramid = "p"+Math.floor(Math.random() * 10000 );
				}
				p.paramname = item.name,
				p.maskingValue = item.maskingValue,
				p.type = item.type,
				p.defvaltype = item.defvaltype,
				p.defvalue = item.defvalue,
				p.defvalue2 = item.defvalue2,
				p.defvalue_table_id = item.defvalueTableId,
				p.defvalue_table_col = item.defvalueTableCol,
				p.hiddenprm = item.hiddenprm,
				p.format = item.format,
				p.valtype = item.valtype,
					p.values = item.values,  //固定值值列表
					p.dataset = item.tid,
					p.param_option_val = item.value,
					p.param_option_txt = item.text;
					p.param_order = item.order;
					p.maxval = item.maxval;
					p.minval = item.minval;
					p.cascade = item.cascade;
					p.matchTable = item.matchTable,
					p.tableIdCol = item.tableIdCol;
					p.tablePidCol = item.tablePidCol;
					p.tableNameCol  = item.tableNameCol;
					p.defFirstValue = item.defFirstValue;
					if(p.dataset){
					this.chgtable(false);
					}
					if(p.defvalue_table_id){
						this.loadCols(false);
					}
					if(p.matchTable){
						this.selectMatchCols(false);
					}

				//及联时上级参数列表
				let ps = [];
				var pms = this.pageInfo.globalParams
				for(let i=0; pms&&i<pms.length; i++){
					if(item && item.id == pms[i].id){  //不能级联自己
						continue;
					}
					if(pms[i].type == 'select' || pms[i].type == 'mselect'){
						ps.push({id:pms[i].id,name:pms[i].name});
					}
				}
				this.opts.parentParams = ps;
				this.setUpdate();
			},
			loadTables(){
				ajax({
					type:"POST",
					url:"etl/loadTable.action",
					dataType:'JSON',
					data: {rows:2000},
					success: (resp) => {
						this.opts.tables = resp.rows;
						this.opts.datasetlist = resp.rows;
						this.opts.matchTables = resp.rows;
					}
				}, this);
			},
			chgtable(clearval){
				if(clearval==true){
					this.param.param_option_val = null;
					this.param.param_option_txt = null;
				}
				ajax({
					type: "post",
					url: "etl/getTableColumns.action",
					data: {
						tableId: this.param.dataset
					},
					dataType: "json",
					success: (resp) => {
						this.opts.collist = resp.rows;
					}
				}, this);
			},
			loadCols(clearval){
				if(clearval==true){
					this.param.defvalue_table_col = null;
				}
				ajax({
					type: "post",
					url: "etl/getTableColumns.action",
					data: {
						tableId: this.param.defvalue_table_id
					},
					dataType: "json",
					success: (resp) => {
						this.opts.cols = resp.rows;
					}
				}, this);
			},
			selectMatchCols(clearval){
				if(clearval==true){
					this.param.tableIdCol = null;
					this.param.tablePidCol = null;
					this.param.tableNameCol = null;
				}
				//let t = this.opts.matchTables.filter(m=>m.tableId === this.param.matchTable)[0];
				ajax({
					type:"post",
					url:"etl/getTableColumns.action",
					data: {tableId:this.param.matchTable},
					dataType:"json",
					success: (resp)=>{
						this.opts.matchTableCols = resp.rows;
						this.$forceUpdate();
					}
				}, this);
			},
			//保存参数
			save(){
				this.$refs['paramForm'].validate((valid) => {
					if(valid==true){
						var paramid = this.param.paramid;
						var name = this.param.paramname;
						var maskingValue = this.param.maskingValue;
						var hiddenprm = this.param.hiddenprm;
						var defvaltype = this.param.defvaltype;
						var defvalue =  this.param.defvalue;
            			var defvalue2 = this.param.defvalue2;
						var defvalueTableId = this.param.defvalue_table_id;
						var defvalueTableCol =this.param.defvalue_table_col;
						var type = this.param.type;
						var r =this.param.valtype;
						var format = this.param.format;
						var maxval = this.param.maxval;
						var minval = this.param.minval;
						var cascade = this.param.cascade;
						var matchTable = this.param.matchTable;
						var tableIdCol = this.param.tableIdCol;
						var tablePidCol = this.param.tablePidCol;
						var tableNameCol = this.param.tableNameCol;
						var defFirstValue = this.param.defFirstValue;
						let p = this.param;
						if(type === "select") {
							if (r == 'static') {
								if (!p.values || p.values.length == 0) {
									this.$notify.error("您还未设置参数值。");
									return;
								}
							} else {
								if (!p.dataset || !p.param_option_val || !p.param_option_txt) {
									this.$notify.error("您的参数还未绑定到数据。");
									return;
								}
							}
						}
						const exedel = function(o){
							var type = o.type;
							if(type === "select" || type === 'mselect') {  //选择框
								if (o.valtype === 'dynamic') {
									var val = p.param_option_val;
									var txt = p.param_option_txt;
									var tid = p.dataset;
									var order = p.param_order;
									o.value = val;
									o.text = txt;
									o.tid = tid;
									o.order = order;
								} else {
									o.values = p.values;
								}
							}else{
								delete o.values, delete o.value, delete o.text, delete o.tid;
							}
							if(!(type == 'datetime' || type ==='onedate')){
								delete o.format;
							}
						}
						const loaddynaVal = (p) => {
							if(p.defvalueTableId && p.defvalueTableCol && p.defvaltype == 'dtz'){
								var q = {paramId:p.id, tid:p.defvalueTableId, col:p.defvalueTableCol};
								ajax({
									url:"dashboard/queryOneDefvalue.action",
									data:JSON.stringify(q),
									postJSON:true,
									type:"POST",
									success: (resp) => {
										p.defvalue = resp.rows;  //把参数默认值给 defvals对象
									}
								});
							}
						}
						let param = this.curparam;
						param.paramid = paramid;
						param.name = name;
						param.maskingValue = maskingValue;
						param.defvalue = defvalue;
						param.defvalue2 = defvalue2;
						param.defvaltype = defvaltype;
						param.defvalueTableId = defvalueTableId;
						param.defvalueTableCol = defvalueTableCol;
						param.valtype = r;
						param.type = type;
						param.format = format;
						param.hiddenprm = hiddenprm;
						param.maxval = maxval;
						param.minval = minval;
						param.cascade = cascade;
						param.matchTable = matchTable;
						param.tableIdCol = tableIdCol;
						param.tablePidCol = tablePidCol;
						param.tableNameCol = tableNameCol;
						param.defFirstValue = defFirstValue;
						exedel(param);
						loaddynaVal(param);
						let o = this.$parent.$refs['mv_' + this.curparam.id];
						o.$forceUpdate();
						this.setUpdate();
						this.show = false;
					}
				});
			},
			setUpdate(){
				this.$parent.setUpdate();
			},
			addStaticVal(isupdate, id){
				this.innerVisible = true;
				this.isvalupdate = isupdate;
				this.tmpid = id;
				if(this.$refs['pvalForm']){
					this.$refs['pvalForm'].resetFields();
				}
				if(isupdate){
					this.valaddtitle = "修改值";
					let ts = this;
					$(this.param.values).each((a, b)=>{
						if(b.value === id){
						ts.pval.value = b.value;
						ts.pval.text = b.text;
						return false;
						}
					});
				}else{
					this.valaddtitle = "添加值";
				}
			},
			addValueSave(){
				let ts = this;
				this.$refs['pvalForm'].validate((valid) => {
					if(valid){
						ts.innerVisible = false;
						if(this.isvalupdate === true){
							let row = ts.param.values.filter(m=>m.value === this.tmpid)[0];
							row.value = ts.pval.value;
							row.text = ts.pval.text;
						}else{
							if(!ts.param.values){
								ts.param.values = [];
							}
							ts.param.values.push({value:ts.pval.value, text:ts.pval.text});
						}

					}
				});
			},
			deleteStaticVal(id){
				$(this.param.values).each((a, b)=>{
					if(b.value === id){
						this.param.values.splice(a, 1);
						return false;
					}
				});
			},
			addMutliVal(){
				this.innerMultiVisible = true;
				this.multi.tableId = null;
				this.multi.tableText = null;
				this.multi.tableValue = null;
			},
			clearDatas(){
				this.param.values = [];
			},
			multiSave(){
				this.$refs['multiForm'].validate((valid) => {
					if(valid){
						this.innerMultiVisible = false;
						let val = this.multi.tableValue;
						let txt = this.multi.tableText;
						ajax({
							type: "GET",
							url: "etl/queryTableDataAsJson.action",
							data: {
								tableId: this.multi.tableId,
								text: txt,
								value: val,
								token:"",
							},
							success:(resp) => {
								var dt = [];
								$(resp.rows).each(function (a, b) {
									dt.push({id: b[val.toLowerCase()] , text: b[txt.toLowerCase()], icon:'fa fa-tag' });
								});
								this.innerMulti2Visible = true;
								this.$nextTick(()=>{
									let ref = $("#valuesTree").jstree(true);
									if(ref){
										ref.destroy();
									}
									$("#valuesTree").jstree({
										core: {
											check_callback: function check_callback(operation, source, node_parent, node_position, more) {
												if (operation === 'move_node') {
													//控制 dnd
													var point = node_position == 0 ? "append" : "";
													if (point === "append") {
														return false;
													}
													return true;
												} else {
													return true;
												}
											},
											dblclick_toggle: false,
											data: dt
										},
										dnd: {
											large_drop_target: 'selected',
											large_drag_target: 'selected'
										},
										"plugins": ["wholerow", "checkbox", "dnd"]
									});
								});
							}
						}, this);
					}
				});
			},
			multiSave2(){
				var ref = $("#valuesTree").jstree(true);
				var selecs = ref.get_selected(true);
				this.param.values = selecs.map(function(o){
					return {value:o.id, text:o.text};
				});
				this.innerMulti2Visible = false;
			},
			chgmtable(){
				this.multi.tableText = null;
				this.multi.tableValue = null;
				ajax({
					type: "post",
					url: "etl/getTableColumns.action",
					data: {
						tableId: this.multi.tableId
					},
					dataType: "json",
					success: (resp) => {
						this.opts.mcols = resp.rows;
					}
				}, this);
			},
		},
		watch: {
			show:function(a){
				if(a == false){
					this.$nextTick(()=>{
						window.setTimeout(()=>this.$parent.showParamAddForm = false, 300);
					});
				}
			}
		}
	}
</script>

<style lang="less" scoped>
.todo-list {
    list-style: none outside none;
    margin: 0;
    padding: 0;
    font-size: 14px;
}
.compopt {
	float:right;
}
.todo-list.small-list > li {
    border-left: none;
    border-right: none;
    border-radius: 4px;
    color: inherit;
    margin-bottom: 2px;
    padding: 6px 6px 6px 12px;
}

.todo-list.small-list > li:hover {
	background-color:#f0f3f4;
}
</style>
