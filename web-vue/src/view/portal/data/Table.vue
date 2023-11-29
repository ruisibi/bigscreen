<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<script>
import {baseUrl, newGuid} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import tableDailog from './PortalTableDailog'
import tableMoveTo from './TableMoveTo'
import dimProperty from './DimProperty'
import paramFilter from '@/view/bireport/ParamFilter'
import crossnodeProperty from './CrossNodeProperty'
import insertNoteRow from './InsertNoteRow'
import splitDims from './SplitDims'
import tableDimAggre from './TableDimAggre'
import tableDimTop from './TableDimTop'
import tableKpiProp from './TableKpiProp'
import tableKpiMerge from './TableKpiMerge'
import tableWarning from './TableWarning'
import tableCompareDate from './TableCompareDate'
import tableMove from './TableMove'

export default {
  components:{
    tableDailog,tableMoveTo,dimProperty,crossnodeProperty,
    insertNoteRow,splitDims, paramFilter,tableDimAggre,tableDimTop,
    tableKpiProp, tableKpiMerge, tableWarning, tableCompareDate, tableMove
  },
  props:{
      comp:{
        type:Object,
        required:true
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
      pageInfo: {
        type:Object,
        required: true,
      }
  },
  data(){
    return {

    }
  },
  render(h){
    let comp = this.comp.comp;
    let trs = [];
    let tds = [];
    let rowNodes = h('div', {class:"tipinfo"}, '拖拽维度/度量到行标签区域');
    let crossNodes = h('div', {attrs:{id:"crossheads"}}, '');
    if(comp.tid && comp.rows && comp.rows.length > 0){
      //{rh : rowH, ch : crossH}
      let retNode = this.renderDomBYJSON(h, comp.rows, 'row');
      rowNodes = retNode.rh;
      crossNodes = retNode.ch;
    }

    tds.push(h('td',{class:"grid3-td", attrs:{width:"30%", valign:"bottom"}}, [crossNodes]));
    let colNodes = h('div', {class:"tipinfo"},'拖拽维度/度量到列标签区域');
    if(comp.tid && comp.cols && comp.cols.length > 0){
      colNodes = this.renderDomBYJSON(h, comp.cols, 'col');
    }
    tds.push(h('td', {class:"grid3-td"}, [[h('div', {attrs:{id:"d_colDims"}}, [colNodes])]]));
    trs.push(h('tr', tds));
    tds = [];
    tds.push(h('td', {class:"grid3-td", attrs:{width:"30%"}}, [h('div', {class:"tableDatasty", attrs:{id:"d_rowDims"}}, [rowNodes])]));
    tds.push(h('td', {class:"grid3-td"}));
    trs.push(h('tr', tds));
    return h('div', {attrs:{id:"dataProperty"}}, [
      h('table', {class:"grid3"}, [h('tbody', trs)]),
      h('tableMoveTo', {ref:"tableMoveToForm"}),
      h('dimProperty', {ref:"dimPropertyForm"}),
      h('crossnodeProperty', {ref:"crossnodePropertyForm"}),
      h('insertNoteRow', {ref:"insertNoteRowForm"}),
      h('splitDims', {ref:"splitDimsForm"}),
      h('paramFilter', {ref:'paramFilterForm', props:{usein:"report"}}),
      h('tableDimAggre', {ref:"tableDimAggreForm"}),
      h('tableDimTop', {ref:"tableDimTopForm"}),
      h('tableKpiProp', {ref:"tableKpiPropForm"}),
      h('tableKpiMerge', {ref:"tableKpiMergeForm"}),
      h('tableWarning', {ref:"tableWarningForm"}),
      h('tableMove', {ref: "tableMoveForm"}),
      h('tableCompareDate', {ref:"tableCompareDateForm", props:{useIn:this.useIn, type:"table"}})
    ]);
  },
  mounted(){
    this.bindDropEvent();
  },
  computed: {

  },
  methods: {
     setUpdate(){
      this.$parent.$parent.isupdate = true;
      this.$forceUpdate();
    },
    tableView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].tableView();
    },
    bindDropEvent(){
      let ts = this;
      $("#dataProperty #d_colDims, #dataProperty #d_rowDims").droppable({
        accept:"#datasettree .jstree-node",
        tolerance:"pointer",
        over:function(e, ui){
          //对维度拖拽设置图标
          $(ui.helper[0]).find("span").removeClass("glyphicon-remove").addClass("glyphicon-ok");
          $(this).css("border", "1px solid #ff0000");
        },
        out:function(e, ui){
           $(ui.helper[0]).find("span").removeClass("glyphicon-ok").addClass("glyphicon-remove");
		      	$(this).css("border", "none");
        },
        drop:function(e, ui){
          var json = ts.comp.comp;
          //清除边框颜色
          $(this).css("border", "none");
          //获取TREE
          const ref = $("#datasettree").jstree(true);
          const node = ref.get_node(ui.draggable[0]);

          //判断拖入的维度及度量是否和以前维度及度量在同一个表。
          if(json.tid){
            if(json.tid !== node.li_attr.tid){
              utils.msginfo("您拖入的"+ (node.li_attr.col_type === 2 ? "度量" : "维度") +"与组件已有的内容不在同一个数据表中，拖放失败。");
              return;
            }
          }
          if(!json.tid){  //如果 tid不存在，可能是演示数据，需要清除kpiJson, rows 等数据
            json.rows = [];
            json.cols = [];
            json.dims = [];
            json.kpiJson = [];
          }
          json.tid = node.li_attr.tid;
          json.tname = node.li_attr.tname;
          json.tincome = node.li_attr.income;
          if(!json.kpiJson){  //交叉表使用的指标
            json.kpiJson = [];
          }
          if(!json.dims){ //交叉表使用的维度
            json.dims = [];
          }
          if(!json.cols){  //交叉表列标签
            json.cols = [];
          }
          if(!json.rows){  //交叉表行标签
            json.rows = [];
          }
          const targetDiv = $(this).attr("id");
          //写度量
          if(node.li_attr.col_type === 2){
            if(json.kpiJson.filter(o=>o.kpi_id === node.li_attr.col_id).length > 0){
              utils.msginfo("度量已经存在了。");
              return;
            }
            //写度量
            const o = {"kpi_id":node.li_attr.col_id, "kpi_name" : node.li_attr.name, "col_name":node.li_attr.col_name, "aggre":node.li_attr.aggre, "fmt":node.li_attr.fmt, "alias":node.li_attr.alias,"tid":json.tid,"unit":node.li_attr.unit,"rate":node.li_attr.rate,fromCol:node.li_attr.fromCol, "distinctCol":node.li_attr.distinctCol};
            json.kpiJson.push(o);

            //写crossReport col/row 对象
            const r = {id:newGuid(),match:o.kpi_id, type:'kpi', desc:node.li_attr.name, level:1, children:[]};
            let pos = null;
            let ls = null;
            if(targetDiv === "d_rowDims"){
              json.rows.push(r);
              pos = 'row';
              ls = json.rows;
            }else if(targetDiv === 'd_colDims'){
              json.cols.push(r);
              pos = 'col';
              ls = json.cols;
            }
            ts.setRowHeader(pos, r);

          }
          //写维度
          if(node.li_attr.col_type === 1){
            if(json.dims.filter(o=>o.id === node.li_attr.col_id).length > 0){
              utils.msginfo("维度已经存在了。");
              return;
            }
            //写维度
            const o = {"id":node.li_attr.col_id, "dimdesc" : node.text, "type":node.li_attr.dim_type, "colname":node.li_attr.col_name,"tid":json.tid,"iscas":node.li_attr.iscas, "tableName":node.li_attr.tableName, "tableColKey":node.li_attr.tableColKey,"tableColName":node.li_attr.tableColName, "dimord":node.li_attr.dimord, "dim_name":node.li_attr.dim_name,"grouptype":node.li_attr.grouptype,"valType":node.li_attr.valType,"ordcol":node.li_attr.ordcol,"alias":node.li_attr.alias,fromCol:node.li_attr.fromCol,"levelCol":node.li_attr.levelCol,"pclevel":node.li_attr.pclevel,"pcId":node.li_attr.pcId,"pcPid":node.li_attr.pcPid,"ispcdim":node.li_attr.ispcdim, "dateformat":node.li_attr.dateformat};
            json.dims.push(o);

            //写crossReport col/row 对象
            const r = {id:newGuid(),match:o.id, type:'dim', desc:node.text, level:1, children:[]};
            let ls = null;
            let pos = null;
            if(targetDiv === "d_rowDims"){
              json.rows.push(r);
              ls = json.rows;
              pos = "row";
            }else if(targetDiv === 'd_colDims'){
              json.cols.push(r);
              ls = json.cols;
              pos = "col";
            }
            ts.setRowHeader(pos, r);
          }
          ts.setUpdate();
          ts.tableView();
        }
      });
    },
    renderDomBYJSON(h, ls, pos){
      let comp = this.comp.comp;
      if(!ls || ls.length === 0){
        return;
      }
      if(!comp.tid){
        return;
      }
      const backDoms = (item) => {
        let ret = [];
        let style = {};
        if(pos === 'row'){
          style['display'] = "block";
          style['width'] = "95%";
        }
        if(item.type === 'kpi') {
          let o = comp.kpiJson.filter(t=>t.kpi_id === item.match)[0];
          let node = h('span', {class:"col", style:style}, [
            h('span',{class:"text"}, o.kpi_name),
            h('div', {class:"ibox-tools"},[h('button', {class:"btn btn-outline btn-success btn-xs tableKpioptbtn", domProps:{innerHTML:`<i class="fa fa-wrench"></i>`}, on:{click:()=>{ this.setNodeInfo(pos, 'kpi', item) }}})])
          ]);
          ret.push(node);
        }else if(item.type === 'dim'){
          let o = comp.dims.filter(t=>t.id===item.match)[0];
          let node = h('span', {class:"dimcol", style:style}, [
            h('span',{class:"text"}, o.dimdesc),
            h('div', {class:"ibox-tools"},[h('button', {class:"btn btn-outline btn-success btn-xs tableoptbtn", domProps:{innerHTML:`<i class="fa fa-wrench"></i>`}, on:{click:()=>{ this.setNodeInfo(pos, 'dim', item) }}})])
          ]);
          ret.push(node);
        }else if(item.type === 'none' || item.type === 'note' || item.type === 'kpiMatch'){   //kpiMath 对应交叉表的 type=kpi
          let node = h('span', {class:"dimcol", style:style}, [
            h('span',{class:"text"}, item.desc),
            h('div', {class:"ibox-tools"},[h('button', {class:"btn btn-outline btn-success btn-xs tableNonebtn", domProps:{innerHTML:`<i class="fa fa-wrench"></i>`}, on:{click:()=>{ this.setNodeInfo(pos, item.type, item) }}})])
          ]);
          ret.push(node);
        }
        return ret;
      }
      //查询最大level
      let maxlevel = 1;
      utils.loopDims(ls, item=>{
        if(item.level > maxlevel){
          maxlevel = item.level;
        }
      });
      if(pos === 'col') {
        //root 层几点，一个节点一个TD
        let tds = [];
        for (let col of ls) {
          let dom = backDoms(col);
          let lis = []; //li 标签
           //通过li嵌套层级
          lis.push(h('li', {class:"cross-node", attrs:{level:col.level}}, dom));
           //查找下级
          if (col.children && col.children.length > 0) {
            for (let i = col.level + 1; i <= maxlevel; i++) {

              let nds = [];
              utils.loopDims(col.children, a => {
                if (a.level === i) {
                  let ret = backDoms(a);
                  $(ret).each((c, d)=>{
                    nds.push(d);
                  });
                }
              });
              let hli = h('li',{class:"cross-node", attrs:{level:i}}, nds);
              lis.push(hli);
            }
          }
          tds.push(h('td', {attrs:{valign:"top"}},lis));
        }
        return h('table', [h('tr', tds)]);
      }else if(pos === 'row'){
        let wd = Math.round(100/maxlevel);
        let tds = [];
        //按level生成div

        for(let i=1; i<=maxlevel; i++){
          let nodes = [];
          utils.loopDims(comp.rows, (item) => {
            if(item.level === i) {
              let r = backDoms(item);
              $(r).each((a, b)=>{
                nodes.push(b);
              });
            }
          });
          tds.push(h('td', {attrs:{width: wd + "%", level:i}}, nodes));
        }
        let rowH = h('table', {style:{width:"100%"}}, [h('tr', tds)]);
        //处理交叉处
        wd = Math.round(100 / comp.rowHeads.length);
        let htds = [];
        $(comp.rowHeads).each((idx, val) =>{
          htds.push(h('td', {attrs:{width:wd+"%"}}, [h('span', {class:"dimcol", style:{width:"95%"}}, [
            h('span', {class:"text"}, val.desc),
            h('div', {class:"ibox-tools"}, [h('button', {class:"btn btn-outline btn-success btn-xs tablecrossbtn",on:{click:()=>{this.setNodeInfo('cross', idx, val)}}, domProps:{innerHTML:`<i class="fa fa-wrench"></i>`}})])
          ])]));
        });

        let crossH = h('table', {style:{width:"100%"}}, [h('tr', htds)]);
        return {rh : rowH, ch : crossH}
      }
    },
    setNodeInfo(pos, nodeType, node){
      let ts = this;
      $.contextMenu( 'destroy');
      const comp = this.comp.comp;
      //维度
      if(nodeType === 'dim') {
        const items = {
          "prop": {name: "属性..."},
          "move": {
            name: "移动", items: {
              left: {name: pos === 'col' ? "左移" : "上移"},
              right: {name: pos === 'col' ? "右移" : "下移"},
              more: {name: "移动多步"},
              moveTo: {name: "移至下级..."}
            }
          },
          "insert":{name:"插入行"},
          "split":{name:"分解"},
          "asc": {name: "升序", icon: "fa-sort-amount-asc"},
          "desc": {name: "降序", icon: 'fa-sort-amount-desc'},
          "filter": {name: "筛选...", icon: "fa-filter"},
          "aggre": {name: "聚合...", icon: ""},
          "top": {name: "取Top...", icon: ""},
          "remove": {name: "删除", icon: "fa-remove"}
        };
        //只能在行上插入
        if(pos === 'col'){
          delete items.insert;
        }
        $.contextMenu({
          selector: '#dataProperty button.tableoptbtn',
          trigger: 'left',
          delay: 500,
          autoHide: true,
          callback: function (opt) {
            if (opt == "prop") {
              ts.$refs['dimPropertyForm'].openDailog(node, pos, comp);
            } else if (opt == "asc" || opt == "desc") {
              ts.dimsort(opt, node);
            } else if (opt == "filter") {
              ts.filterDims(node, pos);
            } else if (opt == "aggre") {
              ts.$refs['tableDimAggreForm'].openDailog(node, comp);
            } else if (opt == "top") {
              ts.$refs['tableDimTopForm'].openDailog(node, comp);
            } else if (opt == "remove") {
              ts.delJsonKpiOrDim('dim', node, pos);
            } else if (opt === 'left' || opt === 'right' || opt === 'moveTo' || opt === 'more') {
              ts.moveCrossFiled(opt, pos, node);
            }else if(opt === 'insert'){
              ts.insertNoteRow(pos, node);
            }else if(opt === "split"){
              let isfrd = false;
              $(comp.dims).each((a, b) => {
                if(b.id === node.match){
                  isfrd = b.type === 'frd';
                  return false;
                }
              });
              if(!isfrd){
                utils.msginfo("维度类型不支持分解");
                return;
              }
              //维度分解
              ts.$refs['splitDimsForm'].openDailog(node, comp, pos, false);
            }
          },
          items
        });
      }else if(nodeType === 'kpi'){
        //设置度量排序的标识
        const kpi = comp.kpiJson.filter(t=>t.kpi_id === node.match)[0];
        const items = {
          "compute": {name: "计算", items:{sq:{name:"上期"}, tq:{name:"同期"}, zje:{name:'增减额'}, hb:{name:'环比(%)'}, tb:{name:"同比(%)"},lj:{name:"累计"},
              "sep1": "---------",zdrq:{name:"比较指定日期"},"sep2": "---------",sxpm:{name:"升序排名"}, jxpm:{name:"降序排名"}}},
          "prop": {name: "属性..."},
          "move":{name:"移动", items:{
              left:{name:pos==='col'?"左移":"上移"},
              right:{name:pos==='col'?"右移":"下移"},
              more:{name:"移动多步"},
              moveTo:{name:"移至下级..."}
            } },
          "insert":{name:"插入行"},
          "merge":{ name:"合并..."},
          "yujing": {name: "预警...", icon:"fa-warning"},
          "sort":{name:"排序", items:{asc:{name:"升序",icon:"fa-sort-amount-asc"},
              desc:{name:"降序", icon:"fa-sort-amount-desc"},
              def:{name:"默认", icon:""}}},
          "clear": {name: "删除", icon:"fa-times"}
        };
        if(kpi.compute){
          let ls = kpi.compute.split(",");
          $(ls).each(function(a, b){
            items.compute.items[b].icon = 'fa-check';
          });
        }
        //只能在行上插入
        if(pos === 'col'){
          delete items.insert;
        }
        $.contextMenu({
          selector: '#dataProperty button.tableKpioptbtn',
          trigger: 'left',
          delay: 500,
          autoHide:true,
          callback: function(opt) {
            if(opt == "sq" || opt == "tq" || opt == "zje" || opt == "hb" || opt == "tb" || opt =='lj' || opt === 'zb' || opt == 'sxpm' || opt == 'jxpm'){
              ts.kpicompute(opt, node);
            }else if(opt === 'zdrq'){  //数据和指定日期做比较
              let kpi = comp.kpiJson.filter(m=>m.kpi_id === node.match)[0];
              ts.$refs['tableCompareDateForm'].openDailog(kpi, ts.pageInfo, ()=>{
                  ts.setUpdate();
                  ts.tableView();
              });
            }else if(opt == "asc" || opt == "desc" || opt == 'def'){
              if(opt == "def"){
                opt = "";
              }
              ts.kpisort(node, opt);
            }else if(opt == "prop"){
              ts.$refs['tableKpiPropForm'].openDailog(node, comp);
            }else if(opt == "clear"){
              ts.delJsonKpiOrDim('kpi', node, pos);
            }else if(opt == "yujing"){
              ts.$refs['tableWarningForm'].openDailog(node, comp);
            }else if(opt === 'left' || opt === 'right' || opt === 'moveTo' || opt === 'more'){
              ts.moveCrossFiled(opt, pos, node);
            }else if(opt === "merge"){
              ts.$refs['tableKpiMergeForm'].openDailog(node, pos, comp);
            }else if(opt === 'insert'){
              ts.insertNoteRow(pos, node);
            }
          },
          items
        });
      }else if(nodeType === 'none' || nodeType === 'note' || nodeType === 'kpiMatch'){ //type='none'/'note' 类型
        let items = {
          "prop": {name: "属性..."},
          "move":{name:"移动", items:{
              left:{name:pos==='col'?"左移":"上移"},
              right:{name:pos==='col'?"右移":"下移"}
            } },
          "merge":{ name:"合并..."},
          "insert":{name:"插入行"},
          "clear": {name: "删除", icon:"fa-times"}
        };
        //只能在行上插入
        if(pos === 'col'){
          delete items.insert;
        }
        //只在列上合并
        if(pos === 'row'){
          delete items.merge;
        }
        $.contextMenu({
          selector: '#dataProperty button.tableNonebtn',
          trigger: 'left',
          delay: 500,
          autoHide:true,
          callback: function(opt) {
            if(opt == "prop"){
              if(nodeType==='kpiMatch'){
                 ts.$refs['splitDimsForm'].openDailog(node, comp, pos, true);
              }else{
                 ts.$refs['crossnodePropertyForm'].openDailog(node, comp, false);
              }
            }else if(opt === "clear"){
              ts.delJsonKpiOrDim(nodeType, node, pos);
            }else if(opt === 'left' || opt === 'right'){
              ts.moveCrossFiled(opt, pos, node);
            }else if(opt === 'insert'){
              ts.insertNoteRow(pos, node);
            }else if(opt === 'merge'){
              ts.$refs['tableKpiMergeForm'].openDailog(node, pos, comp);
            }
          },
          items
        });
      }else if(pos === 'cross'){
        $.contextMenu({
          selector: '#dataProperty button.tablecrossbtn',
          trigger: 'left',
          delay: 500,
          autoHide:true,
          callback: function(opt) {
            if(opt === "prop"){
              let nd = comp.rowHeads[nodeType];
              ts.$refs['crossnodePropertyForm'].openDailog(nd, comp, true);
            }
          },
          items:{
            "prop": {name: "属性..."}
          }
        });
      }
    },
    insertNoteRow(pos, node){
      this.$refs['insertNoteRowForm'].openDailog(node, this.comp.comp);
    },
    setRowHeader(pos, r){
      let comp = this.comp.comp;
      //设置crossHead
      if(pos === 'row'){
        if(!comp.rowHeads){
          comp.rowHeads = [];
        }
        let idx = r.level - 1;
        if(!comp.rowHeads[idx]) {
          comp.rowHeads[idx] = {desc:r.desc};
        }
      }
    },
    //移动交叉表的行列标签的节点
    moveCrossFiled(opt, pos, item){
      const comp = this.comp.comp;
      const id = item.id;
      const ls = pos === 'row'?comp.rows:comp.cols;
      let canMove = true;
      utils.loopDims(ls, (a,b,c)=>{
        if(a.id === id){
          if(b.length <= 1){
            canMove = false;
          }
        }
      });
      if(!canMove){
        utils.msginfo('无效移动。');
        return;
      }
      utils.loopDims(ls, (item,b,i)=>{
        if(item.id == id){
          if(opt === 'left'){
            if(i <= 0){
              utils.msginfo('无效移动。');
              return false;
            }else{
              let tp = b[i - 1];
              b[i - 1] = b[i];
              b[i] = tp;
              this.setUpdate();
              this.tableView();
            }
          }else
          if(opt === 'right'){
            if( i >= b.length - 1){
              utils.msginfo('无效移动。');
              return;
            }else{
              let tp = b[i + 1];
              b[i + 1] = b[i];
              b[i] = tp;
              this.setUpdate();
              this.tableView();
            }
          }
          else if(opt === 'moveTo'){
            this.$refs['tableMoveToForm'].openDailog(ls, item, pos);
          }else if(opt === 'more'){
            this.$refs['tableMoveForm'].showDailog(comp, ls, item);
          }
          return false;
        }
      });
    },
    //从交叉表JSON中删除KPI
    delJsonKpiOrDim(tp, node, pos){
      let comp = this.comp.comp;
      let matchId = node.match;
      let id = node.id;
      if(tp === 'kpi'){
        var kpis = comp.kpiJson;
        var idx = -1;
        for(var i=0; i<kpis.length; i++){
          if(kpis[i].kpi_id === matchId){
            idx = i;
            break;
          }
        }
        kpis.splice(idx, 1);
      }
      if(tp === 'dim'){
        var dims = comp.dims;
        for(let i=0; i<dims.length; i++){
          if(dims[i].id === matchId){
            idx = i
            break;
          }
        }
        dims.splice(idx, 1);
      }
      //从 cols/rows中删除
      const ls = pos === "row" ? comp.rows : comp.cols;
      let curNode = null;
      utils.loopDims(ls, (a, b, c) => {
        if(a.id === id){
          curNode = a;
          b.splice(c, 1);
        }
      });
      //从 kpiJson, dims 中删除当前节点的下级
      utils.loopDims(curNode.children, (a, b, c)=>{
        if(a.type === 'kpi') {
          $(comp.kpiJson).each((idx, item) => {
            if(item.kpi_id === a.match){
              comp.kpiJson.splice(idx, 1);
              return false;
            }
          });
        }else if(a.type === 'dim'){
          $(comp.dims).each((idx, item) => {
            if(item.id === a.match){
              comp.dims.splice(idx, 1);
              return false;
            }
          });
        }
      });
      //如果是kpiMath类型（维度分解后的类型），在删除完kpiMath后，需要删除对应的 dim
      if(tp === 'kpiMatch') {
        let cnt = 0;
        utils.loopDims(ls, (a) => {
          if (a.match === matchId) {
            cnt++;
          }
        });
        if(cnt === 0){
          $(comp.dims).each((a, b)=>{
            if(b.id === matchId){
              comp.dims.splice(a, 1);
              return false;
            }
          });
        }
      }
      //删除crossHead
      if(pos === 'row'){
        let cnt = 0;
        utils.loopDims(comp.rows, (a, b, c)=>{
          if(a.level === curNode.level){
            cnt++;
          }
        });
        if(cnt === 0){
          let l = comp.rowHeads.length;
          let idx = curNode.level - 1;
          comp.rowHeads.splice(idx, l - idx);
        }
      }
      this.setUpdate();
      this.tableView();
    },
    dimsort(tp, node){
      let comp = this.comp.comp;
      let dims = comp.dims;
      for(let i=0; i<dims.length; i++){
        if(dims[i].id == node.match){
          dims[i].dimord = tp;
          break;
        }
      }
      //进行维度排序时，清除度量的排序信息
      for(let i=0; i<comp.kpiJson.length; i++){
        comp.kpiJson[i].sort = '';
      }
      this.setUpdate();
      this.tableView();
    },
    filterDims(node, pos){
      let comp = this.comp.comp;
      let curNode = comp.dims.filter(m=>m.id === node.match)[0];
      this.$refs['paramFilterForm'].createDimFilter(curNode, comp, 'table');
    },
    kpicompute(tp, node){
      let comp = this.comp.comp;
      var kpis = comp.kpiJson;
      var kpi = null;
      for(let j=0; j<kpis.length; j++){
        if(kpis[j].kpi_id == node.match){
          kpi = kpis[j];
          break;
        }
      }
      if(tp == "sxpm" || tp == 'jxpm'){
        if(kpi.compute == tp){
          delete kpi.compute;
        }else{
          kpi.compute = tp;
        }
      }else{
        //先判断已经存在的，如果是时间偏移计算就追加，或者替换.
        var exist = kpi.compute;
        if(!exist || exist == ""){
          kpi.compute = tp;
        }else{
          var js = exist.split(",");
          //过滤 sxpm, jxpm
          var newjs = [];
          for(let j=0; j<js.length; j++){
            if(!(js[j] == 'sxpm' || js[j] == 'jxpm')){
              newjs.push(js[j]);
            }
          }
          js = newjs;

          var idx = -1;   //不存在才添加
          for(let j=0; j<js.length; j++){
            if(js[j] == tp){
              idx = j;  //存在
              break;
            }
          }
          if(idx == -1){
            if(js.length > 0){
              kpi.compute = js.join(",")+","+tp;
            }else{
              kpi.compute = tp;
            }
          }else{  //存在直接删除
            js.splice(idx, 1);
            kpi.compute = js.join(",");
          }
        }
      }
      this.setUpdate();
      this.tableView();
    },
    kpisort(node, tp){
      let comp = this.comp.comp;
      for(let i=0; i<comp.kpiJson.length; i++){
        if(comp.kpiJson[i].kpi_id == node.match){
          comp.kpiJson[i].sort = tp;
        }else{
          delete comp.kpiJson[i].sort;
        }
      }
      //清除rows维度排序
      utils.loopDims(comp.rows, o=>{
        let dim = comp.dims.filter(item => item.id === o.match)[0];
        delete dim.dimord;
      });
      this.setUpdate();
      this.tableView();
    }

  },
  watch: {

  }
}
</script>

<style lang="less" scoped>
 .tipinfo {
	color:#999;
	padding:10px;
}
TABLE.grid3 {
    width: 100%;
    border-collapse: collapse;
    table-layout: fixed;
}
TD.grid3-td {
  border: 1px solid #CACACA;
    padding: 3px 2px 3px 2px;
    font-size: 12px;
    height: 20px;
    word-wrap: break-word;
}
  .col {
    border: 1px solid #DF7809;
    display: inline-block;
    margin: 3px;
    padding: 3px;
    text-align: center;
    width:120px;
    font-size:14px;
    border-radius:5px;
  }

  .dimcol {
    border: 1px solid #0C6ADF;
    display: inline-block;
    margin: 3px;
    padding: 3px;
    text-align: left;
    font-size:14px;
    width:120px;
    border-radius:5px;
  }
  .boxcol {
    border: 1px solid #DF7809;
    display: inline-block;
    margin: 3px;
    padding: 3px;
    text-align: center;
    width:120px;
    border-radius:5px;
  }
 .ibox-tools {
    display: inline-block;
    float: right;
    margin-top: -1px;
    position: relative;
    padding: 0;
}
li.cross-node {
	list-style-type: none;
}
</style>
