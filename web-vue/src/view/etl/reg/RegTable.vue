<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <operationDailog title="注册Restful接口" mainDiv="maindiv" :callback="saveRest" ref="restTablePanel">
    <el-form :model="rest" :rules="rules" ref="restForm" size="mini">
      <el-form-item label="接口名称：" label-width="110px" prop="restName">
         <el-input v-model="rest.restName"></el-input>
      </el-form-item>
      <el-form-item label="接口地址：" label-width="110px" prop=restUrl>
         <el-input v-model="rest.restUrl"></el-input>
      </el-form-item>
      <el-form-item label="请求方式：" label-width="110px" prop="postType">
        <el-select v-model="rest.postType" placeholder="请选择" style="width:100%">
          <el-option
          v-for="item in opts.types"
          :key="item.value"
          :label="item.label"
          :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="数据字段：" label-width="110px" prop="dataKey">
         <el-input v-model="rest.dataKey"></el-input>
      </el-form-item>
       <el-form-item label="总记录数字段：" label-width="110px">
         <el-input v-model="rest.totalKey" placeholder="在分页查询中，通过此字段获取分页的总记录数"></el-input>
      </el-form-item>
      <button class="btn btn-primary" type="button" @click="addParam()">添加参数</button>
      <template v-for="(p,index) in rest.restParam">
        <el-row :gutter="20" :key="p.id">
          <el-col :span="6">
         <el-form-item label="名称：" label-width="80px" :prop="'restParam.' + index + '.paramName'" :rules="{
            required: true, message: '参数名称不能为空', trigger: 'blur'
          }">
            <el-input v-model="p.paramName"></el-input>
          </el-form-item>
          </el-col>

            <!--
           <el-col :span="6">
          <el-form-item label="说明：" label-width="80px" :prop="p.paramNote">
            <el-input v-model="p.paramNote"></el-input>
          </el-form-item>
           </el-col>
          -->

          <el-col :span="5">
          <el-form-item label="类型：" label-width="80px">
            <el-select v-model="p.paramType" placeholder="请选择" style="width:90%">
                <el-option
                v-for="item in opts.colTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
              </el-select>
          </el-form-item>
          </el-col>
          <el-col :span="6">
          <el-form-item label="默认值：" label-width="80px" :prop="p.paramDefvalue">
            <el-input v-model="p.paramDefvalue"></el-input>
          </el-form-item>
          </el-col>
          <el-col :span="3">
          <el-form-item label="" label-width="10px" >
            <el-checkbox v-model="p.paramisPageSize">PageSize</el-checkbox>
          </el-form-item>
          </el-col>
           <el-col :span="3">
          <el-form-item label="" label-width="10px" >
            <el-checkbox v-model="p.paramisCurPage">当前页</el-checkbox>
          </el-form-item>
          </el-col>
          <el-col :span="1">
            <button class="btn btn-link" @click="delparam(p.paramName)" type="button"><i class="glyphicon glyphicon-trash"></i></button>
          </el-col>
        </el-row>
      </template>
    </el-form>
     <tableInfoView ref="tableInfoForm"></tableInfoView>
  </operationDailog>
</template>

<script>
import { baseUrl, ajax, newGuid } from "@/common/biConfig";
import operationDailog from '@/components/OperationDailog'
import tableInfoView from '@/view/etl/table/TableInfoView'
import { Loading } from "element-ui";
import $ from "jquery";

