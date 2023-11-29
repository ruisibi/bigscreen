/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.context.grid.PageInfo;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.service.bireport.TableCacheService;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataSetService extends EtlBaseService {

	@Autowired
	private DataSourceService dataSourceService;
	@Autowired
	private EtlTableMetaColService metaColService;

	@Autowired
	private TableCacheService cacheService;


	private static Logger log = LogManager.getLogger(DataSetService.class);
	/**
	 * 根据SQL查询数据，
	 * @param sql
	 * @param rsds == null. 表示直接通过 系统已有链接查询
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public List<Object> queryTopN(String sql, DataSource rsds, int n) throws Exception{
		Connection conn  = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List<Object> ret = new ArrayList<Object>();
			if(rsds == null){
				conn = dataSourceService.getDwConnection();
			}else{
				conn = dataSourceService.getConnection(rsds);
			}
			ps = conn.prepareStatement(sql);
			ps.setMaxRows(n);
			rs = ps.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			List<String> cols = new ArrayList<String>();
			for(int i=0; i<meta.getColumnCount(); i++){
				String name = meta.getColumnLabel(i+1).toLowerCase();
				cols.add(name);
			}
			ret.add(cols);
			int idx = 0;
			while(rs.next() && idx <= n){
				Map<String, Object> m = new CaseInsensitiveMap<String, Object>();
				for(String s : cols){
					m.put(s, rs.getString(s));
				}
				ret.add(m);
				idx++;
			}
			return ret;
		} catch (Exception e) {
			log.error("sql:"+sql, e);
			throw e;
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

	/**
	 * 获取 dataset 的元数据，用在数据转换和 明细查询中
	 * @param dataset
	 * @param rsds
	 * @return
	 * @throws Exception
	 */
	public List<DSColumn> queryMetaAndIncome(JSONObject dataset, DataSource rsds) throws Exception {
		List<String> tables = new ArrayList<String>();
		//需要进行关联的表
		JSONArray joinTabs = (JSONArray)dataset.get("joininfo");
		//生成sql
		StringBuffer sb = new StringBuffer("select a0.* ");
		//添加 列的分隔符，方便识别列是从哪个表来
		if(joinTabs!=null&&joinTabs.size() != 0){ //无关联表，不需要该字段
			sb.append(",'' a$idx ");
		}

		List<String> tabIds = new ArrayList<String>(); //需要进行关联的表ID，从joininfo中获取，剔除重复的表
		for(int i=0; joinTabs!=null&&i<joinTabs.size(); i++){
			JSONObject join = joinTabs.getJSONObject(i);
			String tid = (String)join.get("tid");
			if(!tabIds.contains(tid)){
				tabIds.add(tid);
			}
		}

		for(int i=0; i<tabIds.size(); i++){
			sb.append(", a"+(i+1)+".* ");
			if(i != tabIds.size() - 1){
				//添加 列的分隔符，方便识别列是从哪个表来
				sb.append(",'' a$idx");
			}
		}
		sb.append("from ");
		String masterId = (String)dataset.get("tid"); //主表是否包含SQL
		TableInfoVO tinfo = cacheService.getTableInfo(new Integer(masterId));
		if(tinfo.getSql() != null && tinfo.getSql().length() > 0){
			sb.append("("+tinfo.getSql()+") a0 ");
		}else{
			sb.append( tinfo.getTname() + " a0 ");
		}
		tables.add(tinfo.getTname());
		for(int i=0; i<tabIds.size(); i++){
			String tabId = tabIds.get(i);
			TableInfoVO subTinfo = cacheService.getTableInfo(new Integer(tabId));
			if(subTinfo.getSql() != null && subTinfo.getSql().length() > 0){
				sb.append(", ("+subTinfo.getSql()+")");
			}else{
				sb.append(", " +subTinfo.getTname());
			}
			sb.append(" a"+(i+1)+" ");
			tables.add(subTinfo.getTname());
		}
		sb.append("where 1=2 ");
		for(int i=0; i<tabIds.size(); i++){
			String tabId = tabIds.get(i);
			TableInfoVO subTinfo = cacheService.getTableInfo(new Integer(tabId));
			List<JSONObject> refs = getJoinInfoByTname(subTinfo.getTname(), joinTabs);
			for(int k=0; k<refs.size(); k++){
				JSONObject t = refs.get(k);
				sb.append("and a0."+t.getString("col")+"=a"+(i+1)+"."+t.getString("refKey"));
				sb.append(" ");
			}
		}

		Connection conn  = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if(rsds == null){
				conn = dataSourceService.getDwConnection();
			}else{
				conn = dataSourceService.getConnection(rsds);
			}
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			ResultSetMetaData meta = rs.getMetaData();
			List<DSColumn> cols = new ArrayList<DSColumn>();
			String tname = tables.get(0);
			int idx = 1;
			for(int i=0; i<meta.getColumnCount(); i++){
				String name = meta.getColumnLabel(i+1);
				String tp = meta.getColumnTypeName(i+1);
				//遇到a$idx 表示字段做分割, 需要变换字段所属表信息
				if("a$idx".equalsIgnoreCase(name)){
					tname = tables.get(idx);
					idx++;
					continue;
				}
				tp = columnType2java(tp);
				DSColumn col = new DSColumn();
				col.setDispName(name);
				col.setName(name);
				col.setType(tp);
				col.setTname(tname);
				col.setIsshow(true);
				col.setIdx(i);
				col.setExpression("");
				cols.add(col);
			}
			return cols;
		} catch (SQLException e) {
			log.error("sql:"+sb.toString(), e);
			throw new RuntimeException(e.getMessage());
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

	private List<JSONObject> getJoinInfoByTname(String tname, JSONArray joins){
		List<JSONObject> ret = new ArrayList<JSONObject>();
		for(int i=0; joins!=null&&i<joins.size(); i++){
			JSONObject join = joins.getJSONObject(i);
			String ref = join.getString("ref");
			if(ref.equals(tname)){
				ret.add(join);
			}
		}
		return ret;
	}
}
