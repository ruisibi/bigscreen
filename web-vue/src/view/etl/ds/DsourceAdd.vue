<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <el-dialog :title="dialogTitle" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
       <template slot="title">
          <span class="el-dialog__title">{{dialogTitle}}</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
            <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
        <el-form :model="dsource" :rules="rules" ref="dsourceForm" size="mini">
          <el-form-item label="数据源名称" label-width="100px" prop="name">
            <el-input v-model="dsource.name"></el-input>
          </el-form-item>
          <el-form-item label="数据源类型" label-width="100px" prop="linkType">
            <el-select style="width:100%"
              v-model="dsource.linkType"
              placeholder="请选择"
              @change="chglinkType"
            >
              <el-option
                v-for="item in opts.linkTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="IP地址" label-width="100px" prop="ipAddress">
            <label slot="label">{{dblabel}}</label>
            <el-input v-model="dsource.ipAddress" ></el-input>
          </el-form-item>
          <template v-if="dsource.linkType!=='sqlite'">
            <el-form-item label="端口号" label-width="100px" prop="ipPort">
              <el-input v-model.number="dsource.ipPort"></el-input>
            </el-form-item>
            <el-form-item label="数据库名称" label-width="100px" prop="database">
              <el-input v-model="dsource.database"></el-input>
            </el-form-item>
          </template>
            <el-form-item label="用户名" label-width="100px" prop="uname">
              <el-input v-model="dsource.uname"></el-input>
            </el-form-item>
            <el-form-item label="密码" label-width="100px" prop="psd">
              <el-input type="password" v-model="dsource.psd"></el-input>
            </el-form-item>
           <template v-if="dsource.linkType!=='sqlite'">
            <el-form-item label="额外参数" label-width="100px" prop="extParams">
              <el-input v-model="dsource.extParams"></el-input>
              <div class="text-warning">
              (参数会追加到连接URL后)
              </div>
            </el-form-item>
          </template>
        </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button type="warning" @click="testDsource()">测试连接</el-button>
      <el-button type="primary" @click="saveDsource()">确 定</el-button>
      <el-button @click="show = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import { baseUrl, ajax } from "@/common/biConfig";
import $ from "jquery";
import { Loading } from "element-ui";

export default {
  data() {
    return {
      dialogTitle: "",
      show: false,
      ischeckds:false,
      isupdate:null,
      dblabel:"IP地址",
      dsource: {
        name:null,
        linkType:null,
        ipAddress:null,
        ipPort:null,
        database:null,
        uname:null,
        psd:null,
        extParams:null
      },
      rules: {
        name: [{ required: true, message: "必填", trigger: "blur" }],
        linkType: [{ required: true, message: "必填", trigger: "blur" }],
        ipAddress: [{ required: true, message: "必填", trigger: "blur" }],
        ipPort: [{ required: true, message: "必填", trigger: "blur" },
         { type: 'number', message: '必须是数字', trigger: "blur"}],
        database: [{ required: true, message: "必填", trigger: "blur" }],
      },
      opts: {
        linkTypes: [
          { label: "Mysql", value: "mysql" },
          { label: "Oracle", value: "oracle" },
          { label: "SQL Server", value: "sqlser" },
          { label: "DB2", value: "db2" },
          { label: "PostgreSQL", value: "psql" },
          { label: "Hive", value: "hive" },
          //{ label: "Kylin", value: "kylin" },
          { label: "Sqlite", value: "sqlite" },
          {label:"达梦", value:"dm"},
          {label:"ClickHouse", value:"clickhouse"}
        ],
      },
    };
  },
  components: {},
  mounted() {
    if (this.isupdate) {
      this.dialogTitle = "修改数据源";
    } else {
      this.dialogTitle = "创建数据源";
    }
  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
    saveDsource: function () {
      let ts = this;
      this.$refs["dsourceForm"].validate(
        (valid) => {
          if (valid && this.ischeckds === true) {
            ajax(
              {
                type: "POST",
                url: ts.isupdate
                  ? "etl/updateDataSource.action"
                  : "etl/saveDataSource.action",
                data: ts.dsource,
                success: (resp) => {
                  ts.$notify.success({
                    title: ts.isupdate ? "数据源修改成功" : "数据源创建成功",
                    offset: 50,
                  });
                  //关闭窗口
                  ts.show = false;
                  //刷新数据
                  ts.$parent.loadDatas();
                },
              },
              ts
            );
          }else if(this.ischeckds === false){
            this.$notify.warning("请先点击测试连接，连接成功后再点击确定按钮");
          }
        }
      );
    },
    chglinkType: function (val) {
      if(val === 'sqlite'){
        this.dblabel = "文件路径";
      }else{
        this.dblabel = "IP地址";
      }
      let linktype = this.dsource.linkType;
      if(this.isupdate){
        return;
      }
      if(linktype == "mysql"){
        this.dsource.ipPort = (3306);
      }else if(linktype == "oracle"){
        this.dsource.ipPort = (1521);
      }else if(linktype == "sqlser"){
        this.dsource.ipPort = (1433);
      }else if(linktype == "db2"){
        this.dsource.ipPort = (50000);
      }else if(linktype == "hive"){
        this.dsource.ipPort = (10000);
      }else if(linktype == "psql"){
        this.dsource.ipPort = (5432);
      }else if(linktype == "kylin"){
        this.dsource.ipPort = (7070);
      }else if(linktype === 'sqlite'){
        this.dsource.ipPort = null;
      }else if(linktype ==='dm'){
        this.dsource.ipPort = (5236);
      }else if(linktype === 'clickhouse'){
        this.dsource.ipPort = (8123);
      }
    },
    addDsource(update, dsId) {
      this.ischeckds = false;
      this.isupdate = update;
      if (update) {
        if (!dsId || dsId === "") {
          this.$notify.error("未勾选数据");
          return;
        }
        this.show = true;
        //回写数据
        ajax(
          {
            type: "GET",
            url: "etl/getDataSource.action",
            data: { id: dsId },
            success: (resp) => {
              let o = resp.rows;
              this.dsource = o;

            },
          },
          this
        );
      } else {
        this.show = true;
        if (this.$refs["dsourceForm"]) {
          this.$refs["dsourceForm"].resetFields();
        }
      }
    },
    testDsource() {
      this.$refs["dsourceForm"].validate(
        (valid) => {
          if (valid) {
            let load = Loading.service({ fullscreen: true });
            ajax(
              {
                type: "POST",
                url:"etl/testDataSource.action",
                data:  this.dsource,
                success: (resp) => {
                  this.$notify.success({
                    title: "测试成功",
                    offset: 50,
                  });
                  this.ischeckds = true;
                },
              },
              this,
              load
            );
          }
        }
      );
    },
  },
};
</script>
