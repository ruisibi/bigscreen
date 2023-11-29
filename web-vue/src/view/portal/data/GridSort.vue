<!--
 * Copyright 2021 本系统版权归中移在线服务有限公司
 -->
<template>
    <el-dialog title="字段排序" :visible.sync="show" width="60%" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
              <span class="el-dialog__title">字段排序</span>
              <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
              <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
              </button>
            </template>
        <el-form :model="val" ref="valForm" :rules="rule" label-position="right" size="mini">
          <el-row>
            <el-col :span="8">
              <el-form-item label="字段：" label-width="70px" >
                {{col.name}}
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="方式：" label-width="70px" prop="type" >
                <el-select v-model="val.type" size="mini" placeholder="请选择" style="width:100%;">
                  <el-option
                    v-for="item in opt.types"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value">
                  </el-option>
                </el-select>
            </el-form-item>
            </el-col>
            <el-col :span="9">
              <el-form-item label="顺序：" label-width="120px" prop="sortIndex" >
                <el-input-number v-model="val.sortIndex" :min="1" :max="10" ></el-input-number>
            </el-form-item>
            </el-col>
            <el-col :span="2">
              <div style="padding:3px; text-align:right;">
              <button type="button" class="btn btn-primary btn-xs" @click="save()">添加</button>
              </div>
            </el-col>
          </el-row>
        </el-form>
        <div style="font-size:16px;">已设置排序字段列表：</div>
        <el-table :data="sorts" border style="width: 100%" header-row-class-name="tableHeadbg">
          <el-table-column align="center" prop="sortIndex" label="序号" width="100">
            
          </el-table-column>
          
          <el-table-column align="center" prop="name" label="字段"></el-table-column>
          <el-table-column align="center" prop="sort" label="排序方式"></el-table-column>
          <el-table-column align="center" prop="col" label="操作" width="100">
            <template slot-scope="scope">
                <a class="btn btn-danger btn-xs" @click="delSort(scope.row.name)"> 删除 </a>
            </template>
          </el-table-column>
        </el-table>
       <div slot="footer" class="dialog-footer">
        <el-button @click="closeme()">关 闭</el-button>
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
        type:null,
        sortIndex:null,
      },
      sorts:[],
      rule:{
         type: [{ required: true, message: "必填", trigger: "blur" }],
         sortIndex: [{ required: true, message: "必填", trigger: "blur" }],
      },
      comp: null,
      col:{},
      opt:{
        types: [{value:"asc", text:"升序"},{value:"desc", text:"降序"}]
      }
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
    showDailog(comp, col){
      this.show = true;
      this.comp = comp;
      this.col = col;
      this.val.type = null;
      this.val.sortIndex = null;
      this.loadSorts();
    },
    /**
     * 获取所有排序字段
     */
    loadSorts(){
      let sorts = [];
      let cols = this.comp.cols;
      for(let i=0; i<cols.length; i++){
        if(cols[i].sort){
          sorts.push(cols[i]);
        }
        if(cols[i].children){
          $(cols[i].children).each((c, d)=>{
            if(d.sort){
               sorts.push(d);
            }
          });
        }
      }
      this.sorts = sorts.sort((a, b)=>{
        return a.sortIndex - b.sortIndex;
      });
    },
    save(){
      	this.$refs['valForm'].validate((valid) => {
          if(valid){
            let size = this.sorts.filter(m=>m.name === this.col.name).length;
            if(size >= 1){
              this.$notify.error("字段已经添加！");
              return;
            }
            this.col.sort = this.val.type;
            this.col.sortIndex = this.val.sortIndex;
            this.loadSorts();
          }
        });
    },
    closeme(){
      this.show = false;
      this.$parent.setUpdate();
      this.$parent.gridView();
    },
    delSort(name){
      $(this.sorts).each((a, b)=>{
        if(b.name === name){
          delete b.sort;
          delete b.sortIndex;
          return false;
        }
      });
      this.loadSorts();
    }
  },
  watch: {

  }
}
</script>
