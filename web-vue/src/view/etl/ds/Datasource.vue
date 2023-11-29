<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="wrapper-content">
    <div class="ibox" id="mainDiv">
      <div class="ibox-title">数据源管理</div>
      <div class="ibox-content">
          <div class="btn-group optbtncls" role="group">
						<button type="button" class="btn btn-outline btn-default" title="新增" @click="addDsource(false)">
              <i class="glyphicon glyphicon-plus" aria-hidden="true"></i>
            </button>
            <button type="button" class="btn btn-outline btn-default" title="修改" @click="addDsource(true)">
              <i class="glyphicon glyphicon-edit" aria-hidden="true"></i>
            </button>
            <button type="button" class="btn btn-outline btn-default" title="删除" @click="delDsource()">
              <i class="glyphicon glyphicon-trash" aria-hidden="true"></i>
            </button>
					</div>
					<el-table :data="tableData" @row-click="selectme" border style="width: 100%" header-row-class-name="tableHeadbg">
						<!--
						<el-table-column type="selection" width="45"></el-table-column>
						-->
						<el-table-column label="" width="45">
							<template slot-scope="scope">
								<el-radio v-model="checked" name="myselect2" :label="scope.row.id">&nbsp;</el-radio>
							</template>
						</el-table-column>
						<el-table-column align="center" prop="name" label="名称"></el-table-column>
						<el-table-column align="center" prop="linkType" label="数据库类型"></el-table-column>
						<el-table-column align="center" prop="ipAddress" label="地址"></el-table-column>
						<el-table-column align="center" prop="ipPort" label="端口"></el-table-column>
						<el-table-column align="center" prop="database" label="数据库"></el-table-column>
            			<el-table-column align="center" prop="uname" label="用户名"></el-table-column>
					</el-table>
      </div>
    </div>
	<dsourceAdd ref="dsourceAddForm"></dsourceAdd>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import operationDailog from '@/components/OperationDailog'
import dsourceAdd from '@/view/etl/ds/DsourceAdd'
import $ from "jquery";

export default {
  data() {
    return {
	  tableData:[],
	  checked:null
    }
  },
  components: {
	  dsourceAdd
	},
  mounted() {

  },
  computed: {},
  methods: {
	   loadDatas:function(){
			let ts = this;
			ajax({
				type:"GET",
				data:{},
				postJSON:true,
				url:"etl/listDataSource.action",
				success:function(resp){
					ts.tableData = resp.rows;
				}
			}, ts);
		},
		selectme:function(a, b){
			this.checked = a.id;
		},
		addDsource(isupdate){
			this.$refs['dsourceAddForm'].addDsource(isupdate, this.checked);
		},
		delDsource(){
			if (!this.checked) {
				this.$notify.error("未勾选数据");
				return;
			}
			if(confirm("是否确认删除？")){
				ajax({
					url:"etl/deleteDataSource.action",
					data:{id:this.checked},
					type:"GET",
					success:(resp)=>{
						this.loadDatas();
					}
				}, this);
			}
		}

  },
  watch: {},
  beforeRouteLeave(to, from, next) {
    next();
  },
  beforeRouteEnter(to, from, next){
    next(vm=>{
     vm.loadDatas();
    });
  },
};
</script>
