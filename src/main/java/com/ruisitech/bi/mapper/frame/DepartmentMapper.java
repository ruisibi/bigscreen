/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.frame;

import com.ruisitech.bi.entity.frame.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper {

    int deleteByPrimaryKey( @Param("id") Integer id);

    int insertSelective(Department record);

    Department selectByPrimaryKey( @Param("id") Integer id);

    int updateByPrimaryKeySelective(Department record);

    List<Department> list();

    Integer maxDeptId();

    List<Department> tree( @Param("pid") Integer pid);

    int cntDepartmentUsers( @Param("deptId") Integer deptId);

}
