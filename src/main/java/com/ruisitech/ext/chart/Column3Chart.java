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
 * 日借还量
 * @Author huangqin
 * @Date 2023/4/20 4:07 下午
 */
public class Column3Chart extends AbstractChartEmitter implements ChartEmitter {

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
        this.analyseData(chart.loadOptions(), chart.getXcol(), chart.getYcol(), chart.getScol());
        out.println("			{");
        out.println("                    grid: {left: 10, bottom: 10, right: 10, top: 40,");
        out.println("                        containLabel: true");
        out.println("                    },");
        out.println(" legend: {");
        out.println("   data:["+ChartUtils.list2string(sers)+"],");
        if(config.getLegendLayout() != null && config.getLegendLayout().length() > 0){
            out.println("orient:'"+config.getLegendLayout()+"',");
        }
        out.print("textStyle:{color:'"+ChartUtils.getChartlegendColor(config)+"'},");
        out.println(ChartUtils.getChartLegendPosition(config));
        out.println("  },");
        out.println("                    yAxis: {");
        out.println("                        type: 'value',");
        out.println("                        axisLine: {");
        out.println("                            show: true,");
        out.println("                            onZero: false,");
        out.println("                            lineStyle: {");
        out.println("                                color: 'rgba(24, 127, 172, 0.2)'");
        out.println("                            }");
        out.println("                        },");
        out.println("                        axisLabel: {");
        out.println("                            color: 'rgba(75, 146, 186, 1)',");
        out.println("                            fontSize: 12,");
        out.println("                            margin: 6");
        out.println("                        },");
        out.println("                        splitLine: {");
        out.println("                            lineStyle: {");
        out.println("                                color: 'rgba(24, 127, 172, 0.2)'");
        out.println("                            }");
        out.println("                        },");
        out.println("                    },");
        out.println("                    xAxis: {");
        out.println("                        type: 'category',");
        out.println("                        axisTick: {");
        out.println("                            show: false");
        out.println("                        },");
        out.println("                        axisLabel: {");
        out.println("                            color: 'rgba(75, 146, 186, 1)',");
        out.println("                            fontSize: 12,");
        out.println("                            margin: 12");
        out.println("                        },");
        out.println("                        axisLine: {");
        out.println("                            lineStyle: {");
        out.println("                                color: 'rgba(24, 127, 172, 0.2)'");
        out.println("                            }");
        out.println("                        },");
        out.print("data:[");
        out.print(setXcolDesc(this.xcolList));
        out.println("]");
        out.println("                    },");
        out.println("                    series: [");
        //通过数据生成图形系列
        for (int i = 0; i < dataInfoList.size(); i++) {
            Map<String, Object> map = dataInfoList.get(i);
            Object scolValue = (Object) map.get("scolValue");

            //系列数据值
            StringBuffer ycolDataStr = new StringBuffer();
            StringBuffer vals2 = new StringBuffer();
            //获取数据值字符串
            int index = 0;
            for (int j = 0; j < this.xcolList.size(); j++) {
                Object xval = this.xcolList.get(j);
                Object ycolValueTemp = ChartUtils.findRow(chart.getXcol(), chart.getYcol(), chart.getScol(), xval, scolValue , dataList);

                ycolDataStr.append( ycolValueTemp+ ",");
                vals2.append("{ coord: ["+j+", "+ycolValueTemp+"] },");
                index ++;
                if(index >= config.getXcnt_Num()){
                    break;
                }
            }
            if (ycolDataStr.lastIndexOf(",") >= 0) {
                ycolDataStr.deleteCharAt(ycolDataStr.lastIndexOf(","));
            }
            if (vals2.lastIndexOf(",") >= 0) {
                vals2.deleteCharAt(vals2.lastIndexOf(","));
            }

            out.println("                        {");
            out.println("                            name: '"+scolValue+"',");
            out.println("                            type: 'bar',");
            out.println("                            barWidth: 16,");
            out.println("                            label: {");
            out.println("                                show: true,");
            out.println("                                fontSize: 11,");
            out.println("                                position: 'top',");
            if(i == 0) {
                out.println("                                color: 'rgba(52, 199, 232, 1)',");
            }else{
                out.println("                                 color: 'rgba(76, 210, 43, 1)',");
            }
            out.println("                            },");
            out.println("                            markPoint: {");
            out.println("                                symbol: 'rect',");
            out.println("                                symbolSize: [16, 5],");
            out.println("                                itemStyle: {");
            if(i == 0) {
                out.println("                                    color: 'rgba(52, 199, 232, 1)'");
            }else{
                out.println("                                    color: 'rgba(76, 210, 43, 1)'");
            }
            out.println("                                },");
            out.println("                                data: ["+vals2+"],");
            out.println("                            },");
            out.println("                            itemStyle: {");
            if(i == 0) {
                out.println("                                color: 'rgba(52, 199, 232, 0.2)'");
            }else{
                out.println(" color: 'rgba(76, 210, 43, 0.2)'");
            }
            out.println("                            },");
            out.println("                            data: ["+ycolDataStr+"],");
            out.println("                        },");

            if(i != dataInfoList.size() - 1){
                out.println(",");
            }
        }

        out.println("                    ]");
        out.println("                }");

        return 0;
    }
}
