/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

/**
 * 日期比较dto，用在指标计算 - 比较指定日期 模块
 * @Author huangqin
 * @Date 2022/6/14 11:34 上午
 */
public class DateCompareDto {
    private String valtype;  //值类型
    private String val;  //值
    private String param; //关联参数的参数ID
    private String paramType; //参数类型
    private String name; //字段名称
    private String code; //回调函数

    public String getValtype() {
        return valtype;
    }

    public void setValtype(String valtype) {
        this.valtype = valtype;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
}
