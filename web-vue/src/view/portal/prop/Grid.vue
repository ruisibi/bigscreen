<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
 <div>
  <el-form :model="prop" ref="propForm" label-position="left" size="mini">
    <el-collapse v-model="activeName" accordion>
      <el-collapse-item title="组件属性" name="1" v-if="useIn === 'report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm1" :comp="comp" :useIn="useIn" :showCompProp="true" :showTitleProp="false"></boxProperties>
       </el-collapse-item>
       <el-collapse-item title="标题属性" name="2" v-if="useIn === 'report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm2" :comp="comp" :useIn="useIn" :showCompProp="false" :showTitleProp="true"></boxProperties>
       </el-collapse-item>
       <el-collapse-item title="组件属性" name="33" v-if="useIn==='bigscreen'">
          <el-form-item label="图层名称：" label-width="110px">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
          <el-form-item label="边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.compBorderStyle" @change="changevalue('compBorderStyle')" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.compBorderColor" @change="changevalue('compBorderColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.compBorderWidth" :max="10" :min="0" @change="changevalue('compBorderWidth')"></el-slider>
          </el-form-item>
       </el-collapse-item>
       <el-collapse-item title="表格属性" name="3" >
          <el-form-item label="表格背景色：" label-width="210px">
            <el-color-picker v-model="prop.tableBgcolor" @change="changevalue('tableBgcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="鼠标悬停颜色：" label-width="210px">
            <el-color-picker v-model="prop.tableMousecolor" @change="changevalue('tableMousecolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="宽度自适应：" label-width="200px">
            <el-switch v-model="prop.colwidthauto" @change="changevalue('colwidthauto')"></el-switch>
          </el-form-item>
           <el-form-item label="单元格宽度：" label-width="110px" v-if="prop.colwidthauto === false">
               <el-input-number v-model="prop.colwidth" :precision="0" placeholder="默认宽度90" controls-position="right" @change="changevalue('colwidth')"></el-input-number>
          </el-form-item>
          <el-form-item label="表格滚动：" label-width="200px">
            <el-switch v-model="prop.scroll" @change="changevalue('scroll')"></el-switch>
          </el-form-item>
          <el-form-item label="锁定列：" label-width="110px" >
             <el-select v-model="prop.lockcols" @change="changevalue('lockcols')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.lockcolsList"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="表格转置：" label-width="200px" >
            <el-switch v-model="prop.transposition" @change="changevalue('transposition')"></el-switch>
          </el-form-item>
          <el-form-item label="序号字段：" label-width="200px" >
            <el-switch v-model="prop.numberCol" @change="changevalue('numberCol')"></el-switch>
          </el-form-item>
          <el-form-item label="数据集/表：" label-width="110px">
            <div align="right" :title="comp.comp.tname">{{ comp.comp ? comp.comp.tname : "" }}</div>
          </el-form-item>
           <el-form-item label="更换数据集/表：" label-width="190px">
            <button class="btn btn-success btn-xs" type="button" @click="chgtable()">修改</button>
          </el-form-item>
          <el-form-item label="清除数据集/表：" label-width="200px">
            <el-switch v-model="prop.clear" @change="changevalue('clear')"></el-switch>
          </el-form-item>
       </el-collapse-item>
       <el-collapse-item title="表头属性" name="4" >
         <el-form-item label="隐藏表头：" label-width="200px">
            <el-switch v-model="prop.hideTitle" @change="changevalue('hideTitle')"></el-switch>
          </el-form-item>
         <el-form-item label="背景颜色" label-width="210px">
            <el-color-picker v-model="prop.headBgColor" show-alpha @change="changevalue('headBgColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体：" label-width="100px">
            <el-select v-model="prop.family" @change="changevalue('family')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.familys"
                :key="item.en"
                :label="item.ch"
                :value="item.en">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="100px">
            <el-slider v-model="prop.headFontSize" :max="46" :min="9" @change="changevalue('headFontSize')"></el-slider>
          </el-form-item>
          <el-form-item label="是否粗体：" label-width="200px">
            <el-switch v-model="prop.headTextBold" @change="changevalue('headTextBold')"></el-switch>
          </el-form-item>
          <el-form-item label="字体颜色：" label-width="210px">
            <el-color-picker v-model="prop.headTextColor" @change="changevalue('headTextColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="单元格高度：" label-width="110px">
            <el-slider v-model="prop.headcolheight" :precision="0" :max="200" :min="20" @change="changevalue('headcolheight')" ></el-slider>
          </el-form-item>
          <el-form-item label="位置：" label-width="110px">
            <el-select v-model="prop.headTextAlign" @change="changevalue('headTextAlign')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.aligns"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="上边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.headTopType" @change="changevalue('headTopType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.headTopColor" @change="changevalue('headTopColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.headTopWidth" :max="10" :min="0" @change="changevalue('headTopWidth')"></el-slider>
          </el-form-item>

          <el-form-item label="右边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.headRightType" @change="changevalue('headRightType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.headRightColor" @change="changevalue('headRightColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.headRightWidth" :max="10" :min="0" @change="changevalue('headRightWidth')"></el-slider>
          </el-form-item>

          <el-form-item label="下边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.headBottomType" @change="changevalue('headBottomType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.headBottomColor" @change="changevalue('headBottomColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.headBottomWidth" :max="10" :min="0" @change="changevalue('headBottomWidth')"></el-slider>
          </el-form-item>

          <el-form-item label="左边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.headLeftType" @change="changevalue('headLeftType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.headLeftColor" @change="changevalue('headLeftColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.headLeftWidth" :max="10" :min="0" @change="changevalue('headLeftWidth')"></el-slider>
          </el-form-item>

       </el-collapse-item>
       <el-collapse-item title="表体属性" name="5" >
          <el-form-item label="背景颜色：" label-width="210px">
            <el-color-picker v-model="prop.bodyBgColor" @change="changevalue('bodyBgColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体：" label-width="100px">
            <el-select v-model="prop.bodyfamily" @change="changevalue('bodyfamily')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.familys"
                :key="item.en"
                :label="item.ch"
                :value="item.en">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="字体颜色：" label-width="210px">
            <el-color-picker v-model="prop.bodyTextColor" @change="changevalue('bodyTextColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-slider v-model="prop.bodyFontSize" :max="46" :min="9" @change="changevalue('bodyFontSize')"></el-slider>
          </el-form-item>
          <el-form-item label="位置：" label-width="110px">
            <el-select v-model="prop.bodyTextAlign" @change="changevalue('bodyTextAlign')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.aligns"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="单元格高度：" label-width="110px">
            <el-input-number v-model="prop.bodycolheight" :max="200" :min="20" @change="changevalue('bodycolheight')" controls-position="right"></el-input-number>
          </el-form-item>
           <el-form-item label="启用隔行换色：" label-width="200px">
            <el-switch v-model="prop.bodyEnableTr" @change="changevalue('bodyEnableTr')"></el-switch>
          </el-form-item>
          <el-form-item label="隔行颜色设置：" label-width="180px" v-if="prop.bodyEnableTr === true">
            <el-color-picker v-model="prop.bodyTrColor1" @change="changevalue('bodyTrColor1')"></el-color-picker>
            <el-color-picker v-model="prop.bodyTrColor2" @change="changevalue('bodyTrColor2')"></el-color-picker>
          </el-form-item>

          <el-form-item label="上边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.bodyTopType" @change="changevalue('bodyTopType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.bodyTopColor" @change="changevalue('bodyTopColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.bodyTopWidth" :max="10" :min="0" @change="changevalue('bodyTopWidth')"></el-slider>
          </el-form-item>

          <el-form-item label="右边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.bodyRightType" @change="changevalue('bodyRightType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.bodyRightColor" @change="changevalue('bodyRightColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.bodyRightWidth" :max="10" :min="0" @change="changevalue('bodyRightWidth')"></el-slider>
          </el-form-item>

          <el-form-item label="下边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.bodyBottomType" @change="changevalue('bodyBottomType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.bodyBottomColor" @change="changevalue('bodyBottomColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.bodyBottomWidth" :max="10" :min="0" @change="changevalue('bodyBottomWidth')"></el-slider>
          </el-form-item>

          <el-form-item label="左边框：" label-width="110px">
            <el-row>
              <el-col :span="16">
              <el-select v-model="prop.bodyLeftType" @change="changevalue('bodyLeftType')" :clearable="true" placeholder="请选择">
                  <el-option v-for="item in opt.borderTypes"  :key="item.value"  :label="item.name" :value="item.value"/>
                </el-select>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="6">
                 <el-color-picker v-model="prop.bodyLeftColor" @change="changevalue('bodyLeftColor')"></el-color-picker>
              </el-col>
            </el-row>
            <el-slider v-model="prop.bodyLeftWidth" :max="10" :min="0" @change="changevalue('bodyLeftWidth')"></el-slider>
          </el-form-item>
       </el-collapse-item>
       <el-collapse-item title="列属性" name="col" >
          <el-form-item label="选择列：" label-width="110px">
          <el-select v-model="prop.colselect" @change="changevalue('colselect')" placeholder="请选择">
            <el-option v-for="item in opt.cols"  :key="item.value"  :label="item.name" :value="item.value"/>
          </el-select>
          </el-form-item>
          <el-form-item label="背景颜色：" label-width="210px">
            <el-color-picker v-model="prop.colBgColor" @change="changevalue('colBgColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体：" label-width="100px">
            <el-select v-model="prop.colfamily" @change="changevalue('colfamily')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.familys"
                :key="item.en"
                :label="item.ch"
                :value="item.en">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="字体颜色：" label-width="210px">
            <el-color-picker v-model="prop.colTextColor" @change="changevalue('colTextColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-slider v-model="prop.colFontSize" :max="46" :min="9" @change="changevalue('colFontSize')"></el-slider>
          </el-form-item>
          <el-form-item label="位置：" label-width="110px">
            <el-select v-model="prop.colTextAlign" @change="changevalue('colTextAlign')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.aligns"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="宽度：" label-width="110px">
               <el-input-number v-model="prop.colwidth2" controls-position="right" :precision="0"  @change="changevalue('colwidth2')"></el-input-number>
            </el-form-item>
       </el-collapse-item>
      <el-collapse-item title="分页属性" name="6">
            <el-form-item label="禁用分页：" label-width="200px">
              <el-switch v-model="prop.isnotfy" @change="changevalue('isnotfy')"></el-switch>
            </el-form-item>
            <template v-if="!(prop.isnotfy === true)">
            <el-form-item label="每页显示条数：" label-width="110px">
               <el-input-number v-model="prop.pageSize" controls-position="right" :precision="0"  @change="changevalue('pageSize')"></el-input-number>
            </el-form-item>
            </template>
            <el-form-item label="背景颜色：" label-width="210px">
              <el-color-picker v-model="prop.fyBgColor" @change="changevalue('fyBgColor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="字体颜色：" label-width="210px">
              <el-color-picker v-model="prop.fyTextColor" @change="changevalue('fyTextColor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="字体大小：" label-width="110px">
              <el-slider v-model="prop.fyFontSize" :max="46" :min="9" @change="changevalue('fyFontSize')"></el-slider>
            </el-form-item>
      </el-collapse-item>
       <el-collapse-item title="滚动条样式" name="scroll">
         <scrollStyle :comp="comp" ref="scrollForm"/>
       </el-collapse-item>
      <el-collapse-item title="位置信息" name="pos" v-if="useIn === 'bigscreen'">
            <posProperties :comp="comp" ref="posPropForm"></posProperties>
       </el-collapse-item>
       <el-collapse-item title="自动刷新" name="22" v-if="useIn === 'bigscreen' || useIn === 'dashboard'">
          <compAutoFlush :comp="comp" ref="autoFlushForm"/>
       </el-collapse-item>
    </el-collapse>
  </el-form>
   <modifyCompTable ref="modifyCompTableForm"></modifyCompTable>
  </div>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import boxProperties from '@/components/toolbox/BoxProperties'
import posProperties from '@/components/toolbox/BsCompProperties'
import scrollStyle from '@/components/toolbox/Scroll'
import compAutoFlush from '@/components/toolbox/CompAutoFlush'
import * as bsUtils from '@/view/bigscreen/bsUtils'
import modifyCompTable from '@/components/toolbox/ModifyCompTable'
import { util } from 'echarts'

export default {
  components:{
    boxProperties, posProperties,compAutoFlush, modifyCompTable, scrollStyle
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
      userOpers:{
       type:Array,
       required : true,
     }
  },
  data(){
    return {
      activeName:this.useIn === 'report' || this.useIn === 'dashboard'?"1":"33",
      prop:{
        title:null, //图层名称
        fyBgColor:null,
        fyTextColor:null,
        fyFontSize:13,
        isnotfy:false,
        pageSize:10,
        tableBgcolor:null,
        tableMousecolor:null,
        colwidthauto:false,
        colwidth:90,
        scroll:false,
        headBgColor:this.useIn === 'bigscreen'?"#093247":"#f5f5f6",
        headFontSize:13,
        headTextBold:true,
        headTextColor:null,
        headcolheight:30,
        headTextAlign:null,
        headTopType:null,
        headTopWidth:null,
        headTopColor:null,
        headRightWidth:null,
        headRightType:null,
        headRightColor:null,
        headBottomColor:null,
        headBottomType:null,
        headBottomWidth:null,
        headLeftWidth:null,
        headLeftType:null,
        headLeftColor:null,
        bodyBgColor:null,
        bodyFontSize:13,
        bodyTextColor:null,
        bodyTextAlign:"center",
        bodyEnableTr:false,
        bodyTrColor2:"#F5F5F6",
        bodyTrColor1:"#ffffff",
        bodyLeftColor:null,
        bodyLeftType:null,
        bodyLeftWidth:null,
        bodyBottomColor:null,
        bodyBottomType:null,
        bodyBottomWidth:null,
        bodyRightColor:null,
        bodyRightType:null,
        bodyRightWidth:null,
        bodyTopColor:null,
        bodyTopType:null,
        bodyTopWidth:null,
        bodycolheight: 30,
        compBorderColor:"#e7eaec",
        compBorderWidth:1,
        compBorderStyle:"solid",
        hideTitle:false,
        transposition:false,
        scrollWidth: null,
        lockcols:null,
        numberCol: false,
        family:null,
        bodyfamily:null,
        colselect:null,
        colTextAlign: null,
        colFontSize : null,
        colTextColor : null,
        colfamily:null,
        colBgColor:null,
        colwidth2:null,
      },
      opt:{
        borderTypes:[{value:"solid", name:"实线"}, {value:"dashed", name:"虚线"}, {value:"dotted", name:"点划线"}],
        aligns:[{value:"left", name:"居左"},{value:"center", name:"居中"},{value:"right", name:"居右"}],
        lockcolsList: [{value:1, name:"前一列"},{value:2, name:"前二列"},{value:3, name:"前三列"},{value:4, name:"前四列"}],
        familys:bsUtils.fonts,
        cols:[],
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
      this.$refs['scrollForm'].init();
      let p = this.prop;
      let c = this.comp.comp;

      if(c.pageSize){
        p.pageSize = c.pageSize;
      }
      p.isnotfy = c.isnotfy;
      //回写 tstyle
      if(this.comp.tstyle){
        for(let item in this.comp.tstyle){
          p[item] = this.comp.tstyle[item];
        }
      }
      //处理colwidth
      if(c.colwidth === 'auto'){
        p.colwidth = 90;
        p.colwidthauto = true;
      }else{
        p.colwidthauto = false;
        p.colwidth = c.colwidth;
      }
      p.numberCol = c.numberCol || false;
       p.scroll = this.comp.scroll;
       p.hideTitle = this.comp.hideTitle || false;
       p.transposition = c.transposition || false;
       p.lockcols = this.comp.lockcols;

       //设置列属性
       /**
       this.opt.cols = this.comp.comp.cols.map(m=>{
        return {value:m.id, name:m.name};
       });
        */
       let cols = [];
       $(this.comp.comp.cols).each((a, b)=>{
         if(b.children){
            $(b.children).each((c, d)=>{
               cols.push({value:d.id, name: d.name});
            });
         }else{
           cols.push({value:b.id, name:b.name});
         }
      });
      this.opt.cols = cols;
      if(this.opt.cols.length > 0 ){
         p.colselect = this.opt.cols[0].value;
      }
       //回写列值
       if(this.comp.comp.cols ){
        let cc = utils.getGridColById(null, this.comp.comp.cols, true);
        p.colTextAlign = cc.align;
        p.fmt = cc.fmt;
        p.colwidth2 = cc.colwidth;
        p.colFontSize = cc.colFontSize;
        p.colTextColor = cc.colTextColor;
        p.colfamily = cc.colfamily;
        p.colBgColor = cc.colBgColor;
       }
    },
    gridView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].gridView();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    compRender(){
      this.$parent.$parent.$forceUpdate();
      var o = this.$parent.$parent.$refs['optarea'];
      o.$forceUpdate();
      o.$refs['mv_'+this.comp.id].$forceUpdate();
    },
    optareaRender(){
      this.$parent.$parent.$refs['optarea'].$forceUpdate();
    },
    changevalue(prop){
      if(!this.comp.tstyle){
        this.comp.tstyle = {};
      }
      let c = this.comp.comp;
      let v = this.prop[prop];
      let col = prop;
      if(prop === 'pageSize' || prop === 'isnotfy'){
        c[prop] = v;
        this.gridView();
      }else if(prop === 'title'){
        this.comp.title = v;
      }else if(prop==='scroll'){
         this.comp.scroll = v;
         this.compRender();
      }else if(prop ==='clear'){
         utils.delCompData(this.comp);
         this.clearData();
         this.$notify.success("数据清除成功");
      }else if(col == 'tableMousecolor' || col == 'headBgColor' || col == 'headTextBold' || col == 'headTextColor' || col == 'headTopWidth' || col == 'headTopType' ||
				col == 'headTopColor' || col == 'headRightWidth' || col == 'headRightType' || col == 'headRightColor' || col == 'tableBgcolor' ||
				col == 'headBottomWidth' || col == 'headBottomType' || col == 'headBottomColor' || col == 'headLeftWidth' || col == 'headLeftType' || col =='headLeftColor' ||
				col == 'bodyBgColor' || col =='bodyTextColor' || col == 'bodyTopWidth' || col =='bodyTopType' || col =='bodyTopColor' || col == 'bodyRightWidth' || col == 'bodyRightType' ||
				col == 'bodyRightColor' || col =='bodyBottomWidth' || col == 'bodyBottomType' || col =='bodyBottomColor' || col =='bodyLeftWidth' || col =='bodyLeftType' ||
				col == 'bodyLeftColor' || col =='fyBgColor' || col == 'fyTextColor' || col =='headFontSize' || col == 'bodyFontSize' || col == 'fyFontSize' ||
        col == 'bodyEnableTr' || col == 'headTextAlign' || col == 'bodyTextAlign'||col==='bodycolheight'||col==='headcolheight' || col === 'bodyTrColor1' || col == 'bodyTrColor2' ||
        col === 'compBorderColor' || col === 'compBorderWidth' || col === 'compBorderStyle' || col =='bodyfamily' || col =='family' ){
        if(this.comp.tstyle[col] == v){ //值未改变
          return;
        }
        this.comp.tstyle[col] = v;

        if(col === 'compBorderColor' || col === 'compBorderWidth' || col === 'compBorderStyle'){
          this.optareaRender();
        }else{
          this.compRender();
        }
      }else if(col == 'colwidthauto'){
          if(v == true){
            c.colwidth = 'auto';
          }else{
            c.colwidth = 90;
          }
          this.gridView();
      }else if(col === 'colwidth' || col == 'numberCol'){
        c[col] = v;
        this.gridView();
      }else if(col == 'lockcols'){
        this.comp.lockcols = v;
         this.compRender();
      }else if(col == 'transposition'){
        c.transposition = v;
        this.compRender();
      }else if(col == 'hideTitle'){
        this.comp.hideTitle = v;
        this.compRender();
      }else if(col == 'colselect'){
         let ccc = utils.getGridColById(this.prop.colselect, this.comp.comp.cols);
         this.prop.colFontSize = ccc.colFontSize;
         this.prop.colTextColor = ccc.colTextColor;
         this.prop.colfamily = ccc.colfamily;
         this.prop.colBgColor = ccc.colBgColor;
         this.prop.colTextAlign = ccc.align;
         this.prop.colwidth2 = ccc.colwidth;
      }else if(col == 'colFontSize' || col =='colTextColor' || col == 'colfamily' || col == 'colBgColor' || col =='colTextAlign' || col == 'colwidth2'){
        let ccc = utils.getGridColById(this.prop.colselect, this.comp.comp.cols);
        if(col == 'colTextAlign'){
          ccc.align = v;
          this.gridView();
        }else if(col == 'colwidth2'){
          ccc.colwidth = v;
          this.gridView();
        }else{
          ccc[col] = v;
        }
      }else{
        console.log("字段 " + prop + " 未定义处理对象。");
      }
      this.setUpdate();
    },
    clearData(){
      let o = this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id];
      o.data = null;
      o.$forceUpdate();
    },
    chgtable(){
      if(this.comp.comp && this.comp.comp.tid){
        this.$refs['modifyCompTableForm'].showDialog((table)=>{
          this.comp.comp.tid = table.tableId;
          this.comp.comp.tname = table.tableName;
          this.comp.comp.tincome = table.income;
          this.setUpdate();
          this.gridView();
        });
      }
    }
  },
  watch: {

  }
}
</script>

<style lang="less" scoped>

</style>
