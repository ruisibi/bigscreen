<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 表格对话框都放这里面 -->
<template>
  	<el-dialog title="选择图形类型" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
		<template slot="title">
          <span class="el-dialog__title">选择图形类型</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
		<div class="row">
			<div class="col-sm-3">
				<ul>
				<template v-for="item in charts">
					<li :key="item.cid" :class="item.show==true?'select':''" @click="selectchart(item.cid)">{{item.cname}}</li>
				</template>
				</ul>
			</div>
			<div class="col-sm-9">
				<template v-for="item in charts">
					<div class="one" :key="item.cid"  v-show="item.show">
						<template v-for="c in item.children">
						<span :key="item.type + c.index" :class="c.select===true?'select':''" @click="selectone(c.index, item.type)">
							<img :src="require('../../assets/chart/' + c.img)">
							<br/>
							{{ c.name }}
						</span>
						</template>
						<template v-if="item.type == 'map'">
							 <el-select v-model="mapArea" size="small" filterable placeholder="请选择地图区域" style="width:100%">
								<el-option
								v-for="item in opts.areas"
								:key="item.code"
								:label="item.name"
								:value="item.code">
								</el-option>
							</el-select>
						</template>
					</div>
				</template>
			</div>
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
	import $ from 'jquery'

	export default {
	    data(){
			return {
				show:false,
				mapArea:"china",
				mapAreaname:null,
				layoutId:null,
				comp:null,
				opts:{
					areas:[{
						code:"china",name:"全国"
					}]
				},
				charts:[
					{cid:"1", cname:"曲线图", type:"line",show:false,children:[
						{img:"c1.gif", index:"1", name:"曲线图", select:true},
						{img:"c12.gif", index:"2", name:"双轴曲线图"}
					]},
					{cid:"2", cname:"柱状图",type:"column",show:false,children:[
						{img:"c2.gif",  index:"1", name:"柱状图"},
						{img:"c22.gif", index:"2", name:"双轴柱状图"},
						{img:"c23.gif",  index:"3", name:"堆积图"},
						{img:"c24.gif", index:"4", name:"双轴堆积图"}
					]},
					{cid:"3", cname:"条形图",type:"bar",show:false,children:[
						{img:"bar.gif",  index:"1", name:"条形图"},
						{img:"bar-2.gif",  index:"2", name:"双轴条形图"},
						{img:"bar-3.png",  index:"3", name:"胶囊图"}
					]},
					{cid:"4", cname:"面积图",type:"area",show:false,children:[
						{img:"area.gif", index:"1", name:"面积图"}
					]},
					{cid:"5", cname:"饼图",type:"pie", show:false,children:[
						{img:"c3.gif", index:"1", name:"饼图"},
						{img:"c32.gif", index:"2", name:"环形图"},
						{img:"c33.gif", index:"3", name:"嵌套环形图"}
					]},
					{cid:"6", cname:"仪表盘",type:"gauge",show:false,children:[
						{img:"c4.gif", index:"1", name:"仪表盘"},
						{img:"c42.gif", index:"2", name:"完成率图"},
						{img:"c43.gif", index:"3", name:"多指标完成率"},
						{img:"liquidFill.png", index:"4", name:"水球图"},
					]},
					{cid:"7", cname:"雷达图", type:"radar", show:false,children:[
						{img:"c5.gif",  index:"1", name:"雷达图"}
					]},
					{cid:"8", cname:"散点图",type:"scatter",show:false,children:[
						{img:"c6.gif",  index:"1", name:"散点图"}
					]},
					{cid:"9", cname:"气泡图",type:"bubble",show:false,children:[
						{img:"c7.gif",  index:"1", name:"气泡图"}
					]},
					{cid:"10", cname:"地图",type:"map", show:false,children:[
						{img:"c8.gif", index:"1", name:"地图"},
						{img:"c82.gif", index:"2", name:"地图散点图"},
						{img:"c83.gif", index:"3", name:"城市地图"},
						{img:"c84.gif", index:"4", name:"百度地图"},
						{img:"c85.png", index:"5", name:"3D地图"},
						{img:"heatmap.png", index:"6", name:"热力图"},
					]},
					{cid:"11", cname:"词云", type:"wordcloud", show:false, children:[
						{img:"c11.gif", index:"1", name:"词云"},
					]},
					{cid:"12", cname:"漏斗图", type:"funnel", show:false, children:[
						{img:"c13.gif", index:"1", name:"漏斗图"},
					]},
					{cid:"13", cname:"树状图", type:"treeMap", show:false, children:[
						{img:"c14.gif", index:"1", name:"树状图"},
					]},
					{cid:"14", cname:"桑基图", type:"sankey", show:false, children:[
						{img:"sankey.gif", index:"1", name:"桑基图"},
					]},

					{cid:"25", cname:"自定义", type:"custom", show:false, children:[

						//其中 maparea 属性表示地图使用的 地图JSON， 321200.json 是泰州地图
						{img:"c82.gif", index:"2", shape:"map2", maparea:"321200", name:"泰兴化工分布图"},
					]},
				]
			}
		},
		props:{
			 //在哪里使用report/dashboard/bigscreen
			useIn:{
				type:String,
				required:true,
			},
		},
		mounted(){
			this.initAreas();
		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			initAreas(){
				var ts = this;
				ajax({
					url:"bireport/listAreas.action",
					data:{},
					type:"GET",
					success:(resp)=>{
						$(resp.rows).each((a, b)=>{
							ts.opts.areas.push(b);
						});
					}

				}, this);
			},
			save(){
				this.show = false;
				let tp = null;
				let index = null;
				let shape = null;
				let custMaparea = null;  //自定义图形的地图 maparea
				$(this.charts).each((a, b)=>{
					$(b.children).each((c, d)=>{
						if(d.select === true){
							tp = b.type;
							index = d.index;
							shape = d.shape;
							custMaparea = d.maparea;
						}
					});
				})
				if(this.comp){
					let comp = this.comp.comp;
					comp.chartJson.type = tp;
					if(tp == 'map'){
						comp.chartJson.maparea = this.mapArea;
						if(index == 3 || index == 4){  //城市地图只能是 全国
							comp.chartJson.maparea = "china";
						}
					}
					if(tp == 'custom' && custMaparea){
						comp.chartJson.maparea = custMaparea;
					}
					comp.chartJson.typeIndex = index;
					comp.chartJson.shape = shape;

					if(this.useIn === 'dashboard'){
						this.$parent.$refs['optarea'].$refs['mv_'+comp.id].chartView( true );
					}else{
						this.$parent.$refs['mv_'+comp.id].chartView(true);
					}
				}else{
					var compId = newGuid();
					var name = "图表";
					var comp = {"id":compId, "name":name, "type":"chart", height:200, comp:{id:compId}};
					//用两层comp, 第二层 comp 封装数据。
					comp.comp.chartJson = {"type":tp, typeIndex:index, shape:shape, xcol:{}, ycol:{}, scol:{}, params:[],height:tp == 'map' ? '400':'250'};
					if(tp == 'map'){
						comp.comp.chartJson.maparea = this.mapArea;
					}
					if(tp == 'custom' && custMaparea){
						comp.comp.chartJson.maparea = custMaparea;
					}
					comp.comp.kpiJson = [null, null, null];

					this.$parent.addComp(this.layoutId, comp);
					this.$parent.$forceUpdate();
				}
			},
			insertChart(layoutId){
				this.layoutId = layoutId;
				this.show = true;
				this.comp = null;
				//选中第一个
				$(this.charts).each((a, b)=>{
					b.show=false;
					$(b.children).each((c, d)=>{
						if(a==0 && c == 0){
							d.select = true;
						}else{
							d.select = false;
						}
					});
				});
				this.charts[0].show = true;
			},
			//更改图形类型
			changeType(comp){
				this.show = true;
				this.comp = comp;
				comp = comp.comp;
				let type = comp.chartJson.type;
				let index = comp.chartJson.typeIndex;
				$(this.charts).each((a, b)=>{
					b.show=false;
					$(b.children).each((c, d)=>{
						d.select = false;
					});
				});
				//选中值
				$(this.charts).each((a, b)=>{
					$(b.children).each((c, d)=>{
						if(b.type === type && d.index == index){
							d.select = true;
							b.show = true;
							return false;
						}
					});
				})
				this.mapArea = comp.chartJson.maparea;
				this.$forceUpdate();
			},
			selectchart(chartId){
				$(this.charts).each((a, b)=>{
					$(b.children).each((c, d)=>{
						d.select = false;
					});
				})

				$(this.charts).each((a, b)=>{
					if(b.cid == chartId){
						b.show = true
						//选中第一个
						b.children[0].select = true;
					}else{
						b.show = false;
					}
					this.$forceUpdate();
				});

			},
			selectone(index, type){
				$(this.charts).each((a, b)=>{
					$(b.children).each((c, d)=>{
						if(b.type === type && d.index == index){
							d.select = true;
						}else{
							d.select = false;
						}
					});
				})
				this.$forceUpdate();
			}
		},
		watch: {
		}
	}
</script>

<style lang="less" scoped>
.one{
	margin:10px;
	padding:10px;
	cursor:pointer;
	text-align:left;
	span {
		width:130px;
		height:143px;
		display:inline-block;
		margin-right:10px;
		overflow:hidden;
		text-align:center;
		cursor:pointer;
	}
	span.select {
			border:solid 1px #666;
		}
}
.row {
	ul {
		margin:0px;
		padding:0px;
		li {
			margin:0px;
			padding:6px;
			background-color:#EEE;
			border-bottom:solid 1px #CCCCCC;
			cursor:pointer;
			list-style: none;
			text-align: left;
		}
		li.select {
			background-color:#FFF;
		}
	}
}
</style>
