/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.rsbi.ext.engine.ConstantsEngine;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库元数据操作service
 * @author hq
 *
 */
@Service
public class MetaService extends EtlBaseService {

	@Autowired
	private DataSourceService dSource;

	@Autowired
	private DataSetService dsetService;

	private static Logger log = LogManager.getLogger(MetaService.class);

	@Value("${spring.datasource.dbType}")
	private String dbName;

	@Value("${spring.datasource.dwType}")
	private String dwType;


	/**
	 * 获取所有表
	 * @param schema, 如果 == null 获取当前 schema
	 * @param dsource 如果 == null,获取当前 connection
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryTables(String schema, DataSource dsource) throws Exception {
		List<Map<String, Object>> ret = null;
		Connection conn = null;
		try {
			if(dsource == null) {
				conn = dSource.getDwConnection();
			}else {
				conn = dSource.getConnection(dsource);
			}
			if(dsource != null && "kylin".equalsIgnoreCase(dsource.getLinkType())){  //kylin 系统通过元数据查询 所有表
				ret = queryTablesByMetadata(schema, dsource != null ? dsource.getLinkType() : dbName , conn);
			}else{
				ret = queryTablesBySql(schema, dsource , conn);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql 执行报错.");
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return ret;

	}

	/**
	 * 通过sql查询所有表
	 * @param schema
	 * @param dsource
	 * @param conn
	 * @return
	 */
	public List<Map<String, Object>> queryTablesBySql(String schema , DataSource dsource, Connection conn){
		final List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		String dbName = dsource != null ? dsource.getLinkType() : this.dbName;

		try {
			String qsql = null;
			if("mysql".equals(dbName)){
				qsql = ConstantsEngine.replace(DataSourceService.showTables_mysql, schema == null ? getCurrentSchema(conn, dbName) : schema);
			}else if("oracle".equals(dbName)){
				String s = schema == null ? getCurrentSchema(conn, dbName) : schema;
				qsql = ConstantsEngine.replace(DataSourceService.showTables_oracle, s, s);
			}else if("sqlser".equals(dbName)){
				qsql = ConstantsEngine.replace(DataSourceService.showTables_sqlser, schema == null ? getCurrentSchema(conn, dbName) : schema);
			}else if("db2".equals(dbName)){
				//需要获取当前 schema
				qsql = ConstantsEngine.replace(DataSourceService.showTables_db2, schema == null ? getCurrentSchema(conn, dbName) : schema);
			}else if("hive".equals(dbName)){
				qsql = DataSourceService.showTables_hive;
			}else if("psql".equals(dbName)){
				qsql = ConstantsEngine.replace(DataSourceService.showTables_psql, schema == null ? getCurrentSchema(conn, dbName) : schema);
			}else if("sqlite".equals(dbName)){
				qsql = ConstantsEngine.replace(DataSourceService.showTables_sqlite, "main");
			}else if("dm".equals(dbName)){ //达梦数据库
				String s = schema == null ?  dsource.getDatabase().toUpperCase() : schema;  //直接通过链接取数据库名
				qsql = ConstantsEngine.replace(DataSourceService.showTables_dm, s);
			}else if("clickhouse".equalsIgnoreCase(dbName)){
				qsql = ConstantsEngine.replace(DataSourceService.showTables_clickhouse, schema == null ? getCurrentSchema(conn, dbName) : schema);
			}

			ps = conn.prepareStatement(qsql);
			rs = ps.executeQuery();
			while(rs.next()){
				Map<String, Object> m = new HashMap<String, Object>();
				String tname = rs.getString(1);
				m.put("id", tname);
				m.put("text", tname);
				m.put("icon", "fa fa-table");
				ret.add(m);
			}
		}catch (SQLException e) {
			log.error("出错了", e);
			throw new RuntimeException("sql 执行报错.");
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	/**
	 * 通过元数据查询所有表
	 * @param schema
	 * @param dbName
	 * @param conn
	 * @return
	 */
	public List<Map<String, Object>> queryTablesByMetadata(String schema , String dbName, Connection conn){
		final List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		ResultSet rs = null;
		try {
			ResultSet tbs = conn.getMetaData().getTables(null, schema, "%", new String[]{"TABLE"});
			while(tbs.next()){
				Map<String, Object> m = new HashMap<String, Object>();
				String tname = tbs.getString("TABLE_NAME");
				m.put("id", tname);
				m.put("text", tname);
				m.put("iconCls", "icon-table");
				ret.add(m);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql 执行报错.");
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	public String getCurrentSchema(Integer dsource) {
		String dbName = RSBIUtils.getConstant("dbName");
		Connection conn = null;
		try{
			if(dsource == null || dsource == -1) {
				conn = dSource.getDwConnection();
				return this.getCurrentSchema(conn, dbName);
			}else{
				DataSource ds = dSource.selectDataSourceByPrimaryKey(dsource);
				conn = dSource.getConnection(ds);
				return this.getCurrentSchema(conn, ds.getLinkType());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw new RuntimeException("sql 执行报错.");
		}finally{
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * 根据SQL获取 字段信息
	 * @param sql
	 * @param ds
	 * @param filterRepeat
	 * @return
	 * @throws Exception
	 */
	public List<DSColumn> queryTableCols(String sql, DataSource ds, boolean filterRepeat) throws Exception {
			//采用用户定义的数据源进行连接
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				if (ds == null) {
					conn = this.dSource.getDwConnection();
				} else {
					conn = this.dSource.getConnection(ds);
				}
				/*PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ps.setFetchSize(Integer.MIN_VALUE);
				ps.setFetchDirection(ResultSet.FETCH_REVERSE);
				*/
				ps = conn.prepareStatement(sql);
				ps.setMaxRows(1);
				rs = ps.executeQuery();
				List<DSColumn> cols = copyValue(rs, filterRepeat);
				//查询用到的表
				Map<String, String> tableRemarks = new HashMap<>();
				Map<String, List<DSColumn>> ls = cols.stream().collect(Collectors.groupingBy(e -> e.getTname()));

				//查询表备注信息
				for (String tname : ls.keySet()) {
					if (tname == null || tname.length() == 0) {
						continue;
					}
					String schema = null;
					if (ds != null) {
						schema = this.getCurrentSchema(conn, ds.getLinkType());
					} else {
						schema = this.getCurrentSchema(null);
					}
					ResultSet metas = conn.getMetaData().getColumns(null, schema, tname, "%");
					while (metas.next()) {
						String colName = metas.getString("COLUMN_NAME");
						String remarks = metas.getString("REMARKS");
						if (remarks == null || remarks.length() == 0) {
							remarks = null;
						}
						tableRemarks.put(colName, remarks);
					}
					metas.close();
				}
				//匹配数据
				cols.forEach(e -> {
					String remark = tableRemarks.get(e.getName());
					e.setDispName(remark);
				});

				return cols;
			}catch (SQLException ex){
				throw new SQLException("查询SQL出错，SQL : " + sql+", 错误信息： " + ex.getMessage(), ex);
			}finally{
				if(rs != null){
					rs.close();
				}
				if(ps != null){
					ps.close();
				}
				if(conn != null){
					conn.close();
				}
			}
	}

	public List<DSColumn> copyValue(ResultSet rs, boolean filterRepeat) throws SQLException{
		ResultSetMetaData meta = rs.getMetaData();
		List<DSColumn> cols = new ArrayList<DSColumn>();
		for(int i=0; i<meta.getColumnCount(); i++){
			String name = meta.getColumnLabel(i+1);
			String colType = meta.getColumnTypeName(i+1);
			//meta.get
			//tp转换
			String tp = columnType2java(colType);
			if(tp == null){
				log.info("数据库类型：" + colType+" 未识别到。");
			}
			DSColumn col = new DSColumn();
			col.setName(name);
			col.setType(tp);
			col.setIsshow(true);
			col.setIdx(i+1);
			String tname = meta.getTableName(i+1);
			col.setTname(tname == null ?"":tname);
			if("Date".equals(tp)){
				//日期不设置长度
			}else{
				col.setLength(meta.getPrecision(i+1));
				if("oracle".equals(dwType) && col.getLength() == 0){
					col.setLength(18);  //oracle 下未识别出来长度， 默认18
				}
				if("Double".equals(tp)){
					int scale = meta.getScale(i+1);
					col.setScale(scale == 0 ? null : scale);
					if("oracle".equals(dwType) && (scale == 0 || scale == -127)){
						col.setScale(4);  //oracle 下未识别出来精度，默认4
					}
				}

			}
			if(filterRepeat) {
				if(!colExist(name, cols)) {
					cols.add(col);
				}
			}else {
				cols.add(col);
			}
		}
		return cols;
	}

	private boolean colExist(String colName, List<DSColumn> cols) {
		boolean exist = false;
		for(DSColumn col : cols) {
			if(col.getName().equals(colName)) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	public List<String> queryDbSchemas(Integer dsource){
		String dbName = RSBIUtils.getConstant("dbName");
		Connection conn = null;
		try{
			if(dsource == null  || dsource == -1) {  //查询当前数据仓
				conn = dSource.getDwConnection();
				return queryDbSchemas(conn, dbName);
			}else{ //通过数据源查询
				DataSource ds = dSource.selectDataSourceByPrimaryKey(dsource);
				if(ds == null){
					throw new Exception("数据源不存在。");
				}
				conn = dSource.getConnection(ds);
				return queryDbSchemas(conn, ds.getLinkType());
			}
		}catch(Exception ex){
			//ex.printStackTrace();
			log.error("出错了", ex);
			throw new RuntimeException(ex.getMessage());
		}finally{
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 获取所有 schema
	 * @param conn
	 * @param dbName
	 * @return
	 */
	public List<String> queryDbSchemas(Connection conn, String dbName){
		List<String> ret = new ArrayList<String>();
		String sql = null;
		if("db2".equalsIgnoreCase(dbName)){
			sql = "select schemaname from syscat.schemata";
		}else if("mysql".equalsIgnoreCase(dbName)){
			sql = "select schema_name from information_schema.schemata";
		}else if("oracle".equals(dbName)){
			sql = "select username from all_users";
		}else if("sqlser".equalsIgnoreCase(dbName)){
			sql = "select name from sysdatabases";
		}else if("psql".equalsIgnoreCase(dbName)) {
			sql = "select schema_name from information_schema.schemata";
		}else if("dm".equalsIgnoreCase(dbName)) {
			sql = "select object_name from all_objects where object_type = 'SCH'";
		}else if("clickhouse".equalsIgnoreCase(dbName)){
			sql = "show databases";
		}else{
			throw new RuntimeException("数据源类型"+dbName+"未实现.");
		}
		try {
			Statement ps = conn.createStatement();
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next()){
				ret.add(rs.getString(1));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
