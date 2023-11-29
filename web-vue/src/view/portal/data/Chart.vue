<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!-- 图形数据设置 -->
<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import * as chartUtils from '@/view/portal/ChartUtils'
import paramFilter from '@/view/bireport/ParamFilter'
import kpiFilter from './ChartKpiFilter'
import mulitKpiProp from './MultiKpiProp'
import tableCompareDate from './TableCompareDate'

export default {
  components:{
    paramFilter, kpiFilter, mulitKpiProp, tableCompareDate
  },
  props:{
      comp:{
        type:Object,
        required:true
	  },
	  //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
	  },
	  pageInfo: {
        type:Object,
        required: true,
      }
  },
  data(){
    return {

    }
  },
  render(h){
    let leftCols = [];
    let comp = this.comp.comp;
	let tp = comp.chartJson.type;
	let typeIndex = comp.chartJson.typeIndex;
	let isscatter = tp === 'scatter' || tp === 'bubble';
	let isbubble = tp === 'bubble';
	let isMulti = comp.mkpi == true;  //是否勾选了多指标查询
	let multi = (typeIndex == "1" && (tp == "line" || tp == "bar" || tp == "area" || tp == "radar")) ||
			((typeIndex == "1" || typeIndex == "3") && tp == "column") || (typeIndex == '3' && tp == 'gauge') ||
			tp == 'map'; //多指标完成率图 //是否支持多指标查询 //地图都支持多指标，在tooltip中显示
	let ispie = tp == 'pie' || tp == 'pie2' || tp == 'gauge' || tp == 'gauge2' || tp == 'wordcloud' || tp == "funnel" || tp == 'treeMap';
	if(tp === 'bar' && typeIndex == 3){	//胶囊图
		ispie = true;
	}
	let isheatmap = tp === 'map' && typeIndex == 6;
	let isnesting = tp == 'pie' && typeIndex == 3;
	let sankey = tp === "sankey";  //是否桑基图


	if(isscatter){
		let xcol = null;
		if(comp.kpiJson && comp.kpiJson.length >0 && comp.kpiJson[0] && comp.kpiJson[0].tid ){
			let o = comp.kpiJson[0];
			xcol = [h('span', {class:"charttxt",attrs:{title:o.kpi_name}}, o.kpi_name), h('a', {attrs:{class:"charticon"},domProps:{innerHTML:`<i class="fa fa-gear"></i>`},on:{click:()=>this.chartmenu(o, 'ycol')}})]
		}else{
			xcol = [h('span', {class:"charttip"}, '将度量拖到这里')]
		}
		//横轴　
		let xcolobj = h('div', {class:"ts_h"}, [h('div', '横轴：'), h('div', {attrs:{class:"h_ctx", id:"ycol"}}, xcol)]);
		leftCols.push(xcolobj);

		let ycol = null;
		if(comp.kpiJson && comp.kpiJson.length >0 && comp.kpiJson[1]){
			let o = comp.kpiJson[1];
			ycol = [h('span', {class:"charttxt",attrs:{ title:o.kpi_name}}, o.kpi_name), h('a', {attrs:{class:"charticon"},domProps:{innerHTML:`<i class="fa fa-gear"></i>`},on:{click:()=>this.chartmenu(o, 'y2col')}})]
		}else{
			ycol = [h('span', {class:"charttip"}, '将度量拖到这里')]
		}
		let ycolobj = h('div', {class:"ts_h"}, [h('div', '纵轴'), h('div', {attrs:{class:"h_ctx", id:"y2col"}}, ycol)]);
		leftCols.push(ycolobj);

		//气泡大小
		if(isbubble){
			let qp = null;
			if(comp.kpiJson && comp.kpiJson.length >0 && comp.kpiJson[2] && comp.kpiJson[2].tid){
				let o = comp.kpiJson[2];
				qp = [h('span', {class:"charttxt", attrs:{ title:o.kpi_name}}, o.kpi_name), h('a', {attrs:{class:"charticon"},domProps:{innerHTML:`<i class="fa fa-gear"></i>`},on:{click:()=>this.chartmenu(o, 'ycol')}})]
			}else{
				qp = [h('span', {class:"charttip"}, '将度量拖到这里')]
			}
			let qpobj = h('div', {class:"ts_h"}, [h('div', '气泡大小'), h('div', {attrs:{class:"h_ctx", id:"y3col"}}, qp)]);
			leftCols.push(qpobj);
		}
	}

	//横轴
	let xcol = null;
	if(!$.isEmptyObject(comp.chartJson.xcol) && comp.chartJson.xcol.tid){
		let o = comp.chartJson.xcol;
		xcol = [h('span', {class:"charttxt",attrs:{title:o.dimdesc}}, o.dimdesc), h('a', {attrs:{class:"charticon"},domProps:{innerHTML:`<i class="fa fa-gear"></i>`},on:{click:()=>this.chartmenu(o, 'xcol')}})]
	}else{
		xcol = [h('span', {class:"charttip"}, '将维度拖到这里')]
	}
	let xcolobj = h('div', {class:"ts_h"}, [h('div', isscatter?'观察维度：':(sankey?'源：':'横轴：')), h('div', {attrs:{class:"h_ctx", id:"xcol"}}, xcol)]);
	if(isheatmap){
		xcolobj.children.push(h('div',{class:"text-warning", domProps:{innerHTML:"热力图的横轴维度由地图经度加逗号加纬度构成,比如：101.91,30.19"}}));
	}
	leftCols.push(xcolobj);

	if(!(isscatter || isMulti && multi)){
		let ycol = null;
		if(comp.kpiJson && comp.kpiJson.length > 0 && comp.kpiJson[0] != null && comp.kpiJson[0].tid){
			let o = comp.kpiJson[0];
			ycol = [h('span', {class:"charttxt", attrs:{ title:o.kpi_name}}, o.kpi_name), h('a', {attrs:{class:"charticon"},domProps:{innerHTML:`<i class="fa fa-gear"></i>`},on:{click:()=>this.chartmenu(o, 'ycol')}})]
		}else{
			ycol = [h('span', {class:"charttip"}, '将度量拖到这里')];
		}
		let ycolobj = h('div', {class:"ts_h"}, [h('div', (sankey?'度量：':'纵轴：')), h('div', {attrs:{class:"h_ctx", id:"ycol"}}, ycol)]);
		leftCols.push(ycolobj);
	}

	if(isMulti && multi){ //多指标查询
		let ycols = [];
		$(comp.mkpiJson).each((a, b)=>{
			ycols.push(h('div', {class:"mkpidiv"},
				[
					h('span', {class:"mcharttxt", attrs:{title:b.kpi_name}}, b.kpi_name),
					h('a', {class:"charticon chartoptbtn", attrs:{title:"配置"}, domProps:{innerHTML:"<i class='fa fa-gear'></i>"}, on:{click:()=>{ this.chartmenu(b, 'ycols') }}})
				])
			);
		});
		//纵轴,
		let ycolobj = h('div', {class:"ts_h", style: {'width' : "135px"}}, [h('div', '纵轴：'), h('div', {attrs:{class:"h_ctx_multi", id:"ycols"}}, ycols)]);
		leftCols.push(ycolobj);
	}

	if((tp === 'map' && (typeIndex == 1 || typeIndex == 5) ) || isheatmap || (tp==='bar' && typeIndex == 3) || (tp === 'pie' && (typeIndex == 1 ||typeIndex == 2 )) || tp =='wordcloud' || tp =='funnel' || tp =='treeMap' || isMulti && multi){
		//此类图形没有图例 (系列)
	}else{
		let scol = null;
		if(!$.isEmptyObject(comp.chartJson.scol)){
			let o = comp.chartJson.scol;
			scol = [h('span', {class:"charttxt", attrs:{ title:o.dimdesc}}, o.dimdesc), h('a', {attrs:{class:"charticon"},domProps:{innerHTML:`<i class="fa fa-gear"></i>`},on:{click:()=>this.chartmenu(o, 'scol')}})]
		}else{
			scol = [h('span', {class:"charttip"}, '将维度拖到这里')]
		}
		//图例 Ser
		let scolobj = h('div', {class:"ts_h"}, [h('div', (sankey?'目标：':'图例')), h('div', {attrs:{class:"h_ctx", id:"scol"}},scol)]);
		leftCols.push(scolobj);
	}

	//第二纵轴
	if((tp==='column' || tp === 'line' || tp ==='bar') && (typeIndex == 2 || typeIndex == 4) ){
		let y2col = null;
		if(comp.kpiJson && comp.kpiJson.length > 1 && comp.kpiJson[1] != null){
			let o = comp.kpiJson[1];
			y2col = [h('span', {class:"charttxt", attrs:{ title:o.kpi_name}}, o.kpi_name), h('a', {attrs:{class:"charticon"},domProps:{innerHTML:`<i class="fa fa-gear"></i>`},on:{click:()=>this.chartmenu(o, 'y2col')}})]
		}else{
			y2col = [h('span', {class:"charttip"}, '将度量拖到这里')]
		}
		let y2obj = h('div', {class:"ts_h"}, [h('div', '第二纵轴'), h('div', {attrs:{class:"h_ctx", id:"y2col"}},y2col)]);
		leftCols.push(y2obj);
	}
	//更新拖拽事件
	this.$nextTick(()=>{
		this.initChartKpiDrop();
	});
	//数据编辑对话框
	leftCols.push(h('paramFilter', {ref:'paramFilterForm', props:{usein:"report"}}));
	//度量筛选对话框
	leftCols.push(h('kpiFilter', {ref:"kpiFilterForm"}));
	//多指标设置
	leftCols.push(h('mulitKpiProp', {ref:"mulitKpiPropForm"}));
	leftCols.push(h('tableCompareDate', {ref:"tableCompareDateForm", props:{useIn:this.useIn, type:"chart"}}));
	return h('div', {class:"tsbd", attrs:{id:"chartData"}}, leftCols);
},
  mounted(){

  },
  computed: {

  },
  methods: {
    setUpdate(){
      this.$parent.$parent.isupdate = true;
      this.$forceUpdate();
    },
    chartView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].chartView();
	},
	chartmenu(o, pos){
		const ts = this;
		let tp = pos;
		var items = {
            "compute": {name: "计算", items:{sq:{name:"上期"}, tq:{name:"同期"}, zje:{name:'增减额'}, hb:{name:'环比(%)'}, tb:{name:"同比(%)"}, lj:{name:"累计"}, "sep1": "---------",zdrq:{name:"比较指定日期"}},disabled:function(){
            	if("ycol" == tp || 'ycols' == tp){
            		return false;
            	}else{
            		return true;
            	}
            }},
            asc:{name:"升序",icon:"fa-sort-amount-asc"},
            desc:{name:"降序", icon:"fa-sort-amount-desc"},
            "filter": {name: "筛选",icon:"fa-filter"},
            "prop": {name: "属性", disabled: function(){
            	if("ycols" == tp){
            		return false;
            	}else{
            		return true;
            	}
            }},
            "clear": {name: "清除", icon:"fa-times"}
        };
		if("ycol" == tp || "ycols" == tp){
			//清除按钮
			var js = ['sq', 'tq', 'zje', 'lj', 'tb', 'hb'];
			var comp = ts.comp.comp;
			if(comp.mkpi == true){ //多指标查询
				if(o.compute){
					let ls = o.compute.split(",");
					$(ls).each(function(a, b){
						items.compute.items[b].icon = 'fa-check';
					});
				}
			}else{
				if(comp.kpiJson[0].compute){
					var ls = comp.kpiJson[0].compute.split(",");
					$(ls).each(function(a, b){
						items.compute.items[b].icon = 'fa-check';
					});
				}
			}
		}
		$.contextMenu( 'destroy');
		$.contextMenu({
			selector: 'a.charticon',
			className: "chartDimOpt",
			trigger: 'left',
			delay: 500,
			autoHide:true,
			callback: function(key, opt) {
				var comp = ts.comp.comp;
				if(key == 'asc' || key == 'desc'){
					chartUtils.chartsort(pos, key, comp, o, ()=>{
						ts.setUpdate();
						ts.chartView();
					});
				}else if(key == "clear"){
					chartUtils.delChartKpiOrDim(pos, comp, o, ()=>{
						ts.setUpdate();
						ts.chartView();
					});
				}else if(key === 'prop'){
					ts.$refs['mulitKpiPropForm'].kpiProp(o);
				}else if(key == 'filter'){
					if(pos ==='ycol' || pos === 'ycols' || pos === 'y2col'){
						ts.$refs['kpiFilterForm'].showKpiFilter(o);
					}else{
						ts.$refs['paramFilterForm'].createDimFilter(o, comp, 'chart');
					}
				}else if(key == "sq" || key == "tq" || key == "zje" || key == "hb" || key == "tb" || key === 'lj'){
					ts.chartKpiCompute(key, o);
				}else if(key == "zdrq"){ //数据和指定日期做比较
					ts.$refs['tableCompareDateForm'].openDailog(o, ts.pageInfo, ()=>{
						ts.setUpdate();
						ts.chartView();
					});
				}
			},
			items: items
		});
	},
    initChartKpiDrop(){
        const ts = this;
		let comp = this.comp;
		$("#chartData #xcol, #chartData #ycol, #chartData #y2col, #chartData #y3col, #chartData #ycols, #chartData #scol").droppable({
			accept:"#datasettree .jstree-node",
			tolerance:"pointer",
			over:function(e, ui){
				var ref = $("#datasettree").jstree(true);
				var node = ref.get_node(ui.draggable[0]);
				var tp = node.li_attr.col_type;
				var nodeId = $(this).attr("id");
				if(tp == 1 && (nodeId == 'xcol' || nodeId == 'scol')){
					$(ui.helper[0]).find("span").removeClass("glyphicon-remove").addClass("glyphicon-ok");
					$(this).css("border", "1px solid #ff0000");
				}

				if(tp == 2 && (nodeId == "ycol" || nodeId ==='y2col' || nodeId==='y3col' || nodeId === 'ycols')){
					$(ui.helper[0]).find("span").removeClass("glyphicon-remove").addClass("glyphicon-ok");
					$(this).css("border", "1px solid #ff0000");
				}
			},
			out:function(e, ui){
				$(ui.helper[0]).find("span").removeClass("glyphicon-ok").addClass("glyphicon-remove");
				$(this).css("border-color", "#7F9DB9");
			},
			drop:function(e, ui){
				var id = ts.chartId;
				var json = comp.comp;
				//清除边框样式
				$("#chartData #"+$(this).attr("id")).css("border-color", "#7F9DB9");
				//获取TREE
				var ref = $("#datasettree").jstree(true);
				var node = ref.get_node(ui.draggable[0]);

				//判断拖入的维度及度量是否和以前维度及度量在同一个表。
				if(json.tid != undefined){
					if(json.tid != node.li_attr.tid){
						utils.msginfo("您拖入的"+ (node.li_attr.col_type == 2 ? "度量" : "维度") +"与组件已有的内容不在同一个数据表中，拖放失败。");
						return;
					}
				}
				//如果是地图，横轴必须是省/地市等地域维度
				if(json.chartJson.type == "map" && node.li_attr.col_type == 1 && $(this).attr("id") == "xcol"){
					if(!(node.li_attr.dim_type == 'prov' || node.li_attr.dim_type == 'city' || node.li_attr.dim_type == 'town' ||
						node.li_attr.dim_type == 'lonlat')){
						utils.msginfo("只能拖放省/市/县区/经纬度类型维度到横轴!");
						return;
					}
					/**
					var maparea = json.chartJson.maparea || 'china';
					if(maparea == "china"){
						if(node.li_attr.dim_type == 'town' || (node.li_attr.dim_type == 'city' && json.chartJson.typeIndex != "3")){   //type == 3 表示为GIS地图
							msginfo("只能把省份维度拖放到全国地图上!");
							return;
						}
					}
					**/
				}

				if(!json.tid){  //如果 tid不存在，可能是演示数据，需要清除xcol, kpiJson 等数据
					json.chartJson.xcol = {};
					json.kpiJson = [];
				}
				json.tid = node.li_attr.tid;
				json.tname = node.li_attr.tname;
				json.tincome = node.li_attr.income;
				var targetid = $(this).attr("id");
				var nestingPie = json.chartJson && json.chartJson.type == "pie" && json.chartJson.typeIndex== "3"  //是否是嵌套圆环图

				if(node.li_attr.col_type == 2 && (targetid == "ycol" || targetid == "y2col" || targetid == "y3col")){
					var ooo = {"kpi_id":node.li_attr.col_id, "kpi_name" : node.li_attr.name, ydispName:node.li_attr.name, "col_name":node.li_attr.col_name, "aggre":node.li_attr.aggre, "fmt":node.li_attr.fmt, "alias":node.li_attr.alias,"tid":json.tid,"unit":node.li_attr.unit,"rate":node.li_attr.rate,"fromCol":node.li_attr.fromCol, "distinctCol":node.li_attr.distinctCol};
					if(targetid == "ycol"){
						json.kpiJson[0] = ooo;
					}else if(targetid == "y2col"){
						json.kpiJson[1] = ooo;
					}else{
						json.kpiJson[2] = ooo;
					}
					json.chartJson.ycol = {"type":"kpi"};
					ts.setUpdate();
					ts.chartView();
				}else if(node.li_attr.col_type == 2 && targetid == "ycols"){  //多指标查询显示
					var ooo = {"kpi_id":node.li_attr.col_id, "kpi_name" : node.li_attr.name, ydispName:node.li_attr.name, "col_name":node.li_attr.col_name, "aggre":node.li_attr.aggre, "fmt":node.li_attr.fmt, "alias":node.li_attr.alias,"tid":json.tid,"unit":node.li_attr.unit,"rate":node.li_attr.rate,"fromCol":node.li_attr.fromCol, "distinctCol":node.li_attr.distinctCol};
					if(!json.mkpiJson){
						json.mkpiJson = [];
					}
					//判断指标是否重复
					var ext = false;
					$(json.mkpiJson).each(function(a, b){
						if(b.kpi_id == node.li_attr.col_id){
							ext = true;
							return false;
						}
					});
					if(ext){
						utils.msginfo("度量已经存在。");
						return;
					}
					json.mkpiJson.push(ooo);
					json.chartJson.ycol = {"type":"kpi"};
					ts.setUpdate();
					ts.chartView();
				}else
				if(node.li_attr.col_type == 1 && targetid == "xcol"){
					//判断是否在xcol中已经存在
					if(json.chartJson.scol != undefined && json.chartJson.scol.id == node.li_attr.col_id){
						utils.msginfo("您拖放的维度已存在于图例项中，不能拖放。")
						return;
					}

					//判断xcol 和 scol 是否属于一个分组，如果是，不让拖动
					var gt = node.li_attr.grouptype;
					if(gt != null && gt != '' && !nestingPie){
						if(json.chartJson.scol != undefined && json.chartJson.scol.grouptype == gt){
							utils.msginfo("您拖放的维度与此图形中已有维度分组相同，不能拖放。")
							return;
						}
					}

					json.chartJson.xcol = {"id":node.li_attr.col_id, "dimdesc" : node.text,xdispName:node.text, "type":node.li_attr.dim_type, "colname":node.li_attr.col_name,"tid":json.tid,"iscas":node.li_attr.iscas, "tableName":node.li_attr.tableName, "tableColKey":node.li_attr.tableColKey,"tableColName":node.li_attr.tableColName, "dimord":node.li_attr.dimord, "dim_name":node.li_attr.dim_name, "grouptype":node.li_attr.grouptype,"valType":node.li_attr.valType, "ordcol":node.li_attr.ordcol,dateformat:node.li_attr.dateformat,"alias":node.li_attr.alias,"fromCol":node.li_attr.fromCol,"levelCol":node.li_attr.levelCol,"pclevel":node.li_attr.pclevel,"pcId":node.li_attr.pcId,"pcPid":node.li_attr.pcPid,"ispcdim":node.li_attr.ispcdim};							ts.setUpdate();
					ts.setUpdate();
					ts.chartView();
				}else
				if(node.li_attr.col_type == 1 &&  targetid == "scol"){
					//判断是否在xcol中已经存在
					if(json.chartJson.xcol != undefined && json.chartJson.xcol.id == node.li_attr.col_id){
						utils.msginfo("您拖放的维度已存在于横轴中，不能拖放。")
						return;
					}

					//判断xcol 和 scol 是否属于一个分组，如果是，不让拖动
					var gt = node.li_attr.grouptype;
					if(gt != null && gt != '' && !nestingPie){
						if(json.chartJson.xcol != undefined && json.chartJson.xcol.grouptype == gt){
							utils.msginfo("您拖放的维度与此图形中已有维度分组相同，不能拖放。")
							return;
						}
					}

					json.chartJson.scol = {"id":node.li_attr.col_id, "dimdesc" : node.text, "type":node.li_attr.dim_type, "colname":node.li_attr.col_name,"tid":json.tid,"iscas":node.li_attr.iscas, "tableName":node.li_attr.tableName, "tableColKey":node.li_attr.tableColKey,"tableColName":node.li_attr.tableColName, "dimord":node.li_attr.dimord, "dim_name":node.li_attr.dim_name, "grouptype":node.li_attr.grouptype,"valType":node.li_attr.valType, "ordcol":node.li_attr.ordcol, "alias":node.li_attr.alias,dateformat:node.li_attr.dateformat,"fromCol":node.li_attr.fromCol,"levelCol":node.li_attr.levelCol,"pclevel":node.li_attr.pclevel,"pcId":node.li_attr.pcId,"pcPid":node.li_attr.pcPid,"ispcdim":node.li_attr.ispcdim};
					ts.setUpdate();
					ts.chartView();
				}
			}
		});
	},
	chartKpiCompute(jsType, kpi){
		let comp = this.comp.comp;
		var ctp = comp.chartJson.type;
		if(!(ctp == 'column' || ctp == 'line')){
			utils.msginfo("此图形类型不支持计算。");
			return;
		}
		if(comp.chartJson.typeIndex == "2" || comp.chartJson == "4"){
			utils.msginfo("此图形类型不支持计算。");
			return;
		}
		//var kpi = comp.kpiJson[0];
		var exist = kpi.compute;
		if(!exist || exist == ""){
			kpi.compute = jsType;
		}else{
			var js = exist.split(",");
			var idx = -1;   //不存在才添加
			for(let j=0; j<js.length; j++){
				if(js[j] == jsType){
					idx = j;
					break;
				}
			}
			if(idx == -1){
				kpi.compute = exist+","+jsType;
			}else{  //存在直接删除
				js.splice(idx, 1);
				kpi.compute = js.join(",");
			}
		}
		this.setUpdate();
		this.chartView();
	}
  },
  watch: {

  }
}
</script>

<style lang="less" scoped>
  .tsbd {
     .ts_h{
      font-size:13px;
      margin:5px 20px 5px 5px;
      width:125px;
      float:left;
    }
    .h_ctx{
      border:1px solid #7F9DB9;
      height:28px;
      overflow:hidden;
      border-radius:5px;
      padding:2px;
	}
	.h_ctx_multi {
		border: 1px solid #7F9DB9;
		height: 120px;
		overflow: auto;
		line-height:16px;
		border-radius:5px;
		div:hover {
			background-color:#ccc;
		}
	}
  }
  span.charttip {
    color:#999999;
    padding:3px;
    display:block;
  }
  span.charttxt {
    display:inline-block;
    width:99px;
    white-space:nowrap;
    overflow:hidden;
	margin-left:3px;
	margin-top:3px;
  }
  a.charticon {
    display:inline-block;
    width:16px;
    height:16px;
    cursor:pointer;
	font-size: 14px;
	position: absolute;
  }
  span.mcharttxt {
	display:-moz-inline-box;
	display:inline-block;
	width:101px;
	white-space:nowrap;
	overflow:hidden;
	margin:1px 1px 1px 5px;
}
.mkpidiv {
	padding-top: 1px;
	position: relative;
}
</style>
