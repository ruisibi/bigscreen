/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import java.util.List;

/**
 * 交叉表的 cols/rows 中的选项
 * @Author gdp
 * @Date 2020/9/9 9:43 上午
 */
public class CrossFieldDto {

    private String id; //通过id关联维度，度量
    private Integer match; //对应的维度/指标ID
    private String desc; //用来显示的名称
    private String type; //维度(dim) 还是度量(kpi) 还是 维度分解后的维值 (kpiMatch) / kpiCalcMatch (分解后的维值计算) 还是 none 还是 note
    private Integer level; //层级，从1开始
    private String value; //节点值列表，在type是 kpiMatch 时有效，对应 CrossReport type 是 kpi
    private Integer spaceNum; //缩进
    private String fmt; //格式化
    private String expression; //表达式
    private String hideNodeCallback; //隐藏节点的回调函数

    private List<CrossFieldDto> children;  //下级节点

    /**
     * 当 type === 'kpiMatch' 时，表示此维度被分解成维值了，需要用 values 来存放分解后的维值
     */
    private List<CrossFieldDto> values;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMatch() {
        return match;
    }

    public void setMatch(Integer match) {
        this.match = match;
    }

    public List<CrossFieldDto> getChildren() {
        return children;
    }

    public void setChildren(List<CrossFieldDto> children) {
        this.children = children;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDim(){
        return "dim".equals(type);
    }

    public boolean isKpi(){
        return "kpi".equals(type);
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getSpaceNum() {
        return spaceNum;
    }

    public void setSpaceNum(Integer spaceNum) {
        this.spaceNum = spaceNum;
    }

    public List<CrossFieldDto> getValues() {
        return values;
    }

    public void setValues(List<CrossFieldDto> values) {
        this.values = values;
    }
    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getHideNodeCallback() {
        return hideNodeCallback;
    }

    public void setHideNodeCallback(String hideNodeCallback) {
        this.hideNodeCallback = hideNodeCallback;
    }
}
