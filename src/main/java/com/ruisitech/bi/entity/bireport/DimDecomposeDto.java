/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.portal.CompParamDto;

import java.util.List;

/**
 * @ClassName DimDecomposeDto
 * @Description DimDecomposeDto
 * @Author huangqin
 * @Date 2020/10/5 11:52 上午
 */
public class DimDecomposeDto {

    private List<CompParamDto> params;

    private Integer tid;

    private Integer dimId;

    public List<CompParamDto> getParams() {
        return params;
    }

    public void setParams(List<CompParamDto> params) {
        this.params = params;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public Integer getDimId() {
        return dimId;
    }

    public void setDimId(Integer dimId) {
        this.dimId = dimId;
    }
}
