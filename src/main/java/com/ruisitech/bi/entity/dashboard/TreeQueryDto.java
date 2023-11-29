/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.dashboard;

/**
 * @ClassName TreeQueryDto
 * @Description TreeQueryDto
 * @Author huangqin
 * @Date 2022/7/27 11:01 上午
 */
public class TreeQueryDto {

    private Integer matchTable;
    private String tableIdCol;
    private String tablePidCol;
    private String tableNameCol;
    private String tablePidValue;

    public Integer getMatchTable() {
        return matchTable;
    }

    public void setMatchTable(Integer matchTable) {
        this.matchTable = matchTable;
    }

    public String getTableIdCol() {
        return tableIdCol;
    }

    public void setTableIdCol(String tableIdCol) {
        this.tableIdCol = tableIdCol;
    }

    public String getTablePidCol() {
        return tablePidCol;
    }

    public void setTablePidCol(String tablePidCol) {
        this.tablePidCol = tablePidCol;
    }

    public String getTableNameCol() {
        return tableNameCol;
    }

    public void setTableNameCol(String tableNameCol) {
        this.tableNameCol = tableNameCol;
    }

    public String getTablePidValue() {
        return tablePidValue;
    }

    public void setTablePidValue(String tablePidValue) {
        this.tablePidValue = tablePidValue;
    }
}
