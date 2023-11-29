<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'
import * as echartsUtils from '@/common/echartsUtils'

export default {
  components:{
  },
  data(){
    return {

    }
  },
  props:{
      comp:{
        type:Object,
        required:true,
        default:()=>{}
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
    if(comp.picurl){
      let u = comp.picurl;
      u = u.startsWith('http') ? u : baseUrl + u;
      console.log(u);
      return h('img', {attrs:{id:"pic"+comp.id, src: u}, style:{width:"100%", height:"100%"}});
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
