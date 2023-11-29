<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="度量筛选" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
          <span class="el-dialog__title">度量筛选</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
        <el-form :model="val" ref="valForm" :rules="rule" label-position="left">
            <el-form-item label="度量名称" label-width="100px">
              {{kpi?kpi.kpi_name:''}}
            </el-form-item>
            <el-form-item label="操作" label-width="100px" prop="oper">
              <el-select
                v-model="val.oper"
                placeholder="请选择"
                >
                <el-option
                  v-for="item in opt.oper"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
              </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="值" label-width="100px" prop="value1">
              <el-input-number v-model="val.value1" label="描述文字" size="small"></el-input-number>
            </el-form-item>
            <el-form-item label="值2" v-show="val.oper === 'between'" label-width="100px" prop="value2">
              <el-input-number v-model="val.value2" label="描述文字" size="small"></el-input-number>
            </el-form-item>
             <el-form-item label="" label-width="100px">
                <button id="clear" type="button" class="btn btn-info btn-sm" @click="clearkey()">清除筛选条件</button>
             </el-form-item>
        </el-form>
       <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">确 定</el-button>
        <el-button @click="show = false">取 消</el-button>
      </div>
    </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl, newGuid, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
      show:false,
      val:{
        oper:null,
        value1:null,
        value2:null,
      },
      rule:{
          oper:[
            { required: true, message: '必填', trigger: 'blur' },
          ],
          value1:[
            { required: true, message: '必填', trigger: 'blur' },
          ],
      },
      kpi: null,
      opt:{
        oper:[
						{value:">", text:"大于"},
						{value:"<", text:"小于"},
						{value:"=", text:"等于"},
						{value:"between", text:"区间"}
					]
      }
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
    showKpiFilter(kpi){
      this.kpi = kpi;
      this.show = true;
      if(kpi.filter){
        this.val.oper = kpi.filter.filterType;
        this.val.value1 = kpi.filter.val1;
         this.val.value2 = kpi.filter.val2;
      }else{
        this.val.oper = null;
        this.val.value1 = null;
        this.val.value2 = null;
      }

    },
    save(){
      	this.$refs['valForm'].validate((valid) => {
          let kpi = this.kpi;
          let val = this.val;
          if(valid){
            var filter = {"kpi":kpi.kpi_id,"filterType":val.oper,"val1":val.value1,"val2": val.value2};
			      kpi.filter = filter;
            this.show = false;
            var p = this.$parent;
            p.setUpdate();
            p.chartView();
          }
        });
    },
    clearkey(){
      delete this.kpi.filter;
      var p = this.$parent;
      this.show = false;
      p.setUpdate();
      p.chartView();
    }
  },
  watch: {

  }
}
</script>
