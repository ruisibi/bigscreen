/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.model;

import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.entity.model.TableDimension;
import com.ruisitech.bi.entity.model.TableMeasure;
import com.ruisitech.bi.entity.model.TableMeta;
import com.ruisitech.bi.mapper.model.TableDimensionMapper;
import com.ruisitech.bi.mapper.model.TableMeasureMapper;
import com.ruisitech.bi.mapper.model.TableMetaColMapper;
import com.ruisitech.bi.mapper.model.TableMetaMapper;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.EtlTableMetaService;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据建模 Service,用来构建立方体，修改立方体，删除立方体等操作。
 * 实际上，立方体(cube),也对应一个分析主题(subject)
 * @author hq
 *
 */
@Service
public class SubjectManagerService {

	@Resource
	private TableMetaMapper tableMapper;

	@Resource
	private TableDimensionMapper dimMapper;

	@Resource
	private TableMeasureMapper kpiMapper;

	@Resource
	private TableMetaColMapper colMapper;

	@Autowired
	private TableCacheService cahceService;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	private EtlTableMetaService tableMetaService;


	public List<TableMeta> listAuthSubject(TableMeta table){
		return tableMapper.listSubject(table);
	}

	public List<TableMeta> listAllSubject(TableMeta table){
		Integer crtUser = null;
		table.setUserId(crtUser);
		return tableMapper.listSubject(table);
	}

	public Integer tableExist(Integer tableId){
		return tableMapper.tableExist(tableId);
	}

	/**
	 * 获取立方体信息
	 * @param tableId
	 * @return
	 */
	public TableMeta getCube(Integer tableId) {
		TableMeta table = tableMapper.getTable(tableId);
		//获取维度
		List<DimDto> dims = dimMapper.getTableDims(tableId);
		table.setDimDtos(dims);
		//获取度量
		List<TableMeasure> kpis = kpiMapper.getTableKpis(tableId);
		table.setKpis(kpis);
		return table;
	}

	/**
	 * 获取表字段段信息，并包括维度字段类型
	 * @param tableId
	 */
	public List<EtlTableMetaCol> columnsWithDim(Integer tableId){
		//获取表字段
		List<EtlTableMetaCol> cols = tableMetaService.queryTableColumns(tableId, true);
		//获取维度
		List<DimDto> dims = dimMapper.getTableDims(tableId);
		for(DimDto dim : dims){
			cols.stream().filter(c->dim.getFromCol().equals(c.getColName())).findFirst().ifPresent(m->{
				m.setDimType(dim.getType());
				m.setAlias(dim.getAlias());
				m.setDateformat(dim.getDateformat());
			});
		}
		return cols;
	}

	public List<TableMeasure> getCubeKpis(Integer tableId){
		return  kpiMapper.getTableKpis(tableId);
	}

