<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="wrapper-content">
    <div class="ibox" id="mainDiv">
      <div class="ibox-title">模型分类管理</div>
      <div class="ibox-content">
        <div class="row">
          <div class="col-sm-6">
            <div id="typetree"></div>
          </div>
          <div class="col-sm-6">
            <p class="text-warning">在分类上点击鼠标右键来新建或编辑分类。</p>
          </div>
        </div>
      </div>
    </div>
	  	<el-dialog :title="title" :visible.sync="dailogShow" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
          <span class="el-dialog__title">{{title}}</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
        <el-form :model="type" :rules="rules" ref="typeForm" size="mini">
				   <el-form-item label="名称" label-width="100px" prop="name">
				     	<el-input v-model="type.name" ></el-input>
				    </el-form-item>
					<el-form-item label="说明" label-width="100px">
				     	<el-input v-model="type.note" ></el-input>
				    </el-form-item>
					<el-form-item label="排序字段" label-width="100px">
              <el-input-number v-model="type.ord" ></el-input-number>
				  </el-form-item>
				</el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="saveType()">确 定</el-button>
          <el-button @click="dailogShow = false">取 消</el-button>
        </div>
	  	</el-dialog>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { baseUrl, ajax } from "@/common/biConfig";
import $ from "jquery";
import "jstree";
import "jstree/dist/themes/default/style.min.css";

export default {
  data() {
    return {
		type:{
      dsId:null,
      name:null,
      note:null,
      ord:null
		},
		rules:{
			name:[
				{ required: true, message: '必填', trigger: 'blur' }
			]
    },
    title:"",
    dailogShow:false,
    isupdate:false,
    treeRef:null
	};
  },
  components: {

	},
  mounted() {
    this.initTree();
  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
	  addType:function(node, isupdate){
		  this.operDailogTitle = isupdate?"修改分类":"创建分类";
      this.isupdate = isupdate;
      this.dailogShow = true;
      if(this.$refs['typeForm']){
        this.$refs['typeForm'].clearValidate();
      }
      if(isupdate){
        this.title = "编辑分类";
        const node = this.treeRef.get_selected(true)[0];
        //回写值
        let ts = this;
        ajax({
          url:"model/getSubjectType.action",
						data:{id:node.id},
						dataType:"json",
						success:function(dt){
							dt = dt.rows;
              ts.type.dsId = dt.dsId;
              ts.type.name = dt.name;
              ts.type.note = dt.note;
              ts.type.ord = dt.ord;
						}
        }, ts);
      }else{
        this.title = "创建分类";
        //清空值
        for(let v in this.type){
          this.type[v] = null;
        }
        var ord = this.treeRef.get_node("zty").children.length + 1 ;
        this.type.ord = ord;
      }
	  },
	  delType:function(node){
      let ts = this;
      if(confirm("是否确认?")){
        ajax({
          type:"GET",
          data:{id:node.id},
          postJSON:false,
          url:"model/delSubjectType.action",
          success:function(){
            ts.treeRef.delete_node(node);
          }
        }, ts);
      }
	  },
	  saveType:function(){
      const node = this.treeRef.get_selected(true)[0];
      let ts = this;
      let ret = true;
				this.$refs['typeForm'].validate((valid) => {
					if (valid) {
						ajax({
							type:"POST",
							data: ts.type,
							postJSON:false,
              url:!ts.isupdate?"model/saveSubjectType.action":"model/updateSubjectType.action",
							success:function(resp){
                /**
                if(ts.isupdate){
                  ts.treeRef.rename_node(node, ts.type.name);
                }else{
                  ts.treeRef.create_node('zty', {id:resp.rows,text:ts.type.name, icon:'glyphicon glyphicon-stats'});
                }
                 */
                ts.initTree();
                ts.dailogShow = false;
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
      if(this.treeRef){
        this.treeRef.destroy();
      }
		let ts = this;
      $("#typetree")
        .jstree({
          core: {
            check_callback: true,
            data: function (obj, callback) {
              if (obj.id == "#") {
                callback.call(this, [
                  {
                    id: "zty",
                    text: "主题域",
                    children: true,
                    state: { opened: true },
                    icon: "glyphicon glyphicon-globe",
                  },
                ]);
              } else {
                ajax({
                  type: "GET",
                  data: {id: obj.id},
                  postJSON: false,
                  url: 'model/SubjectType.action',
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
                  ts.addType(node, false);
                },
              },
              modify: {
                label: "修改",
                icon: "glyphicon glyphicon-edit",
                action: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  ts.addType(node, true);
                },
                _disabled: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  if (node.id == "zty") {
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
                  if (node.id == "zty") {
                    return true;
                  } else {
                    return false;
                  }
                },
                action: function (data) {
                  const inst = $.jstree.reference(data.reference),
                    node = inst.get_node(data.reference);
                  ts.delType(node);
                },
              },
            },
          },
          plugins: ["wholerow", "contextmenu"],
        });
        this.treeRef = $("#typetree").jstree(true);
    },
  },
  watch: {},
};
</script>
