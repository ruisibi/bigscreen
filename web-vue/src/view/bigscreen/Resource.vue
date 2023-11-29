<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="wrapper-content">
    <div class="ibox">
      <div class="ibox-title">资源管理</div>
      <div class="ibox-content">
          <div class="text-warning">管理在大屏中使用的图片资源文件</div>
		  <div style="margin-bottom:10px;" class="btn-group" role="group">
				<button type="button" class="btn btn-outline btn-default" title="新建" @click="newResource()">
					<i class="glyphicon glyphicon-plus" aria-hidden="true"></i>
				</button>
				<button type="button" class="btn btn-outline btn-default" title="导出" @click="expResource()">
					<i class="fa fa-mail-forward" aria-hidden="true"></i>
				</button>
				<button type="button" class="btn btn-outline btn-default" title="删除" @click="delResource()">
					<i class="glyphicon glyphicon-trash" aria-hidden="true"></i>
				</button>
			</div>
			<div class="row">
				<div class="col-sm-12">
					<el-checkbox-group v-model="selects">
					<template v-for="item in datas">
					<div class="file-box"  :key="item.id">
						<div class="file">
							<div class="image">
								<a href="javascript:;" @click="showpic(item)"><img class="img-responsive" :src="getUrl(item)"></a>
							</div>
							<div class="file-name">
								<div class="name"><el-checkbox :label="item.id">{{item.name}}</el-checkbox></div>

							</div>
						</div>
					</div>
					</template>
					</el-checkbox-group>
				</div>
			</div>
				<el-pagination
					background
					@size-change="handleSizeChange"
					@current-change="handleCurrentChange"
					:page-sizes="[10, 20, 50, 100]"
					:current-page="page"
					:page-size="rows"
					layout="total, sizes, prev, pager, next, jumper"
					:total="total">
				</el-pagination>
      </div>
    </div>
	<resourceAdd ref="resourceAddForm"></resourceAdd>
	 <el-drawer title="图片预览" :visible.sync="innerVisible" direction="ltr" size="100%" :append-to-body="true" :with-header="true">
		 <div style="width:100%; height:100%; overflow: auto;">
		 <img :src="getUrl(curPic)">
		 </div>
	  </el-drawer>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import resourceAdd from './ResourceAdd'
import $ from "jquery";

export default {
  data() {
    return {
		datas:[],
		total:0,
		page:1,
		rows:10,
		innerVisible:false,
		curPic:null,
		selects:[],
    }
  },
  components: {
	  resourceAdd
	},
  mounted() {
    this.loadData();
  },
  computed: {},
  methods: {
	  newResource(){
		  this.$refs['resourceAddForm'].openDailog();
	  },
	 getUrl(item){
		  if(item){
			  return baseUrl + 'bigscreen/resource/img/' + item.path+'.action'
		  }
		   else{
			   return "#";
		   }
	  },
	  handleSizeChange(v){
        this.rows = v;
				this.loadData();
    },
    handleCurrentChange(v){
        this.page = v;
				this.loadData();
    },
		loadData(){
			ajax({
				url:"bigscreen/resource/list.action",
				data:{page:this.page,rows:this.rows},
				type:"get",
				success:(resp)=>{
					this.datas = resp.rows;
					this.total = resp.total;
				}
			}, this);
		},
		expResource(){
			if(this.selects.length == 0){
				this.$notify.error("未勾选数据。");
				return;
			}
			/**
			ajax({
				url:"bigscreen/resource/export.action",
				data:JSON.stringify(this.selects),
				postJSON:true,
				type:"POST",
				success:(r)=>{

				}
			}, this);
			 */
			var ctx = "<form name='expff' method='post' action=\""+(baseUrl==""?"":(baseUrl+"/"))+"bigscreen/resource/export.action\" id='expresfrom'><input type='hidden' name='ids' id='ids'></form>";
			if($("#expresfrom").length == 0){
				$(ctx).appendTo("body");
			}
			$("#expresfrom #ids").val(JSON.stringify(this.selects));
			$("#expresfrom").submit().remove();
		},
		delResource(){
			if(this.selects.length == 0){
				this.$notify.error("未勾选数据。");
				return;
			}
			if(confirm("是否确认删除？")){
				ajax({
					url:"bigscreen/resource/delete.action",
					data:JSON.stringify(this.selects),
					postJSON:true,
					type:"POST",
					success:(r)=>{
						this.loadData();
						this.selects = [];
					}
				}, this);
			}
		},
		showpic(p){
			this.innerVisible = true;
			this.curPic = p;
		}
  },
  watch: {}
};
</script>

<style lang="less" scoped>
.file-box {
    float: left;
    width: 220px;
}
.file {
    border: 1px solid #e7eaec;
    padding: 0;
    background-color: #ffffff;
    position: relative;
    margin-bottom: 20px;
    margin-right: 20px;
}
.file .image {
    height: 100px;
    overflow: hidden;
}
.file .file-name {
    padding: 10px;
    background-color: #f8f8f8;
    border-top: 1px solid #e7eaec;
}

.file-name small {
    color: #676a6c;
}
.file .file-name .name {
    overflow: hidden;
    white-space: nowrap;
    width: 100%;
    text-overflow: ellipsis;
}
</style>
