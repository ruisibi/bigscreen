/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

/**
 * 纵表指标DTO
 * @Author huangqin
 * @Date 2023/3/2 8:41 下午
 */
public class VerticalKpiDto {

    private String code; //纵表指标 code
    private String alias; //纵表指标 别名
    private String aggre; //纵表指标聚合方式
    private String distinctCol; //指标的 distinctCol 字段

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAggre() {
        return aggre;
    }

    public void setAggre(String aggre) {
        this.aggre = aggre;
    }

    public String getDistinctCol() {
        return distinctCol;
    }

    public void setDistinctCol(String distinctCol) {
        this.distinctCol = distinctCol;
    }
}
