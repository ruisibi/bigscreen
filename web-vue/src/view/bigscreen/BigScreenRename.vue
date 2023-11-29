<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="大屏改名" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
            <span class="el-dialog__title">大屏改名</span>
            <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
            <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
            </button>
          </template>
        <el-form :model="table" ref="tableForm" :rules="rule" label-position="left" size="mini">
          <el-form-item label="大屏名称：" label-width="120px" prop="name">
            <el-input v-model="table.name"></el-input>
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
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
        show:false,
        table:{
          name:null,
        },
        opt:{
        },
        report:null,  //报表对象
        rule:{
          name: [{ required: true, message: "必填", trigger: "blur" }],
        }
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
     showDailog(report){
       this.report = report;
       this.show = true;
       this.table.name = report.pageName;
     },
     save(){
        this.$refs["tableForm"].validate(valid=>{
          if(valid){
            ajax({
              type: "POST",
              url: "bigscreen/rename.action",
              dataType:"json",
              data: {id:this.report.id, pageName:this.table.name},
              success: (resp) => {
                //更新数据
                this.$parent.loadDatas();
              }
            });
            this.show = false;
          }
        });
     }
  }
}
</script>
