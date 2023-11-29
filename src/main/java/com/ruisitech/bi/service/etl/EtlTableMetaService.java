/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.common.DSColumn;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.entity.model.TableMeta;
import com.ruisitech.bi.mapper.etl.EtlTableMetaColMapper;
import com.ruisitech.bi.mapper.etl.EtlTableMetaMapper;
import com.ruisitech.bi.mapper.model.TableMetaMapper;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.bi.util.SortService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EtlTableMetaService extends EtlBaseService {

	@Resource
	private EtlTableMetaMapper mapper;

	@Resource
	private EtlTableMetaColMapper colMapper;

	@Autowired
	private DaoHelper daoHelper;

	@Autowired
	private TableCacheService cacheService;

	@Autowired
	private DataSourceService dsService;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	private DataSourceService dataSourceService;

	@Autowired
	private QueryRestService restService;

	@Autowired
	private DatasetService datasetService;

	@Resource
	private TableMetaMapper tableMapper;

	private static Logger log = LogManager.getLogger(EtlTableMetaService.class);

	public Integer tableExist(String tableName){
		EtlTableMeta meta = new EtlTableMeta();
		meta.setTableName(tableName.toUpperCase());
		return mapper.tableExist(meta);
	}

	/**
	 * 数据填报 选择表
	 * @return
	 */
	public List<EtlTableMeta> dwselectTables(){
		return mapper.dwselectTables();
	}

	/**
	 * 根据表类型计算表的数量
	 * @param type
	 * @return
	 */
	public Integer cntTables(String type){
		Integer crtUser = null;
		return mapper.cntTables(type, crtUser);
	}

	public EtlTableMeta getTableByTname(String tableName ){
		return mapper.getTableByTname(tableName);
	}

	/**
	 * 获取表信息，同时获取子表信息，表分区信息
	 * @param tableId
	 * @param hasExpress 是否包含表达式
	 * @return
	 */
	public EtlTableMeta getTableAll(Integer tableId, boolean hasExpress){
		EtlTableMeta table = mapper.getTable(tableId);
		if(table == null){
			throw new RuntimeException("ID为"+tableId+"的表已经被删除了。");
		}
		List<EtlTableMetaCol> metaCols = hasExpress ? colMapper.queryTableColumns(tableId, null) : colMapper.queryTableColumnsNotExpress(tableId);
		table.setMetaCols(metaCols);
		return table;
	}

	public EtlTableMeta getTableAll(Integer tableId){
		return getTableAll(tableId, true);
	}

	public EtlTableMeta getTable(Integer tableId){
		EtlTableMeta table = mapper.getTable(tableId);
		return table;
	}

	/**
	 * 只获取表信息，不需要 子表字段， 表分区信息
	 * @param tableId
	 * @return
	 */
	public EtlTableMeta getTableOnly(Integer tableId){
		EtlTableMeta table = mapper.getTable(tableId);
		return table;
	}

	/**
	 * 根据类型查询表，如果income为空，查询所有表
	 * @param income
	 * @return
	 */
	public List<EtlTableMeta> selectTables(String income, String tableLevel, String keyword, PageParam p){
		Integer crtUser = null;
		List<EtlTableMeta> ret = mapper.selectTables(income, tableLevel, crtUser,keyword, null);
		if(p.getOrder() != null && p.getSort() != null){
			SortService sort = new SortService(p.getSort(), p.getOrder());
			Collections.sort(ret, sort);
		}
		return ret;
	}

	public List<EtlTableMeta> selectTables(String income, String keyword, PageParam p) {
		return selectTables(income, null, keyword, p);
	}

	/**
	 * 查询所有不是ES来源的表和 rest接口来源的表
	 * ES/Rest的表不能进行关联，行专列，SQL脚本等二次处理
	 */
	public List<EtlTableMeta> selectTablesNotEsAndRest(Integer dsourceId, boolean filter){
		Integer crtUser = null;
		List<EtlTableMeta> ret = mapper.selectTablesNotEsAndRest(crtUser);
		//根据dsourceId进行过滤
		if(filter == true) {
			ret = ret.stream().filter(m -> {
				if (dsourceId == null || dsourceId == -1) {
					return m.getDsourceId() == null || m.getDsourceId() == -1;
				} else {
					return m.getDsourceId() != null && m.getDsourceId().equals(dsourceId);
				}
			}).collect(Collectors.toList());
		}
		return ret;
	}

	public List<EtlTableMeta> selectTablesNotRest(String keyword, String income){
		Integer crtUser = null;
		List<EtlTableMeta> ret = mapper.selectTablesNotRest(keyword, income, crtUser);
		return ret;
	}

	public List<EtlTableMeta> selectTablesNotRest(String keyword, Integer crtUser, String income){
		List<EtlTableMeta> ret = mapper.selectTablesNotRest(keyword, income, crtUser);
		return ret;
	}

	public List<EtlTableMeta> selectTablesByLevel(String tableLevel){
		return this.selectTablesIncomeAndLevel(null, tableLevel, null);
	}

	public List<EtlTableMeta> selectTables(String income){
		return selectTablesIncomeAndLevel(income, null, null);
	}
	public List<EtlTableMeta> selectTablesIncomeAndLevel(String income, String tableLevel,  String keyword){
		Integer crtUser = null;
		List<EtlTableMeta> ret = mapper.selectTables(income, tableLevel, crtUser, keyword, null);
		return ret;
	}

	public List<EtlTableMeta> selectByIncomes(List<String> incomes, PageParam p){
		Integer crtUser = null;
		List<EtlTableMeta> ret = mapper.selectByIncomes(incomes, p.getSearch(), crtUser);
		if(p.getOrder() != null && p.getSort() != null){
			SortService sort = new SortService(p.getSort(), p.getOrder());
			Collections.sort(ret, sort);
		}
		return ret;
	}

	/**
	 * 从数据库查询表字段列表， hasExpress 表示是否查询表达式字段列表
	 * @param tableId
	 * @param hasExpress
	 * @return
	 */
	public List<EtlTableMetaCol> queryTableColumns(Integer tableId, boolean hasExpress){
		if(hasExpress){
			return colMapper.queryTableColumns(tableId, null);
		}else{
			return colMapper.queryTableColumnsNotExpress(tableId);
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public EtlTableMeta saveTableInfo(EtlTableMeta table){
		Integer dsourceId = table.getDsourceId();
		String dbType = null;
		if(dsourceId == null || -1 == dsourceId){  //采用bi数据仓的数据源
			dbType = table.getDwName();
		}else{
			dbType = dsService.selectDataSourceByPrimaryKey(table.getDsourceId()).getLinkType();
		}
		//创建表
		String sql = createImpTableSql(table, dbType);
		if(dsourceId == null || -1 == dsourceId) {
			this.daoHelper.execute(sql);
		}else{
			//调用数据源创建表
			DataSource ds = dsService.selectDataSourceByPrimaryKey(table.getDsourceId());
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = dsService.getConnection(ds);
				ps = conn.prepareStatement(sql);
				ps.executeUpdate();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new RuntimeException(ex.getMessage());
			}finally {
				if(null != ps){
					try {
						ps.close();
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					}
				}
				if( conn != null){
					try {
						conn.close();
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					}
				}
			}
		}

		if(table.getIdType() == 2 ){
			Integer tableId = mapper.maxTableId();
			table.setTableId(tableId);
		}
		//插入主表数据
		mapper.insertMetaTable(table);
		if(table.getTableId() == null){
			table.setTableId(mapper.maxTableId() - 1);
		}

		//插入子表数据。
		Integer maxId = null;
		if(table.getIdType() == 2){
			maxId = colMapper.maxColId();
		}
		for(int i=0; i<table.getCols().size(); i++){
			DSColumn col = table.getCols().get(i);
			EtlTableMetaCol mcol = new EtlTableMetaCol();
			mcol.setColName(col.getName());
			mcol.setColType(col.getType());
			mcol.setColLength(col.getLength());
			mcol.setColScale(col.getScale());
			mcol.setColNote(col.getDispName());
			mcol.setColOrd(col.getIdx());
			mcol.setTableId(table.getTableId());
			mcol.setColDesc(col.getDispName());
			mcol.setDefvalue(col.getDefvalue());
			if(table.getIdType() == 2){
				mcol.setColId(maxId);
				maxId++;
			}
			this.colMapper.insertMetaTableCol(mcol);
		}
		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("tid", table.getTableId());
		pms.put("tname", table.getTableName());
		pms.put("operType", 1);
		pms.put("oper", "创建表"+table.getTableName());
		loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
		return table;
	}

	public void updateTableInfoNoLogs(EtlTableMeta table){
		this.mapper.updateMetaTable(table);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateTableInfo(EtlTableMeta table){
		this.mapper.updateMetaTable(table);
		//移除缓存
		cacheService.removeTableInfo(table.getTableId());

		//写日志
		Map<String, Object> pms = new HashMap<>();
		table = this.getTableOnly(table.getTableId());
		pms.put("tid", table.getTableId());
		pms.put("tname", table.getTableName());
		pms.put("operType", 3);
		pms.put("oper", "修改表"+table.getTableName());
		loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
	}

	/**
	 * 改表名
	 * @param table
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateTableName(EtlTableMeta table){
		EtlTableMeta oldTable = mapper.getTable(table.getTableId());
		//修改表
		mapper.updateMetaTable(table);
		//修改模型
		TableMeta meta = new TableMeta();
		meta.setTid(table.getTableId());
		meta.settName(table.getTableName());
		tableMapper.updateTableName(meta);
		//修改原表
		List<EtlTableMeta> tables = mapper.selectTables(null, null, null, null, oldTable.getTableName());
		for (EtlTableMeta t : tables){
			String[] srcs = t.getSrcTables().split(",");
			StringBuilder newSrcs = new StringBuilder();
			for(String src : srcs){
				if(src.equals(oldTable.getTableName())){
					newSrcs.append(table.getTableName());
				}else {
					newSrcs.append(src);
				}
				newSrcs.append(",");
			}
			EtlTableMeta newTable = new EtlTableMeta();
			newTable.setTableId(t.getTableId());
			newTable.setSrcTables(newSrcs.substring(0, newSrcs.length() - 1));
			mapper.updateMetaTable(newTable);
		}

		//移除缓存
		cacheService.removeTableInfo(table.getTableId());
	}

	/**
	 * 删除表，
	 * @param tableId
	 * @param dropTable 是否移除表
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteTable(Integer tableId, boolean dropTable) {
		//先移除表
		EtlTableMeta table = mapper.getTable(tableId);
		if(dropTable){
			DataSource ds = null;
			if(table.getDsourceId() != null){
				ds = dsService.selectDataSourceByPrimaryKey(table.getDsourceId());
			}
			dsService.droptable(table.getTableName(), ds);
		}
		mapper.deleteTable(table);
		colMapper.delTableColByTableId(table);
		cacheService.removeTableInfo(table.getTableId());

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("tid", table.getTableId());
		pms.put("tname", table.getTableName());
		pms.put("operType", 2);
		pms.put("oper", "删除表"+table.getTableName());
		loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
	}

	/**
	public String getTableSql(Integer tableId){
		return mapper.getTableSql(tableId);
	}

	public Integer maxTableId( String sysUser ){
		return mapper.maxTableId();
	}
	 **/

	public int updateEsEnable(EtlTableMeta table){
		return mapper.updateEsEnable(table);
	}

	public int updateUseEs(EtlTableMeta table){
		return mapper.updateUseEs(table);
	}

	/**
	 * 清空表数据，并删除时间戳
	 * @param tableId
	 */
	public void clearData(Integer tableId, boolean dropTable, String useIn, Integer tfId) throws Exception {
		//如果是 dropTable， 需要清空更新时间
		if(dropTable) {
			mapper.clearExeDate(tableId);
		}

		TableInfoVO tableInfoVO = cacheService.getTableInfo(tableId);
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			DataSource ds = tableInfoVO.getDsource();
			if (ds == null) {
				conn = dsService.getDwConnection();
			} else {
				conn = dsService.getConnection(ds);
			}
			String sql = "";
			if(dropTable){
				if("sql".equals(useIn)){
					sql = "delete from "+tableInfoVO.getTname() +" where " +srcTf + " = 'tf"+tfId+"'" ;
				}else{
					sql = "drop table " + tableInfoVO.getTname();
				}
			}else{
				sql =  "TRUNCATE TABLE " + tableInfoVO.getTname();
			}
			log.info(sql);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		}finally {
			if(ps != null){
				ps.close();
			}
			if(conn != null){
				conn.close();
			}
		}
	}

	/**
	 * 根据表ID查询 所属的数据抽取/转换
	 * @param tableId
	 * @return
	 */
	public List<Map<String, Object>> queryTfByTableId(Integer tableId){
		return mapper.queryTfByTableId(tableId);
	}

	public List<Map<String, Object>> queryTableDataAsJson(Integer tableId, String text, String value, String order, String parentCol, String parentVal) throws Exception {
		TableInfoVO tinfo = cacheService.getTableInfo(tableId);
		if(tinfo.isRest()) {
			return restService.queryRestApi(tinfo, null, null);
		}else{
			EtlTableMetaCol col = tinfo.findColByAlias(text);
			EtlTableMetaCol valCol = tinfo.findColByAlias(value);
			String sql = "";
			if(text.equals(value)){ //text 和 value 选的相同的字段
				sql += "select " + (col.getExpression() != null && col.getExpression().length() > 0 ?col.getExpression():col.getColName()) + " as " + text.toLowerCase();
			}else{
				sql += "select " + (col.getExpression() != null && col.getExpression().length() > 0 ?col.getExpression():col.getColName()) + " as " + text.toLowerCase() + ", " + (valCol.getExpression()!=null&&valCol.getExpression().length()>0?valCol.getExpression().toLowerCase():valCol.getColName().toLowerCase()) + " as " + value;
			}
			sql += ", count(*) cnt from " + datasetService.createTableSql(tinfo);
			sql += this.createCascadeParam(parentCol, parentVal, tinfo);
			sql += " group by "+text+(text.equals(value)?"":","+value);
			if(order != null && order.length() > 0){
				if(text.equals(value)) { //text 和 value 选的相同的字段
					sql += " order by " + text.toLowerCase() + " " + order;
				}else{
					sql += " order by " + value + " " + order;
				}
			}
			//数据源
			if(tinfo.getDsource() != null){
				return dataSourceService.querySqlUseDataSource(sql, null, tinfo.getDsource());
			}else {
				return daoHelper.queryForList(sql);
			}
		}
	}

	/**
	 * 创建及联参数的过滤SQL
	 * @param parentCol
	 * @param parentVal
	 * @return
	 */
	private String createCascadeParam(String parentCol, String parentVal, TableInfoVO tinfo){
		StringBuilder sql = new StringBuilder("");
		if(parentCol != null && parentCol.length() > 0 && parentVal != null && parentVal.length() > 0){
			EtlTableMetaCol col = tinfo.getCols().stream().filter(m->m.getColName().equals(parentCol)).findFirst().orElseThrow(RuntimeException::new);
			sql.append(" where ").append(col.getExpression()!=null&&col.getExpression().length()>0?col.getExpression():col.getColName()).append(" in ");
			sql.append("(");
			String[] vls = parentVal.split(",");
			for(int i=0; i<vls.length; i++){
				String val = vls[i];
				sql.append("'");
				sql.append(val);
				sql.append("'");
				if(i != vls.length - 1){
					sql.append(",");
				}
			}
			sql.append(")");
		}
		return sql.toString();
	}

	public void insertMetaTable(EtlTableMeta table){
		mapper.insertMetaTable(table);
	}

	public void updateMetaTable(EtlTableMeta table){
		mapper.updateMetaTable(table);
	}

	public Integer maxTableId(  ){
		return mapper.maxTableId();
	}
}
