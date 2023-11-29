<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="title" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
            <span class="el-dialog__title">{{title}}</span>
            <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
            <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
            </button>
          </template>
        <div class="el-dialog-div">
          <div  v-if="type=='bs'">
          <ul class="todo-list small-list ui-sortable">
            <template v-for="item in files">
              <li :key="item.id">
                <a class="openreport" href="javascript:;" @click.stop="selectfxt(item)">{{item.pageName}}</a>
              </li>
            </template>
          </ul>
          </div>
          <div v-if="type=='comp'">
            <button class="btn btn-xs btn-outline btn-info" @click="backpage()"><i class="fa fa-arrow-left"></i>返回</button>
          <ul class="todo-list small-list ui-sortable">
            <template v-for="item in comps">
              <li :key="item.id">
                <a class="openreport" href="javascript:;" @click.stop="selectComp(item)">{{item.title}}</a>
              </li>
            </template>
          </ul>
          </div>
        </div>
      <div slot="footer" class="dialog-footer">
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
       files:[],
       comps:[],
       type:"bs",
       title:"",
       curSelectComp : null,
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
     showDailog(curComp){
       this.show = true;
       this.type = "bs";
       this.title = "选择已有大屏";
       this.curSelectComp = curComp;
       this.loadBss();
     },
     loadBss(){
       ajax({
         url:"bigscreen/listAll.action",
         type:"POST",
         data:{},
         success:(resp)=>{
           this.files = resp.rows;
         }
       }, this);
     },
     selectfxt(item){
       ajax({
         url:"bigscreen/getJson.action",
         data:{id: item.id},
         type:"GET",
         success:(resp)=>{
           let json = JSON.parse(resp.rows);
           this.type = "comp";
           this.comps = json.comps;
           this.title= "选择已有组件";
         }
       }, this);
     },
     selectComp(comp){
       comp.left = this.curSelectComp.left;
       comp.top = this.curSelectComp.top;
       this.$parent.coloneComp(comp);
       this.show = false;
     },
     backpage(){
       this.type = "bs";
       this.title = "选择已有大屏";
     }
  }
}
</script>
<style lang="less" scoped>
.todo-list {
    list-style: none outside none;
    margin: 0;
    padding: 0;
    font-size: 14px;
}
.todo-list.small-list > li {
    border-left: none;
    border-right: none;
    border-radius: 4px;
    color: inherit;
    margin-bottom: 2px;
    padding: 6px 6px 6px 12px;
}

.todo-list.small-list > li:hover {
	background-color:#f0f3f4;
}
</style>
