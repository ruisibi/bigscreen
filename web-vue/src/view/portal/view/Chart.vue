<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<script>
import {baseUrl, ajax, loopChartJson} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'
import * as echartsUtils from '@/common/echartsUtils'
import 'echarts-wordcloud/dist/echarts-wordcloud'
import 'echarts-liquidfill'
import * as dutils from '@/view/dashboard/Utils'
import 'echarts-gl'  //3D 地图插件

export default {
  components:{
  },
  data(){
    return {
      data:null,
      islink:false, //是否做了事件联动
      chartDivPrefix:null,  //图形div的前缀
      styleType:null, //风格类型，白色背景还是黑色背景
      carouselId:null, //轮播事件ID
      error: null,  //错误信息
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
      portalParams:{
        type:Array,
        required:false
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
      //在仪表盘分享后，需要使用token
      token:{
        type:String,
        required:false,
        default:null,
      },
       //是否运行自动刷新(一般在设计模式不运行，在发布后运行)
      autuFlush:{
        type:Boolean,
        required: false,
        default: false
      }
  },
  render(h){
    let ts = this;
    let comp = this.comp;
    this.styleType = this.pageInfo&&this.pageInfo.style ? this.pageInfo.style.styleType:null;
    if(this.useIn === 'dashboardEdit'){
      this.chartDivPrefix = "cte_";
    }else if(this.useIn === 'tobig'){
      this.chartDivPrefix = "ctb_";
    }else{
      this.chartDivPrefix = "ct_";
    }
    if(this.data){
      let css = {width:"100%", height:"100%"};
      if(this.useIn == 'bigscreen'){  //大屏中需要固定宽度高度
        css.width = comp.width+'px';
        css.height = comp.height+'px';
      }
      let ch = h('div', {attrs:{id:this.chartDivPrefix+comp.id}, style:css});
      if(this.islink == true){  //添加返回按钮
        return h('div', {style:{width:"100%", height:"100%"}}, [h('span', {class:"eventback"}, [h('span', {class:"label label-primary", on:{click:()=>{
          this.linkBack();
        }}, domProps:{innerHTML:"<i class=\"fa fa-arrow-left\"></i>返回"}})]), ch]);
      }else{
        return h('div', {style:{width:"100%", height:"100%"}}, [ch]);
      }
    }else if(this.error){
      return h('div', this.error);
    }else{
      if(this.editor === true){
        if(this.useIn === 'bigscreen'){
          return h("div", "未配置图表");
        }else if(this.useIn==='dashboardEdit'){
          return h('div', {attrs:{align:"center", class:"tipinfo"}, domProps:{innerHTML:"(未配置"+utils.getCompTypeDesc(comp.type)+"数据)"}});
        }else{
          return h('div', {attrs:{align:"center", class:"tipinfo"}, domProps:{innerHTML:"(点击<i class=\"fa fa-wrench\"></i>按钮配置"+utils.getCompTypeDesc(comp.type)+")"}});
        }
      }else{
        return h('div','数据加载中...');
      }
    }
  },
  mounted(){
    //正在报表中使用 editor 属性
    if(this.useIn==='report'){
      if(this.editor === true){
        this.chartView();
        if(this.autuFlush == true){
          utils.autoFlush(this.comp, ()=>this.chartView());
        }
      }
    }else{
      //获取参数值
      dutils.setCompDefValue(this.comp.comp, this.pageInfo);
      this.chartView();
       if(this.autuFlush == true){
          utils.autoFlush(this.comp, ()=>this.chartView());
       }
    }
  },
  computed: {
  },
  methods: {
     //通用方法，view
    view(isDisposed){
      this.chartView(isDisposed);
    },
    /**
     *  isDisposed : true/false 表示是否释放 图形对象，在切换图形类型的时候需要
     */
    chartView(isDisposed){
      let ts = this;
      let json = this.comp.comp;
      if(!json){
        return;
      }
      if(!json.kpiJson){
        return;
      }
      if((!json.mkpi || json.mkpi==false )&& json.kpiJson[0] == null){
        return;
      }
      if((json.mkpi == true) && (!json.mkpiJson || json.mkpiJson.length === 0) ){
        return;
      }
      if(json.chartJson.type == "scatter" && json.kpiJson[1] == null  ){
        return;
      }
      if(json.chartJson.type == "bubble" && json.kpiJson[2] == null  ){
        return;
      }
      this.styleType = this.pageInfo&&this.pageInfo.style ? this.pageInfo.style.styleType:null;

      json = JSON.parse(JSON.stringify(json));
      //如果是仪表盘，需要带的参数是 globalParams
      if("dashboard" == this.useIn || "tobig" == this.useIn || "dashboardEdit" == this.useIn){
        json.portalParams = ts.pageInfo.globalParams;
      }else{
        json.portalParams = ts.portalParams ;
      }
      //处理指标定制(多指标图形)
      if(json.mkpi==true && json.mkpiJson){
        //处理指标定制
        if(this.comp.kpiCustomized == true){
          let selKpis = JSON.parse(localStorage.getItem("c_" + this.comp.id) || "[]");
          if(selKpis.length > 0) {
            $(json.mkpiJson).each((a, b)=>{
              b.hideNode = true;
              if(selKpis.indexOf(b.alias) >= 0 ){
                b.hideNode = false;
              }
            });
            json.mkpiJson = json.mkpiJson.filter(m=>m.hideNode == false);
          }
        }
      }
      json.useIn = this.useIn;
      json.styleType = this.styleType;
      let prefix = "#" + this.useIn;
      //let target = document.querySelector(prefix+json.id+" div."+(this.useIn ==='bigscreen'?"comp_item":"ccctx")+"");
      let target = document.querySelector(prefix+this.comp.id);
      let loadingInstance = Loading.service({fullscreen:false, background:utils.getLoadingbackground(this.styleType),  target}); //text:"加载中...", spinner:"el-icon-loading",
      let url = "portal"+(this.token?"/share":"")+"/ChartView.action" + (this.token?"?token="+this.token:"");
      ajax({
        url:url,
        type:"POST",
        data:JSON.stringify(json),
        postJSON:true,
        success:(resp)=>{
          ts.data = resp.rows;
          ts.error = null;
          loadingInstance.close();
          ts.$nextTick(()=>ts.showChart(isDisposed));
        }
      }, this, loadingInstance, (resp)=>{
          if(resp.result == 0){
            ts.error = resp.msg;
            ts.data = null;
          }
      });
    },
    //事件点击返回按钮
    linkBack(){
      this.islink = false;
      if(this.useIn === 'report'){
        utils.compBackEvent(this.comp.comp.chartJson.link, this);
      }else if(this.useIn === 'dashboard'){
        this.$parent.$parent.$parent.$parent.compLinkBack(this.comp);
      }
    },
    /**
     * 调用echarts渲染图形
     */
    showChart(isDisposed){
      let ts = this;
      let comp = this.comp.comp;

      const exec = ()=>{
        let option = loopChartJson(this.data);
        let o = document.getElementById(this.chartDivPrefix+comp.id);
        if(!o){
          return;
        }
        /**
        if(option == null){
          let myChart = echarts.getInstanceByDom(o);
          if(myChart){
            myChart.dispose();
          }
          $(o).html("<div align='center'>无数据</div>");
          return;
        }
         */
        if(option.result === 0){
           let myChart = echarts.getInstanceByDom(o);
          if(myChart){
            myChart.dispose();
          }
           $(o).html("<div align='center' style='padding:10px;'>图形未查询出数据.</div>");
           return;
        }
        let myChart = echarts.getInstanceByDom(o);
        if(isDisposed == true){  //重新释放图形对象
          if(myChart && !myChart.isDisposed()){
          myChart.dispose();
          }
          myChart = echarts.init(document.getElementById(this.chartDivPrefix+comp.id), "default", {width:"auto", height:"auto"});
        }else{
          if(!myChart){
             myChart = echarts.init(document.getElementById(this.chartDivPrefix+comp.id), "default", {width:"auto", height:"auto"});
          }
        }
        if(this.editor == true){
          //在编辑模式移除chart 的 tooltip className
          if(comp.chartJson.type == 'map' && option.tooltip){
            delete option.tooltip.className;
          }
        }
        myChart.setOption(option, true);
       // if(this.editor === true){  //编辑模式
          //设置图形显示颜色, 此功能已经移至图形属性设置界面
          /**
          if(this.useIn==='report'){
            myChart.off("click").on('click', function(params){
              ts.$parent.$refs['ChartSeriesColorForm'].showDailog(comp, params);
            });
          }
           */
        //}else{  //浏览模式，设置图形点击事件
          if(comp.chartJson.type == 'gauge2'){  //gauge2 <比例图> 不支持事件
              return;
          }
          if(comp.chartJson.link && comp.chartJson.link.target && comp.chartJson.link.target.length > 0){
            myChart.off("click").on('click', function(params){
                //报表和仪表盘链接方式不一样
                if(ts.useIn === 'report'){
                  utils.compFireEvent(comp.chartJson.link, ts, comp.chartJson.link.paramName, params.name);
                  ts.islink = true;
                }else if(ts.useIn === 'dashboard'){
                  ts.islink = ts.$parent.$parent.$parent.$parent.compLink(ts.comp, params.name, comp.chartJson.link.paramName);
                }
            });
          }else if(comp.chartJson.link && comp.chartJson.link.reportId){
            if(ts.useIn === 'report'){
              //图形上链接到报表
              myChart.off("click").on('click', function(params){
                  //报表和仪表盘链接方式不一样
                  let pname = utils.getLinkParam(ts.comp.comp, ts.comp.type).key;
                  utils.compLink(ts.comp, ts, pname, params.name);
              });
            }else if(ts.useIn === 'dashboard'){
              //链接到仪表盘
              myChart.off("click").on('click', function(params){
                let pname = utils.getLinkParam(ts.comp.comp, ts.comp.type).key;
                ts.$parent.$parent.$parent.$parent.compLink2Dashboard(ts.comp, pname, params.name, comp.chartJson.link.reportId);
              });
            }
          }else{
            //没有事件，取消 图形 click
            myChart.off("click");
          }
        //}
        //注册 pie2 的 轮播事件
        if(comp.chartJson.type === 'pie2'){
          let currentIndex = 0;
          let  scollIdx = 0;
          const carousel = ()=>{
            if(scollIdx == 0 || scollIdx % 170 == 0) {
              option.series[0].data[currentIndex].selected=false;
              var dataLen = option.series[0].data.length;
              var sumSize = 0;
              for(let j=0; j<dataLen; j++){
                sumSize = sumSize + Number(option.series[0].data[j].value);
              }
              currentIndex = (currentIndex + 1) % dataLen;
              option.series[0].data[currentIndex].selected=true;
              var v = option.series[0].data[currentIndex].value;
              option.title.text = echartsUtils.formatNumber(v/sumSize, '0.00%');
              option.title.subtext = option.series[0].data[currentIndex].name;
              myChart.setOption(option, true);
            }
            scollIdx++;
            if(scollIdx >= 10000){
              scollIdx = 0;
            }
            this.carouselId = requestAnimationFrame(carousel);
          }
          if(this.carouselId){
            cancelAnimationFrame(this.carouselId);
          }
          carousel();
          /**
           * 此处要考虑轮播取消。
           * 取消放在 beforeDestroy 过程中
           */
        }
        //注册地图的Tooltip轮播事件
        if(comp.chartJson.type === 'map' && comp.chartJson.intervalTp == true && this.editor == false){
          let scollIdx = 0;
          let dataIndex = 0;
          let cnt = option.series[0].data.length;
          const carousel = ()=>{
            if(scollIdx == 0 || scollIdx % 170 == 0) {
              //取消高亮
              myChart.dispatchAction({
                type:"downplay",
                seriesIndex:0,
              });
              //高亮
              myChart.dispatchAction({
                type:"highlight",
                seriesIndex:0,
                dataIndex:dataIndex,
              });
              //显示tooltip
              myChart.dispatchAction({
                type:"showTip",
                seriesIndex:0,
                dataIndex:dataIndex,
              });
              dataIndex++;
              if(dataIndex > cnt){
                dataIndex = 0;
              }
            }
            scollIdx++;
            if(scollIdx >= 10000){
              scollIdx = 0;
            }
            this.carouselId = requestAnimationFrame(carousel);
          }
          carousel();
        }
        if(comp.chartJson.type === 'map' && comp.chartJson.typeIndex == 6){  //热力图，需要设置热力效果
          echartsUtils.heatMapSet(comp, option.data, myChart, o);
        }
      }
      let tpIndex = comp.chartJson.typeIndex;
      //两种情况下加载地图， 1. 类型为地图组件， 2. 类型为自定义组件，并且 maparea 存在
      if((comp.chartJson.type === 'map' && (tpIndex == 2 || tpIndex == 3 ||  tpIndex == 4 || tpIndex == 5)) ||
          comp.chartJson.type === 'custom' && comp.chartJson.maparea ){
         let maparea = comp.chartJson.maparea || "china";
         var u = baseUrl + "chartjson/city-baidu.json";
         if($.isEmptyObject(echartsUtils.cityPosJson)){
            $.getJSON(u, {}, (cityjson)=>{
              for(let k in cityjson){
                echartsUtils.cityPosJson[k] = cityjson[k];
              }
              var u2 = baseUrl + "chartjson/"+maparea+".json";
                $.getJSON(u2, {}, (resp)=>{
                    if(comp.chartJson.typeIndex == 4){ //百度地图
                      if(typeof BMap === 'undefined'){
                        var url = 'https://api.map.baidu.com/getscript?v=3.0&ak=' + this.data.ak;
                        $.getScript(url, function(){
                          $.getScript( baseUrl + "chartjson/bmap.js", function(){
                            exec();
                          });
                        });
                      }else{
                        exec();
                      }
                    }else{
                      echarts.registerMap(maparea, resp);
                      exec();
                    }
              });
            });
         }else{
              var u2 = baseUrl + "chartjson/"+maparea+".json";
                $.getJSON(u2, {}, (resp)=>{
                  if(comp.chartJson.typeIndex == 4){ //百度地图
                    if(typeof BMap === 'undefined'){
                        var url = 'https://api.map.baidu.com/getscript?v=3.0&ak=' + this.data.ak;
                        $.getScript(url, function(){
                          $.getScript( baseUrl + "chartjson/bmap.js", function(){
                            exec();
                          });
                        });
                    }else{
                        exec();
                      }
                  }else{
                    echarts.registerMap(maparea, resp);
                    exec();
                  }
              });
         }

      //两种情况下加载地图， 1. 类型为地图组件， 2. 类型为自定义组件，并且 maparea 存在
      }else if(comp.chartJson.type === 'map' || comp.chartJson.type === 'custom' && comp.chartJson.maparea){ //地图，需要加载地图JSON数据
         let maparea = comp.chartJson.maparea || "china";
         var u = baseUrl + "chartjson/"+maparea+".json";
         $.getJSON(u, {}, (resp)=>{
            echarts.registerMap(maparea, resp);
            exec();
         });
      }else{
        exec();
      }
    }
  },
  watch: {

  },
  beforeDestroy(){
    //终止轮播事件
    if(this.carouselId){
      cancelAnimationFrame(this.carouselId);
    }
    utils.stopFlush(this.comp);
  },
}
</script>

<style lang="less" scoped>
.eventback {
	position:absolute;
	width:50px;
	right:5px;
  cursor:pointer;
  display:block;
	z-index:1100;
}
.tipinfo {
  color: #999;
  padding: 10px;
}
</style>
