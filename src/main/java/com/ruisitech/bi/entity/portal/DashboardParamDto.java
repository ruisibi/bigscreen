/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.util.RSBIUtils;

import java.util.Map;

/**
 * 仪表盘中使用的参数，仪表盘筛选(非 rest接口)
 * @author hq
 *
 */
public class DashboardParamDto {

    private String key;
    private String value;
    private String value2; //在 between 时起作用
    private String colname; //参数对应表的字段
    private String id;
    private Integer tid;
    private String from;
    private String valType;  //维度value 字段的类型，用在拼接sql中，判断是否增加单引号
    private String keyDesc;
    private String valueDesc;
    private String fromCol;
    private String oper; //筛选方式 =/between/in 3种形式
    private String tableColKey; //对应立方体的 维度 key 字段
    private String tableColName; //对应立方体的 维度 text 字段

    /**
     * 检测SQL注入
     */
    public void checkSql(Map<String, String> colAlias){
        //RSBIUtils.processSql(colname);
        RSBIUtils.processSql(value);
        //RSBIUtils.processSql(fromCol);
        RSBIUtils.processSql(value2);
        RSBIUtils.processSql(oper);
        RSBIUtils.processSql(tableColKey);
        RSBIUtils.processSql(tableColName);
        colname = colAlias.get(key);
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Integer getTid() {
        return tid;
    }
    public void setTid(Integer tid) {
        this.tid = tid;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getValType() {
        return valType;
    }
    public void setValType(String valType) {
        this.valType = valType;
    }
    public String getKeyDesc() {
        return keyDesc;
    }
    public void setKeyDesc(String keyDesc) {
        this.keyDesc = keyDesc;
    }
    public String getValueDesc() {
        return valueDesc;
    }
    public void setValueDesc(String valueDesc) {
        this.valueDesc = valueDesc;
    }
    public String getColname() {
        return colname;
    }
    public void setColname(String colname) {
        this.colname = colname;
    }
    public String getFromCol() {
        return fromCol;
    }
    public void setFromCol(String fromCol) {
        this.fromCol = fromCol;
    }
    public String getOper() {
        return oper;
    }
    public void setOper(String oper) {
        this.oper = oper;
    }
    public String getValue2() {
        return value2;
    }
    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getTableColKey() {
        return tableColKey;
    }

    public void setTableColKey(String tableColKey) {
        this.tableColKey = tableColKey;
    }

    public String getTableColName() {
        return tableColName;
    }

    public void setTableColName(String tableColName) {
        this.tableColName = tableColName;
    }
}
