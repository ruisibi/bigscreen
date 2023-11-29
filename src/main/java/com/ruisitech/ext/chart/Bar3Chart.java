/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.chart;

import com.rsbi.ext.engine.view.emitter.chart.AbstractChartEmitter;
import com.rsbi.ext.engine.view.emitter.chart.ChartEmitter;
import com.rsbi.ext.engine.view.emitter.chart.ChartUtils;
import com.rsbi.ext.engine.view.emitter.chart.highcharts.LineChart;

import java.util.*;

/**
 * 用户自定义条形图， 本周借还册数
 * @Author huangqin
 * @Date 2023/3/28 2:55 下午
 */
public class Bar3Chart extends AbstractChartEmitter implements ChartEmitter {

    private List<Object> sers = new ArrayList<>();  //图形序列/用来 echarts 的 图例上

    private void analyseData(List<Map<String, Object>> list, String xcol, String ycol, String scol){
        List<String> scolList = new LinkedList<String>();

        for (Map<String, Object> map : list) {
            Object xcolValue = map.get(xcol);
            String scolValue = (String) map.get(scol);

            //加入X列, 根据xcnt控制数量
            if(xcolList.size() < config.getXcnt_Num()) {
                if (!xcolList.contains(xcolValue)) {
                    xcolList.add(xcolValue);
                }
            }

            //加入S列和U列
            if(scol != null){
                if (!scolList.contains(scolValue)) {
                    scolList.add(scolValue);
                    sers.add(scolValue);

                    Map mapTemp = new HashMap<String, String>();
                    mapTemp.put("scolValue", scolValue);

                    if(!(dataInfoList.size() > LineChart.maxsercnt)){
                        dataInfoList.add(mapTemp);
                    }
                }
            }
        }
        //bar 图反转 xcol
        //Collections.reverse(xcolList);
    }

    private String setXcolDesc(List<Object> xcolLst){
        //没有x轴，直接返回合计
        if(chart.getXcol() == null || chart.getXcol().length() == 0){
            return "'合计'";
        }
        String xcolDesc = "";

        int size = xcolLst.size();
        for (int i = 0; i < xcolLst.size() && i< config.getXcnt_Num(); i++) {
            xcolDesc += "\"" + xcolLst.get(i) + "\"";

            if (i == (size - 1) || i == config.getXcnt_Num() - 1) {

            }else{
                xcolDesc += ",";
            }
        }

        return xcolDesc;
    }

    @Override
    public int createChartJS(boolean toJSON) {
        sers.clear();
        //先计算max
        double max = 0D;
        for(Map<String, Object> dt : chart.loadOptions()){
            double v = ChartUtils.getKpiValue(dt, chart.getYcol());
            if(v > max){
                max = v;
            }
        }
        max = max * 1.2;
        this.analyseData(chart.loadOptions(), chart.getXcol(), chart.getYcol(), chart.getScol());
        //生成JSON
        out.println(" { ");
        out.println("grid: { left: 10, bottom: 10, right: 10, top: 40, containLabel: true},");
        out.println(" legend: {");
        out.println("   data:["+ChartUtils.list2string(sers)+"],");
        if(config.getLegendLayout() != null && config.getLegendLayout().length() > 0){
            out.println("orient:'"+config.getLegendLayout()+"',");
        }
        out.print("textStyle:{color:'"+ChartUtils.getChartlegendColor(config)+"'},");
        out.println(ChartUtils.getChartLegendPosition(config));
        out.println("  },");
        out.println("xAxis: {");
        out.println("type: 'value',");
        //设置最大最小值，让两边对齐
        out.println("max:"+Math.round(max)+",");
        out.println("min:-"+Math.round(max)+",");
        out.println("axisLine: { show: true, lineStyle: {  color: 'rgba(36, 55, 103, 1)' } },");
        out.println("axisLabel: {  color: 'rgba(140, 157, 200, 1)', fontSize: 11,  margin: 4, formatter: 'f$function (value) {  return value < 0 ? -value : value; }'   },");
        out.println("splitLine: { show: true, lineStyle:{color: 'rgba(36, 55, 103, 1)'}}},");
        out.println("yAxis: {");
        out.println("type: 'category',");
        out.println("inverse: true,");
        out.println("axisTick:{show: false},");
        out.println("axisLine:{show: true, lineStyle:{color: 'rgba(36, 55, 103, 1)'}},");
        out.println("axisLabel:{color:'rgba(140, 157, 200, 1)',fontSize: 11,margin: 10},");
        out.print("data:[");
        out.print(setXcolDesc(this.xcolList));
        out.println("]");
        out.println("},");
        out.println("series:[");
        //输出图形系列，这种图形只需要两个系列
        for (int i = 0; i < dataInfoList.size(); i++) {
            Map<String, Object> map = dataInfoList.get(i);
            Object scolValue = (Object) map.get("scolValue");

            //系列数据值
            StringBuffer ycolDataStr = new StringBuffer();
            //获取数据值字符串
            int index = 0;
            for (int j = 0; j < this.xcolList.size(); j++) {
                Object xval = this.xcolList.get(j);
                Object ycolValueTemp = ChartUtils.findRow(chart.getXcol(), chart.getYcol(), chart.getScol(), xval, scolValue , dataList);

                ycolDataStr.append( (i == 1 && ycolValueTemp != null ? "-":"") + ycolValueTemp+ ",");
                index ++;
                if(index >= config.getXcnt_Num()){
                    break;
                }
            }
            if (ycolDataStr.lastIndexOf(",") >= 0) {
                ycolDataStr.deleteCharAt(ycolDataStr.lastIndexOf(","));
            }

            out.println(" {");
            out.println(" type: 'bar',");
            out.println("  stack: 'stack1',");
            out.println("  name:'"+scolValue+"',");
            out.println("  barWidth: 12,");
            if(i == 0) {
                out.println("itemStyle: {borderRadius: [0, 6, 6, 0],  color: { type: 'linear', x: 1, y: 0, x2: 0, y2: 0,  colorStops: [ { offset: 0, color: 'rgba(70, 100, 255, 1)' },{ offset: 1, color: 'rgba(70, 100, 255, 0)' }]}},");
                out.println("label:{show: true, position: 'outside',fontSize: 10,color: 'rgba(128, 163, 255, 1)', distance: 4},");
            }else if(i == 1){
                out.println("itemStyle: {borderRadius: [6, 0, 0, 6], color: { type: 'linear', x: 0, y: 0, x2: 1,y2: 0, colorStops: [ { offset: 0, color: 'rgba(49, 228, 252, 1)' },{ offset: 0.9, color: 'rgba(49, 228, 252, 0)' },{ offset: 1, color: 'rgba(49, 228, 252, 0)' } ]}},");
                out.println("label:{ show: true, position: 'outside',fontSize: 10,color: 'rgba(48, 227, 252, 1)',distance: 4, formatter: 'f$function (params) { return Math.abs(params.value)}'},");
            }
            out.println("data: ["+ycolDataStr+"]");
            out.println(" }");
            if(i != dataInfoList.size() - 1){
                out.println(",");
            }
            if(i >= 1){  //只保留两个图例
                break;
            }
        }
        out.println("]");
        out.println("}");

        return 0;
    }
}
