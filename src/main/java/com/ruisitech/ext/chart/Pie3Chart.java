/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.chart;

import com.rsbi.ext.engine.view.emitter.chart.AbstractChartEmitter;
import com.rsbi.ext.engine.view.emitter.chart.ChartEmitter;

import java.util.Map;

/**
 * 用户自定义饼图， 馆藏图书统计
 * @Author huangqin
 * @Date 2023/3/28 2:55 下午
 */
public class Pie3Chart extends AbstractChartEmitter implements ChartEmitter {

    @Override
    public int createChartJS(boolean toJSON) {

        //生成JSON
        out.println("			{");
        out.println("                top: 'center',");
        out.println("                left: 'center',");
        out.println("                series: [");
        out.println("                    {");
        out.println("                        type: 'pie',");
        out.println("                        radius: ['60%', '80%'],");
        out.println("                        avoidLabelOverlap: false,");
        out.println("                        labelLine: {");
        out.println("                            show: false");
        out.println("                        },");
        out.println("                        label: {");
        out.println("                            show: true,");
        out.println("                            position: 'outside',");
        out.println("                            formatter: 'f$function (params) { return `{a|${params.percent.toFixed(0)}%}`;}',");
        out.println("                            color: '#fff',");
        out.println("                            fontSize: 14,");
        out.println("                            rich: {");
        out.println("                                a: {");
        out.println("                                    backgroundColor: 'rgba(0, 0, 0, 0.6)',");
        out.println("                                    borderRadius: 4,");
        out.println("                                    padding: 6");
        out.println("                                }");
        out.println("                            }");
        out.println("                        },");
        out.println("                        labelLine: {");
        out.println("                            show: true,");
        out.println("                            length: -5, // 设置引导线长度");
        out.println("                            length2: -10, // 设置引导线第二段长度");
        out.println("                            lineStyle: {");
        out.println("                                color: 'transparent'");
        out.println("                            }");
        out.println("                        },");
        out.println("                        startAngle: 0,");
        out.println("                        clockwise: true,");
        out.println("                        color: [");
        out.println("                            \"f$new echarts.graphic.LinearGradient(1, 1, 0, 0, [ { offset: 0, color: 'rgba(37, 190, 238, 1)' },{ offset: 1, color: 'rgba(37, 190, 238, 0)' } ])\",");
        out.println("                            \"f$new echarts.graphic.LinearGradient(0, 0, 1, 1, [ { offset: 0, color: 'rgba(35, 128, 238, 1)' }, { offset: 1, color: 'rgba(35, 128, 238, 0)' } ])\",");
        out.println("                            \"f$new echarts.graphic.LinearGradient(0, 0, 1, 1, [{ offset: 0, color: 'rgba(76, 210, 43, 1)' }, { offset: 1, color: 'rgba(76, 210, 43, 0)' } ])\"");
        out.println("                        ],");
        out.println("                        data: [");

        //生成数据
        String dataString = "";
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            Object xcolValue = map.get(chart.getXcol());
            Object ycolValue = map.get(chart.getYcol());

            if (i >= config.getXcnt_Num()) {
                break;
            }

            //饼图
            dataString += "{ name: \""+ (chart.getXcol() == null ? "合计":xcolValue) +"\",";
            dataString += "	value: "+ ycolValue +"}";

            if (i == (dataList.size() - 1) || i == config.getXcnt_Num() - 1){

            }else{
                dataString += ",";
            }
        }
        out.println(dataString);
        out.println("                        ],");
        out.println("                    }");
        out.println("                ]");
        out.println("            }");

        return 0;
    }
}
