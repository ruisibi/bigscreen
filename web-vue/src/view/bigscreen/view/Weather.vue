<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 天气组件 -->
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
      data:null,
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
    let s = {width:"100%", height:"100%"};
    let txt = "天气信息加载中...";
    if(this.data){
      if(!comp.style || !comp.style.showType || comp.style.showType.length == 0){
        txt = this.data.city + " " + this.data.weather + " " + this.data.temperature;
      }else{
        txt = "";
        if(comp.style.showType.indexOf('city') >= 0){
          txt += this.data.city;
        }
        if(comp.style.showType.indexOf('weather') >= 0){
          txt += " " + this.data.weather;
        }
        if(comp.style.showType.indexOf('temperature') >= 0){
          txt += " " + this.data.temperature;
        }
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
    return h('div', {style:style}, txt);
  },
  mounted(){
    this.loadWeather();
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
      this.loadWeather();
    },
    loadWeather(){
      let city = null;
      if(this.comp.style && this.comp.style.city){
        city = this.comp.style.city;
      }
      ajax({
        url:"bigscreen/weather/get.action",
        type:"GET",
        data:{city: city},
        success:(resp)=>{
          this.data = resp.rows;
        }
      });
    },
    //自动刷新时间
    refresh(){

    }
  },
  watch: {

  },
  beforeDestroy(){

  },
}
</script>
