<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <div style="height:100%">
    <template v-if=" useIn !== 'dashboardEdit'">
      <div v-html="data"></div>
    </template>
    <template v-if="useIn === 'dashboardEdit'">
      <div id="text-div"></div>
      <div class="actColumn">
        <div>可绑定指标<a @click="helper()" href="javascript:;"><i class="fa fa-question-circle"></i></a>：</div>
           <template v-for="item in kpis">
             <button :key="item.colId" @click="selectkpi(item.alias, item.name, item.tid)" type="button" class="btn btn-primary btn-xs kpiInfo">{{item.name}}</button>
           </template>
      </div>

      <el-dialog custom-class="nopadding" width="50%" title="富文本组件绑定指标说明" :visible.sync="innerVisible" append-to-body>
        <div style="line-height:30px; padding:15px;">
          一个富文本组件可以绑定一个立方体，然后可以把立方体的指标插入文本中。 采用 ${指标名.别名} 方式映射一个指标数据。 <br/>
          当前可绑定的指标来源于用户当前选择的立方体，当把一个立方体的指标插入文本后，其他立方体的指标将不能再插入。<br/>
          采用 $extUtils.numberFmt(${指标名.别名}, '#,##0') 函数可以格式化指标值。 <br/>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="innerVisible = false">关 闭</el-button>
        </div>
      </el-dialog>
    </template>
  </div>
</template>
<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'

export default {
  components:{
  },
  data(){
    return {
      data:null, //文本字符串
      kpis:[], //可绑定的指标
      innerVisible:false,
    }
  },
  props:{
     pageInfo:{
        type:Object,
        required:false,
        default:()=>{}
      },
      comp:{
        type:Object,
        required:true,
        default:()=>{}
      },
      editor:{
        type:Boolean,
        required:true,
        default:true
      },
      portalParams:{
        type:Array,
        required:false
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
      //在仪表盘分享后，需要使用token
      token:{
        type:String,
        required:false,
        default:null,
      },
      //是否运行自动刷新(一般在设计模式不运行，在发布后运行)
      autuFlush:{
        type:Boolean,
        required: false,
        default: false
      }
  },
  mounted(){
     //正在报表中使用 editor 属性
    if(this.useIn==='report') {
      if (this.editor === true) {
        this.textView();
      }
    }else{
      this.textView();
    }
    if(this.useIn === 'dashboardEdit'){
      this.loadKpis();
    }
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){
      this.textView();
    },
    helper(){
      this.innerVisible = true;
    },
    loadKpis(){
      if(!this.pageInfo.selectDs){
        return;
      }
      ajax({
        url:"model/cubeInfo.action",
        data:{tableId:this.pageInfo.selectDs},
        type:"POST",
        success:(resp)=>{
          this.kpis = resp.rows.kpis;
        }
      }, this);
    },
    //请求后台绑定变量
    textView(){
      let comp = this.comp;
      if(comp.comp && comp.comp.tid){
        let styleType = this.pageInfo&&this.pageInfo.style ? this.pageInfo.style.styleType:null;
        let prefix = "#" + this.useIn;
        let target = document.querySelector(prefix+comp.id);
        let loadingInstance = Loading.service({fullscreen:false, background:utils.getLoadingbackground(styleType), target});
        let url = "portal"+(this.token?"/share":"")+"/TextView.action" + (this.token?"?token="+this.token:"");
        ajax({
          url:url,
          type:"POST",
          data:JSON.stringify({id:comp.id, tid:comp.comp.tid, ctx:comp.desc}),
          postJSON:true,
          success:(resp)=>{
            this.data = resp.rows;
            loadingInstance.close();
          }
        }, this, loadingInstance);
      }else{
        this.data = comp.desc;
      }
    },
    selectkpi(alias, desc, tid){
      let comp = this.comp;
      if(!comp.comp){
        comp.comp = {id:comp.id};
      }
      let c = this.comp.comp;
      if(c.tid && c.tid != tid){
        this.$notify.error("文本框中已绑定的指标和当前选择的指标不在一个表中，选择失败。");
        return;
      }
      c.tid = tid;
      c.tname = this.tname;
      this.$parent.editor.cmd.do('insertHTML', " ${"+desc+"."+alias+"} ");
    },
  },
  watch: {

  }
}
</script>
<style lang="less" scoped>
.kpiInfo {
  margin-right: 5px;
  margin-bottom: 5px;
}
.actColumn {
  text-align: left;
}
</style>
