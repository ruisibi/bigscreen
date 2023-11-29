/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.rsbi.ext.engine.util.DaoUtils;
import com.rsbi.ext.engine.view.builder.dsource.DataSourceBuilder;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.rsbi.ext.engine.view.context.dsource.DataSourceContext;
import com.rsbi.ext.engine.view.context.grid.PageInfo;
import com.rsbi.ext.engine.view.exception.ExtConfigException;
import com.rsbi.ext.engine.wrapper.ExtRequest;
import com.rsbi.ext.engine.wrapper.TestRequestImpl;
import com.ruisitech.bi.entity.common.RequestStatus;
import com.ruisitech.bi.entity.common.Result;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.mapper.etl.DataSourceMapper;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Service
public class DataSourceService extends EtlBaseService {


	public static final String showTables_mysql = "select table_name from information_schema.tables where table_schema='$0'";
	public static final String showTables_oracle = "select table_name from all_tables t where t.owner='$0' union all select view_name from all_views t where t.OWNER = '$1' ";
	public static final String showTables_sqlser = "select name from $0.sys.sysobjects where xtype in ('U', 'V') order by name";
	public static final String showTables_db2 = "select name from sysibm.systables where type='T' and creator='$0'";
	public static final String showTables_hive = "show tables";
	public static final String showTables_psql = "select tablename from pg_tables where schemaname='$0'";
	public static final String showTables_sqlite = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
	public static final String showTables_dm = "select table_name from dba_tables where owner='$0'";
	public static final String showTables_clickhouse = "SELECT name FROM system.tables WHERE database = '$0'";

	private Logger log = LogManager.getLogger(DataSourceService.class);

	@Autowired
	private DaoHelper daoHelper;

	@Resource
	private DataSourceMapper mapper;

	@Resource(name = "dataWareHouseDataSource")
	private javax.sql.DataSource dataWareHouseDataSource;

	@Resource(name = "masterDataSource")
	private javax.sql.DataSource masterDataSource;

	@Value("${spring.datasource.dwType}")
	private String dwType;

	public List<DataSource> listDataSource(){
		DataSource ds = new DataSource();
		return mapper.listDataSource(ds);
	}

	public void insertDataSource(DataSource ds){
		//判断ip + 端口 + 数据库名称 + 用户名 是否存在
		if("true".equals(RSBIUtils.getConstant("userPartition"))){
			ds.setCrtuser(RSBIUtils.getLoginUserInfo().getUserId());
		}
		int cnt = mapper.linkExist(ds);
		if(cnt > 0){
			throw new RuntimeException("连接已经存在。");
		}
		ds.setCrtuser(RSBIUtils.getLoginUserInfo().getUserId());
		ds.setId(this.maxDataSourceid());
		mapper.insertDataSource(ds);
	}

	public void updateDataSource(DataSource ds){
		mapper.updateDataSource(ds);
	}

	public DataSource selectDataSourceByPrimaryKey(Integer id){
		return mapper.selectDataSourceByPrimaryKey(id);
	}

	public void deleteDataSource(DataSource ds){
		mapper.delDataSource(ds);
	}

	public Integer maxDataSourceid(){
		return mapper.maxDataSourceid();
	}

