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
       <el-collapse-item title="组件属性" name="33" v-if="useIn === 'bigscreen'">
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
      <el-collapse-item title="交叉表属性" name="3">
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
               <el-input-number v-model="prop.colwidth" placeholder="默认宽度90" :precision="0" controls-position="right" @change="changevalue('colwidth')"></el-input-number>
          </el-form-item>
          <el-form-item label="表格滚动：" label-width="200px">
            <el-switch v-model="prop.scroll" @change="changevalue('scroll')"></el-switch>
          </el-form-item>
          <el-form-item label="禁止合并行单元格：" label-width="200px">
            <el-switch v-model="prop.mergeRow" @change="changevalue('mergeRow')"></el-switch>
          </el-form-item>
          <el-form-item label="折叠父子维度：" label-width="200px">
            <el-switch v-model="prop.foldpcdim" @change="changevalue('foldpcdim')"></el-switch>
          </el-form-item>
          <el-form-item label="序号字段：" label-width="200px"> <!-- 原排名字段 -->
            <el-switch v-model="prop.ranking" @change="changevalue('ranking')"></el-switch>
          </el-form-item>
          <el-form-item label="锁定列字段：" label-width="200px">
            <el-switch v-model="prop.lockrow" @change="changevalue('lockrow')"></el-switch>
          </el-form-item>
          <el-form-item label="启用指标定制：" label-width="200px" v-if="useIn === 'dashboard'">
            <el-switch v-model="prop.kpiCustomized" @change="changevalue('kpiCustomized')"></el-switch>
          </el-form-item>
          <el-form-item label="交叉表下钻：" label-width="190px">
              <button class="btn btn-success btn-xs" type="button" @click="crossdirll()"><i class="fa fa-wrench"></i>设置</button>
          </el-form-item>
           <el-form-item label="聚合维设置：" label-width="190px">
              <button class="btn btn-success btn-xs" type="button" @click="crossGroupDim()"><i class="fa fa-wrench"></i>设置</button>
          </el-form-item>
          <el-form-item label="数据集/表：" label-width="110px">
            <div align="right" :title="comp.comp.tname">{{ comp.comp ? comp.comp.tname : "" }}</div>
          </el-form-item>
          <el-form-item label="更换数据集/表：" label-width="190px">
            <button class="btn btn-success btn-xs" type="button" @click="chgtable()"><i class="fa fa-wrench"></i>修改</button>
          </el-form-item>
          <el-form-item label="清除数据集/表：" label-width="200px">
            <el-switch v-model="prop.clear" @change="changevalue('clear')"></el-switch>
          </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="表头属性" name="4" >
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
            <el-slider v-model="prop.headcolheight" :max="100" :min="20" @change="changevalue('headcolheight')"></el-slider>
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
          <!--
          <el-form-item label="作用行：" label-width="110px">
            <el-input v-model="prop.bodyStyleScope" placeholder="(1-5或2,5,6)" @change="changevalue('bodyStyleScope')"/>
          </el-form-item>
          -->
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
      <el-collapse-item title="列属性" name="6" >
         <el-form-item label="选择列：" label-width="110px">
            <el-select v-model="prop.selectCols" @change="changevalue('selectCols')" placeholder="请选择">
              <el-option v-for="item in opt.tableCols"  :key="item.value"  :label="item.text" :value="item.value"/>
            </el-select>
         </el-form-item>
         <el-form-item label="背景颜色：" label-width="210px">
            <el-color-picker v-model="prop.colBgcolor" @change="changevalue('colBgcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体颜色：" label-width="210px">
            <el-color-picker v-model="prop.colFontcolor" @change="changevalue('colFontcolor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-slider v-model="prop.colFontsize" :max="46" :min="9" @change="changevalue('colFontsize')"></el-slider>
          </el-form-item>
          <el-form-item label="位置：" label-width="110px">
            <el-select v-model="prop.colTextPos" @change="changevalue('colTextPos')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opt.aligns"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="分页属性" name="pageInfo">
           <el-form-item label="启用分页：" label-width="200px">
              <el-switch v-model="prop.usefy" @change="changevalue('usefy')"></el-switch>
            </el-form-item>
            <template v-if="prop.usefy === true">
              <el-form-item label="每页显示条数：" label-width="110px">
                <el-input-number v-model="prop.pageSize" controls-position="right" :precision="0"  @change="changevalue('pageSize')"></el-input-number>
              </el-form-item>
            </template>
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
  <tableDirll ref="tableDirllForm"></tableDirll>
  <tableGroupDim ref="tableGroupDimForm"/>
  <modifyCompTable ref="modifyCompTableForm"></modifyCompTable>
  </div>
