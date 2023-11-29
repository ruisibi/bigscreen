/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.hongwangweb.com/licenses/index.html
 */
package com.ruisitech.bi.service.dashboard;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.view.context.grid.PageInfo;
import com.rsbi.ext.engine.view.exception.ExtConfigException;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.DataSourceService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询参数默认值
 * @ClassName ParamDefvalService
 * @Description ParamDefvalService
 * @Author huangqin
 * @Date 2021/1/13 10:11 下午
 */
@Service
public class ParamDefvalService {

    @Autowired
    private TableCacheService cacheService;

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DatasetService datasetService;

    public Map<String, Object> queryDefval(Map<String, Object> params) throws ExtConfigException {
        Map<String, Object> ret = new HashMap<>();
        for(Map.Entry<String, Object> p : params.entrySet()){
            Map<String, Object> param = (Map<String, Object>)p.getValue();
            Integer tid = new Integer(param.get("tid").toString());
            String col = param.get("col").toString();
            ret.put(p.getKey(), this.queryOneParamdefval(tid, col));
        }
        return ret;
    }

    public Object queryOneParamdefval(Integer tid, String col) throws ExtConfigException {
        TableInfoVO tvo = cacheService.getTableInfo(tid);
        //检测SQL注入
        RSBIUtils.processSql(col);
        EtlTableMetaCol tableCol = tvo.findColByAlias(col);
        String sql = "select " + (tableCol.getExpression()==null?tableCol.getColName():tableCol.getExpression()) +" as \"val\" from " + datasetService.createTableSql(tvo);
        List<Map<String, Object>> ret =  null;
        if(tvo.getDsource() != null){
            PageInfo page = new PageInfo();
            page.setCurtpage(0);
            page.setPagesize(10);
            ret = dataSourceService.querySqlUseDataSource(sql, page, tvo.getDsource());
        }else {
            ret = daoHelper.queryForList(sql);
        }
        if(ret.size() == 0){
            throw new RuntimeException("参数表"+tvo.getTname()+"无数据");
        }else if(ret.size() > 1){
            //throw new RuntimeException("参数表"+tvo.getTname()+"只能有一行数据");
        }
        return ret.get(0).get("val");
    }
}
