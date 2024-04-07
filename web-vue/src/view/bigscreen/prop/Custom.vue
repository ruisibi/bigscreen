<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<template>
    <el-form :model="prop" ref="propForm" label-position="left" size="mini">
      <el-collapse v-model="activeName" accordion>
        <el-collapse-item title="组件属性" name="1">
           <el-form-item label="图层名称：" label-width="100px">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
         <el-form-item label="实现：" label-width="100px">
            <el-select v-model="prop.impl" @change="changevalue('impl')" :clearable="true" placeholder="请选择">
                <el-option
                v-for="item in opt.impls"
                :key="item.value"
                :label="item.name"
                :value="item.value">
                </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="数据集/表：" label-width="100px">
              <div align="right" :title="comp.comp.tname">{{ comp.comp ? comp.comp.tname : "" }}</div>
            </el-form-item>
            <el-form-item label="更换数据集/表：" label-width="210px">
            <button class="btn btn-success btn-xs" type="button" @click="chgtable()">修改</button>
          </el-form-item>
            <el-form-item label="清除数据集/表：" label-width="210px">
              <el-switch v-model="prop.clear" @change="changevalue('clear')"></el-switch>
            </el-form-item>
        </el-collapse-item>
         <el-collapse-item title="指标引导线" name="kpiline" v-if="prop.impl == 'kpiline'">
            <el-form-item label="线条颜色：" label-width="210px">
             <el-color-picker v-model="prop.kpiLinelinecolor" @change="changevalue('kpiLinelinecolor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="指标颜色：" label-width="210px">
             <el-color-picker v-model="prop.kpiLinekpicolor" @change="changevalue('kpiLinekpicolor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="指标名称：" label-width="100px">
             <el-input v-model="prop.kpiLinekpiname" @change="changevalue('kpiLinekpiname')"></el-input>
            </el-form-item>
            <el-form-item label="格式化：" label-width="100px">
               <el-select v-model="prop.kpiLinefmt" :clearable="true" placeholder="请选择" @change="changevalue('kpiLinefmt')">
                  <el-option
                    v-for="item in opt.fmt"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
            </el-form-item>
             <el-form-item label="反转：" label-width="200px">
             <el-switch v-model="prop.kpiLineReverse" @change="changevalue('kpiLineReverse')"></el-switch>
            </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="扇形图" name="kpiline" v-if="prop.impl == 'sector'">
            <el-form-item label="指标颜色：" label-width="110px">
             <el-input placeholder="定义3个颜色，用@分割" v-model="prop.sectorKpicolor" @change="changevalue('sectorKpicolor')"></el-input>
            </el-form-item>
            <el-form-item label="背景颜色：" label-width="210px">
             <el-color-picker v-model="prop.sectorBgcolor" @change="changevalue('sectorBgcolor')"></el-color-picker>
            </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="3D地球飞线配置" name="earth3d" v-if="prop.impl == 'earth3d'">
          <el-form-item label="起始经纬度：" label-width="110px">
            <el-input placeholder="[10, 30]格式" v-model="prop.lonlatStart" @change="changevalue('lonlatStart')"></el-input>
          </el-form-item>
          <el-form-item label="终点经纬度：" label-width="110px">
            <el-input placeholder="[{lon:12, lat:45}]格式"  v-model="prop.lonlatEnd" @change="changevalue('lonlatEnd')"></el-input>
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="3D地图配置" name="map3d" v-if="prop.impl == 'map3d'">
          <el-form-item label="地图JSON：" label-width="110px">
            <el-input placeholder="JSON数据的文件名" v-model="prop.mapJsonFile" @change="changevalue('mapJsonFile')"></el-input>
          </el-form-item>
          <el-form-item label="地图中心点：" label-width="110px">
            <el-input placeholder="经纬度，逗号分隔" v-model="prop.mapCenter" @change="changevalue('mapCenter')"></el-input>
          </el-form-item>
          <el-form-item label="大小比例：" label-width="115px">
            <el-input-number placeholder="10-10000之间" :max="10000" :min="10" v-model="prop.mapRate" @change="changevalue('mapRate')"></el-input-number>
          </el-form-item>
          <el-form-item label="是否显示名称：" label-width="205px">
            <el-switch v-model="prop.showAreaName" @change="changevalue('showAreaName')"></el-switch>
          </el-form-item>
        </el-collapse-item>
         <el-collapse-item title="地图飞线配置" name="flyline" v-if="prop.impl == 'map3d'">
           <el-form-item label="起始经纬度：" label-width="110px">
            <el-input placeholder="[10, 30]格式" v-model="prop.lonlatStart" @change="changevalue('lonlatStart')"></el-input>
          </el-form-item>
          <el-form-item label="终点经纬度：" label-width="110px">
            <el-input placeholder="[{lon:12, lat:45}]格式"  v-model="prop.lonlatEnd" @change="changevalue('lonlatEnd')"></el-input>
          </el-form-item>
         </el-collapse-item>
        <el-collapse-item title="动态框配置" name="dynamicbox" v-if="prop.impl == 'dynamicBox'">
          <el-form-item label="显示方式：" label-width="130px">
            <el-radio-group v-model="prop.dynamicBoxType"  @change="changevalue('dynamicBoxType')">
              <el-radio-button label="solid">实线</el-radio-button>
              <el-radio-button label="dotted">虚线</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="边框颜色：" label-width="210px">
            <el-color-picker v-model="prop.borderColor" @change="changevalue('borderColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="滚动频率：" label-width="110px">
            <el-select v-model="prop.frequency" :clearable="true" placeholder="请选择" @change="changevalue('frequency')">
              <el-option
                v-for="item in opt.frequencys"
                :key="item.value"
                :label="item.text"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="反转滚动：" label-width="210px">
            <el-switch v-model="prop.switchMove" @change="changevalue('switchMove')"></el-switch>
          </el-form-item>
          <el-form-item label="流光颜色：" label-width="210px">
            <el-color-picker v-model="prop.flyColor" @change="changevalue('flyColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="流光数量：" label-width="115px">
            <el-input-number :max="20" :min="1" v-model="prop.flycount" @change="changevalue('flycount')"></el-input-number>
          </el-form-item>
        </el-collapse-item>
         <el-collapse-item title="位置信息" name="2">
            <posProperties :comp="comp" ref="posPropForm"></posProperties>
       </el-collapse-item>
      </el-collapse>
      <modifyCompTable ref="modifyCompTableForm"></modifyCompTable>
    </el-form>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import boxProperties from '@/components/toolbox/BoxProperties'
