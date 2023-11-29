<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
 <!--
 创建接口API
 -->
<template>
<div class="wrapper-content">
    <div class="ibox" id="maindiv">
      <div class="ibox-title">数据集管理</div>
      <div class="ibox-content">
       <el-row>
        <el-col :span="8">
        <div class="text-warning">数据集可以是一张表、一句SQL语句或一个Restful接口</div>
       <div style="margin-bottom:10px;" class="btn-group" role="group">
          <el-dropdown style="float:left;" placement="bottom-start" @command="handleCommand">
					<button type="button" style="border-top-right-radius:0px;border-bottom-right-radius:0px;border-right:0px;" class="btn btn-outline btn-default" title="新增">
						<i class="glyphicon glyphicon-plus" aria-hidden="true"></i>
					</button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="reg">通过数据源注册表</el-dropdown-item>
            <el-dropdown-item command="sql">通过SQL创建数据集</el-dropdown-item>
            <el-dropdown-item command="rest">注册Restful接口</el-dropdown-item>
          </el-dropdown-menu>
          </el-dropdown>
					<button type="button" class="btn btn-outline btn-default" title="编辑" @click="updateDataset()">
						<i class="glyphicon glyphicon-edit" aria-hidden="true"></i>
					</button>
					<button type="button" class="btn btn-outline btn-default" title="删除" @click="delDataset()">
						<i class="glyphicon glyphicon-trash" aria-hidden="true"></i>
					</button>
				</div>
         </el-col>
        <el-col :span="16" align="right">
               <el-input placeholder="请输入关键字" size="mini" style="width:220px;" clearable v-model="keyword" class="input-with-select">
								<el-button slot="append" icon="el-icon-search" @click="loadDatas()"></el-button>
							</el-input>

         </el-col>
      </el-row>
        <el-table :data="tableData" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
						<el-table-column label="" width="45">
							<template slot-scope="scope">
								<el-radio v-model="checked" name="myselect2" :label="scope.row.tableId">&nbsp;</el-radio>
							</template>
						</el-table-column>
            <el-table-column align="center" prop="tableId" label="标识" width="90"></el-table-column>
						<el-table-column align="center" prop="tableName" label="名称"></el-table-column>
						<el-table-column align="center" prop="tableNote" label="中文名"></el-table-column>
            <el-table-column align="center" prop="income" label="来源" :formatter="fmtval"></el-table-column>
						<el-table-column align="center" prop="crtDate" label="创建时间"></el-table-column>
            <el-table-column align="center" prop="tableId" label="操作">
							<template slot-scope="scope">
								<a class="btn btn-info btn-xs" @click.stop="viewTable(scope.row.tableId)"> 详情 </a>
							</template>
						</el-table-column>

					</el-table>
          <el-pagination
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-sizes="[10, 20, 50, 100]"
            :current-page="page"
            :page-size="rows"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
				</el-pagination>
      </div>
    </div>
    <dataSetAdd ref="dataSetAddForm"></dataSetAdd>
    <regTable ref="regTableForm"></regTable>
    <dbTable ref="dbTableForm"></dbTable>
    <tabeInfoView ref="tabeInfoViewForm"></tabeInfoView>
</div>

</template>
<script>
import {baseUrl, ajax} from '@/common/biConfig'
import dataSetAdd from './DatasetAdd'
import dbTable from '@/view/etl/reg/DBTable'
import regTable from '@/view/etl/reg/RegTable'
import tabeInfoView from '@/view/etl/table/TableInfoView'

export default {
  components: {
    dataSetAdd, dbTable, regTable, tabeInfoView
  },
  props: {

  },
  data() {
    return {
      tableData:[],
      checked:null,
      total:0,
      page:1,
      rows:10,
      keyword:null,
    }
  },

  methods: {
    handleCommand(c){
      if(c == 'sql'){
        this.newDataset(false);
      }else if(c == 'reg'){
        this.$refs['dbTableForm'].newCustom();
      }else if(c == 'rest'){
        this.$refs['regTableForm'].newRestTable();
      }
    },
    newDataset(isupdate){
       if(isupdate == true){
        if(!this.checked){
          this.$notify.error("未勾选数据");
          return;
        }
        this.$refs['dataSetAddForm'].updateDataset(this.checked);
       }else{
         this.$refs['dataSetAddForm'].addDataset();
       }
    },
    updateDataset(){
      if(!this.checked){
        this.$notify.error("未勾选数据");
        return;
      }
      let c = this.tableData.filter(m=>m.tableId==this.checked)[0];
      if(c.income == 'ds'){
         this.$refs['dataSetAddForm'].updateDataset(this.checked);
      }else if(c.income == 'rest'){
        this.$refs['regTableForm'].updateRestTable(true, this.checked);
      }else if(c.income == 'custom'){
        this.flash(c);
      }
    },
    flash(row){

      ajax({
        type:"post",
        url:"etl/flashRegTable.action",
        dataType:"json",
        data:{tableId:row.tableId,tableName:row.tableName, income:row.income, dsid:row.dsourceId},
        success:(resp) => {
          if(resp.result == 1){
            this.$notify.success("表刷新成功。");
            this.loadDatas();
          }else{
            this.$notify.error(resp.msg);
          }
        }
      }, this);
    },
    fmtval(a, b, v){
      if(v == 'ds'){
        return "SQL语句";
      }else if(v == 'custom'){
        return "已有表";
      }else if(v == 'rest'){
        return "rest接口";
      }
      return "";
    },
    loadDatas(){
      ajax({
				type:"POST",
				data:{page:this.page, rows:this.rows, income:"ds,custom,rest", search:this.keyword},
				url:"etl/loadByIncomes.action",
				success:(resp)=>{
					this.tableData = resp.rows;
          this.total = resp.total;
				}
			}, this);
    },

    selectme:function(a, b){
			this.checked = a.tableId;
    },

    handleSizeChange(v){
			this.rows = v;
			this.loadDatas();
		},
		handleCurrentChange(v){
			this.page = v;
			this.loadDatas();
    },
    delDataset(){
      if(!this.checked){
        this.$notify.error("未勾选数据");
        return;
      }
      if(confirm('是否确认？')){
          ajax({
            url:"model/dataset/delete.action",
            data:{id: this.checked},
            type:"GET",
            success:(resp)=>{
              this.checked = null;
              this.loadDatas();
            }
          }, this);
      }
    },
    viewTable(tableId){
      let c = this.tableData.filter(m=>m.tableId==tableId)[0];
      this.$refs['tabeInfoViewForm'].showInfo(tableId, c.income);
    }
  },
  mounted(){

  },
  watch: {

  },
  beforeRouteLeave(to, from, next) {
    //this.$destroy();
    next();
  },
  beforeRouteEnter(to, from, next){
    next(vm=>{
		  vm.loadDatas();
	  });
  }
}
</script>
