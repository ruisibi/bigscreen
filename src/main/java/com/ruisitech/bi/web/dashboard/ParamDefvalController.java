/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.dashboard;

import com.ruisitech.bi.service.dashboard.ParamDefvalService;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 查询参数默认值
 * @ClassName ParamDefvalController
 * @Description ParamDefvalController
 * @Author huangqin
 * @Date 2021/1/13 10:10 下午
 */
@Controller
@RequestMapping(value = "/dashboard")
public class ParamDefvalController extends BaseController {

    private static Logger logger = LogManager.getLogger(ParamDefvalController.class);

    @Autowired
    private ParamDefvalService service;

    @PostMapping(value={"/queryParamDefvalue.action", "/share/queryParamDefvalue.action"})
    public @ResponseBody
    Object queryParamDefvalue(@RequestBody Map<String, Object> param) {
        try {
            return this.buildSucces(service.queryDefval(param));
        }catch (Exception ex){
            logger.error("查询参数默认值错误", ex);
            return super.buildError(ex.getMessage());
        }
    }

    @PostMapping(value={"/queryOneDefvalue.action", "/share/queryOneDefvalue.action"})
    public @ResponseBody
    Object queryOneDefvalue(@RequestBody Map<String, Object> param) {
        try {
            Integer tid = new Integer(param.get("tid").toString());
            String col = param.get("col").toString();
            return this.buildSucces(service.queryOneParamdefval(tid, col));
        }catch (Exception ex){
            logger.error("查询参数默认值错误", ex);
            return super.buildError(ex.getMessage());
        }
    }

}
