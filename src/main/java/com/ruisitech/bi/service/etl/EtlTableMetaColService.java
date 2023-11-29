/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.entity.model.TableDimension;
import com.ruisitech.bi.mapper.etl.EtlTableMetaColMapper;
import com.ruisitech.bi.mapper.etl.EtlTableMetaMapper;
import com.ruisitech.bi.mapper.model.TableDimensionMapper;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EtlTableMetaColService extends EtlBaseService {

	@Value("${spring.datasource.dwType}")
	private String dwType;

	@Autowired
	private EtlTableMetaColMapper colMapper;

	@Autowired
	private EtlTableMetaMapper mapper;

	@Autowired
	private DaoHelper daoHelper;

	@Autowired
	private TableCacheService cacheService;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	private TableDimensionMapper tableDimensionMapper;

	/**
	 * 添加表格字段
	 * @param meta
	 * @param dyna 表示是否是动态字段，如果不是，需要更新数据库
	 */
	@Transactional(rollbackFor = Exception.class)
	public void insertMetaTableCol(EtlTableMetaCol meta, boolean dyna){
		if(!dyna){
			EtlTableMeta table = mapper.getTable(meta.getTableId());
			meta.setTableName(table.getTableName());
			super.addTableColumn(meta, dwType, daoHelper);
		}
		if(meta.getIdType() == 2){
			meta.setColId(colMapper.maxColId());
		}
		colMapper.insertMetaTableCol(meta);
		cacheService.removeTableInfo(meta.getTableId());
	}

	/**
	 *
	 * @param meta
	 * @param dyna
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateTableCol(EtlTableMetaCol meta, boolean dyna){
		if(!dyna){
			EtlTableMeta table = mapper.getTable(meta.getTableId());
			meta.setTableName(table.getTableName());
			EtlTableMetaCol oldCol = this.getTableColumn(meta.getTableId(), meta.getColId());
			//只有colType, colLength 发生变化，才会更新数据库
			String income = table.getIncome();
			if(!(income.equals("es") || income.equals("custom") || income.equals("rest"))) {
				if(!oldCol.getColType().equals(meta.getColType()) || !oldCol.getColLength().equals(meta.getColLength())){
					super.updateColumn(meta, dwType, daoHelper);
				}
			}
		}else{
			//更新数据模型的字段
			colMapper.updDimExpressByFromCol(meta);
			colMapper.updKpiExpressByFromCol(meta);
		}
		colMapper.updateTableCol(meta);
		cacheService.removeTableInfo(meta.getTableId());

		//写日志
		EtlTableMeta table = mapper.getTable(meta.getTableId());
		Map<String, Object> pms = new HashMap<>();
		pms.put("tid", table.getTableId());
		pms.put("tname", table.getTableName());
		pms.put("operType", 4);
		pms.put("oper", "表"+table.getTableName()+"添加字段" + meta.getColName());
		loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
	}

	/**
	 * 只更新字段元数据信息，不修改数据类型
	 * @param meta
	 */
	public void updateTableColOnly(EtlTableMetaCol meta){
		colMapper.updateTableCol(meta);
		//更新维度表的 valtype 字段
		TableDimension col = new TableDimension();
		col.setFromCol(meta.getColName());
		col.setTid(meta.getTableId());
		col.setVtype(meta.getColType());
		tableDimensionMapper.updateDimByFromcol(col);
		cacheService.removeTableInfo(meta.getTableId());
	}

	public void delTableColById(Integer colId){
		colMapper.delTableColById(colId);
	}

	public List<EtlTableMetaCol> queryTableColumns(Integer tableId, Integer colId){
		return colMapper.queryTableColumns(tableId, colId);
	}

	public List<EtlTableMetaCol> queryTableColumnsNotExpress(Integer tableId){
		return colMapper.queryTableColumnsNotExpress(tableId);
	}

	public EtlTableMetaCol getTableColumn(Integer tableId, Integer colId){
		List<EtlTableMetaCol> ret = colMapper.queryTableColumns(tableId, colId);
		if(ret == null || ret.size() == 0){
			return null;
		}else{
			return ret.get(0);
		}
	}


	/**
	 * 删除表字段
	 * @param tableId
	 * @param colId
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delTableColumn(Integer tableId, Integer colId){

		EtlTableMetaCol col = this.getTableColumn(tableId, colId);
		//如果不是动态字段，删除字段，
		if(col.getExpression() == null || col.getExpression().length() == 0) {
			EtlTableMeta table = mapper.getTable(tableId);
			col.setTableName(table.getTableName());
			super.removeTableColumn(col, daoHelper);
		}
		colMapper.delTableColById(colId);
		cacheService.removeTableInfo(tableId);

		//写日志
		EtlTableMeta table = mapper.getTable(tableId);
		Map<String, Object> pms = new HashMap<>();
		pms.put("tid", table.getTableId());
		pms.put("tname", table.getTableName());
		pms.put("operType", 5);
		pms.put("oper", "表"+table.getTableName()+"删除字段" + col.getColName());
		loggerService.insertTableLog(pms, RSBIUtils.getLoginUserInfo());
	}

	public void delTableColByTableId(EtlTableMeta table){
		colMapper.delTableColByTableId(table);
	}

	/**
	 * 删除表字段不删除动态字段
	 * @param table
	 */
	public void delTableColByTableIdNotExpress(EtlTableMeta table){
		colMapper.delTableColByTableIdNotExpress(table);
	};

	public void updateTableCol(EtlTableMetaCol meta){
		colMapper.updateTableCol(meta);
	}

	public Integer maxColId(  ){
		return colMapper.maxColId();
	}

}
