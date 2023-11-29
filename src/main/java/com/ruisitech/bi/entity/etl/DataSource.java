/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.etl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rsbi.ext.engine.dao.DatabaseHelper;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.exception.ExtConfigException;
import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.io.Serializable;

public class DataSource extends BaseEntity implements Serializable {

	private String uname;
	private String psd;
	/**
	 * 链接的数据库
	 */
	private String database;
	private String ipAddress;
	private Integer ipPort;
	private String linkType;
	private String name;
	private Integer id;
	private String extParams; //额外参数，追加在url后面

	private Integer crtuser;

	/**
	 * 获取驱动类
	 * @return
	 * @throws ExtConfigException
	 */
	@JsonIgnore
	public String getClazz() throws ExtConfigException{

		String linktype = this.linkType;
		DatabaseHelper db = ExtContext.getInstance().getDatabaseHelper(linktype);
		String clazz = db.getClazz();
		return clazz;
	}

	/**
	 * 获取连接URL
	 * @return
	 */
	@JsonIgnore
	public String getLinkUrl(){
		String url = "";
		String ip = this.ipAddress;
		Integer port = this.ipPort;
		String dbname = this.database;
		String extParams = this.extParams;
		if(linkType.equals("mysql")){
			url = "jdbc:mysql://"+ip+":"+port+"/"+dbname;
		}else if(linkType.equals("oracle")){
			url = "jdbc:oracle:thin:@"+ip+":"+port+"/" + dbname;
		}else if(linkType.equals("sqlser")){
			url = "jdbc:jtds:sqlserver://"+ip+":"+port+"/" + dbname;
		}else if(linkType.equals("db2")){
			url = "jdbc:db2://"+ip+":"+port+"/" + dbname;
		}else if(linkType.equals("hive")){
			url = "jdbc:hive2://"+ip+":"+port+"/" + dbname;
		}else if(linkType.equals("psql")){
			url = "jdbc:postgresql://"+ip+":"+port+"/" + dbname;
		}else if(linkType.equals("kylin")){
			url = "jdbc:kylin://"+ip+":"+port+"/" + dbname;
		}else if(linkType.equals("sqlite")){
			url = "jdbc:sqlite:" + ip;
		}else if(linkType.equals("dm")) { //达梦
			url = "jdbc:dm://" + ip + ":" + port + "/" + dbname;
		}else if(linkType.equals("clickhouse")){ //clickhouse
			url = "jdbc:clickhouse://"+ip+":"+port+"/" + dbname;
		}else {
			throw new RuntimeException("数据源类型 "+linkType+" 未定义.");
		}
		if(extParams != null && extParams.length()> 0) {
			url += "?"+extParams;
		}
		return url;
	}



	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPsd() {
		return psd;
	}

	public void setPsd(String psd) {
		this.psd = psd;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getIpPort() {
		return ipPort;
	}

	public void setIpPort(Integer ipPort) {
		this.ipPort = ipPort;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getExtParams() {
		return extParams;
	}

	public void setExtParams(String extParams) {
		this.extParams = extParams;
	}

	@Override
	public void validate() {
		this.uname = RSBIUtils.htmlEscape(this.uname);
		this.database = RSBIUtils.htmlEscape(this.database);
		this.ipAddress = RSBIUtils.htmlEscape(this.ipAddress);
		this.linkType = RSBIUtils.htmlEscape(this.linkType);
		this.name = RSBIUtils.htmlEscape(this.name);
	}

	public Integer getCrtuser() {
		return crtuser;
	}

	public void setCrtuser(Integer crtuser) {
		this.crtuser = crtuser;
	}
}
