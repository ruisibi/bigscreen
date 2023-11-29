/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.chart;

import com.rsbi.ext.engine.view.emitter.chart.AbstractChartEmitter;
import com.rsbi.ext.engine.view.emitter.chart.ChartEmitter;
import com.rsbi.ext.engine.view.emitter.chart.highcharts.LineChart;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用户自定义条形图， 图书馆借阅统计
 * @Author huangqin
 * @Date 2023/3/28 2:55 下午
 */
public class Bar4Chart extends AbstractChartEmitter implements ChartEmitter {

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
        }
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
        this.analyseData(chart.loadOptions(), chart.getXcol(), chart.getYcol(), chart.getScol());

        //生成JSON
        out.println("			{");
        out.println("                grid: {");
        out.println("                    left: 0, top: 0,  right: 10, bottom: 10, ");
        out.println("                    containLabel: true");
        out.println("                },");
        out.println("                xAxis: {");
        out.println("                    type: 'value',");
        //out.println("                    min: 100,");
        //out.println("                      max:200,");
        out.println("                    axisLine: {");
        out.println("                        show: true,");
        out.println("                        onZero: false,");
        out.println("                        lineStyle: {");
        out.println("                            color: 'rgba(24, 127, 172, 0.2)'");
        out.println("                        }");
        out.println("                    },");
        out.println("                    axisLabel: {");
        out.println("                        color: 'rgba(75, 146, 186, 1)',");
        out.println("                        fontSize: 12,");
        out.println("                        margin: 10");
        out.println("                    },");
        out.println("                    splitLine: {");
        out.println("                        show: true,");
        out.println("                        lineStyle: {");
        out.println("                            color: 'rgba(24, 127, 172, 0.2)'");
        out.println("                        }");
        out.println("                    },");
        out.println("                },");
        out.println("                yAxis: {");
        out.println("                    type: 'category',");
        out.println("                    offset: 0,");
        out.println("                    axisTick: {");
        out.println("                        show: false");
        out.println("                    },");
        out.println("                    axisLabel: {");
        out.println("                        color: 'rgba(75, 146, 186, 1)',");
        out.println("                        fontSize: 14,");
        out.println("                        margin: 16");
        out.println("                    },");
        out.println("                    axisLine: {");
        out.println("                        show: true,");
        out.println("                        lineStyle: {");
        out.println("                            color: 'rgba(24, 127, 172, 0.2)'");
        out.println("                        }");
        out.println("                    },");
        out.println("                    data: ["+ setXcolDesc(this.xcolList)+"],");
        out.println("                },");
        out.println("                series: [");
        out.println("                    {");
        out.println("                        type: 'bar',");
        out.println("                        barWidth: 18,");
        out.println("                        showBackground: true,");
        out.println("                        backgroundStyle: {");
        out.println("                            color: 'rgba(0, 45, 67, 1)'");
        out.println("                        },");
        out.println("                        label: {");
        out.println("                            show: true,");
        out.println("                            fontSize: 14,");
        out.println("                            position: 'inside',");
        out.println("                        },");
        out.println("                        markPoint: {");
        out.println("                            symbol: 'rect',");
        out.println("                            symbolSize: [8, 18],");
        out.println("                            data: [");
        //解析数据
        StringBuilder dataString = new StringBuilder("");
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            Object xcolValue = map.get(chart.getXcol());
            Object ycolValue = map.get(chart.getYcol());

            if (i >= config.getXcnt_Num()) {
                break;
            }
            String c = "";
            if(i == 0){
                c = "rgba(76, 210, 43, 1)";
            }else if(i == 1){
                c = "rgba(35, 128, 238, 1)";
            }else if(i == 2){
                c = "rgba(37, 190, 238, 1)";
            }
            //饼图
            dataString.append("{ coord:[ "+ycolValue+","+i+"], itemStyle:{color:'"+c+"'}}");

            if (i == (dataList.size() - 1) || i == config.getXcnt_Num() - 1){

            }else{
                dataString.append(",");
            }
        }
        out.println(dataString.toString());
        out.println("                            ],");
        out.println("                        },");
        out.println("                        data: [");

        StringBuilder dataString2 = new StringBuilder("");
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            Object xcolValue = map.get(chart.getXcol());
            Object ycolValue = map.get(chart.getYcol());

            if (i >= config.getXcnt_Num()) {
                break;
            }
            String c = "";
            if(i == 0){
                c = "rgba(76, 210, 43, 0.5)";
            }else if(i == 1){
                c = "rgba(35, 128, 238, 0.5)";
            }else if(i == 2){
                c = "rgba(37, 190, 238, 0.5)";
            }
            //饼图
            dataString2.append("{ value: "+ ycolValue +", itemStyle: { color:'"+c+"' }}");

            if (i == (dataList.size() - 1) || i == config.getXcnt_Num() - 1){

            }else{
                dataString2.append(",");
            }
        }
        out.println(dataString2.toString());
        out.println("                        ],");
        out.println("                    }");
        out.println("                ]");
        out.println("            }");

        return 0;
    }
}
