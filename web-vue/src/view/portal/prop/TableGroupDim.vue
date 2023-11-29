<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="交叉表聚合维配置" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
        <span class="el-dialog__title">交叉表聚合维配置</span>
        <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
        <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
        </button>
      </template>
       <el-form :model="prop" ref="propForm" label-position="left" size="mini">
          <p class="text-warning">说明：在维度聚合类型设置为first/last时，需要设置聚合维配合使用。</p>
          <el-form-item label="启用聚合维：" label-width="120px">
              <el-switch v-model="prop.usegroupdim"></el-switch>
            </el-form-item>
            <el-form-item label="分组维度：" label-width="120px" v-if="prop.usegroupdim == true">
              <el-select v-model="prop.groupdim" placeholder="请选择" style="width:100%">
                <el-option
                v-for="item in cols"
                :key="item.alias"
                :label="item.name"
                :value="item.alias">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="排序方式：" label-width="120px" v-if="prop.usegroupdim == true">
              <el-radio-group v-model="prop.grouporder" size="mini">
                <el-radio label="ASC" border>升序</el-radio>
                <el-radio label="DESC" border>降序</el-radio>
              </el-radio-group>
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
          usegroupdim:false,
          groupdim:null,
          grouporder:"ASC",
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
            cols.push({id:id, name:name, alias:alias});
          }
          this.cols = cols;
          this.dims = dims;
        }
      });
    },
    openDailog(comp){
      this.show = true;
      this.comp = comp;
      this.queryTableDims();
      if(comp.groupDto){
        this.prop.groupdim = comp.groupDto.alias;
        this.prop.grouporder = comp.groupDto.order;
        this.prop.usegroupdim = true;
      }else{
        this.prop.usegroupdim = false;
      }
    },
    save(){
      let comp = this.comp;
      var col = this.prop.groupdim;
      let p = this.prop;
      if(this.prop.usegroupdim == true){
        $(this.dims).each(function (a, b) {
          if(b.alias === col){
            let o = {col:b.colname, alias: b.alias, order: p.grouporder};
            comp.groupDto = o;
            return false;
          }
        });
      } else{
        delete comp.groupDto;
      }

      this.show = false;
      this.$parent.setUpdate();
      this.$parent.tableView();
    }
  }
}
</script>
