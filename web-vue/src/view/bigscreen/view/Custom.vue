<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 自定义组件 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'
import * as dutils from '@/view/dashboard/Utils'
import kpiLine from './impl/KpiLine'
import sector from './impl/Sector'
import earth3d from './impl/Earth3d'
import map3d from './impl/Map3D'
import pyramid3d from './impl/Pyramid3D'

export default {
  components:{
    kpiLine, sector, earth3d, map3d, pyramid3d
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
      token:{
          type:String,
          required: false
      }
  },
  render(h){
    let ts = this;
    this.styleType = this.pageInfo&&this.pageInfo.style ? this.pageInfo.style.styleType:null;
    let comp = this.comp;
    let s = {width:"100%", height:"100%"};
    if(!comp.impl){ //未定义实现
      return h('div', {style:s, domProps:{innerHTML:"未配置自定义组件的实现"}});
    }
    if(comp.impl == 'kpiline'){  //指标连接线
      return h('div', {style:s}, [h('kpiLine', {ref:"v_"+comp.id,attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, portalParams:this.portalParams, token:this.token,  editor:this.editor}})]);
    }else if(comp.impl == 'sector'){
      return h('div', {style:s}, [h('sector', {ref:"v_"+comp.id,attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, portalParams:this.portalParams, token:this.token,  editor:this.editor}})]);
    }else if(comp.impl == 'earth3d'){
      return h('div', {style:s}, [h('earth3d', {ref:"v_"+comp.id,attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, portalParams:this.portalParams, token:this.token,  editor:this.editor}})]);
    }else if(comp.impl == 'map3d'){
      return h('div', {style:s}, [h('map3d', {ref:"v_"+comp.id,attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, portalParams:this.portalParams, token:this.token,  editor:this.editor}})]);
    }else if(comp.impl == 'pyramid3d'){
      return h('div', {style:s}, [h('pyramid3d', {ref:"v_"+comp.id,attrs:{comp:comp, pageInfo:this.pageInfo, useIn:this.useIn, portalParams:this.portalParams, token:this.token,  editor:this.editor}})]);
    }
  },
  mounted(){
     //获取参数值
    dutils.setCompDefValue(this.comp.comp, this.pageInfo);
    this.customView();
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
      this.customView();
    },
    //自动刷新时间
    refresh(){

    },
    //调整组件大小 (通用方法)
    resize(){
      let ts = this;
      let comp = this.comp;
      if(ts.$refs['v_'+comp.id]){
        ts.$refs['v_'+comp.id].resize();
      }
    },
    customView(){
      let ts = this;
      let comp = this.comp.comp;
      if(!comp.kpi){
        return;
      }

      let json = JSON.parse(JSON.stringify(comp));
      json.portalParams = ts.portalParams;
      json.useIn = this.useIn;
      json.kpis = [json.kpi];
      delete json.kpi;
      let prefix = "#" + this.useIn;
      let target = document.querySelector(prefix+json.id);
      let loadingInstance = Loading.service({fullscreen:false, background:utils.getLoadingbackground(this.styleType), target}); //text:"加载中...", spinner:"el-icon-loading",
      let url = "portal"+(this.token?"/share":"")+"/CustomView.action" + (this.token?"?token="+this.token:"");
      ajax({
        url:url,
        type:"POST",
        data:JSON.stringify(json),
        postJSON:true,
        success:(resp)=>{
          ts.data = resp.rows;
          loadingInstance.close();
          //数据更新，刷新自定义组件
          if(ts.$refs['v_'+comp.id]){
            ts.$refs['v_'+comp.id].refreshData(ts.data);
          }
        }
      }, this, loadingInstance);

    },

  },
  watch: {

  },
  beforeDestroy(){

  },
}
</script>
