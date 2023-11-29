/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.portal;

import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GridQuery extends CompBaseEntity {

	private String id;
	private String type;
	private String name;
	private Integer tid;
	private String tname;
	private String tincome;  //表得来源
	private List<GridColDto> cols;
	private List<PortalParamDto> portalParams;
	private List<CompParamDto> compParams;
	private List<DashboardParamDto> dashboardParam;  //仪表盘中使用的筛选参数(非rest接口)
	private String lockhead;
	private Integer pageSize;
	private Integer curPage;  //当前页
	private Boolean isnotfy;

	private Boolean showtitle;
	private String colwidth; //单元格宽度， auto 表示自适应
	private Boolean numberCol; //序号字段

	private String useIn; //在仪表盘或是在报表中使用

	private Integer currPage;//当前第几页
	private String dashboardStyle;
	private String styleType; //风格类型，白色背景还是黑色背景

	private LinkAcceptDto linkAccept;  //事件接收对象
	private Map<String, Object> link; //事件发起
	private String mock; //模拟数据
	private Boolean transposition; //转置

	/**
	 *检测SQL注入
	 */
	public void checkSql(TableInfoVO tinfo){
		if(cols != null){
			for(GridColDto dto : cols){
				if("note".equals(dto.getNodeType())){
					if(dto.getChildren() != null) {
						dto.getChildren().forEach(c -> {
							c.checkSql(tinfo);
						});
					}
					continue;
				}
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

	/**
	 * 查询所有用到的字段
	 * @return
	 */
	public List<GridColDto> getAllCols(boolean ignoreHideNode) {
		List<GridColDto> allCols = new ArrayList<>();
		for(GridColDto col : this.getCols()){
			if("order".equals(col.getNodeType())){
				continue;
			}
			if(ignoreHideNode && (col.getHideCol() != null && col.getHideCol())){
				continue;
			}
			if("note".equals(col.getNodeType())){	//合并节点
				for(GridColDto cld : col.getChildren()){
					if("order".equals(cld.getNodeType())){	//剔除排名字段
						continue;
					}
					allCols.add(cld);
				}
			}else{
				allCols.add(col);
			}
		}
		return allCols;
	}

	/**
	 * 返回包含 childrenParent 的所有节点
	 * @return
	 */
	public List<GridColDto> getAllColsHasChildrenParent() {
		//模拟添加排名字段
		Function<GridColDto, GridColDto> crt = (dto) ->{
			GridColDto orderCol = new GridColDto();
			orderCol.setAlias(dto.getAlias()+"_order");
			orderCol.setName("排名");
			orderCol.setType("Int");
			orderCol.setNodeType("order");
			orderCol.setId(dto.getId()+"_order");
			orderCol.setLevel(dto.getLevel());
			return orderCol;
		};
		List<GridColDto> allCols = new ArrayList<>();
		for(GridColDto col : this.getCols()){
			if(col.getHideCol() != null && col.getHideCol()){
				continue;
			}
			if("note".equals(col.getNodeType())){	//合并节点
				allCols.add(col);
				List<GridColDto> addDto = new ArrayList<>();
				for(GridColDto cld : col.getChildren()){
					allCols.add(cld);
					if(cld.getPm() != null){
						GridColDto add = crt.apply(cld);
						allCols.add(add);
						addDto.add(add);
					}
				}
				col.getChildren().addAll(addDto);
			}else{
				allCols.add(col);
				if(col.getPm() != null){
					allCols.add(crt.apply(col));
				}
			}
		}
		return allCols;
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
	public List<GridColDto> getCols() {
		return cols;
	}
	public void setCols(List<GridColDto> cols) {
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
	public String getLockhead() {
		return lockhead;
	}
	public void setLockhead(String lockhead) {
		this.lockhead = lockhead;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Boolean getIsnotfy() {
		return isnotfy;
	}

	public void setIsnotfy(Boolean isnotfy) {
		this.isnotfy = isnotfy;
	}

	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public Integer getCurrPage() {
		return currPage;
	}
	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public Boolean getShowtitle() {
		return showtitle;
	}
	public void setShowtitle(Boolean showtitle) {
		this.showtitle = showtitle;
	}
	public String getUseIn() {
		return useIn;
	}
	public void setUseIn(String useIn) {
		this.useIn = useIn;
	}
	@Override
	public void validate() {
		this.name = RSBIUtils.htmlEscape(this.name);
	}
	public String getDashboardStyle() {
		return dashboardStyle;
	}
	public void setDashboardStyle(String dashboardStyle) {
		this.dashboardStyle = dashboardStyle;
	}
	public Integer getCurPage() {
		return curPage == null ? 0  : curPage;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public String getColwidth() {
		return colwidth;
	}
	public void setColwidth(String colwidth) {
		this.colwidth = colwidth;
	}
	public LinkAcceptDto getLinkAccept() {
		return linkAccept;
	}
	public void setLinkAccept(LinkAcceptDto linkAccept) {
		this.linkAccept = linkAccept;
	}

	public String getTincome() {
		return tincome;
	}

	public void setTincome(String tincome) {
		this.tincome = tincome;
	}
	public List<DashboardParamDto> getDashboardParam() {
		return dashboardParam;
	}

	public void setDashboardParam(List<DashboardParamDto> dashboardParam) {
		this.dashboardParam = dashboardParam;
	}

	public String getStyleType() {
		return styleType;
	}

	public void setStyleType(String styleType) {
		this.styleType = styleType;
	}

	public Map<String, Object> getLink() {
		return link;
	}

	public void setLink(Map<String, Object> link) {
		this.link = link;
	}

	public String getMock() {
		return mock;
	}

	public void setMock(String mock) {
		this.mock = mock;
	}

	public Boolean getTransposition() {
		return transposition;
	}

	public void setTransposition(Boolean transposition) {
		this.transposition = transposition;
	}

	public Boolean getNumberCol() {
		return numberCol;
	}

	public void setNumberCol(Boolean numberCol) {
		this.numberCol = numberCol;
	}
}
