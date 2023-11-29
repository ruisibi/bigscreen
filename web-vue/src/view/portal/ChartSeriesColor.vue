<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 表格对话框都放这里面 -->
<template>
  	<el-dialog :title="title" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
		<template slot="title">
          <span class="el-dialog__title">{{title}}</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
		<div align="left">

			<template v-for="(item, index) in colors">
				<span :key="item" :class="index===cidx - 1 ?'selcolor':''">
					<span :style="'background-color:'+item+';'" class="seriesColor" @click="select(index + 1)"></span>
				</span>
			</template>
			<br/>
					<button class="btn btn-info btn-sm" @click="usedef()">使用默认色</button>

		</div>
		<div slot="footer" class="dialog-footer">
			<el-button @click="show = false">关 闭</el-button>
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
				comp:null,
				title:null,
				colors:[],
				chartParams:null,
				cidx:1
			}
		},
		mounted(){
			this.loadColors();
		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			showDailog(comp, chartParams){
				this.comp = comp;
				if(comp.chartJson.type == "pie" || comp.chartJson.type == "map"){
					return;
				}
				this.chartParams = chartParams;
				this.show = true;
				this.title = chartParams.seriesName + " - 设置系列颜色";
				if(comp.colors){
					let t = comp.colors[chartParams.seriesName];
					if(t){
						this.cidx = t;
					}
				}
			},
			loadColors(){
				ajax({
					url:"portal/chartColors.action",
					data:{},
					type:"GET",
					success:(resp)=>{
						this.colors = resp.rows;
					}
				}, this);
			},
			select(colorIdx){
				let comp = this.comp;
				if(!comp.colors){
					comp.colors = {};
				}
				comp.colors[this.chartParams.seriesName] = colorIdx;
				this.show = false;
				this.$parent.setUpdate();
				this.$parent.$refs['mv_'+comp.id].chartView();
			},
			usedef(){
				let comp = this.comp;
				if(comp.colors){
					delete comp.colors[this.chartParams.seriesName];
					this.show = false;
				}
				this.$parent.setUpdate();
				this.$parent.$refs['mv_'+comp.id].chartView();
			}
		},
		watch: {
		}
	}
</script>

<style lang="less" scoped>
.seriesColor {
	width: 16px;
	height: 16px;
	padding: 5px;
	display:inline-block;
	cursor:pointer;
	margin:5px;
}
.selcolor {
	border:1px solid #990000;
	height:30px;
	display:inline-block;
}
</style>
