<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="度量属性" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
          <span class="el-dialog__title">度量属性</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
        <el-form :model="val" ref="valForm" :rules="rule" label-position="left" size="mini">
           <el-form-item label="所属字段：" label-width="120px">
                {{val.col_name}}
            </el-form-item>
            <el-form-item label="显示名称：" label-width="120px">
                <el-input v-model="val.name"></el-input>
            </el-form-item>
            <el-form-item label="显示位置：" label-width="120px">
                <el-radio v-model="val.kpiPostion" label="left" border>y轴</el-radio>
                <el-radio v-model="val.kpiPostion" label="right" border>y2轴</el-radio>
            </el-form-item>
            <el-form-item label="显示方式：" label-width="120px">
                <el-radio v-model="val.kpiShape" label="line" border>曲线</el-radio>
                <el-radio v-model="val.kpiShape" label="column" border>柱子</el-radio>
            </el-form-item>
              <el-form-item label="隐藏曲线：" label-width="120px">
                <el-switch v-model="val.hideLine" ></el-switch> <span class="text-warning">只对曲线有效</span>
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

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
      show:false,
      val:{
        name:null,
        col_name:null,
        kpiPostion:"left",
        kpiShape:"line",
        hideLine:false,
      },
      rule:{
          oper:[
            { required: true, message: '必填', trigger: 'blur' },
          ],
      },
      kpi: null,
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
    kpiProp(kpi){
      this.show = true;
      this.kpi = kpi;
      //回写值
      this.val.name = kpi.kpi_name;
      this.val.kpiPostion = kpi.kpiPostion || "left";
      this.val.kpiShape = kpi.kpiShape || "line";
      this.val.hideLine = kpi.hideLine || false;
      this.val.col_name = kpi.col_name;
    },
    save(){
      let kpi = this.kpi;
      kpi.kpiPostion = this.val.kpiPostion;
      kpi.kpiShape = this.val.kpiShape;
      kpi.hideLine = this.val.hideLine;
      kpi.kpi_name = this.val.name;
      var p = this.$parent;
      p.setUpdate();
      p.chartView();
      this.show = false;
    },
  },
  watch: {

  }
}
</script>
