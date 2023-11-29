/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import java.util.List;

/**
 * 在 ES 纵表中查询，需要用到的维度值对象
 * @Author huangqin
 * @Date 2023/2/19 10:23 上午
 */
public class DimValuesDto {

    private List<Object> values;

    private int valType; //1  数字， 2 字符串

    private DimDto dim; //当前需要过滤的维度

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public int getValType() {
        return valType;
    }

    public void setValType(int valType) {
        this.valType = valType;
    }

    public DimDto getDim() {
        return dim;
    }

    public void setDim(DimDto dim) {
        this.dim = dim;
    }
}
