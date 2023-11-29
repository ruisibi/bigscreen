/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.bigscreen;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.bigscreen.Resource;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.service.bigscreen.WeatherService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @ClassName WeatherController
 * @Description WeatherController
 * @Author huangqin
 * @Date 2023/1/3 2:13 下午
 */
@Controller
@RequestMapping(value = "/bigscreen")
public class WeatherController extends BaseController {

    @Autowired
    private WeatherService weatherService;

    @RequestMapping(value="/weather/get.action")
    public @ResponseBody
    Object get(String city){
        try {
            Map<String, Object> m = weatherService.getWeather(city);
            return super.buildSucces(m);
        }catch (Exception ex){
            return super.buildError(ex.getMessage());
        }
    }
}
