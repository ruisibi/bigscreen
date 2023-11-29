/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bireport;

import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.entity.etl.EtlRestTableMetaParam;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 封装查询时表的基本信息
 * @author hq
 * @date 2017-8-12
 */
public class TableInfoVO extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 2003614094616152407L;
	private Integer tid;
	private String tname;
	private String dataControlCol;  //表映射的数据权限字段
	private String sql;  //表由SQL语句构成
	private String esEnable;  //es已经同步
	private String useEs; //把数据同步到es
	private Date createTime; //缓存创建时间
	private String income; //表的来源
	private String tableType; //表类型，横表还是纵表
	private String kpiCodeColumn;  //指标编码字段
	private String kpiNameColumn;  //指标名称字段
	private String kpiValueColumn;  //指标值字段

	private String restPostType; //rest接口请求方式
	private String restUrl; //restUrl
	private String restUrlBak; //备用地址
	private String restDataKey;
	private String restParams;
	private String restDataProc; //数据处理方式
	private String restAuthType; //rest接口的鉴权方式
	private String restTotalKey; //在分页时获取总记录数的字段

	private List<EtlTableMetaCol> cols; //缓存的表列信息
	private List<EtlRestTableMetaParam> restParam; //rest参数
	private Map<String, String> colALias;  //立方体字段和别名的映射关系
	private Map<String, String> aliasAggre; //立方体字段和聚合方式的关系

	private Integer dsourceId; //数据源ID
	private DataSource dsource; //表使用的第三方数据源信息

	private String esUrl; //注册ES使用的URL
	private String esUname;	//注册ES使用的用户名
	private String esPsd;	//注册ES使用的密码
	private Integer esVersion;  //注册ES使用的版本号

	/**
	 * 根据指标别名获取指标的聚合方式
	 * @param alias
	 * @return
	 */
	public String getKpiAggre(String alias){
		if(alias.contains("_")){  //指标中有计算
			String[] names = alias.split("_");
			if(names[1].equals("tb") || names[1].equals("hb")){
				return "avg";
			}else{
				return getAliasAggre().get(names[0]);
			}
		}else{
			return getAliasAggre().get(alias);
		}
	}

	/**
	 * 根据字段别名查询字段信息
	 * @param alias
	 * @return
	 */
	public EtlTableMetaCol findColByAlias(String alias){
		EtlTableMetaCol ret = null;
		for(EtlTableMetaCol col : cols){
			if(col.getColName().equals(alias)){
				ret = col;
				break;
			}
		}
		return ret;
	}

	/**
	 * 根据 fromCol 判断 是否是 注册 ES 的表的 keyword 类型字段
	 * @param fromCol
	 * @return
	 */
	public boolean isEsKeyword(String fromCol){
		if(fromCol == null){
			return false;
		}
		EtlTableMetaCol ret = null;
		for(EtlTableMetaCol col : cols){
			if(col.getColName().equals(fromCol)){
				ret = col;
				break;
			}
		}
		if(ret == null){
			return false;
		}
		return ret.getEsKeyword() != null && ret.getEsKeyword() == 1;
	}

	/**
	 * 根据指标的 fromCol 查找指标的 别名
	 * @param fromCol
	 * @return
	 */
	public String getAliasByFromCol(String fromCol){
		String ret = null;
		for(Map.Entry<String, String> ent : aliasAggre.entrySet()){
			if(ent.getValue() != null && ent.getValue().equals(fromCol)){
				ret = ent.getKey().replaceAll("@fromCol", "");
			}
		}
		return ret;
	}

	public Integer getTid() {
		return tid;
	}
	public String getTname() {
		return tname;
	}
	public String getSql() {
		return sql;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<EtlTableMetaCol> getCols() {
		return cols;
	}
	public void setCols(List<EtlTableMetaCol> cols) {
		this.cols = cols;
	}

	public String getDataControlCol() {
		return dataControlCol;
	}

	public void setDataControlCol(String dataControlCol) {
		this.dataControlCol = dataControlCol;
	}

	public String getEsEnable() {
		return esEnable;
	}

	public void setEsEnable(String esEnable) {
		this.esEnable = esEnable;
	}

	public String getUseEs() {
		return useEs;
	}

	public void setUseEs(String useEs) {
		this.useEs = useEs;
	}

	@Override
	public void validate() {

	 }

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 数据同步到es
	 * @return
	 */
	public boolean isSyncEs() {
		return "y".equals(this.getEsEnable()) && "y".equals(this.getUseEs()) && !"es".equals(income);
	}

	/**
	 * 直接注册到es的表
	 * @return
	 */
	public boolean isRegEsTable() {
		return "y".equals(this.getEsEnable()) && "y".equals(this.getUseEs()) && "es".equals(this.income);
	}

	/**
	 * 是否 采用 es 查询
	 * @return
	 */
	public boolean isEs() {
		return "y".equals(this.getEsEnable()) && "y".equals(this.getUseEs());
	}

	public boolean isRest() {
		return "rest".equals(this.getIncome());
	}

	/**
	 * 是否是纵表
	 * @return
	 */
	public boolean isVertical() {
		return "vertical".equals(this.getTableType());
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getKpiCodeColumn() {
		return kpiCodeColumn;
	}

	public void setKpiCodeColumn(String kpiCodeColumn) {
		this.kpiCodeColumn = kpiCodeColumn;
	}

	public String getKpiNameColumn() {
		return kpiNameColumn;
	}

	public void setKpiNameColumn(String kpiNameColumn) {
		this.kpiNameColumn = kpiNameColumn;
	}

	public String getKpiValueColumn() {
		return kpiValueColumn;
	}

	public void setKpiValueColumn(String kpiValueColumn) {
		this.kpiValueColumn = kpiValueColumn;
	}

	public String getRestPostType() {
		return restPostType;
	}

	public void setRestPostType(String restPostType) {
		this.restPostType = restPostType;
	}

	public String getRestUrl() {
		return restUrl;
	}

	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}

	public String getRestDataKey() {
		return restDataKey;
	}

	public void setRestDataKey(String restDataKey) {
		this.restDataKey = restDataKey;
	}

	public String getRestParams() {
		return restParams;
	}

	public void setRestParams(String restParams) {
		this.restParams = restParams;
	}

	public List<EtlRestTableMetaParam> getRestParam() {
		return restParam;
	}

	public void setRestParam(List<EtlRestTableMetaParam> restParam) {
		this.restParam = restParam;
	}

	public String getRestDataProc() {
		return restDataProc;
	}

	public void setRestDataProc(String restDataProc) {
		this.restDataProc = restDataProc;
	}

	public String getRestUrlBak() {
		return restUrlBak;
	}

	public void setRestUrlBak(String restUrlBak) {
		this.restUrlBak = restUrlBak;
	}

	public String getRestAuthType() {
		return restAuthType;
	}

	public void setRestAuthType(String restAuthType) {
		this.restAuthType = restAuthType;
	}

	public Map<String, String> getColALias() {
		return colALias;
	}

	public void setColALias(Map<String, String> colALias) {
		this.colALias = colALias;
	}

	public Map<String, String> getAliasAggre() {
		return aliasAggre;
	}

	public void setAliasAggre(Map<String, String> aliasAggre) {
		this.aliasAggre = aliasAggre;
	}

	public DataSource getDsource() {
		return dsource;
	}

	public void setDsource(DataSource dsource) {
		this.dsource = dsource;
	}

	public Integer getDsourceId() {
		return dsourceId;
	}

	public void setDsourceId(Integer dsourceId) {
		this.dsourceId = dsourceId;
	}

	public String getRestTotalKey() {
		return restTotalKey;
	}

	public void setRestTotalKey(String restTotalKey) {
		this.restTotalKey = restTotalKey;
	}

	public String getEsUrl() {
		return esUrl;
	}

	public void setEsUrl(String esUrl) {
		this.esUrl = esUrl;
	}

	public String getEsUname() {
		return esUname;
	}

	public void setEsUname(String esUname) {
		this.esUname = esUname;
	}

	public String getEsPsd() {
		return esPsd;
	}

	public void setEsPsd(String esPsd) {
		this.esPsd = esPsd;
	}

	public Integer getEsVersion() {
		return esVersion;
	}

	public void setEsVersion(Integer esVersion) {
		this.esVersion = esVersion;
	}
}
