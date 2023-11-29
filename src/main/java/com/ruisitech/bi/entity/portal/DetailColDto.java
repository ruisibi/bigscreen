/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.util.RSBIUtils;

/**
 * @ClassName DetailColDto
 * @Description DetailColDto
 * @Author hq
 * @Date 2020/5/17 11:40
 */
public class DetailColDto {
    private String id;
    private String name;
    private String tname;
    private String type;
    private String expression;
    private String dispName;  //显示名
    private String align;
    private String fmt;
    private String funcname;
    private String code;

    public void checkSql(TableInfoVO tinfo){
        RSBIUtils.processSql(name);
        //通过name获取表达式
        EtlTableMetaCol col = tinfo.findColByAlias(name);
        this.expression = col.getExpression();
        //RSBIUtils.processSql(expression);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDispName() {
        return dispName;
    }

    public void setDispName(String dispName) {
        this.dispName = dispName;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public String getFuncname() {
        return funcname;
    }

    public void setFuncname(String funcname) {
        this.funcname = funcname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
