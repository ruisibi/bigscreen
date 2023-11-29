<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="交叉表钻取配置" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">交叉表钻取配置</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
      </template>
       <p class="text-warning">说明：在交叉表上实现一次维度下钻。</p>
       <el-form :model="prop" ref="propForm" label-position="left" size="mini">
          <el-form-item label="启用下钻：" label-width="120px">
              <el-switch v-model="prop.usedrill"></el-switch>
            </el-form-item>
            <el-form-item label="钻取维：" label-width="120px" v-if="prop.usedrill == true">
              <el-select v-model="prop.drillDim" placeholder="请选择" style="width:100%">
                <el-option
                v-for="item in cols"
                :key="item.alias"
                :label="item.name"
                :value="item.alias">
                </el-option>
              </el-select>
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
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
        show:false,
        prop:{
          usedrill:false,
          drillDim:null,
        },
        comp:null,
        cols:[],
        dims:null,
    }
  },
  mounted(){

  },
  computed: {
     ...mapState(["isMini"])
  },
  methods: {
    //查询交叉表数据对应的维度
    queryTableDims(){
      ajax({
        type:"post",
        url:"model/cubeInfo.action",
        data: {tableId: this.comp.tid},
        dataType:"json",
        success: (resp)=>{
          let dims = resp.rows.dimDtos;
          let cols = [];
          for(let k=0; k<dims.length; k++){
            var name = dims[k].dimdesc;
            var id =  dims[k].colname;
            var alias = dims[k].alias;
            if(dims[k].id == this.comp.rows[0].match){
              continue;
            }
            cols.push({id:id, name:name, alias:alias});
          }
          this.cols = cols;
          this.dims = dims;
        }
      });
    },
    openDailog(comp){
      if(comp.rows.length !== 1  || comp.rows[0].children.length != 0 ){
        this.$notify.error("交叉表行标签有且只有一个维度的时候才能配置交叉表钻取。");
        return;
      }
      this.show = true;
      this.comp = comp;
      this.queryTableDims();
      if(comp.drillDim && comp.drillDim.length > 0){
        this.prop.usedrill = true;
        this.prop.drillDim = comp.drillDim[0].code;
      }else{
        this.prop.usedrill = false;
        this.prop.drillDim = null;
      }
    },
    save(){
      let comp = this.comp;
      if(this.prop.usedrill === true){
        let dim = this.dims.filter(m=>m.alias === this.prop.drillDim)[0];
        if(!comp.drillDim){
          comp.drillDim = [];
        }
        comp.drillDim[0] = {name:dim.dimdesc,code:dim.alias,type:dim.type,tableColKey:dim.tableColKey,tableColName:dim.tableColName,dimord:dim.dimord,colname:dim.colname,fromCol:dim.fromCol};
      }else{
        delete comp.drillDim;
      }
      this.show = false;
      this.$parent.setUpdate();
      this.$parent.tableView();
    }
  }
}
</script>
