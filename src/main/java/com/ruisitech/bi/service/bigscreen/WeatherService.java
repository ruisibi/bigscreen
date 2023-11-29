/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.bigscreen;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 天气Service 获取天气信息
 * @Description WeatherService
 * @Author gudapeng
 * @Date 2023/1/3 11:01 上午
 */
@Service
public class WeatherService {

    private static Logger log = LogManager.getLogger(WeatherService.class);

    public Map<String, Object> getWeather(String city) throws IOException, ParserException {
        //请注意：此地址只为测试功能使用，不可商用，用户应该申请自己的天气API，并返回 city, weather, temperature 三个值
        String apiUrl = "https://tianqiapi.com/api.php?style=te&skin=pitaya";
        if(city != null && city.length() > 0){
            apiUrl += "&city="+city;
        }
        String context = this.doGetMethod(apiUrl);
        Document doc = Jsoup.parse(context);
        Map<String, Object> m = new HashMap<>();
        Elements elements = doc.getElementsByTag("em");
        for(int i=0; i<elements.size(); i++){
            Element ele = elements.get(i);
            String str = ele.text();
            if(i == 0){
                m.put("city", str);
            }else if(i == 1){
                m.put("weather", str);
            }else if(i == 2){
                m.put("temperature", str);
            }
        }
        return m;
    }

    public String doGetMethod(String url) throws IOException {
        CloseableHttpClient httpclient =  HttpClients.createDefault();
        HttpGet hg = new HttpGet(url);
        hg.addHeader("Content-Type", "text/html;charset=utf-8");
        try{
            CloseableHttpResponse response = httpclient.execute(hg);
            InputStream is = response.getEntity().getContent();
            String ret = IOUtils.toString(is, "utf-8");
            is.close();
            response.close();
            return ret;
        }catch (IOException ex){
            log.error("URL获取异常",ex);
            throw ex;
        }
    }
}
