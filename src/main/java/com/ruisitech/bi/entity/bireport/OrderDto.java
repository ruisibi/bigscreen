/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

/**
 * 排序对象
 * @Author huangqin
 * @Date 2022/10/24 5:19 下午
 */
public class OrderDto {
    private String col;  //字段
    private String order; //排序方式

    private boolean curSortKpi; //当前排序字段是指标
    private String parentId;
    private String parentValue;
    private String aggre;
    private String sort;
    private String alias;

    public OrderDto(String col, String order){
        this.col = col;
        this.order = order;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean isCurSortKpi() {
        return curSortKpi;
    }

    public void setCurSortKpi(boolean curSortKpi) {
        this.curSortKpi = curSortKpi;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentValue() {
        return parentValue;
    }

    public void setParentValue(String parentValue) {
        this.parentValue = parentValue;
    }

    public String getAggre() {
        return aggre;
    }

    public void setAggre(String aggre) {
        this.aggre = aggre;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
