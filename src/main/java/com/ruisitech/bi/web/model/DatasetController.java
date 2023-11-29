/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.model;

import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 数据集
 * @Author huangqin
 * @Date 2022/11/15 3:26 下午
 */
@Controller
@RequestMapping(value = "/model")
public class DatasetController extends BaseController {

    @Autowired
    private DatasetService datasetService;

    private static Logger log = LogManager.getLogger(DatasetController.class);

    @RequestMapping(value="/dataset/save.action")
    public @ResponseBody
    Object save(@RequestBody EtlTableMeta meta){
        try {
            datasetService.save(meta);
            return super.buildSucces();
        }catch (Exception ex){
            log.error("出错了", ex);
            return super.buildError(ex.getMessage());
        }
    }

    @RequestMapping(value="/dataset/update.action")
    public @ResponseBody
    Object update(@RequestBody EtlTableMeta meta){
        try {
            datasetService.update(meta);
            return super.buildSucces();
        }catch (Exception ex){
            log.error("出错了", ex);
            return super.buildError(ex.getMessage());
        }
    }

    @RequestMapping(value="/dataset/delete.action")
    public @ResponseBody
    Object delete(Integer id){
        datasetService.delete(id);
        return super.buildSucces();
    }
}
