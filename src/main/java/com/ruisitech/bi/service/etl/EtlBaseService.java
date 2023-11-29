/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

/**
 * Etl Serivce 的基类
 * @author hq
 *
 */
public class EtlBaseService {

	/**
	 * 填报表的两个固定字段。
	 */
	public static final String tbbId = "tmp_data_id";
	public static final String tbbUser = "tmp_user_id";
	//增加审核状态的字段, 1, 未审核， 2.审核通过，3.审核不通过
	public static final String auditState = "tmp_audit_state";

	public static final String productBatch = "rsbi_pb";  //数据仓库批次字段
	public static final String createDate = "rsbi_crtdate"; //数据仓库创建时间字段
	public static final String srcTf = "rsbi_src_tf";  //表数据来源于哪个数据抽取或转换的字段
	public static final String productBatchKey = "productBatch";
	public static final String dateLabelKey = "dateLabel";
	public static final String NOWKey = "now"; //当前时间 key
	public static final String srcTfKey = "srcTf"; //当前时间 key

	private static Logger log = LogManager.getLogger(EtlBaseService.class);

	private static String[] specialWords = new String[]{"(", ")", "."};  //字段特殊符号

	/**
	 * 通过json 创建导入表 sql
	 * @param table
	 * @return
	 */
	public String createImpTableSql(EtlTableMeta table,String dbType){
		StringBuffer sb = new StringBuffer("");
		sb.append("create table " + table.getTableName() + " (\n");
		List<DSColumn> ls = table.getCols();
		for(int i=0; i<ls.size(); i++){
			sb.append("\t");
			DSColumn col = ls.get(i);
			for(String word : specialWords){
				if(col.getName().contains(word)){
					throw new RuntimeException("建表语句错误，字段"+col.getName()+"包含特殊符号：" + word);
				}
			}
			sb.append(col.getName());
			sb.append(" ");
			Integer length = col.getLength();
			Integer scale = col.getScale();
			sb.append(javaType2db(col.getType(), dbType, length, scale));
			/**
			 *通过程序写默认值
			if(col.getDefvalue() != null && col.getDefvalue().length() > 0) {   //追加默认值
				sb.append(" default ");
				if("Int".equalsIgnoreCase(col.getType()) || "Double".equalsIgnoreCase(col.getType())) {
					sb.append(col.getDefvalue());
				}else {
					sb.append("'");
					sb.append(col.getDefvalue());
					sb.append("'");
				}
			}
			**/
			sb.append(",");
			sb.append("\n");
		}
		//创建默认字段（批次，创建时间）
		sb.append(createExtColumn(dbType));
		sb.append(")");
		//如果是mysql, 增加编码代码,默认编码是 utf8
		if("mysql".equals(dbType)){
			sb.append(" ENGINE=MyISAM CHARSET=utf8 COMMENT='"+(table.getTableDesc()==null?"":table.getTableDesc())+"'");
		}else if("clickhouse".equals(dbType)){
			sb.append(" ENGINE = MergeTree() ORDER BY " + createDate );
		}
		return sb.toString();
	}

