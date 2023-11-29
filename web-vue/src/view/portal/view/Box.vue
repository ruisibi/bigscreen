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
import * as dutils from '@/view/dashboard/Utils'

export default {
  components:{
  },
  data(){
    return {
      data:null,
      styleType:null, //风格类型，白色背景还是黑色背景
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
    this.styleType = this.pageInfo&&this.pageInfo.style ? this.pageInfo.style.styleType:null;
    let comp = this.comp;
    if(this.data){
      //处理样式
      let cs = comp.style;
      let style = {};
      let size = cs && cs.fontsize?cs.fontsize : 32;
      style["font-size"] = size + "px";

      if(cs && cs.fontcolor){
        style['color'] = cs.fontcolor;
      }
      if(cs && cs.color1 && cs.color2){
          style['background-image'] = 'linear-gradient(180deg, '+cs.color1+' 0%, '+cs.color2+' 100%)';
          style['-webkit-background-clip'] = 'text';
          style['color'] = 'transparent';
      }
      if(cs && cs.family){
        style['font-family'] = "'"+cs.family + "'";
      }
      let align = cs && cs.textAlign?cs.textAlign:"center";
      style['text-align'] = align;
      style['justify-content'] = align;

      //带同环比，高度减少52
      if(comp && comp.comp && comp.comp.thbDim && comp.comp.thbDim.id){
        style['height'] = "calc(100% - 52px)";
      }else
      //带进度条，高度减少50
      if(comp && comp.comp && comp.comp.progressBar){
        style['height'] = "calc(100% - 50px)";
      }else
      //带图形，高度减少60
      if(comp && comp.comp && comp.comp.chart){
        style['height'] = "calc(100% - 60px)";
      }else{
        style['height'] = "100%";
      }

      if(cs && cs.weight){
        style['font-weight'] = "bold";
      }
      if(cs && cs.titalic){
        style['font-style'] = "italic";
      }
      if(cs && cs.underscore){
        style['text-decoration'] = "underline";
      }
      if(cs && cs.bgcolor){
        style['background-color'] = cs.bgcolor;
      }

      //处理单位，rate
      var v = this.data.value;
      var rate = utils.findRateName(this.data.rate);
      if(rate){
        v += rate;
      }
      if(this.data.unit){
        v += this.data.unit;
      }
      if(this.comp && this.comp.comp && this.comp.comp.progressBar){ //带进度条
        let v2 = this.data.trueValue ? this.data.trueValue * 100 : 0;
        let progressBarColor = "red";
        if(this.comp.style && this.comp.style.progressBarColor){
          progressBarColor = this.comp.style.progressBarColor;
        }
        let prog = h('el-progress', {props:{"text-inside":true, color:progressBarColor,"stroke-width":20, "percentage":v2}});
        prog = h('div', {style:{margin:"10px 5px 10px 5px"}}, [prog]); //用div封装进度条
        return h('div', {style:{width:"100%", height:"100%"}}, [h('div', {class:"boxcls", style:style,domProps:{innerHTML:v}}), prog]);
      }else if(this.comp && this.comp.comp && this.comp.comp.thbDim && this.comp.comp.thbDim.id){ //带同环比
        let tb = echartsUtils.formatNumber(this.data.tb, '0.00%');
        let hb = echartsUtils.formatNumber(this.data.hb, '0.00%');
        let s = tb +
          (this.data.tb > 0 ? "<i class='fa fa-long-arrow-up' style='color:#ff2300;'></i>" :"<i class='fa fa-long-arrow-down' style='color:#70AD47;'></i>") +
          "<br/>" +
          hb +
          (this.data.hb  > 0 ? "<i class='fa fa-long-arrow-up' style='color:#ff2300;'></i>" :"<i class='fa fa-long-arrow-down' style='color:#70AD47;'></i>");
        let thb = h('div', {class:"box-thb"},
          [
            h('div', {class:"stat-percent", domProps:{innerHTML:s}}),
            h('small', {domProps:{innerHTML:"同比：<br/>环比："}})
          ]
        );
        return h('div', {style:{width:"100%", height:"100%"}}, [h('div', {class:"boxcls", style:style,domProps:{innerHTML:v}}), thb]);
      }else if(this.comp && this.comp.comp && this.comp.comp.chart){  //带图形
        let chart = this.comp.comp.chart;
        let kpi = h('div', {class:"boxcls", style:style,domProps:{innerHTML:v}});
        let ct = h('div', {attrs:{id: this.useIn + chart.id}, style:{height:"60px"}})
        //加载图形
        this.$nextTick(()=>this.showChart(chart));
        return h('div', {style:{width:"100%", height:"100%"}}, [kpi, ct]);
      }else{
        //生成数字背景
        if(cs.kpiBg == true){
          var s = cs;
          let arrays = [];
          //kpiLength 表示指标长度大于 实际长度，需要补0
           if(s && s.kpiLength && s.kpiLength > v.length){
            for(let j=0; j<s.kpiLength - v.length; j++){
              arrays.push("-1");
            }
          }
          for(let l=0; l<v.length; l++){
              arrays.push( v.charAt(l) );
          }
          let numberStyle = {};
          let hs = [];
          $(arrays).each((c, d)=>{
              if(s && s.fontsize){//字体大小12，对应大小是18
                  var size = 16/ 12 * s.fontsize;
                  numberStyle['width'] = size + "px";
                  numberStyle['height'] = size + "px";
                  numberStyle['line-height'] = size + "px";
              }
              if(s && s.kpiBgColor){
                numberStyle['background-color'] = s.kpiBgColor;
              }
              if(s && s.kpiBgImg){
                  numberStyle["background"] = "url(bigscreen/"+s.kpiBgImg+") center no-repeat";
                  numberStyle['background-size'] = "100% 100%"
              }
              if(s && s.kpiSpacing){
                numberStyle['margin-right'] = s.kpiSpacing + "px";
              }
              if(s && s.family){
                numberStyle['font-family'] = "'"+s.family + "'";
              }
              hs.push(h('span', {class:"boxNumber"+(d=='-1'?" boxNumber-zwf":""), style:numberStyle}, d == '-1' ? '0': d));
          });
          return h('div', {class:"boxcls", style:style}, hs);
        }else{
          return h('div', {class:"boxcls", style:style, domProps:{innerHTML:v}});
        }
      }
    }else{
      if(this.editor === true){
        if(this.useIn === 'bigscreen'){
          return h('div', "未配置数据块");
        }else if(this.useIn === 'dashboard' || this.useIn === 'dashboardEdit'){
          return h('div', {attrs:{align:"center"}, class:"tipinfo", domProps:{innerHTML:"(还未配置"+utils.getCompTypeDesc(comp.type)+"数据)"}});;
        }else{
          return h('div', {attrs:{align:"center"}, class:"tipinfo", domProps:{innerHTML:"(点击<i class=\"fa fa-wrench\"></i>按钮配置"+utils.getCompTypeDesc(comp.type)+")"}});
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
        this.boxView();
        if(this.autuFlush == true){
          utils.autoFlush(this.comp, ()=>this.boxView());
        }
      }
    }else{
      //获取参数值
      dutils.setCompDefValue(this.comp.comp, this.pageInfo);
      this.boxView();
      if(this.autuFlush == true){
        utils.autoFlush(this.comp, ()=>this.boxView());
      }
    }
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
      this.boxView();
    },
    boxView(){
      let ts = this;
      let comp = this.comp.comp;
      if(!comp){
        return;
      }
      if(comp.thbDim && !comp.thbDim.id){  //带同环比，但未配置时间维度
        return;
      }
      //配置了图形未配置图形指标
      if(comp.chart && (!comp.chart.kpiJson || comp.chart.kpiJson.length == 0)){
        return;
      }
      if(comp.kpiJson){
          let json = JSON.parse(JSON.stringify(comp));
          json.portalParams = ts.portalParams;
          json.useIn = this.useIn;
          let prefix = "#" + this.useIn;
          let target = document.querySelector(prefix+json.id);
          //let target = document.querySelector(prefix+json.id+" div."+(this.useIn ==='bigscreen'?"comp_item":"ccctx")+"");
          let loadingInstance = Loading.service({fullscreen:false, background:utils.getLoadingbackground(this.styleType), target}); //text:"加载中...", spinner:"el-icon-loading",
          let url = "portal"+(this.token?"/share":"")+"/BoxView.action" + (this.token?"?token="+this.token:"");
          ajax({
            url:url,
            type:"POST",
            data:JSON.stringify(json),
            postJSON:true,
            success:(resp)=>{
              ts.data = resp.rows;
              loadingInstance.close();
            }
          }, this, loadingInstance, (resp)=>{
            if(resp.result == 0){
              ts.data = {value: "X"};  //系统报错，构建错误对象
            }
          });
      }
    },
    showChart(chart){
        let o = document.getElementById(this.useIn+chart.id);
        if(!o){
          return;
        }
        let myChart = echarts.getInstanceByDom(o);
        if(!myChart){
          myChart = echarts.init(document.getElementById(this.useIn+chart.id), "default");
        }
        let option = loopChartJson(this.data[chart.id]);
        myChart.setOption(option, true);

    },
  },
  watch: {

  },
  beforeDestroy(){
    utils.stopFlush(this.comp);
  },
}
</script>

<style lang="less" scoped>
.stat-percent {
    float: right;
}
.tipinfo {
  color: #999;
  padding: 10px;
}
.boxcls {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: justify;
    width: 100%;
}
.box-thb {
  padding: 5px;
}
.boxNumber {
	display: inline-block;
	width: 18px;
	height: 18px;
	border-radius: 4px;
	background-color: #0b3f65;
	margin-right: 2px;
	text-align: center;
	line-height: 18px;
}
.boxNumber-zwf {
  color: #202596 !important;
}
</style>
