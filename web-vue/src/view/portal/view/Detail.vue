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
import * as dutils from '@/view/dashboard/Utils'

export default {
  components:{
  },
  data(){
    return {
      data:null,
      coc:null, //鼠标悬停颜色
      styleType:null, //风格类型，白色背景还是黑色背景
      error: null, //错误信息
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
    let tstyle = comp.tstyle;  //组件的样式放在 tstyle 中
    if(this.data){
      let tr = [];
      $(this.data).each((a, b)=>{
        let tds = [];
        let brtds = []; //如果有换行，需要新生成一行td
        $(b).each((c, d) =>{
           if(d){
             let style = {};
             if(d.align){
               style['text-align'] = d.align;
             }
             let dtstyle = {};
             if(tstyle && tstyle.kpicolor){
               dtstyle['color'] = tstyle.kpicolor;
             }
             if(tstyle && tstyle.kpisize){
               dtstyle['font-size'] = tstyle.kpisize+"px";
             }
             //判断是否换行
             let td1 = h('td', {class:"detail-head", style:style}, d.desc);
             let td2 = h('td', {class:"detail-data", style:dtstyle}, d.value);
             if(tstyle && tstyle.brdata == true){
                tds.push(td1);  //表头
               brtds.push(td2); //表内容
             }else{
                tds.push(td1);  //表头
                tds.push(td2); //表内容
             }
           }
        });
        if(tstyle && tstyle.tableMousecolor){
          this.coc = tstyle.tableMousecolor;
        }else{
          this.coc = this.styleType==='bigscreen' ? "#0a6aa1" : "#FFFFD2";  //鼠标悬停默认色
        }
        this.$nextTick(()=>this.tableHover());
        tr.push(h('tr', {class:"t-row"}, tds));
        if(brtds.length > 0){
          tr.push(h('tr', {class:"t-row"}, brtds));
        }
      });
      let table = h('table', {class:"grid-detail", attrs:{cellpadding:"0", cellspacing:"0"}}, [h('tbody', tr)]);
      let bs = {width:"100%", height:"100%"};
      if(tstyle && tstyle.tableBgcolor){
        bs['background-color'] = tstyle.tableBgcolor;
      }
      if(tstyle && tstyle.color){
        bs['color'] = tstyle.color;
      }
      if(tstyle && tstyle.size){
        bs['font-size'] = tstyle.size+"px";
      }
      return h('div', {style:bs}, [table]);
    }else if(this.error){
      return h('div', this.error);
    }else{
      if(this.editor === true){
        if(this.useIn === 'bigscreen'){
           return h('div', '未配置详情页');
        }else if(this.useIn === "dashboardEdit"){
            return h('div', {attrs:{align:"center"}, class:"tipinfo", domProps:{innerHTML:"(未配置"+utils.getCompTypeDesc(comp.type)+"数据)"}});
        }else{
          return h('div', {attrs:{align:"center"}, class:"tipinfo", domProps:{innerHTML:"(点击<i class=\"fa fa-wrench\"></i>按钮配置"+utils.getCompTypeDesc(comp.type)+")"}});
        }
      }else{
        return h('div','无数据...');
      }
    }
  },
  mounted(){
    //正在报表中使用 editor 属性
    if(this.useIn==='report'){
      if(this.editor === true){
        this.detailView();
        if(this.autuFlush == true){
          utils.autoFlush(this.comp, ()=>this.detailView());
        }
      }
    }else{
      //获取参数值
      dutils.setCompDefValue(this.comp.comp, this.pageInfo);
      this.detailView();
      if(this.autuFlush == true){
        utils.autoFlush(this.comp, ()=>this.detailView());
      }
    }
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
      this.detailView();
    },
    tableHover(){
      //解决鼠标悬停颜色问题
      this.$el.style.setProperty('--coc',  this.coc);
    },
    detailView(){
      let detail = this.comp.comp;
      detail.useIn = this.useIn;
      if(!detail.cols || detail.cols.length===0){
          return;
      }
      let prefix = "#" + this.useIn;
      let target = document.querySelector(prefix+detail.id);
      //let target = document.querySelector(prefix+detail.id+" div."+(this.useIn ==='bigscreen'?"comp_item":"ccctx")+"");
      let loadingInstance = Loading.service({fullscreen:false,background:utils.getLoadingbackground(this.styleType), target});
      let url = "portal"+(this.token?"/share":"")+"/detailView.action" + (this.token?"?token="+this.token:"");
      ajax({
          type: "POST",
          url: "portal/detailView.action",
          dataType:"json",
          data: JSON.stringify(detail),
          postJSON:true,
          success: (resp)=>{
             this.data = resp.rows;
             this.error = null;
          }
      }, this, loadingInstance, (resp)=>{
          if(resp.result == 0){
               this.error = resp.msg;
               this.data = null;
            }
      });
    }
  },
  watch: {

  },
  beforeDestroy(){
    utils.stopFlush(this.comp);
  },
}
</script>
<style lang="less" scoped>
table.grid-detail {
    width: 100%;
    height: 100%;
}
table.grid-detail td.detail-head {
    font-weight: bold;
    padding: 3px;
}
table.grid-detail td.detail-data {
    padding: 3px;
}
tr.t-row:hover {
	background-color: var(--coc) !important;
}
.tipinfo {
  color: #999;
  padding: 10px;
}
</style>
