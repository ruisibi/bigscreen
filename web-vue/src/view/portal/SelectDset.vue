<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="选择数据表" :visible.sync="show" :close-on-click-modal="false" :append-to-body="true" custom-class="nopadding">
       <el-form :model="type" ref="tableTypeForm" label-position="left" size="mini">
         <el-row :gutter="20">
            <el-col :span="10">
              <el-select v-model="type.income" size="mini" clearable placeholder="表来源" style="width:100%">
                <el-option
                  v-for="item in opt.types"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="11">
               <el-input v-model="type.keyword" clearable size="mini" placeholder="关键字" style="width:100%">
               </el-input>
            </el-col>
            <el-col :span="3">
              <el-button @click="search()">搜索</el-button>
            </el-col>
          </el-row>

        </el-form>
      <div class="el-dialog-div">
        <div :id="divId"></div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">确 定</el-button>
        <el-button @click="show = false">取 消</el-button>
      </div>
    </el-dialog>
</template>

<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'

export default {
  components:{

  },
  props: {
			divId: {
				type: String,
				required: false,
				default:"targettables"
			},
		},
  data(){
    return {
        show:false,
        type:{
          income:null,
          keyword:null,
        },
        opt:{
          types:[
            {id:"db", name:"数据源抽取"},
             {id:"sql", name:"SQL脚本"},
             {id:"js", name:"JS脚本"},
               {id:"join", name:"表关联"},
               {id:"r2c", name:"行转列"},
                {id:"rest", name:"Rest接口"},
                {id:"custom", name:"自定义"},
            ],
        }
    }
  },
  mounted(){

  },
  computed: {

  },
  methods: {
     select(){
       this.show = true;
       this.loadData();
     },
     selectme:function(a, b){
				this.checked = a.dsetId;
			},
     save(){
        var ref = $("#"+this.divId+"").jstree(true);
        var node = ref.get_selected(true);
        if(node.length == 0){
          this.$notify.error("请选择表!");
          return;
        }
        node = node[0];
        //回写 layoutLeft 组件
        var o = {tid:node.li_attr.tid, tname:node.li_attr.tname, tnote: node.li_attr.tnote,income:node.li_attr.income};
        this.$parent.pageInfo.table = o;
        this.$parent.$refs['layoutleftForm'].tabActive = 'data-tab-3';
        this.$parent.$refs['layoutleftForm'].initTableTree();
        this.show = false;
     },
     search(){
       this.loadData();
     },
     loadData(){
       ajax({
         url:"etl/listTables.action",
         data:{income:this.type.income, keyword: this.type.keyword},
         success:(dt)=>{
           dt = dt.rows;
          for(let i=0; i<dt.length; i++){
            dt[i].id = dt[i].tableId;
            dt[i].text = dt[i].tableName + "(" + dt[i].tableNote + ")";
            dt[i].icon = 'fa fa-table';
            dt[i].li_attr = {tid: dt[i].tableId, tname:dt[i].tableName, tnote : dt[i].tableNote, income:dt[i].income};
          }
          var ref = $("#"+this.divId+"").jstree(true);
          if(ref){
            ref.destroy();
          }
          $("#"+this.divId+"").jstree({
            core:{
                data:dt
              },
              "plugins" : [
                  "wholerow"
                ]
          });
          }
       }, this);
		}
  }
}
</script>

<style lang="less" scoped>

</style>