	public Connection getConnection(DataSource ds) throws Exception{
		try {
			Connection conn = null;
			Class.forName(ds.getClazz()).newInstance();
			conn= DriverManager.getConnection(ds.getLinkUrl(), ds.getUname(), ds.getPsd());
			return conn;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	public Connection getConnection(ServletContext ctx) throws SQLException {
		return dataWareHouseDataSource.getConnection();
	}
	 **/

	public Connection getDwConnection() throws SQLException {
		return dataWareHouseDataSource.getConnection();
	}

	public Result testDataSource(DataSource ds) throws ExtConfigException{
		Result ret = new Result();
		String clazz = ExtContext.getInstance().getDatabaseHelper(ds.getLinkType()).getClazz();
		Connection conn = null;
		try {
			Class.forName(clazz).newInstance();
			conn= DriverManager.getConnection(ds.getLinkUrl(), ds.getUname(),  ds.getPsd());
			if(conn != null){
				ret.setResult(RequestStatus.SUCCESS.getStatus());
			}else{
				ret.setResult(RequestStatus.FAIL_FIELD.getStatus());
			}
		} catch (Exception e) {
			log.error("JDBC测试出错。", e);
			ret.setResult(RequestStatus.FAIL_FIELD.getStatus());
			ret.setMsg(e.getMessage());
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	/**
	 * 通过datasource查询sql
	 * @param sql
	 * @param page
	 * @param ds
	 * @return
	 */
	public List<Map<String, Object>> querySqlUseDataSource(String sql, PageInfo page, DataSource ds) throws ExtConfigException {
		if(ds == null){
			if(page == null) {
				return daoHelper.queryForList(sql);
			}else{
				List<Map<String, Object>> ls = DaoUtils.calPage(sql, page, daoHelper);
				//dto.setTotal((int)page.getAllsize());
				return ls;
			}
		}
		DataSourceContext dsource = new DataSourceContext();
		dsource.putProperty("id", "ds-" + ds.getId());
		dsource.putProperty("usetype", "jdbc");
		String linktype = ds.getLinkType();
		dsource.putProperty("linktype", linktype);
		dsource.putProperty("linkname", ds.getUname());
		dsource.putProperty("linkpwd", ds.getPsd());
		dsource.putProperty("linkurl", ds.getLinkUrl());
		if(page == null){
			return new DataSourceBuilder().queryForList(sql, dsource);
		}else{
			ExtRequest req =  new TestRequestImpl(null, null);
			return new DataSourceBuilder().queryForList(sql, dsource, page, req);
		}
	}

	/**
	 * 删除表
	 */
	@Transactional(rollbackFor = Exception.class)
	public void droptable(String tname, DataSource ds){
		String dbName = ds == null ? dwType : ds.getLinkType();
		if("sqlser".equals(dbName)){
			String drop = "if OBJECT_ID('"+tname+"', 'U') is not null drop table " + tname;
			if(ds == null) {
				daoHelper.execute(drop);
			}else{ //通过数据源删除
				this.execteUseDatasource(null, drop, ds);
			}
		}else if("mysql".equals(dbName)){
			String drop = "drop table if exists " + tname;
			if(ds == null){
				daoHelper.execute(drop);
			}else{
				this.execteUseDatasource(null, drop, ds);
			}
		}else if("oracle".equals(dbName)){
			if(ds == null) {
				//获取当前owner
				String schema = (String) daoHelper.execute(new ConnectionCallback<Object>() {

					@Override
					public Object doInConnection(Connection arg0)
							throws SQLException, DataAccessException {
						return getCurrentSchema(arg0, dbName);
					}

				});
				String sql = "select count(1) cnt from all_tables where owner='" + schema + "'  and TABLE_NAME = upper('" + tname + "')";
				int cnt = daoHelper.queryForInt(sql);
				if (cnt >= 1) {
					daoHelper.execute("drop table " + tname);
				}
			}else{
				String sql = "select count(1) cnt from all_tables where owner='" + ds.getUname() + "'  and TABLE_NAME = upper('" + tname + "')";
				this.execteUseDatasource(sql, "drop table " + tname, ds);
			}
		}else if("db2".equals(dbName)){
			if(ds == null) {
				String sql = "select count(1) cnt from syscat.tables where type='T' and tabname = upper('" + tname + "')";
				int cnt = daoHelper.queryForInt(sql);
				if (cnt >= 1) {
					daoHelper.execute("drop table " + tname);
				}
			}else{
				String sql = "select count(1) cnt from syscat.tables where type='T' and tabname = upper('" + tname + "')";
				this.execteUseDatasource(sql, "drop table " + tname, ds);
			}
		}else if("sqlite".equals(dbName)){
			String sql = "DROP TABLE " + tname;
			if(ds == null) {
				daoHelper.execute(sql);
			}else{
				this.execteUseDatasource(null, sql, ds);
			}
		}else if("dm".equals(dbName)) {
			String sql = "select count(*) from\n" +
					"all_objects where object_type = 'TABLE' and owner != 'SYS' and object_name = '"+tname+"'";
			if(ds == null) {
				int cnt = daoHelper.queryForInt(sql);
				if(cnt > 0) {
					daoHelper.execute("drop table " + tname);
				}
			}else{
				this.execteUseDatasource(sql, "drop table " + tname, ds);
			}
		}else if("clickhouse".equals(dbName)){
			try {
				String sql = "drop table " + tname;
				if (ds == null) {
					daoHelper.execute(sql);
				} else {
					this.execteUseDatasource(null, sql, ds);
				}
			} catch (Exception ex) {
				//忽略表不存在的异常
			}
		}else {
			throw new RuntimeException("类型不支持");
		}
	}

	/**
	 * 删除视图
	 */
	@Transactional(rollbackFor = Exception.class)
	public void dropview(String tname, DataSource ds){
		String dbName = ds == null ? dwType : ds.getLinkType();
		if("sqlser".equals(dbName)){
			String drop = "if OBJECT_ID('"+tname+"', 'V') is not null drop view " + tname;
			if(ds == null) {
				daoHelper.execute(drop);
			}else{ //通过数据源删除
				this.execteUseDatasource(null, drop, ds);
			}
		}else if("mysql".equals(dbName)){
			String drop = "drop view if exists " + tname;
			if(ds == null) {
				daoHelper.execute(drop);
			}else{ //通过数据源删除
				this.execteUseDatasource(null, drop, ds);
			}
		}else if("oracle".equals(dbName)){
			String sql = "select count(1) cnt from all_views where VIEW_NAME = upper('" + tname + "')";
			if(ds == null) {
				int cnt = daoHelper.queryForInt(sql);
				if (cnt >= 1) {
					daoHelper.execute("drop view " + tname);
				}
			}else{
				this.execteUseDatasource(sql, "drop view " + tname, ds);
			}
		}else if("db2".equals(dbName)){
			String sql = "select count(1) cnt from syscat.views where viewname = upper('"+tname+"')";
			if(ds == null) {
				int cnt = daoHelper.queryForInt(sql);
				if (cnt >= 1) {
					daoHelper.execute("drop view " + tname);
				}
			}else{
				this.execteUseDatasource(sql, "drop view " + tname, ds);
			}
		}else if("dm".equals(dbName)){
			String sql = "select count(*) from\n" +
					"all_objects where object_type = 'VIEW' and owner != 'SYS' and object_name = '"+tname+"'";
			if(ds == null) {
				int cnt = daoHelper.queryForInt(sql);
				if(cnt > 0) {
					daoHelper.execute("drop view " + tname);
				}
			}else{
				this.execteUseDatasource(sql, "drop view " + tname, ds);
			}
		}else if("clickhouse".equals(dbName)){
			try {
				String sql = "drop view " + tname;
				if (ds == null) {
					daoHelper.execute(sql);
				} else {
					this.execteUseDatasource(null, sql, ds);
				}
			}catch (Exception ex){
				//忽略视图不存在的异常
			}
		}
	}

	/**
	 * 通过 datasource 执行 sql
	 * @param querysql
	 * @param ds
	 */
	public void execteUseDatasource(String querysql, String deletesql, DataSource ds){
		Connection conn = null;
		try {
			int cnt = 0;
			conn = this.getConnection(ds);
			if(querysql != null) {
				PreparedStatement ps = conn.prepareStatement(querysql);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					cnt = rs.getInt(1);
				}
				rs.close();
				ps.close();
			}else{
				cnt = 1;
			}
			if(cnt > 0){
				PreparedStatement ps2 = conn.prepareStatement(deletesql);
				ps2.executeUpdate();
				ps2.close();
			}
		}catch (Exception ex){
			log.error("执行sql出错。", ex);
			throw new RuntimeException(ex.getMessage());
		}finally {
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
		}
	}
}
