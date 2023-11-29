<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 参数组件 -->
<template>
<div style="width:100%; height:100%">
  <template v-if="comp.comp && comp.comp.paramid">
    <div :class="getClassByCfg()">
        <template v-if="comp.comp.type === 'input'">
					<el-input size="mini" v-model="param" :clearable="true" :placeholder="comp.comp.name" @change="sumbitParam()"></el-input>
				</template>
        <template v-if="comp.comp.type === 'select'">
					<el-select v-model="param" clearable filterable :placeholder="comp.comp.name" @change="sumbitParam()" size="mini" style="width:100%">
						<el-option v-for="item in opts" :key="item.value" :label="item.text" :value="item.value">
						</el-option>
					</el-select>
				</template>
				<template v-if="comp.comp.type === 'mselect'">
					<el-select v-model="param" multiple clearable filterable collapse-tags @change="sumbitParam()" :placeholder="comp.comp.name" size="mini" style="width:100%">
						<el-option v-for="item in opts" :key="item.value" :label="item.text" :value="item.value">
						</el-option>
					</el-select>
				</template>

        <template v-if="comp.comp.type === 'onedate'">
					<el-date-picker v-model="param" size="mini" @change="sumbitParam()" :format="comp.comp.format" style="width:100%" :type="getDateType()" :placeholder="comp.comp.name" :value-format="comp.comp.format" :picker-options="pickerOptions"></el-date-picker>
				</template>
				<template v-if="comp.comp.type === 'datetime'">
					<!-- elementui 不支持 yearrange, 用下拉框实现 -->
					<template v-if="comp.comp.format ==='yyyy'">
						<el-row>
							<el-col :span="10">
								<el-date-picker v-model="paramStartVal" size="mini" @change="yearSubmit()" :format="comp.comp.format" style="width:100%" :type="getDateType()" :placeholder="comp.comp.name + '开始'" :value-format="comp.comp.format"></el-date-picker>
							</el-col>
							<el-col :span="2" style="margin-top: 3px; text-align: center;">
								-
							</el-col>
							<el-col :span="10">
								<el-date-picker v-model="paramEndVal" size="mini" @change="yearSubmit()"  :format="comp.comp.format" style="width:100%" :type="getDateType()" :placeholder="comp.comp.name + '截止'" :value-format="comp.comp.format"></el-date-picker>
							</el-col>
						</el-row>
					</template>
					<template v-if="comp.comp.format !=='yyyy'">
						<el-date-picker v-model="param" size="mini" @change="sumbitParam()" :format="comp.comp.format" style="width:100%" :type="getDateTypeByBetween()" :start-placeholder="comp.comp.name + '开始'" :end-placeholder="comp.comp.name + '截止'" :value-format="comp.comp.format" :picker-options="pickerOptions"></el-date-picker>
					</template>
				</template>
        <template v-if="comp.comp.type === 'tree'">
					<el-popover
						placement="bottom"
						title="树形选择框"
						trigger="hover"
						width="296"
						>
						<button type="button" @click="deletTreeParam()" class="btn btn-xs btn-outline btn-danger">清除</button>
						<div :id="'tree_' + comp.comp.id" class="treeparam"></div>
						<el-input size="mini" slot="reference" readonly v-model="paramName" clearable :placeholder="comp.comp.name">
						<i slot="suffix" class="el-input__icon el-icon-s-home"></i>
						</el-input>
					</el-popover>
				</template>
    </div>
  </template>
  <template v-else>
   <div>
    未配置参数
    </div>
  </template>
</div>
</template>
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import * as echartsUtils from '@/common/echartsUtils'
import * as dutils from '@/view/dashboard/Utils'

