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
import java.util.stream.Collectors;

/**
 * 用户自定义地图， 泰兴化工分布图
 * @Author huangqin
 * @Date 2023/3/28 2:55 下午
 */
public class Map2Chart extends AbstractChartEmitter implements ChartEmitter {

    private List<Object> sers = new ArrayList<>();  //图形序列/用来 echarts 的 图例上


    @Override
    public int createChartJS(boolean toJSON) {
        this.analyseData(chart.loadOptions(), chart.getXcol(), chart.getYcol(), chart.getScol());
        //输出 json
        out.println("{");
        out.println("    \"geo\": {");
        out.println("   silent: true, ");
        out.println("        \"itemStyle\": {");
        out.println("            \"borderColor\": \"#0d87f5\",");
        out.println("            \"areaColor\": \"#09146e\",");
        out.println("            \"borderWidth\": 2,");
        out.println("            \"emphasis\": {");
        out.println("                \"areaColor\": \"#09146e\"");
        out.println("            }");
        out.println("        },");
        out.println("        \"zoom\": 1.2,");
        out.println("        \"label\": { show:true, color:'#ffffff'");
        out.println("        },");
        out.println("        \"roam\": true,");
        out.println("        \"map\": \"321200\",");
        out.println("       regions:[{name:'泰兴市', itemStyle:{areaColor:'#f38a2b', borderWidth:1}}]");
        out.println("    },");
        out.println("    \"series\": [");

        for (int i = 0; i < dataInfoList.size(); i++) {
            Map<String, Object> map = dataInfoList.get(i);
            Object scolValue =  map.get("scolValue");
            List<Map<String, Object>> dts = this.dataList.stream().filter(m->m.get(chart.getScol()).equals(scolValue)).collect(Collectors.toList());
            out.println("        {");
            out.println("            \"symbol\": \"circle\",");
            out.println("            \"data\": [");

            for(int j=0; j<dts.size(); j++){
                Map<String, Object> dt = dts.get(j);
                Object xval = dt.get(chart.getXcol());
                Object ycolValueTemp = ChartUtils.findRow(chart.getXcol(), chart.getYcol(), chart.getScol(), xval, scolValue , dataList);
                out.println("                {");
                out.println("                    \"name\": \""+xval+"\",");
                out.println("                    \"value\": \"f$echartsUtils.converMapData2('"+xval+"',"+ycolValueTemp+")\"");
                out.println("                }");
                if( j != dts.size() - 1){
                    out.println(",");
                }
            }
            out.println("            ],");
            out.println("            \"symbolSize\": \"f$function(data){return 20}\",");
            //out.println("            \"selectedMode\": \"single\",");
            out.println("            \"name\": \""+scolValue+"\",");
            out.println("            \"itemStyle\": {");
            if(i == 0) {
                out.println("                \"color\": \"#f20000\"");
            }else{
                out.println("                \"color\": \"#ffff00\"");
            }
            out.println("            },");
            out.println("            \"zoom\": 1.2,");
            out.println("            \"coordinateSystem\": \"geo\",");
            out.println("            \"label\": {");
            out.println("                \"formatter\": \"{b}\",");
            out.println("                \"color\": \"#822A2A\",");
            out.println("                \"show\": false,");
            out.println("                \"fontSize\": 12,");
            out.println("                \"position\": \"inside\",");
            out.println("                \"fontWeight\": \"normal\"");
            out.println("            },");
            out.println("            \"type\": \"scatter\",");
            out.println("            \"roam\": true");
            out.println("        }");
            if(i != dataInfoList.size() - 1){
                out.println(" ,");
            }
        }

        out.println("    ],");
        out.println("    \"tooltip\": {");
        //out.println("        \"formatter\": \"f$function(params){  var x = null;\treturn echartsUtils.mapToolTips(params, '#,###', '', x)}\",");
        out.println("        \"trigger\": \"item\"");
        out.println("    },");
        out.println("    \"textStyle\": {");
        out.println("        \"fontStyle\": \"normal\"");
        out.println("    },");
        out.println("    \"animation\": true");
        out.println("}");

        return 0;
    }

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
}
