<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="合并度量" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">合并度量</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
      <el-form ref="tableForm" :model="prop" :rules="rule" label-width="90px" size="mini">
        <el-form-item label="待合并节点：" label-width="120px" prop="tnodeId" >
            <el-checkbox-group v-model="prop.tnodeId">
              <el-checkbox v-for="nd in opt.nodes" :label="nd.id" :key="nd.id">{{nd.desc}}</el-checkbox>
            </el-checkbox-group>
        </el-form-item>
         <el-form-item label="节点名称：" label-width="120px" prop="nodeName">
					 <el-input v-model="prop.nodeName" />
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
import * as utils from '@/view/portal/Utils'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
      show:false,
      prop:{
        tnodeId:[],
        nodeName:null
      },
      opt:{
        nodes:[]
      },
      rule:{
        tnodeId:[{ required: true, message: "必填", trigger: "blur" }],
        nodeName:[{ required: true, message: "必填", trigger: "blur" }],
      },
      ls:null,
      item:null,
      pos:null,
      currKpis:null,
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    openDailog(node, pos, comp){
      this.show = true;
      //查询选择节点平级的指标列表
      let kpis = [];
      let currKpis = null;
      utils.loopDims(pos==='row'?comp.rows:comp.cols, (a,ls,c) => {
        if(a.id === node.id){
          currKpis = ls;
          ls.forEach((val, idx)=>{
            if(val.type === 'kpi' || val.type === 'none'){  //可以合并kpi和已经合并的节点
              kpis.push(val);
            }
          });
        }
      });
      this.opt.nodes = kpis;
      this.currKpis = currKpis;
      this.prop.nodeName = null;
      this.prop.tnodeId = [];
    },
    save(){
      this.$refs['tableForm'].validate((valid)=>{
        if(valid){
            let o = this.$parent;
            let desc = this.prop.nodeName;
            let nodes = this.prop.tnodeId;
            let currKpis = this.currKpis;
            let newNode = {id:newGuid(),type:"none", desc, level:currKpis[0].level, children:[]};
            //从currKpis中移除已经选择的节点, 添加到 newNode 的 children中
            for(let n of nodes){
              let nodeId = n;
              currKpis.forEach((val, idx)=>{
                if(val.id === nodeId){
                  val.level = val.level + 1;
                  newNode.children.push(val);
                  currKpis.splice(idx, 1);

                  //给当前node的下级node设置 level + 1
                  utils.loopDims(val.children, function (a) {
                    a.level = a.level + 1;
                  });
                }
              });
            }
            currKpis.push(newNode);
            this.show = false;
            o.setUpdate();
            o.tableView();
        }
      });
    },
  }
}
</script>
