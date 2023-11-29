<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 图片组件 -->
<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import * as echartsUtils from '@/common/echartsUtils'

export default {
  components:{
  },
  data(){
    return {

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
    if(comp.src){
      let s = {};
      let className = "";
      let u = comp.src;
      s["width"] = "100%";
      s['height'] = "100%";
      if(comp.animation == "rotate"){
        className = "comp-turn";
      }else if(comp.animation == 'translate'){
        className = "comp-translate";
      }else if(comp.animation == 'translate-left'){
        className = "comp-translate-left";
      }
       if(comp.src.startsWith('http')){
          s['background-image'] = "url(" + comp.src + ")";
       }else{
          s['background-image'] = "url(" + baseUrl + "bigscreen/" + comp.src + ")";
       }
      s['background-size'] = "100% 100%";
      if(comp.opacity){
        s['opacity'] = comp.opacity / 100;
      }
      if(comp.duration) {
        s["animation-duration"] = comp.duration + "s";
      }
      return h('div', {style:s, class:className});
    }else{
      if(this.editor === true){
        if(this.useIn === 'bigscreen'){
           return h('div', '未配置图片路径');
        }else{
          return h('div', {attrs:{align:"center"}, class:"tipinfo", domProps:{innerHTML:"(点击<i class=\"fa fa-wrench\"></i>按钮添加"+utils.getCompTypeDesc(comp.type)+")"}});
        }
      }else{
        return h('div','数据加载中...');
      }
    }
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){

    },
  },
  watch: {

  }
}
</script>
<style lang="less" scoped>
/** 旋转样式 */
@keyframes rotate{
	from{transform: rotate(0deg)}
	to{transform: rotate(359deg)}
}
.comp-turn {
	animation:rotate 5s linear infinite;
}
/** 移动样式 */
@keyframes translate {
	0% {
      transform: translate(0px, 0px);
  }
  25% {
      transform: translate(0px, -10px);
  }
  50% {
      transform: translate(0px, 0px);
  }
  75% {
      transform: translate(0px, 10px);
  }
  100% {
      transform: translate(0px, 0px);
  }
}
.comp-translate {
	animation:translate 2s linear infinite;
}

@keyframes translate-left {
	0% {
      transform: translate(0px, 0px);
  }
  25% {
      transform: translate(-10px, 0px);
  }
  50% {
      transform: translate(0px, 0px);
  }
  75% {
      transform: translate(10px, 0px);
  }
  100% {
      transform: translate(0px, 0px);
  }
}
.comp-translate-left {
	animation:translate-left 2s linear infinite;
}
</style>
