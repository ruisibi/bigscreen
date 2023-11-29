<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="添加图片" width="40%" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">添加图片</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
      </template>
        <el-form label-width="120px" size="mini">
          <el-form-item label="方式：">
            <div>
              <el-radio v-model="picType" label="write" border>选择图片</el-radio>
              <el-radio v-model="picType" label="upload" border>上传图片</el-radio>
            </div>
          </el-form-item>
        <el-form-item label="图片地址：" v-if="picType === 'write'">
          <resourceSelect ref="resourceSelectForm" column="selectpic" :cb="getImage" :prop="prop"></resourceSelect>
        </el-form-item>
        <el-form-item label="上传图片：" v-if="picType === 'upload'">
          <el-upload
						class="upload-demo"
						ref="upload"
						:action="upurl"
						:multiple="false"
						:limit="1"
						:auto-upload="false"
						 :on-change="handleChange"
						 :on-success="success"
						:file-list="fileList">
						<el-button size="small" type="primary">点击上传图片，且不超过1MB</el-button>
					</el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">确 定</el-button>
        <el-button @click="show = false">取 消</el-button>
      </div>
    </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl, newGuid} from '@/common/biConfig'
import $ from 'jquery'
import resourceSelect from '@/components/toolbox/ResourceSelect'

export default {
  components:{
    resourceSelect
  },
  props:{

  },
  data(){
    return {
        show:false,
        comp:null,
        picType:"write",
        //url:null,
        upurl:"portal/ImgUpload.action",
        fileList:[],
        prop:{
          selectpic:null,
        }
    }
  },
  mounted(){

  },
  computed: {
      ...mapState(["isMini"])
  },
  methods: {
    openDailog(comp){
      this.show = true;
      this.comp = comp;
      this.picType = "write";
      this.prop.selectpic = null;
      this.fileList = [];
    },

    handleChange(p){

    },
    getImage(v){
      //console.log("v=" + v);
    },

    success(resp){
      if(resp.result === 0){  //上传失败
        this.$notify.error(resp.msg);
      }else{
        //设置对象
        let comp = this.comp;
        comp.picurl =  "portal/img/" + resp.rows+".action";
        comp.picType = "local";  //分 net/local 两种
        this.$parent.setUpdate();
        this.compRender();
        this.show = false;

        //获取图片宽度和高度
       var compId = comp.id;
        window.setTimeout(function(){
          var img = document.getElementById("pic" + compId);
          var wd = img.width;
          var h = img.height;
          comp.width = wd;
          comp.height = h;
        }, 600);

      }
    },
    compRender(){
      let comp = this.comp;
      this.$parent.$refs['mv_'+comp.id].$forceUpdate();
    },
    save(){
      let comp = this.comp;
      if(this.picType === 'write'){  //选择图片
        let u = this.prop.selectpic;
       if(!u){
         this.$notify.error("还未选择图片。");
         return;
       }
       comp.picurl = "bigscreen/" + u;
       comp.picType = "net";  //分 net/local 两种
       this.$parent.setUpdate();
       this.compRender();
       this.show = false;
        //获取图片宽度和高度
       var compId = comp.id;
        window.setTimeout(function(){
          var img = document.getElementById("pic" + compId);
          var wd = img.width;
          var h = img.height;
          comp.width = wd;
          comp.height = h;
        }, 600);
      }else{  //上传图片
       var files = this.$refs['upload'].uploadFiles;
       if(files.length == 0){
         this.$notify.error("请选择上传图片。");
         return;
       }
        this.$refs['upload'].submit();
      }
    }
  }
}
</script>
