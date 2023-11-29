<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div class="bslayout" :style="bsstyle">
		<layout-view ref="optarea" useIn="view" :token="token" :pageInfo="pageInfo"></layout-view>
	</div>
</template>
<script>
import {baseUrl, ajax} from '@/common/biConfig'
import { MessageBox } from 'element-ui';
import $ from 'jquery'
import LayoutView from './layout/LayoutView'
import * as bsUtils from './bsUtils'

export default {
  components: {
    LayoutView
  },
  props: {

  },
  data() {
    return {
      token:null,
      pageInfo:{},
      bsstyle:{},  //大屏样式
    }
  },

  methods: {
    getCfg(){
      ajax({
        url:"report/share/get.action",
        data:{token:this.token},
        type:"get",
        success:(resp)=>{
           window.document.title = "大屏分享";
           let p = JSON.parse(resp.rows.cfg);
						if(!p.style.pageBgColor){
							this.bsstyle['background-color'] =  p.style.styleType==='bigscreen' ? bsUtils.pageBgColor:bsUtils.pageWhiteBgColor;
						}else{
							this.bsstyle['background-color'] = p.style.pageBgColor;
            }
            this.pageInfo = bsUtils.initTabHideEvent(p);
        }
      }, this);
    },
    //组件链接
    linkurl(link, url, param){
      this.$router.push({path:url, query:param});
      //if(link.linkType==='blank'){
        //添加菜单
        //this.$parent.$refs['navMenuForm'].menuAdd({menuId:link.id, menuName:link.linkName, menuUrl:url});
      //}
    },
  },
  mounted(){

  },
  watch: {

  },

  beforeRouteEnter(to, from, next){
    next((vm)=>{
        vm.token = vm.$route.query.token;
        vm.getCfg();
    })
  }
}
</script>
<style lang="less" scoped>
	.bslayout{
		width: 100%;
		height: 100%;
	}
</style>
