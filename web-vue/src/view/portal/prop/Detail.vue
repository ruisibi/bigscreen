<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-form :model="prop" ref="propForm" label-position="left" size="mini">
      <el-collapse v-model="activeName" accordion>
        <el-collapse-item title="组件属性" name="1" v-if="useIn === 'report' || useIn === 'dashboard'">
          <boxProperties ref="boxPropForm1" :comp="comp" :useIn="useIn" :showCompProp="true" :showTitleProp="false"></boxProperties>
        </el-collapse-item>
        <el-collapse-item title="标题属性" name="2" v-if="useIn === 'report' || useIn === 'dashboard'">
          <boxProperties ref="boxPropForm2" :comp="comp" :useIn="useIn" :showCompProp="false" :showTitleProp="true"></boxProperties>
        </el-collapse-item>
        <el-collapse-item title="详情页属性" name="3">
          <el-form-item label="图层名称：" label-width="110px" v-if="useIn==='bigscreen'">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
          <el-form-item label="表格背景色：" label-width="210px">
            <el-color-picker v-model="prop.tableBgcolor" @change="changevalue('tableBgcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="鼠标悬停颜色：" label-width="210px">
            <el-color-picker v-model="prop.tableMousecolor" @change="changevalue('tableMousecolor')"></el-color-picker>
          </el-form-item>
           <el-form-item label="每列显示指标：" label-width="120px">
              <el-slider v-model="prop.colSize" :max="10" :min="1" @change="changevalue('colSize')"></el-slider>
           </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-input-number v-model="prop.size" :min="9" :precision="0" controls-position="right" @change="changevalue('size')"></el-input-number>
          </el-form-item>
          <el-form-item label="颜色：" label-width="210px">
            <el-color-picker v-model="prop.color" @change="changevalue('color')"></el-color-picker>
          </el-form-item>
           <el-form-item label="数据字体大小：" label-width="110px">
            <el-input-number v-model="prop.kpisize" :min="9" :precision="0" controls-position="right" @change="changevalue('kpisize')"></el-input-number>
          </el-form-item>
          <el-form-item label="数据颜色：" label-width="210px">
            <el-color-picker v-model="prop.kpicolor" @change="changevalue('kpicolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="换行显示：" label-width="200px">
            <el-switch v-model="prop.brdata" @change="changevalue('brdata')"></el-switch>
          </el-form-item>
           <el-form-item label="导出方式：" label-width="120px">
              <el-select v-model="prop.exportType" @change="changevalue('exportType')" :clearable="true" placeholder="对Word/Pdf有效">
              <el-option
                v-for="item in opt.exportTypes"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
           </el-form-item>
          <el-form-item label="数据集：" label-width="110px">
            <div align="right" :title="comp.comp.tname">{{ comp.comp ? comp.comp.tname : "" }}</div>
          </el-form-item>
          <el-form-item label="清除数据集：" label-width="200px">
            <el-switch v-model="prop.clear" @change="changevalue('clear')"></el-switch>
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="位置信息" name="pos" v-if="useIn === 'bigscreen'">
            <posProperties :comp="comp" ref="posPropForm"></posProperties>
       </el-collapse-item>
       <el-collapse-item title="自动刷新" name="22" v-if="useIn === 'bigscreen' || useIn === 'dashboard'">
          <compAutoFlush :comp="comp" ref="autoFlushForm"/>
       </el-collapse-item>
      </el-collapse>
    </el-form>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import boxProperties from '@/components/toolbox/BoxProperties'
import posProperties from '@/components/toolbox/BsCompProperties'
import compAutoFlush from '@/components/toolbox/CompAutoFlush'

export default {
  components:{
    boxProperties, posProperties,compAutoFlush
  },
  props:{
      comp:{
        type:Object,
        required:true
      },
       //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
  },
  data(){
    return {
      activeName:this.useIn === 'report' || this.useIn === 'dashboard'?"1":"3",
      prop:{
        title:null, //图层名称
        tableBgcolor:null,
        tableMousecolor:null,
        colSize:null,
        exportType:null,
        clear:null,
        size:null,
        color:null,
        brdata:false, //换行显示
        kpicolor: null,
        kpisize: null,
      },
      opt:{
        exportTypes:[{id:"table", name:"表格"},{id:"paragraph", name:"段落"},]
      }
    }
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    init(){
      if(this.useIn === 'report' || this.useIn === 'dashboard'){
        this.$refs['boxPropForm1'].init();
        this.$refs['boxPropForm2'].init();
      }else if(this.useIn === 'bigscreen'){
        this.$refs['posPropForm'].init();
        this.prop.title = this.comp.title;
      }
      if(this.useIn === 'bigscreen' || this.useIn === 'dashboard'){
        this.$refs['autoFlushForm'].init();
      }
       //回写 tstyle
      if(this.comp.tstyle){
        for(let item in this.comp.tstyle){
          this.prop[item] = this.comp.tstyle[item];
        }
      }
      let c = this.comp.comp;
      this.prop.colSize = c.colSize || 2;
      this.prop.exportType = c.exportType;
    },
    compRender(){
      this.$parent.$parent.$forceUpdate();
      var o = this.$parent.$parent.$refs['optarea'];
      o.$forceUpdate();
      o.$refs['mv_'+this.comp.id].$forceUpdate();
    },
    detailView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].detailView();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    changevalue(col){
      if(!this.comp.tstyle){
        this.comp.tstyle = {};
      }
      let v = this.prop[col];
      if(col === "clear"){
          utils.delCompData(this.comp);
         this.clearData();
         this.$notify.success("数据清除成功");
      }else if(col == 'tableMousecolor' || col == 'tableBgcolor' || col == 'size' || col == 'color' || col == 'brdata' ||
        col =='kpicolor' || col == 'kpisize'){
				if(this.comp.tstyle[col] == v){ //值未改变
					return;
				}
				this.comp.tstyle[col] = v;
				this.detailView();
			}else if(col === 'colSize'){
        this.comp.comp.colSize = v;
        this.detailView();
      }else if(col === 'exportType'){
        this.comp.comp.exportType = v;
      }else if(col === 'title'){
        this.comp.title = v;
      }
      this.setUpdate();
    },
    clearData(){
      let o = this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id];
      o.data = null;
      o.$forceUpdate();
    },
  },
  watch: {

  }
}
</script>
