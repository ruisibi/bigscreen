<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 表格对话框都放这里面 -->
<template>
  	<el-dialog title="组件事件设置" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		 <template slot="title">
          <span class="el-dialog__title">组件事件设置</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
		  <div align="left">
			  <el-form label-width="110px" size="mini">

				<el-tabs v-model="activeName">
					<el-tab-pane label="事件发起" name="send">
						<template v-if="comp.type == 'chart' || comp.type == 'table' || comp.type ==='grid'">
							<el-form-item label="表格参数：" label-width="100px" v-if="comp.type=='grid'">
								<el-select v-model="zqcs" placeholder="请选择">
									<el-option v-for="item in gridLinkParams" :key="item.id" :label="item.name" :value="item.id" >
									</el-option>
								</el-select>
							</el-form-item>
							<div>
								<el-radio v-model="usetype" label="comp">联动组件</el-radio>
							</div>
								<el-form-item label="" label-width="90px" v-if="usetype === 'comp'">
									<el-checkbox-group v-model="linkComps" size="mini">
										<template v-for="item in comps">
											<el-checkbox :key="item.id" :label="item.id" border>{{ item.name }}</el-checkbox>
										</template>
									</el-checkbox-group>
								</el-form-item>
								<div class="hr-line-dashed"></div>
								<div>
								<el-radio v-model="usetype" label="report">链接到{{ useIn==='report'?"报表":"仪表盘" }}</el-radio>
								</div>
								<el-form-item label="" label-width="90px" v-if="usetype === 'report'">
									<el-select v-model="linkReport" placeholder="请选择要链接的仪表盘" style="width:100%;">
										<el-option
										v-for="item in reports"
										:key="item.id"
										:label="item.name"
										:value="item.id">
										</el-option>
									</el-select>
								</el-form-item>
								<el-form-item label="" label-width="90px" v-if="usetype === 'report'">
									参数名：
									{{zqcs}}
									<span class="text-warning">（{{ useIn==='report'?"报表":"仪表盘" }}会把此参数传递给链接的{{ useIn==='report'?"报表":"仪表盘" }}）</span>
								</el-form-item>
							<el-form-item label="" label-width="90px">
								<button type="button" class="btn btn-xs btn-outline btn-danger" @click="cleanPostEvent">清除事件发起</button>
							</el-form-item>
						</template>
						<template v-if="!(comp.type == 'chart' || comp.type == 'table' || comp.type ==='grid')">
							<div>此分析图不支持事件发起</div>
						</template>
					</el-tab-pane>
					<el-tab-pane label="事件接收" name="accept">
						<el-form-item label="接收字段：">
							<el-select v-model="acceptCol" placeholder="请选择" style="width:100%">
								<el-option
								v-for="item in cols"
								:key="item.colName"
								:label="item.colName"
								:value="item.colName"
								>
								</el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="">
							<button type="button" @click="cleanAcceptEvent" class="btn btn-xs btn-outline btn-danger">清除事件接收</button>
						</el-form-item>
					</el-tab-pane>
				</el-tabs>
			  </el-form>
		  </div>
		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="save()">确 定</el-button>
			<el-button @click="show = false">取 消</el-button>
		</div>
  </el-dialog>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, newGuid} from '@/common/biConfig'
	import * as utils from './Utils'
	import $ from 'jquery'

	export default {
	    data(){
			return {
				show:false,
				comp:{},
				activeName:null,
				linkComps:[],
				comps:[],
				acceptCol:null,
				cols:[],
				usetype:"comp", //联动到组件还是链接到其他报表/仪表盘
				reports:[], //链接的报表/仪表盘列表
				linkReport:null, //选中的报表/仪表盘
				zqcs:null,
				gridLinkParams:[], //表格链接参数列表
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
			loadCols(){
				ajax({
					type:"get",
					url:"etl/getTableColumns.action",
					data: {tableId: this.comp.comp.tid},
					success:(resp)=>{
						this.cols = resp.rows;
					}
				}, this);
			},
			listReports(){
				ajax({
					type:"GET",
					url:"portal/listAllReports.action",
					data:{},
					success:(resp)=>{
						this.reports = resp.rows.map(m=>{
							return {id:m.pageId, name:m.pageName}
						});
					}
				}, this);
			},
			listDashboard(){
				ajax({
					type:"GET",
					url:"dashboard/list.action",
					data:{},
					success:(resp)=>{
						this.reports = resp.rows.map(m=>{
							return {id:m.id, name:m.pageName}
						});
					}
				}, this);
			},
			getLinkParam(){
				let comp = this.comp.comp;
				this.zqcs = utils.getLinkParam(comp, this.comp.type, this.gridLinkParams).name;
			},
			openDailog(comp){
				this.comp = comp;
				if(!comp.comp.tid){
					utils.msginfo("组件还未定义数据，不能定义事件。");
					return;
				}
				let ccomps = [];
				//报表和仪表盘组件存放位置不一样
				if(this.useIn === 'report'){
					let comps = utils.findAllComps(this.pageInfo);
					for(let i=0; comps && i<comps.length; i++){
						var o = comps[i];
						if(o.type == 'chart' || o.type == 'table'){
							if(o.id != comp.id){  //不添加它自己
								ccomps.push({id:o.id, name:o.name});
							}
						}
					}
					this.listReports();
				}else if(this.useIn === 'dashboard'){
					for(let o in this.pageInfo.comps){
						var c = this.pageInfo.comps[o];
						if(c.id == this.comp.id){ //不添加它自己
							continue;
						}
						if(c.type ==='text' || c.type === 'iframe'){
							continue;
						}
						ccomps.push({id:c.id, name:c.name});
					}
					this.listDashboard();
					if(comp.type === 'grid'){
						let cols = [];
						$(comp.comp.cols).each((a, b)=>{
							if(b.children){
								$(b.children).each((c,d)=>{
									cols.push(d);
								});
							}else{
								cols.push(b);
							}
						});
						this.gridLinkParams = cols;
					}
				}
				this.comps = ccomps || [];
				var clink;
				var linkaccept;
				if(this.comp.type == "chart" && comp.comp.chartJson){
					clink = comp.comp.chartJson.link;
					linkaccept = comp.comp.chartJson.linkAccept;
				}else{
					clink = comp.comp.link;
					linkaccept = comp.comp.linkAccept;
				}
				if(clink && clink.target){  //联动组件
					let linkComps = [];
					let cps = clink.target.split(",");
					//判断cps的有消息
					$(cps).each((a, b)=>{
						let c = null;
						if(this.useIn === 'report'){
							c = utils.findCompById(this.pageInfo, b);
						}else if(this.useIn === 'dashboard'){
							c = this.pageInfo.comps[b];
						}
						if(c){
							linkComps.push(b);
						}
					});
					this.linkComps = linkComps;
					this.usetype = "comp";
				}else{
					this.linkComps = [];
				}
				if(clink && clink.reportId){ //链接到报表
					this.linkReport = clink.reportId;
					this.usetype = "report";
					if(comp.type === 'grid'){
						this.zqcs = clink.colName;  //回写链接的字段
					}
				}else{
					this.linkReport = null;
				}
				if(linkaccept){
					this.acceptCol = linkaccept.col;
				}else{
					this.acceptCol = null;
				}
				try {
					this.getLinkParam();
				}catch(e){
					utils.msginfo(e.message);
					return;
				}
				this.show = true;
				this.activeName = "send";
				this.loadCols();
			},
			save(){
				let comp = this.comp.comp;
				let ts = this;
				if(this.activeName === 'send'){
					var seles = this.linkComps;
					var linkReport =  this.linkReport;
					if(this.usetype === 'comp'){
						if(!seles ||seles.length == 0){
							this.$notify.error("未勾选组件.");
							return;
						}
						var link = {};
						if(this.comp.type == "chart"){
							comp.chartJson.link = link;
						}else{
							comp.link = link;
						}
						var targets = "";
						var types = "";
						$(seles).each((a, b)=>{
							var id = b;
							var linkComp = null;
							if(this.useIn==='report'){
								linkComp = utils.findCompById(this.pageInfo, id);
							}else if(this.useIn === 'dashboard'){
								linkComp = this.pageInfo.comps[id];
							}
							targets = targets + id + ",";
							types = types + (linkComp.type=="table"?"cross":linkComp.type)+","
						});
						targets = targets.substring(0, targets.length - 1);
						types = types.substring(0, types.length - 1);
						link.target = targets;
						link.type = types;
						link.paramName = "p"+Math.floor(Math.random()*100);
						if(this.comp.type === 'grid'){
							var colId = this.zqcs;
							$(this.gridLinkParams).each(function(a, b){
								if(b.id == colId){
									link.colName = b.name;
									link.expression = b.expression;
									link.colId = b.id;
									link.colType = b.type;
									return false;
								}
							});
						}
					}else if(this.usetype === 'report'){
						var link = {};
						if(this.comp.type == "chart"){
							comp.chartJson.link = link;
						}else{
							comp.link = link;
						}
						link.reportId = linkReport;
						if(this.comp.type === 'grid'){
							link.colName = this.zqcs;  //通过哪个字段来联动
						}
						//link.url = 'view.action?pageId='+linkReport;
					}
					this.$parent.$forceUpdate();
					//刷新组件
					if(this.useIn === 'dashboard'){
						let c = this.$parent.$refs['r_'+comp.id][0].$refs['mv_'+comp.id];
						c.view();
					}
				}else if(this.activeName === 'accept'){
					var col = this.acceptCol;
					var o = null;
					var val = "";
					if(comp.tincome==='rest'){
						o = {id:0, col:col};
					}else{
						var dim = this.cols.filter(m=>m.colName===col)[0];
						o = {id:dim.colId,col:dim.colName, expression:dim.expression, valType:dim.colType, dftval: val};
					}
					if(this.comp.type == "chart"){
						comp.chartJson.linkAccept = o;
					}else{
						if(comp){
							comp.linkAccept = o;
						}else{
							utils.msginfo("组件还未定义数据，不能定义事件。");
						}
					}
				}
				this.show = false;
				this.$parent.setUpdate();
			},
			cleanPostEvent(){
				this.linkComps = [];
				let comp = this.comp.comp;
				if(this.comp.type == "chart"){
					delete comp.chartJson.link;
				}else{
					delete comp.link;
				}
				//刷新组件
				if(this.useIn === 'report'){
					let c = this.$parent.$refs['mv_'+comp.id];
					c.view();
				}else if(this.useIn === 'dashboard'){
					let c = this.$parent.$refs['r_'+comp.id][0].$refs['mv_'+comp.id];
					c.view();
				}
				this.$parent.$forceUpdate();
				this.show = false;
				this.$parent.setUpdate();
			},
			cleanAcceptEvent(){
				this.acceptCol = null;
				this.show = false;
				let comp = this.comp.comp;
				if(this.comp.type == "chart"){
					delete comp.chartJson.linkAccept;
				}else{
					delete  comp.linkAccept;
				}
				this.$parent.setUpdate();
			}
		},
		watch: {
		}
	}
</script>

<style lang="less" scoped>
.hr-line-dashed {
    border-top: 1px dashed #e7eaec;
    color: #ffffff;
    background-color: #ffffff;
    height: 1px;
    margin: 20px 0;
}
</style>
