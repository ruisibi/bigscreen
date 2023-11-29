/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.model;

import com.ruisitech.bi.entity.model.SubjectType;
import com.ruisitech.bi.mapper.model.SubjectTypeMapper;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubjectTypeService {

	@Autowired
	private SubjectTypeMapper mapper;




	public void deleteByPrimaryKey(Integer dsId) {
		mapper.deleteByPrimaryKey(dsId);
	}

	public int cntTables(Integer typeId){
		return mapper.cntTables(typeId);
	}

	public void insert(SubjectType record) {
		if(record.getIdType() == 2){
			record.setDsId(mapper.maxTypeid());
		}
		mapper.insert(record);
		if(record.getIdType() != 2) {
			record.setDsId(mapper.maxTypeid() - 1);
		}
	}

	public SubjectType selectByPrimaryKey(Integer dsId) {
		return mapper.selectByPrimaryKey(dsId);
	}

	public void updateByPrimaryKey(SubjectType record) {
		mapper.updateByPrimaryKey(record);
	}

	public List<Map<String, Object>> selectByTree() {
		return mapper.selectByTree();
	}

	public List<SubjectType> list(){
		return mapper.list();
	}
}
