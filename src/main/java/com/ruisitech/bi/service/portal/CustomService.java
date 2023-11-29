/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.portal;

import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.bireport.KpiDto;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.bireport.VerticalKpiDto;
import com.ruisitech.bi.entity.portal.CustomQuery;
import com.ruisitech.bi.service.bireport.BaseCompService;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.DataSourceService;
import com.ruisitech.bi.service.etl.QueryRestService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.ext.service.DataControlInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 生成自定组件数据的service
 * @ClassName TextService
 * @Description TextService
 * @Author huangqin
 * @Date 2021/12/10 1:59 下午
 */
@Service
public class CustomService extends BaseCompService {

    @Autowired
    private DataControlInterface dataControl; //数据权限控制

    @Autowired
    private TableCacheService cacheService;


    @Autowired
    private QueryRestService restService;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private DataSourceService dataSourceService;

    private static Logger log = LogManager.getLogger(CustomService.class);

    /**
     * 查询自定义组件的数据
     * @param dto
     */
    public List<Map<String, Object>> queryData(CustomQuery dto) throws Exception {
        TableInfoVO tvo = cacheService.getTableInfo(dto.getTid());
        String sql = this.createSql(dto, tvo);
        if(tvo.isRest()){
            return restService.queryData(sql, true, tvo.getTname(), tvo.getTid(), null, null);
        }else{
            return dataSourceService.querySqlUseDataSource(sql, null, tvo.getDsource());
        }
    }


    public String createSql(CustomQuery custom, TableInfoVO tinfo){
        boolean qEs = tinfo.isSyncEs();
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        if(tinfo.isVertical()) {
            sql.append(tinfo.getKpiCodeColumn());
            sql.append(",");
        }
        //查询指标
        if(tinfo.isVertical()){
            sql.append("sum(");
            sql.append(tinfo.getKpiValueColumn());
            sql.append(")");
            sql.append(" as ");
            sql.append(tinfo.getKpiValueColumn());
        }else {
            for(int i=0; i<custom.getKpis().size(); i++) {
                KpiDto kpiDto = custom.getKpis().get(i);
                sql.append(kpiDto.getCol_name());
                Integer rate = kpiDto.getRate();
                if (rate != null) {
                    sql.append("/" + rate);
                }
                sql.append(" as ");
                sql.append(kpiDto.getAlias());
                if(i != custom.getKpis().size() - 1){
                    sql.append(",");
                }
            }
        }
        //维度
        if(custom.getDim() != null){
            sql.append(",");
            DimDto dim = custom.getDim();
            String key = dim.getTableColKey();
            String txt = dim.getTableColName();
            if(key != null && txt != null && key.length() >0 && txt.length() >0){
                sql.append(key+" "+dim.getAlias()+", " + txt + " "+dim.getAlias()+"2");
            }else{
                sql.append(" "+(qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname())+" "+dim.getAlias()+"");
            }
        }
        sql.append(" from ");
        sql.append(datasetService.createTableSql(tinfo));
        sql.append(" where 1=1 ");

        if(dataControl != null){
            String ret = dataControl.process(RSBIUtils.getLoginUserInfo(), tinfo);
            if(ret != null){
                sql.append(ret + " ");
            }
        }
        sql.append(super.dealCubeParams(custom.getCompParams(), "bigscreen", "dim", tinfo));

        if(custom.getDim() == null) {
            if (tinfo.isVertical()) {
                sql.append(" group by " + tinfo.getKpiCodeColumn());
            }
        }else{
            sql.append(" group by ");
            if (tinfo.isVertical()) {
                sql.append( tinfo.getKpiCodeColumn() + ",");
            }
            DimDto dim = custom.getDim();
            String key = dim.getTableColKey();
            String txt = dim.getTableColName();
            if(key != null && txt != null && key.length() >0 && txt.length() >0){
                sql.append(key+", " + txt);
            }else{
                sql.append(tinfo.isEs()?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname());
            }
        }

        return sql.toString();
    }

}
