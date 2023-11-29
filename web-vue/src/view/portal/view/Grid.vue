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
import * as bsUtils from '@/view/bigscreen/bsUtils'

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
      lockHeadHeight : 0,
      sortType: null, //前端排序，当前排序状态
      sortCol: null, //前端排序，当前排序字段
      scrollStyle: {
        scrollWidth: 10,
        scrollThumbColor:'#F5F5F5',
        scrollThumbRadius: 0,
        scrollTrackColor: '#F5F5F5',
      },
      error: null,
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
      let headTrs = [];
      let tstyle = comp.tstyle;  //组件的样式放在 tstyle 中
      let level = data.header.length;
      let lastNodes = {};
      data.header.forEach((e, row) => {
        let ths = [];
        $(e).each((c, element)=>{
          if(!element.name){
            return;
          }
          let hasChildren = true;
           if(row + element.rowspan === level){  //最后级节点
            hasChildren = false;
            lastNodes[element.name] = element;
          }
          let s = {};
          if(element.width && !hasChildren){
            s['width'] = element.width + "px";
          }
          if(hasChildren){
            s['width'] = "auto";
          }
          let hs = utils.crtTableHeadStyle(tstyle);
          if(element.order == true){
            hs['cursor'] = "pointer";
          }
          let v = element.desc;
          if(element.order == true){
            if(this.sortCol && this.sortCol.name == element.name){
              if(this.sortType == 'desc'){
                v += " <i class='fa fa-long-arrow-down'></i>";
              }else{
                v += " <i class='fa fa-long-arrow-up'></i>";
              }
            }else{
              v += " <i class='fa fa-arrows-v'></i>";
            }
          }
          ths.push(h('th', {class:"grid3-td"+(this.styleType=='bigscreen'?"-bs":""), style:hs, attrs:{colspan:element.colspan, rowspan:element.rowspan, pos:c}, on:{
            click:()=>{
              if(element.order==true){
                  this.tableColSort(element);
              }
            }
          }}, [h('div', {class:(hasChildren?"hasChildren":"dg-cell"), style:s, domProps:{innerHTML:v}})]));
        })
        headTrs.push(h('tr', ths));
      });
      let table1 = h('table', {class:"lockgrid"}, [h('thead', headTrs)]);

      let cnt = 0;
      let trs = [];
      if(data.datas){
        data.datas.forEach((e, idx) => {
          cnt = idx;
          let tds = []
          $(e).each((c, d)=>{
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
             //获取列节点
             let colNode = utils.getGridColById(d.alias, this.comp.comp.cols);
             //根据列节点设置样式
             this.copyColStyle(bs, colNode);
             let v = nd.value != null ? nd.value : "";
             tds.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style: bs, attrs:{align:hd.align?hd.align:"center"}}, [h('div', {class:"dg-cell", style:s, domProps:{innerHTML:v}})]));
          });
          //表格隔行换色
          let trstyle = {};
          if(tstyle && tstyle.bodyEnableTr == true){
            if(idx % 2 === 0){
              trstyle['background-color'] = tstyle.bodyTrColor1 || "#ffffff";
            }else{
              trstyle['background-color'] = tstyle.bodyTrColor2 || '#F5F5F6';
            }
          }
          //有事件加上鼠标样式
          if(comp.comp.link){
            trstyle['cursor'] = "pointer";
          }
          trs.push(h('tr', {class:"t-row", style:trstyle, attrs:{pos:idx}, on:{
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
                  if(c.link && c.link.target && c.link.target.length > 0){
                    let colName = comp.comp.link.colName;
                    let node = e.filter(m=>m.alias === colName)[0];
                    let v = node.value;
                    this.islink = this.$parent.$parent.$parent.$parent.compLink(this.comp, v, comp.comp.link.colName);
                  }
                }
              }
            }
          }}, tds));
        });
        //附加 footer 信息
        if(this.data.footer){
          let footer = [];
          for(let c of this.data.footer){
             let bs = utils.crtTableBodyStyle(tstyle);
             if(!c.alias){ //没值
               footer.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style:bs}, [h('div',{class:"dg-cell"})]));
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
             let v = c.value;
             footer.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style: bs, attrs:{align:hd.align?hd.align:"center"}}, [h('div', {class:"dg-cell", style:s, domProps:{innerHTML:v}})]));
          }
          cnt++;
          //表格隔行换色
          let trstyle = {};
          if(tstyle && tstyle.bodyEnableTr == true){
            if(cnt % 2 === 0){
              trstyle['background-color'] = tstyle.bodyTrColor1 || "#ffffff";
            }else{
              trstyle['background-color'] = tstyle.bodyTrColor2 || '#F5F5F6';
            }
          }
          trs.push(h('tr', {class:"t-row", style: trstyle}, footer));
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
      let pageinfo = h('div', {class:"pagesizeinfo", style:pstyle}, [h('div', {class:"pagesizeLeft"}, pg), h('div', {class:"pagesizeRight"}, '第'+(data.curPage + 1)+'页，共'+allpage+'页/'+data.total+'条记录')]);
      let bodysyl = {"overflow":"auto"};
      bodysyl.height = this.tableHeight+"px";
      //渲染完成后计算表格高度
      this.$nextTick(()=>{
        this.computeTableHeight();
        this.bindScrollEvent();
        utils.autowidth(this.comp, this.data, this.useIn);
        //表格滚动
        this.tableScroll(this.comp);
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
      if(comp.scrollStyle){
        let ss = comp.scrollStyle;
        this.scrollStyle.scrollWidth = ss.scrollWidth >= 0 ? ss.scrollWidth : 10;
        this.scrollStyle.scrollThumbColor = ss.scrollThumbColor || '#F5F5F5';
        this.scrollStyle.scrollThumbRadius = ss.scrollThumbRadius || 0;
        this.scrollStyle.scrollTrackColor = ss.scrollTrackColor || '#F5F5F5';
      }

      let cld = [h('div', {class:"lock-dg-header", style:hs}, [table1]), h('div', {class:"lock-dg-body", style:bodysyl}, [table2])];
      if(this.comp.lockcols){ //锁定列
        //输出锁定列的表头
        let headTrs = [];
        let w = 0;
        data.header.forEach((e, row) => {
          let ths = [];
          $(e).each((c, element)=>{
            if(!element.name){
              return;
            }
            if(c >= this.comp.lockcols){
              return false;
            }
            let hasChildren = true;
            if(row + element.rowspan === level){  //最后级节点
              hasChildren = false;
              lastNodes[element.name] = element;
            }
            let s = {};
            if(element.width && !hasChildren){
              s['width'] = element.width + "px";
            }
            if(hasChildren){
              s['width'] = "auto";
            }
            if(!hasChildren){
              w = w + 6 + ( element.width || 90 ) ;
              if(tstyle && (tstyle.bodyRightWidth || tstyle.bodyLeftWidth)){
                if(tstyle.bodyRightWidth){
                  w += tstyle.bodyRightWidth;
                }
                if(tstyle.bodyLeftWidth){
                  w += tstyle.bodyLeftWidth;
                }
              }
            }
            let hs = utils.crtTableHeadStyle(tstyle);
            let v = element.desc;
            ths.push(h('th', {class:"grid3-td"+(this.styleType=='bigscreen'?"-bs":""), style:hs, attrs:{colspan:element.colspan, rowspan:element.rowspan, pos:c}}, [h('div', {class:(hasChildren?"hasChildren":"dg-cell"), style:s, domProps:{innerHTML:v}})]));
          })
          headTrs.push(h('tr', ths));
        });
        //输出锁定列的表体
        let trs = [];
        let cnt = 0;
        data.datas.forEach((e, idx) => {
          cnt = idx;
          let tds = []
          $(e).each((c, d)=>{
            if(c >= this.comp.lockcols){
              return false;
            }
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
             let v = nd.value != null ? nd.value : "";
             tds.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style: bs, attrs:{align:hd.align?hd.align:"center"}}, [h('div', {class:"dg-cell", style:s, domProps:{innerHTML:v}})]));
          });
          //表格隔行换色
          let trstyle = {};
          if(tstyle && tstyle.bodyEnableTr == true){
            if(idx % 2 === 0){
              trstyle['background-color'] = tstyle.bodyTrColor1 || "#ffffff";
            }else{
              trstyle['background-color'] = tstyle.bodyTrColor2 || '#F5F5F6';
            }
          }

          trs.push(h('tr', {class:"t-row", style:trstyle, attrs:{"pos":idx}}, tds));
        });
        //输出锁定列的footer
        if(this.data.footer){
          let footer = [];
          let cidx = 0;
          for(let c of this.data.footer){
            if(cidx >= this.comp.lockcols){
              continue;
            }
            cidx++;
             let bs = utils.crtTableBodyStyle(tstyle);
             if(!c.alias){ //没值
               footer.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style:bs}, [h('div',{class:"dg-cell"})]));
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
             let v = c.value;
             footer.push(h('td', {class:"lockgrid-td"+ (this.styleType=='bigscreen'?"-bs":""), style: bs, attrs:{align:hd.align?hd.align:"center"}}, [h('div', {class:"dg-cell", style:s, domProps:{innerHTML:v}})]));
          }
          cnt++;
          //表格隔行换色
          let trstyle = {};
          if(tstyle && tstyle.bodyEnableTr == true){
            if(cnt % 2 === 0){
              trstyle['background-color'] = tstyle.bodyTrColor1 || "#ffffff";
            }else{
              trstyle['background-color'] = tstyle.bodyTrColor2 || '#F5F5F6';
            }
          }
          trs.push(h('tr', {class:"t-row", style: trstyle}, footer));
        }


        let headTable = h('table', {class:"lockgrid", style:{height:"100%"}}, [h('thead', headTrs)]);
        let dataTable = h('table', {class:"lockgrid"}, [h('thead', trs)])
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
        hs['height'] = this.lockHeadHeight+"px";
        //设置锁定列的背景样式
        let ds = {height: (this.tableHeight - 10) +"px", overflow:'hidden'};
        if(this.styleType === "bigscreen"){ //在大屏中，表格背景色是  #093247
          ds['background-color'] = bsUtils.pageBgColor;
        }else{
          ds['background-color'] = "#ffffff";
        }
        if(tstyle && tstyle.bodyBgColor){
          ds['background-color'] = tstyle.bodyBgColor;
        }
        cld.push(h('div', {class:"lock-dg-fixed", style:{width: w + "px"}}, [h('div', {class:"lock-dg-fixed-header", style:hs}, [headTable]), h('div', {class:"lock-dg-fixed-data", style:ds}, [dataTable])]));
      }

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
      if(this.styleType == 'bigscreen'){
        tsty['color'] = "#eaeaea";
      }
      ret.push(h('div', {class:"lock-dg", style:tsty}, cld));
      return h('div',{style:{width:'100%', height:'100%'}}, ret);
    }
    if(this.error){
      return  h('div', this.error);
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
      //绑定 tr 的 hover 事件
      let ts = this;
      if(this.comp.lockcols > 0){  //锁定列
        $("#" + this.useIn + this.comp.id+" .lock-dg-body table.lockgrid tr").hover(function(e){
           let pos = $(this).attr("pos");
           $("#" + ts.useIn + ts.comp.id+" .lock-dg-fixed-data table.lockgrid tr[pos="+pos+"]").addClass("tableHoverCss");
        }, function(e){
          let pos = $(this).attr("pos");
          $("#" + ts.useIn + ts.comp.id+" .lock-dg-fixed-data table.lockgrid tr[pos="+pos+"]").removeClass("tableHoverCss");
        });
      }
    },
    tableScrollStyle(){  //表格滚动条颜色
      this.$el.style.setProperty('--scrollWidth',  this.scrollStyle.scrollWidth + "px");
      this.$el.style.setProperty('--scrollThumbColor',  this.scrollStyle.scrollThumbColor);
      this.$el.style.setProperty('--scrollThumbRadius',  this.scrollStyle.scrollThumbRadius + "px");
      this.$el.style.setProperty('--scrollTrackColor',  this.scrollStyle.scrollTrackColor);
    },
    bindScrollEvent(){
      let comp = this.comp;
      let useIn = this.useIn;
      //注册table的scroll事件
      $("#" + useIn +comp.id+" .lock-dg-body").unbind("scroll").bind("scroll", function(){
        var left = $(this).scrollLeft();
        $("#" + useIn +comp.id+" .lock-dg-header").css("margin-left", "-"+left+"px");

        if(comp.lockcols > 0){  //锁定列
          var top = $(this).scrollTop();
          $("#" + useIn +comp.id+" .lock-dg-fixed-data table.lockgrid").css("margin-top", "-"+top+"px");
        }
      });
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
      var headHeight = $(prefix+compId+" div.lock-dg-header").height() || 0;  //表头高度
      if(this.comp.hideTitle == true){
        headHeight = 0;
      }
      var fyHeight = $(prefix+compId+" div.pagesizeinfo").height() || 0;  //分页高度	(padding:6px)
      //fyHeight += 6;
      h = Math.round(h - Math.round(headHeight) - (this.comp.comp.isnotfy == true ?  0 : Math.round(fyHeight))); //减去表头距离， 分页距离
      this.lockHeadHeight = headHeight;
     this.tableHeight = h;
    },
    autowidth(){
      utils.autowidth(this.comp, this.data, this.useIn);
    },
    //表格前端排序（点击表头）
    tableColSort(node){
      //console.log(this.useIn + "," + this.editor);
      if(this.editor == true){
        this.$notify.warning("表头排序已设置成功，请在发布后尝试。");
        return;
      }
      this.sortCol = node;
      if(!this.sortType){
        this.sortType = "asc";
      }else{
        if(this.sortType == "asc"){
          this.sortType = "desc";
        }else{
          this.sortType = "asc";
        }
      }
      if(this.editor === false && this.useIn === 'report'){  //报表页面排序
        this.fy();
      }else{  //其他地方排序
        let comp = this.comp.comp;
        $(comp.cols).each((a, b)=>{
          if(b.children){
            $(b.children).each((c, d)=>{
              if(d.id == node.name){
                d.sort = this.sortType;
                d.sortIndex = 1;
              }else{
                delete d.sort;
                delete d.sortIndex;
              }
            });
          }else{
            if(b.id == node.name){
              b.sort = this.sortType;
              b.sortIndex = 1;
            }else{
              delete b.sort;
              delete b.sortIndex;
            }
          }
        });
        this.gridView();
      }
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
      if(this.sortCol){  //前端排序
        dt['t_sort_col'] = this.sortCol.name;
        dt['t_sort_type'] = this.sortType;
      }

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
    copyColStyle(cs, colNode){
      if(colNode == null){
        return;
      }
      if(colNode.colFontSize){
        cs['font-size'] = colNode.colFontSize+"px";
      }
      if(colNode.colTextColor){
         cs['color'] = colNode.colTextColor;
      }
      if(colNode.colfamily){
         cs['font-family'] = colNode.colfamily;
      }
      if(colNode.colBgColor){
         cs['background-color'] = colNode.colBgColor;
      }
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
              ts.error = null;
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
          }, this, loadingInstance, (resp)=>{
            if(resp.result == 0){
               ts.error = resp.msg;
               ts.data = null;
            }
          });
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
    tableScroll(comp){
      if(comp.scroll==true ){
        if(comp.scrollEventId){
          cancelAnimationFrame(comp.scrollEventId);
          delete comp.scrollEventId;
        }
        utils.tableScroll(comp, this.useIn, this.dataSize);
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
.tableHoverCss {
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
div.lock-dg-fixed {
  position: absolute;
  top: 0;
  left: 0;
  overflow: hidden;
  pointer-events: none;  //阻止上层div触发点击事件，并触发下层div的点击事件
}
</style>
