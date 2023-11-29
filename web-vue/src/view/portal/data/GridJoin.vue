<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="合并字段" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
        <template slot="title">
              <span class="el-dialog__title">合并字段</span>
              <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
              <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
              </button>
            </template>
        <el-form :model="val" ref="valForm" :rules="rule" label-position="left" size="mini">
            <el-form-item label="待合并字段：" label-width="110px" prop="selectCols">
                <el-checkbox-group v-model="val.selectCols">
                    <template v-for="item in opt.joinCols">
                        <el-checkbox :key="item.id" :label="item.id">{{item.name}}</el-checkbox>
                    </template>
                </el-checkbox-group>
            </el-form-item>
            <el-form-item label="新节点名：" label-width="110px" prop="nodeName">
               <el-input v-model="val.nodeName"/>
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
import {baseUrl, newGuid, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
      show:false,
      val:{
       selectCols:[],
       nodeName:null,
      },
      rule:{
         selectCols: [{ required: true, message: "必填", trigger: "blur" }],
         nodeName: [{ required: true, message: "必填", trigger: "blur" }],
      },
      comp: null,
      opt:{
        joinCols:[], //带合并字段
      }
    }
  },
  mounted(){

  },
  computed: {
    ...mapState(["isMini"])
  },
  methods: {
    showDailog(comp){
      this.show = true;
      this.comp = comp;
      let jcols = [];
      comp.cols.forEach((v, idx)=>{
        if(v.nodeType === 'note'){	//只支持一级合并
          return;
        }
        jcols.push({id:v.id, name:v.name});
      });
      this.opt.joinCols = jcols;
    },
    save(){
      	this.$refs['valForm'].validate((valid) => {
          if(valid){
            var nodes = [];
            let comp = this.comp;
            $(this.val.selectCols).each(function(a, b){
              $(comp.cols).each(function(c, d){
                if(d.id === b){
                  d.level = 1;
                  nodes.push(d);
                  comp.cols.splice(c, 1);
                  return false;
                }
              });
            });
            var newNode = {id:newGuid(), name:this.val.nodeName, level:0, nodeType:"note", children:nodes};
            comp.cols.push(newNode);
            this.show = false;
            this.$parent.setUpdate();
            this.$parent.gridView();
          }
        });
    }
  },
  watch: {

  }
}
</script>
