<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="计算 - 比较指定日期" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">计算 - 比较指定日期</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="compareForm" :model="prop" size="mini" :rules="rules">
         <el-form-item label="比较值：" label-width="100px" prop="valtype">
            <el-radio-group v-model="prop.valtype">
              <el-radio label="gdz" border>固定值</el-radio>
              <el-radio label="param" border>链接到参数</el-radio>
            </el-radio-group>
         </el-form-item>
         <el-form-item label="值：" label-width="100px" prop="val" v-if="prop.valtype === 'gdz'">
           <el-input  placeholder="请输入符合当前日期格式的值"  v-model="prop.val" clearable> </el-input>
         </el-form-item>
          <el-form-item label="参数：" label-width="100px" prop="param" v-if="prop.valtype === 'param'">
           <el-select  placeholder="请选择需要关联的参数"  v-model="prop.param" clearable style="width:100%">
             <el-option
                v-for="item in params"
                :key="item.paramid"
                :label="item.name"
                :value="item.paramid">
              </el-option>
           </el-select>
         </el-form-item>
          <el-form-item label="显示名称：" label-width="100px" prop="name">
           <el-input  placeholder="字段显示名称"  v-model="prop.name" clearable> </el-input>
         </el-form-item>
         <el-form-item label="回调函数：" label-width="100px" prop="code" v-if="type=='table'">
            <div style="font-weight:bold;">function(value, col, row, data, outType, rowIndex, colIndex){</div>
           <el-input  placeholder="通过 return 返回回调内容" rows="5" type="textarea" v-model="prop.code" clearable> </el-input>
            <div style="font-weight:bold;"> } </div>
         </el-form-item>
         <el-form-item label="" label-width="90px">
          <button type="button" class="btn btn-xs btn-outline btn-danger" @click="clean">清除日期比较</button>
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
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'

export default {
  components:{

  },
  props:{
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
      //使用类型， chart/table
      type:{
        type:String,
        required: true,
      }
  },
  data(){
    return {
      show:false,
      prop:{
        valtype:null,
        val:null,
        param:null,
        name:null,
        code:null,
      },
      comp:null,
      kpi: null, //当前指标
      pageInfo : null,
      params:[],
      rules: {
        valtype: [{ required: true, message: "必填", trigger: "blur" }],
        val: [{ required: true, message: "必填", trigger: "blur" }],
        param: [{ required: true, message: "必填", trigger: "blur" }],
        name: [{ required: true, message: "必填", trigger: "blur" }],
      },
      cb:null, //回掉函数
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    openDailog(kpi, pageInfo, cb){
      this.show = true;
      this.kpi = kpi;
      this.pageInfo = pageInfo;
      if(this.useIn == 'dashboard'){
        if(pageInfo.globalParams){
          this.params = pageInfo.globalParams.map(m=>{
            return {paramid:m.id, name:m.name, type: m.type}
          });
        }
      }else{  //报表中使用
        this.params = pageInfo.params;
      }
      //清空值
      for(let m in this.prop){
        this.prop[m] = null;
      }
      //回写值
      if(kpi.compareDate){
        this.prop.valtype = kpi.compareDate.valtype;
        this.prop.val = kpi.compareDate.val;
        this.prop.param = kpi.compareDate.param;
        this.prop.name = kpi.compareDate.name;
         if(kpi.compareDate.code){
          this.prop.code = unescape(kpi.compareDate.code);
        }
      }
      this.cb = cb;
    },
    save(){
      this.$refs["compareForm"].validate(
        (valid) => {
          if(valid){
            var kpi = this.kpi;
            let type = null;
            if(this.prop.param){
              let p = this.params.filter(m=>m.paramid == this.prop.param)[0];
              type = p.type;
            }
            kpi.compareDate = {
              valtype: this.prop.valtype,
              val: this.prop.val,
              param: this.prop.param,
              paramType: type,
              name: this.prop.name,
              code: this.prop.code?escape(this.prop.code):null,
            };
            this.show = false;
            if(this.cb){
              this.cb();
            }
          }
        }
      )
    },
    clean(){
       var kpi = this.kpi
        delete kpi.compareDate ;
        this.show = false;
        if(this.cb){
          this.cb();
        }
    }

  }
}
</script>
