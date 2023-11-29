/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.portal;

import com.ruisitech.bi.entity.portal.CustomQuery;
import com.ruisitech.bi.service.portal.CustomService;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 大屏自定义组件的后台获取数据借口，目前支持一个维度加一个指标
 * @ClassName CustomViewController
 * @Description CustomViewController
 * @Author huangqin
 * @Date 2023/7/6 5:40 下午
 */
@Controller
@RequestMapping(value = "/portal")
public class CustomViewController extends BaseController {

    private static Logger log = LogManager.getLogger(CustomViewController.class);

    @Autowired
    private CustomService customService;

    @RequestMapping(value={"/CustomView.action","/share/CustomView.action"}, method = RequestMethod.POST)
    public @ResponseBody
    Object view(@RequestBody CustomQuery dto) {
        try {
            List<Map<String, Object>> ls = customService.queryData(dto);
            return super.buildSucces(ls);
        } catch (Exception ex) {
            log.error("custom出错了。", ex);
            return super.buildError(ex.getMessage());
        }
    }
}
