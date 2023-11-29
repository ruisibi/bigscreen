<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="大屏分享" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">大屏分享</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
		  </template>
      <el-form :model="form" label-width="120px" size="mini" :rules="rule">
      <div class="el-dialog-div">
        <el-tabs v-model="activeName">
          <el-tab-pane label="直接生成URL" name="url">
            <el-form-item label="是否需要登录：">
              <el-radio v-model="form.isloginn" label="1" border>是</el-radio>
              <el-radio v-model="form.isloginn" label="0" border>否</el-radio>
            </el-form-item>
            <el-form-item label="有效期：">
              <el-radio v-model="form.yxq" label="1" border>一小时</el-radio>
              <el-radio v-model="form.yxq" label="24" border>一天</el-radio>
              <el-radio v-model="form.yxq" label="-1" border>永久有效</el-radio>
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="推送到菜单" name="menu">
            <el-form-item label="名称：" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入名称"></el-input>
            </el-form-item>
            <el-form-item label="排序：" prop="menuOrder">
              <el-input-number size="small" v-model="form.menuOrder" :min="1" :max="10000" label="描述文字"></el-input-number>
            </el-form-item>
             <el-form-item label="上级菜单：" prop="pid">
              <div id="menuTree" class="treestyle"></div>
            </el-form-item>
          </el-tab-pane>

        </el-tabs>
      </div>
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
import { Loading } from 'element-ui'
import $ from 'jquery'
import "jstree";
import "jstree/dist/themes/default/style.min.css";

export default {
  components:{

  },
  data(){
    return {
        show:false,
        reportId:null,
        activeName:"url",
        form:{
          yxq:"1",
          menuName:null,
          menuOrder:1,
          pid:null,
          isloginn:"1",
        },
        rule:{
          menuName:[{ required: true, message: "必填", trigger: "blur" }],
          menuOrder:[{ required: true, message: "必填", trigger: "blur" }],
          pid:[{ required: true, message: "必填", trigger: "blur" }],
        }
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    share(id){
      this.show = true;
      this.reportId = id;
      this.form.yxq = "1";
      this.form.menuName = null;
      this.form.menuOrder = 1;
      this.form.pid= null;
      this.form.isloginn = "1";
      this.$nextTick(()=>{
        this.initMenuTree();
      });
    },
    save(){
      let v = this.form;
      if(this.activeName == 'url'){
        //复制url
        var islog = v.isloginn;
        var yxq = v.yxq;
        if(yxq == "2"){
          yxq = "24";
        }
        ajax({
          url:"bigscreen/copyUrl.action",
          type:"POST",
          data:{reportId:this.reportId, islogin:islog, yxq:yxq },
          dataType:"json",
          success:(resp)=>{
            this.show = false;
            let base = window.location.href.split("#")[0];
            let u = base + '#' + "/bigscreen/ShareView?token=" + resp.rows;
            window.setTimeout(()=>{
              this.$alert('<a target="_blank" href="'+u+'">'+u+'</a><br/><img src='+baseUrl+'portal/generateqrcode.action?url='+escape(u)+'">','生成URL成功', {
                center: true,
                confirmButtonText: '确定',
                dangerouslyUseHTMLString: true
              });
            }, 200);
          }
        }, this);
      }else if(this.activeName == 'menu'){
        var name = v.menuName;
        var ord = v.menuOrder;
        if(!name){
          this.$notify.error("名称必须填写。");
          return;
        }
        if(!ord){
          this.$notify.error("排序必须填写。");
          return;
        }

        var ref = $("#menuTree").jstree(true);
        var node = ref.get_selected(true);
        if(node.length == 0){
          this.$notify.error("请选择上级菜单。");
          return;
        }
        //新增只能配置3级菜单
        node = node[0];
        var p1 = node.parent;
        if(p1 != null){
          var p2 = ref.get_node(p1).parent;
          if(p2 != null){
            var p3 = ref.get_node(p2).parent
            if(p3 == "#"){
              this.$notify.error("菜单只能建3级。");
              return;
            }
          }
        }
        var url = "/bigscreen/PushView?bsid=" + this.reportId;
        ajax({
          type:"POST",
          url:"auth/menu/save.action",
          data:{"menuName":name,"menuDesc":"","menuOrder":ord, "menuUrl":url, "menuPid":node.id,urls:""},
          dataType:"json",
          success:() => {
            this.$notify.success("菜单推送成功。");
            this.show = false;
          }
        }, this);
      }
    },
    initMenuTree(){
      let ts = this;
      let ref = $("#menuTree").jstree(true);
      if(ref){
        ref.destroy();
      }
      $("#menuTree").jstree({
        core: {
            check_callback: true,
            data: function (obj, callback) {
              let id = obj.id;
              if(obj.id === '#'){
                id = '0';
              }
              ajax({
                type: "GET",
                data: {id: id},
                postJSON: false,
                url: 'auth/menu/loadData.action',
                success: function (resp) {
                  callback.call(this, resp.rows);
                },
              }, ts);
            },
          },
          plugins: ["wholerow"],
      });
    }
  }
}
</script>

<style lang="less" scoped>
 .treestyle{
    height: 160px;
    overflow: auto;
    border: 1px solid #cfdadd;
    border-radius: 5px;
 }
</style>
