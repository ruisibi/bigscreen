/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.bireport.DimDecomposeDto;
import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.model.TableDimension;
import com.ruisitech.bi.entity.portal.CompParamDto;
import com.ruisitech.bi.mapper.model.TableDimensionMapper;
import com.ruisitech.bi.service.bireport.BaseCompService;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.DataSetService;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TableDimensionService extends BaseCompService {

	@Autowired
	private TableDimensionMapper dimMapper;

	@Autowired
	private DataSetService dsService;

	@Autowired
	private TableCacheService cahceService;

	@Autowired
	private DaoHelper daoHelper;

	@Autowired
	private DatasetService datasetService;


	public TableDimension findDimensionByAlias(String alias, Integer tid){
		return dimMapper.findDimensionByAlias(alias, tid);
	}

	public String queryMaxDate(Integer tableId) {
		List<DimDto> dims = dimMapper.getTableDims(tableId);
		String ret = "未知";
		DimDto dayDim = null;
		for(int i=0; i<dims.size(); i++) {
			DimDto dim = dims.get(i);
			if("day".equals(dim.getType())) {  //查找日期维度
				dayDim = dim;
				break;
			}
		}
		TableInfoVO tinfo = cahceService.getTableInfo(tableId);
		String sql = "select max("+dayDim.getFromCol()+") from " + tinfo.getTname();
		if(!tinfo.isEs()) {
			Date dt = (Date)daoHelper.queryForObject(sql, Date.class);
			if(dt != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ret = sdf.format(dt);
			}
		}else {
			//未实现
		}
		return ret;
	}

	/**
	 * 先忽略父子维度
	 * @param tid
	 * @return
	 */
	public List<DimDto> getTableDims(Integer tid){
		return dimMapper.getTableDims(tid);
	}

	public TableDimension queryDimCol(Integer dimId, Integer tid){
		return dimMapper.queryDimCol(dimId, tid);
	}

    /**
     * 维度分解
	 * @param dto
     * @return
     */
	public List<Object> dimDecompose(DimDecomposeDto dto) throws Exception{
		TableDimension d = queryDimCol(dto.getDimId(), dto.getTid());
		TableInfoVO table = cahceService.getTableInfo(dto.getTid());
		return dealDimensionFilter(d, table, null, dto.getParams(), null);
	}

	private List<Object> dealDimensionFilter(TableDimension d, TableInfoVO tinfo, String keyword, List<CompParamDto> pms, List<DimDto> parentFilter)
			throws Exception{
		//查询事实表
		String col = d.getCol();
		String key = d.getColkey();
		String name = d.getColtext();
		String tname = d.getTname();
		String dimord = d.getDimord();
		String tsql = d.getTsql();
		//判断是否查询es
		boolean qEs = tinfo.isSyncEs();
		StringBuffer keys = new StringBuffer("");
		//添加参数条件
		for(int i=0; pms != null && i<pms.size(); i++){
			CompParamDto dto = pms.get(i);
			String filterCol = dto.getCol();
			if(dto.getExpression() != null && dto.getExpression().length() > 0){ //表达式起作用
				filterCol = dto.getExpression();
			}
			String type = dto.getType();
			String val = dto.getVal();
			String val2 = dto.getVal2();
			String valuetype = dto.getValuetype();
			String usetype = dto.getUsetype();
			if("frd".equals(dto.getDimType())) {  //维度分解只支持 type = 'frd'
				if (usetype.equals("gdz") && val != null && val.indexOf("(") == -1) {  //只支持固定值并且不包含表达式
					if(!isNumber(valuetype)){
						if(val != null){
							if("in".equals(type)){  //in需要把数据用逗号分隔的重新生成
								String[] vls = val.split(",");
								val = "";
								for(int j=0; j<vls.length; j++){
									val = val + "'" + vls[j] + "'";
									if(j != vls.length - 1){
										val = val + ",";
									}
								}
							}else{
								val = "'" + val + "'";
							}
						}
						if(val2 != null){
							val2 = "'" + val2 + "'";
						}
					}
					if(type.equals("between")){
						keys.append(" and " +  filterCol + " " + type + " " + val + " and " + val2);
					}else if(type.equals("in")){
						keys.append(" and " + filterCol + " in (" + val + ")");
					}else{
						keys.append(" and " + filterCol + " " + type + " " + val);
					}
				}
			}
		}
		if(parentFilter != null){  //下级在筛选时，及联上级参数
			for(DimDto dim : parentFilter){
				String tp = dim.getType();
				if(tp.equals("day")){
					if(dim.getDay() != null){
						String start = dim.getDay().getStartDay();
						String end = dim.getDay().getEndDay();
						keys.append(" and " + (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " between '"+start+"' and '" + end + "'");
					}else if(dim.getVals() != null && dim.getVals().size() > 0){
						String vls = RSBIUtils.dealStringParam(dim.getVals(), true);
						keys.append(" and " +  (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " in ("+vls+")");
					}
				}
				//处理月份
				else if(tp.equals("month")){
					if(dim.getMonth() != null){
						//如果有计算指标，需要重写数据区间
						String start = dim.getMonth().getStartMonth();
						String end = dim.getMonth().getEndMonth();
						keys.append(" and " +  (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " between '"+start+"' and '" + end + "'");
					}else
					if(dim.getVals() != null && dim.getVals().size() > 0){
						//如果有计算指标，需要重写数据值列表
						String vls = RSBIUtils.dealStringParam(dim.getVals(), true);
						keys.append(" and " +  (qEs?(dim.getColname().equals(dim.getFromCol())?dim.getFromCol():dim.getAlias()):dim.getColname()) + " in ("+vls+")");
					}
				} else {
					//数字维度筛选
					if("Long".equals(dim.getValType()) || "Int".equals(dim.getValType())){
						if(dim.getOper() != null && dim.getOper().length() > 0 && dim.getValue1() != null) {
							keys.append(" and " + (qEs ? (dim.getColname().equals(dim.getFromCol()) ? dim.getFromCol() : dim.getAlias()) : dim.getColname()));
							keys.append(" " + dim.getOper() + " ");
							keys.append(dim.getValue1());
							if ("between".equals(dim.getOper())) {
								keys.append(" and ");
								keys.append(dim.getValue2());
							}
						}
					}else {
						//限制维度筛选
						if (dim.getVals() != null && dim.getVals().size() > 0) {
							String vls = RSBIUtils.dealStringParam(dim.getVals(), "string".equalsIgnoreCase(dim.getValType()));
							keys.append(" and " + (dim.getTableColKey() != null && dim.getTableColKey().length() > 0 ? dim.getTableColKey() : (qEs ? (dim.getColname().equals(dim.getFromCol()) ? dim.getFromCol() : dim.getAlias()) : dim.getColname())) + " in (" + vls + ")");
						}
					}
				}
			}
		}
		String sql =  "select distinct " +  (key==null||key.length() == 0 ? col : key) + " as id, " + (name==null||name.length() == 0 ?col:name) + " as name from ";
		sql += datasetService.createTableSql(tsql, tname);
		sql += " where 1=1 ";
		sql += keys.toString();
		if(keyword != null && keyword.length() > 0){
			sql += " and "+(name==null||name.length() == 0 ?col:name)+" like '%"+keyword.trim()+"%'";
		}
		if("y".equals(d.getIspcdim())) {
			sql += " and "+d.getLevelCol()+" = " + d.getPcLevel();
		}
		if(dimord != null && dimord.length() > 0){
			sql += " order by "+ (key == null || key.length() == 0 ? col : key) + " " + dimord;
		}
		List<Object> ret = dsService.queryTopN(sql, tinfo.getDsource(), 200);
		//移除第一行，第一行是标题
		ret.remove(0);
		return ret;

	}

	public List<Object> paramFilter(TableDimension d, String parentJSON, String keyword, Integer tid) throws Exception{
		TableInfoVO tinfo = cahceService.getTableInfo(tid);
		if(parentJSON == null || parentJSON.length() == 0){
			return dealDimensionFilter(d, tinfo, keyword, null, null);
		}else {
			List<DimDto> parents = new ArrayList<>();
			JSONArray arrys = JSONArray.parseArray(parentJSON);
			for (int i = 0; i < arrys.size(); i++) {
				parents.add(JSONObject.toJavaObject(arrys.getJSONObject(i), DimDto.class));
			}
			return dealDimensionFilter(d, tinfo, keyword, null, parents);
		}
	}
}
