/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.ruisitech.bi.entity.frame.Department;
import com.ruisitech.bi.mapper.frame.DepartmentMapper;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {




	@Autowired
	private DepartmentMapper mapper;

	public int deleteByPrimaryKey(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}

	public int insertSelective(Department record){
		record.setId(mapper.maxDeptId());
		return mapper.insertSelective(record);
	}

	public int cntDepartmentUsers(Integer deptId){
		return mapper.cntDepartmentUsers(deptId);
	}

	public Department selectByPrimaryKey(Integer id){
		return mapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Department record){
		return mapper.updateByPrimaryKeySelective(record);
	}

	public List<Department> list(){
		return mapper.list();
	}

	public List<Department> tree(Integer pid){
		return mapper.tree(pid);
	}
}
