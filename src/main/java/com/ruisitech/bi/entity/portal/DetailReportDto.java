/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.bireport.TableInfoVO;

import java.util.List;

/**
 * @ClassName DetailReportDto
 * @Description DetailReportDto
 * @Author hq
 * @Date 2020/5/17 11:39
 */
public class DetailReportDto extends CompBaseEntity {

    private String id;
    private String type;
    private String name;
    private Integer tid;
    private String tname;
    private String tincome;  //表得来源

    private List<PortalParamDto> portalParams;  //页面的参数， 一般需要把页面参数和组件参数关联起来
    private List<CompParamDto> compParams;   //组件的参数
    private List<DashboardParamDto> dashboardParam;  //仪表盘中使用的筛选参数(非rest接口)
    private String useIn; //在仪表盘或是在报表中使用
    private String dashboardStyle; //风格
    private String styleType; //风格类型，白色背景还是黑色背景
    private Integer colSize = 2; //每列显示两个数据
    private LinkAcceptDto linkAccept;  //事件接收对象
    private Integer height;
    private String exportType; //导出类型 段落还是表格形式

    private List<DetailColDto> cols;

    /**
     *检测SQL注入
     */
    public void checkSql(TableInfoVO tinfo){
        if(cols != null){
            for(DetailColDto dto : cols){
                dto.checkSql(tinfo);
            }
        }
        if(linkAccept != null){
            linkAccept.checkSql(tinfo.getCols());
        }
        if(compParams != null){
            for(CompParamDto p : compParams){
                p.checkSql(tinfo.getCols());
            }
        }
        if(dashboardParam != null){
            for(DashboardParamDto d : dashboardParam){
                d.checkSql(tinfo.getColALias());
            }
        }
    }

    public Integer getColSize() {
        return colSize;
    }

    public void setColSize(Integer colSize) {
        this.colSize = colSize;
    }

    public List<DetailColDto> getCols() {
        return cols;
    }

    public void setCols(List<DetailColDto> cols) {
        this.cols = cols;
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

    public List<DashboardParamDto> getDashboardParam() {
        return dashboardParam;
    }

    public void setDashboardParam(List<DashboardParamDto> dashboardParam) {
        this.dashboardParam = dashboardParam;
    }

    public String getUseIn() {
        return useIn;
    }

    public void setUseIn(String useIn) {
        this.useIn = useIn;
    }

    public String getDashboardStyle() {
        return dashboardStyle;
    }

    public void setDashboardStyle(String dashboardStyle) {
        this.dashboardStyle = dashboardStyle;
    }

    public String getStyleType() {
        return styleType;
    }

    public void setStyleType(String styleType) {
        this.styleType = styleType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LinkAcceptDto getLinkAccept() {
        return linkAccept;
    }

    public void setLinkAccept(LinkAcceptDto linkAccept) {
        this.linkAccept = linkAccept;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
}
