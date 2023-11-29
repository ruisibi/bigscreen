<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 嵌套iframe组件 -->
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
    let txt = "未配置URL";
    if(comp.style && comp.style.url){
      txt = '<iframe scrolling="no" src="'+comp.style.url+'" frameborder="0" width="100%" height="100%" allowtransparency="true"></iframe>';
    }
    return h('div', {style:s, domProps:{innerHTML:txt}});
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
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
