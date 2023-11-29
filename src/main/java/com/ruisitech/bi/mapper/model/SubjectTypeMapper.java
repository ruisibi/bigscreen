/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.model;

import com.ruisitech.bi.entity.model.SubjectType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SubjectTypeMapper {

    int deleteByPrimaryKey( @Param("dsId") Integer dsId);

    int insert(SubjectType record);

    SubjectType selectByPrimaryKey( @Param("dsId") Integer dsId);

    int updateByPrimaryKey(SubjectType record);

    List<Map<String, Object>> selectByTree();

    int cntTables( @Param("typeId") Integer typeId);

    List<SubjectType> list();

    int maxTypeid();
}
