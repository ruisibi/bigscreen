<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="wrapper-content">
    <div class="ibox" id="mainDiv">
      <div class="ibox-title">角色管理 >> 所辖菜单</div>
      <div class="ibox-content">
        <div class="row">
          <div class="col-sm-6">
           <a href="javascript:;" @click="openall">全部展开</a> &nbsp;&nbsp; <a href="javascript:;" @click="closeall">全部关闭</a>
           <div id="menuTree"></div>
          </div>
          <div class="col-sm-6" align="right">
            <el-button type="primary" @click="save()">确 定</el-button>
			    	<el-button @click="backpage()">取 消</el-button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { baseUrl, ajax } from "@/common/biConfig";
import $ from "jquery";

export default {
  data() {
    return {
      roleId : null
  	}
  },
  components: {
	},
  mounted() {
    this.roleId = this.$route.query.roleId;
    this.initTree();
  },
  computed: {},
  methods: {
    //初始化 jstree
    initTree: function () {
		let ts = this;
      $("#menuTree")
        .jstree({
          core: {
            check_callback: true,
            data: function (obj, callback) {
                ajax({
                  type: "GET",
                  data: {roleId:ts.roleId},
                  postJSON: false,
                  url: 'auth/role/roleMenu.action',
                  success: function (resp) {
                    callback.call(this, resp.rows);
                  },
                }, ts);
            },
          },
          plugins: ["checkbox","wholerow"],
        })
        .bind("open_node.jstree", function (a, b) {
          if (b.node.id == "0") {
            return;
          }
          const ref = $("#menuTree").jstree(true);
          ref.set_icon(b.node, "fa fa-folder-open-o");
        })
        .bind("close_node.jstree", function (a, b) {
          if (b.node.id == "0") {
            return;
          }
          const ref = $("#menuTree").jstree(true);
          ref.set_icon(b.node, "fa fa-folder-o");
        });
        this.treeRef = $("#menuTree").jstree(true);
    },
    save:function(){
      const ts = this;
      let ids = "";
      let nodes = ts.treeRef.get_selected(false);
      for(let i=0; nodes&&i<nodes.length; i++){
        ids = ids + nodes[i] + ",";
      }
      $("#menuTree").find(".jstree-undetermined").each(function (i, element) {
          ids = ids + $(element).closest('.jstree-node').attr("id") + ",";
      });
      if(ids.length > 0){
        ids = ids.replace(",root", "");
        ids = ids.substring(0, ids.length - 1);
      }
      ajax({
        type:"POST",
        url:"auth/role/menuSave.action",
        dataType:"JSON",
        data:{roleId:ts.roleId, menuIds:ids},
        success:function(resp){
           ts.$notify.success({title: '角色授权成功',offset: 50});
           ts.backpage();
        }
      }, ts);
    },
    backpage:function(){
      this.$router.push('Role')
    },
    openall:function(){
      this.treeRef.open_all();
    },
    closeall:function(){
        this.treeRef.close_all();
    }
  },
  watch: {},
  beforeRouteLeave: function(to, from, next) {
    this.$destroy();
    next();
  }
};
</script>

<style lang="less" scoped>
@import "../../style/mixin";
</style>
