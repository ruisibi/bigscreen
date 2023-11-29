<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <div class="tableDataView">
      <!--
      <button type="button" class="btn btn-xs btn-outline btn-default" @click="flash()" v-if="showBtn">
        <i class="fa fa-refresh"></i>
      </button>
      -->
        <el-table :data="tableData" style="width: 100%" :height="calcHeight()" border header-row-class-name="tableHeadbg">
          <template v-for="(item) in headData">
            <el-table-column :key="item" :prop="item" :min-width="120"  :label="item">
            </el-table-column>
          </template>
        </el-table>
    </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import $ from "jquery"
import { Loading } from "element-ui";

export default {
   name: 'tableDataView',
  data() {
    return {
      show:false,
      tableData:[],
      headData:[],
      config:{},
    }
  },
  props: {
    tableId:{
      type:Number,
      required:true
    },
    showBtn:{
      type:Boolean,
      required:false,
      default:true
    }
  },
  components: {

	},
  mounted() {
   this.view();
  },
  computed: {},
  methods: {
    calcHeight(){
       var h = $(".wrapper-content").height();
      return h - 160;
    },
	 view(){
     if(this.tableId == 0){
       return;
     }
     this.show = true;
     this.headData = [];
     this.tableData = [];
     let loadingInstance = Loading.service({fullscreen:false,  target:document.querySelector(".tableDataView")}); //text:"加载中...", spinner:"el-icon-loading",
     ajax({
       type:"POST",
       url:"etl/queryTableData.action",
       data:{tableId:this.tableId},
       success:(resp)=>{
         loadingInstance.close();
         this.headData = resp.rows[0];
         resp.rows.splice(0, 1);
         this.tableData = resp.rows;
       }
     }, this, loadingInstance);

   },
   flash(){
     this.view();
   }
  },
  watch:{
    tableId:function(val){
      this.view();
    }
  }
};
</script>

<style lang="less" scoped>

</style>
