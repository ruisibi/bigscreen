/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.bireport;

import com.ruisitech.bi.entity.bireport.DimDecomposeDto;
import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.bireport.ParamDto;
import com.ruisitech.bi.entity.model.TableDimension;
import com.ruisitech.bi.service.model.TableDimensionService;
import com.ruisitech.bi.util.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/bireport")
public class DimController extends BaseController {

	@Autowired
	private TableDimensionService dimService;

	private static Logger log = LogManager.getLogger(DimController.class);


	@RequestMapping(value={"/queryDims.action", "/share/queryDims.action"})
	public @ResponseBody
    Object queryDims(Integer tableId){
		List dims = dimService.getTableDims(tableId);
		//不显示层级维度
		List<DimDto> ret = new ArrayList<DimDto>();
		for(int i=0; i<dims.size(); i++) {
			DimDto dim = (DimDto)dims.get(i);
			if(!"y".equals(dim.getIspcdim())){
				ret.add(dim);
			}
		}
		return super.buildSucces(ret);
	}

	@RequestMapping(value={"/paramFilter.action", "/share/paramFilter.action"})
	public @ResponseBody Object paramFilter(ParamDto param, String parentJSON, String keyword) throws Exception{
		TableDimension d = dimService.queryDimCol(param.getId(), param.getTid());
		List<Object> ls = dimService.paramFilter(d, parentJSON, keyword, param.getTid());
		Map<String, Object> ret = new HashMap<>();
		ret.put("datas", ls);
		if(ls.size() > 0) {
			if (d.getType().equals("month") || d.getType().equals("day")) {
				//排序
				ls = ls.stream().sorted((m1, m2)->{
					Map<String, Object> obj1 = (Map<String, Object>)m1;
					Map<String, Object> obj2 = (Map<String, Object>)m2;
					String v1 = (String)obj1.get("id");
					String v2 = (String)obj2.get("id");
					return v1.compareTo(v2);
				}).collect(Collectors.toList());
				Map<String, Object> first = (Map<String, Object>)ls.get(0);
				Map<String, Object> end = (Map<String, Object>)ls.get(ls.size() - 1);

				ret.put("st", first.get("id"));
				ret.put("end", end.get("id"));
				ret.remove("datas");
			}
		}
		return super.buildSucces(ret);
	}

	/**
	 * 维度分解 (带筛选条件)
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/dim/decompose.action")
	public @ResponseBody
    Object decompose(@RequestBody DimDecomposeDto dto) {
		try {
			return super.buildSucces(dimService.dimDecompose(dto));
		}catch (Exception ex){
			log.error("系统错误", ex);
			return super.buildError(ex.getMessage());
		}
	}
}
