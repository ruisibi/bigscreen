<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 动态字段添加/编辑对话框 -->
<template>
  	<el-dialog :title="title" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding" @closed="clearData">
		  <template slot="title">
			<span class="el-dialog__title">{{title}}</span>
			<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
				<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
			</button>
		  </template>
		  <el-form :model="col" ref="colForm" :rules="rules"  label-position="left" size="mini">
			<el-form-item label="字段名：" label-width="120px" prop="colname">
				<el-input v-model="col.colname"  :disabled="isupdate" placeholder="英文字符，添加后不可修改"></el-input>
			</el-form-item>
			<el-form-item label="备注：" label-width="120px" prop="note">
				<el-input v-model="col.note"></el-input>
			</el-form-item>
			<el-form-item label="字段类型：" label-width="120px" prop="coltype">
				<el-select style="width:100%"
					v-model="col.coltype"
					placeholder="请选择"
					>
					<el-option
						v-for="item in opt.types"
						:key="item.value"
						:label="item.text"
						:value="item.value"
					>
				</el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="表达式：" label-width="120px" prop="expression">
				<label slot="label">表达式 <a @click="helpbdsinfo" href="javascript:;"><i class="fa fa-question-circle"></i></a>：</label>
				<el-row>
					<el-col :span="24" class="colexp">
							<codemirror :options="cmCfg" ref="mysqlcode" v-model="col.expression"></codemirror>
					</el-col>

				</el-row>
			</el-form-item>
			 <div style="line-height:25px;">
				<template v-for="item in cols">
					<button  @click="selectCol(item.colName)" :key="item.colId" style="margin-right:5px;" type="button" class="btn btn-primary btn-xs">{{item.colName}}</button>
				</template>
			</div>
		  </el-form>

		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>

		 <el-dialog custom-class="nopadding" width="50%" title="表达式说明" :visible.sync="innerVisible" append-to-body>
				<div style="line-height:30px;">表达式是一句SQL片段，用来提高系统灵活性。<li>通过表达式来对字段进行运算；</li>字段相加：<div class="mycode"> a+ b</div><li>对字段进行case when 转换；</li><div class="mycode">case when a=1 then '是' when a=2 then '否' else '未知' end</div><p class="text-warning">请注意：此处表达式请勿使用sum/avg/max/min/count等聚合函数。</p><p></p></div>
			<div slot="footer" class="dialog-footer">
				<el-button @click="innerVisible = false">关 闭</el-button>
			</div>
		</el-dialog>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, insertText2focus} from '@/common/biConfig'
	import $ from 'jquery'
	import * as utils from "@/view/etl/EtlUtils.js";
	import { codemirror } from 'vue-codemirror'
	require("codemirror/mode/sql/sql.js")

	export default {
	    data(){
			var checkCol = (rule, value, callback)=>{
				if(utils.ischinese(value)){
					return callback(new Error('字段名必须是英文字符'));
				}else if(utils.containSpecial(value)){
					return callback(new Error('字段名包含特殊字符'));
				}else{
					callback();
				}
			};
			return {
				show:false,
				title:null,
				isupdate:false,
				innerVisible:false,
				col:{
					colId:null,
					coltype:null,
					colname:null,
					note:null,
					expression:null,
					tableId:null,
					tableName:null
				},
				opt:{
					types:[
						{value:"String", text:"字符串"},
						{value:"Int", text:"整数"},
						{value:"Double", text:"小数"},
						{value:"Date", text:"日期"},
						{value:"Datetime", text:"时间"}
					]
				},
				rules:{
					colname:[{ required: true, message: "必填", trigger: "blur" }, { validator: checkCol, trigger: 'blur' }],
					coltype:[{ required: true, message: "必填", trigger: "blur" }],
					expression:[{ required: true, message: "必填", trigger: "blur" }],
				},
				cols:[],
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
			callback: {
				type: Function,
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
			add(tableId, tableName, cols){
				this.show = true;
				this.col.tableId = tableId;
				this.col.tableName = tableName;
				this.col.expression = "";
				this.title = "添加动态字段";
				this.cols = cols;
				this.isupdate = false;
			},
			modify(tableId, tableName, col, cols){
				this.show = true;
				this.title = "编辑动态字段";
				this.col.tableId = tableId;
				this.col.tableName = tableName;
				this.col.colId = col.colId;
				this.col.coltype = col.colType;
				this.col.colname = col.colName;
				this.col.expression = col.expression || "";
				this.col.note = col.colNote;
				this.cols = cols;
				this.isupdate = true;
			},
			save(){
				var ts = this;
				this.$refs["colForm"].validate( (valid) => {
					if(valid){
						var c = this.col;
						if (this.isupdate) {
							//更新
							ajax({
								type: "post",
								url: "etl/updateTableDyna.action",
								dataType: "json",
								data: {
									tableId: c.tableId,
									colName: c.colname,
									colType: c.coltype,
									colNote: c.note,
									expression:c.expression,
									colId: c.colId,
								},
								success: (resp)=> {
									this.$notify.success("编辑成功");
									this.show = false;
									ts.callback(this.isupdate);
								}
							}, ts);
						} else {
							ajax({
								type: "post",
								url: "etl/createTableDyna.action",
								dataType: "json",
								data: {
									tableId: c.tableId,
									colName: c.colname,
									colType: c.coltype,
									expression:c.expression,
									colNote: c.note
								},
								success: (resp) => {
									this.$notify.success("创建成功");
									this.show = false;
									ts.callback(this.isupdate);
								}
							}, ts);
						}
					}
				});
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
			},
			clearData(){
				this.$refs['colForm'].resetFields();
				for(let c in this.col){
					this.col[c] = null;
				}
			},
			helpbdsinfo(){
				this.innerVisible = true;
			},
		},
		watch: {
		}
	}
</script>
<style lang="less">
.colexp .CodeMirror {
    border: 1px solid #DCDFE6;
    border-radius: 5px;
    height: 100px;
    width: 100%;
    font-size: 14px;
}
</style>>