	/**
	 * 创建默认字段（批次，创建时间）
	 * @param dbType
	 * @return
	 */
	public String createExtColumn(String dbType){
		StringBuffer sb = new StringBuffer();
		sb.append(productBatch);
		sb.append(" ");
		if("clickhouse".equals(dbType)){
			sb.append("LowCardinality(String)");
		}else{
			sb.append(javaType2db("String", dbType, 50, 0));
		}
		sb.append(", \n");
		sb.append(createDate);
		if("clickhouse".equals(dbType)){
			sb.append(" DateTime");
		}else {
			sb.append(" " + javaType2db("Datetime", dbType, 50, 0));
		}
		sb.append(", \n");
		sb.append(srcTf);
		if("clickhouse".equals(dbType)){
			sb.append(" LowCardinality(String)");
		}else {
			sb.append(" " + javaType2db("String", dbType, 50, 0));
		}
		return sb.toString();
	}
	/**
	 * java类型到 数据库类型
	 * @param tp
	 * @return
	 */
	public String columnType2java(String tp){
		tp = tp.replaceAll(" UNSIGNED", ""); //mysql 存在 UNSIGNED 类型, 比如： INT UNSIGNED
		tp = tp.replaceAll("Nullable\\((.+)\\)$", "$1");  //替换 clickhouse 中的 Nullable
		String ret = null;
		if("varchar".equalsIgnoreCase(tp) || "bpchar".equalsIgnoreCase(tp) || "varchar2".equalsIgnoreCase(tp) || "nvarchar2".equalsIgnoreCase(tp) || "char".equalsIgnoreCase(tp) || "string".equalsIgnoreCase(tp)
				|| tp.toLowerCase().indexOf("text") >= 0 || tp.toLowerCase().indexOf("string") >= 0 || tp.toLowerCase().indexOf("enum8") >= 0){
			ret = "String";
		}else if("int".equalsIgnoreCase(tp) || "int4".equalsIgnoreCase(tp) || "float4".equalsIgnoreCase(tp) ||  "INTEGER".equalsIgnoreCase(tp) || "MEDIUMINT".equalsIgnoreCase(tp) || "smallint".equalsIgnoreCase(tp) || "TINYINT".equalsIgnoreCase(tp)
				|| "BIT".equalsIgnoreCase(tp) || "UInt32".equalsIgnoreCase(tp)  || "Int32".equalsIgnoreCase(tp)  || "UInt64".equalsIgnoreCase(tp)  || "UInt8".equalsIgnoreCase(tp)){
			ret = "Int";
		}else if( "int8".equalsIgnoreCase(tp) || "BIGINT".equalsIgnoreCase(tp) || "Int64".equalsIgnoreCase(tp) || "Int128".equalsIgnoreCase(tp)){
			ret = "Long";
		}else if("number".equalsIgnoreCase(tp) || "numeric".equalsIgnoreCase(tp) || "DECIMAL".equalsIgnoreCase(tp) || "Float".equalsIgnoreCase(tp) || "Double".equalsIgnoreCase(tp) || "REAL".equalsIgnoreCase(tp) || "dec".equalsIgnoreCase(tp)
				|| "Float32".equalsIgnoreCase(tp) || "Float64".equalsIgnoreCase(tp) || tp.toLowerCase().contains("decimal")){
			ret = "Double";
		}else if("DATETIME".equalsIgnoreCase(tp) || "Timestamp".equalsIgnoreCase(tp)){
			ret = "Datetime";
		}else if("DATE".equalsIgnoreCase(tp)){
			ret = "Date";
		}
		if(ret == null){
			log.error("tp = " + tp+" 字段类型未映射成功");
		}
		return ret;
	}

