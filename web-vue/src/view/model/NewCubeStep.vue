<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="wrapper-content">
    <div class="ibox" id="maindiv">
      <div class="ibox-title">创建新的数据模型</div>
      <div class="ibox-content">
      <el-steps :active="active" simple finish-status="success">
        <el-step icon="el-icon-edit" title="1.选表">
        </el-step>
        <el-step icon="el-icon-edit" title="2.表字段">
        </el-step>
        <el-step icon="el-icon-edit" title="3.立方体">
        </el-step>
		 </el-steps>
		 <template v-if="active == 0">
		 	<selectTable :pageJson="pageJson" ref="selectTableForm"></selectTable>
		 </template>
		  <template v-if="active == 1">
		 	<tableCols :pageJson="pageJson" ref="tableColsForm"></tableCols>
		 </template>
		  <template v-if="active == 2">
		 	<cube :pageJson="pageJson" ref="cubeForm"></cube>
		 </template>
      </div>
    </div>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import selectTable from '@/view/model/SelectTable'
import tableCols from '@/view/model/TableCols'
import cube from '@/view/model/Cube'
import $ from "jquery";

export default {
  data() {
    return {
      active: 0,
      pageJson:{

      }
    }
  },
  components: {
	  selectTable, tableCols, cube
  },
  mounted() {

  },
  computed: {},
  methods: {
    gotoSubject(){
			this.$router.push({path:"/model/SubjectManager", query:{}});
			//添加菜单
			this.$parent.$refs['navMenuForm'].menuAdd({menuId:83, menuName:"已建模管理", menuUrl:"/model/SubjectManager"});
    }
  },
  watch: {},
  beforeRouteLeave(to, from, next) {
    this.$destroy();
    next();
  },
  beforeRouteEnter(to, from, next) {
			next(vm=>{
				let tid = vm.$route.params.tid;
        if(tid){ //tid存在，说明是从已有数据过来的链接
          vm.$refs['selectTableForm'].model.tid = tid;
				}
			});
		}
};
</script>