import posProperties from '@/components/toolbox/BsCompProperties'
import resourceSelect from '@/components/toolbox/ResourceSelect'
import modifyCompTable from '@/components/toolbox/ModifyCompTable'
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  components:{
    boxProperties, posProperties, resourceSelect, modifyCompTable
  },
  props:{
      comp:{
        type:Object,
        required:true
      },
      userOpers: {
        type: Array,
        required: true,
        default:[]
      },
  },
  data(){
    return {
      activeName: "1",
      prop:{
         title:null,
         impl: null,
         clear: false,

         kpiLinelinecolor:null,
         kpiLinekpicolor:null,
         kpiLinekpiname:null,
         kpiLinefmt:null,
         kpiLineReverse:false,

         sectorKpicolor:null,
         sectorBgcolor:null,

        lonlatStart:null,
        lonlatEnd: null,

        mapJsonFile: null,
        mapCenter:null, //地图中心经纬度
        mapRate: 550, //地图显示比例
        showAreaName: false,

        dynamicBoxType: null,
        borderColor: null,
        frequency: null, //频率
        switchMove: null,
        flyColor: null,
        flycount: null,
      },
      opt:{
        impls:[{value:"kpiline", name:"指标引导线"}, {value:"sector", name:"扇形图"},
        {value:"earth3d", name:"3D地球"}, {value:"map3d", name:"3D地图"}, {value:"pyramid3d", name:"3D金字塔"}, {value:"dynamicBox", name:"动态框"}],
        fmt:utils.fmtJson,
        frequencys: [{value:0.05, text:"慢速"}, {value:0.1, text:"适中"}, {value:0.2, text:"快速"}]
      }
    }
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    init(){
        this.$refs['posPropForm'].init();
        this.prop.title = this.comp.title;
        this.prop.impl = this.comp.impl;
        if(this.comp.style){
          for(let s in this.comp.style){
            this.prop[s] = this.comp.style[s];
          }
        }else{

        }
    },
    compRender(){
      var o = this.$parent.$parent;
       o.$forceUpdate();
       o.$refs['optarea'].$forceUpdate();
       o.$refs['optarea'].$refs['mv_' + this.comp.id].$forceUpdate();
       if(o.$refs['optarea'].$refs['mv_' + this.comp.id].$refs['v_'+this.comp.id]){
        o.$refs['optarea'].$refs['mv_' + this.comp.id].$refs['v_'+this.comp.id].propChage();
       }
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    changevalue(col){
      let val = this.prop[col];
      if(!this.comp.style){
        this.comp.style = {};
      }
      let s = this.comp.style;
      if(col === 'title'){
        this.comp.title = val;
      }else if(col == 'impl'){
        this.comp.impl = val;
      }else if(col == 'clear'){
        if(val == true){
          this.delCompData();
          this.clearData();
          this.$notify.success("数据清除成功");
        }
      }else{
        bsUtils.writeOperLogs(this.userOpers, {comp:this.comp, oper:"iframe", options:{col:col, val:s[col], ts: this}});
        s[col] = val;
      }
      this.compRender();
      this.setUpdate();
    },
    //回撤操作
    goback(options){
       let col = options.col;
       let val = options.val;
       this.comp.style[col] = val;
       this.init();
       this.compRender();
    },
    delCompData(){
      this.comp.comp = {id: this.comp.id};
    },
    clearData(){
      var o = this.$parent.$parent;
      if(o.$refs['optarea'].$refs['mv_' + this.comp.id].$refs['v_'+this.comp.id]){
        o.$refs['optarea'].$refs['mv_' + this.comp.id].$refs['v_'+this.comp.id].refreshData(null);
       }
    },
    chgtable(){
      let c = this.comp;
      if(c.comp && c.comp.tid){
        this.$refs['modifyCompTableForm'].showDialog((table)=>{
          c.comp.tid = table.tableId;
          c.comp.tname = table.tableName;
          c.comp.tincome = table.income;
          if(c.comp.kpi){
            c.comp.kpi.tid = table.tableId;
          }
          if(c.comp.dim && c.comp.dim.tid){
            c.comp.dim.tid = table.tableId;
          }
          this.compRender();
          this.setUpdate();
        });
      }
    },
  },
  watch: {

  }
}
</script>
