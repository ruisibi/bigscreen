<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <el-dialog title="个人信息" :visible.sync="show">
    <dl class="dl-horizontal">
      <dt>登录名：</dt>
      <dd>{{user.staffId}}</dd>
      <dt>用户名：</dt>
      <dd>{{user.loginName}}</dd>
      <dt>所属企业：</dt>
      <dd>{{productInfo.company}}</dd>
      <dt>所属部门：</dt>
      <dd>{{user.deptName}}</dd>
      <dt>账号状态：</dt>
      <dd>{{ user.state==1?"启用":"停用" }}</dd>
      <dt>登录次数：</dt>
      <dd>{{user.logCnt}}次</dd>
      <dt>上次登录时间：</dt>
      <dd>{{user.loginDate}}</dd>
    </dl>
      <div slot="footer" class="dialog-footer">
				<el-button @click="show = false">取 消</el-button>
			  </div>
  </el-dialog>
</template>

<script>
import { ajax } from "@/common/biConfig";
import $ from "jquery";

export default {
  name: "userInfo",
  data() {
    return {
      show:false,
      user:{},
      productInfo:{}
    };
  },
  mounted:function(){
    let ts = this;
    ajax({
      type:"GET",
      data:{},
      url:"frame/User.action",
      success:function(resp){
        ts.user = resp.rows;
        ts.$parent.loginName = ts.user.loginName;
      }
    }, ts);
    this.loadProductInfo();
  },
  methods: {
    loadProductInfo(){
				ajax({
					url:"login/productInfo.action",
					data:{},
					type:"GET",
					success:(r)=>{
						this.productInfo = r.rows;
					}
				}, this);
			}
  },
};
</script>
