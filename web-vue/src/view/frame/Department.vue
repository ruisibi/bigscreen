<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="wrapper-content">
    <div class="ibox" id="mainDiv">
      <div class="ibox-title">组织机构管理</div>
      <div class="ibox-content">
        <div class="row">
          <div class="col-sm-6">
            <div id="deptTree"></div>
          </div>
          <div class="col-sm-6">
            <p class="text-warning">点击鼠标右键来新建或编辑组织机构。</p>
          </div>
        </div>
      </div>
    </div>
	<operationDailog mainDiv="mainDiv"  :title="operDailogTitle" ref="operForm" :callback="saveDepartment">
		<div class="row">
			<div class="col-sm-12">
				<el-form :model="dept" :rules="rules" ref="deptForm" size="small">
				    <el-form-item label="名称" label-width="100px" prop="deptName">
				     	<el-input v-model="dept.deptName" ></el-input>
				    </el-form-item>
					<el-form-item label="编码" label-width="100px">
				     	<el-input v-model="dept.deptCode" placeholder="用在数据权限中,多个值用逗号分隔"></el-input>
				    </el-form-item>
					<el-form-item label="备注" label-width="100px">
				     	<el-input v-model="dept.deptNote" ></el-input>
				  </el-form-item>
				</el-form>
			</div>
		</div>
	</operationDailog>
  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import operationDailog from '@/components/OperationDailog'
import $ from "jquery";
import "jstree";
import "jstree/dist/themes/default/style.min.css";

export default {
  data() {
    return {
		dept:{
      deptName:null,
      deptCode:null,
      deptNote:null,
      pid:null,
      id:null
		},
		rules:{
			deptName:[
				{ required: true, message: '必填', trigger: 'blur' }
			]
		},
		operDailog:false,
		operDailogTitle:"",
    isupdate:false,
    treeRef:null
	};
  },
  components: {
		operationDailog
	},
  mounted() {
    this.initTree();
  },
  computed: {},
  methods: {
	  addDepartment:function(node, isupdate){
		  this.operDailogTitle = isupdate?"修改组织机构":"创建组织机构";
      this.$refs.operForm.showDailog();
      this.isupdate = isupdate;

      if(isupdate){
        const node = this.treeRef.get_selected(true)[0];
        //回写值
        let ts = this;
        ajax({
          url:"frame/getDepartment.action",
						data:{id:node.id},
						dataType:"json",
						success:function(dt){
							dt = dt.rows;
              ts.dept.deptName = dt.deptName;
              ts.dept.id = dt.id;
              ts.dept.deptCode = dt.deptCode;
              ts.dept.deptNote = dt.deptNote;
              ts.dept.pid = dt.pid;
						}
        }, ts);
      }else{
        //清空值
        for(let v in this.dept){
          this.dept[v] = null;
        }
      }
	  },
	  delDepartment:function(node){
      let ts = this;
      if(confirm("是否确认?")){
        ajax({
          type:"GET",
          data:{id:node.id},
          postJSON:false,
          url:"frame/delDepartment.action",
          success:function(){
            ts.treeRef.delete_node(node);
          }
        }, ts);
      }
	  },
	  //在 operationDailog 里面进行回调的函数
	  saveDepartment:function(){
      const node = this.treeRef.get_selected(true)[0];
      if(!this.isupdate){
        this.dept.pid = node.id;
      }
      let ts = this;
      let ret = true;
				this.$refs['deptForm'].validate((valid) => {
					if (valid) {
						ajax({
							type:"POST",
							data: ts.dept,
							postJSON:false,
              url:!ts.isupdate?"frame/saveDepartment.action":"frame/updateDepartment.action",
							success:function(resp){
                if(ts.isupdate){
                  ts.treeRef.rename_node(node, ts.dept.deptName);
                }else{
                  ts.treeRef.create_node(node.id, {id:resp.rows,text:ts.dept.deptName, icon:'fa fa-list-ul'});
					        ts.treeRef.open_node(node);
                }
							}
						}, ts);
					}else{
            ret = false;
          }
				});
		  return ret;
	  },
    //初始化 jstree
    initTree: function () {
		let ts = this;
      $("#deptTree")
        .jstree({
          core: {
            check_callback: true,
            data: function (obj, callback) {
              if (obj.id == "#") {
                callback.call(this, [
                  {
                    id: "0",
                    text: "组织机构树",
                    children: true,
                    state: { opened: true },
                    icon: "fa fa-university",
                  },
                ]);
              } else {
                ajax({
                  type: "GET",
                  data: {id: obj.id},
                  postJSON: false,
                  url: 'frame/loadDepartment.action',
                  success: function (resp) {
                    callback.call(this, resp.rows);
                  },
                }, ts);
              }
            },
          },
          contextmenu: {
            items: {
              add: {
                label: "新增",
                icon: "glyphicon glyphicon-plus",
                action: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  ts.addDepartment(node, false);
                },
              },
              modify: {
                label: "修改",
                icon: "glyphicon glyphicon-edit",
                action: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  ts.addDepartment(node, true);
                },
                _disabled: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  if (node.id == "0") {
                    return true;
                  } else {
                    return false;
                  }
                },
              },
              remove: {
                label: "删除",
                icon: "glyphicon glyphicon-trash",
                _disabled: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  if (node.id == "0") {
                    return true;
                  } else {
                    return false;
                  }
                },
                action: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  ts.delDepartment(node);
                },
              },
            },
          },
          plugins: ["wholerow", "contextmenu"],
        });
        this.treeRef = $("#deptTree").jstree(true);
    },
  },
  watch: {},
};
</script>

<style lang="less" scoped>
@import "../../style/mixin";
</style>
