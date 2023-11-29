/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.etl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.util.RSBIUtils;

import java.util.Date;
import java.util.List;

/**
 * BI 数据表 的主表
 * @author hq
 *
 */
public class EtlTableMeta extends BaseEntity {

	private Integer tableId;
	private String tableName;
	private String tableNote;
	private Integer crtUser;
	private String crtUserName;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date crtDate;
	/**
	 * 来源，elt(数据导入)/dw (数据填报)
	 */
	private String income;
	private String tableDesc;
	private String tableSql;
	private String useEs;
	private String esEnable;
	private String dataControlCol; //数据权限字段
	/**
	 * 如果表是SQL片段，saveType为sql，或者为空
	 */
	private String saveType;
	/**
	 *
	 */
	private List<DSColumn> cols;

	/**
	 * 表etl_src_meta对应的子表，etl_src_col_meta 集合
	 */
	private List<EtlTableMetaCol> metaCols;

	private String audit; //数据填报的审核参数

	/**
	 * 注册rest相关的接口
	 */
	private String restUrl;
	private String restPostType;
	private String restDataKey;
	private String restDataProc; //rest接口返回数据后，通过此定义转换数据
	private String restUrlBak; //rest接口得备用地址
	private String restAuthType; //rest接口的鉴权方式
	private String restTotalKey; //在分页时获取总记录数的字段

	//private String targetDatasource; //表所使用的数据源 (owner/other 两种)
	private Integer dsourceId; //数据源ID
	private String dsourceName; //数据源名称
	private String srcTables;  //源表, 可以是多个，用逗号分隔

	@JsonIgnore
	private List<String> srcTable;  //源表, 解析逗号后的结果

	@JsonIgnore
	private String restParams;

	private String tableLevel; //表层级 ods/dw/dm/code 4层
	private Integer incomeDsource;  //表来源的数据源,只用来显示(朔源)
	private String incomeDsourceName; //来源的数据源的名称

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date exeLastDate; //最后执行时间

	private String esUrl; //注册ES使用的URL
	private String esUname;	//注册ES使用的用户名
	private String esPsd;	//注册ES使用的密码
	private Integer esVersion;  //注册ES使用的版本号

	private List<EtlRestTableMetaParam> restParam;
	public Integer getTableId() {
		return tableId;
	}
	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableNote() {
		return tableNote;
	}
	public void setTableNote(String tableNote) {
		this.tableNote = tableNote;
	}
	public Integer getCrtUser() {
		return crtUser;
	}
	public void setCrtUser(Integer crtUser) {
		this.crtUser = crtUser;
	}
	public Date getCrtDate() {
		return crtDate;
	}
	public void setCrtDate(Date crtDate) {
		this.crtDate = crtDate;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getTableDesc() {
		return tableDesc;
	}
	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}
	public String getTableSql() {
		return tableSql;
	}
	public void setTableSql(String tableSql) {
		this.tableSql = tableSql;
	}


	public String getUseEs() {
		return useEs;
	}
	public void setUseEs(String useEs) {
		this.useEs = useEs;
	}
	public String getEsEnable() {
		return esEnable;
	}
	public void setEsEnable(String esEnable) {
		this.esEnable = esEnable;
	}
	public String getCrtUserName() {
		return crtUserName;
	}
	public void setCrtUserName(String crtUserName) {
		this.crtUserName = crtUserName;
	}
	public List<DSColumn> getCols() {
		return cols;
	}
	public void setCols(List<DSColumn> cols) {
		this.cols = cols;
	}
	public List<EtlTableMetaCol> getMetaCols() {
		return metaCols;
	}
	public void setMetaCols(List<EtlTableMetaCol> metaCols) {
		this.metaCols = metaCols;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public String getDataControlCol() {
		return dataControlCol;
	}
	public void setDataControlCol(String dataControlCol) {
		this.dataControlCol = dataControlCol;
	}
	@Override
	public void validate() {
		this.tableName = RSBIUtils.htmlEscape(this.tableName);
		this.tableNote = RSBIUtils.htmlEscape(this.tableNote);
		for(int i=0; this.metaCols != null && i<this.metaCols.size(); i++){
			this.metaCols.get(i).validate();
		}
	}
	public String getAudit() {
		return audit;
	}
	public void setAudit(String audit) {
		this.audit = audit;
	}
	public String getRestUrl() {
		return restUrl;
	}
	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}
	public String getRestPostType() {
		return restPostType;
	}
	public void setRestPostType(String restPostType) {
		this.restPostType = restPostType;
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

	public Integer getDsourceId() {
		return dsourceId;
	}

	public void setDsourceId(Integer dsourceId) {
		this.dsourceId = dsourceId;
	}

	public String getDsourceName() {
		return dsourceName;
	}

	public void setDsourceName(String dsourceName) {
		this.dsourceName = dsourceName;
	}

	public String getTableLevel() {
		return tableLevel;
	}

	public void setTableLevel(String tableLevel) {
		this.tableLevel = tableLevel;
	}

	public Integer getIncomeDsource() {
		return incomeDsource;
	}

	public void setIncomeDsource(Integer incomeDsource) {
		this.incomeDsource = incomeDsource;
	}

	public String getSrcTables() {
		return srcTables;
	}

	public void setSrcTables(String srcTables) {
		this.srcTables = srcTables;
	}

	public Date getExeLastDate() {
		return exeLastDate;
	}

	public void setExeLastDate(Date exeLastDate) {
		this.exeLastDate = exeLastDate;
	}

	public String getIncomeDsourceName() {
		return incomeDsourceName;
	}

	public void setIncomeDsourceName(String incomeDsourceName) {
		this.incomeDsourceName = incomeDsourceName;
	}

	public List<String> getSrcTable() {
		return srcTable;
	}

	public void setSrcTable(List<String> srcTable) {
		this.srcTable = srcTable;
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
