/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.common.RequestStatus;
import com.ruisitech.bi.entity.common.Result;
import com.ruisitech.bi.entity.etl.*;
import com.ruisitech.bi.mapper.etl.EtlTableMetaColMapper;
import com.ruisitech.bi.mapper.etl.EtlTableMetaMapper;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.util.OgnlWrapper;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TableRegService {

	private Logger log = LogManager.getLogger(TableRegService.class);

	@Resource
	private EtlTableMetaMapper mapper;

	@Resource
	private EtlTableMetaColMapper colMapper;

	@Autowired
	private MetaService metaService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private TableCacheService cahceService;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	private DataSourceService dSource;

	public EtlTableMeta getRestCfg(Integer tableId) {
		EtlTableMeta table = mapper.getTable(tableId);
		table.setMetaCols(colMapper.queryTableColumnsNotExpress(tableId));
		//转换参数
		String params = table.getRestParams();
		JSONArray arrays = JSONArray.parseArray(params);
		List<EtlRestTableMetaParam> ls = JSONArray.toJavaObject(arrays, List.class);
		table.setRestParam(ls);
		return table;
	}

	public List<DSColumn> queryRestApi(EtlTableMeta meta) {
		List<DSColumn> cols = null;
		StringBuffer url = new StringBuffer(meta.getRestUrl());
		url.append("?");
		if("GET".equalsIgnoreCase(meta.getRestPostType())) {
			//拼接参数
			meta.getRestParam().forEach(p -> {
				if(p.getParamDefvalue() != null) {
					url.append(p.getParamName());
					url.append("=");
					url.append(p.getParamDefvalue());
					url.append("&");
				}
			});
			String u = url.substring(0, url.length() - 1);
			String response = restTemplate.getForObject(u, String.class);
			JSONObject datas = JSON.parseObject(response);
			cols = this.queryRestTableCols(datas, meta);
		}else if("POST".equalsIgnoreCase(meta.getRestPostType())){
			//MultiValueMap<String, String> pms = new LinkedMultiValueMap<>();
			Map<String, Object> pms = new HashMap<>();
			//拼接参数
			meta.getRestParam().forEach(p -> {
				if(p.getParamDefvalue() != null && p.getParamDefvalue().length() > 0) {
					if("Number".equals(p.getParamType())){
						pms.put(p.getParamName(), new Integer(p.getParamDefvalue()));
					}else {
						pms.put(p.getParamName(), p.getParamDefvalue());
					}
				}else {
					pms.put(p.getParamName(), "");
				}
			});
			HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            String u = meta.getRestUrl();
			HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(pms), headers);
			String s= restTemplate.postForEntity(u,formEntity,String.class).getBody();
    		JSONObject datas = JSONObject.parseObject(s, Feature.OrderedField);
    		cols = this.queryRestTableCols(datas, meta);
    		if(cols.isEmpty()) {
    			throw new RuntimeException("未查询出数据。");
    		}
		}
		return cols;
	}

	@Transactional(rollbackFor = Exception.class)
	public void reg(EsRegDto dto, Integer dsid) {
		String dbName = RSBIUtils.getConstant("dbName");
		if(dsid != null && dsid != -1){
			dbName = dSource.selectDataSourceByPrimaryKey(dsid).getLinkType();
		}
		String tableName = dto.getTableName();
		//判断表是否存在，注意MYSQL表名要区分大小写
		if(!("mysql".equals(dbName) || "clickhouse".equals(dbName))){
			tableName = tableName.toUpperCase();
		}
		//如果是SQL Server ， 需要加上 dbo.
		if("sqlser".equals(dbName)){
			int pos = tableName.indexOf(".");
			if(pos >= 0) {
				tableName = tableName.substring(0, pos) + ".dbo" + tableName.substring(pos, tableName.length());
			}
		}
		EtlTableMeta meta = new EtlTableMeta();
		meta.setTableName(tableName);
		Integer cnt = mapper.tableExist(meta);
		Integer tableId = null;
		if(cnt > 0){
			throw new RuntimeException("您选的表已经存在.");
		}else{
			//开始注册表
			try{
				tableId = this.saveOrUpdateTable(dto, null, dsid,false, false);
				//写日志
				Map<String, Object> pms = new HashMap<>();
				pms.put("tid", tableId);
				pms.put("tname", tableName);
				pms.put("operType", 1);
				pms.put("oper", "注册表"+tableName);
				loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
			}catch(Exception ex){
				log.error("出错了", ex);
				throw new RuntimeException(ex.getMessage());
			}

		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Integer regEsTable(EsRegDto dto) throws Exception {
		EtlTableMeta meta = new EtlTableMeta();
		meta.setTableName(dto.getTableName());
		Integer cnt = mapper.tableExist(meta);
		Integer tableId = null;
		if(cnt > 0){
			throw new Exception("您选的表已经存在。");
		}else{
			try {
				//开始注册表
				tableId = this.saveOrUpdateTable(dto, null, null, true, false);
			}catch (Exception ex){
				log.error("注册ES出错", ex);
				throw ex;
			}
		}
		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("tid", tableId);
		pms.put("tname", dto.getTableName());
		pms.put("operType", 1);
		pms.put("oper", "注册ES表"+dto.getTableName());
		loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
		return tableId;
	}

	private List<DSColumn> queryRestTableCols(JSONObject datas, EtlTableMeta meta) {
		List<DSColumn> ret = new ArrayList<DSColumn>();
		OgnlWrapper wrapper = new OgnlWrapper(datas);
		Object obj = wrapper.get(meta.getRestDataKey());

		JSONObject dt = null;
		if(obj instanceof JSONArray) {
			JSONArray dts = (JSONArray)obj;
			if(!dts.isEmpty()) {
				dt = dts.getJSONObject(0);
			}
		}else if(obj instanceof JSONObject){
			dt = (JSONObject)obj;
		}
		if(dt == null || dt.isEmpty()) {
			throw new RuntimeException("未查询出数据，接口返回数据为：" + datas);
		}
		Map<String, Object> m  = dt;
		//处理带箭头数据
		if("procArrow".equals(meta.getRestDataProc())){
				Map<String, Object> val = new HashMap<>();
				val.put("sj", "now");
				Map<String, Object> m3  = m;
				dt.keySet().forEach(c -> {
					if(c.startsWith("current")){
						val.put(c.replaceAll("current", ""), m3.get(c));
					}
				});
				m = val;
		}
		Map<String, Object> m2  = m;
		m.keySet().forEach((s) -> {
			DSColumn col = new DSColumn();
			col.setName(s);
			col.setIdx(ret.size());
			Object val = m2.get(s);
			if(val instanceof String) {
				col.setType("String");
			}else if(val instanceof Integer){
				col.setType("Int");
			}else if(val instanceof Double) {
				col.setType("Double");
			}else if(val instanceof BigDecimal ) {
				col.setType("Double");
			}
			ret.add(col);
		});

		return ret;
	}

	@Transactional(rollbackFor = Exception.class)
	public void regRestTable(EtlTableMeta meta) {
		//开始注册表
		try{
			meta.setIncome("rest");
			meta.setTableName("Rest接口");
			meta.setCrtUser(RSBIUtils.getLoginUserInfo().getUserId());
			Integer tableId = meta.getTableId();
			if(tableId == null && meta.getIdType() == 2){
				meta.setTableId(this.mapper.maxTableId());
			}
			meta.setRestParams(JSONArray.toJSONString(meta.getRestParam()));
			if(tableId == null){
				mapper.insertMetaTable(meta);
			}else{
				mapper.updateMetaTable(meta);
			}
			if(meta.getTableId() == null){
				meta.setTableId(this.mapper.maxTableId() - 1);
			}
			//如果是修改，删除子表数据
			/**
			if(tableId != null){
				colMapper.delTableColByTableId(meta);
			}
			**/
			//插入子表数据。
			Integer maxId = null;
			if(meta.getIdType() == 2){
				maxId = colMapper.maxColId();
			}
			List<DSColumn> cols = queryRestApi(meta);
			for(int i=0; cols!=null&&i<cols.size(); i++){
				DSColumn col = cols.get(i);
				EtlTableMetaCol mcol = new EtlTableMetaCol();
				mcol.setColName(col.getName());
				mcol.setColType(col.getType());
				mcol.setColLength(col.getLength());
				mcol.setColNote(col.getDispName());
				mcol.setColOrd(col.getIdx());
				mcol.setTableId(meta.getTableId());
				mcol.setColDesc(col.getDispName());
				if(meta.getIdType() == 2){
					mcol.setColId(maxId);
					maxId++;
				}
				this.colMapper.insertMetaTableCol(mcol);
			}
			//写日志
			Map<String, Object> pms = new HashMap<>();
			pms.put("tid", meta.getTableId());
			pms.put("tname", meta.getTableName());
			pms.put("operType", 1);
			pms.put("oper", "注册Rest接口"+meta.getRestUrl());
			loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
		}catch(HttpClientErrorException ex) {
			log.error(ex.getResponseBodyAsString(), ex);
			throw ex;
		}catch(HttpServerErrorException ex){
			log.error(ex.getResponseBodyAsString(), ex);
			throw ex;
		}catch(Exception ex){
			log.error("保存出错。", ex);
			throw ex;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateRestTable(EtlTableMeta meta) {
		try{
			meta.setRestParams(JSONArray.toJSONString(meta.getRestParam()));
			mapper.updateMetaTable(meta);
			//删除子表数据
			colMapper.delTableColByTableId(meta);
			//插入子表数据。
			Integer maxId = null;
			if(meta.getIdType() == 2){
				maxId = colMapper.maxColId();
			}
			List<DSColumn> cols = queryRestApi(meta);
			for(int i=0; cols!=null&&i<cols.size(); i++){
				DSColumn col = cols.get(i);
				EtlTableMetaCol mcol = new EtlTableMetaCol();
				mcol.setColName(col.getName());
				mcol.setColType(col.getType());
				mcol.setColLength(col.getLength());
				mcol.setColNote(col.getDispName());
				mcol.setColOrd(col.getIdx());
				mcol.setTableId(meta.getTableId());
				mcol.setColDesc(col.getDispName());
				if(meta.getIdType() == 2){
					mcol.setColId(maxId);
					maxId++;
				}
				this.colMapper.insertMetaTableCol(mcol);
			}
			//清除缓存
			cahceService.removeTableInfo(meta.getTableId());
		}catch(HttpClientErrorException ex) {
			log.error("保存出错。" + ex.getResponseBodyAsString(), ex);
			throw ex;
		}catch(HttpServerErrorException ex) {
			log.error("保存出错。" + ex.getResponseBodyAsString(), ex);
			throw ex;
		}catch(Exception ex){
			log.error("保存出错。", ex);
			throw ex;
		}
	}

	/**
	 * tid == null 表示新增，或者表示修改
	 * @param dto
	 * @param tableId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public Integer saveOrUpdateTable(EsRegDto dto, Integer tableId, Integer dsid, boolean es, boolean isupdate) throws Exception {
		String tableName = dto.getTableName();
		EtlTableMeta meta = null;
		if(!isupdate){  //刷新不修改表信息
			meta = new EtlTableMeta();
			meta.setTableNote(tableName);
			meta.setTableName(tableName);
			meta.setTableDesc("");
		}else{
			meta = mapper.getTable(tableId);
		}
		meta.setTableId(tableId);
		meta.setCrtUser(RSBIUtils.getLoginUserInfo().getUserId());
		meta.setIncome(es?"es":"custom");
		meta.setDsourceId(dsid == null||dsid==-1?null:dsid);
		if(es) {
			meta.setEsEnable("y");
			meta.setUseEs("y");
			meta.setEsUrl(dto.getUrl());
			meta.setEsUname(dto.getUname());
			meta.setEsPsd(dto.getPsd());
			meta.setEsVersion(dto.getVersion());
		}

		if(tableId == null && meta.getIdType() == 2){
			meta.setTableId(this.mapper.maxTableId());
		}
		if(tableId == null){
			mapper.insertMetaTable(meta);
		}else{
			mapper.updateMetaTable(meta);
		}
		if(meta.getTableId() == null){
			meta.setTableId(this.mapper.maxTableId() - 1);
		}
		//如果是修改，删除子表数据
		if(tableId != null){
			colMapper.delTableColByTableIdNotExpress(meta);
		}
		//插入子表数据。
		Integer maxId = null;
		if(meta.getIdType() == 2){
			maxId = colMapper.maxColId();
		}
		List<DSColumn> cols = null;
		 //注册非ES表
		DataSource ds = null;
		if(dsid != null && dsid != -1){
			ds = dSource.selectDataSourceByPrimaryKey(dsid);
		}
		dSource.selectDataSourceByPrimaryKey(dsid);
		cols = metaService.queryTableCols("select * from " + tableName, ds, false);

		for(int i=0; cols!=null&&i<cols.size(); i++){
			DSColumn col = cols.get(i);
			EtlTableMetaCol mcol = new EtlTableMetaCol();
			mcol.setColName(col.getName());
			mcol.setColType(col.getType());
			mcol.setColLength(col.getLength());
			mcol.setColNote(col.getDispName());
			mcol.setColOrd(col.getIdx());
			mcol.setTableId(meta.getTableId());
			mcol.setColDesc(col.getDispName());
			mcol.setEsKeyword(col.getEsKeyword());
			if(meta.getIdType() == 2){
				mcol.setColId(maxId);
				maxId++;
			}
			this.colMapper.insertMetaTableCol(mcol);
		}
		cahceService.removeTableInfo(meta.getTableId());
		return meta.getTableId();
	}

}