export default {
  components:{
  },
  data(){
    return {
      param: null,  //参数值
      paramStartVal: null,  //时间区间参数值, start
      paramEndVal: null,  //时间区间参数值, end
      opts:[],  //下拉框的选择值
      paramName: null, //对应tree 参数 的 name
      maxval:null,  //限制最大值
      minval:null,  //限制最小值
      format:null,  //日期的个啥
      pickerOptions : {
        disabledDate : (v) => {
          if(this.maxval && this.minval){ //限制时间选择区间
            var dt = formatDate(v, this.format);
            if(dt >= this.minval && dt <= this.maxval){
              return false;
            }else{
              return true;
            }
          }
          return false;
        }
	  },
	  styles:{
		 color:"#e6e6e6",
         bgcolor: "#353535",
         bordercolor:"#535353",
         selectcolor:"#e6e6e6",
	  }
    }
  },
  props:{
     pageInfo:{
        type:Object,
        required:false,
         default:()=>{}
      },
      comp:{
        type:Object,
        required:true,
        default:{}
      },

      editor:{
        type:Boolean,
        required:true,
        default:true
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
  },
  mounted(){
	this.initColors();
	this.initDefvalues();
    this.initSelectVals();
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
	},
	getClassByCfg(){
		return this.pageInfo.style && this.pageInfo.style.styleType == 'bigscreen' ? "model-black":"model-white";
	},
	initColors(){
		if(this.comp.style){
			let s = this.comp.style;
			this.styles.color = s.color;
			this.styles.bgcolor = s.bgcolor;
			this.styles.bordercolor = s.bordercolor;
			this.styles.selectcolor = s.selectcolor;
		}
		 this.$el.style.setProperty('--scolor',  this.styles.color);
		 this.$el.style.setProperty('--sbgcolor',  this.styles.bgcolor);
		 this.$el.style.setProperty('--sbordercolor',  this.styles.bordercolor);
		 this.$el.style.setProperty('--sselectcolor',  this.styles.selectcolor);
	},
	initDefvalues(){
		let b = this.comp.comp;
		let val = b.selval;
		if(!val){
			val =  dutils.parserDefvalue(b);
			if(b.type === 'mselect'){
				this.param = [val];
			}else if(b.type === 'datetime' && b.format !== 'yyyy'){
				if(!val){
					this.param = null;
				}else{
					let val2 = dutils.parserDefvalue2(b);
					this.param = [val, val2];
				}
			//由于 elementui 不支持 yearrange, 需要特殊处理实现
			}else if(b.type === 'datetime' && b.format === 'yyyy'){
				if(!val){
					this.paramStartVal = null;
					this.paramEndVal = null;
				}else{
					let val2 = dutils.parserDefvalue2(b);
					this.paramStartVal = val;
					this.paramEndVal = val2;
				}
			}else if(b.type ==='tree'){
				this.param = val;
				this.paramName = val;
			}else{
				this.param = val;
			}
		}else{
			val = val.value;
			let val2 = b.selval2 && b.selval2.value ? b.selval2.value : val; //datetime 时间区间 用的 selval2
			if(b.type === 'mselect'){
				this.param = val? val.toString().split(","): [];
			}else if(b.type === 'datetime'  && b.format !== 'yyyy'){
				this.param = [val, val2];
			//由于 elementui 不支持 yearrange, 需要特殊处理实现
			}else if(b.type === 'datetime' && b.format === 'yyyy'){
				this.paramStartVal = val;
				this.paramEndVal = val2;
			}else if(b.type === 'tree'){ //tree类型参数需要两个字段，一个带_name 表示名称, 默认的不带_name表示id
				this.param = val;
				this.paramName = val2;
			}else{
				this.param = val;
			}
		}

    //处理日期最大/最小值
    if(b.maxval){
      let v = b.maxval;
      v = dutils.parserDefDate(v, b.format);
      this.maxval = v;
    }
    if(b.minval){
      let v = b.minval;
      v = dutils.parserDefDate(v, b.format);
      this.minval = v;
    }
    this.format = b.format;
	},
    sumbitParam(){
		let v = this.param;
		let param = this.comp.comp;
		//selval 表示参数选择值， defval 表示参数默认值
		var txt = "";
		if(param.type === 'select') {
			if(v){
				let k = this.opts.filter(m=>m.value === v)[0];
				param.selval = {value:v, name:k.text}; //选择得值
			}else{
				delete param.selval;
			}
		}else if(param.type === 'mselect'){  //多选
			if(v){
				let selKeys = [];
				let selTxts = [];
				$(v).each((a, b)=>{
					let k = this.opts.filter(m=>m.value === b)[0];
					selKeys.push(k.value);
					selTxts.push(k.text);
				});
				let seles = {
					value: selKeys.join(","),
					name:selTxts.join(",")
				};
				param.selval = seles;
			}else{
				delete param.selval;
			}
		}else if(param.type === 'input'){
			if(v){
				param.selval = {value:v, name:v};
			}else{
				delete param.selval;
			}
		}else if(param.type === 'tree'){
			if(v){
				param.selval = {value:v, name:v};
			}else{
				delete param.selval;
			}
		}else if(param.type ==='onedate'){
			if(v){
				param.selval = {value:v, name:v};
			}else{
				delete param.selval;
			}
		}else if(param.type === 'datetime'){
			if(v){
				var st = v[0];
				var end = v[1];
				//有两个值，采用selval,和 selval2存储。
				param.selval = {value:st, name:st};
				param.selval2 = {value:end, name:end};
			}else{
				delete param.selval;
				delete param.selval2;
			}
		}
		//刷新组件
		this.$parent.flushCompsByPms(param);
    },
    initSelectVals(){
      let b = this.comp.comp;
      if((b.type === 'select' || b.type === 'mselect') && b.valtype ==='static'){ //静态值
          this.opts = b.values;
      }
      if((b.type === 'select' || b.type === 'mselect') && b.valtype ==='dynamic'){ //静态值
         this.loadDynaData();
      }
      if(b.type === 'tree'){
        this.loadTrees(b);
      }
	},
	//年区间选择
	yearSubmit(){
		let start = this.paramStartVal;
		let end = this.paramEndVal;
		let param = this.comp.comp;
		if(start == null && end == null){
			delete param.selval;
			delete param.selval2;
			//刷新组件
			this.$parent.flushCompsByPms(param);
			return;
		}
		if(start == null || end == null){
			return;
		}
		param.selval = {value:start, name:start};
		param.selval2 = {value:end, name:end};

		//刷新组件
		this.$parent.flushCompsByPms(param);
	},
    //加载动态值
	loadDynaData(){
		let url = "etl"+(this.token?"/share":"")+"/queryMultiTablesDataAsJson.action"+(this.token?"?token="+this.token:"");
        let p = [];
        let b = this.comp.comp;
        let param = b;
        let o = {
          tableId: b.tid,
          text: b.text,
          value: b.value,
          paramId : b.paramid,
          order: b.order,
        };
        p.push(o)

		ajax({
			url:url,
			data:JSON.stringify(p),
			postJSON:true,
			type:"POST",
			success:(r)=>{
				for(let p in r.rows){
					this.opts = r.rows[b.paramid].map(m=>{
						let k = m[param.value];
						if(!k){
							k = m[param.value.toLowerCase()];
						}
						if(!k){
							k = m[param.value.toUpperCase()];
						}
						let v = m[param.text];
						if(!v){
							v = m[param.text.toLowerCase()];
						}
						if(!v){
							v = m[param.text.toUpperCase()];
						}
						return {value:k?k:null,text:v};
					});
					if(param.defFirstValue === true){  //下拉框第一行为默认值
						let vls = this.opts;
						let vl = vls[0].value;
						if(param.type === 'mselect'){
							this.param = [vl];
						}else{
							this.param = vl;
						}
					}
				}
				this.$forceUpdate();
			}
		}, this);
      },
      getDateType(){
        		let p = this.comp.comp;
				if(p.format === 'yyyy'){
					return "year";
				}else if(p.format === 'yyyy-MM' || p.format === 'yyyyMM'){
					return 'month';
				}else if(p.format === 'yyyy-MM-dd' || p.format === 'yyyyMMdd'){
					return 'date';
				}else if(p.format === 'yyyyMMddHHmmss' || p.format === 'yyyy-MM-dd HH:mm:ss'){
					return "datetime";
				}else{
					return null;
				}
			},
			getDateTypeByBetween(){
        		let p = this.comp.comp;
				if(p.format === 'yyyy'){
					return "yearrange";
				}else if(p.format === 'yyyy-MM' || p.format === 'yyyyMM'){
					return 'monthrange';
				}else if(p.format === 'yyyy-MM-dd' || p.format === 'yyyyMMdd'){
					return 'daterange';
				}else if(p.format === 'yyyyMMddHHmmss' || p.format === 'yyyy-MM-dd HH:mm:ss'){
					return "datetimerange";
				}else{
					return null;
				}
      },
      deletTreeParam(){
        let param = this.comp.comp;
		var ref = $("#tree_" + param.id).jstree(true);
		ref.deselect_all();
		this.param = null;
		this.paramName = null;
		this.sumbitParam();
      },
      loadTrees(param){  //加载tree 类型
				let ts = this;
				let url = "etl"+(this.token?"/share":"")+"/queryTreeData.action";
				ajax({
					url:url,
					data:{
						matchTable: param.matchTable,
						tableIdCol: param.tableIdCol,
						tablePidCol: param.tablePidCol,
						tableNameCol: param.tableNameCol,
						token: this.token
					},
					type:"POST",
					success:(r)=>{
						$('#tree_' + param.id).jstree({
							core:{
								data:r.rows
							},
							"plugins" : [
								"wholerow"
							]
						}).bind("select_node.jstree",function(c, d){
							var nid = d.node.id;
							var ntxt = d.node.text;
							ts.paramName = ntxt;
							ts.param = nid;
							ts.sumbitParam();
						})
					}
				}, this);
			},
  },
  watch: {

  },
  beforeDestroy(){

  },
}
</script>

<style lang="less">
.treeparam {
	width:276px;
	height: 300px;
	overflow: auto;
}

/**黑色模式，覆盖 elementui 的样式  */
.model-black .el-input__inner {
	background-color: var(--sbgcolor);
    color: var(--scolor);
    border: 1px solid var(--sbordercolor);
}
.model-black .el-input__inner:hover {
    border: 1px solid var(--sselectcolor);
}
.model-black .el-select .el-input.is-focus .el-input__inner {
    border: 1px solid var(--sselectcolor);
}
.model-black .el-select .el-input__inner:focus {
    border: 1px solid var(--sselectcolor);
}
.model-black .el-date-editor .el-range-input {
    color: var(--scolor);
    background-color: var(--sbgcolor);
}
</style>
