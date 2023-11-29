<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div style="margin-top:5px;">
    <template v-if="showBtn">
      <div class="table-toolbar" style="margin-bottom:3px;padding-top:10px;">
          <button class="btn btn-sm btn-info" type="button" @click="createDyna(false)">创建动态字段</button>
          &nbsp;
          <el-button  @click="before()"><i class="fa fa-arrow-left"></i> 上一步</el-button>
          <el-button @click="save()">下一步 <i class="fa fa-arrow-right"></i></el-button>
      </div>
    </template>
    <template v-if="!showBtn">
      <div style="padding-bottom:5px;">
       <button class="btn btn-default btn-rounded btn-outline btn-xs" type="button" @click="createDyna(false)">创建动态字段</button>
      </div>
    </template>

     <el-table :data="tableData" height="430" border style="width: 100%" header-row-class-name="tableHeadbg">
        <!--
        <el-table-column type="selection" width="45"></el-table-column>
        -->
        <el-table-column label="操作" width="90" align="center" >
          <template slot-scope="scope">
            <template v-if="scope.row.expression">
            <button class="btn btn-link btn-xs" type="button" @click="editer(scope.row.colId)">
              <i class="fa fa-edit"></i>
            </button>
            <button class="btn btn-link btn-xs" type="button" @click="del(scope.row.colId)">
              <i class="fa fa-remove"></i>
            </button>
            </template>
             <template v-if="!scope.row.expression">
                -
             </template>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="colId" label="标识" width="120"></el-table-column>
        <el-table-column align="left" prop="colName" label="字段名"></el-table-column>
        <el-table-column align="left" prop="colNote" label="备注"></el-table-column>
        <el-table-column align="center" prop="colType" width="100" :formatter="fmttype" label="字段类型"></el-table-column>
        <el-table-column align="center" prop="colLength" width="80" label="长度"></el-table-column>
        <el-table-column align="center" prop="expression" label="表达式"></el-table-column>
      </el-table>
     <div style="margin-top:20px;" v-if="showBtn">
        <el-button  @click="before()"><i class="fa fa-arrow-left"></i> 上一步</el-button>
        <el-button @click="save()">下一步 <i class="fa fa-arrow-right"></i></el-button>
     </div>
     <tableDynaAdd ref="dynaAddForm" :callback="addSave"></tableDynaAdd>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import tableDynaAdd from '@/view/etl/table/TableDynaAdd'
import $ from "jquery";

export default {
  data() {
    return {
      tableData:[]
    }
  },
  props:{
    pageJson: {
      type: Object,
      required: true,
    },
    //是否显示上一步，下一步按钮
    showBtn:{
      type:Boolean,
      required:false,
      default:true
    }
  },
  components: {
    tableDynaAdd
	},
  mounted() {
   this.loadCols();
  },
  computed: {},
  methods: {
    loadCols(){
      var json = this.pageJson;
      if(!json.tid){
        return;
      }
      ajax({
        url:"model/newCubeStep2.action",
        type:"POST",
        data:{
          tableType:"horizontal",
          kpiCodeColumn:"",
          kpiNameColumn:"",
          kpiValueColumn: "",
          tid : json.tid,
          tName : json.tName,
          onlyTableId: 'n'
        },
        success:(r)=>{
          this.tableData = r.rows;
        }
      }, this);
    },
	  save(){
      this.$parent.active = 2;
      this.$nextTick(()=>{
        //初始化tree
        let cube = this.$parent.$refs['cubeForm'];
        cube.initLeftTree();
        cube.initRightCubeTree();
      });
    },
    before(){
       this.$parent.active = 0;
    },
    fmttype(a, b, c){
      if(c === 'String'){
        return "字符串";
      }else if(c === 'Double'){
        return "小数";
      }else if(c === 'Int'){
        return "整数";
      }else if(c === 'Date'){
        return "日期";
      }else if(c === 'Datetime'){
        return "时间";
      }else if(c ==='Long'){
        return "长整数";
      }
      return '-';
    },
    editer(colId){
      var json = this.pageJson;
      var col = this.tableData.filter(m=>m.colId === colId)[0];
      this.$refs['dynaAddForm'].modify(json.tid, json.tName, col, this.tableData);
    },
    del(colId){
      var json = this.pageJson;
        if(confirm("是否确认删除?")){
          ajax({
            url: "etl/delTableColumn.action",
            type: "post",
            data: {
              tableId: json.tid,
              colId: colId
            },
            success:(r)=>{
               this.$notify.success("删除成功");
               this.loadCols();
               //更新立方体
                this.$parent.$parent.$parent.$parent.$refs['cubeForm'].initLeftTree();
            }
          });
        }
    },
    createDyna(){
      var json = this.pageJson;
      this.$refs['dynaAddForm'].add(json.tid, json.tName, this.tableData);
    },
    //保存动态字段后的回调函数
    addSave(isupdate){
      this.loadCols();
      if(isupdate == false){
        //更新立方体
        this.$parent.$parent.$parent.$parent.$refs['cubeForm'].initLeftTree();
      }else{
        this.$parent.$parent.$parent.closeDailog();
      }
    }
  },
  watch: {
    "pageJson.tid":function(v){
      if(v){
        this.loadCols();
      }
    }
  }
};
</script>
