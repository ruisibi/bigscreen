/**
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
/**
 * 数据ETL工具类
 */
import $ from 'jquery'
import { baseUrl, ajax } from "@/common/biConfig";
import { Loading } from 'element-ui';

export const periods = [
  {value:"minute",label:"每分钟"},{value:"hour",label:"每小时"},{value:"day",label:"每天"},{value:"week",label:"每周"},{value:"month",label:"每月"},
];

/**
 * 是否是中文
 * @param {*} a
 */
export const ischinese = (a)=>{
     if (/[\u4E00-\u9FA5]/i.test(a)) {
        return true;
      } else {
        return false;
      }
}
/**
 *
 * @param {是否以字母开始} w
 */
export const isStartWithWord = (w) => {
  var asc = w.charCodeAt(0);
  if ((asc >= 65 && asc <= 90 || asc >= 97 && asc <= 122)) {
      return true;
  }else{
    return false;
  }
}
/**
是否包含特殊字符
**/
export const containSpecial = (s) =>{
    var containSpecial = RegExp(/[(\ )(\~)(\!)(\@)(\#)(\$)(\%)(\^)(\&)(\*)(\()(\))(\-)(\+)(\=)(\[)(\])(\{)(\})(\|)(\\)(\;)(\:)(\')(\")(\,)(\.)(\/)(\<)(\>)(\?)(\)]+/);
    return containSpecial.test(s);
}

export const getTableColumns = (tableId, srccols, ts, clickFunc, callback) => {
  if(!tableId){
    return;
  }
  var exef = (cols) => {
    //生成panel
    var srcexist = function srcexist(col) {
      var ret = null;

      for (let k = 0; srccols && k < srccols.length; k++) {
        if (srccols[k].col == col) {
          ret = srccols[k];
          break;
        }
      }
      return ret;
    };

    var dt = [];

    for (let i = 0; i < cols.length; i++) {
      var nd = srcexist(cols[i].colName);
      var srcidx = nd ? nd.link : null;
      let colName = cols[i].colName;
      if(nd){
        if(nd.link || nd.link == 0){
          colName +=  " -> " +  "Field"+ (Number(srcidx) + 1);
        }else if(nd.defvalue){
          colName += " -> " + "固定值(" + nd.defvalue + ")";
        }
      }
      dt.push({
        id: cols[i].colName,
        text: colName,
        "icon": "glyphicon glyphicon-menu-hamburger",
        parent: "#",
        li_attr: {
          name: cols[i].colName,
          type: cols[i].colType,
          defvalue: nd ? nd.defvalue : cols[i].defvalue,
          link: srcidx
        }
      });
    }

    var ref = $('#tabletree').jstree(true);

    if (ref) {
      ref.destroy();
    }

    $("#tabletree").jstree({
      core: {
        check_callback: true,
        data: dt
      },
      "plugins": ["wholerow"]
    }).bind("ready.jstree", function () {
      if(callback){
        callback();
      }
    }).bind("click.jstree", function () {
      if(clickFunc){  //点击函数
        var ref = $("#tabletree").jstree(true);
        var node = ref.get_node(ref.get_selected());
        clickFunc(node);
      }
    });
  };

  ajax({
    type: "GET",
    url: "etl/getTableColumnsNotExpress.action",
    data: {
      "tableId": tableId
    },
    success: (resp) => {
      exef(resp.rows);
    },
  }, ts);
}

export const automatch = (ts, cfg) => {
  var ref = $('#tabletree').jstree(true);
  if(!ref){
    ts.$notify.error("还未生成表字段。");
    return;
  }
  var ls = ref.get_children_dom('#');

  if (ls == null || ls.length == 0) {
    ts.$notify.error("还未生成表字段。");
    return;
  }

  ajax({
    type: "post",
    url: "etl/showImpColumns.action",
    dataType: "json",
    postJSON:true,
    data: JSON.stringify(cfg),
    success: function success(resp) {
      var cols = resp.rows;
      if (!cols || cols.length === 0) {
        ts.$notify.error("无数据，请先测试您的sql/csv/excel文件是否正确。");
        return;
      }

      var findexist = function findexist(id) {
        var ret = null;

        for (let j = 0; j < cols.length; j++) {
          if (cols[j].name.toUpperCase() == id.toUpperCase()) {
            ret = cols[j];
            ret.idx = j + "";
            break;
          }
        }

        return ret;
      };

      for (let k = 0; k < ls.length; k++) {
        var id = ls[k].id;
        var r = findexist(id);

        if (r != null) {
          var o = ref.get_node(ls[k]);
          o.li_attr.link = r.idx;
          ref.rename_node(o, ls[k].id + " -> Field" + (Number(r.idx) + 1));
        }
      }
    },
  }, ts);
}

export const saveConfig = (ts, uploadFile, cfg)=>{
    var ref = $('#tabletree').jstree(true);
    if(ref){
      cfg.cols = [];
      var cols = ref.get_children_dom('#');
      for (let i = 0; cols && i < cols.length; i++) {
        var node = ref.get_node(cols[i]);
        var lk = node.li_attr.link;
        var type = node.li_attr.type;
        var defvalue = node.li_attr.defvalue;
        var col = node.id;
        cfg.cols.push({
          "col": col,
          "type": type,
          "defvalue": defvalue,
          "link": lk
        });
      }
    }
    if(!cfg.cfgid){ //新增
        var json = {
            cfgContent: JSON.stringify(cfg),
            cfgName: uploadFile.nodeName,
            impType: uploadFile.impType,
            fileId: uploadFile.fileId,
            targetTableId: uploadFile.targettableid,
          };
        ajax({
          type: "POST",
          url: 'etl/saveCfg.action',
          dataType: "json",
          postJSON:true,
          data: JSON.stringify(json),
          success: (resp) => {
            ts.$notify.success("保存成功！");
            uploadFile.cfgid = resp.rows;
          }
        }, ts);
    }else{
        var json = {
            cfgContent: JSON.stringify(cfg),
            cfgid: cfg.cfgid,
            fileId: uploadFile.fileId,
            cfgName:cfg.nodeName,
            targetTableId: cfg.targettableid,
          };
        ajax({
          type: "POST",
          url: 'etl/updateCfg.action',
          dataType: "json",
          postJSON:true,
          data: JSON.stringify(json),
          success: (resp) => {
            ts.$notify.success("更新成功！");
          }
        });
    }
}

export const resume = (uploadFile, r, cfgid)=>{
      uploadFile.path = r.filePath;
      uploadFile.fileName = r.fileName;
      uploadFile.fileType = r.fileType;
      uploadFile.fileId = r.id;

      var json = JSON.parse(r.config);
      uploadFile.splitWord = json.splitWord;
      uploadFile.sheetIndex = json.sheetIndex;
      uploadFile.encode = json.encode;
      uploadFile.nameinhead = json.nameinhead;
      uploadFile.targettable = json.targettable;
      uploadFile.targettableid = json.targettableid;
      uploadFile.truncate = json.truncate;
      uploadFile.clearKey = json.clearKey;
      uploadFile.impType = json.impType;
      uploadFile.datelabel = json.datelabel;
      uploadFile.sql = json.sql;
      uploadFile.cfgid = cfgid;
      uploadFile.dsource = json.dsource;
      uploadFile.srcTables = json.srcTables;
      uploadFile.exception = json.exception;
      uploadFile.autopage = json.autopage;
      uploadFile.nodeName = r.nodeName;
      return json;
}

export const startImp = (ts, cfg) => {
  var ref = $('#tabletree').jstree(true);
  if(!ref){
    ts.$notify.error("您还未选择数据导入的目标表。");
    return
  }
  var cols = ref.get_children_dom('#');

  if (cols == null || cols.length == 0) {
    ts.$notify.error("您还未选择数据导入的目标表。");
    return;
  }

  var ismatch = false; //判断是否有字段关联, 只要有一个字段关联上就算关联了

  for (let i = 0; i < cols.length; i++) {
    var lk = cols[i].attributes.link;

    if (lk) {
      ismatch = true;
      break;
    }
  }

  if (ismatch == false) {
    ts.$notify.error("目标表字段未关联到输入字段！");
    return;
  }

  var json = {
    impType: cfg.impType,
    targettableid: cfg.targettableid,
    targettable: cfg.targettable,
    sheetIndex: cfg.sheetIndex,
    encode: cfg.encode,
    splitWord: cfg.splitWord,
    path: cfg.path,
    nameinhead: cfg.nameinhead,
    cols: [],
    sql: cfg.sql,
    datelabel: cfg.datelabel,
    truncate: cfg.truncate,
    clearKey: cfg.clearKey,
    dsource:cfg.dsource,
    link:cfg.link,
    srcTables:cfg.srcTables,
    exception: cfg.exception,
    cfgid: cfg.cfgid,
    rest: cfg.rest,
    restCols: cfg.restCols,
    autopage: cfg.autopage,
    url: cfg.url,
    chartset: cfg.chartset,
    selectTable: cfg.selectTable,
    firstAsHead: cfg.firstAsHead,
  };

  for (let i = 0; cols && cols != null && i < cols.length; i++) {
    var node = ref.get_node(cols[i]);
    var lk = node.li_attr.link;
    var type = node.li_attr.type;
    var defvalue = node.li_attr.defvalue;
    var col = node.id;
    json.cols.push({
      "col": col,
      "type": type,
      defvalue: defvalue,
      "link": lk
    });
  }

  if (confirm("是否确认？")) {
    let txt = `任务进行中，请稍后...已导入0条数据。`;
    let loadinstance = Loading.service({fullscreen: true, text:txt, customClass:"dataImportDlg" });
    var inter = showProcPanel(json, ts);
    ajax({
      type: "post",
      url: "etl/runDataLoad.action",
      dataType: "JSON",
      data: JSON.stringify(json),
      postJSON:true,
      success: function success(resp) {
        clearInterval(inter);
        loadinstance.close();
        if (resp.result == 1) {
          ts.$notify.success(resp.rows + '条数据导入成功。');
        } else {
          ts.$notify.error(resp.msg);
        }
      }
    }, ts, loadinstance, ()=>{
      clearInterval(inter);
    });
  }
}

export const showProcPanel = (json, ts)=>{
  let inter = window.setInterval(function () {
    ajax({
      type: "get",
      url: "etl/getLoadState.action",
      dataType: "json",
      data: {
        tableId: json.targettableid,
        t: Math.random()
      },
      success: function success(resp) {
        var txt = `任务进行中，请稍后...已导入${resp.rows}条数据。`;
        $(".dataImportDlg .el-loading-text").text(txt);
      }
    }, ts);
  }, 2000);
  return inter;
}

export const managerType = (ts)=>{
  ts.$router.push({path:"/etl/tf/TfType", query:{}});
  //添加菜单
  ts.$parent.$parent.$refs['navMenuForm'].menuAdd({menuId:"9988", menuName:"节点分类管理", menuUrl:"/etl/tf/TfType"});
}
