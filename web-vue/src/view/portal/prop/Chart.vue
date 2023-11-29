<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  <el-form :model="prop" ref="propForm" label-position="left" size="mini">
    <el-collapse v-model="activeName" accordion>
      <el-collapse-item title="组件属性" name="1" v-if="useIn==='report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm1" :comp="comp" :useIn="useIn" :showCompProp="true" :showTitleProp="false"></boxProperties>
       </el-collapse-item>
       <el-collapse-item title="标题属性" name="2" v-if="useIn==='report' || useIn === 'dashboard'">
         <boxProperties ref="boxPropForm2" :comp="comp" :useIn="useIn" :showCompProp="false" :showTitleProp="true"></boxProperties>
       </el-collapse-item>
      <el-collapse-item title="图形属性" name="3">
         <el-form-item label="图层名称：" label-width="100px" v-if="useIn=='bigscreen'">
            <el-input v-model="prop.title" @change="changevalue('title')"></el-input>
          </el-form-item>
          <el-form-item label="字体：" label-width="110px">
            <el-select v-model="prop.fFamily" @change="changevalue('fFamily')" :clearable="true" placeholder="请选择">
              <el-option
                v-for="item in opts.familys"
                :key="item.en"
                :label="item.ch"
                :value="item.en">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="字体大小：" label-width="110px">
            <el-input-number v-model="prop.fsize"  controls-position="right"  :precision="0" :min="9" @change="changevalue('fsize')"></el-input-number>
          </el-form-item>
         <el-form-item label="背景颜色：" label-width="210px"  v-if="ctp != 'custom'">
          <el-color-picker v-model="prop.chartbgcolor" @change="changevalue('chartbgcolor')"></el-color-picker>
        </el-form-item>
        <el-form-item label="图形动画：" label-width="200px"  v-if="ctp != 'custom'">
          <el-switch v-model="prop.animation" @change="changevalue('animation')"></el-switch>
        </el-form-item>
        <el-form-item label="颜色系列：" label-width="120px" v-if="ctp == 'pie' || ctp == 'treeMap' || ctp == 'sankey'">
          <el-select v-model="prop.colorSeries" placeholder="请选择" @change="changevalue('colorSeries')">
            <el-option v-for="item in opts.colorSeriess" :key="item.value" :label="item.text"  :value="item.value" />
          </el-select>
        </el-form-item>
        <!-- 通过@符号分割定义饼图等图形多个颜色，用 bmapDataAreaColor 属性代替 -->
        <el-form-item label="自定义颜色：" label-width="120px" v-if="ctp == 'pie' || ctp == 'treeMap' || ctp == 'sankey'">
          <el-input v-model="prop.bmapDataAreaColor" placeholder="多值用@符号分割"  @change="changevalue('bmapDataAreaColor')"/>
        </el-form-item>
        <template v-if="ctp == 'line' || ctp == 'area' || ctp == 'radar'">
         <el-form-item label="启用描点：" label-width="200px">
          <el-switch v-model="prop.markerEnabled" @change="changevalue('markerEnabled')"></el-switch>
         </el-form-item>
        </template>
        <template v-if="ctp == 'line' || ctp == 'area'">
           <el-form-item label="启用平滑曲线：" label-width="200px">
            <el-switch v-model="prop.spline" @change="changevalue('spline')"></el-switch>
          </el-form-item>
          <el-form-item label="添加平均线：" label-width="200px">
            <el-switch v-model="prop.makeLine" @change="changevalue('makeLine')"></el-switch>
          </el-form-item>
        </template>
         <template v-if="ctp == 'line' || ctp == 'column'">
          <el-form-item label="隐藏线条：" label-width="200px">
            <el-switch v-model="prop.hideLine" @change="changevalue('hideLine')"></el-switch>
          </el-form-item>
         </template>
        <el-form-item label="圆角半径：" label-width="100px" v-if="ctp =='bar' || ctp == 'column'">
          <el-slider v-model="prop.barBorderRadius" :max="100" :min="0" @change="changevalue('barBorderRadius')"></el-slider>
        </el-form-item>
        <el-form-item label="胶囊边框颜色：" label-width="210px" v-if="ctp == 'bar' && typeIndex == 3">
          <el-color-picker v-model="prop.mapBorderColor" :show-alpha="true" @change="changevalue('mapBorderColor')"></el-color-picker>
        </el-form-item>
        <template v-if="ctp === 'pie'">
           <el-form-item label="是否显示标签：" label-width="200px">
            <el-switch v-model="prop.pieDataLabel" @change="changevalue('pieDataLabel')"></el-switch>
          </el-form-item>
          <el-form-item label="标签位置：" label-width="120px">
              <el-select v-model="prop.pieDataLabelPos" placeholder="请选择" @change="changevalue('pieDataLabelPos')">
                <el-option
                  v-for="item in opts.pielabelposs"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="标签内容：" label-width="120px">
              <el-select v-model="prop.labelType" placeholder="请选择" :clearable="true" @change="changevalue('labelType')">
                <el-option v-for="item in opts.pielabels"  :key="item.value"  :label="item.text"  :value="item.value" >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="隐藏引导线：" label-width="200px">
            <el-switch v-model="prop.hidePieLine" @change="changevalue('hidePieLine')"></el-switch>
          </el-form-item>
          <el-form-item label="极区图：" label-width="200px">
            <el-switch v-model="prop.pieRoseType" @change="changevalue('pieRoseType')"></el-switch>
          </el-form-item>
          <el-form-item label="外环半径%：" label-width="110px" v-if="typeIndex != 3">
            <el-slider v-model="prop.outerRing" :max="100" :min="20" @change="changevalue('outerRing')"></el-slider>
          </el-form-item>
          <el-form-item label="中心点位置%：" label-width="110px">
            <el-slider v-model="prop.centerPos" :max="90" :min="10" @change="changevalue('centerPos')"></el-slider>
          </el-form-item>
          <el-form-item label="内环半径%：" label-width="110px" v-if="typeIndex==2">
            <el-slider v-model="prop.innerRing" :max="100" :min="20" @change="changevalue('innerRing')"></el-slider>
          </el-form-item>
        </template>
        <template v-if='ctp == "gauge" && typeIndex == 1 '> <!-- 用  dataZoomStart 和 dataZoomEnd 属性代替仪表盘 startAngle 和 endAngle -->
          <el-form-item label="起始角度" label-width="110px">
            <el-input-number v-model="prop.dataZoomStart" controls-position="right"  @change="changevalue('dataZoomStart')" :precision="0" :min="-360" :max="360"></el-input-number>
          </el-form-item>
          <el-form-item label="结束角度" label-width="110px">
            <el-input-number v-model="prop.dataZoomEnd" controls-position="right"  @change="changevalue('dataZoomEnd')" :precision="0" :min="-360" :max="360"></el-input-number>
          </el-form-item>
           <el-form-item label="中心点位置%" label-width="110px">
            <el-input-number v-model="prop.centerPos" controls-position="right"  @change="changevalue('centerPos')" :precision="0" :min="0" :max="100"></el-input-number>
          </el-form-item>
          <el-form-item label="仪表盘半径%" label-width="110px">
            <el-input-number v-model="prop.mapBorderWidth" controls-position="right"  @change="changevalue('mapBorderWidth')" :precision="0" :min="50" :max="200"></el-input-number>
          </el-form-item>
        </template>
        <el-form-item label="启用多指标查询：" label-width="200px" v-if="isMulit()">
          <el-switch v-model="prop.mkpi" @change="changevalue('mkpi')"></el-switch>
        </el-form-item>
        <el-form-item label="启用指标定制：" label-width="200px" v-if="prop.mkpi == true && ( useIn === 'dashboard')">
          <el-switch v-model="prop.kpiCustomized" @change="changevalue('kpiCustomized')"></el-switch>
        </el-form-item>
        <el-form-item label="数据形状：" label-width="120px" v-if="ctp == 'map' && !(typeIndex == 1 || typeIndex == 6)">
            <el-select v-model="prop.scatterSymbol" placeholder="请选择" :clearable="true" @change="changevalue('scatterSymbol')">
                <el-option v-for="item in opts.scatterSymbols"  :key="item.value"  :label="item.text"  :value="item.value" >
                </el-option>
              </el-select>
        </el-form-item>
        <template v-if="ctp == 'map' && typeIndex != 4">
          <el-form-item label="地图默认区域颜色：" label-width="210px">
            <el-color-picker v-model="prop.mapAreaColor" :show-alpha="true" @change="changevalue('mapAreaColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="鼠标移上后颜色：" label-width="210px">
            <el-color-picker v-model="prop.mapHoverColor" @change="changevalue('mapHoverColor')"></el-color-picker>
          </el-form-item>
           <el-form-item label="鼠标移上后文本颜色：" label-width="210px"><!-- 属性值不够，用bmapDataAreaColor代替 -->
            <el-color-picker v-model="prop.bmapDataAreaColor" @change="changevalue('bmapDataAreaColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="地图边框颜色：" label-width="210px">
            <el-color-picker v-model="prop.mapBorderColor" @change="changevalue('mapBorderColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="地图边框宽度：" label-width="120px">
            <el-slider v-model="prop.mapBorderWidth" :max="10" :min="0" @change="changevalue('mapBorderWidth')"></el-slider>
          </el-form-item>
          <el-form-item label="数据颜色区间：" label-width="145px">
            <el-color-picker v-model="prop.mapDataAreaColor1" :show-alpha="true" @change="changevalue('mapDataAreaColor1')"></el-color-picker>
            <el-color-picker v-model="prop.mapDataAreaColor2" :show-alpha="true" @change="changevalue('mapDataAreaColor2')"></el-color-picker>
            <el-color-picker v-model="prop.mapDataAreaColor3" :show-alpha="true" @change="changevalue('mapDataAreaColor3')"></el-color-picker>
          </el-form-item>
        </template>
         <template v-if="ctp == 'map' && typeIndex == 5">
           <el-form-item label="距离：" label-width="110px">
              <el-slider v-model="prop.dataZoomStart" :max="200" :min="50" @change="changevalue('dataZoomStart')"></el-slider>
            </el-form-item>
         </template>
         <template v-if="ctp == 'map' && typeIndex == 4"> <!--百度地图 -->
            <el-form-item label="地图中心点：" label-width="110px">
              <el-input v-model="prop.bmapCenterPos" placeholder="经纬度逗号分割" @change="changevalue('bmapCenterPos')"></el-input>
            </el-form-item>
            <el-form-item label="地图层级：" label-width="110px">
              <el-slider v-model="prop.bmapZoom" :max="14" :min="3" @change="changevalue('bmapZoom')"></el-slider>
            </el-form-item>
            <el-form-item label="数据颜色：" label-width="210px">
              <el-color-picker v-model="prop.bmapDataAreaColor" @change="changevalue('bmapDataAreaColor')"></el-color-picker>
            </el-form-item>
         </template>
         <template v-if="ctp == 'radar'">
           <el-form-item label="颜色间隔：" label-width="175px">
              <el-color-picker v-model="prop.mapDataAreaColor1" @change="changevalue('mapDataAreaColor1')"></el-color-picker>
              <el-color-picker v-model="prop.mapDataAreaColor2" @change="changevalue('mapDataAreaColor2')"></el-color-picker>
            </el-form-item>
            <el-form-item label="雷达形状：" label-width="115px">
              <el-radio-group v-model="prop.radarShape" size="mini" @change="changevalue('radarShape')">
                <el-radio-button label="polygon">多边形</el-radio-button>
                <el-radio-button label="circle">圆形</el-radio-button>
              </el-radio-group>
            </el-form-item>
         </template>
         <template v-if="ctp == 'gauge' && typeIndex==1">
             <el-form-item label="数据颜色区间：" label-width="140px">
              <el-color-picker v-model="prop.mapDataAreaColor1" @change="changevalue('mapDataAreaColor1')"></el-color-picker>
              <el-color-picker v-model="prop.mapDataAreaColor2" @change="changevalue('mapDataAreaColor2')"></el-color-picker>
              <el-color-picker v-model="prop.mapDataAreaColor3" @change="changevalue('mapDataAreaColor3')"></el-color-picker>
            </el-form-item>
            <el-form-item label="仪表盘宽度：" label-width="110px">
              <el-slider v-model="prop.outerRing" :max="100" :min="1" @change="changevalue('outerRing')"></el-slider>
            </el-form-item>
         </template>
          <template v-if="ctp == 'gauge' && (typeIndex== 2 || typeIndex==3)">
            <el-form-item label="非数据颜色：" label-width="210px">
              <el-color-picker show-alpha v-model="prop.mapAreaColor" @change="changevalue('mapAreaColor')"></el-color-picker>
            </el-form-item>
          </template>
          <template v-if="ctp == 'gauge' &&  typeIndex==3 ">
            <el-form-item label="曲线宽度：" label-width="110px">
              <el-slider v-model="prop.barWidth" :max="40" :min="5" @change="changevalue('barWidth')"></el-slider>
            </el-form-item>
          </template>
           <template v-if="ctp == 'gauge' &&  typeIndex==4">  <!-- 水球图颜色 -->
              <el-form-item label="形状：" label-width="115px">
                <el-select v-model="prop.scatterSymbol" placeholder="请选择" :clearable="true" @change="changevalue('scatterSymbol')">
                <el-option v-for="item in opts.liquidFills"  :key="item.value"  :label="item.text"  :value="item.value" >
                </el-option>
              </el-select>
              </el-form-item>
               <el-form-item label="背景颜色：" label-width="210px">
                 <el-color-picker show-alpha v-model="prop.mapAreaColor" @change="changevalue('mapAreaColor')"></el-color-picker>
               </el-form-item>
                <el-form-item label="外边框颜色：" label-width="210px">
                 <el-color-picker show-alpha v-model="prop.bmapDataAreaColor" @change="changevalue('bmapDataAreaColor')"></el-color-picker>
               </el-form-item>
           </template>
          <template v-if="ctp == 'treeMap'">
            <el-form-item label="图形间距：" label-width="110px">
              <el-slider v-model="prop.treeMapGap" :max="30" :min="0" @change="changevalue('treeMapGap')"></el-slider>
            </el-form-item>
          </template>
          <template v-if="ctp == 'line' || ctp=='column'">
            <el-form-item label="强制对齐0刻度：" label-width="200px">
              <el-switch v-model="prop.alignZeroScale" @change="changevalue('alignZeroScale')"></el-switch>
            </el-form-item>
          </template>
          <template v-if="ctp == 'line' || ctp == 'area' || ctp == 'column' || ctp =='bar'">
            <el-form-item label="强制Null转0：" label-width="200px">
              <el-switch v-model="prop.null2zero" @change="changevalue('null2zero')"></el-switch>
            </el-form-item>
          </template>
           <el-form-item label="数据集/表：" label-width="100px">
              <div align="right" :title="comp.comp.tname">{{ comp.comp ? comp.comp.tname : "" }}</div>
            </el-form-item>
            <el-form-item label="更换数据集/表：" label-width="200px">
            <button class="btn btn-success btn-xs" type="button" @click="chgtable()">修改</button>
          </el-form-item>
            <el-form-item label="清除数据集/表：" label-width="200px">
              <el-switch v-model="prop.clear" @change="changevalue('clear')"></el-switch>
            </el-form-item>
      </el-collapse-item>

      <el-collapse-item title="图例" name="11" v-if="isLengend()">
          <el-form-item label="是否显示图例：" label-width="200px">
            <el-switch v-model="prop.showLegend" @change="changevalue('showLegend')"></el-switch>
          </el-form-item>
          <el-form-item label="图例位置：" label-width="120px">
              <el-select v-model="prop.legendPosition" placeholder="请选择" @change="changevalue('legendPosition')">
                <el-option
                  v-for="item in opts.legendpos"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="排列方式：" label-width="120px">
            <el-radio-group v-model="prop.legendLayout" size="mini" @change="changevalue('legendLayout')">
            <el-radio-button label="vertical">垂直</el-radio-button>
            <el-radio-button label="horizontal">水平</el-radio-button>
            </el-radio-group>
          </el-form-item>
           <el-form-item label="文本颜色：" label-width="210px">
            <el-color-picker v-model="prop.legendTextColor" @change="changevalue('legendTextColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="背景颜色：" label-width="210px">
            <el-color-picker v-model="prop.legendBgColor" @change="changevalue('legendBgColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="边框宽度：" label-width="100px">
            <el-slider v-model="prop.legendBorderWidth" :max="10" :min="0" @change="changevalue('legendBorderWidth')"></el-slider>
          </el-form-item>
           <el-form-item label="边框颜色：" label-width="210px">
            <el-color-picker v-model="prop.legendBorderColor" @change="changevalue('legendBorderColor')"></el-color-picker>
          </el-form-item>
      </el-collapse-item>

      <el-collapse-item title="网格" v-if="iswg()" name="12">
          <el-form-item label="左间距：" label-width="100px">
            <el-slider v-model="prop.marginLeft" :max="300" :min="1" @change="changevalue('marginLeft')"></el-slider>
          </el-form-item>
          <el-form-item label="右间距：" label-width="100px">
            <el-slider v-model="prop.marginRight" :max="300" :min="1" @change="changevalue('marginRight')"></el-slider>
          </el-form-item>
           <el-form-item label="上间距：" label-width="100px">
            <el-slider v-model="prop.marginTop" :max="300" :min="1" @change="changevalue('marginTop')"></el-slider>
          </el-form-item>
           <el-form-item label="下间距：" label-width="100px">
            <el-slider v-model="prop.marginBottom" :max="300" :min="1" @change="changevalue('marginBottom')"></el-slider>
          </el-form-item>
          <el-form-item label="显示网格线：" label-width="200px">
            <el-switch v-model="prop.splitLine" @change="changevalue('splitLine')"></el-switch>
          </el-form-item>
          <el-form-item label="网格线颜色：" label-width="210px">
            <el-color-picker v-model="prop.splitLineColor" @change="changevalue('splitLineColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="网格线宽度：" label-width="100px">
            <el-slider v-model="prop.splitLineWidth" :max="10" :min="0" @change="changevalue('splitLineWidth')"></el-slider>
          </el-form-item>
          <el-form-item label="网格线类型：" label-width="100px">
              <el-select v-model="prop.splitLineType" placeholder="请选择" @change="changevalue('splitLineType')">
                <el-option
                  v-for="item in opts.borderStyles"
                  :key="item.value"
                  :label="item.name"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
      </el-collapse-item>

      <el-collapse-item title="文本标签" v-if="iswgbq()" name="13">
        <el-form-item label="选择系列：" label-width="100px">
              <el-select v-model="prop.dataLabelSeries" placeholder="请选择" @change="changevalue('dataLabelSeries')">
                <el-option
                  v-for="item in opts.series"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="是否显示值" label-width="200px">
            <el-switch v-model="prop.dataLabel" @change="changevalue('dataLabel')"></el-switch>
          </el-form-item>
          <el-form-item label="值的颜色" label-width="210px">
            <el-color-picker v-model="prop.dataLabelColor" @change="changevalue('dataLabelColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="位置：" label-width="100px">
              <el-select v-model="prop.dataLabelPosition" :clearable="true" placeholder="请选择" @change="changevalue('dataLabelPosition')">
                <el-option
                  v-for="item in opts.position"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="文本大小：" label-width="100px">
            <el-slider v-model="prop.dataLabelSize" :max="50" :min="9" @change="changevalue('dataLabelSize')"></el-slider>
          </el-form-item>
           <el-form-item label="是否粗体：" label-width="200px">
            <el-switch v-model="prop.dataLabelWeight" @change="changevalue('dataLabelWeight')"></el-switch>
          </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="提示条" v-if="isShowTooltip()" name="tooltip">
        <el-form-item label="是否显示：" label-width="200px">
            <el-switch v-model="prop.showTooltip" @change="changevalue('showTooltip')"></el-switch>
          </el-form-item>
          <el-form-item label="提示条风格：" label-width="120px">
            <el-select v-model="prop.tooltipClass" placeholder="请选择" @change="changevalue('tooltipClass')">
                <el-option
                  v-for="item in opts.tooltipClasss"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="轮询提示条：" label-width="200px">
            <el-switch v-model="prop.intervalTp" @change="changevalue('intervalTp')"></el-switch>
          </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="热力图设置" v-if="ctp =='map' && typeIndex == 6" name="heatmap">
            <el-form-item label="最大值：" label-width="120px">
            <el-input-number v-model="prop.max" controls-position="right"  @change="changevalue('max')" :precision="2"></el-input-number>
          </el-form-item>
           <el-form-item label="最小值：" label-width="120px">
            <el-input-number v-model="prop.min" controls-position="right"  @change="changevalue('min')" :precision="2"></el-input-number>
          </el-form-item>
          <el-form-item label="圆点半径：" label-width="120px">  <!-- 用 barBorderRadius 代替 -->
            <el-input-number v-model="prop.barBorderRadius" controls-position="right"  @change="changevalue('barBorderRadius')" :precision="0"></el-input-number>
          </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="横轴" name="4" v-if="showX()">
        <template v-if='ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =="scatter" || ctp == "bubble"'>
          <el-form-item label="隐藏横轴：" label-width="200px">
            <el-switch v-model="prop.hideXaxis" @change="changevalue('hideXaxis')"></el-switch>
          </el-form-item>
          <el-form-item label="标题：" label-width="110px">
            <el-input v-model="prop.xdispName" @blur="changevalue('xdispName')"></el-input>
          </el-form-item>
          <el-form-item label="文本颜色：" label-width="210px">
            <el-color-picker v-model="prop.axisNameColor" @change="changevalue('axisNameColor')"></el-color-picker>
          </el-form-item>
        </template>
        <template v-if='ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar"'>
          <el-form-item label="显示间隔：" label-width="110px">
            <el-slider v-model="prop.tickInterval" :max="10" :min="0" @change="changevalue('tickInterval')"></el-slider>
          </el-form-item>
          <el-form-item label="旋转角度：" label-width="110px">
            <el-slider v-model="prop.routeXaxisLable" :max="360" :min="0" @change="changevalue('routeXaxisLable')"></el-slider>
          </el-form-item>
        </template>
        <template v-if='ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =="scatter" || ctp == "bubble"'>
          <el-form-item label="显示坐标轴线：" label-width="200px">
            <el-switch v-model="prop.axisLine" @change="changevalue('axisLine')"></el-switch>
          </el-form-item>
          <el-form-item label="坐标轴线颜色：" label-width="210px">
            <el-color-picker v-model="prop.axisLineColor" @change="changevalue('axisLineColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="显示刻度：" label-width="200px">
            <el-switch v-model="prop.axisTick" @change="changevalue('axisTick')"></el-switch>
          </el-form-item>
          <el-form-item label="显示标签：" label-width="200px">
            <el-switch v-model="prop.axisLabel" @change="changevalue('axisLabel')"></el-switch>
          </el-form-item>
        </template>
        <template v-if="ctp == 'radar'">
          <el-form-item label="显示坐标轴线：" label-width="200px">
            <el-switch v-model="prop.axisLabel" @change="changevalue('axisLabel')"></el-switch>
          </el-form-item>
          <el-form-item label="标签颜色：" label-width="200px">
            <el-color-picker v-model="prop.axisNameColor" @change="changevalue('axisNameColor')"></el-color-picker>
          </el-form-item>
          <el-form-item label="显示坐标轴线：" label-width="200px">
            <el-switch v-model="prop.axisLine" @change="changevalue('axisLine')"></el-switch>
          </el-form-item>
          <el-form-item label="坐标轴线颜色：" label-width="200px">
            <el-color-picker v-model="prop.axisLineColor" @change="changevalue('axisLineColor')"></el-color-picker>
          </el-form-item>
        </template>
         <template v-if='ctp == "line" || ctp == "area"'>
           <el-form-item label="贴边间距：" label-width="200px">
            <el-switch v-model="prop.boundaryGap" @change="changevalue('boundaryGap')"></el-switch>
          </el-form-item>
         </template>
        <el-form-item label="取Top：" label-width="110px" v-if='ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp == "pie" || ctp == "radar" || ctp == "wordcloud" || ctp == "funnel" || ctp == "treeMap"'>
          <el-input-number v-model="prop.top" :min="1" @change="changevalue('top')" :precision="0"></el-input-number>
        </el-form-item>
        <el-form-item label="柱子宽度：" label-width="110px" v-if='ctp == "column" || ctp == "bar" || (ctp == "line" && prop.mkpi == true)'>
          <el-slider v-model="prop.barWidth" :max="100" :min="1" @change="changevalue('barWidth')"></el-slider>
        </el-form-item>
        <!-- 散点图或气泡图需要y2轴的属性 -->
        <template v-if='ctp =="scatter" || ctp == "bubble"'>
          <el-form-item label="标题：" label-width="110px">
            <el-input v-model="prop.y2dispName" @blur="changevalue('y2dispName')"></el-input>
          </el-form-item>
          <el-form-item label="单位：" label-width="110px">
            <el-input v-model="prop.unit2" @blur="changevalue('unit2')"></el-input>
          </el-form-item>
          <el-form-item label="格式化：" label-width="70px">
              <el-select v-model="prop.fmt2" placeholder="请选择" @change="changevalue('fmt2')">
                <el-option
                  v-for="item in opts.fmt"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="度量比例：" label-width="70px">
              <el-select v-model="prop.rate2" placeholder="请选择" clearable @change="changevalue('rate2')">
                <el-option
                  v-for="item in opts.rates"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                >
                </el-option>
              </el-select>
          </el-form-item>
        </template>
      </el-collapse-item>
       <el-collapse-item title="纵轴" name="5" v-if="ctp != 'custom'">
          <template v-if='ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =="scatter" || ctp == "bubble" || ctp == "gauge"'>
            <el-form-item label="隐藏纵轴：" label-width="200px">
              <el-switch v-model="prop.hideYaxis" @change="changevalue('hideYaxis')"></el-switch>
            </el-form-item>
             <el-form-item label="标题：" label-width="110px">
              <el-input v-model="prop.ydispName" @blur="changevalue('ydispName')"></el-input>
            </el-form-item>
            <el-form-item label="文本颜色：" label-width="210px">
              <el-color-picker v-model="prop.yAxisNameColor" @change="changevalue('yAxisNameColor')"></el-color-picker>
            </el-form-item>
            <el-form-item label="字体大小：" label-width="110px" v-if="ctp === 'gauge' && typeIndex == 1">
               <el-slider v-model="prop.yAxisNameSize" :min="9" :max="42" @change="changevalue('yAxisNameSize')"></el-slider>
            </el-form-item>
          </template>
            <el-form-item label="单位：" label-width="110px">
              <el-input v-model="prop.unit" @blur="changevalue('unit')"></el-input>
            </el-form-item>
            <el-form-item label="格式化：" label-width="110px">
               <el-select v-model="prop.fmt" placeholder="请选择" @change="changevalue('fmt')">
                  <el-option v-for="item in opts.fmt" :key="item.value" :label="item.text" :value="item.value"  >
                  </el-option>
                </el-select>
            </el-form-item>
            <template v-if='ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =="scatter" || ctp == "bubble"'>
              <el-form-item label="显示坐标轴线：" label-width="200px">
                <el-switch v-model="prop.yAxisLine" @change="changevalue('yAxisLine')"></el-switch>
              </el-form-item>
              <el-form-item label="坐标轴线颜色：" label-width="210px">
                <el-color-picker v-model="prop.yAxisLineColor" @change="changevalue('yAxisLineColor')"></el-color-picker>
              </el-form-item>
              <el-form-item label="显示刻度：" label-width="200px">
                <el-switch v-model="prop.yAxisTick" @change="changevalue('yAxisTick')"></el-switch>
              </el-form-item>
              <el-form-item label="显示标签：" label-width="200px">
                <el-switch v-model="prop.yAliasLabel" @change="changevalue('yAliasLabel')"></el-switch>
              </el-form-item>
              <el-form-item label="分隔段数：" label-width="110px">
                <el-slider v-model="prop.splitNumber" :max="10" :min="1" @change="changevalue('splitNumber')"></el-slider>
              </el-form-item>
            </template>
            <template v-if='ctp == "gauge" && typeIndex==1'>
              <el-form-item label="显示刻度：" label-width="200px">
                <el-switch v-model="prop.yAxisTick" @change="changevalue('yAxisTick')"></el-switch>
              </el-form-item>
              <el-form-item label="刻度颜色：" label-width="200px">
                <el-color-picker v-model="prop.yAxisTickColor" @change="changevalue('yAxisTickColor')"></el-color-picker>
              </el-form-item>
              <el-form-item label="分隔段数：" label-width="110px">
                <el-slider v-model="prop.splitNumber" :max="10" :min="1" @change="changevalue('splitNumber')"></el-slider>
              </el-form-item>
            </template>
            <template v-if="ctp === 'radar'">
              <el-form-item label="显示标签：" label-width="200px">
                <el-switch v-model="prop.yAliasLabel" @change="changevalue('yAliasLabel')"></el-switch>
              </el-form-item>
              <el-form-item label="标签颜色：" label-width="200px">
                <el-color-picker v-model="prop.yAxisNameColor" @change="changevalue('yAxisNameColor')"></el-color-picker>
              </el-form-item>
              <el-form-item label="显示坐标轴线：" label-width="200px">
                <el-switch v-model="prop.yAxisLine" @change="changevalue('yAxisLine')"></el-switch>
              </el-form-item>
              <el-form-item label="坐标轴线颜色：" label-width="210px">
                <el-color-picker v-model="prop.yAxisLineColor" @change="changevalue('yAxisLineColor')"></el-color-picker>
              </el-form-item>
              <el-form-item label="分隔段数：" label-width="110px">
                <el-slider v-model="prop.splitNumber" :max="10" :min="1" @change="changevalue('splitNumber')"></el-slider>
              </el-form-item>
            </template>

            <template v-if='ctp == "gauge" || ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =="scatter" || ctp == "bubble" '>
              <el-form-item label="最小值：" label-width="110px">
                <el-input-number v-model="prop.min" controls-position="right"  @change="changevalue('min')" :precision="2"></el-input-number>
              </el-form-item>
            </template>
            <template v-if="ctp == 'gauge' || ctp == 'line' || ctp === 'column' || ctp == 'area' || ctp === 'bar'">
              <el-form-item label="最大值：" label-width="110px">
                  <el-input-number v-model="prop.max" controls-position="right"  @change="changevalue('max')" :precision="2"></el-input-number>
                </el-form-item>
            </template>
            <el-form-item label="度量比例：" label-width="110px">
               <el-select v-model="prop.rate" placeholder="请选择" clearable @change="changevalue('rate')">
                  <el-option
                    v-for="item in opts.rates"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
            </el-form-item>
             <el-form-item label="合并数据：" label-width="200px" v-if='(ctp == "column" || ctp == "line") && (typeIndex=="2" || typeIndex=="4")'>
                <el-switch v-model="prop.mergeData2" @change="changevalue('mergeData2')"></el-switch>
              </el-form-item>
       </el-collapse-item>
        <el-collapse-item title="第二纵轴" name="6" v-if="isy2()">
            <el-form-item label="隐藏纵轴：" label-width="200px">
                <el-switch v-model="prop.hideY2axis" @change="changevalue('hideY2axis')"></el-switch>
              </el-form-item>
            <el-form-item label="标题：" label-width="110px">
              <el-input v-model="prop.y2dispName" @blur="changevalue('y2dispName')"></el-input>
            </el-form-item>
             <el-form-item label="文本颜色：" label-width="210px">
                <el-color-picker v-model="prop.y2AxisNameColor" @change="changevalue('y2AxisNameColor')"></el-color-picker>
              </el-form-item>
            <el-form-item label="单位：" label-width="110px">
                <el-input v-model="prop.unit2" @blur="changevalue('unit2')"></el-input>
              </el-form-item>
              <el-form-item label="格式化：" label-width="110px">
                <el-select v-model="prop.fmt2" placeholder="请选择" @change="changevalue('fmt2')">
                    <el-option
                      v-for="item in opts.fmt"
                      :key="item.value"
                      :label="item.text"
                      :value="item.value"
                    >
                    </el-option>
                  </el-select>
              </el-form-item>
              <el-form-item label="显示坐标轴线：" label-width="200px">
                <el-switch v-model="prop.y2AxisLine" @change="changevalue('y2AxisLine')"></el-switch>
              </el-form-item>
              <el-form-item label="坐标轴线颜色：" label-width="210px">
                <el-color-picker v-model="prop.y2AxisLineColor" @change="changevalue('y2AxisLineColor')"></el-color-picker>
              </el-form-item>
              <el-form-item label="显示刻度：" label-width="200px">
                <el-switch v-model="prop.y2AxisTick" @change="changevalue('y2AxisTick')"></el-switch>
              </el-form-item>
              <el-form-item label="显示标签：" label-width="200px">
                <el-switch v-model="prop.y2AliasLabel" @change="changevalue('y2AliasLabel')"></el-switch>
              </el-form-item>
              <el-form-item label="分隔段数：" label-width="110px">
                <el-slider v-model="prop.y2SplitNumber" :max="10" :min="1" @change="changevalue('y2SplitNumber')"></el-slider>
              </el-form-item>
              <el-form-item label="最小值：" label-width="110px">
                <el-input-number v-model="prop.y2min" controls-position="right"  @change="changevalue('y2min')" :precision="2"></el-input-number>
              </el-form-item>
               <el-form-item label="最大值：" label-width="110px">
                <el-input-number v-model="prop.y2max" controls-position="right"  @change="changevalue('y2max')" :precision="2"></el-input-number>
              </el-form-item>
              <el-form-item label="度量比例：" label-width="110px">
                <el-select v-model="prop.rate2" placeholder="请选择" clearable @change="changevalue('rate2')">
                    <el-option
                      v-for="item in opts.rates"
                      :key="item.value"
                      :label="item.text"
                      :value="item.value"
                    >
                    </el-option>
                  </el-select>
              </el-form-item>
              <el-form-item label="合并数据：" label-width="200px">
                <el-switch v-model="prop.mergeData" @change="changevalue('mergeData')"></el-switch>
              </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="系列颜色" name="19" v-if="!(ctp == 'pie' || ctp == 'map' || ctp == 'treeMap' || ctp =='sankey' || ctp == 'custom')">
          <el-form-item label="清除所有颜色：" label-width="200px">
            <el-switch v-model="prop.clearall" @change="changevalue('clearall')"></el-switch>
          </el-form-item>
          <el-form-item v-for="(sc, index) in prop.seriesColors"  :label="sc.text+'：'"  :label-width="prop.seriesColorsMore === true?'110px':'210px'"  :key="index" >
            <template v-if="prop.seriesColorsMore === true">
              <el-input v-model="prop.seriesColors[index].value" placeholder="3个颜色值以@分割" @change="changevalue('seriesColors')"/>
            </template>
            <template v-else>
            <el-color-picker v-model="prop.seriesColors[index].value" show-alpha @change="changevalue('seriesColors')"></el-color-picker>
            </template>
          </el-form-item>
          <el-form-item label="启用多颜色值：" label-width="200px">
            <el-switch v-model="prop.seriesColorsMore" @change="changevalue('seriesColorsMore')"></el-switch>
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="缩放" name="21" v-if="ctp == 'line' || ctp == 'column' || ctp == 'area' ">
          <el-form-item label="是否启用：" label-width="200px">
            <el-switch v-model="prop.useDataZoom" @change="changevalue('useDataZoom')"></el-switch>
          </el-form-item>
          <el-form-item label="起始百分比：" label-width="110px" v-if="prop.useDataZoom == true">
            <el-input-number v-model="prop.dataZoomStart" :min="0" :max="100" @change="changevalue('dataZoomStart')" />
          </el-form-item>
          <el-form-item label="结束百分比：" label-width="110px" v-if="prop.useDataZoom == true">
            <el-input-number v-model="prop.dataZoomEnd" :min="0" :max="100" @change="changevalue('dataZoomEnd')" />
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="位置信息" name="17" v-if="useIn === 'bigscreen'">
            <posProperties :comp="comp" ref="posPropForm"></posProperties>
       </el-collapse-item>
       <el-collapse-item title="自动刷新" name="22" v-if="useIn === 'bigscreen' || useIn === 'dashboard'">
          <compAutoFlush :comp="comp" ref="autoFlushForm"/>
       </el-collapse-item>
    </el-collapse>
    <modifyCompTable ref="modifyCompTableForm"></modifyCompTable>
  </el-form>
