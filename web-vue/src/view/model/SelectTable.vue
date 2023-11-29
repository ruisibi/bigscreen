<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div style="margin-top:30px;">
      <el-form :model="model" :rules="rules" ref="modelForm" size="mini">
            <el-form-item label="名称" label-width="150px" prop="cubename">
              <el-input v-model="model.cubename" ></el-input>
            </el-form-item>
          <el-form-item label="所属分类" label-width="150px" prop="ftype">
            <el-row>
              <el-col :span="20">
                <el-select v-model="model.ftype" placeholder="请选择" style="width:100%">
                  <el-option v-for="item in opt.types"
                    :key="item.dsId"
                    :label="item.name"
                    :value="item.dsId">
                  </el-option>
              </el-select>
              </el-col>
              <el-col :span="4" align="right">
                  <button type="button" class="btn btn-sm btn-primary" @click="newSubjectType()">管理分类</button>
              </el-col>
            </el-row>

            </el-form-item>
          <el-form-item label="备注" label-width="150px">
              <el-input v-model="model.cubenote" ></el-input>
          </el-form-item>
          <el-form-item label="对应数据表" label-width="150px" prop="tid">
              <el-select v-model="model.tid" @change="showTableData()" filterable style="width:100%" placeholder="请选择">
                <el-option v-for="item in opt.tables"
                  :key="item.tableId"
                  :label="item.tableName + '(' + item.tableNote + ')'"
                  :value="item.tableId">
                </el-option>
              </el-select>
          </el-form-item>
          <transition name = "fade">
            <div v-if="model.tableType === 'vertical'">
            <el-form-item label="指标编码字段" label-width="150px" prop="kpiCodeColumn">
                <el-select v-model="model.kpiCodeColumn" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in opt.cols"
                    :key="item.colId"
                    :label="item.colName"
                    :value="item.colName">
                  </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="指标名称字段" label-width="150px" prop="kpiNameColumn">
                <el-select v-model="model.kpiNameColumn" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in opt.cols"
                    :key="item.colId"
                    :label="item.colName"
                    :value="item.colName">
                  </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="指标值字段" label-width="150px" prop="kpiValueColumn">
                <el-select v-model="model.kpiValueColumn" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in opt.cols"
                    :key="item.colId"
                    :label="item.colName"
                    :value="item.colName">
                  </el-option>
                </el-select>
            </el-form-item>
            </div>
          </transition>
        </el-form>

      <div style="margin-bottom:10px;margin-left:150px;">
        <!--
        <el-button  @click="before"><i class="fa fa-arrow-left"></i> 上一步</el-button>
        -->
        <el-button @click="save()">下一步 <i class="fa fa-arrow-right"></i></el-button>
     </div>

        <tableDataView v-if="model.tid" :showBtn="false" ref="tableViewForm" :tableId="model.tid==null?0:model.tid"></tableDataView>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import $ from "jquery";
import tableDataView from '@/view/etl/table/TableDataView'

export default {
  data() {
    return {
      model:{
        cubename:null,
        ftype:null,
        cubenote:null,
        tid:null,
        tableType: "horizontal",
        kpiCodeColumn: null,
        kpiNameColumn: null,
        kpiValueColumn: null,
      },

      rules:{
        cubename:[{ required: true, message: "必填", trigger: "blur" }],
        ftype:[{ required: true, message: "必填", trigger: "blur" }],
        tid:[{ required: true, message: "必填", trigger: "blur" }],
      },
      opt:{
        types:[],
        tables:[],
        cols: []
      }
    }
  },
  props:{
    pageJson: {
      type: Object,
      required: true,
    },
  },
  components: {
    tableDataView
	},
  mounted() {
    this.backVal();
    this.loadData();
  },
  computed: {},
  methods: {
    backVal(){
      var json = this.pageJson;
      this.model.cubename = json.tDesc;
      this.model.cubenote = json.tNote;
      this.model.tid = json.tid;
      this.model.ftype = json.typeId;
      this.model.tableType = json.tableType;
      this.model.kpiCodeColumn = json.kpiCodeColumn;
      this.model.kpiNameColumn = json.kpiNameColumn;
      this.model.kpiValueColumn = json.kpiValueColumn;
      this.loadCols(json.tid);
    },
    loadData(){
      ajax({
        url:"model/newCubeStep1.action",
        type:"GET",
        data:{},
        success:(r)=>{
          this.opt.types = r.rows.types;
          this.opt.tables = r.rows.tables;
        }
      }, this);
    },
    chgtables(){
      this.model.tid = null;
      this.loadData();
    },
    newSubjectType(){
      this.$router.push({path:"/model/SubjectType", query:{}});
			//添加菜单
			 this.$parent.$parent.$refs['navMenuForm'].menuAdd({menuId:81, menuName:"分类管理", menuUrl:"/model/SubjectType"});
    },
    save(){
      this.$refs["modelForm"].validate( (valid) => {
        if(valid){
          ajax({
            url:"model/tableExist.action",
            data:{tableId:this.model.tid},
            success:(r)=>{
              if(r.rows > 0){
                this.$notify.error("您选择的数据表已经建模！");
                return;
              }
              var json = this.pageJson;
              json.tDesc = this.model.cubename;
              json.tNote = this.model.cubenote;
              json.tid = this.model.tid;
              json.tName = this.opt.tables.filter(m=>m.tableId===json.tid)[0].tableName;
              json.typeId = this.model.ftype;
              json.tableType = this.model.tableType;
              json.kpiCodeColumn = this.model.kpiCodeColumn;
              json.kpiNameColumn = this.model.kpiNameColumn;
              json.kpiValueColumn = this.model.kpiValueColumn;
              this.$parent.active = 1;
            }
          }, this);
        }
      });
    },
    showTableData(){
      var tid = this.model.tid;
      this.loadCols(tid);
    },
    loadCols(tid){
      //获取字段
      ajax({
        url:"etl/getTableColumns.action",
        type:"GET",
        data:{tableId:tid},
        success:(resp)=>{
          this.opt.cols = resp.rows;
        }
      }, this);
    }
  },
  watch: {}
};
</script>
<style lang="less" scoped>
.fade-enter-active, .fade-leave-active {
    transition: opacity 0.2s
}
.fade-enter, .fade-leave-to /* .fade-leave-active, 2.1.8 版本以下 */ {
    opacity: 0
}
</style>
