/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.bireport.KpiDto;

import java.util.List;

/**
 * @ClassName CustomQuery
 * @Description CustomQuery
 * @Author huangqin
 * @Date 2023/7/6 5:47 下午
 */
public class CustomQuery extends CompBaseEntity {

    private String id;
    private Integer tid;
    private String tname;
    private String tincome;  //表得来源
    private List<KpiDto> kpis;  //指标
    private DimDto dim; //维度

    private List<PortalParamDto> portalParams;  //页面的参数， 一般需要把页面参数和组件参数关联起来
    private List<CompParamDto> compParams;   //组件的参数

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

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTincome() {
        return tincome;
    }

    public void setTincome(String tincome) {
        this.tincome = tincome;
    }

    public List<KpiDto> getKpis() {
        return kpis;
    }

    public void setKpis(List<KpiDto> kpis) {
        this.kpis = kpis;
    }

    public DimDto getDim() {
        return dim;
    }

    public void setDim(DimDto dim) {
        this.dim = dim;
    }

    public List<PortalParamDto> getPortalParams() {
        return portalParams;
    }

    public void setPortalParams(List<PortalParamDto> portalParams) {
        this.portalParams = portalParams;
    }

    public List<CompParamDto> getCompParams() {
        return compParams;
    }

    public void setCompParams(List<CompParamDto> compParams) {
        this.compParams = compParams;
    }
}