</template>

<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import boxProperties from '@/components/toolbox/BoxProperties'
import * as utils from '@/view/portal/Utils'
import tableDirll from './TableDirll'
import tableGroupDim from './TableGroupDim'
import posProperties from '@/components/toolbox/BsCompProperties'
import compAutoFlush from '@/components/toolbox/CompAutoFlush'
import scrollStyle from '@/components/toolbox/Scroll'
import modifyCompTable from '@/components/toolbox/ModifyCompTable'
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  components:{
    boxProperties, tableDirll, tableGroupDim, posProperties, compAutoFlush, modifyCompTable, scrollStyle
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
      activeName: this.useIn === 'report' || this.useIn === 'dashboard'?"1":"33",
      prop:{
        title:null, //图层名称
        tableBgcolor:null,
        tableMousecolor:null,
        colwidthauto:false,
        colwidth:90,
        scroll:false,
        mergeRow:false,
        foldpcdim:false,
        clear:null,
        headBgColor:null,
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
        bodyTextAlign:null,
        bodyEnableTr:false,
        bodyTrColor2:"#F5F5F6",
        bodyTrColor1:"#ffffff",
        bodyLeftColor:null,
        bodyLeftType:null,
        bodyLeftWidth:null,
        bodyBottomColor:null,
        bodyBottomType: null,
        bodyBottomWidth:null,
        bodyRightColor:null,
        bodyRightType:null,
        bodyRightWidth:null,
        bodyTopColor:null,
        bodyTopType:null,
        bodyTopWidth:null,
        bodycolheight: 30,
        bodyStyleScope:null,
        selectCols:null,
        colBgcolor:null,
        colFontcolor:null,
        colFontsize:13,
        colTextPos:null,
        compBorderColor:"#e7eaec",
        compBorderWidth:1,
        compBorderStyle:"solid",
        ranking:false,
        kpiCustomized:false,
        lockrow: false,
        usefy:false,
        pageSize:10,
        family: null,
        bodyfamily: null,
      },
      opt:{
        borderTypes:[{value:"solid", name:"实线"}, {value:"dashed", name:"虚线"}, {value:"dotted", name:"点划线"}],
        aligns:[{value:null, name:"默认"}, {value:"left", name:"居左"},{value:"center", name:"居中"},{value:"right", name:"居右"}],
        tableCols:[],  //交叉表列字段
        familys:bsUtils.fonts,
      },
      cols:[]
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
      let c = this.comp;

       //回写 tstyle
      if(this.comp.tstyle){
        for(let item in this.comp.tstyle){
          p[item] = this.comp.tstyle[item];
        }
      }
      //处理colwidth
      if(c.comp.colwidth === 'auto'){
        p.colwidth = 90;
        p.colwidthauto = true;
      }else{
        p.colwidthauto = false;
        p.colwidth = c.comp.colwidth;
      }
      let cc = this.comp.comp;
      p.foldpcdim = cc.foldpcdim;
      p.mergeRow = cc.mergeRow;
      p.scroll = this.comp.scroll;
      p.ranking = cc.ranking;
      p.kpiCustomized = c.kpiCustomized;
      p.lockrow = c.lockrow;
      p.usefy = c.usefy;
      p.pageSize = c.pageSize;
      this.initTableCols();
    },
    //初始化表格列字段
    initTableCols(){
      let comp = this.comp.comp;
       var currCols = [];
       //序号字段
       if(comp.ranking == true){
         currCols.push({text:"序号", value:0});
       }
      if(comp.rowHeads) {
        $(comp.rowHeads).each(function(a, b){
          let idx = a;
          if(comp.ranking == true){
            idx += 1;
          }
          currCols.push({text:b.desc, value:idx});
        });
      }
      if(comp.cols){
        utils.loopDims(comp.cols, function(a, b, c){
          if(!a.children || a.children.length === 0){
            currCols.push({text:a.desc, value:a.id});
           if(a.type == 'dim'){  //判断是否有合计项
              let dim = comp.dims.filter(m=>m.id == a.match)[0];
              if(dim.issum == 'y'){
                currCols.push({text:a.desc+"-聚合", value:a.id+'_agg'});
              }
           }
          }
        });
      }
      if(currCols.length > 0) {
        //curTmpInfo.selectCols = currCols[0].value
        if(comp.ranking == true){
          this.prop.selectCols = 0;
          let colStyle = this.comp.rankStyle || {};
          this.setColStyle(colStyle.colStyleDto);
        }else{
          this.prop.selectCols = currCols[0].value;
          let colStyle = this.findCurrColStyle(this.prop.selectCols);
          this.setColStyle(colStyle);
        }
      }
      this.opt.tableCols = currCols;
    },

    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    tableView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].tableView();
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
    crossdirll(){
      let comp = this.comp.comp;
      if(comp.rows.length !== 1 ){
        utils.msginfo("交叉表行标签有且只有一个维度的时候才能配置交叉表钻取。");
        return;
      }
      this.$refs['tableDirllForm'].openDailog(this.comp.comp);
    },
    changevalue(prop){
      if(!this.comp.tstyle){
        this.comp.tstyle = {};
      }
      let c = this.comp.comp;
      let v = this.prop[prop];
      let col = prop;
      //样式控制
      if(col == 'tableMousecolor' || col == 'headBgColor' || col == 'headTextBold' || col == 'headTextColor' || col == 'headTopWidth' || col == 'headTopType' ||
				col == 'headTopColor' || col == 'headRightWidth' || col == 'headRightType' || col == 'headRightColor' || col == 'tableBgcolor' ||
				col == 'headBottomWidth' || col == 'headBottomType' || col == 'headBottomColor' || col == 'headLeftWidth' || col == 'headLeftType' || col =='headLeftColor' ||
				col == 'bodyBgColor' || col =='bodyTextColor' || col == 'bodyTopWidth' || col =='bodyTopType' || col =='bodyTopColor' || col == 'bodyRightWidth' || col == 'bodyRightType' ||
				col == 'bodyRightColor' || col =='bodyBottomWidth' || col == 'bodyBottomType' || col =='bodyBottomColor' || col =='bodyLeftWidth' || col =='bodyLeftType' ||
				col == 'bodyLeftColor' || col =='fyBgColor' || col == 'fyTextColor' || col =='headFontSize' || col == 'bodyFontSize' || col == 'fyFontSize' ||
        col == 'bodyEnableTr' || col == 'headTextAlign' || col == 'bodyTextAlign'||col==='bodycolheight'||col==='headcolheight' || col === 'bodyTrColor1' || col == 'bodyTrColor2' ||
        col === 'compBorderColor' || col === 'compBorderWidth' || col === 'compBorderStyle' || col == 'bodyfamily' || col == 'family' ){
        if(this.comp.tstyle[col] == v){ //值未改变
          return;
        }
        this.comp.tstyle[col] = v;
        if(col === 'compBorderColor' || col === 'compBorderWidth' || col === 'compBorderStyle'){
          this.optareaRender();
        }else{
          this.compRender();
        }
      }else if(col === 'title'){
         this.comp.title = v;
      }else if(col === 'lockrow'){
        this.comp.lockrow = v;
      }else if(col === 'scroll'){
         this.comp.scroll = v;
         this.compRender();
      }else if(col =='ranking'){
        c.ranking = v;
        this.tableView();
      }else if(col === 'selectCols'){
        this.prop.selectCols = v;
        //回写值
        let colStyle = this.findCurrColStyle(v);
        this.setColStyle(colStyle);
      }else if(col ==='colBgcolor' || col ==='colFontcolor' || col ==='colFontsize' || col === 'colTextPos'){
        //确定是哪个节点
        let vv = this.prop.selectCols;
        if(isNaN(vv)){  //列标签
          let isAgg = false;
          if(vv.indexOf('_') >=0){  //说明是聚合项
            vv = vv.split("_")[0];
            isAgg = true;
          }
          utils.loopDims(this.comp.comp.cols, (a, b, c)=>{
            if(a.id === vv){
              if(isAgg == false){  //非聚合项
                if(!a.colStyleDto){
                  a.colStyleDto = {};
                }
                a.colStyleDto[col] = v;
              }else{  //聚合项
                if(!a.colStyleAggDto){
                  a.colStyleAggDto = {};
                }
                a.colStyleAggDto[col] = v;
              }
              return false;
            }
          });
        }else{ //交叉区域
          var o = null;
          if(this.comp.comp.ranking == true){
            if(vv == 0){
              if(!this.comp.rankStyle){
                this.comp.rankStyle = {};
              }
              o =  this.comp.rankStyle;
            }else{
              o = this.comp.comp.rowHeads[vv - 1];
            }
          }else{
             o = this.comp.comp.rowHeads[vv];
          }
          if(!o.colStyleDto){
            o.colStyleDto = {};
          }
          o.colStyleDto[col] = v;
        }
        this.compRender();
      }else if(col == 'colwidthauto'){
          if(v == true){
            c.colwidth = 'auto';
          }else{
            c.colwidth = 90;
          }
          this.tableView();
      }else if(col === 'colwidth' || col === 'mergeRow'){
        c[col] = v;
        this.tableView();
      }else if(col === 'foldpcdim'){
        if(c.rows && c.rows.length == 1 && c.rows[0].type == 'dim'){
          let dim = c.dims.filter(m=>m.id === c.rows[0].match)[0];
          if(dim.ispcdim == 'y'){
            c[col] = v;
            this.tableView();
          }else{
            this.$notify.error("无父子维度");
            this.prop[col] = false;
          }
        }else{
          this.$notify.error("无父子维度");
          this.prop[col] = false;
        }
      }else if(col ==='clear'){
         utils.delCompData(this.comp);
         this.clearData();
         this.$notify.success("数据清除成功");
      }else if(col === 'kpiCustomized'){
        this.comp.kpiCustomized = v;
        this.optareaRender();
      }else if(col === 'usefy' || col === 'pageSize'){
        this.comp[col] = v;
        if(col === 'pageSize'){
          this.tableView();
        }
      }
    },
    setColStyle(colStyle){
        if(colStyle && colStyle.colBgcolor){
          this.prop.colBgcolor = colStyle.colBgcolor;
        }else{
          this.prop.colBgcolor = null;
        }
        if(colStyle && colStyle.colFontcolor){
          this.prop.colFontcolor = colStyle.colFontcolor;
        }else{
          this.prop.colFontcolor = null;
        }
        if(colStyle && colStyle.colTextPos){
          this.prop.colTextPos = (colStyle.colTextPos);
        }else{
          this.prop.colTextPos = null;
        }
        if(colStyle && colStyle.colFontsize){
         this.prop.colFontsize =  colStyle.colFontsize
        }else{
         this.prop.colFontsize = 13;
        }
    },
    findCurrColStyle(v){
      let comp = this.comp.comp;
      var ret = null;
      if (isNaN(v)) {  //列标签
        let isAgg = false;
        if(v.indexOf("_") >= 0){  //处理维度聚合项
          v = v.split("_")[0];
          isAgg = true;
        }
        utils.loopDims(comp.cols, function (a, b, c) {
          if (a.id === v) {
            if(isAgg == true){
              ret = a.colStyleAggDto;
            }else{
              ret = a.colStyleDto;
            }
            return false;
          }
        });
      } else { //交叉区域
        if(comp.ranking == true){  //有序号字段
          if(v == 0){
            if(!this.comp.rankStyle){
              this.comp.rankStyle = {};
            }
            ret = this.comp.rankStyle.colStyleDto;
          }else{
            var o = comp.rowHeads[v - 1];
            ret = o.colStyleDto;
          }
        }else{
          var o = comp.rowHeads[v];
          ret = o.colStyleDto;
        }
      }
      return ret;
    },
    crossGroupDim(){
      let comp = this.comp.comp;
      if(!comp.tid){
        utils.msginfo("组件还未配置数据。");
        return;
      }
      this.$refs['tableGroupDimForm'].openDailog(comp);
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
          $(this.comp.comp.kpiJson).each((a, b)=>{
            b.tid = table.tableId;
          });
          $(this.comp.comp.dims).each((a, b)=>{
            b.tid = table.tableId;
          });
          this.setUpdate();
          this.tableView();
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