	/**
	 * 转换JAVA类型到SQL类型
	 * @param type
	 * @param dbName
	 * @return
	 */
	protected String javaType2db(String type, String dbName, Integer length, Integer scale){
		if(scale == null){  //小数位默认2
			scale = 2;
		}
		if("mysql".equals(dbName)){
			if("String".equals(type)){
				if(length > 6000){
					return "text";
				}else{
					return "varchar("+length+")";
				}
			}else if("Int".equals(type)){
				return "int("+length+")";
			}else if("Long".equals(type)){
				return "bigint("+length+")";
			}else if("Double".equals(type)){
				return "DECIMAL("+length+","+(scale >= 4 ? 4 : scale)+")";  //对于 double 类型，默认保留4位小数
			}else if("Date".equals(type)){
				return "DATE";
			}else if("Datetime".equals(type)){
				return "DATETIME";
			}else{
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}else if("psql".equals(dbName)) {
			if("String".equals(type)){
				if(length > 6000){
					return "text";
				}else{
					return "varchar("+length+")";
				}
			}else if("Int".equals(type)){
				return "integer";
			}else if("Long".equals(type)){
				return "bigint";
			}else if("Double".equals(type)){
				return "NUMERIC("+length+","+(scale >= 4 ? 4 : scale)+")";  //对于 double 类型，默认保留2位小数
			}else if("Date".equals(type)){
				return "date";
			}else if("Datetime".equals(type)){
				return "timestamp";
			}else{
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}else if("sqlser".equals(dbName) || "sqlserver".equals(dbName)){
			if("String".equals(type)){
				if(length > 6000){
					return "nvarchar(MAX)";
				}else{
					return "nvarchar("+length+")";
				}
			}else if("Int".equals(type)){
				return "int";
			}else if("Long".equals(type)){
				return "int";
			}else if("Double".equals(type)){
				return "float";
			}else if("Datetime".equals(type)){
				return "datetime";
			}else if("Date".equals(type)){
				return "date";
			}else{
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}else if("oracle".equals(dbName)){
			if("String".equals(type)){
				if(length > 6000){
					return "clob";
				}else{
					return "varchar2("+length+")";
				}
			}else if("Int".equals(type)){
				return "number("+length+")";
			}else if("Long".equals(type)){
				return "number("+length+")";
			}else if("Double".equals(type)){
				return "number("+length+", "+(scale >= 4 ? 4 : scale)+")";
			}else if("Date".equals(type)){
				return "date";
			}else if("Datetime".equals(type)){
				return "date";
			}else{
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}else if("db2".equals(dbName)){
			if("String".equals(type)){
				if(length > 6000){
					return "CLOB";
				}else{
					return "VARCHAR("+length+")";
				}
			}else if("Int".equals(type)){
				return "INTEGER";
			}else if("Long".equals(type)){
				return "INTEGER";
			}else if("Double".equals(type)){
				return "DECIMAL("+length+", "+(scale >= 4 ? 4 : scale)+")";
			}else if("Date".equals(type)){
				return "DATE";
			}else if("Datetime".equals(type)){
				return "Timestamp";
			}else{
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}else if("sqlite".equals(dbName)) {
			if ("String".equals(type)) {
				return "text(" + length + ")";
			} else if ("Int".equals(type)) {
				return "INTEGER";
			} else if ("Long".equals(type)) {
				return "INTEGER";
			} else if ("Double".equals(type)) {
				return "REAL(" + length + ", " + (scale >= 4 ? 4 : scale) + ")";
			} else if ("Date".equals(type)) {
				return "datetime";
			} else if ("Datetime".equals(type)) {
				return "datetime";
			} else {
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}else if("dm".equals(dbName)) {
			if ("String".equals(type)) {
				if (length > 6000) {
					return "clob";
				} else {
					return "varchar(" + length + ")";
				}
			} else if ("Int".equals(type)) {
				return "int";
			} else if ("Long".equals(type)) {
				return "bigint";
			} else if ("Double".equals(type)) {
				return "number(" + length + ", " + (scale >= 4 ? 4 : scale) + ")";
			} else if ("Date".equals(type)) {
				return "date";
			} else if ("Datetime".equals(type)) {
				return "timestamp(0)";
			} else {
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}else if("clickhouse".equals(dbName)){
			if ("String".equals(type)) {
				return "Nullable(String)";
			} else if ("Int".equals(type)) {
				return "Nullable(UInt32)";
			} else if ("Long".equals(type)) {
				return "Nullable(UInt64)";
			} else if ("Double".equals(type)) {
				return "Nullable(Decimal(" + length + ", " + (scale >= 4 ? 4 : scale) + "))";
			} else if ("Date".equals(type)) {
				return "Nullable(Date)";
			} else if ("Datetime".equals(type)) {
				return "Nullable(DateTime)";
			} else {
				throw new RuntimeException("类型 " + type + " 未定义。");
			}
		}
		throw new RuntimeException(dbName + " 类型数据库未定义。");
	}

	/**
	 * 给表格添加字段
	 */
	public void addTableColumn(EtlTableMetaCol vo, String dwType, DaoHelper dao){
		String tp = javaType2db(vo.getColType(), dwType, vo.getColLength(), vo.getColScale());
		String sql = " ALTER TABLE "+vo.getTableName()+" ADD ";
		if(!"oracle".equals(dwType)){
			sql += "COLUMN";
		}
		sql += " "+vo.getColName()+" " + tp;
		dao.execute(sql);
	}

	/**
	 * 修改表格字段
	 * @param vo
	 * @param dao
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateColumn(EtlTableMetaCol vo, String dwType, DaoHelper dao){
		String tp = javaType2db(vo.getColType(), dwType, vo.getColLength(), vo.getColScale());
		String sql = null;
		if("db2".equals(dwType)){
			sql = " ALTER TABLE "+vo.getTableName()+" alter column "+vo.getColName()+"  set data type " + tp;
		}else if("psql".equals(dwType)) {
			sql = " ALTER TABLE " + vo.getTableName() + " ALTER COLUMN " + vo.getColName() + " type " + tp;
		}else if("oracle".equals(dwType)){
			sql = " ALTER TABLE " + vo.getTableName() + " modify " + vo.getColName() + " " + tp;
		}else{
			sql = " ALTER TABLE "+vo.getTableName()+" modify COLUMN "+vo.getColName()+" " + tp;
		}
		dao.execute(sql);
	}

	/**
	 * 移除表格字段
	 */
	@Transactional(rollbackFor = Exception.class)
	public void removeTableColumn(EtlTableMetaCol vo, DaoHelper dao){
		String sql = "ALTER TABLE "+vo.getTableName()+" DROP  column " + vo.getColName();
		dao.execute(sql);
	}

	public String getCurrentSchema(Connection conn, String dbName) throws SQLException{
		String ret = null;
		if("db2".equalsIgnoreCase(dbName)){
			String sql = "select current schema from sysibm.dual";
			try {
				Statement ps = conn.createStatement();
				ResultSet rs = ps.executeQuery(sql);
				while(rs.next()){
					ret = rs.getString(1);
					break;
				}
				rs.close();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("mysql".equalsIgnoreCase(dbName)){
			ret = conn.getCatalog();
		}else if("oracle".equals(dbName)){
			String sql = "select username from user_users";
			try {
				Statement ps = conn.createStatement();
				ResultSet rs = ps.executeQuery(sql);
				while(rs.next()){
					ret = rs.getString(1);
					break;
				}
				rs.close();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("psql".equals(dbName)){
			String sql = "SHOW search_path";
			try{
				Statement ps = conn.createStatement();
				ResultSet rs = ps.executeQuery(sql);
				while(rs.next()){
					ret = rs.getString(1);
					break;
				}
				rs.close();
				ps.close();
				if(ret != null && ret.indexOf("public") >=0 ) {
					ret = "public";
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}else if("sqlser".equals(dbName)) {
			ret = conn.getCatalog();
		}else if("dm".equalsIgnoreCase(dbName)) { //达梦
			ret = conn.getCatalog();
		}else if("clickhouse".equals(dbName)){
			ret = conn.getSchema();
		}
		return ret;
	}

	/**
	 * 按批次号统计本次数据转换后的数据量
	 * (当目标表数据量很大的时候，全表扫描查询数据量，存在性能问题)
	 * @param pb
	 * @param tname
	 * @param conn
	 * @return
	 */
	public int queryDataCntByPb(String pb, String tname, Connection conn) throws SQLException {
		String sql = "select count(1) cnt from " + tname+" where "+EtlBaseService.productBatch+" = ?" ;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, pb);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int cnt = rs.getInt(1);
		rs.close();
		ps.close();
		return cnt;
	}
}
