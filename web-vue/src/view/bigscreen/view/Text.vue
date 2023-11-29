<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 文本组件 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  components:{

  },
  data(){
    return {
      carouselId: null, //轮播ID
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
  render(h){
    let ts = this;
    let comp = this.comp;
    let s = bsUtils.createTextStyle(comp);
    this.$nextTick(()=>{
      let s = this.comp.style;
      if(s && s.scroll == true){
        this.scoll();
      }else{
        if(this.carouselId){
          cancelAnimationFrame(this.carouselId);
        }
      }
    });
    return h('div', {style:s, class:"text-comp", domProps:{innerHTML:comp.content}});
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
    },
    //滚动
    scoll(){
      let ts = this;
      let useIn = this.useIn;
      let compId = this.comp.id;
      let  scollIdx = 0;
      var idx = 1;
      var o = $("#"+useIn+compId+" .text-comp");
      var curHeight = Math.round($("#"+useIn+compId+" .comp_body").height());
      var tableHeight = o.height();
      const carousel = ()=>{
        if(scollIdx != 0 && scollIdx % 120 == 0) {
          let lineHeight = this.comp.style && this.comp.style.lineheight ? this.comp.style.lineheight:23;
          var h = idx * lineHeight;
          o.animate({"margin-top": "-" + Math.round(h) + "px"});
          idx++;
          if (h > tableHeight - curHeight) {
            idx = 0;
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