</template>

<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import boxProperties from '@/components/toolbox/BoxProperties'
import posProperties from '@/components/toolbox/BsCompProperties'
import compAutoFlush from '@/components/toolbox/CompAutoFlush'
import modifyCompTable from '@/components/toolbox/ModifyCompTable'
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  name:"chartProperty",
  components:{
    boxProperties, posProperties,compAutoFlush, modifyCompTable
  },
  props:{
      comp:{
        type:Object,
        required:true,
        default:{}
      },
       //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
  },
  data(){
    return {
      activeName: this.useIn==='report' || this.useIn === 'dashboard' ?"1" :"3",
      prop:{
        title:null,
        chartbgcolor:null,
        animation:true,
        colorSeries:null,
        spline:null,
        hideLine:null,
        makeLine:null,
        unit:null,
        fmt:null,
        rate:null,
        showLegend:true,
        legendLayout:"horizontal",
        pieDataLabelPos:null,
        pieDataLabel:false,
        hidePieLine:null,
        markerEnabled:true,
        marginLeft:65,
        marginRight:10,
        marginTop:42,
        marginBottom:60,
        pieRoseType:null,
        splitLineWidth:null,
        splitLineType:null,
        splitLine:true,
        dataLabel:false,
        dataLabelColor:null,
        dataLabelPosition:null,
        dataLabelSize:14,
        dataLabelWeight:false,
        xdispName:"",
        tickInterval:null,
        routeXaxisLable:null,
        top:null,
        ydispName:null,
        min:null,
        max:null,
        labelType:null,
        y2dispName:null,
        unit2:null,
        fmt2:null,
        rate2:null,
        mergeData:null,
        barBorderRadius:0,
        outerRing:15,
        centerPos:50,
        innerRing:55,
        mkpi:null,
        scatterSymbol:null,
        mapAreaColor:null,
        mapHoverColor:null,
        mapBorderColor:null,
        mapBorderWith:1,
        mapDataAreaColor1:null,
        mapDataAreaColor2:null,
        mapDataAreaColor3:null,
        bmapCenterPos:null,
        bmapZoom:7,
        bmapDataAreaColor:null,
        radarShape:'polygon',
        barWidth:30,
        treeMapGap:0,
        alignZeroScale:null,
        legendTextColor:null,
        legendBgColor:null,
        legendBorderWidth:null,
        legendBorderColor:null,
        axisNameColor:null,
        hideXaxis:false,
        axisLine:true,
        axisLineColor:null,
        axisTick:true,
        axisLabel:true,
        boundaryGap:true,
        hideYaxis:false,
        yAxisNameColor:null,
        yAxisNameSize:12,
        yAxisLine:true,
        yAxisLineColor:null,
        yAxisTick:true,
        yAliasLabel:true,
        splitNumber:5,
        yAxisTickColor:null,
        hideY2axis:false,
        y2AxisNameColor:null,
        y2AxisLine:true,
        y2AxisLineColor:null,
        y2AxisTick:true,
        y2AliasLabel:true,
        y2SplitNumber:5,
        y2min:null,
        y2max:null,
        dataLabelSeries:null,
        clearall:false,
        seriesColors:[],  //图形系列颜色
        kpiCustomized: false,
        useDataZoom: false,
        dataZoomStart: null,
        dataZoomEnd: null,
         null2zero:false,
         showTooltip: true,
         intervalTp: false,
         tooltipClass: null, //tooltip 的风格样式切换
         seriesColorsMore: null,
         fFamily:null,
         fsize:13,
         mapBorderWidth:null,
      },
      ctp:null, //图形类型
      typeIndex:null, //选择的第几个图
      iskpiPosInRight: false,  //是否多指标，并且指标在Y2轴显示
      opts:{
        familys:bsUtils.fonts,
        fmt:utils.fmtJson,
        rates: utils.rates,
        legendpos:utils.legendpos,
        pielabels:utils.pielabels,
        borderStyles:utils.borderStyles,
        colorSeriess:[{"text":"默认","value":"def"},{"text":"大屏","value":"bs"},{"text":"渐变","value":"jb"},{"text":"明亮","value":"ml"},{"text":"柔和","value":"rh"},{"text":"科技","value":"kj"},{"text":"冷色","value":"ls"},{"text":"炫彩","value":"xc"}],
        series:[],
        position:utils.position,
        pielabelposs:[{text:"outside",value:"outside"},{"text":"inside","value":"inside"}],
        scatterSymbols:[{text:"圆形",value:"circle"},{text:"正方形",value:"roundRect"},{text:"气泡",value:"pin"}],
        tooltipClasss:[{text:"默认", value:"def"},{text:"暗黑", value:"black"}],
        liquidFills: [{text:"圆形",value:"circle"},{text:"rect",value:"rect"},{text:"roundRect",value:"roundRect"},{text:"triangle",value:"triangle"},{text:"diamond",value:"diamond"},{text:"pin",value:"pin"},{text:"arrow",value:"arrow"}]
      }
    }
  },
  mounted(){

  },
  computed: {
  },
  methods: {
    //是否显示y2
    isy2(){
      let ctp = this.ctp;
      let typeIndex = this.typeIndex;
      return (ctp == "column" || ctp == "line" || ctp==='bar') && (typeIndex==2 || typeIndex==4 || this.iskpiPosInRight == true );
    },
    iswgbq(){
      let ctp = this.ctp;
      return ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =="scatter"  || ctp == 'radar' || ctp == "bubble" || ctp=="map"
		    || ctp == "funnel" || ctp == "treeMap" || ctp === 'sankey';
    },
    isShowTooltip(){
      let ctp = this.ctp;
      return ("bigscreen" == this.useIn) && (ctp == 'map' && this.typeIndex == 1);
    },
    //是否显示图例
    isLengend(){
      let ctp = this.ctp;
      let typeIndex = this.typeIndex;
      let noLengend = ctp === 'gauge' || ctp === 'wordcloud' || ctp ==='funnel' || ctp === 'treeMap' || ctp === 'sankey' || ctp === 'custom' || ctp =='map' && typeIndex == 6;
      return !noLengend;
    },
    //是否显示横轴
    showX(){
      let ctp = this.ctp;
      let noX = ctp === 'gauge' || ctp =='map' || ctp ==='sankey' || ctp === 'custom';
      return !noX;
    },
    init(){
      if(this.useIn === 'report' || this.useIn === 'dashboard'){
        this.$refs['boxPropForm1'].init();
        this.$refs['boxPropForm2'].init();
      }else{
         this.$refs['posPropForm'].init();
        this.prop.title = this.comp.title;
      }
      if(this.useIn === 'bigscreen' || this.useIn === 'dashboard'){
        this.$refs['autoFlushForm'].init();
      }

      let p = this.prop;
      let c = this.comp.comp;
      this.ctp = c.chartJson.type;
      this.typeIndex = c.chartJson.typeIndex;
      if(c.chartJson.xcol){
        let x = c.chartJson.xcol;
        p.xdispName = x.xdispName;
        p.tickInterval = x.tickInterval;
        p.routeXaxisLable = x.routeXaxisLable;
        p.top = x.top;
      }

      let comp = this.comp.comp;
      //是否多指标，并且指标在Y2轴显示
      let y2node = null;
      if((comp.mkpi == true ) && comp.mkpiJson && comp.mkpiJson.length > 0 ){  //多指标查询
        $(comp.mkpiJson).each((a, b)=>{
          if(b.kpiPostion === 'right'){
            this.iskpiPosInRight = true;
            y2node = b;
            return false;
          }
        });
      }
      this.initSeries();
      //回写属性值
      for(let cp in c.chartJson){
        //提出的属性
        if(cp === 'type' || cp ==='mapDataAreaColor' || cp==='typeIndex' || cp ==='xcol' || cp === 'scol' || cp === 'ycol' || cp === 'link' || cp === 'linkAccept'){
          continue;
        }
        p[cp] = c.chartJson[cp];
      }
      //回写 mapDataAreaColor
      if(c.chartJson.mapDataAreaColor){
        let cols = c.chartJson.mapDataAreaColor.split("@");
        for(let j=0; j<cols.length; j++){
          p['mapDataAreaColor'+(j+1)] = cols[j];
        }
      }
      //回写 ydispName
      if(comp.mkpi == true){
        this.prop.ydispName = comp.mkpiJson && comp.mkpiJson[0] ? comp.mkpiJson[0].ydispName : "";
      }else{
        this.prop.ydispName = comp.kpiJson[0] ? comp.kpiJson[0].ydispName : "";
      }
      this.prop.mkpi = c.mkpi;
      //回写 seriesColor
      if(!comp.colors){
        comp.colors = {};
      }
      let colors = comp.colors;
      for(let c in colors){
        let one = this.prop.seriesColors.filter(m=>m.text === c);
        if(one.length > 0){
          one = one[0];
          one.value = colors[c];
        }
      }
      this.prop.seriesColorsMore = this.comp.seriesColorsMore; //系列多颜色
      this.prop.kpiCustomized = this.comp.kpiCustomized;
      //回写  fmt/unit/rate/max/min
      //非多指标（y轴）
      if((!comp.mkpi || comp.mkpi == false) && comp.kpiJson && comp.kpiJson.length > 0 && comp.kpiJson[0]){
        this.prop.fmt = comp.kpiJson[0].fmt;
        this.prop.unit = comp.kpiJson[0].unit;
        this.prop.rate = comp.kpiJson[0].rate;
        this.prop.max = comp.kpiJson[0].max;
        this.prop.min = comp.kpiJson[0].min;
      }
      //多指标（y2轴）
      if(comp.mkpi == true && comp.mkpiJson && comp.mkpiJson.length > 0 && comp.mkpiJson[0]){
        this.prop.fmt = comp.mkpiJson[0].fmt;
        this.prop.unit = comp.mkpiJson[0].unit;
        this.prop.rate = comp.mkpiJson[0].rate;
        this.prop.max = comp.mkpiJson[0].max;
        this.prop.min = comp.mkpiJson[0].min;
      }
      //非多指标（y轴）
      if((!comp.mkpi || comp.mkpi == false) && comp.kpiJson && comp.kpiJson.length > 1 && comp.kpiJson[1]){
          this.prop.fmt2 = comp.kpiJson[1].fmt;
          this.prop.unit2 = comp.kpiJson[1].unit;
          this.prop.rate2 = comp.kpiJson[1].rate;
          this.prop.max2 = comp.kpiJson[1].max;
          this.prop.min2 = comp.kpiJson[1].min;
      }
      //多指标 （y2轴）
      if(comp.mkpi == true && this.iskpiPosInRight == true){
          this.prop.fmt2 = y2node.fmt;
          this.prop.unit2 = y2node.unit;
          this.prop.rate2 = y2node.rate;
          this.prop.max2 = y2node.max;
          this.prop.min2 = y2node.min;
      }
    },
    initSeries(){
      //初始化图形系列
      //获取系列
      var chartSeries = [];
      var currSeries = null; //当前系列
      //需要用到系列设置label（文本标签）的图形
      let ctp = this.ctp;
      let comp = this.comp.comp;
      var o = document.getElementById('cte_' + comp.id);
      if(!o){
        o = document.getElementById('ct_' + comp.id);
      }
        if (o) {
          var chart = echarts.getInstanceByDom(o);
          if (chart) {
            var option = chart.getOption();
            $(option.series).each(function (a, b) {
              if (a == 0) {
                currSeries = b.name;
              }
              chartSeries.push({text: b.name, value: b.name});
            });
          }

        //从chartLabels删除已经没有的系列
        if(comp.chartLabels){
          var dls = [];
          for(var lb in comp.chartLabels){
            var r = chartSeries.filter(m=>m.value === lb);
            if(r.length === 0){
              dls.push(lb);
            }
          }
          $(dls).each(function(a, b){
            delete comp.chartLabels[b];
          });
        }
      }
      this.opts.series = chartSeries;
      this.prop.dataLabelSeries = currSeries;
      this.serSeriesVal();
      this.prop.seriesColors = chartSeries.map(m=>{
        return {text:m.text,value:null};
      });
    },
    isMulit(){
       let comp = this.comp.comp;
       let ctp = this.ctp;
       let idx = comp.chartJson.typeIndex;
       return (idx == "1" && (ctp == "line" || ctp == "bar" || ctp == "area" || ctp == "radar")) ||
				((idx == "1" || idx == "3") && ctp == "column") ||
				(ctp == "gauge" && idx == '3') || (ctp == 'map' && !(idx == 5 || idx == 6));  //地图都支持在 tooltip 显示多指标
    },
    //是否显示网格选项卡
    iswg(){
      var ctp = this.ctp;
      return ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =="scatter" || ctp == "bubble" || ctp == "sankey"
    },
    chartView(){
      this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id].chartView();
    },
    clearData(){
      let o = this.$parent.$parent.$refs['optarea'].$refs['mv_'+this.comp.id];
      o.data = null;
      o.$forceUpdate();
    },
    setUpdate(){
      this.$parent.$parent.isupdate = true;
    },
    compRender(){
      var o = this.$parent.$parent.$refs['optarea'];
      o.$forceUpdate();
      o.$refs['mv_'+this.comp.id].$forceUpdate();
    },
    changevalue(prop){
      let c = this.comp.comp;
      let v = this.prop[prop];
      let col = prop;
      if(col == "showLegend" || col == "legendLayout" || col == "legendPosition" || col =="labelType" || col == "markerEnabled" ||
				col == "marginLeft" || col == "marginRight" || col=='marginTop' || col =='marginBottom' || col =='splitLine' || col == 'barWidth' ||
				col == 'chartbgcolor' || col =='animation' || col == 'spline' || col == 'makeLine' || col =='axisLine' || col == 'axisTick' || col =='boundaryGap' ||
				col == 'hideYaxis' || col == 'yAxisTick' || col == 'splitNumber' || col == 'hideXaxis' || col == 'yAxisLine' || col == 'barBorderRadius' ||
				col == 'axisLabel' || col == 'yAliasLabel' || col == 'splitLineColor' || col == 'splitLineWidth' || col == 'splitLineType' || col=='yAxisNameColor' || col == 'yAxisNameSize' ||
				col == 'axisNameColor' || col == 'legendTextColor' || col == 'legendBgColor' || col =='legendBorderWidth' || col =='legendBorderColor' || col =='mapAreaColor' ||
				col == 'hideY2axis' || col =='y2AxisLine' || col == 'y2AxisTick' || col == 'y2AliasLabel' ||
				col == 'y2SplitNumber' || col == 'axisLineColor' || col == 'yAxisLineColor' || col =='y2AxisLineColor' || col == 'y2AxisNameColor' || col == 'innerRing' ||
        col == 'pieRoseType' || col == 'outerRing' || col ==='pieDataLabel' || col == 'pieDataLabelPos' || col == 'yAxisTickColor' || col == 'radarShape' || col == 'mapBorderColor' ||
        col == 'mapBorderWidth' ||
				col == 'mapHoverColor' || col == 'treeMapGap' || col == 'hidePieLine' || col == 'colorSeries'|| col === 'bmapDataAreaColor' || col ==='scatterSymbol' ||
        col == 'bmapZoom' || col === 'bmapCenterPos' || col==='centerPos' || col === 'hideLine' || col === 'alignZeroScale' || col === 'useDataZoom' ||
        col == 'dataZoomStart' || col == 'dataZoomEnd' || col ==='null2zero' || col == 'showTooltip' || col == 'intervalTp' || col =='tooltipClass' || col =='fFamily' || col == 'fsize'){
        if(v === c.chartJson[prop] ){ //值未变
           return;
        }
        c.chartJson[prop] = v;
        this.chartView();
      }else if(col === 'mapDataAreaColor1'||col === 'mapDataAreaColor2' || col === 'mapDataAreaColor3'){
          if(c.chartJson.type === 'gauge' || c.chartJson.type === 'map'){
             if(this.prop['mapDataAreaColor1'] && this.prop['mapDataAreaColor2'] && this.prop['mapDataAreaColor3']){
               c.chartJson.mapDataAreaColor = this.prop['mapDataAreaColor1']+"@"+this.prop['mapDataAreaColor2']+"@"+this.prop['mapDataAreaColor3'];
               this.chartView();
             }
          }else if(c.chartJson.type === 'radar'){
             if(this.prop['mapDataAreaColor1'] && this.prop['mapDataAreaColor2']){
               c.chartJson.mapDataAreaColor = this.prop['mapDataAreaColor1']+"@"+this.prop['mapDataAreaColor2'];
               this.chartView();
             }
          }
      }else if(col == "tickInterval" || col == "routeXaxisLable" || col == "xdispName" || col == "top"){
        if(c.chartJson.xcol[prop] === v){ //值未变
          return;
        }
        c.chartJson.xcol[prop] = v;
         this.chartView();
      }else if(col === 'dataLabelSeries'){ //定义图形Lable (文本标签)
        this.serSeriesVal();
      }else if(col == 'seriesColorsMore'){
        this.comp.seriesColorsMore = v;
      }else if(col == "dataLabel" || col ==='dataLabelColor' || col === 'dataLabelPosition' || col ==='dataLabelSize'||col==='dataLabelWeight'){//文本标签设置
        //支持选择系列的图形
        let chartSeries = this.opts.series;
        let currSeries = this.prop.dataLabelSeries;
         //需要单独对每个系列设置文本标签的图形
        let ctp = c.chartJson.type;
        if(ctp == "line" || ctp == "column" || ctp == "area" || ctp == "bar" || ctp =='gauge' || ctp =="scatter"  || ctp == 'radar' || ctp == "bubble") {
          if (!c.chartLabels) {
            c.chartLabels = {};
          }
          var cl = c.chartLabels;
          if (!cl[currSeries]) {
            cl[currSeries] = {};
          }
          var onelable = cl[currSeries];
          onelable[col] = v;
        }else{  //采用 chartJson 里面定义文本标签
          c.chartJson[col] = v;
        }
        this.chartView();
      }else if(col == "ydispName" || col == "unit" || col == "fmt" || col == "min" || col == "max" || col == "rate"){
        var o = null;
        if((c.mkpi == true  || c.mkpi == "true") && c.mkpiJson.length > 0 ){  //多指标查询
          $(c.mkpiJson).each(function(a, b){
            if(!b.kpiPostion || b.kpiPostion === 'left'){
              o = b;
              return false;
            }
          });
        }else{  //单指标查询
          o = c.kpiJson[0];
        }

        if(v === o[col] ){ //值未变
           return;
        }
        o[col] = v;
        this.chartView();
      }else if(col == "mergeData2"){

		  }else if(col == "y2dispName" || col == "unit2" || col == "fmt2" || col == "y2min" || col === 'y2max' || col == "rate2" || col == "mergeData"){	//处理y2col y2轴
        var o = c.kpiJson[1];
        if(this.iskpiPosInRight){
          $(c.mkpiJson).each((a, b)=>{
            if(b.kpiPostion === 'right'){
              o = b;
              return false;
            }
          });
        }
        if(v === o[col] ){ //值未变
           return;
        }
				if(col == "y2dispName"){
					o.ydispName = v;
				}else if(col == "unit2"){
					o.unit = v;
				}else if(col == "fmt2"){
					o.fmt = v;
				}else if(col == "rate2"){
					o.rate = v;
				}else if(col == "mergeData"){
					o.mergeData = v;
        }else if(col === "y2max"){
          o.max = v;
        }else if(col === 'y2min'){
          o.min = v;
        }
        this.chartView();
      }else if(col == "mkpi"){
         c.mkpi = v;
         this.clearData();
      }else if(col ==='clear'){
        if(v == true){
         utils.delCompData(this.comp);
         this.clearData();
         this.$notify.success("数据清除成功");
        }
      }else if(col === 'clearall'){
        if(v == true){
          $(this.prop.seriesColors).each((a, b)=>{
            b.value = null;
          });
          c.colors = {};
          //this.prop.ser
          this.chartView();
        }
      }else if(col ==='seriesColors'){
         //赋值给组件的 colors 对象
          let colors = {};
          $(this.prop.seriesColors).each((a, b)=>{
            if(b.value){
              colors[b.text] = b.value;
            }
          });
          c.colors = colors;
          this.chartView();
      }else if(col === 'kpiCustomized'){
        this.comp.kpiCustomized = v;
      }else if(prop === 'title'){
        this.comp.title = v;
        this.compRender();
      }else{
        alert("字段 " + col + " 未定义处理对象。");
      }
      this.setUpdate();
    },
    serSeriesVal(){
       let comp = this.comp.comp;
       let currSeries = this.prop.dataLabelSeries;
       let p = this.prop;
        //更新值
        var tlabel = comp.chartLabels?comp.chartLabels[currSeries]:null;  //当前系列
        //dataLabel
        if(tlabel&&tlabel.dataLabel==true){
          p.dataLabel = true;
        }else{
          p.dataLabel = false;
        }
        //dataLabelPosition
        if(tlabel&&tlabel.dataLabelPosition){
          p.dataLabelPosition = tlabel.dataLabelPosition;
        }else{
          p.dataLabelPosition = null;
        }
        //dataLabelSize
        if(tlabel&&tlabel.dataLabelSize){
          p.dataLabelSize = tlabel.dataLabelSize;
        }else{
          p.dataLabelSize = 14;
        }
        //dataLabelColor
        if(tlabel&&tlabel.dataLabelColor){
          p.dataLabelColor = tlabel.dataLabelColor;
        }else {
          p.dataLabelColor = null;
        }
        //dataLabelWeight
        if(tlabel&&tlabel.dataLabelWeight == true){
          p.dataLabelWeight = true;
        }else{
          p.dataLabelWeight = false;
        }
    },
    chgtable(){
      let c = this.comp;
      if(c.comp && c.comp.tid){
        this.$refs['modifyCompTableForm'].showDialog((table)=>{
          c.comp.tid = table.tableId;
          c.comp.tname = table.tableName;
          c.comp.tincome = table.income;
          if(c.comp.mkpiJson){
            $(c.comp.mkpiJson).each((a, b)=>{
              b.tid = table.tableId;
            });
          }
          if(c.comp.kpiJson){
            $(c.comp.kpiJson).each((a, b)=>{
              if(b){
                b.tid = table.tableId;
              }
            });
          }
          if(c.comp.chartJson.xcol && c.comp.chartJson.xcol.tid){
            c.comp.chartJson.xcol.tid = table.tableId;
          }
          if(c.comp.chartJson.scol && c.comp.chartJson.scol.tid){
            c.comp.chartJson.scol.tid = table.tableId;
          }
          this.setUpdate();
          this.chartView();
        });
      }
    },
  },
  watch: {

  }
}
</script>

<style lang="less" scoped>

</style>
