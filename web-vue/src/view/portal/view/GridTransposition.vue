<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
 <!-- 表格设置表格转置后的渲染类 -->
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
      dataSize: null,  //数据的长度(在滚动时，未包含滚动的虚拟数据)
      coc:null, //表格鼠标悬停颜色,
      islink:false, //是否做了事件联动
      styleType:null, //风格类型，白色背景还是黑色背景
      tableHeight:320,
      scrollStyle: {
        scrollWidth: 10,
        scrollThumbColor:'#F5F5F5',
        scrollThumbRadius: 0,
        scrollTrackColor: '#F5F5F5',
      }
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
    this.styleType = this.pageInfo&&this.pageInfo.style ? this.pageInfo.style.styleType:null;
    let comp = this.comp;
    let data = this.data;
    if(data){
      //计算行数
      let rowSize = data.header[0].length;
      //初始化trs
      let headTrs = [];
      for(let i=0; i<rowSize; i++){
        headTrs.push(h('tr', []));
      }

      let tstyle = comp.tstyle;  //组件的样式放在 tstyle 中
      let level = data.header.length;
      let lastNodes = {};
      for(let i=0; i<level; i++){
        let nodes = data.header[i];
        $(nodes).each((c, element)=>{
          if(!element.name){
            return;
          }
           let hasChildren = true;
           if(i + element.rowspan === level){  //最后级节点
            hasChildren = false;
            lastNodes[element.name] = element;
          }
          let s = {};
          if(element.width && element.rowspan == 1){
            s['width'] = element.width + "px";
          }
          let hs = utils.crtTableHeadStyle(tstyle);
          hs['padding'] = "0";
          if(!hs['height']){
            hs['height'] = "30px";
          }
          let v = element.desc;
          let th = h('th', {class:"grid3-td", style:hs, attrs:{colspan:element.rowspan, rowspan:element.colspan}}, [h('div', {class:(hasChildren || element.rowspan == 1 ? "dg-cell":""), style:s, domProps:{innerHTML:v}})]);
          let tr = headTrs[c];
          tr.children.push(th);
        });
      }
      let table1 = h('table', {class:"lockgrid", style:{width:"100%", height:"100%"}}, [h('thead', headTrs)]);

      let trs = [];
      if(data.datas){
        for(let i=0; i<this.data.datas[0].length; i++){
          //表格隔行换色
          let trstyle = {};
          if(tstyle && tstyle.bodyEnableTr == true){
            if(i % 2 === 0){
              trstyle['background-color'] = tstyle.bodyTrColor1 || "#ffffff";
            }else{
              trstyle['background-color'] = tstyle.bodyTrColor2 || '#F5F5F6';
            }
          }
          trs.push(h('tr', {style: trstyle}, []));
        }
        data.datas.forEach((e, idx) => {
          $(e).each((c, d)=>{
            let tr = trs[c];
            let hd = lastNodes[d.alias];
            let nd = d;
             let s = {};
             if(hd.width && hd.width >= 0){
               s['width'] = hd.width + "px";
             }
             let bs = utils.crtTableBodyStyle(tstyle);
             if(hd.align){  //字段上的 align 属性覆盖表格属性上的 align 属性
               bs['text-align'] = hd.align;
             }
             if(!bs['height']){
               bs['height'] = "30px";
             }
             bs['padding'] = "0";
             let v = nd.value != null ? nd.value : "";
             tr.children.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style: bs, attrs:{align:hd.align?hd.align:"center"}}, [h('div', {class:"dg-cell", style:s, domProps:{innerHTML:v}})]));
          });

          /**
          //有事件加上鼠标样式
          if(comp.comp.link){
            trstyle['cursor'] = "pointer";
          }
          */
          /**
          tr.children.push(h('tr', {class:"t-row", style:trstyle, on:{
            click:()=>{
              if(comp.comp.link){
                let c = comp.comp;
                if(this.useIn === 'dashboard'){
                  if(c.link && c.link.reportId){ //链接到仪表盘
                    let colName = comp.comp.link.colName;
                    let node = e.filter(m=>m.alias === colName)[0];
                    let v = node.value;
                    this.$parent.$parent.$parent.$parent.compLink2Dashboard(c, colName, v, c.link.reportId);
                  }
                  //联动组件
                  if(c.link && c.link.target && c.target.length > 0){
                    let colName = comp.comp.link.colName;
                    let node = e.filter(m=>m.alias === colName)[0];
                    let v = node.value;
                    this.islink = this.$parent.$parent.$parent.$parent.compLink(this.comp, v, comp.comp.link.colName);
                  }
                }
              }
            }
          }}, tds));
           */
        });
        //附加 footer 信息
        if(this.data.footer){
          let pos = 0;
          for(let c of this.data.footer){
             let footer = trs[pos];
             pos++;
             let bs = utils.crtTableBodyStyle(tstyle);
             if(!c.alias){ //没值
               footer.children.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style:bs}, [h('div',{class:"dg-cell"})]));
               continue;
             }
             let hd = lastNodes[c.alias];
             let s = {};
             if(hd.width && hd.width >= 0){
               s['width'] = hd.width + "px";
             }
              if(hd.align){  //字段上的 align 属性覆盖表格属性上的 align 属性
                bs['text-align'] = hd.align;
              }
              bs['padding'] = "0";
             let v = c.value;
             footer.children.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style: bs, attrs:{align:hd.align?hd.align:"center"}}, [h('div', {class:"dg-cell", style:s, domProps:{innerHTML:v}})]));
          }
        }
      }else{
        trs = [h('tr',[h('td', {attrs:{colspan:data.header.length, align:"center"}}, '无数据')])];
      }
      let table2 = h('table', {class:"lockgrid"}, [h('thead', trs)]);

      //分页信息
      let allpage = 0;
      if (data.total % data.pageSize === 0) {
        allpage = data.total / data.pageSize;
      } else {
        allpage = Math.floor(data.total / data.pageSize) + 1;
      }
      let first = data.curPage <= 0;
      let end = data.curPage >= allpage - 1;
      let pg = [
        h('button', {class:"btn btn-link",attrs:{disabled:first},on:{click:()=>{
          if(!first){
            this.comp.comp.curPage = 0;
            if(this.editor === false && this.useIn === 'report'){
              this.fy();
            }else{
               this.gridView();
            }
          }
        }},domProps:{innerHTML:"<i class='fa fa-angle-double-left'></i>"}}),
        h('button', {class:"btn btn-link", on:{click:()=>{
          if(!first){
            this.comp.comp.curPage = data.curPage - 1;
            if(this.editor === false && this.useIn === 'report'){
              this.fy();
            }else{
               this.gridView();
            }
          }
        }},attrs:{disabled:first},domProps:{innerHTML:"<i class='fa fa-angle-left'></i>"}}),
        h('button', {class:"btn btn-link",attrs:{disabled:end},on:{click:()=>{
          if(!end){
            this.comp.comp.curPage = data.curPage + 1;
            if(this.editor === false && this.useIn === 'report'){
              this.fy();
            }else{
               this.gridView();
            }
          }
        }},domProps:{innerHTML:"<i class='fa fa-angle-right'></i>"}}),
        h('button', {class:"btn btn-link",attrs:{disabled:end}, on:{click:()=>{
          if(!end){
            this.comp.comp.curPage = allpage - 1;
            if(this.editor === false && this.useIn === 'report'){
              this.fy();
            }else{
               this.gridView();
            }
          }
        }},domProps:{innerHTML:"<i class='fa fa-angle-double-right'></i>"}})
      ];

      //分页样式
      let pstyle = {};
      if(tstyle && tstyle.fyBgColor){
        pstyle['background-color'] = tstyle.fyBgColor;
      }
      if(tstyle && tstyle.fyTextColor){
        pstyle['color'] = tstyle.fyTextColor;
      }
      if(tstyle && tstyle.fyFontSize){
        pstyle['font-size'] = tstyle.fyFontSize+"px";
      }
      let pageinfo = h('div', {class:"pagesizeinfo", style:pstyle}, [h('div', {class:"pagesizeLeft"}, pg), h('div', {class:"pagesizeRight"}, '第'+(data.curPage + 1)+'页，共'+data.total+'条记录')]);
      let bodysyl = {"overflow":"auto"};
      bodysyl.height = this.tableHeight+"px";
      //渲染完成后计算表格高度
      this.$nextTick(()=>{
        this.computeTableHeight();
        //utils.autowidth(this.comp, this.data, this.useIn);
        //表格滚动
        this.tableScroll(this.comp, this.data.datas.length);
      });
      if(tstyle && tstyle.bodyBgColor){
        bodysyl['background-color'] = tstyle.bodyBgColor;
      }
      let hs = {};
      if(this.styleType === "bigscreen"){ //在大屏中，表格背景色是  #093247
        hs['background-color'] = "#093247";
      }
      if(tstyle && tstyle.headBgColor){
        hs['background-color'] = tstyle.headBgColor;
      }
      if(this.comp.hideTitle == true){  //移除表头
        hs['display'] = "none";
      }
      //计算head宽度
      let headWidth = 0; //表头宽度
      $(this.data.header).each((a, b)=>{
        let w = 90;
        $(b).each((c, d)=>{
          if(d.width && d.width > w){
            w = d.width;
          }
        });
        headWidth += w;
      });
      headWidth = headWidth + 3;
      hs['width'] = headWidth  + "px";
      hs['float'] = "left";
      bodysyl['float'] = "right";
      if(this.comp.hideTitle == true){
        bodysyl['width'] = "100%";
      }else{
        bodysyl['width'] = "calc(100% - "+headWidth+"px)";
      }
      if(comp.scrollStyle){
        let ss = comp.scrollStyle;
        this.scrollStyle.scrollWidth = ss.scrollWidth >= 0 ? ss.scrollWidth : 10;
        this.scrollStyle.scrollThumbColor = ss.scrollThumbColor || '#F5F5F5';
        this.scrollStyle.scrollThumbRadius = ss.scrollThumbRadius || 0;
        this.scrollStyle.scrollTrackColor = ss.scrollTrackColor || '#F5F5F5';
      }
      let cld = [h('div', {class:"lock-dg-header", style:hs}, [table1]), h('div', {class:"lock-dg-body", style:bodysyl}, [table2])];

      if(!(this.comp.comp.isnotfy == true)){
        cld.push(pageinfo);
      }
      let tsty = {};
      if(tstyle && tstyle.tableBgcolor){
        tsty['background-color'] = tstyle.tableBgcolor;
      }
      if(tstyle && tstyle.tableMousecolor){
        this.coc = tstyle.tableMousecolor;
      }else{
        this.coc = this.styleType==='bigscreen' ? "#0a6aa1" : "#FFFFD2";  //鼠标悬停默认色
      }
      this.$nextTick(()=>{
        this.tableHover();
        this.tableScrollStyle();
      });

      let ret = [];
      if(this.islink == true){  //添加返回按钮
        ret.push(h('span', {class:"eventback"}, [h('span', {class:"label label-primary", on:{click:()=>{
          this.linkBack();
        }}, domProps:{innerHTML:"<i class=\"fa fa-arrow-left\"></i>返回"}})]));
      }
      ret.push(h('div', {class:"lock-dg", style:tsty}, cld));
      return h('div',{style:{width:'100%', height:'100%'}}, ret);
    }
     if(this.editor === true){
        if(this.useIn === 'bigscreen'){
          return h('div', '未配置表格');
        }else if(this.useIn === 'dashboardEdit'){
          return h('div', {attrs:{align:"center", class:"tipinfo"}, domProps:{innerHTML:"(未配置"+utils.getCompTypeDesc(comp.type)+"数据)"}});
        }else{
          return h('div', {attrs:{align:"center", class:"tipinfo"}, domProps:{innerHTML:"(点击<i class=\"fa fa-wrench\"></i>按钮配置"+utils.getCompTypeDesc(comp.type)+")"}});
        }
     }else{
        return h('div','数据加载中...');
     }
  },
  mounted(){
    //正在报表中使用 editor 属性
    if(this.useIn==='report'){
      if(this.editor === true){
        this.gridView();
        if(this.autuFlush == true){
          utils.autoFlush(this.comp, ()=>this.gridView());
        }
      }
    }else{
      //获取参数值
      dutils.setCompDefValue(this.comp.comp, this.pageInfo);
      this.gridView();
      if(this.autuFlush == true){
        utils.autoFlush(this.comp, ()=>this.gridView());
      }
    }
  },
  computed: {
  },
  methods: {
     //通用方法，view
    view(){
      this.gridView();
    },
    tableHover(){
      //解决鼠标悬停颜色问题
      this.$el.style.setProperty('--coc',  this.coc);
    },
    tableScrollStyle(){  //表格滚动条颜色
      this.$el.style.setProperty('--scrollWidth',  this.scrollStyle.scrollWidth + "px");
      this.$el.style.setProperty('--scrollThumbColor',  this.scrollStyle.scrollThumbColor);
      this.$el.style.setProperty('--scrollThumbRadius',  this.scrollStyle.scrollThumbRadius + "px");
      this.$el.style.setProperty('--scrollTrackColor',  this.scrollStyle.scrollTrackColor);
    },
    bindScrollEvent(){

    },
    //计算表格高度
    computeTableHeight(){
      let prefix = "#" + this.useIn;
      let compId = this.comp.id;
      let h = $(prefix+compId).height();
      if(this.useIn === 'bigscreen'){  //减去边框
        let borderWidth = this.comp.tstyle && this.comp.tstyle.compBorderWidth ? this.comp.tstyle.compBorderWidth : 1;
        h = h - borderWidth * 2 ;
      }
      var fyHeight = $(prefix+compId+" div.pagesizeinfo").height() || 0;  //分页高度	(padding:6px)
      //fyHeight += 6;
      h = Math.round(h - (this.comp.comp.isnotfy == true ?  0 : Math.round(fyHeight))); //减去表头距离， 分页距离

     this.tableHeight = h;
    },
    autowidth(){
      utils.autowidth(this.comp, this.data, this.useIn);
    },
    //查看模式，分页方法(好像只在报表查看中使用)
    fy(){
      let dt = null;
      if(this.useIn === 'report'){  //报表，添加参数
        dt = this.$parent.$parent.$refs['paramViewForm'].getParamValues();
      }else{
        dt = {};
      }
      let reportId = this.$parent.pageInfo.id;
      dt['serviceid'] = "ext.sys.fenye.ajax";
      dt['t_from_id'] = "mv_" + reportId;
      dt['currPage'] = this.comp.comp.curPage;
      dt['id'] = this.comp.id;
      dt['pageSize'] = this.comp.comp.pageSize;

      let loadingInstance = Loading.service({fullscreen:false,background:utils.getLoadingbackground(this.styleType), target:document.querySelector('#'+this.useIn+this.comp.id)});
      ajax({
        url:"control/extControl?",
        data:dt,
        type:"POST",
        success:(resp)=>{
          //重新渲染表格
          this.data = resp.rows;
        }
      }, this, loadingInstance);
    },
    gridView(){
      let ts = this;
      let comp = this.comp.comp;
      comp = JSON.parse(JSON.stringify(comp));
      comp.portalParams = ts.portalParams;
      if(comp.cols && comp.cols.length > 0){
          let json = JSON.parse(JSON.stringify(comp));
          json.useIn = this.useIn;
          let prefix = "#" + this.useIn;
          let target = document.querySelector(prefix+this.comp.id);
          //let target = document.querySelector(prefix+json.id+" div."+(this.useIn ==='bigscreen'?"comp_item":"ccctx")+"");
          let loadingInstance = Loading.service({fullscreen:false, background:utils.getLoadingbackground(this.styleType), target}); //text:"加载中...", spinner:"el-icon-loading",
          let url = "portal"+(this.token?"/share":"")+"/GridView.action" + (this.token?"?token="+this.token:"");
          ajax({
            url:url,
            type:"POST",
            data:JSON.stringify(json),
            postJSON:true,
            success:(resp)=>{
              ts.data = resp.rows;
              //如果有滚动，给数据前后添加虚拟数据，实现连续滚动
              if(ts.comp.scroll){
                ts.dataSize = ts.data.datas.length;
                let dts = [];
                for(let i=0; i<2; i++){
                  $(resp.rows.datas).each((a, b)=>{
                    dts.push(b);
                  });
                }
                ts.data.datas = dts;
              }
              loadingInstance.close();
            }
          }, this, loadingInstance);
      }
    },
     //事件点击返回按钮
    linkBack(){
      this.islink = false;
      if(this.useIn === 'report'){

      }else if(this.useIn === 'dashboard'){
        this.$parent.$parent.$parent.$parent.compLinkBack(this.comp);
      }
    },
    tableScroll(comp, colLength){
      if(comp.scroll==true ){
        if(comp.scrollEventId){
          cancelAnimationFrame(comp.scrollEventId);
          delete comp.scrollEventId;
        }
        utils.tableTranspositionScroll(comp, this.useIn, colLength, this.dataSize);
      }else{
        if(comp.scrollEventId){
          cancelAnimationFrame(comp.scrollEventId);
          delete comp.scrollEventId;
        }
      }
    }
  },
  watch: {

  },
  beforeDestroy(){
     utils.stopScroll(this.comp);
     utils.stopFlush(this.comp);
  },
}
</script>

<style lang="less" scoped>
div.lock-dg-body TABLE.lockgrid tr:hover {
	background-color: var(--coc) !important;
}
.tipinfo {
  color: #999;
  padding: 10px;
}
.eventback {
	position:absolute;
	width:50px;
	right:5px;
  cursor:pointer;
  display:block;
	z-index:1100;
}
/** 滚动条样式 */
div.lock-dg-body::-webkit-scrollbar {
  width: var(--scrollWidth);
  height: var(--scrollWidth);
  background-color:var(--scrollTrackColor);
}
div.lock-dg-body::-webkit-scrollbar-thumb{
    background-color: var(--scrollThumbColor);
    border-radius: var(--scrollThumbRadius);
}
div.lock-dg-body::-webkit-scrollbar-track{
    background-color:var(--scrollTrackColor);
}
</style>
