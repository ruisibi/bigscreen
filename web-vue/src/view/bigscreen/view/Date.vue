<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 日历组件 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import * as echartsUtils from '@/common/echartsUtils'

export default {
  components:{
  },
  data(){
    return {
      carouselId:null, //轮播ID
      weeks:['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
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
  },
  render(h){
    let ts = this;
    let comp = this.comp;
    let dt = new Date();
    let week = dt.getDay();
    let date = formatDate(dt, "yyyy年MM月dd日");
    let time = formatDate(dt, "HH:mm:ss");
    let s = "";
    if(!comp.style || !comp.style.showType || comp.style.showType.length == 0){
      s += date + " " + this.weeks[week] + ' ' +time;
    }else{
     if(comp.style.showType.indexOf('date') >= 0){
       s += date;
     }
     if(comp.style.showType.indexOf('week') >= 0){
       s += " " + this.weeks[week];
     }
     if(comp.style.showType.indexOf('time') >= 0){
       s += " " + time;
     }
    }
    let style = {};
    if(comp.style){
      let st = comp.style;
      if(st.textAlign){
        style['text-align'] = st.textAlign;
      }
      if(st.fontsize){
        style['font-size'] = st.fontsize+'px';
      }
      if(st.fontcolor){
        style['color'] = st.fontcolor;
      }
      if(st.family){
         style['font-family'] = "'"+st.family + "'";
      }
    }
    return h('div', {style:style},s);
  },
  mounted(){
    this.refresh();
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){

    },
    //自动刷新时间
    refresh(){
      let ts = this;
      let currentIndex = 0;
      let  scollIdx = 0;
      const carousel = ()=>{
        if(scollIdx == 0 || scollIdx % 60 == 0) {
          ts.$forceUpdate();
        }
        scollIdx++;
        if(scollIdx >= 10000){
          scollIdx = 0;
        }
        this.carouselId = requestAnimationFrame(carousel);
      }
      carousel();
    }
  },
  watch: {

  },
  beforeDestroy(){
    //终止轮播事件
    if(this.carouselId){
      cancelAnimationFrame(this.carouselId);
    }
  },
}
</script>
