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
      dataTemp: null, //临时存放 data 中的数据, 只用在 父子维度 中（维度展开/折叠）
      pcExpandVls: [], //在父子维度中存放 展开的节点
      dataSize: null,  //数据的长度(在滚动时，未包含滚动的虚拟数据)
      islink:false, //是否做了事件联动
      coc:null,
      styleType:null, //风格类型，白色背景还是黑色背景
      tableHeight: 320,
      lockHeadHeight : 0,
      sortCol:null, //当前排序字段
      sortType:null,  //排序方式
      dirllData:null,  //钻取后数据对象
      curPage: null, //交叉表分页页码数，从0开始
      pageDatas: [], //交叉表分页时的分页数据
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
    let ts = this;
    this.styleType = this.pageInfo&&this.pageInfo.style ? this.pageInfo.style.styleType:null;
    let comp = this.comp;
    if(this.data){
      let trs = [];
      let tstyle = comp.tstyle;  //组件的样式放在 tstyle 中
			$(this.data.header).each((a, b)=>{
				let ths = [];
				$(b).each((c, d)=>{
          let hs = utils.crtTableHeadStyle(tstyle); //表头样式
          if(d.order == true){
            hs['cursor'] = "pointer";
          }
          let hasChildren = !(d.level + d.rowspan === this.data.colLevel);  //含有下级
          let s = {};
          if(d.width && !hasChildren){
            s['width'] = d.width + "px";
          }
          if(hasChildren){
            s['width'] = "auto";
          }
          let v = d.desc;
          if(d.order == true){
            if(this.sortCol && this.sortCol.col == d.col){
              if(this.sortType == 'desc'){
                v += " <i class='fa fa-long-arrow-down'></i>";
              }else{
                v += " <i class='fa fa-long-arrow-up'></i>";
              }
            }else{
              v += " <i class='fa fa-arrows-v'></i>";
            }
          }
          ths.push(h('th', {class:"grid3-td"+(this.styleType=='bigscreen'?"-bs":""), style:hs, attrs:{colspan:d.colspan, rowspan:d.rowspan, pos:d.pos}, on:{
            click:()=>{
              //点击表头排序
              if(d.order==true){
                  ts.tableColSort(d);
              }
            }
          }}, [h('div', {class:hasChildren?"dg-haschildren":"dg-cell", style:s, domProps:{innerHTML:v}})]));
				});
				trs.push(h('tr', ths));
      });

      let table1 = h('table', {class:"lockgrid"}, [h('thead', trs)]);

			let tbodytrs = [];
		  let dts = this.comp.usefy == true ? this.pageDatas : this.data.datas;
			$(dts).each((a, b)=>{
        let tds = [];
        let curRowValue = null;
        let linkParams = null;
        let curLvl = 0;
				$(b).each((c, d)=>{
          let bs = utils.crtTableBodyStyle(tstyle);
          //if(hd.align){  //字段上的 align 属性覆盖表格属性上的 align 属性
          // bs['text-align'] = hd.align;
          //}
          //查找col上的样式 (列属性)
          let colStyle = null;
          if(d.isRow === true){ //交叉表头
            if(this.comp.comp.ranking == true){
              if(d.level == 0){
                 colStyle = this.comp.rankStyle || {};
                 colStyle = colStyle.colStyleDto;
              }else{
                let node = this.comp.comp.rowHeads[d.level - 1];
                colStyle = node && node.colStyleDto ? node.colStyleDto : {};
              }
            }else{
              let node = this.comp.comp.rowHeads[d.level];
              colStyle = node && node.colStyleDto ? node.colStyleDto : {};
            }
          }else{ //列字段
            let agg = false;
            let ref = d.colRef;
            if(d.colRef && d.colRef.indexOf('_agg') >= 0){
              ref = d.colRef.split("_")[0];
              agg = true;
            }
            utils.loopDims(this.comp.comp.cols, function (a, b, c) {
              if (a.match == ref) {
                if(agg == false){
                  colStyle = a.colStyleDto;
                }else{
                  colStyle = a.colStyleAggDto;
                }
                return false;
              }
            });
          }
          //根据colStyle 设置列单元格样式
          if(colStyle){
            if(colStyle.colBgcolor){
              bs['background-color'] = colStyle.colBgcolor;
            }
            if(colStyle.colFontcolor){
              bs['color'] = colStyle.colFontcolor;
            }
            if(colStyle.colFontsize){
              bs['font-size'] = colStyle.colFontsize+"px";
            }
            if(colStyle.colTextPos){
              bs['text-align'] = colStyle.colTextPos;
            }
          }
          let cs = {};  //div class='dg-cell' 上的样式
          if( d.width >= 0){
            cs['width'] = d.width + 'px';
          }
          //查找 列 上的样式
          let val = d.value;
          let dispTxt = [h('div', {class:"dg-cell", style:cs, domProps:{innerHTML:val}})];
          //控制父子维度的输出
          if(d.isRow){
            let dispChild = [h('span', {domProps:{innerHTML:val}})];
             if(this.comp.comp.foldpcdim && this.pcHasChildren(d.desc)){  //父子维度是否有children
                dispChild.unshift(h('span', {on:{click:()=>this.expandNode(d)},class:"pcDimBtn",domProps:{innerHTML:"<i class='fa "+(this.pcExpandVls.indexOf(d.desc) >= 0 ? "fa-plus-square-o":"fa-minus-square-o")+"'></i>"}}));
             }
            if(d.spaceNum > 0){
              dispChild.unshift(h('span', {style:{display:"inline-block", width: (d.spaceNum * 20)+ "px"}}));
            }
            dispTxt = [h('div', {class:"dg-cell", style:cs}, dispChild)];
          }
          curLvl = d.level >= 0 ? d.level : curLvl + 1;  //设置当前列位置

          if(d.nodeType == 'note'){  //注释行, 没数据
            tds.push(h('td', {class:"td-noterow lockgrid-td"+(this.styleType=='bigscreen'?"-bs":""), attrs:{colspan:b.length, rowspan:d.rowspan, lvl:curLvl, align:"left"}}, [h('div', {domProps:{innerHTML:val}})]));
            return false;
          }else{
            tds.push(h('td', {class:"lockgrid-td"+(this.styleType=='bigscreen'?"-bs":""), style:bs, attrs:{colspan:d.colspan, rowspan:d.rowspan, lvl:curLvl, align:d.isRow==true?"left":"right"}}, dispTxt));
          }
          if(d.isRow === true){
            curRowValue = d.value;
            linkParams = d.pms;
          }
        });
        //表格隔行换色
        let trstyle = {};
        if(tstyle && tstyle.bodyEnableTr == true){
          if(a % 2 === 0){
            trstyle['background-color'] = tstyle.bodyTrColor1 || "#ffffff";
          }else{
            trstyle['background-color'] = tstyle.bodyTrColor2 || '#F5F5F6';
          }
        }
        if(utils.compHasLink(this.comp)){  //在浏览模式下有联动事件
         trstyle['cursor'] = "pointer";
          tbodytrs.push(h('tr',{style:trstyle, on:{click:()=>{
              ts.tableEvent(linkParams, b, curRowValue);
            }}}, tds));
        }else{
	        tbodytrs.push(h('tr',{class:"t-row", attrs:{pos:a}, style:trstyle}, tds));
        }

        //判断交叉表是否有下钻, 并渲染下钻内容
        if(this.dirllData &&  this.dirllData.value === curRowValue){
          let bs = utils.crtTableBodyStyle(tstyle);
           $(this.dirllData.datas).each((e, f)=>{
              let tds = [];
              $(f).each((j, k)=>{
                  let cs = {};  //div class='dg-cell' 上的样式
                  if( f.width >= 0){
                    cs['width'] = k.width + 'px';
                  }
                tds.push(h('td', {class:"lockgrid-td"+(this.styleType=='bigscreen'?"-bs":""), style:bs, attrs:{colspan:k.colspan, rowspan:k.rowspan, align:k.isRow==true?"left":"right"}}, [h('div', {class:"dg-cell", style:cs, domProps:{innerHTML: ' &nbsp; &nbsp; ' + k.value}})]));
              });
              tbodytrs.push(h('tr', {class:"t-row",attrs:{pos:a}, style: trstyle}, tds));
           });
        }
      });
      let table2 = h('table', {class:"lockgrid"}, [h('thead', tbodytrs)]);

      let bodysyl = {"overflow":"auto"};
      bodysyl.height = this.tableHeight+"px";
      //渲染完成后计算表格高度
      this.$nextTick(()=>{
        this.computeTableHeight();
        //渲染完成后绑定滚动，自适应事件
        this.bindScrollEvent();
        //宽度自适应
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
      let tsty = {};
      if(tstyle && tstyle.tableBgcolor){
        tsty['background-color'] = tstyle.tableBgcolor;
      }
      if(tstyle && tstyle.tableMousecolor){
        this.coc = tstyle.tableMousecolor;
      }else{
        this.coc = this.styleType ==='bigscreen' ? "#0a6aa1" : "#FFFFD2";  //鼠标悬停默认色
      }
      if(comp.scrollStyle){  //滚动条样式
        let ss = comp.scrollStyle;
        this.scrollStyle.scrollWidth = ss.scrollWidth >= 0 ? ss.scrollWidth : 10;
        this.scrollStyle.scrollThumbColor = ss.scrollThumbColor || '#F5F5F5';
        this.scrollStyle.scrollThumbRadius = ss.scrollThumbRadius || 0;
        this.scrollStyle.scrollTrackColor = ss.scrollTrackColor || '#F5F5F5';
      }
      this.$nextTick(()=>{
        this.tableHover();
        this.tableScrollStyle();
      });
      let cld = [h('div', {class:"lock-dg-header", style:hs}, [table1]), h('div', {class:"lock-dg-body", style:bodysyl}, [table2])];
      if(this.comp.lockrow){  //添加锁定列的代码
        let lockHead = [];
        let w = 0;
        $(this.data.header).each((a, b)=>{
          let ths = [];
          $(b).each((c, d)=>{
            if(d.isCross!=true){
              return false;
            }
            let hs = utils.crtTableHeadStyle(tstyle); //表头样式
            let hasChildren = !(d.level + d.rowspan === this.data.colLevel);  //含有下级
            let s = {};
            if(d.width && !hasChildren){
              s['width'] = d.width + "px";
            }
            if(hasChildren){
              s['width'] = "auto";
            }
            if(!hasChildren){
              w = w + 6 + ( d.width || 90 ) ;
              if(tstyle && (tstyle.bodyRightWidth || tstyle.bodyLeftWidth)){
                if(tstyle.bodyRightWidth){
                  w += tstyle.bodyRightWidth;
                }
                if(tstyle.bodyLeftWidth){
                  w += tstyle.bodyLeftWidth;
                }
              }
            }
            let v = d.desc;
            ths.push(h('th', {class:"grid3-td"+(this.styleType=='bigscreen'?"-bs":""), style:hs, attrs:{colspan:d.colspan, rowspan:d.rowspan, pos:d.pos}}, [h('div', {class:hasChildren?"dg-haschildren":"dg-cell", style:s, domProps:{innerHTML:v}})]));
          });
          if(ths.length > 0){
            lockHead.push(h('tr', ths));
          }
        });

       let thead = h('table', {class:"lockgrid", style:{height:'100%'}}, [h('thead', lockHead)]);
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

        //输出锁定列的标题
        let tableTrs = [];
        let dts = this.comp.usefy == true ? this.pageDatas : this.data.datas;
        $(dts).each((a, b)=>{
          let tds = [];
          let curRowValue = null;
          let linkParams = null;
          $(b).each((c, d)=>{
            if(d.isRow == false){
              return false;
            }
            let bs = utils.crtTableBodyStyle(tstyle);
            //if(hd.align){  //字段上的 align 属性覆盖表格属性上的 align 属性
            // bs['text-align'] = hd.align;
            //}
            //查找col上的样式 (列属性)
            let colStyle = null;
            if(d.isRow === true){ //交叉表头
              if(this.comp.comp.ranking == true){
                if(d.level == 0){
                  colStyle = this.comp.rankStyle || {};
                  colStyle = colStyle.colStyleDto;
                }else{
                  let node = this.comp.comp.rowHeads[d.level - 1];
                  colStyle = node && node.colStyleDto ? node.colStyleDto : {};
                }
              }else{
                let node = this.comp.comp.rowHeads[d.level];
                colStyle = node && node.colStyleDto ? node.colStyleDto : {};
              }
            }else{ //列字段
              let agg = false;
              let ref = d.colRef;
              if(d.colRef && d.colRef.indexOf('_agg') >= 0){
                ref = d.colRef.split("_")[0];
                agg = true;
              }
              utils.loopDims(this.comp.comp.cols, function (a, b, c) {
                if (a.match == ref) {
                  if(agg == false){
                    colStyle = a.colStyleDto;
                  }else{
                    colStyle = a.colStyleAggDto;
                  }
                  return false;
                }
              });
            }
            //根据colStyle 设置列单元格样式
            if(colStyle){
              if(colStyle.colBgcolor){
                bs['background-color'] = colStyle.colBgcolor;
              }
              if(colStyle.colFontcolor){
                bs['color'] = colStyle.colFontcolor;
              }
              if(colStyle.colFontsize){
                bs['font-size'] = colStyle.colFontsize+"px";
              }
              if(colStyle.colTextPos){
                bs['text-align'] = colStyle.colTextPos;
              }
            }
            let cs = {};  //div class='dg-cell' 上的样式
            if( d.width >= 0){
              cs['width'] = d.width + 'px';
            }
            //查找 列 上的样式
            let val = d.value;
            let dispTxt = [h('div', {class:"dg-cell", style:cs, domProps:{innerHTML:val}})];
            //控制父子维度的输出
            if(d.isRow){
              let dispChild = [h('span', {domProps:{innerHTML:val}})];
              if(this.comp.comp.foldpcdim && this.pcHasChildren(d.desc)){  //父子维度是否有children
                  dispChild.unshift(h('span', {on:{click:()=>this.expandNode(d)},class:"pcDimBtn",domProps:{innerHTML:"<i class='fa "+(this.pcExpandVls.indexOf(d.desc) >= 0 ? "fa-plus-square-o":"fa-minus-square-o")+"'></i>"}}));
              }
              if(d.spaceNum > 0){
                dispChild.unshift(h('span', {style:{display:"inline-block", width: (d.spaceNum * 20)+ "px"}}));
              }
              dispTxt = [h('div', {class:"dg-cell", style:cs}, dispChild)];
            }
            if(d.nodeType == 'note'){  //注释行, 没数据
              tds.push(h('td', {class:"td-noterow lockgrid-td"+(this.styleType=='bigscreen'?"-bs":""), attrs:{colspan:b.length, rowspan:d.rowspan, lvl:d.level, align:"left"}}, [h('div', {domProps:{innerHTML:val}})]));
              return false;
            }else{
              tds.push(h('td', {class:"lockgrid-td"+(this.styleType=='bigscreen'?"-bs":""), style:bs, attrs:{colspan:d.colspan, rowspan:d.rowspan, lvl:d.level, align:d.isRow==true?"left":"right"}}, dispTxt));
            }
            if(d.isRow === true){
              curRowValue = d.value;
              linkParams = d.pms;
            }
          });
          //表格隔行换色
          let trstyle = {};
          if(tstyle && tstyle.bodyEnableTr == true){
            if(a % 2 === 0){
              trstyle['background-color'] = tstyle.bodyTrColor1 || "#ffffff";
            }else{
              trstyle['background-color'] = tstyle.bodyTrColor2 || '#F5F5F6';
            }
          }
          tableTrs.push(h('tr',{class:"t-row", attrs:{pos:a}, style:trstyle}, tds));
        });
        let dataTable = h('table', {class:"lockgrid"}, [h('thead', tableTrs)])
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
        cld.push(h('div', {class:"lock-dg-fixed", style:{width: w + "px"}}, [h('div', {class:"lock-dg-fixed-header", style:hs}, [thead]), h('div', {class:"lock-dg-fixed-data",style:ds}, [dataTable])]));
      }
      if(this.comp.usefy){  //启用交叉表分页（前端分页）
        //分页信息
        let total = this.data.datas.length;
        let pageSize = this.comp.pageSize || 10;
        let curPage = this.curPage || 0;
        let allpage = null;
        if (total % pageSize === 0) {
          allpage = total / pageSize;
        } else {
          allpage = Math.floor(total / pageSize) + 1;
        }
        let first = curPage <= 0;
        let end = curPage >= allpage - 1;
        let pg = [
          h('button', {class:"btn btn-link",attrs:{disabled:first},on:{click:()=>{
            if(!first){
              this.curPage = 0;
              this.clientPaging();
            }
          }},domProps:{innerHTML:"<i class='fa fa-angle-double-left'></i>"}}),
          h('button', {class:"btn btn-link", on:{click:()=>{
            if(!first){
              this.curPage = this.curPage - 1;
              this.clientPaging();
            }
          }},attrs:{disabled:first},domProps:{innerHTML:"<i class='fa fa-angle-left'></i>"}}),
          h('button', {class:"btn btn-link",attrs:{disabled:end},on:{click:()=>{
            if(!end){
              this.curPage = this.curPage + 1;
              this.clientPaging();
            }
          }},domProps:{innerHTML:"<i class='fa fa-angle-right'></i>"}}),
          h('button', {class:"btn btn-link",attrs:{disabled:end}, on:{click:()=>{
            if(!end){
              this.curPage = allpage - 1;
              this.clientPaging();
            }
          }},domProps:{innerHTML:"<i class='fa fa-angle-double-right'></i>"}})
        ];
        cld.push(h('div', {class:"pagesizeinfo"}, [h('div', {class:"pagesizeLeft"}, pg), h('div', {class:"pagesizeRight"}, '第'+(curPage + 1)+'页，共'+allpage+'页/'+total+'条记录')]));
      }
      if(ts.styleType == 'bigscreen'){
        tsty['color'] = "#eaeaea";
      }
      let ret = h('div', {class:"lock-dg", style:tsty}, cld);
      if(ts.islink == true){  //添加返回按钮
        return h('div', [h('span', {class:"eventback"}, [h('span', {class:"label label-success", on:{click:()=>{
          this.linkBack();
        }}, domProps:{innerHTML:"<i class=\"fa fa-arrow-left\"></i>返回"}})]), ret]);
      }else{
        return h('div', [ret]);
      }
    }else if(this.error){
      return h('div', this.error);
    }else{
      if(this.editor === true){
         if(this.useIn === 'bigscreen'){
          return h('div', '未配置交叉表');
         }else if(this.useIn === 'dashboardEdit'){
          return h('div', {attrs:{align:"center", class:"tipinfo"}, domProps:{innerHTML:"(未配置"+utils.getCompTypeDesc(comp.type)+"数据)"}});
        }else{
          return h('div', {attrs:{align:"center", class:"tipinfo"}, domProps:{innerHTML:"(点击<i class=\"fa fa-wrench\"></i>按钮配置"+utils.getCompTypeDesc(comp.type)+")"}});
        }
      }else{
        return h('div','数据加载中...');
      }
    }
  },
  mounted(){
    //正在报表中使用 editor 属性
    if(this.useIn==='report'){
      if(this.editor === true){
        this.tableView();
        if(this.autuFlush == true){
          utils.autoFlush(this.comp, ()=>this.tableView());
        }
      }
    }else{
      dutils.setCompDefValue(this.comp.comp, this.pageInfo);
      this.tableView();
      if(this.autuFlush == true){
        utils.autoFlush(this.comp, ()=>this.tableView());
      }
    }
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
      this.tableView();
    },
    tableHover(){
      //解决鼠标悬停颜色问题
      this.$el.style.setProperty('--coc',  this.coc);
      //绑定 tr 的 hover 事件
      let ts = this;
      if(this.comp.lockrow){  //锁定列
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
      $("#"+useIn+comp.id+" .lock-dg-body").unbind("scroll").bind("scroll", function(){
        var left = $(this).scrollLeft();
        $("#"+useIn+comp.id+" .lock-dg-header").css("margin-left", "-"+left+"px");

        if(comp.lockrow){  //锁定列
          var top = $(this).scrollTop();
          $("#" + useIn +comp.id+" .lock-dg-fixed-data table.lockgrid").css("margin-top", "-"+top+"px");
        }
      });
    },
    tableEvent(val, row, curRowValue){
      let comp = this.comp.comp;
      if(this.useIn === 'report' ){
        if(comp.link && comp.link.reportId){ //链接到报表
          let pname = utils.getLinkParam(comp, this.comp.type).key;  //交叉表可能是多个维度
          let pms = pname.split(",");
          let valJson = JSON.parse( "{" + val + "}");
          let vls = [];
          $(pms).each((a,b)=>{
            vls.push(valJson[b]);
          });
          utils.compLink(this.comp, this, pms, vls);
        }else if(comp.link && comp.link.target){  //联动
          this.islink = utils.compFireEvent(comp.link, this, comp.link.paramName, curRowValue);
        }else if(comp.drillDim && comp.drillDim.length > 0){ //表格下钻
          this.tableDirll(curRowValue, row);
        }
      }else if(this.useIn === 'dashboard'){
        if(comp.link && comp.link.reportId){ //链接到仪表盘
          let pname = utils.getLinkParam(comp, this.comp.type).key;
          let pms = pname.split(",");
          let valJson = JSON.parse( "{" + val + "}");
          let vls = [];
          $(pms).each((a,b)=>{
            vls.push(valJson[b]);
          });
          this.$parent.$parent.$parent.$parent.compLink2Dashboard(this.comp, pms, vls, comp.link.reportId);
        }else if(comp.link && comp.link.target){  //联动
          this.islink = this.$parent.$parent.$parent.$parent.compLink(this.comp, curRowValue, comp.link.paramName);
        }else if(comp.drillDim && comp.drillDim.length > 0){ //表格下钻
          this.tableDirll(curRowValue, row);
        }
      }

    },
    //在交叉表上配置下钻
    tableDirll(val, row){
      //如果节点类型是 none, nodeType == none, 维度不能下钻
      if(row[0].nodeType === 'none'){
        return;
      }
      //已经下钻，并且又点击刚才下钻的列，直接取消下钻
      if(this.dirllData && this.dirllData.value == val){
        this.dirllData = null;
        return;
      }
      if(this.useIn == 'report' && this.editor == false){
        let dt = this.$parent.$parent.$refs['paramViewForm'].getParamValues(); // 获取参数
        let reportId = "mv_" +  this.$parent.$parent.reportId;
        dt['serviceid'] = "ext.sys.cross.loadData";
        dt['t_from_id'] =  reportId; //报表id
        dt['id'] = this.comp.id;  //组件id

        dt['drillDim'] = this.comp.comp.drillDim[0].code  //钻取维
        dt['dirrLevel'] = 1;

        let node = this.comp.comp.dims.filter(m=>m.id == this.comp.comp.rows[0].match)[0];
        dt[node.alias] = val;  //当前维及维度值

        let prefix = "#" + this.useIn;
        let target = document.querySelector(prefix+this.comp.id);

        let loadingInstance = Loading.service({fullscreen:false, target:target});
        ajax({
          url:"control/extControl",
          data:dt,
          type:"POST",
          success:(resp)=>{
            let dirllData = {};
            dirllData.value = val;
            dirllData.datas = resp.rows.datas;
            this.dirllData = dirllData;
            loadingInstance.close();
            this.$forceUpdate();
          }
        }, this, loadingInstance);
      }else{  //在大屏/仪表盘中使用下钻
        let dt = "drillDim="+this.comp.comp.drillDim[0].code; //钻取维
        dt += "&dirrLevel=1";
        let node = this.comp.comp.dims.filter(m=>m.id == this.comp.comp.rows[0].match)[0];
        dt += "&" + node.alias + "="+val; //当前维及维度值

        let comp = this.comp.comp;
        let json = JSON.parse(JSON.stringify(comp));
        //如果是仪表盘，需要带的参数是 globalParams
        if("dashboard" == this.useIn || "tobig" == this.useIn || "dashboardEdit" == this.useIn){
          json.portalParams = this.pageInfo.globalParams;
        }else{
          json.portalParams = this.portalParams ;
        }
        json.useIn = this.useIn;
        let prefix = "#" + this.useIn;
        let target = document.querySelector(prefix+json.id);
        let loadingInstance = Loading.service({fullscreen:false, background:utils.getLoadingbackground(this.styleType), target}); //text:"加载中...", spinner:"el-icon-loading",
        let url = "portal"+(this.token?"/share":"")+"/TableView.action?" + dt + (this.token?"&token="+this.token:"");
        ajax({
          url:url,
          type:"POST",
          data:JSON.stringify(json),
          postJSON:true,
          success:(resp)=>{
            let dirllData = {};
            dirllData.value = val;
            dirllData.datas = resp.rows.datas;
            this.dirllData = dirllData;
            loadingInstance.close();
            this.$forceUpdate();
          }
        }, this, loadingInstance);
      }
    },
    /**
     * 计算表格高度
     */
    computeTableHeight(){
        let prefix = "#" + this.useIn;
        let h = $(prefix+this.comp.id).height();
        if(this.useIn === 'bigscreen'){  //减去边框
          let borderWidth = this.comp.tstyle && this.comp.tstyle.compBorderWidth ? this.comp.tstyle.compBorderWidth : 1;
          h = h - borderWidth * 2 ;
        }
        //减去表头
        let hd = $(prefix+this.comp.id+" div.lock-dg-header").height();
        if(!hd){
          hd = 0;
        }
        this.lockHeadHeight = hd;
        //减去分页
        if(this.comp.usefy == true){
          hd = hd + 26;
        }
        h = h - hd- 1;
        this.tableHeight = Math.floor(h);
    },
    autowidth(){
      utils.autowidth(this.comp, this.data, this.useIn);
    },
    linkBack(){
      this.islink = false;
      if(this.useIn === 'report'){
        utils.compBackEvent(this.comp.comp.link, this);
      }else if(this.useIn ==='dashboard'){
        this.$parent.$parent.$parent.$parent.compLinkBack(this.comp);
      }
    },
    tableView(){
      let ts = this;
      let comp = this.comp.comp;
      if(comp.kpiJson && comp.kpiJson.length > 0 && comp.dims.length > 0){ //有维度和度量才刷新
          let json = JSON.parse(JSON.stringify(comp));
          //处理指标定制
          if(this.comp.kpiCustomized == true){
            let selKpis = JSON.parse(localStorage.getItem("c_" + ts.comp.id) || "[]");
            if(selKpis.length > 0) {
              $(json.kpiJson).each((a, b)=>{
                b.hideNode = true;
                if(selKpis.indexOf(b.alias) >= 0 ){
                  b.hideNode = false;
                }
              });
            }
          }
          //如果是仪表盘，需要带的参数是 globalParams
          if("dashboard" == this.useIn || "tobig" == this.useIn || "dashboardEdit" == this.useIn){
            json.portalParams = ts.pageInfo.globalParams;
          }else{
            json.portalParams = ts.portalParams ;
          }
          json.useIn = this.useIn;
          let prefix = "#" + this.useIn;
          let target = document.querySelector(prefix+this.comp.id);
          //let target = document.querySelector(prefix+json.id+" div."+(this.useIn ==='bigscreen'?"comp_item":"ccctx")+"");
          let loadingInstance = Loading.service({fullscreen:false, background:utils.getLoadingbackground(this.styleType), target}); //text:"加载中...", spinner:"el-icon-loading",
          let url = "portal"+(this.token?"/share":"")+"/TableView.action" + (this.token?"?token="+this.token:"");
          ajax({
            url:url,
            type:"POST",
            data:JSON.stringify(json),
            postJSON:true,
            success:(resp)=>{
              ts.data = resp.rows;
              ts.error = null;
              ts.pcExpandVls = [];
              ts.dataTemp = null;
              if(ts.comp.usefy){
                 ts.clientPaging();
              }
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
    //交叉表前端分页
    clientPaging(){
      let total = this.data.datas.length;
      let curPage = this.curPage;
      let pageSize = this.comp.pageSize;
      let ret = [];
      for(let i= curPage * pageSize; i< (curPage + 1) * pageSize && i<total ; i++ ){
        ret.push(this.data.datas[i]);
      }
      this.pageDatas = ret;
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
    },
    /**
     * 表头排序
     */
    tableColSort(node){
       let colIndex = node.pos;
      let dt = this.data;
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
      dt.datas.sort((a, b)=>{
        if(a[colIndex]==null){
          return 1;
        }
        if(b[colIndex] == null){
          return -1;
        }
        let v1 = a[colIndex].trueValue;
        let v2 = b[colIndex].trueValue;
        let r = null;
        if(v1 == null){
          r = 1;
        }
        if(v2 == null){
          r = -1;
        }
        r = v1 - v2;
        if(this.sortType === 'desc'){
          r = -r;
        }
        return r;
      });
      if(this.comp.usefy == true){
        this.clientPaging();
      }
    },
    //父子维度是否含有子节点
    pcHasChildren(pid){
      if(!this.dataTemp){
        this.dataTemp = JSON.parse( JSON.stringify(this.data ));
      }
      let has = false;
      $(this.dataTemp.datas).each((a, b)=>{
        if(b[0].pid == pid){
          has = true;
          return false;
        }
      });
      return has;
    },
    /**
     * 父子维度的展开和合并
     */
    expandNode(node){
      if(this.pcExpandVls.indexOf(node.desc) >= 0){ //存在，需要展开
        let children = $(this.dataTemp.datas).filter(m=>{
          return this.dataTemp.datas[m][0].pid == node.desc;
        });
        //在指定位置添加数组
        $(this.data.datas).each((a, b)=>{
          if(b[0].desc == node.desc){
            for(let i=0; i<children.length; i++){
              this.data.datas.splice(a + 1 + i, 0, children[i])  //添加元素
            }
            return false;
          }
        });
         this.pcExpandVls.splice(this.pcExpandVls.indexOf(node.desc), 1);
      }else{  //不存在，需要收起
        let remove = []; //remove 表示需要移除的对象
        $(this.data.datas).filter(m=>{
          let ret =  this.data.datas[m][0].pid == node.desc;
          if(ret){  //查找子级
            remove.push(this.data.datas[m][0].desc);
            $(this.data.datas).filter(m2=>{
              let r2 = this.data.datas[m2][0].pid == this.data.datas[m][0].desc;
              if(r2){
                if( this.pcExpandVls.indexOf(this.data.datas[m][0].desc) == -1){
                  this.pcExpandVls.push(this.data.datas[m][0].desc);
                }
                remove.push(this.data.datas[m2][0].desc);
                //继续查找子 (支持4级)
                $(this.data.datas).filter(m3=>{
                  let r3 = this.data.datas[m3][0].pid == this.data.datas[m2][0].desc;
                  if(r3){
                    remove.push(this.data.datas[m3][0].desc);
                  }
                });
              }
              return r2;
            });
          }
          return ret;
        });
        this.data.datas =  $(this.data.datas).filter(m=>{
          if(remove.indexOf(this.data.datas[m][0].desc) >= 0){
            return false;
          }else{
            return true;
          }
        });
        this.pcExpandVls.push(node.desc);
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
.eventback {
	position:absolute;
	width:50px;
	right:5px;
  cursor:pointer;
  display:block;
	z-index:1110;
}
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
/** 父子维度折叠按钮 */
span.pcDimBtn {
    display: inline-block;
    width: 12px;
    cursor: pointer;
  }
td.td-noterow {
  background-color: #F5F5F6;
  font-weight: bold;
}
</style>
