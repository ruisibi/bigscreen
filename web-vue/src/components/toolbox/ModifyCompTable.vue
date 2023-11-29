<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<!--
修改组件的映射表
 -->
<template>
    <el-dialog title="修改数据集" :visible.sync="show" :close-on-click-modal="false" :append-to-body="true" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">修改数据集</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
      </template>
       <p class="text-warning">请注意：如果修改后的表和原表字段不一致，系统会出错。</p>
       <el-select v-model="tableId" size="mini" style="width:100%;" filterable placeholder="选择需要更改的表">
         <el-option
            v-for="item in data"
            :key="item.tableId"
            :label="item.tableName"
            :value="item.tableId">
            </el-option>
        </el-select>
        <div slot="footer" class="dialog-footer">
             <el-button type="primary" @click="save()">确 定</el-button>
            <el-button @click="show = false">关 闭</el-button>
        </div>
    </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'

export default {
  props:{

  },
  data () {
    return {
        show:false,
        data:null,
        tableId:null, //选中的表
        cb:null, //回调函数
    }
  },
  computed:{
      ...mapState(["isMini"])
  },
  methods:{
   showDialog(cb){
       this.cb = cb;
       this.show = true;
       this.loadTables();
       this.tableId = null;
   },
   save(){
       this.show = false;
       if(this.cb){
           let tb = this.data.filter(m=>m.tableId === this.tableId)[0];
           this.cb(tb);
       }
   },
   loadTables(){
        ajax({
            url:"etl/listTables.action",
            data:{},
            type:"get",
            success:(resp)=>{
                this.data = resp.rows;
            }
        });
    },

  }
}
</script>