	/**
	 * 删除立方体
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delCube(Integer tid){
		TableMeta table = tableMapper.getTable(tid);
		tableMapper.deleteTable(tid);
		TableDimension dim = new TableDimension();
		dim.setTid(tid);
		dimMapper.deleteDim(dim);
		dimMapper.deleteGroupByTid(tid);
		TableMeasure kpi = new TableMeasure();
		kpi.setTid(tid);
		kpiMapper.deleteKpi(kpi);
		kpiMapper.deleteKpiGroupByTid(tid);
		colMapper.deleteByTid(tid);
		//移除缓存
		cahceService.removeTableInfo(tid);
		//记录日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", table.getTid());
		pms.put("name", table.gettDesc());
		pms.put("operType", 3);
		pms.put("oper", "删除数据模型" + table.gettDesc());
		pms.put("bsType", 4);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	/**
	 * 保存立方体
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveCube(TableMeta table) {
		//插入主表
		tableMapper.insertTable(table);
		this.insertDim(table);
		this.insertDimRela(table);
		this.insertKpi(table);
		this.insertKpiRela(table);
		//移除缓存
		cahceService.removeTableInfo(table.getTid());
		//记录日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", table.getTid());
		pms.put("name", table.gettDesc());
		pms.put("operType", 1);
		pms.put("oper", "创建数据模型" + table.gettDesc());
		pms.put("bsType", 4);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	/**
	 * 更新立方体
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateCube(TableMeta table) {
		tableMapper.updateTable(table);
		List<Map<String, Object>> delObj = table.getDelObj();
		//在编辑立方体时，通过delObj 来描述哪些维度，度量、分组被删除掉了。先第一步删除这些
		if(delObj != null && !delObj.isEmpty()){
			for(int i=0; i<delObj.size(); i++){
				Map<String, Object> obj = delObj.get(i);
				String tp = (String)obj.get("type");
				Object id = obj.get("id");
				if(id == null ){
					continue;
				}

				if("dim".equals(tp)){
					TableDimension dim = new TableDimension();
					dim.setTid(table.getTid());
					dim.setDimId((Integer)id);
					dimMapper.deleteDim(dim);
				}else if("kpi".equals(tp)){
					TableMeasure kpi = new TableMeasure();
					kpi.setTid(table.getTid());
					kpi.setKpiId((Integer)id);
					kpiMapper.deleteKpi(kpi);
				}else if("group".equals(tp)){
					dimMapper.deleteGroupById((String)id);
				}else if("kpigroup".equals(tp)){
					kpiMapper.deleteKpiGroup((String)id);
				}
			}
		}
		//删除关系表数据，再从建
		colMapper.deleteByTid(table.getTid());
		//处理维度
		this.updateDim(table);
		this.insertDimRela(table);
		//处理指标
		this.updateKpi(table);
		this.insertKpiRela(table);
		//移除缓存
		cahceService.removeTableInfo(table.getTid());
		//记录日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", table.getTid());
		pms.put("name", table.gettDesc());
		pms.put("operType", 2);
		pms.put("oper", "编辑数据模型" + table.gettDesc());
		pms.put("bsType", 4);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateKpi(TableMeta table){
		Map<String, Object> groupkeys = new HashMap<String, Object>(); //group 的 hashmap 对象
		List<String> ls = kpiMapper.listGroup(table.getTid());
		for(int i=0; i<ls.size(); i++){
			String str = (String)ls.get(i);
			groupkeys.put(str, "");
		}

		List<TableMeasure> kpis = table.getKpis();
		for(int i=0; i<kpis.size(); i++){
			TableMeasure kpi = kpis.get(i);
			kpi.setTid(table.getTid());
			//是否需要新增 指标分组
			String kpiGroup = kpi.getKpiGroup();
			if(kpiGroup != null && kpiGroup.length() > 0){
				if(!groupkeys.containsKey(kpiGroup)){  //不存在，新增
					kpiMapper.insertKpiGroup(kpi);
					groupkeys.put(kpiGroup, "");
				}
			}
			Integer targetId = kpi.getTargetId();
			if(targetId == null){ //新增
				if(table.getIdType() == 2){
					targetId = kpiMapper.getMaxKpiId();
					kpi.setKpiId(targetId);
				}
				//如果指标不是计算指标，直接拼接，计算指标直接取公式
				int calcKpi = kpi.getCalcKpi();  //新增度量那创建的计算指标
				Integer calc = kpi.getCalc();  //数据集创建的动态字段
				if(calcKpi == 1){
					kpi.setCol(kpi.getCol());
				}else if(calc != null && calc == 1){
					String c = kpi.getCol();
					kpi.setCol(kpi.getAggre()+"("+c+")");
				}else{
					kpi.setCol(kpi.getAggreCol());
				}
				kpiMapper.insertKpi(kpi);
				targetId = targetId == null ? kpiMapper.getMaxKpiId() - 1 : targetId;
			}else{ //修改
				String isupdate = kpi.getIsupdate();
				kpi.setKpiId(targetId);
				//如果指标不是计算指标，直接拼接，计算指标直接取公式
				int calcKpi = kpi.getCalcKpi();  //新增度量那创建的计算指标
				Integer calc = kpi.getCalc();  //数据集创建的动态字段
				if(calcKpi == 1){
					kpi.setCol(kpi.getCol());
				}else if(calc != null && calc == 1){
					String c = kpi.getCol();
					if(c.indexOf("(") >= 0) {
						c = c.substring(c.indexOf("("), c.length());
					}else{
						c = "(" + c + ")";
					}
					kpi.setCol(kpi.getAggre()+c);
				}else{
					kpi.setCol(kpi.getAggreCol());
				}
				if("y".equals(isupdate)){
					kpiMapper.updateKpi(kpi);
				}
			}
			kpi.setKpiId(targetId);
		}
	}

	//插入指标
	@Transactional(rollbackFor = Exception.class)
	public void insertKpi(TableMeta table){
		Map<String, Object> groupkeys = new HashMap<String, Object>(); //group 的 hashmap 对象
		int kpiId = 0;
		if(table.getIdType() == 2){
			kpiId = this.kpiMapper.getMaxKpiId();
		}
		List<TableMeasure> kpis = table.getKpis();
		for(int i=0; i<kpis.size(); i++){
			TableMeasure kpi = kpis.get(i);
			kpi.setTid(table.getTid());
			if(table.getIdType() == 2){  //oracle 自动生成id
				kpi.setKpiId(kpiId);
				kpiId++;
			}
			String groupId = kpi.getKpiGroup();
			if(groupId != null && groupId.length() > 0 && !groupkeys.containsKey(groupId)){
				kpiMapper.insertKpiGroup(kpi);
				groupkeys.put(groupId, "");
			}
			//如果指标不是计算指标，直接拼接，计算指标直接取公式
			int calcKpi = kpi.getCalcKpi();  //新增度量那创建的计算指标
			Integer calc = kpi.getCalc();  //数据集创建的动态字段
			if(calcKpi == 1){
				kpi.setCol(kpi.getCol());
			}else if(calc != null && calc == 1){
				String c = kpi.getCol();
				kpi.setCol(kpi.getAggre()+"("+c+")");
			}else{
				kpi.setCol(kpi.getAggreCol());
			}
			kpiMapper.insertKpi(kpi);
			if(table.getIdType() == 1){  //不是oracle 才有数据库生成id,再获取id
				if(i == 0){
					kpiId = kpiMapper.getMaxKpiId() - 1;
				}else{
					kpiId++;
				}
				kpi.setKpiId(kpiId);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateDim(TableMeta table){
		Map<String, Object> groupkeys = new HashMap<String, Object>(); //group 的 hashmap 对象
		List<String> ls = dimMapper.listGroup(table.getTid());
		for(int i=0; i<ls.size(); i++){
			String str = (String)ls.get(i);
			groupkeys.put(str, "");
		}
		List<TableDimension> dims = table.getDims();
		for(int i=0; i<dims.size(); i++){
			TableDimension dim = dims.get(i);
			dim.setOrd(i);
			dim.setTid(table.getTid());
			String type = dim.getType();
			if(type == null || type.length() == 0){
				dim.setType("frd");
			}
			//判断是否有分组，如果有分组插入分组
			String groupId = dim.getGroupId();
			if(groupId != null && groupId.length() > 0 && !groupkeys.containsKey(groupId)){
				dimMapper.insertGroup(dim);
				groupkeys.put(groupId, "");
			}
			Integer targetId = dim.getTargetId();
			if(targetId == null){  //新增维度
				if(table.getIdType() == 2){
					Integer dimId = dimMapper.getMaxDimId();
					dim.setDimId(dimId);
				}
				dimMapper.insertDim(dim);
				targetId = targetId == null ? dimMapper.getMaxDimId() - 1 : targetId;
			}else{ //修改维度
				dim.setDimId(targetId);
				String isupdate = dim.getIsupdate();
				if("y".equals(isupdate)){  //只有修改过的维度才更新
					dimMapper.updateDim(dim);
				}
			}
			dim.setDimId(targetId);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void insertDim(TableMeta table){
		Map<String, Object> groupkeys = new HashMap<String, Object>(); //group 的 hashmap 对象
		int dimId = 0;
		List<TableDimension> dims = table.getDims();
		for(int i=0; i<dims.size(); i++){
			TableDimension dim = dims.get(i);
			String type =dim.getType();
			if(type == null || type.length() == 0){
				dim.setType("frd");
			}
			dim.setTid(table.getTid());
			//判断是否有分组，如果有分组插入分组
			String groupId = dim.getGroupId();
			if(groupId != null && groupId.length() > 0 && !groupkeys.containsKey(groupId)){
				dimMapper.insertGroup(dim);
				groupkeys.put(groupId, "");
			}
			if(table.getIdType() == 2){  //oracle 插入数据库，需要 dim_id
				if(dimId == 0){
					dimId = dimMapper.getMaxDimId();
				}else{
					dimId++;
				}
				dim.setDimId(dimId);
				dimMapper.insertDim(dim);
			}else{  //其他数据库采用自增
				dimMapper.insertDim(dim);
				if(i == 0){
					dimId = dimMapper.getMaxDimId() - 1;
				}else{
					dimId++;
				}
			}
			dim.setDimId(dimId);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void insertKpiRela(TableMeta table){
		//先删除指标数据
		colMapper.deleteKpiMeta(table.getTid());

		//获取id
		int maxid = table.getIdType() == 2 ? colMapper.getMaxRid() : 0;

		//添加指标
		List<TableMeasure> kpis = table.getKpis();
		for(int i=0; i<kpis.size();i++){
			TableMeasure kpi = kpis.get(i);
			kpi.setRid(maxid);
			kpi.setColType(2);
			kpi.setColId(kpi.getKpiId());
			kpi.setTid(table.getTid());
			kpi.setOrd(i);
			maxid++;
			colMapper.insertMeta(kpi);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void insertDimRela(TableMeta table){
		//先删除维度数据
		colMapper.deleteDimMeta(table.getTid());
		int maxid = table.getIdType() == 2 ? colMapper.getMaxRid() : 0;
		//添加维
		for(int i=0; i<table.getDims().size(); i++) {
			TableDimension dim = table.getDims().get(i);
			dim.setRid(maxid);
			maxid++;
			dim.setTid(table.getTid());
			dim.setColType(1);
			dim.setColId(dim.getDimId());
			dim.setOrd(i);
			colMapper.insertMeta(dim);
		}
	}
}
