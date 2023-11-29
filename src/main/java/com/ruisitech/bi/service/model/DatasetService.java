/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.model;

import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.*;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据集
 * @Author huangqin
 * @Date 2022/11/15 4:44 下午
 */
@Service
public class DatasetService extends EtlBaseService {

    public static final String cond = "[cond]";  //数据集条件关键字

    @Autowired
    private EtlTableMetaService etlTableMetaService;

    @Autowired
    private EtlTableMetaColService etlTableMetaColService;

    @Autowired
    private TableCacheService cacheService;

    @Autowired
    private DataSourceService dsService;

    @Autowired
    private MetaService metaService;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private LoggerService loggerService;

    @Transactional(rollbackFor = Exception.class)
    public void save(EtlTableMeta meta) throws Exception {
        this.saveOrUpdateTable(meta, null);

        //写日志
        Map<String, Object> pms = new HashMap<>();
        pms.put("tid", meta.getTableId());
        pms.put("tname", meta.getTableName());
        pms.put("operType", 1);
        pms.put("oper", "数据集创建表"+meta.getTableName()+",sql:"+meta.getTableSql());
        loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(EtlTableMeta meta) throws Exception {
        this.saveOrUpdateTable(meta, meta.getTableId());

        //写日志
        Map<String, Object> pms = new HashMap<>();
        pms.put("tid", meta.getTableId());
        pms.put("tname", meta.getTableName());
        pms.put("operType", 3);
        pms.put("oper", "数据集修改表"+meta.getTableName()+",sql:" + meta.getTableSql());
        loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
    }

    public String createTableSql(TableInfoVO tvo){
        return createTableSql(tvo, true);
    }

    public String createTableSql(TableInfoVO tvo, boolean replace){
        return createTableSql(tvo.getSql(), tvo.getTname(), replace);
    }

    public String createTableSql(String tableSql, String tname){
        return createTableSql(tableSql, tname, true);
    }

    /**
     * 替换tableSQL中的条件
     * @param tableSql
     * @param tname
     * @return
     */
    public String createTableSql(String tableSql, String tname, boolean replace){
        if(tableSql == null || tableSql.length() == 0){
            return tname;
        }else{
            String s = replace ? replaceSql(tableSql, "") : tableSql;
            return "(" + s+") tt";
        }
    }

    public String replaceSql(String sql){
        return replaceSql(sql, "");
    }

    public String replaceSql(String sql, String replaceStr){
        return sql.replaceAll("\\[cond\\]", java.util.regex.Matcher.quoteReplacement(replaceStr));  //把数据集条件字段替换为空
    }

    @Transactional(rollbackFor = Exception.class)
    public EtlTableMeta saveOrUpdateTable(EtlTableMeta table, Integer tid) throws Exception {
        table.setIncome("ds"); //表示来源是数据集
        //判断是插入还是查询
        if(tid == null){
            table.setCrtUser(RSBIUtils.getLoginUserInfo().getUserId());
            etlTableMetaService.insertMetaTable(table);
            if (table.getIdType() == 1) {
                table.setTableId(etlTableMetaService.maxTableId() - 1);
            }
        } else {
            etlTableMetaService.updateMetaTable(table);
        }
        //先删除字段
        etlTableMetaColService.delTableColByTableIdNotExpress(table);
        //写字段
        Integer maxId = null;
        if (table.getIdType() == 2) {
            maxId = etlTableMetaColService.maxColId();
        }
        String sql = datasetService.replaceSql(table.getTableSql());

        Integer dsource = table.getDsourceId();
        DataSource ds = null;
        if (dsource != null && dsource != -1) {
            ds = dsService.selectDataSourceByPrimaryKey(dsource);
        }
        List<DSColumn> cols = metaService.queryTableCols(sql, ds, false);
        //判断是否有 批次号，创建时间, 来源过程等 系统内建字段
        boolean hasPb = false;
        boolean hasCrtDate = false;
        boolean hasTfId = false;
        for (DSColumn c : cols) {
            if (c.getName().equals(createDate)) {
                hasCrtDate = true;
            }
            if (c.getName().equals(productBatch)) {
                hasPb = true;
            }
            if(c.getName().equals(srcTf)){
                hasTfId = true;
            }
        }
        if (hasPb) {
            throw new Exception("sql语句中不能包含系统内建字段(" + productBatch + ")");
        }
        if (hasCrtDate) {
            throw new Exception("sql语句中不能包含系统内建字段(" + createDate + ")");
        }
        if(hasTfId){
            throw new Exception("sql语句中不能包含系统内建字段(" + srcTf + ")");
        }

        List<EtlTableMetaCol> metaCols = new ArrayList<>();
        for (int i = 0; i < cols.size(); i++) {
            DSColumn col = cols.get(i);
            //忽略数据仓库表的默认字段
            if (col.getName().equals(createDate)) {
                continue;
            }
            if (col.getName().equals(productBatch)) {
                continue;
            }
            if(col.getName().equals(srcTf)){
                continue;
            }
            EtlTableMetaCol mcol = new EtlTableMetaCol();
            mcol.setColName(col.getName());
            mcol.setColType(col.getType());
            mcol.setTableId(table.getTableId());
            mcol.setColLength(col.getLength());
            mcol.setColScale(col.getScale());
            mcol.setColNote(col.getDispName());
            mcol.setDefvalue(col.getDefvalue());
            mcol.setTableName(table.getTableName());
            mcol.setColOrd(i);
            if (mcol.getIdType() == 2) {
                mcol.setColId(maxId);
                maxId++;
            }
            metaCols.add(mcol);
        }

        for (EtlTableMetaCol mcol : metaCols) {
            etlTableMetaColService.insertMetaTableCol(mcol, true);
        }
        if(tid != null) {
            cacheService.removeTableInfo(tid);
        }
        return table;
    }

    public void delete(Integer id) {
        etlTableMetaService.deleteTable(id, false);
    }
}
