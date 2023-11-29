/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

/**
 * 交叉表的分组维，区别于 交叉表 dims 里的其他维度
 * 用在数据last/first等聚合中
 * @ClassName GroupDto
 * @Description GroupDto
 * @Author huangqin
 * @Date 2021/3/25 8:28 下午
 */
public class GroupDto {

    private String col; //分组字段
    private String alias; //别名
    private String order; //分组排序

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
