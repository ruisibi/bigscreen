/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.bireport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.exception.ExtRuntimeException;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.entity.etl.EtlRestTableMetaParam;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.entity.etl.EtlTableMetaCol;
import com.ruisitech.bi.mapper.bireport.OlapMapper;
import com.ruisitech.bi.service.etl.DataSourceService;
import com.ruisitech.bi.service.etl.EtlTableMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 数据表信息缓存对象
 * @author hy
 * @date 2017-5-2
 */
@Service
public class TableCacheService {

	@Resource
	private OlapMapper mapper;

	private final static String prefix = "Table-";

	@Autowired
	private EtlTableMetaService tableService;

	@Autowired
	private DataSourceService dataSourceService;

	@Resource
	private RedisTemplate<String, TableInfoVO> redisTemplate;

	public TableInfoVO getTableInfo(Integer tid){
		TableInfoVO ret =redisTemplate.opsForValue().get(prefix+tid);
		if(ret == null){
			TableInfoVO tinfo = mapper.getQueryTable(tid);
			if(tinfo == null){
				throw new ExtRuntimeException("无法找到数据表，表ID："+tid+"。");
			}
			tinfo.setCreateTime(new Date());
			//查询缓存表字段
			List<EtlTableMetaCol> cols = mapper.getQueryTableCols(tid);
			tinfo.setCols(cols);
			//缓存字段和别名映射关系
			List<Map<String, String>> cubeCols = mapper.getCubeCols(tid);
			Map<String, String> alias = new HashMap<>();
			for(Map<String, String> col : cubeCols){
				alias.put(col.get("alias"), col.get("cName"));
			}
			tinfo.setColALias(alias);

			//缓存指标聚合方式
			List<Map<String, String>> aliass = mapper.getKpisAlias(tid);
			Map<String, String> aggres = new HashMap<>();
			for(Map<String, String> a : aliass){
				aggres.put(a.get("alias"), a.get("aggre"));
				aggres.put(a.get("alias") + "@distinctCol", a.get("distinctCol"));  //在 aliasAggre 对象里面，存放 distinctCol 的信息，就是加 @distinctCol后缀
				aggres.put(a.get("alias") + "@fromCol", a.get("fromCol")); //在 aliasAggre 对象里面，存放 fromCol 的信息，就是加 @fromCol 后缀
			}
			tinfo.setAliasAggre(aggres);

			//缓存数据源
			if(tinfo.getDsourceId() != null  && tinfo.getDsourceId() == -1){
				tinfo.setDsourceId(null);
			}
			if(tinfo.getDsourceId() != null){
				DataSource ds = dataSourceService.selectDataSourceByPrimaryKey(tinfo.getDsourceId());
				if(ds == null){
					throw new RuntimeException("表"+tinfo.getTid()+"("+tinfo.getTname()+")对应的数据源不存在。");
				}
				tinfo.setDsource(ds);
			}

			//如果是rest接口，设置Rest参数
			if(tinfo.isRest()) {
				String params = tinfo.getRestParams();
				List<EtlRestTableMetaParam> ls = new ArrayList<EtlRestTableMetaParam>();
				JSONArray arrays = JSONArray.parseArray(params);
				arrays.forEach((o) ->{
					EtlRestTableMetaParam obj = JSONObject.toJavaObject((JSONObject)o, EtlRestTableMetaParam.class);
					ls.add(obj);
				});
				tinfo.setRestParam(ls);
			}
			ret = tinfo;
			redisTemplate.opsForValue().set(prefix+tid, tinfo, 1, TimeUnit.HOURS);  //缓存1小时
		}
		return ret;
	}


	public TableInfoVO getTableInfoByTname(String tname){
		TableInfoVO ret = null;
		Set<String> keys = redisTemplate.keys(prefix+"*");
		List<TableInfoVO> tbs = redisTemplate.opsForValue().multiGet(keys);
		for(TableInfoVO tb : tbs){
			if(tb.getTname().equals(tname)){
				ret = tb;
				break;
			}
		}
		if(ret == null) {
			EtlTableMeta table = tableService.getTableByTname(tname);
			ret = Optional.ofNullable(table).map(t -> getTableInfo(t.getTableId())).orElseThrow(()->new RuntimeException("表 "+tname+" 不存在"));
		}
		return ret;
	}

	public void removeTableInfo(Integer tid){
		redisTemplate.delete(prefix+tid);
	}
}