export default {
  data() {
    return {
      isupdate: false,
      rest:{
        restName:null,
        restUrl:null,
        postType:'post',
        dataKey:null,
        totalKey:null,
        restParam:[]
      },
      rules:{
        restName:[{ required: true, message: '必填', trigger: 'blur' }],
        restUrl:[{ required: true, message: '必填', trigger: 'blur' }],
         postType:[{ required: true, message: '必填', trigger: 'blur' }],
        dataKey:[{ required: true, message: '必填', trigger: 'blur' }]
      },
      opts:{
        types:[{value:"post",label:"POST"},{value:"get",label:"GET"}],
        colTypes:[
          {value:"String",label:"String"},{value:"Number",label:"Number"},{value:"Date",label:"Date"}
        ]
      }
    }
  },
  components: {
    operationDailog, tableInfoView
	},
  mounted() {

  },
  computed: {},
  methods: {
    newRestTable(isupdate){
      this.isupdate = isupdate;
      var o = this.$refs['restTablePanel'];
      o.showDailog();
      var f = this.$refs['restForm'];
      if(f){
        f.clearValidate();
      }
      this.clearData();
    },
    updateRestTable(isupdate, id){
      this.isupdate = isupdate;
      if(!id){
        this.$notify.error("未勾选数据。");
        return;
      }
      ajax({
        type:"get",
        url:"etl/regRest/get.action",
        data:{tableId: id},
        success:(r)=>{
          r = r.rows;
          this.tableId = r.tableId;
          this.newRestTable(isupdate);
          var o = this.backRestData(r);
        }
      }, this);
    },
    saveRestInfo(rest){
      var isupdate = this.isupdate;
      var json = {
            tableNote: rest.restName,
            restUrl: rest.restUrl,
            //restUrlBak: null,
            restPostType: rest.postType,
            restTotalKey: rest.totalKey,
            restDataKey: rest.dataKey,
            //restAuthType: rest.restAuthType,
            restParam: []
      };
      $(rest.restParam).each((a, b)=>{
        json.restParam.push({
          paramName:b.paramName, paramNote:b.paramNote, paramDefvalue:b.paramDefvalue, paramType:b.paramType,
          paramisPageSize:b.paramisPageSize, paramisCurPage:b.paramisCurPage
        });
      });
      if (isupdate) {
        json.tableId = this.tableId;
      }
      let load = Loading.service({ fullscreen: true });
      ajax({
        type: "post",
        url: !isupdate ? "etl/regRestTable.action" : "etl/updateRestTable.action",
        dataType: "json",
        postJSON:true,
        data: JSON.stringify(json),
        success: (resp) => {
          var o = this.$refs['restTablePanel'];
          o.closeDailog();
          this.$notify.success("接口注册成功！");
          this.$parent.loadDatas();
        }
      }, this, load);
      return false;
    },
    tableview(table, tp){
       this.$refs['tableInfoForm'].showInfo(table.tableId, tp);
    },
	  saveRest(){
      let ret = false;
      this.$refs['restForm'].validate(valid=>{
        if(valid){
          ret = this.saveRestInfo(this.rest);
        }
      });
      return ret;
    },
    clearData(){
       var o = this.rest;
      o.restName = null;
      o.restUrl = null;
      o.postType = "post";
      o.dataKey = null;
      o.totalKey = null;
      o.restParam = [];
    },
    backRestData(json){
      var o = this.rest;
      o.restName = json.tableNote;
      o.restUrl = json.restUrl;
      o.postType = json.restPostType;
      o.dataKey = json.restDataKey;
      o.totalKey = json.restTotalKey;
      var pms = [];
      $(json.restParam).each((a, b)=>{
        pms.push({
          paramName:b.paramName, paramNote:b.paramNote, paramDefvalue:b.paramDefvalue, paramType:b.paramType, paramisPageSize:b.paramisPageSize, paramisCurPage:b.paramisCurPage
        });
      });
      o.restParam = pms;
    },
    addParam(){
      this.rest.restParam.push({
        id:newGuid(),
        paramName:null,
        paramNote:null,
        paramType:"String",
        paramDefvalue:null,
        paramisPageSize:false,
        paramisCurPage:false,
      });
    },
    delparam(paramName){
      $(this.rest.restParam).each((a, b)=>{
        if(b.paramName === paramName){
          this.rest.restParam.splice(a, 1);
          return false;
        }
      });
    }
  },
  watch: {},
};
</script>
